package com.zxxkj.cache;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.zxxkj.model.CallRecord;
import com.zxxkj.model.Customer;
import com.zxxkj.util.ConstantUtil;
import com.zxxkj.util.SleepUtil;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@Component("redisCache")
public class RedisCacheUtil<T> {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Object> vOps;
    /**
     * 向Hash中添加值
     *
     * @param key   可以对应数据库中的表名
     * @param field 可以对应数据库表中的唯一索引
     * @param value 存入redis中的值
     */
    public void hset(String key, String field, String value) {
        if (key == null || "".equals(key)) {
            return;
        }
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 从redis中取出值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        if (key == null || "".equals(key)) {
            return null;
        }
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 判断 是否存在 key 以及 hash key
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String key, String field) {
        if (key == null || "".equals(key)) {
            return false;
        }
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 查询 key中对应多少条数据
     *
     * @param key
     * @return
     */
    public long hsize(String key) {
        if (key == null || "".equals(key)) {
            return 0L;
        }
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 删除
     *
     * @param key
     * @param field
     */
    public void hdel(String key, String field) {
        if (key == null || "".equals(key)) {
            return;
        }
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public  void setCacheObject(String key, T value) {
        if (key == null || "".equals(key)) {
            return;
        }
         vOps.set(key,value);
    }
    /**
     * 缓存基本的对象，Integer、String、实体类、失效时间等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param timeOut 失效时间
     * @return 缓存的对象
     */
    public void setCacheObjectTimeOut(String key, T value,long timeOut) {
        if (key == null || "".equals(key)) {
            return;
        }
        try {
        	vOps.set(key,value,timeOut, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
			SleepUtil.sleep(1000);
			vOps.set(key, value, timeOut, TimeUnit.MINUTES);
		}finally {
			if(!(value).equals(vOps.get(key))) {
				SleepUtil.sleep(1000);
				vOps.set(key, value, timeOut, TimeUnit.MINUTES);
			}
		}
    }
    
    public void setCacheObjectTimeOut1(String key, T value,long timeOut) {
        if (key == null || "".equals(key)) {
            return;
        }
        try {
        	vOps.set(key,value,timeOut, TimeUnit.SECONDS);
		} catch (Exception e) {
			SleepUtil.sleep(1000);
			vOps.set(key,value,timeOut, TimeUnit.SECONDS);
		}finally {
			if(!(value).equals(vOps.get(key))) {
				SleepUtil.sleep(1000);
				vOps.set(key, value, timeOut, TimeUnit.MINUTES);
			}
		}
    }
    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    @SuppressWarnings({ "unchecked", "hiding" })
	public <T> T getCacheObject(String key) {
        ValueOperations<String, T> operation = (ValueOperations<String, T>) redisTemplate.opsForValue();
        return operation.get(key);
    }

    public boolean delete(String key){
    	try {
    		redisTemplate.delete(key);
    	}catch (Exception e) {
    		return true;
		}
        
        ValueOperations<String, T> operation = (ValueOperations<String, T>) redisTemplate.opsForValue();
        String removeKey = (String) operation.get(key);
        if(removeKey == null || ("").equals(removeKey)) {
        	return true;
        }
        return false;
    }
    
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param callRecordeList 缓存的值
     * @return 缓存的对象
     */
    public  void setCacheList(String key, List<T> list) {
        if (key == null || "".equals(key)) {
            return;
        }
         vOps.set(key,list);
    }
    
    /**
     * 获取redis中存放的通话记录列表
     * @return
     */
	public List<CallRecord> getRedisCallRecordList(String threadUUID) {
		List<CallRecord> callRecordeList = (List<CallRecord>) getCacheObject(threadUUID +ConstantUtil.REDIS_CALLRECORDELIST);
		if(callRecordeList == null) {
			callRecordeList = new ArrayList<CallRecord>();
			vOps.set(threadUUID +ConstantUtil.REDIS_CALLRECORDELIST,callRecordeList);
		}
		
		return callRecordeList;
	}
	
	/**
	 * 向redis中添加记录
	 * @param callRecord
	 * @return
	 */
	public synchronized boolean setRedisCallRecordList(String threadUUID, CallRecord callRecord) {
		List<CallRecord> callRecordeList = (List<CallRecord>) getCacheObject(threadUUID +ConstantUtil.REDIS_CALLRECORDELIST);
		
		if(callRecordeList == null) {
			callRecordeList = new ArrayList<CallRecord>();
		}
		int oldSize = callRecordeList.size();
		callRecordeList.add(callRecord);
		vOps.set(threadUUID +ConstantUtil.REDIS_CALLRECORDELIST,callRecordeList);
		int newSize = ((List<CallRecord>) getCacheObject(threadUUID +ConstantUtil.REDIS_CALLRECORDELIST)).size();
		
		if((oldSize + 1) == newSize) {
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 向redis中添加已拨打完的客户
	 * @param callRecord
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean setRedisIsCalledCustomerList(Customer customer, String threadUUID) throws Exception {
		
		synchronized(threadUUID) {
			Vector<Customer> customerList = (Vector<Customer>) getCacheObject(threadUUID + "_isCalledCustomer");
			if(customerList == null) {
				customerList = new Vector<Customer>();
			}
			int oldSize = customerList.size();
			customerList.add(customer);
			int newSize = customerList.size();
			vOps.set(threadUUID + "_isCalledCustomer",customerList);
			
			if((oldSize + 1) == newSize) {
				
				return true;
			}
		}
		
		return false;
	}
	
	/** 
	 * 增加(自增长), 负数则为自减 
	 *  
	 * @param key 
	 * @param value 
	 * @return 
	 */ 
//	 public Long incrBy(String key, long increment) { 
//		 
//		 return redisTemplate.opsForValue().increment(key, increment); 
//	 } 
	 
	 public Long incrBy(String key, long delta){
		    ValueOperations<String, String> operations = redisTemplate.opsForValue();
		    redisTemplate.setKeySerializer(new StringRedisSerializer());
		    redisTemplate.setValueSerializer(new StringRedisSerializer());
		    return operations.increment(key, delta);
		}
}
