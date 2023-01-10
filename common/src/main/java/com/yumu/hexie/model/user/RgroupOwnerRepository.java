package com.yumu.hexie.model.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RgroupOwnerRepository extends JpaRepository<RgroupOwner, Long> {

	RgroupOwner findByUserId(long userId);
	
	RgroupOwner findById(long id);
	
	@Query(value = "select ro.id, ro.userId, ro.name, ro.tel, ro.createDate, ro.attendees, ro.members, ro.feeRate, ro.headImgUrl from rgroupOwner ro "
			+ "join orgoperator op on ro.userId = op.userId "
			+ "where ro.id > 0 "
			+ "and if(?1 > 0, ro.id = ?1, 1 = 1) "
			+ "and if(?2!='', ro.tel like CONCAT('%',?2,'%'), 1 = 1 ) "
			+ "and if(?3!='', ro.name like CONCAT('%', ?3, '%'), 1 = 1 ) "
			+ "and if(?4!='', op.orgId = ?4, 1 = 1) "
			, countQuery = "select count(*) from rgroupOwner ro "
				+ "join orgoperator op on ro.userId = op.userId "
				+ "where ro.id > 0 "
				+ "and if(?1 > 0, ro.id = ?1, 1 = 1) "
				+ "and if(?2!='', ro.tel like CONCAT('%',?2,'%'), 1 = 1 ) "
				+ "and if(?3!='', ro.name like CONCAT('%', ?3, '%'), 1 = 1 ) " 
				+ "and if(?4!='', op.orgId = ?4, 1 = 1) "
			, nativeQuery = true )
	Page<Object[]> findByUserIdAndTelLikeAndNameAndAgentNo(long ownerId, String tel, String name, String agentNo, Pageable pageable);
}
