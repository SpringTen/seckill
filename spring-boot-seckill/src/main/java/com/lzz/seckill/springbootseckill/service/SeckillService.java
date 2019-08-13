package com.lzz.seckill.springbootseckill.service;

import com.lzz.seckill.springbootseckill.dto.Exposer;
import com.lzz.seckill.springbootseckill.dto.SeckillExecution;
import com.lzz.seckill.springbootseckill.entity.Seckill;
import com.lzz.seckill.springbootseckill.entity.SuccessKilled;
import com.lzz.seckill.springbootseckill.exception.RepeatKillException;
import com.lzz.seckill.springbootseckill.exception.SeckillCloseException;
import com.lzz.seckill.springbootseckill.exception.SeckillException;
import java.util.List;

/**
 * @Class SeckillService
 * @Package com.seckill.service
 * @Author lizhanzhan
 * @Date 2019/5/4 11:00
 * @Motto talk is cheap,show me the code
 * 秒杀接口
 */
public interface SeckillService {
    /**
     * 通过id获取Seckill对象
     *
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 获取Seckill列表
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 秒杀开启时输出秒杀地址，
     * 否则输出系统时间和秒杀时间，
     * 调用此方法可以拿到
     *  秒杀是否开启
     *  秒杀开始时间
     *  秒杀结束时间
     *  md5等
     *
     * @param seckillId
     * @return
     */
    Exposer exposeSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * 上面的接口暴露方法应该先被调用，所以调用当前秒杀方法时，
     * 可以获取到md5值，如果不一致，说明接口被篡改了，就不执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException;

    /**
     * redis预减库存
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

    /**
     * 执行秒杀操作 by 存储过程
     * 上面的接口暴露方法应该先被调用，所以调用当前秒杀方法时，
     * 可以获取到md5值，如果不一致，说明接口被篡改了，就不执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution realExecuteSeckill(long seckillId, long userPhone);

    /**
     * 获取秒杀成功后的信息
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled getByIdWithSeckill(long seckillId, long userPhone);

}
