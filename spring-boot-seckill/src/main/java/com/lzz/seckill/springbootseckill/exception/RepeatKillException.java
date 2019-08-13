package com.lzz.seckill.springbootseckill.exception;

/**
 * @Class RepeatKillException
 * @Package com.seckill.exception
 * @Author lizhanzhan
 * @Date 2019/5/4 11:28
 * @Motto talk is cheap,show me the code
 * 重复秒杀异常
 * 必须是运行时异常，因为spring只对运行时异常提供事务支持
 * 异常
 *  运行时异常
 *  编译时异常
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
