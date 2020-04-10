package com.yumu.hexie.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yumu.hexie.model.user.BankCard;
import com.yumu.hexie.model.user.BankCardRepository;
import com.yumu.hexie.service.user.BankCardService;

@Service
public class BankCardServiceImpl implements BankCardService {
	
	@Autowired
	private BankCardRepository bankCardRepository;

	@Override
	public List<BankCard> getByUserId(long userId) {

		Assert.notNull(userId, "用户id不能为空");
		return bankCardRepository.findByUserId(userId);
	}

}
