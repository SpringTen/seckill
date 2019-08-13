package com.lzz.seckill.springbootseckill.service.impl;

import com.lzz.seckill.springbootseckill.dao.SeckillDao;
import com.lzz.seckill.springbootseckill.dao.SuccessKilledDao;
import com.lzz.seckill.springbootseckill.dao.cache.RedisDao;
import com.lzz.seckill.springbootseckill.dto.Exposer;
import com.lzz.seckill.springbootseckill.dto.SeckillExecution;
import com.lzz.seckill.springbootseckill.entity.Seckill;
import com.lzz.seckill.springbootseckill.entity.SuccessKilled;
import com.lzz.seckill.springbootseckill.enums.SeckillStateEnum;
import com.lzz.seckill.springbootseckill.exception.RepeatKillException;
import com.lzz.seckill.springbootseckill.exception.SeckillCloseException;
import com.lzz.seckill.springbootseckill.exception.SeckillException;
import com.lzz.seckill.springbootseckill.rabbitmq.Producer;
import com.lzz.seckill.springbootseckill.rabbitmq.SeckillMessage;
import com.lzz.seckill.springbootseckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class SeckillServiceImpl
 * @Package com.seckill.service.impl
 * @Author lizhanzhan
 * @Date 2019/5/5 8:51
 * @Motto talk is cheap,show me the code
 */
@Service
public class SeckillServiceImpl implements SeckillService, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * redis内存标记，库存是否已经没了，减少redis访问
     */
    private Map<Long, Boolean> isOver = new ConcurrentHashMap<>();

    /**
     * 锁
     */
    private static ReentrantLock lock = new ReentrantLock(false);

    @Autowired  //@Inject @Resource  是Java提供的
    private SeckillDao seckillDao;  //SeckillDao的实现是mybatis帮我们做的

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    RedisDao redisDao;

    @Autowired
    Producer producer;

    private final String salt = "hjdf78^^%jSDJFHI)58DFldfie@!dskfjIIGG*-+";

    /**
     * redis预减库存
     * 项目初始化就从数据库取出所有秒杀商品的库存，缓存到redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<Seckill> seckills = seckillDao.queryNumber();
        for (Seckill seckill : seckills) {
            redisDao.setSeckillNumber(seckill.getSeckillId(), seckill.getNumber());
            if (seckill.getNumber() > 0) {
                isOver.put(seckill.getSeckillId(), false);
            } else {
                //如果库存数量 <=0，内存标记为true，即库存没了
                isOver.put(seckill.getSeckillId(), true);
            }
        }
    }

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
     * @return 什么情况才能暴露秒杀接口呢？
     * 1、秒杀商品的id存在，也就是秒杀商品存在
     * 2、秒杀商品已经开启秒杀，并且还没结束
     */
    @Override
    public Exposer exposeSeckillUrl(long seckillId) {
        //优化
        Seckill seckill = redisDao.getSeckill(seckillId);
        System.out.println("redis seckill: " + seckill);
        //1、如果redis里没有数据
        if (seckill == null) {
            //2、从数据库取出
            seckill = seckillDao.queryById(seckillId);
            if (seckill != null) {
                //3、放入缓存中
                String result = redisDao.setSeckill(seckill);
                System.out.println("mysql seckill: " + result);
            } else {
                return new Exposer(false, seckillId);
            }
        }

        long start = seckill.getStartTime().getTime();
        long end = seckill.getEndTime().getTime();
        //系统当前时间
        long now = new Date().getTime();
        //失败，秒杀未开启或者秒杀已结束
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

    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException {
        //验证前端传来（也是根据seckillId从后端Exposer获取的）
        // 的md5是否与通过seckillId计算的md5一致，如果不一致，说明接口被篡改
        if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(seckillId))) {
        throw new SeckillException("seckill data(md5 changed) rewrite!");
    }
        /////////////////////////////////////////////////////////////////////////
        /**
         * 用户点击秒杀按钮执行秒杀
         * 1、查看内存标记，库存是否存在
         *  1）库存没了，返回秒杀结束
         *  2）库存还有
         *      1））redis预减库存，
         *      2））更新isOver
         *      3））验证当前用户是否已经秒杀过此商品
         *          1）））如果已经秒杀过，则返回重复秒杀
         *          2）））如果秒杀结束，（时间结束），则返回秒杀关闭
         *          3）））没有秒杀过，则加入队列
         * ----------------------------
         * 以上操作，为避免高并发问题，应属于原子操作，应该加锁
         */

        //1、查看内存标记，库存是否存在
        if (isOver.get(seckillId)) {
            //如果库存 <= 0，即已经结束秒杀
            throw new SeckillCloseException("seckill close exception!");
        }
        //2）））如果秒杀结束，（时间结束），则返回秒杀关闭
        if (seckillDao.isStop(seckillId, new Date()) == null) {
            throw new SeckillCloseException("seckill close exception!");
        }
        //3））验证当前用户是否已经秒杀过此商品
        SuccessKilled successKilled1 = successKilledDao.queryByIdAndPhone(seckillId, userPhone);
        if (successKilled1 != null) {
            //1）））如果已经秒杀过，则返回重复秒杀
            throw new RepeatKillException("seckill repead exception!");
        }

        //3）））没有秒杀过，再次判断是否库存存在,则加入队列，
        if (!isOver.get(seckillId)) {
            //1））redis预减库存
            Long number = redisDao.decreaseNumber(seckillId);//返回的是redis对应商品减少后的数量
            if (number <= 0) {
                //2））更新isOver，库存没了，将isOver置为true
                isOver.put(seckillId, true);
                //如果库存 <= 0，即已经结束秒杀
                throw new SeckillCloseException("seckill close exception!");
            }else{
                SeckillMessage seckillMessage = new SeckillMessage(seckillId, userPhone);
                producer.sendToMQ(seckillMessage);
                //返回排队中
                return new SeckillExecution(seckillId, SeckillStateEnum.IN_QUEUE);
            }

        } else {
            //如果库存 <= 0，即已经结束秒杀
            throw new SeckillCloseException("seckill close exception!");
        }
    }

    /**
     * 使用注解的优点
     * 1：开发团队保持一致，明确事务行为的编程风格
     * 2：保证事务的执行时间尽可能地短，尽量不穿插其他的RPC/HTTP等网络操作，如果必须，则剥离到事务方法外部
     * 3：不是所有方法都需要事务，如：只有一次修改，只读操作等
     * *********************
     * 注意非常重要的优化：
     * 减少锁的持有时间
     * 首先是在update操作的时候给行加锁，insert并不会加锁，如果更新操作在前，那么就需要执行完更新和插入以后事务提交或回滚才释放锁，而如果插入在前，则更新完以后事务提交或回滚就释放锁。也就是说是更新在前加锁和释放锁之间两次的网络延迟和GC，如果插入在前则加锁和释放锁之间只有一次的网络延迟和GC，也就是减少的持有锁的时间。
     * 1、	Update加上行锁-> insert ->事务commit或者rollback
     * 2、	Insert -> update 加上行锁 -> 事务commit或者rollback
     * 第一步锁的持有时间是第二步的2倍！并且，先insert可以挡住一部分重复秒杀
     */
    //减库存，生成订单
    @Override
    @Transactional
    public SeckillExecution realExecuteSeckill(long seckillId, long userPhone) {
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

    @Override
    public SuccessKilled getByIdWithSeckill(long seckillId, long userPhone) {
        return successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
    }
}
