package com.lzz.seckill.springbootseckill.enums;

/**
 * @Class SeckillStateEnum
 * @Package com.seckill.enums
 * @Author lizhanzhan
 * @Date 2019/5/5 10:44
 * @Motto talk is cheap,show me the code
 */
public enum SeckillStateEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改"),
    IN_QUEUE(2, "排队中");

    private int state;

    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStateEnum stateOf(int index){
        for (SeckillStateEnum stateEnum : values()){
            if (stateEnum.state == index){
                return stateEnum;
            }
        }
        return null;
    }

}
