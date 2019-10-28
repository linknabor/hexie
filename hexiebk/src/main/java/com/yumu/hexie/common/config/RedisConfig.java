package com.yumu.hexie.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value(value = "${redis.host}")
	private String redisHost;
	@Value(value = "${redis.port}")
	private int redisPort;
	@Value(value = "${redis.password}")
	private String redisPassword;
	@Value(value = "${redis.database}")
	private int redisDatabase;

	@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(redisHost);
        connectionFactory.setPort(redisPort);
        connectionFactory.setPassword(redisPassword);
        connectionFactory.setDatabase(redisDatabase);
        connectionFactory.setUsePool(true);
        return connectionFactory;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate getStringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory());
    }

    @Bean(name = "redisTemplate")
    public <V> RedisTemplate<String, V> getRedisTemplate() {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
    
    @Bean(name = "authRedisTemplate")
	public RedisTemplate<String, Object> authRedisTemplate() {
		
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		Jackson2JsonRedisSerializer<String> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(String.class);
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(getStringRedisTemplate().getStringSerializer());
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);	//有泛型的对象先转换成json字符串再往redis里存，不然反序列化时会报错。
		redisTemplate.setHashKeySerializer(getStringRedisTemplate().getStringSerializer());
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);	//同上
		return redisTemplate;
		
	}
    
    @Bean
    public CacheManager getCacheManager() {
    	RedisCacheManager m = new RedisCacheManager(getRedisTemplate());
    	m.setDefaultExpiration(1800);//
    	return m;
    }
}
