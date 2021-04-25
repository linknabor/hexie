package com.yumu.hexie.model.community;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query(value = "select n.* from ( "
			+ "select n.* from notice n where n.appid = '' or n.appid is null "
			+ "union all "
			+ "select n.* from notice n where n.appid = ?1 "
			+ "union all "
			+ "select n.* from notice n join noticeSect ns on n.id = ns.noticeId "
			+ "where ns.sectId = ?2 "
			+ ") n ", 
			nativeQuery = true)
	public List<Notice>getNoticeList(String appid, String sectId, Pageable pageable);
	
	
}
