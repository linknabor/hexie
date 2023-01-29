package com.yumu.hexie.model.commonsupport.info;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDepotRepository extends JpaRepository<ProductDepot, Long> {

    //查询团长的商品
    List<ProductDepot> findByOwnerIdAndNameContaining(long ownerId, String name, Pageable pageable);
    
    //查询可以帮卖的商品
    List<ProductDepot> findByAgentIdAndNameContaining(long agentId, String name, Pageable pageable);

    String sqlColumn1 = " a.id,a.name,a.createDate,a.mainPicture,a.smallPicture,a.pictures,a.miniPrice,a.oriPrice,a.singlePrice,a.otherDesc,a.totalCount,a.ownerName as userName, a.areaLimit, count(DISTINCT d.id) as groupCount, count(distinct f.orderId) as orderNum ";

    @Query(value = "select " + sqlColumn1
            + "from ProductDepot a "
            + "left join Product b on b.depotId = a.id "
            + "left join ProductRule c on b.id = c.productId "
            + "left join rgrouprule d on c.ruleId = d.id "
            + "left join orderItem f on b.id = f.productId "
            + "where 1 = 1 "
            + "and IF (?1!='', a.name like CONCAT('%',?1,'%'), 1=1) "
            + "and IF (?2!='', a.ownerName like CONCAT('%',?2,'%'), 1=1) "
            + "and (COALESCE(?3) IS NULL OR (b.agentId IN (?3) )) "
            + "group by a.id "
            , countQuery = "select count(1) from ProductDepot a "
            + "left join Product b on b.depotId = a.id "
            + "left join ProductRule c on b.id = c.productId "
            + "left join rgrouprule d on c.ruleId = d.id "
            + "left join orderItem f on b.id = f.productId "
            + "where 1 = 1 "
            + "and IF (?1!='', a.name like CONCAT('%',?1,'%'), 1=1) "
            + "and IF (?2!='', a.ownerName like CONCAT('%',?2,'%'), 1=1) "
            + "and (COALESCE(?3) IS NULL OR (b.agentId IN (?3) )) "
            + "group by a.id "
            , nativeQuery = true)
    Page<Object[]> getDepotListPage(String name, String ownerName, List<Long> agentIds, Pageable pageable);



}
