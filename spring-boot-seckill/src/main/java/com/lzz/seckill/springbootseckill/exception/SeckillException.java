package com.lzz.seckill.springbootseckill.exception;

/**
 * @Class SeckillException
 * @Package com.seckill.exception
 * @Author lizhanzhan
 * @Date 2019/5/4 11:31
 * @Motto talk is cheap,show me the code
 * 秒杀异常
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
