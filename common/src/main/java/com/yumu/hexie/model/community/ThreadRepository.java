/**
 * 
 */
package com.yumu.hexie.model.community;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author HuYM
 *
 */
public interface ThreadRepository extends JpaRepository<Thread, Long> {

	
	public List<Thread> findByThreadStatus(String threadStatus, Pageable page);
	
	public List<Thread> findByThreadStatusAndUserSectId(String threadStatus, long userSectId, Pageable page);
	
	@Query(value="from Thread t where t.threadStatus = ?1 and t.userSectId = ?2 and t.threadCategory = ?3 ")
	public List<Thread> getThreadListByCategory(String threadStatus, long userSectId, String threadCategory, Pageable page);
	
	@Query(value="from Thread t where t.threadStatus = ?1 and t.threadCategory = ?2 ")
	public List<Thread> getThreadListByCategory(String threadStatus, String threadCategory, Pageable page);
	
	@Query(value="from Thread t where t.threadStatus = ?1 and t.userSectId = ?2 and t.threadCategory <> ?3 ")
	public List<Thread> getThreadListByNewCategory(String threadStatus, long userSectId, String threadCategory, Pageable page);
	
	@Query(value="from Thread t where t.threadStatus = ?1 and t.threadCategory <> ?2 ")
	public List<Thread> getThreadListByNewCategory(String threadStatus, String threadCategory, Pageable page);
	
	public List<Thread> findByThreadStatusAndUserId(String threadStatus, long userId, Sort sort);
	
	@Query(value="select thread.*,FROM_UNIXTIME(thread.createDateTime / 1000,'%Y-%m-%d %H:%i:%s') create_DateTime,"
			+ "user.nickname from thread join user on thread.userId = user.id where threadStatus = 0 " 
			+ " and IF (?1!='', user.nickName like CONCAT('%',?1,'%'), 1=1)"
			+ " and IF (?2!='', thread.createDate = ?2, 1=1)"
			+ " and thread.userSectId in ?3 order by thread.stickPriority desc,thread.threadId desc  limit ?4,?5",nativeQuery = true)
	public List<Object> getThreadList(String nickName, String createDate,String[] sectIds,int pageNum,int pageSize);
	
	@Query(value="select count(*) from thread join user on thread.userId = user.id where threadStatus = 0 " 
			+ " and IF (?1!='', user.nickName like CONCAT('%',?1,'%'), 1=1)"
			+ " and IF (?2!='', thread.createDate = ?2, 1=1)"
			+ " and thread.userSectId in ?3",nativeQuery = true)
	public int getThreadListCount(String nickName, String createDate,String[] sectIds);
	
	@Query(nativeQuery = true,value="update thread set threadStatus=1 where threadId in ?1")
	public int deleteThread(String[] threadIds);
}
