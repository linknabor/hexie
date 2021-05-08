package com.yumu.hexie.model.community;

import java.util.List;

import org.springframework.data.domain.Page;
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


	@Query(value = "select distinct * from notice "
			+ "where status = ?1 and if( ?2 != '', id = ?2, 1=1 )"
			+ "and if( ?3 != '', title like CONCAT('%',?3,'%') , 1=1 ) and if( ?4 != '', publishDate >= ?4, 1=1 ) "
			+ "and if ( ?5 != '', publishDate <= ?5, 1=1) and noticeType = ?6 ",
			countQuery = "select count(*) from notice "
					+ "where status = ?1 and if( ?2 != '', id = ?2, 1=1 ) "
					+ "and if( ?3 != '', title like %?3%, 1=1 ) and if( ?4 != '', publishDate >= ?4, 1=1 ) "
					+ "and if ( ?5 != '', publishDate <= ?5, 1=1) and noticeType = ?6 ",
			nativeQuery = true)
	public Page<Notice> queryNoticeMutipleCons(int status, long id, String title,
											   String startDate, String endDate, int msgType, Pageable pageable);
	
}
