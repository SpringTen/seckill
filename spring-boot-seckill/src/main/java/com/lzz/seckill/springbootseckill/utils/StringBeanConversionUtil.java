package com.lzz.seckill.springbootseckill.utils;

import com.alibaba.fastjson.JSON;

/**
 * @Class StringBeanConversionUtil
 * @Package com.lzz.seckill.springbootseckill.utils
 * @Author lizhanzhan
 * @Date 2019/5/10 12:41
 * @Motto talk is cheap,show me the code
 */
public class StringBeanConversionUtil {
    /**
     * 将 bean 类型转化为字符串类型
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value) {
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return value + "";
        } else if (clazz == long.class || clazz == Long.class) {
            return value + "";
        } else if (clazz == String.class) {
            return (String)value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * 把String转成JavaBean
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String value, Class<T> clazz) {
        if (value == null || value == "" || value.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(value);
        } else if (clazz == String.class) {
            return (T) value;
        } else {
            return JSON.toJavaObject(JSON.parseObject(value), clazz);
        }
    }
}
