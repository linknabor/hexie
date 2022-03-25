package com.yumu.hexie.integration.eshop.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-12-15 17:14
 */
public class RgroupOperatorMapper {
    @JsonProperty("region_id")
    private BigInteger regionId;
    private String name;
    @JsonProperty("parent_name")
    private String parentName;
    @JsonProperty("sect_id")
    private String sectId;

    @JsonProperty("product_id")
    private BigInteger productId;
    @JsonProperty("product_name")
    private String productName;

    private BigInteger userid; //小区对应的团长ID
    @JsonProperty("oper_name")
    private String operName; //对应的团长名称
    @JsonProperty("oper_tel")
    private String operTel;	//对应团长电话
    @JsonProperty("oper_openid")
    private String operOpenid;	//团长openid
    
    @JsonProperty("group_addr")
    private String groupAddr; //对应的团长地址

    public RgroupOperatorMapper(BigInteger regionId, String name, String parentName, String sectId, BigInteger productId, 
    		String productName, BigInteger userid, String operName, String operTel, String operOpenid, String groupAddr) {
        super();
        this.regionId = regionId;
        this.name = name;
        this.parentName = parentName;
        this.sectId = sectId;
        this.productId = productId;
        this.productName = productName;
        this.userid = userid;
        this.operName = operName;
        this.operTel = operTel;
        this.operOpenid = operOpenid;
        this.groupAddr = groupAddr;
    }

    public BigInteger getRegionId() {
        return regionId;
    }

    public void setRegionId(BigInteger regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getParentName() {
        return parentName;
    }
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
    public String getSectId() {
        return sectId;
    }
    public void setSectId(String sectId) {
        this.sectId = sectId;
    }

    public BigInteger getUserid() {
        return userid;
    }

    public void setUserid(BigInteger userid) {
        this.userid = userid;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    public BigInteger getProductId() {
        return productId;
    }

    public void setProductId(BigInteger productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

	public String getOperTel() {
		return operTel;
	}

	public void setOperTel(String operTel) {
		this.operTel = operTel;
	}

	public String getOperOpenid() {
		return operOpenid;
	}

	public void setOperOpenid(String operOpenid) {
		this.operOpenid = operOpenid;
	}

	@Override
	public String toString() {
		return "RgroupOperatorMapper [regionId=" + regionId + ", name=" + name + ", parentName=" + parentName
				+ ", sectId=" + sectId + ", productId=" + productId + ", productName=" + productName + ", userid="
				+ userid + ", operName=" + operName + ", operTel=" + operTel + ", operOpenid=" + operOpenid
				+ ", groupAddr=" + groupAddr + "]";
	}
    
    
}
