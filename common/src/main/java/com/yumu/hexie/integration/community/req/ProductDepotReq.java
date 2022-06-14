package com.yumu.hexie.integration.community.req;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-04-25 17:08
 */
public class ProductDepotReq {
    private String productId; //商品ID
    private String name; //商品名称
    private String serviceDesc; //描述
    private String pictures; //图片
    private String totalCount; //库存
    private float miniPrice;//成本价
    private float oriPrice;//划线价
    private float singlePrice;//售卖价
    private String tags; //标签
    private String specs;	//规格

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public float getMiniPrice() {
        return miniPrice;
    }

    public void setMiniPrice(float miniPrice) {
        this.miniPrice = miniPrice;
    }

    public float getOriPrice() {
        return oriPrice;
    }

    public void setOriPrice(float oriPrice) {
        this.oriPrice = oriPrice;
    }

    public float getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(float singlePrice) {
        this.singlePrice = singlePrice;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

	public String getSpecs() {
		return specs;
	}

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	@Override
	public String toString() {
		return "ProductDepotReq [productId=" + productId + ", name=" + name + ", serviceDesc=" + serviceDesc
				+ ", pictures=" + pictures + ", totalCount=" + totalCount + ", miniPrice=" + miniPrice + ", oriPrice="
				+ oriPrice + ", singlePrice=" + singlePrice + ", tags=" + tags + ", specs=" + specs + "]";
	}
    
    
}
