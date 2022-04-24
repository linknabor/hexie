package com.yumu.hexie.integration.eshop.req;

import java.io.Serializable;
import java.util.List;

public class CreateRgroupRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8948073270591905841L;
	
	private Boolean createOwner = Boolean.FALSE;
	private Boolean createSect = Boolean.FALSE;
	private List<Sect> sects;
	private GroupOwnerInfo owner;
	
	public Boolean getCreateOwner() {
		return createOwner;
	}

	public void setCreateOwner(Boolean createOwner) {
		this.createOwner = createOwner;
	}

	public Boolean getCreateSect() {
		return createSect;
	}

	public void setCreateSect(Boolean createSect) {
		this.createSect = createSect;
	}

	public List<Sect> getSects() {
		return sects;
	}

	public void setSects(List<Sect> sects) {
		this.sects = sects;
	}

	public GroupOwnerInfo getOwner() {
		return owner;
	}

	public void setOwner(GroupOwnerInfo owner) {
		this.owner = owner;
	}

	public static class GroupOwnerInfo {
		
		private String name;
		private String tel;
		private String openid;
		private String miniopenid;
		private String orgOperId;
		private String orgType;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getMiniopenid() {
			return miniopenid;
		}
		public void setMiniopenid(String miniopenid) {
			this.miniopenid = miniopenid;
		}
		public String getOrgOperId() {
			return orgOperId;
		}
		public void setOrgOperId(String orgOperId) {
			this.orgOperId = orgOperId;
		}
		public String getOrgType() {
			return orgType;
		}
		public void setOrgType(String orgType) {
			this.orgType = orgType;
		}
		@Override
		public String toString() {
			return "GroupOwnerInfo [name=" + name + ", tel=" + tel + ", openid=" + openid + ", miniopenid=" + miniopenid
					+ ", orgOperId=" + orgOperId + ", orgType=" + orgType + "]";
		}
		
	}
	
	public static class Sect {
		
		private long regionId;
		private String province;
		private String city;
		private String district;
		private String sectName;
		private String sectAddr;
		private String sectId;
		private String cspId;
		
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getDistrict() {
			return district;
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public String getSectName() {
			return sectName;
		}
		public void setSectName(String sectName) {
			this.sectName = sectName;
		}
		public String getSectAddr() {
			return sectAddr;
		}
		public void setSectAddr(String sectAddr) {
			this.sectAddr = sectAddr;
		}
		public long getRegionId() {
			return regionId;
		}
		public void setRegionId(long regionId) {
			this.regionId = regionId;
		}
		public String getSectId() {
			return sectId;
		}
		public void setSectId(String sectId) {
			this.sectId = sectId;
		}
		public String getCspId() {
			return cspId;
		}
		public void setCspId(String cspId) {
			this.cspId = cspId;
		}
		@Override
		public String toString() {
			return "Sect [regionId=" + regionId + ", province=" + province + ", city=" + city + ", district=" + district
					+ ", sectName=" + sectName + ", sectAddr=" + sectAddr + ", sectId=" + sectId + ", cspId=" + cspId
					+ "]";
		}
		
	}

	@Override
	public String toString() {
		return "CreateRgroupRequest [createOwner=" + createOwner + ", createSect=" + createSect + ", sects=" + sects
				+ ", owner=" + owner + "]";
	}
	

}
