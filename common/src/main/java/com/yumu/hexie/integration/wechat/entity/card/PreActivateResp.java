package com.yumu.hexie.integration.wechat.entity.card;

import java.io.Serializable;

import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.model.user.User;

public class PreActivateResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3032471579504453949L;
	
	private JsSign jsSign;
	private User user;
	public JsSign getJsSign() {
		return jsSign;
	}
	public void setJsSign(JsSign jsSign) {
		this.jsSign = jsSign;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "PreActivateResp [jsSign=" + jsSign + ", user=" + user + "]";
	}
	
	
}
