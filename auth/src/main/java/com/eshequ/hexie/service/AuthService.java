package com.eshequ.hexie.service;

import com.eshequ.hexie.vo.ComponentAcessToken;
import com.eshequ.hexie.vo.PreAuthCode;
import com.eshequ.hexie.vo.VerifyTicket;

public interface AuthService {
	
	void authEventHandle(String xml);

	void saveVerifyTicket(VerifyTicket verifyTicket);
	
	ComponentAcessToken getComponentAccessToken(String verifyTicket);
	
	PreAuthCode getPreAuthCode();
}
