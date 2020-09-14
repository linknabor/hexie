package com.yumu.hexie.service.eshop.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.notify.PartnerNotification;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.Partner;
import com.yumu.hexie.model.user.PartnerRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.eshop.PartnerService;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * 合伙人相关
 */
@Service
public class PartnerServiceImpl implements PartnerService {

	@Autowired
	private PartnerRepository partnerRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Transactional
	@Override
	public void save(Partner partner) {
		
		Partner pn = partnerRepository.findByTel(partner.getTel());
		if (pn == null) {
			pn = new Partner();
			pn.setName(partner.getName());
			pn.setTel(partner.getTel());
			pn.setUserId(partner.getUserId());
			Date currDate = new Date();
			Date expiredDate = DateUtil.addDate(currDate, 365-1);
			pn.setExpiredDate(expiredDate);
			pn.setInitDate(currDate);
		}else {
			Date currDate = pn.getExpiredDate();
			Date expiredDate = DateUtil.addDate(currDate, 365);
			pn.setExpiredDate(expiredDate);
		}
		partnerRepository.save(pn);
		String key = ModelConstant.KEY_HEXIE_PARTNER + pn.getTel();
		stringRedisTemplate.opsForValue().set(key, pn.getExpiredDate().toString());
	
	}
	
	/**
	 * 合伙人退款更新有效期
	 */
	@Transactional
	@Override
	public void invalidate(PartnerNotification partnerNotification) {
		
		Partner partner = partnerRepository.findByTel(partnerNotification.getTel());
		if (partner == null) {
			return;
		}
		String validDate = partnerNotification.getValidDate();
		validDate = validDate + " 23:59:59";
		Date expiredDate = DateUtil.parse(validDate, DateUtil.dttmSimple);
		partner.setExpiredDate(expiredDate);
		partnerRepository.save(partner);
		String key = ModelConstant.KEY_HEXIE_PARTNER + partner.getTel();
		stringRedisTemplate.opsForValue().set(key, partner.getExpiredDate().toString());
	}
	
	@Override
	public void checkValidation(User user) {
		
		String tel = user.getTel();
		String key = ModelConstant.KEY_HEXIE_PARTNER + tel;
		String validDateStr = stringRedisTemplate.opsForValue().get(key);
		
		if (StringUtils.isEmpty(validDateStr)) {
			Partner partner = partnerRepository.findByTel(tel);
			if (partner == null) {
				throw new BizValidateException("当前用户无法进行此操作。");
			}else {
				validDateStr = partner.getExpiredDate().toString();
				if (StringUtils.isEmpty(validDateStr)) {
					throw new BizValidateException("当前用户无法进行此操作。");
				}
				stringRedisTemplate.opsForValue().set(key, validDateStr);
			}
		}
		Date validDate = null;
		validDate = DateUtil.parse(validDateStr, DateUtil.dateSimple);
		Date now = new Date();
		if (validDate.before(now)) {
			throw new BizValidateException("当前用户无法进行此操作。");
		}

		
	}
	
	@Override
	public void refreshPartnerCache() {
		
		List<Partner> partnerList = partnerRepository.findAll();
		for (Partner partner : partnerList) {
			String key = ModelConstant.KEY_HEXIE_PARTNER + partner.getTel();
			String validateDate = partner.getExpiredDate().toString();
			stringRedisTemplate.opsForValue().set(key, validateDate);
		}
	}
	
	
	
}
