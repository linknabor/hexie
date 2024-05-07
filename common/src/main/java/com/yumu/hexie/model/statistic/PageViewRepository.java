package com.yumu.hexie.model.statistic;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

	@Query(value = "select sum(count) as counts from pageview where appid = ?1 and countDate >= ?2 and countDate <= ?3 group by appid ", nativeQuery = true)
	public List<Map<String, Object>> getPageViewByAppidAndDateBetween(String appid, String startDate, String endDate);
	
	public PageView findByAppidAndCountDateAndPage(String appid, String countDate, String page);
}
