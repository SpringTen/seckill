package com.lzz.seckill.springbootseckill.config;

import com.lzz.seckill.springbootseckill.dao.cache.RedisDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Class RedisConfig
 * @Package com.lzz.seckill.springbootseckill.config
 * @Author lizhanzhan
 * @Date 2019/5/7 16:42
 * @Motto talk is cheap,show me the code
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisDao redisDao(){
        return new RedisDao("localhost", 6379);
    }
}
