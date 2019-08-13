package com.lzz.seckill.springbootseckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Class RabbitmqConfig
 * @Package com.lzz.seckill.springbootseckill.config
 * @Author lizhanzhan
 * @Date 2019/5/10 11:27
 * @Motto talk is cheap,show me the code
 */
@Configuration
public class RabbitmqConfig {
    public static final String seckillQueue = "seckillQueue";
    @Bean
    public Queue queue() {
        return new Queue(seckillQueue);
    }
}
