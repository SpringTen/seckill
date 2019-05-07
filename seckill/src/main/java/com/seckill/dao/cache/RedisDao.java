package com.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Class RedisDao
 * @Package com.seckill.dao.cache
 * @Author lizhanzhan
 * @Date 2019/5/6 10:59
 * @Motto talk is cheap,show me the code
 */
public class RedisDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //获取Jedis连接池
    private final JedisPool jedisPool;

    //获取序列化工具的schema
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);


    public RedisDao(String ip, int port) {
        this.jedisPool = new JedisPool(ip, port);
    }

    /**
     * 根据id获取Seckill对象，
     * 需要注意，无论哪种Nosql，数据存储的都是bytes数组（二进制数组数据），
     * 将二进制数组转化为Java或者PHP或者其他语言的对象这一过程称为反序列化，byte[] -> Object(Seckill)
     * Java提供的Serializable性能不好，
     * 使用第三方的Java序列化框架，protostuff
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(long seckillId){
        try {
            Jedis jedis = jedisPool.getResource();
            jedis.auth("123456");
            try {
                String key = "seckill:" + seckillId;
                //通过key的字节数据获取对应的字节数组值
                byte[] bytes = jedis.get(key.getBytes());
                //通过schema获取一个空的对象，并用这个空对象存储反序列化之后的数据
                Seckill seckill = schema.newMessage();
                //这个工具类将bytes数组的数据按照schema定义的格式，转化到seckill对象里
                ProtobufIOUtil.mergeFrom(bytes, seckill, schema);
                return seckill;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将Seckill对象缓存至Redis，出错返回错误信息，成功会返回'OK'
     * @param seckill
     * @return
     */
    public String setSeckill(Seckill seckill) {

        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                //按照schema的格式将对象序列化为字节数组，并设置存储的大小
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //将字节数组存储到Redis，并设置过期时间
                int timeout = 60 * 60; //一小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;      //存储成功返回"OK",否则返回出错信息
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
