package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgOperatorRepository extends JpaRepository<OrgOperator, Long> {

	List<OrgOperator> findByUserId(long userId);
	
	OrgOperator findByUserIdAndRoleId(long userId, String roleId);
}
