package com.yumu.hexie.model.agent;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.yumu.hexie.model.BaseModel;

@Entity
@Table(name = "agent", uniqueConstraints = {@UniqueConstraint(columnNames="agentNo")})	
public class Agent extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8356721712110977266L;

	private String name;
	private String agentNo;
	private int status;	// 1--on, 0--off
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Agent [name=" + name + ", agentNo=" + agentNo + ", status=" + status + "]";
	}
		
	
	
}
