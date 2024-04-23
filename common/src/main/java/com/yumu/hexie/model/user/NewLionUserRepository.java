package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewLionUserRepository extends JpaRepository<NewLionUser, Long> {

	List<NewLionUser> findByMobile(String mobile);
}
