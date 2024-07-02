package com.yumu.hexie.model.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticDataRepository extends JpaRepository<StatisticData, Long> {

	StatisticData findByAppidAndRecordDate(String appid, String recordDate);
}
