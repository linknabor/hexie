package com.yumu.hexie.model.commonsupport.info;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDepotTagsRepository extends JpaRepository<ProductDepotTags, Long> {

    //查询团长的商品
    List<ProductDepotTags> findByOwnerId(long ownerId);
    
    ProductDepotTags findByNameAndAgentNo(String name, String agentNo);

}
