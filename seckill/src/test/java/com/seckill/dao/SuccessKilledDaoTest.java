package com.seckill.dao;

import com.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @Class SuccessKilledDaoTest
 * @Package com.seckill.dao
 * @Author lizhanzhan
 * @Date 2019/5/3 18:39
 * @Motto talk is cheap,show me the code
 */

/**
 * 配置spring和junit整合，junit启动时加载springIOC
 * 需要spring-test，junit
 * 此时还没有spring-context.xml spring-service.xml spring-mvc.xml等文件
 */
@RunWith(SpringJUnit4ClassRunner.class)//junit提供的，会自动加载springIOC容器
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Autowired
    SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception{
        int i = successKilledDao.insertSuccessKilled(1000L, 13851580482L);
        System.out.println(i);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception{
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(1000L, 13851580482L);
        System.out.println(successKilled);
    }
}