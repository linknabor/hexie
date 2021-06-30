package com.yumu.hexie.model.community;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query(value = "select DISTINCT n.* from ( "
			+ "select n.* from notice n where n.status = ?1 and n.noticeType = '9' "
			+ "union all "
			+ "select n.* from notice n where n.status = ?1 and n.appid = ?2 and n.noticeType not in ?5 "
			+ "union all "
			+ "select n.* from notice n join noticeSect ns on n.id = ns.noticeId "
			+ "where n.status = ?1 and ns.sectId = ?3 "
			+ "union all "
			+ "select n.* from notice n where n.status = ?1 and n.appid = ?2 and n.openid= ?4 and n.noticeType in ?5 "
			+ ") n ", 
			nativeQuery = true)
	public List<Notice>getNoticeList(int status, String appid, String sectId, String openid, List<Integer> list, Pageable pageable);

	public Notice findByOutsideKey(Long outsidKey);
}
