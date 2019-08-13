package com.lzz.seckill.springbootseckill;

import com.lzz.seckill.springbootseckill.rabbitmq.Producer;
import com.lzz.seckill.springbootseckill.rabbitmq.SeckillMessage;
import com.lzz.seckill.springbootseckill.utils.StringBeanConversionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootSeckillApplicationTests {

    /**
     * 测试rabbitmq
     */
    @Autowired
    Producer producer;
    @Test
    public void contextLoads() {
        SeckillMessage seckillMessage = new SeckillMessage(8888, 122222223333L);
        producer.sendToMQ(seckillMessage);
    }

    @Test
    public void testDate(){
        //方法一
        String value = "2019-05-14";
        String year = value.substring(0, 4);
        String month = value.substring(5, 7);
        String day = value.substring(8, 10);
        System.out.println(year + ": " + month + ": " + day);

        //方法2
        value = value.replaceAll("-", "/");
        Date date = new Date(value);
        System.out.println(date);
    }

}
