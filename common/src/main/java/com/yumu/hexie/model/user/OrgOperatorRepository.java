package com.yumu.hexie.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgOperatorRepository extends JpaRepository<OrgOperator, Long> {

	OrgOperator findByUserId(long userId);
}
