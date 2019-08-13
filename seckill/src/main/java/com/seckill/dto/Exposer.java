package com.seckill.dto;

/**
 * @Class Exposer
 * @Package com.seckill.dto
 * @Author lizhanzhan
 * @Date 2019/5/4 11:13
 * @Motto talk is cheap,show me the code
 * 暴露秒杀地址DTO
 */
public class Exposer {

    //秒杀是否开启
    private boolean exposed;

    //暴露给用户的秒杀地址是加密后的
    private String md5;

    //秒杀商品id
    private long seckillId;

    //当前系统时间
    private long now;

    //秒杀开启时间
    private long start;

    //秒杀结束时间
    private long end;

    //秒杀开启构造方法
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    //秒杀未开启，或者秒杀结束，失败
    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    //秒杀商品id不存在，失败
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
