package com.yumu.hexie.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MiniUserPageAccessRepository extends JpaRepository<MiniUserPageAccess, Long> {

	public MiniUserPageAccess findByPageAndRoleId(String page, String roleId);
}
