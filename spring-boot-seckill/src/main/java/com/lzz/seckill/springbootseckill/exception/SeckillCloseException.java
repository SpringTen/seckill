package com.lzz.seckill.springbootseckill.exception;

/**
 * @Class SeckillCloseException
 * @Package com.seckill.exception
 * @Author lizhanzhan
 * @Date 2019/5/4 11:30
 * @Motto talk is cheap,show me the code
 * 秒杀关闭异常   秒杀完毕，秒杀结束
 */
public class SeckillCloseException extends SeckillException {

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
