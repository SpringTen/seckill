package com.seckill.dao;

import com.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * @Class SuccessKilledDao
 * @Package com.seckill.dao
 * @Author lizhanzhan
 * @Date 2019/5/3 11:58
 * @Motto talk is cheap,show me the code
 */
public interface SuccessKilledDao {

    /**
     * 根据联合主键，插入秒杀成功详情信息
     *
     * @param seckillId
     * @param userPhone
     * @return 返回受影响行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询 SuccessKilled 实体，并把 Seckill 实体信息携带上
     * 在SQL中做一个 连接操作，左右连接
     *
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
