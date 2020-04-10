package com.yumu.hexie.service.user;

import java.util.List;

import com.yumu.hexie.model.user.BankCard;

public interface BankCardService {
	
	List<BankCard> getByUserId(long userId);

}
