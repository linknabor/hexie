package com.yumu.hexie.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

	Partner findByTel(String tel);
	
}
