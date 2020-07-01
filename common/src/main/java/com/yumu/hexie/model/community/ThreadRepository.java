/**
 * 
 */
package com.yumu.hexie.model.community;

import java.util.List;

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
	

	@Query(value="from Thread t where t.threadStatus = ?1 and t.userSectId = ?2 and t.threadCategory = ?3 and t.userId = ?4 ")
	public List<Thread> getThreadListByCategory(String threadStatus, String userSectId, int threadCategory, long userId, Pageable page);
	
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
			+ " and userSectId in ?5 \n#pageable\n",
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
			+ " and userSectId in ?5 "
			+ " order by createDateTime desc "
			+ " \n#pageable\n",
			countQuery="select count(*) from thread  where threadStatus = ?1 " 
			+ " and threadCategory = ?2 "
			+ " and IF (?3!='', createDate >= ?3, 1=1)"
			+ " and IF (?4!='', createDate <= ?4, 1=1)"
			+ " and userSectId in ?5 "
			,nativeQuery = true)
	public Page<Thread> getThreadListByCategory(String threadStatus, int threadCategory, String beginDate, String endDate, List<String> sectIds, Pageable pageable);
	
	
	
	
	
	
}
