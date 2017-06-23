/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.web.user.req;

import java.io.Serializable;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: SimpleRegisterReq.java, v 0.1 2016年3月15日 下午3:14:05  Exp $
 */
public class SimpleRegisterReq implements Serializable{
    private static final long serialVersionUID = -2090643413772467559L;
    private String mobile;
    private String yzm;
    private String name;
    private String sn;	//充电桩设备号
    private String sectId;//充电设备所在小区ID servplat
    
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getYzm() {
        return yzm;
    }
    public void setYzm(String yzm) {
        this.yzm = yzm;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	@Override
	public String toString() {
		return "SimpleRegisterReq [mobile=" + mobile + ", yzm=" + yzm
				+ ", name=" + name + ", sn=" + sn + ", sectId=" + sectId + "]";
	}
    
    
    
}
