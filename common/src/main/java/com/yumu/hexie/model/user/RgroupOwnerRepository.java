package com.yumu.hexie.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RgroupOwnerRepository extends JpaRepository<RgroupOwner, Long> {

	RgroupOwner findByUserId(long userId);
}
