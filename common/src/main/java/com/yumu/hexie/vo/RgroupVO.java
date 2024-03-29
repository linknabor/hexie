package com.yumu.hexie.vo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Transient;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;

public class RgroupVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1464110493231846128L;
	
	private String action;	//如果是copy代表复制开团，默认为空
	private String ruleId;	//团购id，编辑保存时有此项
	private String type;	//创建类型，0保存预览，1保存发布，2发布状态中修改保存
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
	private String updateDate;	//更新日期
	private int accessed = 0;	//被访问次数
	private int ordered = 0;	//被下单次数
	private ProductVO[]productList;
	private Region region;	//支持旧版本 TODO
	private RegionVo[] regions;	//团购地区
	private RgroupOwnerVO rgroupOwner;
	private List<String> descMoreImages;
	private String pricePeriod;	//价格区间
	private RgroupRecordsVO rgroupRecords;	//跟团记录
	
	public static class RegionVo {
		
		private long id;
		private String name;
		private String xiaoquAddress;
		private String sectId;
		private String miniNum;
		private String currentNum;
		private Double longitude = 0.00d;
		private Double latitude = 0.00d;
		private String remark;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getXiaoquAddress() {
			return xiaoquAddress;
		}
		public void setXiaoquAddress(String xiaoquAddress) {
			this.xiaoquAddress = xiaoquAddress;
		}
		public String getMiniNum() {
			return miniNum;
		}
		public void setMiniNum(String miniNum) {
			this.miniNum = miniNum;
		}
		public String getCurrentNum() {
			return currentNum;
		}
		public void setCurrentNum(String currentNum) {
			this.currentNum = currentNum;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public String getSectId() {
			return sectId;
		}
		public void setSectId(String sectId) {
			this.sectId = sectId;
		}
		public Integer getProcess() {
			Integer minNum = 0;
			if (!StringUtils.isEmpty(miniNum)) {
				minNum = Integer.parseInt(miniNum);
			}
			if(minNum<=0) {
	    		return 0;
	    	}
			Integer currNum = 0;
			if (!StringUtils.isEmpty(currNum)) {
				currNum = Integer.parseInt(currentNum);
			}
	    	return (100*currNum/minNum);
		}
		public Integer getLeftNum () {
			
			Integer minNum = 0;
			if (!StringUtils.isEmpty(miniNum)) {
				minNum = Integer.parseInt(miniNum);
			}
			if(minNum<=0) {
	    		return 0;
	    	}
			Integer currNum = 0;
			if (!StringUtils.isEmpty(currNum)) {
				currNum = Integer.parseInt(currentNum);
			}
			return (minNum-currNum < 0)?0:(minNum-currNum);
			
		}
		@Override
		public String toString() {
			return "RegionVo [id=" + id + ", name=" + name + ", xiaoquAddress=" + xiaoquAddress + ", sectId=" + sectId
					+ ", miniNum=" + miniNum + ", currentNum=" + currentNum + ", longitude=" + longitude + ", latitude="
					+ latitude + ", remark=" + remark + "]";
		}
		
	}
	
	public static class RgroupOwnerVO {

		private long ownerId;		//这个ownerid是user表的id
		private String ownerName;
		private String ownerAddr;
		private String ownerImg;
		private String ownerTel;
		
		/*下面这些展示用*/
		private int followers;	//TODO 关注人数  这个不知道有什么用？
		private int members = 0;	//成员数，即访问数
		private int attendees = 0;		//跟团人次
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
		
		public String getAttendeesView() {
			if (attendees > 1000) {
				attendees = attendees/1000*1000;
			}
			String attendeesView = String.valueOf(attendees);
			if (attendees >= 1000) {
				attendeesView += "+";
			}
			return attendeesView;
		}
		public String getMembersView() {
			if (members > 1000) {
				members = members/1000*1000;
			}
			String membersView = String.valueOf(members);
			if (members >= 1000) {
				membersView += "+";
			}
			return membersView;
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
		private String depotId;	//商品库id
		private String specs;	//商品规格
		
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
		public String getDepotId() {
			return depotId;
		}
		public void setDepotId(String depotId) {
			this.depotId = depotId;
		}
		public String getSpecs() {
			return specs;
		}
		public void setSpecs(String specs) {
			this.specs = specs;
		}
		@Override
		public String toString() {
			return "ProductVO [id=" + id + ", name=" + name + ", singlePrice=" + singlePrice + ", miniPrice="
					+ miniPrice + ", oriPrice=" + oriPrice + ", totalCount=" + totalCount + ", userLimitCount="
					+ userLimitCount + ", description=" + description + ", images=" + Arrays.toString(images)
					+ ", tags=" + Arrays.toString(tags) + ", imageList=" + Arrays.toString(imageList) + ", cartNum="
					+ cartNum + ", saledNum=" + saledNum + ", status=" + status + ", depotId=" + depotId + ", specs="
					+ specs + "]";
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
	public RegionVo[] getRegions() {
		return regions;
	}

	public void setRegions(RegionVo[] regions) {
		this.regions = regions;
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
	
	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	
	@Transient
	public long getLeftSeconds(){
		if(endDateMills == 0){
			return 3600*24*7;
		}
		return (endDateMills - System.currentTimeMillis())/1000;
	}
	
	@Transient
	public String getStatusCn() {
		
		String statusCn = "";
		long currentMills = System.currentTimeMillis();
		if (ModelConstant.RULE_STATUS_ON == status) {
    		if(startDateMills <= currentMills && endDateMills >= currentMills) {
    			statusCn = "跟团中";
    		}
			if (startDateMills > currentMills) {
				statusCn = "未开始";
			}
		} else if(getStatus() == ModelConstant.RULE_STATUS_OFF) {
			if(startDateMills <= currentMills && endDateMills >= currentMills) {
				statusCn = "预览中";
			}
		}
    	if (endDateMills < currentMills) {
    		statusCn = "已结束";
		}
		if (ModelConstant.RULE_STATUS_END == status) {
			statusCn = "已结束";
		}
		if (ModelConstant.RULE_STATUS_DEL == status) {
			statusCn = "已删除";
		}
		
		return statusCn;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPricePeriod() {
		return pricePeriod;
	}

	public void setPricePeriod(String pricePeriod) {
		this.pricePeriod = pricePeriod;
	}

	public int getAccessed() {
		return accessed;
	}

	public void setAccessed(int accessed) {
		this.accessed = accessed;
	}

	public int getOrdered() {
		return ordered;
	}

	public void setOrdered(int ordered) {
		this.ordered = ordered;
	}

	public RgroupRecordsVO getRgroupRecords() {
		return rgroupRecords;
	}

	public void setRgroupRecords(RgroupRecordsVO rgroupRecords) {
		this.rgroupRecords = rgroupRecords;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return "RgroupVO [action=" + action + ", ruleId=" + ruleId + ", type=" + type + ", status=" + status
				+ ", createDate=" + createDate + ", description=" + description + ", descriptionMore="
				+ Arrays.toString(descriptionMore) + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", startDateMills=" + startDateMills + ", endDateMills=" + endDateMills + ", logisticType="
				+ logisticType + ", groupMinNum=" + groupMinNum + ", updateDate=" + updateDate + ", accessed="
				+ accessed + ", ordered=" + ordered + ", productList=" + Arrays.toString(productList) + ", region="
				+ region + ", regions=" + Arrays.toString(regions) + ", rgroupOwner=" + rgroupOwner
				+ ", descMoreImages=" + descMoreImages + ", pricePeriod=" + pricePeriod + ", rgroupRecords="
				+ rgroupRecords + "]";
	}

	
}
