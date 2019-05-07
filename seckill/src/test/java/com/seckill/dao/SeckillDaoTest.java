package com.seckill.dao;

import com.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * @Class SeckillDaoTest
 * @Package com.seckill.dao
 * @Author lizhanzhan
 * @Date 2019/5/3 17:40
 * @Motto talk is cheap,show me the code
 */

/**
 * 配置spring和junit整合，junit启动时加载springIOC
 * 需要spring-test，junit
 * 此时还没有spring-context.xml spring-service.xml spring-mvc.xml等文件
 */
@RunWith(SpringJUnit4ClassRunner.class)//junit提供的，会自动加载springIOC容器
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Autowired
    SeckillDao seckillDao;

    @Test
    public void testReduceNumber() throws Exception{
        int i = seckillDao.reduceNumber(1000, new Date());
        System.out.println(i);
    }
    @Test
    public void testQueryById() throws Exception{
        Seckill seckill = seckillDao.queryById(1000);
        System.out.println(seckill);
    }
    @Test
    public void testQueryAll() throws Exception{
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill:seckills
             ) {
            System.out.println(seckill);
        }
    }

}