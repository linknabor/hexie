package com.yumu.hexie.integration.community.resp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 描述:
 * 团购购买商品的汇总信息
 * @author jackie
 * @create 2022-04-24 16:54
 */
public class GroupProductSumVo implements Serializable {
    private BigInteger productId; //商品ID
    private String productName; //商品名称
    private BigDecimal count; //商品数量
    private BigDecimal verifyNum; //未核销数

    public GroupProductSumVo(BigInteger productId, String productName, BigDecimal count, BigDecimal verifyNum) {
        this.productId = productId;
        this.productName = productName;
        this.count = count;
        this.verifyNum = verifyNum;
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

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getVerifyNum() {
        return verifyNum;
    }

    public void setVerifyNum(BigDecimal verifyNum) {
        this.verifyNum = verifyNum;
    }

    @Override
    public String toString() {
        return "GroupProductSumVo{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", count=" + count +
                ", verifyNum=" + verifyNum +
                '}';
    }
}
