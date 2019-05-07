package com.seckill.dto;

import com.seckill.entity.SuccessKilled;
import com.seckill.enums.SeckillStateEnum;

/**
 * @Class SeckillExecution
 * @Package com.seckill.dto
 * @Author lizhanzhan
 * @Date 2019/5/4 11:25
 * @Motto talk is cheap,show me the code
 * 封装秒杀执行后的结果
 */
public class SeckillExecution {

    //秒杀商品id
    private long seckillId;

    //秒杀状态
    private int state;

    //秒杀状态详细信息
    private String stateInfo;

    //秒杀成功对象
    private SuccessKilled successKilled;

    //失败
    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }
    //成功
    public SeckillExecution(long seckillId, SeckillStateEnum stateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
