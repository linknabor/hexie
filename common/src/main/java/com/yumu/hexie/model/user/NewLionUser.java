package com.yumu.hexie.model.user;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.yumu.hexie.model.BaseModel;

@Entity
@Table(indexes= {@Index(columnList="mobile")})
public class NewLionUser extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5384072826437078262L;
	
	private String cellAddr;
	private String ownerName;
	private String cspName;
	private String sectName;
	private String mobile;
	private String sectId;
	private String fdSectId;
	
	public String getCellAddr() {
		return cellAddr;
	}
	public void setCellAddr(String cellAddr) {
		this.cellAddr = cellAddr;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getCspName() {
		return cspName;
	}
	public void setCspName(String cspName) {
		this.cspName = cspName;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSectId() {
		return sectId;
	}
	public void setSectId(String sectId) {
		this.sectId = sectId;
	}
	public String getFdSectId() {
		return fdSectId;
	}
	public void setFdSectId(String fdSectId) {
		this.fdSectId = fdSectId;
	}
	
	
}
