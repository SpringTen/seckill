package com.lzz.seckill.springbootseckill.rabbitmq;

/**
 * @Class SeckillMessage
 * @Package com.lzz.seckill.springbootseckill.rabbitmq
 * @Author lizhanzhan
 * @Date 2019/5/10 11:31
 * @Motto talk is cheap,show me the code
 */
public class SeckillMessage {

    private long seckillId;

    private long userPhone;

    public SeckillMessage(long seckillId, long userPhone) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
    }

    @Override
    public String toString() {
        return "SeckillMessage{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }
}
