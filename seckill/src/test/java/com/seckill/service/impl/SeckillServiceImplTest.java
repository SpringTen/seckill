package com.seckill.service.impl;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.Seckill;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @Class SeckillServiceTest
 * @Package com.seckill.service
 * @Author lizhanzhan
 * @Date 2019/5/5 13:08
 * @Motto talk is cheap,show me the code
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void testGetById() throws Exception{
        Seckill seckill = seckillService.getById(1000L);
        logger.info("seckill = {}", seckill);
    }

    @Test
    public void testGetSeckillList() throws Exception{
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("seckillList = {}", seckillList);
    }

    @Test
    //集成代码测试的完整流程，可以重复执行，异常被捕获到了
    public void testExposeSeckillUrl() throws Exception{
        long id = 1000;
        Exposer exposer = seckillService.exposeSeckillUrl(id);
        long userPhone = 13851580482L;
        String md5 = exposer.getMd5();
        if (exposer != null){
            SeckillExecution seckillExecution = null;
            try {
                seckillExecution = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("result = {}", seckillExecution);
            } catch (SeckillCloseException e){
                //在这里不可以继续抛出，而应该捕获
                logger.error(e.getMessage());
                //throw e;
            } catch (RepeatKillException e){
                logger.error(e.getMessage());
                //seckill repead exception!打印结果为重复秒杀，单元测试通过
            }
        } else {
            logger.warn("exposer = {}", exposer);
        }

    }

    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1003;
        long phone = 13851580482L;
        Exposer exposer = seckillService.exposeSeckillUrl(seckillId);
        String md5 = null;
        if (exposer.isExposed()) {
            md5 = exposer.getMd5();
        }
        System.out.println(md5);
        SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
        System.out.println(execution.getStateInfo());
    }
}