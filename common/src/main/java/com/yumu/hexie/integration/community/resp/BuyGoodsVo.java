package com.yumu.hexie.integration.community.resp;

import java.math.BigDecimal;

/**
 * 描述:
 * 购买商品列表
 * @author jackie
 * @create 2022-04-19 15:42
 */
public class BuyGoodsVo {
    private String goodsName;
    private int goodsNum;
    private BigDecimal goodsAmt;
    private String goodsImage;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(int goodsNum) {
        this.goodsNum = goodsNum;
    }

    public BigDecimal getGoodsAmt() {
        return goodsAmt;
    }

    public void setGoodsAmt(BigDecimal goodsAmt) {
        this.goodsAmt = goodsAmt;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    @Override
    public String toString() {
        return "BuyGoodsVo{" +
                "goodsName='" + goodsName + '\'' +
                ", goodsNum=" + goodsNum +
                ", goodsAmt=" + goodsAmt +
                ", goodsImage='" + goodsImage + '\'' +
                '}';
    }
}
