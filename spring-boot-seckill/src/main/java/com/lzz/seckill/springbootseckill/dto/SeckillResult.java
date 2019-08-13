package com.lzz.seckill.springbootseckill.dto;

/**
 * @Class SeckillResult
 * @Package com.seckill.dto
 * @Author lizhanzhan
 * @Date 2019/5/5 15:38
 * @Motto talk is cheap,show me the code
 * 封装所有的ajax请求返回的结果
 *  1、暴露接口时泛型使用Exposer
 *  2、秒杀时泛型使用SeckillExecution
 */
public class SeckillResult<T> {
    //是否成功
    private boolean success;

    //泛型数据
    private T data;

    //错误
    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
