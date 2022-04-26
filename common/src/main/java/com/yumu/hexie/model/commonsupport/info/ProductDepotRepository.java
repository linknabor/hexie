package com.yumu.hexie.model.commonsupport.info;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDepotRepository extends JpaRepository<ProductDepot, Long> {

    //查询团长的商品
    List<ProductDepot> findByOwnerIdAndNameContaining(long ownerId, String name, Pageable pageable);

}
