package com.lzz.seckill.springbootseckill.rabbitmq;

import com.lzz.seckill.springbootseckill.config.RabbitmqConfig;
import com.lzz.seckill.springbootseckill.service.SeckillService;
import com.lzz.seckill.springbootseckill.utils.StringBeanConversionUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Class Consumer
 * @Package com.lzz.seckill.springbootseckill.rabbitmq
 * @Author lizhanzhan
 * @Date 2019/5/10 12:54
 * @Motto talk is cheap,show me the code
 */
@Component  //不加@Conmponent不能成功，暂时不知道为什么
@RabbitListener(queues = RabbitmqConfig.seckillQueue)
public class Consumer {

    @Autowired
    SeckillService seckillService;

    @RabbitHandler
    public void process(String message) {
        SeckillMessage seckillMessage = StringBeanConversionUtil.stringToBean(message, SeckillMessage.class);
        //出队，执行     1、新增    2、减库存
        seckillService.realExecuteSeckill(seckillMessage.getSeckillId(), seckillMessage.getUserPhone());
        System.out.println(seckillMessage);
    }
}
