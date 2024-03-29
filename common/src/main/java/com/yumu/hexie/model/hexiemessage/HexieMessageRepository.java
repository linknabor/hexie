package com.yumu.hexie.model.hexiemessage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface HexieMessageRepository extends JpaRepository<HexieMessage, Long>{
	
	public List<HexieMessage> findByUserId(long userId);

	List<HexieMessage> findByBatchNo(String batchNo);

	HexieMessage findByIdAndValidDateGreaterThanEqual(long messageId, String nowDate);

}
