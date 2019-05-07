package com.seckill.service.impl;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessKilled;
import com.seckill.enums.SeckillStateEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Class SeckillServiceImpl
 * @Package com.seckill.service.impl
 * @Author lizhanzhan
 * @Date 2019/5/5 8:51
 * @Motto talk is cheap,show me the code
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired  //@Inject @Resource  是Java提供的
    private SeckillDao seckillDao;  //SeckillDao的实现是mybatis帮我们做的

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    RedisDao redisDao;

    private final String salt = "hjdf78^^%jSDJFHI)58DFldfie@!dskfjIIGG*-+";

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    /**
     * 秒杀暴露接口需要优化，Seckill seckill = seckillDao.queryById(seckillId);
     * 此操作数据库放在缓存中进行
     *
     * @param seckillId
     * @return
     */
    @Override
    public Exposer exposeSeckillUrl(long seckillId) {
        //优化
        Seckill seckill = redisDao.getSeckill(seckillId);
        //1、如果redis里没有数据
        if (seckill == null) {
            //2、从数据库取出
            seckill = seckillDao.queryById(seckillId);
            if (seckill != null) {
                //3、放入缓存中
                String result = redisDao.setSeckill(seckill);
                System.out.println("seckill: " + seckill);
            } else {
                return new Exposer(false, seckillId);
            }
        }

        long start = seckill.getStartTime().getTime();
        long end = seckill.getEndTime().getTime();
        //系统当前时间
        long now = new Date().getTime();
        //失败
        if (now < start || now > end) {
            return new Exposer(false, seckillId, now, start, end);
        }
        //成功
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    //减库存，生成订单
    @Override
    @Transactional
    /**
     * 使用注解的优点
     *  1：开发团队保持一致，明确事务行为的编程风格
     *  2：保证事务的执行时间尽可能地短，尽量不穿插其他的RPC/HTTP等网络操作，如果必须，则剥离到事务方法外部
     *  3：不是所有方法都需要事务，如：只有一次修改，只读操作等
     *  *********************
     *  注意非常重要的优化：
     *  减少锁的持有时间
     * 首先是在update操作的时候给行加锁，insert并不会加锁，如果更新操作在前，那么就需要执行完更新和插入以后事务提交或回滚才释放锁，而如果插入在前，则更新完以后事务提交或回滚就释放锁。也就是说是更新在前加锁和释放锁之间两次的网络延迟和GC，如果插入在前则加锁和释放锁之间只有一次的网络延迟和GC，也就是减少的持有锁的时间。
     * 1、	Update加上行锁-> insert ->事务commit或者rollback
     * 2、	Insert -> update 加上行锁 -> 事务commit或者rollback
     * 第一步锁的持有时间是第二步的2倍！并且，先insert可以挡住一部分重复秒杀
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        //验证前端传来（也是根据seckillId从后端Exposer获取的）
        // 的md5是否与通过seckillId计算的md5一致，如果不一致，说明接口被篡改
        if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data(md5changed) rewrite!");
        }
        try {
            //2、生成订单
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //插入订单失败，重复秒杀
                throw new RepeatKillException("seckill repead exception!");
            } else {
                //1、减库存，使用当前系统时间判断是否还能秒杀，热点竞争
                int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    //减库存失败，秒杀关闭
                    throw new SeckillCloseException("seckill close exception!");
                } else {
                    //减库存成功
                    //生成订单成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    //用枚举的数据字典
                    //成功
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

            //先捕获较小的异常，否则，这两个在方法体内抛出的异常将无效
        } catch (SeckillCloseException e) {
            throw e;
        } catch (RepeatKillException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error!" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }

        Map<String, Object> map = new HashMap<>();
        Date killTime = new Date();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //为什么使用map作为参数呢？
        //就是为了返回值result，之后后被填充
        try {
            seckillDao.killByProcedure(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
