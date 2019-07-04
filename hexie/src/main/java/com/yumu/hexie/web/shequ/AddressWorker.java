package com.yumu.hexie.web.shequ;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yumu.hexie.common.util.TransactionUtil;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.model.user.TempUser;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.UserService;

public class AddressWorker implements Runnable {

	@Autowired
	private UserService userService;
	@Autowired
	private WuyeService wuyeService;
	
	
	private Logger log = LoggerFactory.getLogger(AddressWorker.class);
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private TransactionUtil transactionUtil;
	
	private HexieUser hexieUser;
	private TempUser tempUser;
	
	public AddressWorker(HexieUser hexieUser, TempUser tempUser) {
		super();
		this.hexieUser = hexieUser;
		this.tempUser = tempUser;
	}

	@Override
	public void run() {
		deal();
	}

	@SuppressWarnings("unchecked")
	public void deal(){
		
		try {
			List<User> userList = userService.getByTel(tempUser.getTel());
			if (userList == null || userList.size()==0) {
				return;
			}
			User u = userList.get(0);
			HouseListVO listVo = wuyeService.queryHouse(u.getWuyeId());
			if (listVo != null) {
				if (listVo.getHou_info() != null && listVo.getHou_info().size() > 0) {
					hexieUser.setCity_id(listVo.getHou_info().get(0).getCity_id());
					hexieUser.setCity_name(listVo.getHou_info().get(0).getCity_name());
					hexieUser.setProvince_id(listVo.getHou_info().get(0).getProvince_id());
					hexieUser.setProvince_name(listVo.getHou_info().get(0).getProvince_name());
					hexieUser.setRegion_id(listVo.getHou_info().get(0).getRegion_id());
					hexieUser.setRegion_name(listVo.getHou_info().get(0).getRegion_name());
					hexieUser.setCell_addr(listVo.getHou_info().get(0).getCell_addr());
					hexieUser.setSect_name(listVo.getHou_info().get(0).getSect_name());
					transactionUtil.transact(s->wuyeService.setDefaultAddress(u, hexieUser));
					log.info("cell_adress:" + listVo.getHou_info().get(0).getCell_addr());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
	}
	

}
