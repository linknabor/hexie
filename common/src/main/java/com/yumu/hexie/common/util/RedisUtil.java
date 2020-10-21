package com.yumu.hexie.common.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.async.RedisAsyncCommands;

public class RedisUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
	
	private static final long DEFAULT_SCAN_SIZE = 10000L;	//10000
	
	/**
     * 获取 指定格式的所有key
     * 迭代执行 SCAN 0 MATCH {pattern} COUNT 10000
     *
     * @param redisTemplate redisTemplate
     * @param pattern       匹配规则
     * @return 指定格式的所有key
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> scanKeys(RedisTemplate redisTemplate, String pattern) {
        //SCAN 0 MATCH {pattern} COUNT 10000
        return (List<String>) redisTemplate.execute(connection -> {
            //scan 迭代遍历键，返回的结果可能会有重复，需要客户端去重复
            Set<String> redisKeys = new HashSet<>();
            //lettuce 原生api
            RedisAsyncCommands conn = (RedisAsyncCommands) connection.getNativeConnection();
            //游标
            ScanCursor curs = ScanCursor.INITIAL;
            try {
                //采用 SCAN 命令，迭代遍历所有key
                while (!curs.isFinished()) {
                    ScanArgs args = ScanArgs.Builder.matches(pattern).limit(DEFAULT_SCAN_SIZE);
                    logger.info("SCAN {} MATCH {} COUNT {}", curs.getCursor(), pattern, DEFAULT_SCAN_SIZE);
                    RedisFuture<KeyScanCursor<byte[]>> future = conn.scan(curs, args);
                    KeyScanCursor<byte[]> keyCurs = future.get();
                    List<byte[]> ks = keyCurs.getKeys();
                    Set<String> set = ks.stream().map(bytes -> new String(bytes, StandardCharsets.UTF_8)).collect(Collectors.toSet());
                    logger.info("return size:{}", set.size());
                    redisKeys.addAll(set);
                    curs = keyCurs;
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return new ArrayList<>(redisKeys);
        }, true);
    }


}
