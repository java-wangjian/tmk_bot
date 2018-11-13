package com.zxxkj.cache;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.Serializable;

public class RedisTest {
    @Autowired
    private RedisCacheUtil redisCache;

    private static String key;
    private static String field;
    private static String value;

    @Before
    public void setUp() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-redis.xml");
        context.start();
        redisCache = (RedisCacheUtil) context.getBean("redisCache");
    }

    // 初始化 数据
    static {
        key = "tb_student";
        field = "stu_name";
        value = "一系列的关于student的信息！";
    }

    // 测试增加数据
    @Test
    public void testHset() {
        redisCache.hset(key, field, value);
        redisCache.setCacheObject("outbang",10);
        redisCache.setCacheObjectTimeOut("liufei11","测试",1000);
        System.out.println("数据保存成功！");
    }

    // 测试查询数据
    @Test
    public void testHget() {
        String re = redisCache.hget(key, field);
        System.out.println("得到的数据：" + re+"  QQ "+"  outNum:"+(int)redisCache.getCacheObject("planId_4476_537198868601293363271540031212191"));
    }

    // 测试数据的数量
    @Test
    public void testHsize() {
        long size = redisCache.hsize(key);
        System.out.println("数量为：" + size);
    }
    
 // 删除数据的数量
    @Test
    public void testDelete() {
        boolean flag = redisCache.delete("");
        System.out.println("数量为：" + flag);
    }
    
    @Test
    public void testincrBy() {
    	int bb =4;
    	long  aa = bb;
    	redisCache.setCacheObject("planId_" + 3333 + "_" + 4, 0);
    	redisCache.delete("planId_" + 3333 + "_" + 4);
    	redisCache.incrBy("planId_" + 3333 + "_" + 4, aa);
    	System.out.println(redisCache.getCacheObject("planId_" + 3333 + "_" + 4));
    }
}
