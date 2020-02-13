package com.yumu.hexie.service.health.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.health.HealthService;

/**
 * 肺炎疫情相关
 * @author david
 *
 */
@Service
public class HealthServiceImpl implements HealthService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ThreadRepository threadRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Override
	@Transactional
	public void addHealthReport(User user, Thread thread) {

		Assert.hasText(thread.getThreadContent(), "上报内容不能为空。");

		thread.setThreadCategory(ModelConstant.THREAD_CATEGORY_HEALTH_REPORT);	//类型
		String content = thread.getThreadContent();
		String[]answers = content.split(",");
		if (answers.length<3) {
			throw new BizValidateException("提交的答案数为：" + answers.length + ",应为4题。");
		}
		boolean isNormal = true;	//是否正常
		for (String answer : answers) {
			answer = answer.trim();
			if ("1".equals(answer)) {
				isNormal = false;
				break;
			}
			if (answer.length()>1) {
				isNormal = false;
				break;
			}
			
		}
		if (!isNormal) {
			thread.setRemark("1");
		}else {
			thread.setRemark("0");
		}
		saveThread(user, thread);
		threadRepository.save(thread);
	}

	@Override
	@Transactional
	public void addMaskReservation(User user, Thread thread) {

		Assert.hasText(thread.getThreadContent(), "预约信息不能为空。");
		
		String content = thread.getThreadContent();
		String[]answers = content.split(",");
		if (answers.length<3) {
			throw new BizValidateException("预约信息未填写完整。");
		}
		thread.setThreadCategory(ModelConstant.THREAD_CATEGORY_MASK_RESV);	//类型
		thread.setUserName(answers[0]);
		thread.setRemark(answers[2]);	//把证件号存起来，可能需要限制一个身份证的预约次数 TODO 
		saveThread(user, thread);
		threadRepository.save(thread);
		
	}

	private void saveThread(User user, Thread thread) {
		User currUser = userRepository.findOne(user.getId());
		List<Address> addrList = addressRepository.findAllByUserId(currUser.getId());
		Address currAdddr = new Address();
		for (Address address : addrList) {
			if (address.getXiaoquName().equals(user.getXiaoquName())) {
				currAdddr = address;
				break;
			}
		}
		
		thread.setCreateDateTime(System.currentTimeMillis());
		thread.setCreateDate(DateUtil.dtFormat(new Date(), "yyyyMMdd"));
		thread.setCreateTime(DateUtil.dtFormat(new Date().getTime(), "HHMMss"));
		thread.setThreadStatus(ModelConstant.THREAD_STATUS_NORMAL);
		thread.setUserHead(currUser.getHeadimgurl());
		thread.setUserId(currUser.getId());
		if (StringUtils.isEmpty(thread.getUserName())) {
			thread.setUserName(currUser.getName());	//口罩预约功能，这里存入用户填写的真实姓名。健康上报功能直接取用户注册时的微信名字
		}
		thread.setUserSectId(currUser.getSectId());
		thread.setUserSectName(currUser.getXiaoquName());
		thread.setUserAddress(currAdddr.getDetailAddress());
		thread.setUserMobile(currUser.getTel());
		thread.setAppid(currUser.getAppId());
		thread.setStickPriority(0);	//默认优先级0，为最低
	}

	/**
	 * 获取健康上报列表
	 */
	@Override
	public Page<Thread> getHealthReport(BaseRequestDTO<Thread> baseRequestDTO) {
		Thread thread = baseRequestDTO.getData();
		thread.setThreadCategory(ModelConstant.THREAD_CATEGORY_HEALTH_REPORT);
		Page<Thread> page = getThread(baseRequestDTO);
		return page;
		
	}

	/**
	 * 获取口罩预约列表
	 */
	@Override
	public Page<Thread> getMaskReservation(BaseRequestDTO<Thread> baseRequestDTO) {
		Thread thread = baseRequestDTO.getData();
		thread.setThreadCategory(ModelConstant.THREAD_CATEGORY_MASK_RESV);
		Page<Thread> page = getThread(baseRequestDTO);
		return page;
		
	}
	
	private Page<Thread> getThread(BaseRequestDTO<Thread> baseRequestDTO) {
		
		Thread thread = baseRequestDTO.getData();
		Pageable pageable = new PageRequest(baseRequestDTO.getCurr_page(), baseRequestDTO.getPage_size());
		Page<Thread> page = threadRepository.getThreadListByCategory(ModelConstant.THREAD_STATUS_NORMAL, thread.getThreadCategory(),
				baseRequestDTO.getBeginDate(), baseRequestDTO.getEndDate(), baseRequestDTO.getSectList(), pageable);
		return page;
	}
	

}
