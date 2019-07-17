package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>{
	List<Member> findByUserid(long userid);
	Member findById(long billid);
}
