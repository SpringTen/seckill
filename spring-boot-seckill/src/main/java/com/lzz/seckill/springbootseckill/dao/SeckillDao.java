package com.lzz.seckill.springbootseckill.dao;

import com.lzz.seckill.springbootseckill.entity.Seckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Class SeckillDao
 * @Package com.seckill.dao
 * @Author lizhanzhan
 * @Date 2019/5/3 11:54
 * @Motto talk is cheap,show me the code
 */
@Mapper
public interface SeckillDao {

    /**
     * 减库存
     *
     * @param seckillId 秒杀商品id
     * @param killTime  秒杀时间
     * @return 如果返回 > 1，表示影响的行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id判断当前商品，是否秒杀结束
     * @param seckillId
     * @param killTime
     * @return
     */
    Seckill isStop(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询商品
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(@Param("seckillId") long seckillId);

    /**
     * 查询所有商品列表
     *
     * @param offset 查询开始位置
     * @param limit  查询条数
     * @return 商品列表
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 秒杀通过存储过程
     * @param map
     */
    void killByProcedure(Map<String, Object> map);

    /**
     * 项目初始化时查询所有秒杀商品的库存
     * @return
     */
    List<Seckill> queryNumber();

}
