package com.yumu.hexie.model.user;

import java.util.List;

import com.yumu.hexie.common.util.TransactionUtil;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.service.shequ.WuyeService;

public class TempHouseWorker implements Runnable {
	
	private TempHouse tempHouse;
	private UserRepository userRepository;
	private WuyeService wuyeService;
	@SuppressWarnings("rawtypes")
	private TransactionUtil transactionUtil;
	
	public TempHouseWorker() {
		super();
	}

	public TempHouseWorker(TempHouse tempHouse, WuyeService wuyeService, 
			UserRepository userRepository, @SuppressWarnings("rawtypes") TransactionUtil transactionUtil) {
		super();
		this.tempHouse = tempHouse;
		this.userRepository = userRepository;
		this.wuyeService = wuyeService;
		this.transactionUtil = transactionUtil;
	}

	@Override
	public void run() {
		deal();
	}
	
	@SuppressWarnings("unchecked")
	public void deal() {
		
		List<User> userList = userRepository.findByWuyeId(tempHouse.getWuyeId());
		for (User user : userList) {	//这里理论上应该只会出现一条
			
			HexieUser hexieUser = new HexieUser();
			hexieUser.setUser_id(tempHouse.getWuyeId());
			hexieUser.setCell_addr(tempHouse.getCellAddr());
			hexieUser.setSect_name(tempHouse.getSectName());
			hexieUser.setCity_name(tempHouse.getCityName());
			hexieUser.setRegion_name(tempHouse.getRegionName());
			hexieUser.setProvince_name(tempHouse.getProvinceName());
			transactionUtil.transact(s -> wuyeService.setDefaultAddress(user, hexieUser));
		}
		
	}

}
