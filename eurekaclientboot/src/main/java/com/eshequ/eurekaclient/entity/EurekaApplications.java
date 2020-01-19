package com.eshequ.eurekaclient.entity;

import java.io.Serializable;
import java.util.List;

public class EurekaApplications implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3823190429248590987L;
	
	private List<EurekaApplication> application;

	public List<EurekaApplication> getApplication() {
		return application;
	}

	public void setApplication(List<EurekaApplication> application) {
		this.application = application;
	}

	
	
}
