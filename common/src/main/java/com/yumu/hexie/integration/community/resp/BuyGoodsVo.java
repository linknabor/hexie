package com.yumu.hexie.integration.community.resp;

import java.math.BigDecimal;

/**
 * 描述:
 * 购买商品列表
 * @author jackie
 * @create 2022-04-19 15:42
 */
public class BuyGoodsVo {
    private Long goodsId;
    private String goodsName;
    private int goodsNum;
    private Float goodsAmt;
    private String goodsImage;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

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

    public Float getGoodsAmt() {
        return goodsAmt;
    }

    public void setGoodsAmt(Float goodsAmt) {
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
                "goodsId='" + goodsId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", goodsNum=" + goodsNum +
                ", goodsAmt=" + goodsAmt +
                ", goodsImage='" + goodsImage + '\'' +
                '}';
    }
}
