package com.lzz.seckill.springbootseckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Class RedisDaoTest
 * @Package com.lzz.seckill.springbootseckill.dao.cache
 * @Author lizhanzhan
 * @Date 2019/5/10 9:38
 * @Motto talk is cheap,show me the code
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisDaoTest {

    @Autowired
    RedisDao redisDao;

    @Test
    public void setSeckillNumber() {
        redisDao.setSeckillNumber(1001, 5);
    }

    @Test
    public void getSeckillNumber(){
        String value = redisDao.getSeckillNumber(1001);
        System.out.println(value);
    }

    @Test
    public void decreaseNumber() {
        redisDao.decreaseNumber(1001);
    }
}