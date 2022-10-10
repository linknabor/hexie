package com.yumu.hexie.service.search.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.yumu.hexie.service.search.SearchService;

@Service(value = "searchService")
public class SearchServiceImpl implements SearchService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public void save(String searchKey, String searchValue) {
		stringRedisTemplate.opsForZSet().incrementScore(searchKey, searchValue, 1d);
	}
	
	@Override
	public Set<String> get(String searchKey) {
		return stringRedisTemplate.opsForZSet().reverseRange(searchKey, 0, 30);
	}
	
	@Override
	public void removeAll(String searchKey) {
		stringRedisTemplate.delete(searchKey);
	}
	
}
