package com.yumu.hexie.common.util;

import java.util.Collections;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * 高版本的redistemplate已经支持setnx设置过期时间，保证了操作的原子性，可以不调用此类中的添加锁释放锁函数。
 * 或者可以使用reddision
 * @author david
 *
 */
public class RedisLock {
	
	/**
	 * 获取锁
	 * @param key
	 * @param redisTemplate，传<String, String>
	 * @return 1表示成功，0表示KEY已经添加过了
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Long lock(String key, RedisTemplate redisTemplate, long lockTime) {
		
		//rua script保证setnx 跟expire 在一个操作里，保证了原子性,新版本setIfAbsent直接支持，老版本无法保证原子性
//		String script = "if redis.call('setNx',KEYS[1],ARGV[1])==1 then return 1 else return 0 end"; 
		String script = "if redis.call('setNx',KEYS[1],ARGV[1])==1 then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end "; 
		RedisScript redisScript = new DefaultRedisScript<>(script, Long.class);
		
		Object result = redisTemplate.execute(redisScript, redisTemplate.getKeySerializer(), redisTemplate.getValueSerializer(), 
				Collections.singletonList(key), "1", String.valueOf(lockTime));
		
		return (Long)result;
	}
	
	 /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return 1表示成功
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Long releaseLock(String key, RedisTemplate redisTemplate){
 
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
 
        RedisScript redisScript = new DefaultRedisScript<>(script, Long.class);
 
        Object result = redisTemplate.execute(redisScript, redisTemplate.getKeySerializer(), redisTemplate.getValueSerializer(), 
        		Collections.singletonList(key), "1");
        return (Long)result;
    }
	
	
}
