package com.yumu.hexie.integration.eshop.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-12-15 17:14
 */
public class RgroupOperatorMapper {
    private String name;
    @JsonProperty("parent_name")
    private String parentName;
    @JsonProperty("sect_id")
    private String sectId;

    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("product_name")
    private String productName;

    private String userid; //小区对应的团长ID
    @JsonProperty("org_oper_name")
    private String orgOperName; //对应的团长名称
    @JsonProperty("group_addr")
    private String groupAddr; //对应的团长地址

    public RgroupOperatorMapper(String name, String parentName, String sectId, String userid, String orgOperName, String groupAddr, String productId, String productName) {
        super();
        this.name = name;
        this.parentName = parentName;
        this.sectId = sectId;
        this.userid = userid;
        this.orgOperName = orgOperName;
        this.groupAddr = groupAddr;
        this.productId = productId;
        this.productName = productName;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOrgOperName() {
        return orgOperName;
    }

    public void setOrgOperName(String orgOperName) {
        this.orgOperName = orgOperName;
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "RgroupOperatorMapper{" +
                "name='" + name + '\'' +
                ", parentName='" + parentName + '\'' +
                ", sectId='" + sectId + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", userid='" + userid + '\'' +
                ", orgOperName='" + orgOperName + '\'' +
                ", groupAddr='" + groupAddr + '\'' +
                '}';
    }
}
