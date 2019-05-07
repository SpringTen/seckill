package com.seckill.dao.cache;

import com.seckill.dao.SeckillDao;
import com.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @Class RedisDaoTest
 * @Package com.seckill.dao.cache
 * @Author lizhanzhan
 * @Date 2019/5/6 13:55
 * @Motto talk is cheap,show me the code
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

    //注入RedisDao
    @Autowired
    RedisDao redisDao;

    @Autowired
    SeckillDao seckillDao;

    private final long id = 1000;

    @Test
    public void testRedis() {
        Seckill seckill = redisDao.getSeckill(id);
        //1、如果redis里没有数据
        if (seckill == null) {
            //2、从数据库取出
            seckill = seckillDao.queryById(id);
            if (seckill != null) {
                //3、放入缓存中
                String result = redisDao.setSeckill(seckill);
                System.out.println("result: " + result);
                seckill = redisDao.getSeckill(id);
                System.out.println("seckill: " + seckill);
            }
        }
        seckill = redisDao.getSeckill(id);
        System.out.println("seckill: " + seckill);
    }
}