package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.distribution.region.Region;

public class CreateRgroupReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1464110493231846128L;
	
	private String type;	//创建类型，0保存预览，1保存发布
	private String description;	//团购title，
	private DescriptionMore[]descriptionMore;	//团购内容
	private String startDate;	//团购起始日期,页面的格式是yyyy/MM/dd HH:mm
	private String endDate;		//团购结束日期,同上
	private int logisticType = 1;//0商户派送 1用户自提 2第三方配送
	private int groupMinNum;	//最小成团份数
	private Product[]productList;
	private Region region;	//团购地区
	private RgroupOwner rgroupOwner;
	
	public static class RgroupOwner {

		private long ownerId;
		private String ownerName;
		private String ownerAddr;
		private String ownerImg;
		private String ownerTel;
		
		public long getOwnerId() {
			return ownerId;
		}
		public void setOwnerId(long ownerId) {
			this.ownerId = ownerId;
		}
		public String getOwnerName() {
			return ownerName;
		}
		public void setOwnerName(String ownerName) {
			this.ownerName = ownerName;
		}
		public String getOwnerAddr() {
			return ownerAddr;
		}
		public void setOwnerAddr(String ownerAddr) {
			this.ownerAddr = ownerAddr;
		}
		public String getOwnerImg() {
			return ownerImg;
		}
		public void setOwnerImg(String ownerImg) {
			this.ownerImg = ownerImg;
		}
		public String getOwnerTel() {
			return ownerTel;
		}
		public void setOwnerTel(String ownerTel) {
			this.ownerTel = ownerTel;
		}
		@Override
		public String toString() {
			return "RgroupOwner [ownerId=" + ownerId + ", ownerName=" + ownerName + ", ownerAddr=" + ownerAddr
					+ ", ownerImg=" + ownerImg + ", ownerTel=" + ownerTel + "]";
		}
		
	}
	
	public static class DescriptionMore {
		
		private String type;	//0文字，1小图，2大图
		private String text;	//type==0时，有此项
		private String image;	//type==2时，有此项,图片链接
		private Thumbnail[]thumbnail;
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public Thumbnail[] getThumbnail() {
			return thumbnail;
		}
		public void setThumbnail(Thumbnail[] thumbnail) {
			this.thumbnail = thumbnail;
		}
		@Override
		public String toString() {
			return "DescriptionMore [type=" + type + ", text=" + text + ", image=" + image + ", thumbnail="
					+ Arrays.toString(thumbnail) + "]";
		}
		
	}
	
	public static class Tag {
		
		private String name;
		private String color;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		@Override
		public String toString() {
			return "Tag [name=" + name + ", color=" + color + "]";
		}
		
	}
	
	public static class Thumbnail {
	
		private String url;	//缩略图链接

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public String toString() {
			return "Thumbnail [url=" + url + "]";
		}
		
	}
	
	public static class Product {
		
		private String name;
		@JsonProperty("price")
		private String singlePrice;	//售卖价
		private String miniPrice;	//成本价
		private String oriPrice;	//划线价
		@JsonProperty("stock")
		private String totalCount;	//库存
		@JsonProperty("limit")
		private String userLimitCount;	
		private String description;
		private Thumbnail[]images;
		private Tag[]tags;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Thumbnail[] getImages() {
			return images;
		}
		public void setImages(Thumbnail[] images) {
			this.images = images;
		}
		public String getSinglePrice() {
			return singlePrice;
		}
		public void setSinglePrice(String singlePrice) {
			this.singlePrice = singlePrice;
		}
		public String getMiniPrice() {
			return miniPrice;
		}
		public void setMiniPrice(String miniPrice) {
			this.miniPrice = miniPrice;
		}
		public String getOriPrice() {
			return oriPrice;
		}
		public void setOriPrice(String oriPrice) {
			this.oriPrice = oriPrice;
		}
		public String getTotalCount() {
			return totalCount;
		}
		public void setTotalCount(String totalCount) {
			this.totalCount = totalCount;
		}
		public String getUserLimitCount() {
			return userLimitCount;
		}
		public void setUserLimitCount(String userLimitCount) {
			this.userLimitCount = userLimitCount;
		}
		public Tag[] getTags() {
			return tags;
		}
		public void setTags(Tag[] tags) {
			this.tags = tags;
		}
		@Override
		public String toString() {
			return "Product [name=" + name + ", singlePrice=" + singlePrice + ", miniPrice=" + miniPrice + ", oriPrice="
					+ oriPrice + ", totalCount=" + totalCount + ", userLimitCount=" + userLimitCount + ", description="
					+ description + ", images=" + Arrays.toString(images) + ", tags=" + Arrays.toString(tags) + "]";
		}
		
		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DescriptionMore[] getDescriptionMore() {
		return descriptionMore;
	}

	public void setDescriptionMore(DescriptionMore[] descriptionMore) {
		this.descriptionMore = descriptionMore;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getLogisticType() {
		return logisticType;
	}

	public void setLogisticType(int logisticType) {
		this.logisticType = logisticType;
	}

	public int getGroupMinNum() {
		return groupMinNum;
	}

	public void setGroupMinNum(int groupMinNum) {
		this.groupMinNum = groupMinNum;
	}

	public Product[] getProductList() {
		return productList;
	}

	public void setProductList(Product[] productList) {
		this.productList = productList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public RgroupOwner getRgroupOwner() {
		return rgroupOwner;
	}

	public void setRgroupOwner(RgroupOwner rgroupOwner) {
		this.rgroupOwner = rgroupOwner;
	}

	@Override
	public String toString() {
		return "CreateRgroupReq [type=" + type + ", description=" + description + ", descriptionMore="
				+ Arrays.toString(descriptionMore) + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", logisticType=" + logisticType + ", groupMinNum=" + groupMinNum + ", productList="
				+ Arrays.toString(productList) + ", region=" + region + ", rgroupOwner=" + rgroupOwner + "]";
	}

}
