package com.yumu.hexie.model.user;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yumu.hexie.common.util.TransactionUtil;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.service.shequ.WuyeService;

public class AddUserSectIdWorker implements Runnable{
	
	private static final Logger log = LoggerFactory.getLogger(AddUserSectIdWorker.class);

	private User user;
	private UserRepository userRepository;
	private WuyeService wuyeService;
	@SuppressWarnings("rawtypes")
	private TransactionUtil transactionUtil;
	
    private AtomicInteger success;
    
    private AtomicInteger fail;
	@Override
	public void run() {
		deal();
	}
	
	public AddUserSectIdWorker() {
		super();
	}

	public AddUserSectIdWorker(User user, UserRepository userRepository, WuyeService wuyeService,
			@SuppressWarnings("rawtypes") TransactionUtil transactionUtil,AtomicInteger success,AtomicInteger fail) {
		super();
		this.user = user;
		this.userRepository = userRepository;
		this.wuyeService = wuyeService;
		this.transactionUtil = transactionUtil;
		this.success=success;
		this.fail=fail;
	}
	@SuppressWarnings("unchecked")
	public void deal(){
		List<User> userList = userRepository.findByWuyeId(user.getWuyeId());
		for (User user : userList) {
			HouseListVO listVo = wuyeService.queryHouse(user.getWuyeId());
			if (listVo != null) {
				if (listVo.getHou_info() != null && listVo.getHou_info().size() > 0) {
					HexieUser hexieUser = new HexieUser();
					hexieUser.setSect_id(listVo.getHou_info().get(0).getSect_id());
					hexieUser.setCsp_id(listVo.getHou_info().get(0).getCsp_id());
					hexieUser.setCenter_id(listVo.getHou_info().get(0).getCenter_id());
					boolean isSuccess = transactionUtil.transact(s -> wuyeService.setUserSectid(user, hexieUser));
				    if(!isSuccess){
				    	fail.incrementAndGet();
				    	log.error("失败用户Id: " + user.getId());
				    }else{
				        success.incrementAndGet();
				    }
				}
			}
		}
	}

}
