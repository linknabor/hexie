package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TempUserRepository extends JpaRepository<TempUser, Long>{
	
	public List<TempUser> findByType(String type);
	
	public List<TempUser> findById(Long id);
	
	public List<TempUser> findBySectid(String sectid);
	
	
	
}
