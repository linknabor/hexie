package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.distribution.region.Region;

public class RgroupVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1464110493231846128L;
	
	private String ruleId;	//团购id，编辑保存时有此项
	private String type;	//创建类型，0保存预览，1保存发布
	private int status;	//团购状态
	private String createDate;	//团购创建事件
	private String description;	//团购title，
	private DescriptionMore[]descriptionMore;	//团购内容
	private String startDate;	//团购起始日期,页面的格式是yyyy/MM/dd HH:mm
	private String endDate;		//团购结束日期,同上
	private long startDateMills;	//前端展示用
	private long endDateMills;	//前端展示用
	private int logisticType = 1;//0商户派送 1用户自提 2第三方配送
	private int groupMinNum;	//最小成团份数
	private ProductVO[]productList;
	private Region region;	//团购地区
	private RgroupOwnerVO rgroupOwner;
	
	private List<String> descMoreImages;
	
	public static class RgroupOwnerVO {

		private long ownerId;		//这个ownerid是user表的id
		private String ownerName;
		private String ownerAddr;
		private String ownerImg;
		private String ownerTel;
		
		/*下面这些展示用*/
		private int followers;	//TODO 关注人数  这个不知道有什么用？
		private int members;	//订阅人数,成员数
		private int attendees;		//跟团人次
		private String consultRate;
		
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
		public int getFollowers() {
			return followers;
		}
		public void setFollowers(int followers) {
			this.followers = followers;
		}
		public int getMembers() {
			return members;
		}
		public void setMembers(int members) {
			this.members = members;
		}
		public int getAttendees() {
			return attendees;
		}
		public void setAttendees(int attendees) {
			this.attendees = attendees;
		}
		public String getConsultRate() {
			return consultRate;
		}
		public void setConsultRate(String consultRate) {
			this.consultRate = consultRate;
		}
		@Override
		public String toString() {
			return "RgroupOwnerVO [ownerId=" + ownerId + ", ownerName=" + ownerName + ", ownerAddr=" + ownerAddr
					+ ", ownerImg=" + ownerImg + ", ownerTel=" + ownerTel + ", followers=" + followers + ", members="
					+ members + ", attendees=" + attendees + ", consultRate=" + consultRate + "]";
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
	
	public static class ProductVO {
		
		private String id;	//产品ID，如果是导入商品，由此项
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
		private String[]imageList;
		private int cartNum = 0;	//在购物车里的数量
		private int saledNum = 0;	//已团数量
		private int status;	//商品状态,1上架，2下架
		
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
		public String[] getImageList() {
			return imageList;
		}
		public void setImageList(String[] imageList) {
			this.imageList = imageList;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public int getCartNum() {
			return cartNum;
		}
		public void setCartNum(int cartNum) {
			this.cartNum = cartNum;
		}
		public int getSaledNum() {
			return saledNum;
		}
		public void setSaledNum(int saledNum) {
			this.saledNum = saledNum;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		@Override
		public String toString() {
			return "ProductVO [id=" + id + ", name=" + name + ", singlePrice=" + singlePrice + ", miniPrice="
					+ miniPrice + ", oriPrice=" + oriPrice + ", totalCount=" + totalCount + ", userLimitCount="
					+ userLimitCount + ", description=" + description + ", images=" + Arrays.toString(images)
					+ ", tags=" + Arrays.toString(tags) + ", imageList=" + Arrays.toString(imageList) + ", cartNum="
					+ cartNum + ", saledNum=" + saledNum + ", status=" + status + "]";
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

	public ProductVO[] getProductList() {
		return productList;
	}

	public void setProductList(ProductVO[] productList) {
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

	public RgroupOwnerVO getRgroupOwner() {
		return rgroupOwner;
	}

	public void setRgroupOwner(RgroupOwnerVO rgroupOwner) {
		this.rgroupOwner = rgroupOwner;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public List<String> getDescMoreImages() {
		return descMoreImages;	
	}

	public void setDescMoreImages(List<String> descMoreImages) {
		this.descMoreImages = descMoreImages;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public long getStartDateMills() {
		return startDateMills;
	}

	public void setStartDateMills(long startDateMills) {
		this.startDateMills = startDateMills;
	}

	public long getEndDateMills() {
		return endDateMills;
	}

	public void setEndDateMills(long endDateMills) {
		this.endDateMills = endDateMills;
	}

	@Transient
	public long getLeftSeconds(){
		if(endDateMills == 0){
			return 3600*24*7;
		}
		return (endDateMills - System.currentTimeMillis())/1000;
	}
	
	@Override
	public String toString() {
		return "RgroupVO [ruleId=" + ruleId + ", type=" + type + ", status=" + status + ", createDate=" + createDate
				+ ", description=" + description + ", descriptionMore=" + Arrays.toString(descriptionMore)
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", startDateMills=" + startDateMills
				+ ", endDateMills=" + endDateMills + ", logisticType=" + logisticType + ", groupMinNum=" + groupMinNum
				+ ", productList=" + Arrays.toString(productList) + ", region=" + region + ", rgroupOwner="
				+ rgroupOwner + ", descMoreImages=" + descMoreImages + "]";
	}

	
}
