/**
 * 
 */
package com.yumu.hexie.model.community;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author HuYM
 *
 */
public interface ThreadRepository extends JpaRepository<Thread, Long> {

	
	public List<Thread> findByThreadStatus(String threadStatus, Pageable page);
	
	public List<Thread> findByThreadStatusAndUserSectId(String threadStatus, String userSectId, Pageable page);
	
	public List<Thread> findByThreadStatusAndUserId(String threadStatus, long userId, Pageable page);
	

	@Query(value="select * from Thread t where t.threadStatus = ?1 and IF (ifnull(?2,'') != '', t.userSectId = ?2, 1=1) and t.threadCategory = ?3 " +
			"and IF (ifnull(?4,'') != '', t.userId = ?4, 1=1) ", nativeQuery = true)
	public List<Thread> getThreadListByCategory(String threadStatus, String userSectId, int threadCategory, Long userId, Pageable page);

	@Query(value="from Thread t where t.threadStatus = ?1 and t.threadCategory = ?2 and t.userId = ?3 ")
	public List<Thread> getThreadListByCategoryAndUserId(String threadStatus, int threadCategory, long userId, Pageable page);
	
	@Query(value="from Thread t where t.threadStatus = ?1 and t.userSectId = ?2 and t.threadCategory <> ?3 ")
	public List<Thread> getThreadListByNewCategory(String threadStatus, String userSectId, int threadCategory, Pageable page);

	
	@Query(value="from Thread t where t.threadStatus = ?1 and t.threadCategory <> ?2 ")
	public List<Thread> getThreadListByNewCategory(String threadStatus, int threadCategory, Pageable page);
	
	public List<Thread> findByThreadStatusAndUserId(String threadStatus, long userId, Sort sort);
	
	@Query(value="select thread.* from thread"
			+ " where threadStatus = 0 " 
			+ " and threadCategory = ?1 "
			+ " and IF (?2!='', userName like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', createDate = ?3, 1=1)"
			+ " and IF (?4!='', userSectId = ?4, 1=1)"
			+ " and userSectId in ?5 ",
			countQuery="select count(*) from thread  where threadStatus = 0 " 
			+ " and threadCategory = ?1 "
			+ " and IF (?2!='', userName like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', createDate = ?3, 1=1)"
			+ " and IF (?4!='', userSectId = ?4, 1=1)"
			+ " and userSectId in ?5"
			,nativeQuery = true)
	public Page<Thread> getThreadList(int threadCategory, String nickName, String createDate,String sectId,List<String> sectIds,Pageable pageable);
	
	@Modifying
	@Query(nativeQuery = true,value="update thread set threadStatus=1 where threadId in ?1")
	public int deleteThread(String[] threadIds);
	
	
	@Query(value="select thread.* from thread"
			+ " where threadStatus = ?1 " 
			+ " and threadCategory = ?2 "
			+ " and IF (?3!='', createDate >= ?3, 1=1)"
			+ " and IF (?4!='', createDate <= ?4, 1=1)"
			+ " and userSectId in ?5 ",
			countQuery="select count(*) from thread  where threadStatus = ?1 " 
			+ " and threadCategory = ?2 "
			+ " and IF (?3!='', createDate >= ?3, 1=1)"
			+ " and IF (?4!='', createDate <= ?4, 1=1)"
			+ " and userSectId in ?5 "
			,nativeQuery = true)
	public Page<Thread> getThreadListByCategory(String threadStatus, int threadCategory, String beginDate, String endDate, List<String> sectIds, Pageable pageable);
	
	final String column1 = "t.threadId, t.createDateTime, t.userSectId, t.userSectName, t.userCspId, t.userName, "
			+ "t.attachmentUrl, t.threadContent ";
	
	
	@Query(value="select " + column1 + " from thread t"
			+ " where threadStatus = 0 " 
			+ " and threadCategory = ?1 "
			+ " and IF (?2!='', userName like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', createDateTime >= ?3, 1=1)"
			+ " and IF (?4!='', createDateTime <= ?4, 1=1)"
			+ " and IF (?5!='', userSectId = ?5, 1=1)"
			+ " and (COALESCE(?6) IS NULL OR (userSectId IN (?6) )) "
			, countQuery="select count(1) from thread where threadStatus = 0 " 
			+ " and threadCategory = ?1 "
			+ " and IF (?2!='', userName like CONCAT('%',?2,'%'), 1=1)"
			+ " and IF (?3!='', createDateTime >= ?3, 1=1)"
			+ " and IF (?4!='', createDateTime <= ?4, 1=1)"
			+ " and IF (?5!='', userSectId = ?5, 1=1)"
			+ " and (COALESCE(?6) IS NULL OR (userSectId IN (?6) )) "
			,nativeQuery = true)
	public Page<Object[]> getThreadList(int threadCategory, String userName, String startDate, String endDate,String sectId, List<String> sectIds, Pageable pageable);
	

	@Query(value="select a.userSectId, a.userSectName, count(distinct a.threadId) as num from thread a " +
			"where a.createDate >= ?1 " +
			"and a.createDate <= ?2 " +
			"and a.userSectId in (?3) " +
			"group by a.userSectId "
			,nativeQuery = true)
	List<Object[]> findThreadCount(String startDate, String endDate, String[] sectIds);

	@Query(value="select b.userSectId, b.userSectName, count(distinct b.threadId) as num from threadcomment a " +
			"join thread b on a.threadId = b.threadId " +
			"where a.isManageComment = '1' " +
			"and b.createDate >= ?1 " +
			"and b.createDate <= ?2 " +
			"and b.userSectId in (?3) " +
			"group by b.userSectId "
			,nativeQuery = true)
	List<Object[]> findThreadCommentCount(String startDate, String endDate, String[] sectIds);

	@Query(value="select t.userSectId, t.userSectName, count(distinct t.threadId) as num from ( " +
			"select m.threadId from ( " +
			"select z.threadId, group_concat(z.isManageComment SEPARATOR '/') as abc from ( " +
			"select a.threadId, a.isManageComment from threadcomment a " +
			"join thread b on a.threadId = b.threadId " +
			"where b.createDate >= ?1 " +
			"and b.createDate <= ?2 " +
			"and b.userSectId in (?3) " +
			"group by a.threadId, a.isManageComment " +
			") z group by z.threadId " +
			") m  " +
			"where m.abc = '0' " +
			"UNION ALL " +
			"select a.threadId from thread a " +
			"where a.createDate >= ?1 " +
			"and a.createDate <= ?2 " +
			"and a.userSectId in (?3) " +
			"and a.commentsCount = '0' " +
			") w " +
			"join thread t on w.threadId = t.threadId " +
			"group by t.userSectId "
			,nativeQuery = true)
	List<Object[]> findThreadNoCommentCount(String startDate, String endDate, String[] sectIds);

	@Query(value="select a.userSectId, a.userSectName, count(distinct a.threadId) as num from thread a " +
			"where a.createDate >= ?1 " +
			"and a.createDate <= ?2 " +
			"and a.userSectId in (?3) " +
			"and a.rectified = '1' " +
			"group by a.userSectId "
			,nativeQuery = true)
	List<Object[]> findThreadRectified(String startDate, String endDate, String[] sectIds);
}
