package com.yumu.hexie.common.config;

import java.time.Duration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
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
	
	private Duration DEFAULT_REDIS_DURATION = Duration.ofSeconds(1800);	//默认过期时间

	@Bean
	public RedisStandaloneConfiguration redisStandaloneConfiguration() {

		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setDatabase(redisDatabase);
		redisStandaloneConfiguration.setHostName(redisHost);
		redisStandaloneConfiguration.setPort(redisPort);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
		return redisStandaloneConfiguration;
	}
	
	/**
     * GenericObjectPoolConfig 连接池配置
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
	@Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(25);
        genericObjectPoolConfig.setMinIdle(10);
        genericObjectPoolConfig.setMaxTotal(50);
        return genericObjectPoolConfig;
    }
	
    
	@SuppressWarnings("rawtypes")
	@Bean(name = "lettuceConnectionFactory")
	@Primary
	public LettuceConnectionFactory lettuceConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration, GenericObjectPoolConfig genericObjectPoolConfig) {
		
		LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder().poolConfig(genericObjectPoolConfig).build();
		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
		return factory;
	}
	

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate getStringRedisTemplate(@Qualifier(value="lettuceConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean(name = "redisTemplate")
    public <V> RedisTemplate<String, V> getRedisTemplate(@Qualifier(value="lettuceConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
    
    @Bean(name = "authRedisTemplate")
	public <V> RedisTemplate<String, Object> authRedisTemplate(@Qualifier(value="redisTemplate") RedisTemplate<String, V> redisTemplate,
			@Qualifier(value="lettuceConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
		
		RedisTemplate<String, Object> authRedisTemplate = new RedisTemplate<>();
		Jackson2JsonRedisSerializer<String> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(String.class);
		authRedisTemplate.setConnectionFactory(lettuceConnectionFactory);
		authRedisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
		authRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer);	//有泛型的对象先转换成json字符串再往redis里存，不然反序列化时会报错。
		authRedisTemplate.setHashKeySerializer(redisTemplate.getStringSerializer());
		authRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);	//同上
		return authRedisTemplate;
		
	}
    
    @Bean
    public CacheManager getCacheManager(@Qualifier(value="lettuceConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
    	
    	RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_REDIS_DURATION).disableCachingNullValues();
    	
    	RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(lettuceConnectionFactory)
    			.cacheDefaults(config).transactionAware();
    	return builder.build();
    	
    }
}
