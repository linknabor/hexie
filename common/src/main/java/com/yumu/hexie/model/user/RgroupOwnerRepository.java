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
	
	@Query(value = "select id, userId, name, tel, createDate, attendees, members, feeRate, headImgUrl from rgroupOwner "
			+ "where id > 0 "
			+ "and if(?1 > 0, id = ?1, 1 = 1) "
			+ "and if(?2!='', tel like CONCAT('%',?2,'%'), 1 = 1 ) "
			+ "and if(?3!='', name like CONCAT('%', ?3, '%'), 1 = 1 ) " 
			, countQuery = "select count(*) from rgroupOwner "
				+ "where id > 0 "
				+ "and if(?1 > 0, id = ?1, 1 = 1) "
				+ "and if(?2!='', tel like CONCAT('%',?2,'%'), 1 = 1 ) "
				+ "and if(?3!='', name like CONCAT('%', ?3, '%'), 1 = 1 ) " 
			, nativeQuery = true )
	Page<Object[]> findByUserIdAndTelLikeAndName(long ownerId, String tel, String name, Pageable pageable);
}
