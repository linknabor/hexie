package com.yumu.hexie.model.commonsupport.info;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDepotRepository extends JpaRepository<ProductDepot, Long> {

    //查询团长的商品
    List<ProductDepot> findByOwnerIdAndNameContaining(long ownerId, String name, Pageable pageable);

    String sqlColumn1 = " a.id,a.name,a.mainPicture,a.smallPicture,a.pictures,a.miniPrice,a.oriPrice,a.singlePrice,a.serviceDesc,a.totalCount,e.name as userName, count(DISTINCT d.id) as groupCount, count(distinct f.orderId) as orderNum ";

    @Query(value = "select " + sqlColumn1
            + "from ProductDepot a "
            + "left join Product b on b.depotId = a.id "
            + "left join ProductRule c on b.id = c.productId "
            + "left join rgrouprule d on c.ruleId = d.id "
            + "join user e on a.ownerId = e.id  "
            + "left join orderItem f on b.id = f.productId "
            + "where 1 = 1 "
            + "and IF (?1!='', a.name like CONCAT('%',?1,'%'), 1=1) "
            + "and IF (?2!='', e.name like CONCAT('%',?2,'%'), 1=1) "
            + "group by a.id "
            , countQuery = "select count(1) from ProductDepot a "
            + "left join Product b on b.depotId = a.id "
            + "left join ProductRule c on b.id = c.productId "
            + "left join rgrouprule d on c.ruleId = d.id "
            + "join user e on a.ownerId = e.id  "
            + "left join orderItem f on b.id = f.productId "
            + "where 1 = 1 "
            + "and IF (?1!='', a.name like CONCAT('%',?1,'%'), 1=1) "
            + "and IF (?2!='', e.name like CONCAT('%',?2,'%'), 1=1) "
            + "group by a.id "
            , nativeQuery = true)
    Page<Object[]> getDepotListPage(String name, String ownerName, Pageable pageable);



}
