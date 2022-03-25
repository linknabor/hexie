package com.yumu.hexie.service.repair.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.repair.RepairConstant;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.localservice.repair.RepairSeed;
import com.yumu.hexie.model.localservice.repair.RepairSeedRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.repair.RepairAssignService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: RepairAssignServiceImpl.java, v 0.1 2016年1月11日 下午8:13:05  Exp $
 */
@Service("repairAssignService")
public class RepairAssignServiceImpl implements RepairAssignService {

    @Inject
    private RepairSeedRepository repairSeedRepository;
    @Inject
    private ServiceOperatorRepository serviceOperatorRepository;
    @Inject
    private GotongService gotongService;
    
    /** 
     * @param order
     * @see com.yumu.hexie.service.repair.RepairAssignService#assignOrder(com.yumu.hexie.model.localservice.repair.RepairOrder)
     */
    @Async
    @Override
    public void assignOrder(RepairOrder order) {
        List<ServiceOperator> ops=serviceOperatorRepository.findBySectId(order.getSectId(), ModelConstant.SERVICE_OPER_TYPE_WEIXIU);
        assign(order, ops);
    }
    private void assign(RepairOrder ro, List<ServiceOperator> ops) {
        if(ro.getStatus() == RepairConstant.STATUS_CREATE && ro.getOperatorId() != null && ro.getOperatorId() != 0){
            return;
        }
        for(ServiceOperator op : ops) {
            RepairSeed rs = new RepairSeed(op,ro);
            repairSeedRepository.save(rs);
            //FIXME 发送消息
            gotongService.sendRepairAssignMsg(rs.getOperatorId(),ro);
        }
    }
}
