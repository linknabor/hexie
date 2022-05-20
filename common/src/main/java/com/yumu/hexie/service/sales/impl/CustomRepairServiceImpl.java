package com.yumu.hexie.service.sales.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrderRepository;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.saleplan.RepairSaleRule;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.user.Address;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: RepairOrderServiceImpl.java, v 0.1 2016年1月1日 上午10:54:39  Exp $
 */
@Service("customRepairService")
public class CustomRepairServiceImpl extends CustomOrderServiceImpl {

    @Inject
    private RepairOrderRepository repairOrderRepository;
    /** 
     * @param so
     */
    @Override
    public void postPaySuccess(ServiceOrder so) {
        so.confirm();
        serviceOrderRepository.save(so);
        List<RepairOrder> ros = repairOrderRepository.findByOrderId(so.getId());
        if(ros!=null && ros.size()>0) {
            RepairOrder ro = ros.get(0);
            ro.paySuccess();
            repairOrderRepository.save(ro);
        }
    }

    /** 
     * @param order
     */
    @Override
    public void postOrderConfirm(ServiceOrder order) {
    }

    /** 
     * @param ruleId
     * @return
     */
    @Override
    public SalePlan findSalePlan(long ruleId) {
        RepairSaleRule r = new RepairSaleRule();
        r.setFreeShippingNum(0);
        r.setId(1L);
        r.setName("维修项目");
        r.setFreeShippingNum(1);
        r.setPrice(1);
        r.setTimeoutForPay(3600000000L);
        return r;
    }

    /** 
     * @param order
     * @param rule
     * @param item
     * @param address
     */
    @Override
    public void validateRule(ServiceOrder order, SalePlan rule, OrderItem item, Address address) {
    }

    /** 
     * @param order
     * @see com.yumu.hexie.service.sales.CustomOrderService#postOrderCancel(com.yumu.hexie.model.market.ServiceOrder)
     */
    @Override
    public void postOrderCancel(ServiceOrder order) {
    }

    
}
