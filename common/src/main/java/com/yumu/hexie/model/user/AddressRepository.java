package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {
	List<Address> findAllByUserId(long userId);
	
	@Query("from Address a where a.userId = ?1 and a.detailAddress=?2")
	List<Address> getAddressByuserIdAndAddress(long userId, String cell_addr);
	
	@Query("from Address a where a.userId = ?1 and a.main=?2")
	List<Address> getAddressByMain(long userId, boolean main);
	
	List<Address> findByUserIdAndBind(long userId, boolean bind);
}
