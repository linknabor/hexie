/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.repair;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.localservice.repair.RepairProject;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorVo;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.repair.req.RepairCancelReq;
import com.yumu.hexie.service.repair.req.RepairComment;
import com.yumu.hexie.service.repair.resp.RepairListItem;
import com.yumu.hexie.vo.req.RepairOrderReq;


/**
 * <pre>
 * 维修服务
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: RepairService.java, v 0.1 2016年1月1日 上午6:57:59  Exp $
 */
public interface RepairService {

    public Long repair(RepairOrderReq req,User user);
    public boolean finish(long orderId,User user);
    public JsSign requestPay(long orderId, float amount, User user) throws Exception;
    public void payOffline(long orderId, float amount, User user);
    public void notifyPaySuccess(long orderId,User user);
    public void cancel(RepairCancelReq req,User user);

    public void deleteByUser(long orderId,User user);
    public void deleteByOperator(long orderId,User user);
    
    public void comment(RepairComment comment, User user);
    
    public void accept(long repairOrderId,User user);
    
    public void finishByOperator(long repairOrderId,User user);
    
    public List<RepairListItem> queryTop20ByOperatorAndStatus(User user,int status);
    public List<RepairListItem> queryTop20ByUser(User user);
    public RepairOrder queryById(long id);
    public List<RepairProject> queryProject(int repairType);
    
    public Long reassgin(long orderId, User user);
	public Page<RepairOrder> getRepairOderList(BaseRequestDTO<Map<String,String>> baseRequestDTO);
	
	public Page<Object> getServiceoperator(BaseRequestDTO<Map<String,String>> baseRequestDTO);
	
	//保存维修工
	public int saveRepiorOperator(BaseRequestDTO<ServiceOperatorVo> baseRequestDTO);
	//根据维修工id获取信息
	public Map<String, Object> operatorInfo(BaseRequestDTO<String> baseRequestDTO);
	//删除维修工信息
	public void deleteOperator(BaseRequestDTO<Map<String, String>> baseRequestDTO);
	//根据电话检验维修员是否存在
	public int checkTel(BaseRequestDTO<String> baseRequestDTO);
	
	//根据维修工id获取服务区域
	public List<String> showSect(String id);
	//根据电话获取用户
	public List<User> getHexieUserInfo(String data);
	
	//报修页面显示的默认地址，这个地址必须是在servplat平台上存在的
	Address getDefaultAddress(User user);
	void updateBindedAddress(User user, List<HexieHouse> houseList);
}
