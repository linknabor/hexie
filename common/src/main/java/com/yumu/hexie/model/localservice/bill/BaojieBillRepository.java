package com.yumu.hexie.model.localservice.bill;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.localservice.HomeServiceConstant;

public interface BaojieBillRepository extends JpaRepository<BaojieBill, Long> {
    BaojieBill findById(long id);
    List<BaojieBill> findByUserId(long userId, Pageable page);

    @Query("From BaojieBill b where b.status = " + HomeServiceConstant.ORDER_STATUS_CREATE
        +" AND b.createDate < ?1")
    List<BaojieBill> findTimeoutBill(long latestTime);
}
