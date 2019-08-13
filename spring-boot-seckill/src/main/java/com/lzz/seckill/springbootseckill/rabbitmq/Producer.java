package com.lzz.seckill.springbootseckill.rabbitmq;

import com.lzz.seckill.springbootseckill.config.RabbitmqConfig;
import com.lzz.seckill.springbootseckill.utils.StringBeanConversionUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Class Producer
 * @Package com.lzz.seckill.springbootseckill.rabbitmq
 * @Author lizhanzhan
 * @Date 2019/5/10 11:30
 * @Motto talk is cheap,show me the code
 */
@Component
public class Producer {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendToMQ(SeckillMessage message) {
        String toSend = StringBeanConversionUtil.beanToString(message);
        amqpTemplate.convertAndSend(RabbitmqConfig.seckillQueue, toSend);
    }
}
