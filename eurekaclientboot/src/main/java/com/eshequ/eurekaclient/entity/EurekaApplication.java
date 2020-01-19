package com.eshequ.eurekaclient.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 访问http://somesit.com:8761/eureka/apps/返回的注册的服务信息
 * @author david
 *
 */
public class EurekaApplication implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7478917972641111275L;

	private String name;
	private List<EurekaApplicationInstance> instance;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<EurekaApplicationInstance> getInstance() {
		return instance;
	}
	public void setInstance(List<EurekaApplicationInstance> instance) {
		this.instance = instance;
	}
	
	
    
    
    
}
