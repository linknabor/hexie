package com.yumu.hexie.service.health.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
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
	private SystemConfigService systemConfigService;
	
	@Override
	@Transactional
	public void healthReport(User user, Thread thread) {

		Assert.hasText(thread.getThreadContent(), "上报内容不能为空。");

		thread.setThreadCategory(ModelConstant.THREAD_CATEGORY_HEALTH_REPORT);	//类型
		User currUser = userRepository.findOne(user.getId());
		
		thread.setCreateDateTime(System.currentTimeMillis());
		thread.setCreateDate(DateUtil.dtFormat(new Date(), "yyyyMMdd"));
		thread.setCreateTime(DateUtil.dtFormat(new Date().getTime(), "HHMMss"));
		thread.setThreadStatus(ModelConstant.THREAD_STATUS_NORMAL);
		thread.setUserHead(currUser.getHeadimgurl());
		thread.setUserId(currUser.getId());
		thread.setUserName(currUser.getNickname());
		thread.setUserSectId(currUser.getSectId());
		thread.setUserSectName(currUser.getXiaoquName());
		thread.setUserMobile(currUser.getTel());
		thread.setAppid(currUser.getAppId());
		thread.setStickPriority(0);	//默认优先级0，为最低
		threadRepository.save(thread);
	}

	@Override
	public void maskReservation(User user, Thread thread) {

		Assert.hasText(thread.getThreadContent(), "预约身份证号不能为空。");

		thread.setThreadCategory(ModelConstant.THREAD_CATEGORY_MASK_RESV);	//类型
		User currUser = userRepository.findOne(user.getId());
		
		thread.setCreateDateTime(System.currentTimeMillis());
		thread.setCreateDate(DateUtil.dtFormat(new Date(), "yyyyMMdd"));
		thread.setCreateTime(DateUtil.dtFormat(new Date().getTime(), "HHMMss"));
		thread.setThreadStatus(ModelConstant.THREAD_STATUS_NORMAL);
		thread.setUserHead(currUser.getHeadimgurl());
		thread.setUserId(currUser.getId());
		thread.setUserName(currUser.getNickname());
		thread.setUserSectId(currUser.getSectId());
		thread.setUserSectName(currUser.getXiaoquName());
		thread.setUserMobile(currUser.getTel());
		thread.setAppid(currUser.getAppId());
		thread.setStickPriority(0);	//默认优先级0，为最低
		threadRepository.save(thread);
		
	}

	@Override
	public void testTemplate(User user) {
		
		String accessToken = systemConfigService.queryWXAToken(user.getAppId());
		TemplateMsgService.testSend(user.getOpenid(), accessToken, user.getAppId());
		
	}

}
