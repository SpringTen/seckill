package com.lzz.seckill.springbootseckill.service.impl;

import com.lzz.seckill.springbootseckill.dao.cache.RedisDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Class SeckillServiceImplTest
 * @Package com.lzz.seckill.springbootseckill.service.impl
 * @Author lizhanzhan
 * @Date 2019/5/10 9:56
 * @Motto talk is cheap,show me the code
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SeckillServiceImplTest {

    @Autowired
    SeckillServiceImpl seckillService;

    @Test
    public void afterPropertiesSet() throws Exception {
        seckillService.afterPropertiesSet();
    }
}