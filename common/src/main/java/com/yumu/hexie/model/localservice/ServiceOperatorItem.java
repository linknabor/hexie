package com.yumu.hexie.model.localservice;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.yumu.hexie.model.BaseModel;


@Entity
@Table(name = "ServiceOperatorItem", uniqueConstraints ={@UniqueConstraint(columnNames = {"operatorId", "serviceId"})})
public class ServiceOperatorItem extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216439538672180808L;
	
	private long operatorId;
	private long serviceId;	//也可以放产品的ID
	private String serviceName;	// 服务名称
	
	public long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	public long getServiceId() {
		return serviceId;
	}
	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	@Override
	public String toString() {
		return "ServiceOperatorItem [operatorId=" + operatorId + ", serviceId=" + serviceId + ", serviceName="
				+ serviceName + "]";
	}
	
	

}
