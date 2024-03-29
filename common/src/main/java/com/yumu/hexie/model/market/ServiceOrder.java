package com.yumu.hexie.model.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.CreateOrderReq;
import com.yumu.hexie.vo.SingleItemOrder;

//订单

@Entity
public class ServiceOrder  extends BaseModel {

	private static final long serialVersionUID = 4808669460780339640L;

	/** 商品相关 **/
	//主商品ID
	private int orderType;//0.拼单单 1.单个订单 2.预约单 3. 特卖单 4.团购单
	@JsonSerialize(using = ToStringSerializer.class)
	private long productId;
	private long groupRuleId;
	private long userId;
	//201501011221xxxx
	private String orderNo;
	private Float totalAmount;//总价 =折扣+支付金额+运费
	private Float discountAmount;
	private float price;//需要支付的金额（如果存在多个商品，则根据item计算，若只有单个商品，则订单内计算）
	/** 订单基础信息 */
	private int count;//个数
	private float shipFee;//运费
	private String seedStr;//订单对应的现金券

	private int status = ModelConstant.ORDER_STATUS_INIT;//0. 创建完成 1. 已支付 2. 已用户取消 3. 待退款 4. 已退款  5. 已使用/已发货 6.已签收 7. 已后台取消 8. 商户取消

	private int asyncStatus;//同步给商户 0 未同步，1已同步
	private int pingjiaStatus = ModelConstant.ORDER_PINGJIA_TYPE_N;//0 未评价 1 已评价

	//现金券
	private Long couponId;
	private Float couponAmount;

	/**用户信息**/
	private String openId;
	private String appid;

	private String miniopenid;
	private String miniappid;
	
	/**地址信息**/
	private long serviceAddressId;//FIXME 服务地址
	private int receiveTimeType;//周一至周五、周六周日、全周
	private String memo;
	@Column(length=1023)
	private String imgUrls;	//上传图片链接，服务内容链接

	private String address;
	private String tel;
	private String receiverName;
	private double lat;
	private double lng;
	private long xiaoquId;
	private String xiaoquName;

	/**操作员信息*/
	private long operatorId;
	private String operatorName;
	private long operatorUserId;
	private String operatorTel;
	private String operatorOpenId;

	private String confirmer;		//确认完工人

	/**团购状态*/
	private int groupStatus = ModelConstant.GROUP_STAUS_GROUPING;//拼单状态
	/**团购状态*/

	//团长信息
	private long groupLeaderId;	//团长id
	private String groupLeader;	//团长名字
	private String groupLeaderAddr;	//团长地址
	private String groupLeaderTel;	//团长电话

	//产品冗余信息
	private long merchantId;
	private String merchantName;
	private String productName;
	private String productPic;
	private String productThumbPic;
	private String groupRuleName;

	private long agentId;
	private String agentName;
	private String agentNo;

	private String gongzhonghao;

	/**物流信息**/
	private int logisticType;//0商户派送 1用户自提 2第三方配送
	private String logisticName;
	private String logisticNo;
	private String logisticCode;
	/**退货信息**/
	private String returnMemo;
	private int refundType;

	private long closeTime;//超时支付时间，超出则取消订单
	private long updateDate;
	private Date payDate;
	private Date refundDate;
	private Date returnDate;
	private Date acceptedDate;	//接单日期
	private Date confirmDate;	//确认完工日期
	private Date sendDate;
	private Date signDate;
	private Date cancelDate;
	private Date closeDate;
	private Date asyncDate;

	//评论相关
	private String comment;
	private int commentQuality;
	private int commentAttitude;
	private int commentService;
	@Column(length=1023)
	private String commentImgUrls;
	private Date commentDate;

	private long subType;	//子类，对于自定义服务列说，有子类
	private String subTypeName;	//子类中文名称

	private Long groupOrderId;	//拆单的情况下，这个作为支付订单关联的id

	private int groupNum; //团购号，以团购为单位，每个团购的顺序号
	private Float refundAmt;	//已退金额

	@JsonIgnore
	@OneToMany(targetEntity = OrderItem.class, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH}, mappedBy = "serviceOrder")
	@Fetch(FetchMode.SUBSELECT)
	private List<OrderItem> items = new ArrayList<OrderItem>();

	@Transient
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	public ServiceOrder(){}
	public ServiceOrder(SingleItemOrder sOrder) {
		if (!"2".equals(sOrder.getPayType())) {
			orderNo = OrderNoUtil.generateServiceOrderNo();
		}
		this.memo = sOrder.getMemo();
		this.count = sOrder.getCount();
		this.orderType = sOrder.getOrderType();
		this.receiveTimeType = sOrder.getReceiveTimeType();
		this.groupRuleId = sOrder.getRuleId();
		this.serviceAddressId = sOrder.getServiceAddressId();
		this.userId = sOrder.getUserId();
		this.openId = sOrder.getOpenId();
		this.couponId = sOrder.getCouponId();
		if (ModelConstant.ORDER_TYPE_PROMOTION == this.orderType) {
			this.agentId = sOrder.getAgentId();
		}
		OrderItem item = new OrderItem();
		item.setRuleId(sOrder.getRuleId());
		item.setCount(sOrder.getCount());
		items.add(item);
	}
	//维修单订单
	public ServiceOrder(RepairOrder sOrder,float amount) {
		orderNo = OrderNoUtil.generateServiceOrderNo();
		this.memo = sOrder.getMemo();
		this.count = 1;
		this.orderType = ModelConstant.ORDER_TYPE_REPAIR;
		this.receiveTimeType = 1;
		this.groupRuleId = 1;
		this.serviceAddressId = sOrder.getAddressId();
		this.userId = sOrder.getUserId();
		this.openId = sOrder.getOpenId();
		this.couponId = 0l;
		this.productName = sOrder.getProjectName();

		OrderItem item = new OrderItem();
		item.setRuleId(1l);
		item.setCount(1);
		item.setOrderType(ModelConstant.ORDER_TYPE_REPAIR);
		item.setPrice(amount);
		item.setAmount(amount);
		item.setRuleName("维修项目");

		item.setProductId(1l);
		item.setMerchantId(1l);
		item.setProductName("维修项目");
		item.setOriPrice(0f);
		items.add(item);
	}

	public ServiceOrder(User user, CreateOrderReq req, Cart cart) {
		if (!"2".equals(req.getPayType())) {
			orderNo = OrderNoUtil.generateServiceOrderNo();
		}
		this.memo = req.getMemo();
		this.receiveTimeType = req.getReceiveTimeType();
		this.serviceAddressId = req.getServiceAddressId();
		this.couponId = req.getCouponId();

		this.orderType = cart.getOrderType();

		this.userId = user.getId();
		this.openId = user.getOpenid();
		this.appid = user.getAppId();
		this.items = cart.getItems();
	}


	public ServiceOrder(User user, CreateOrderReq req) {

		if (!"2".equals(req.getPayType())) {
			orderNo = OrderNoUtil.generateServiceOrderNo();
		}
		this.memo = req.getMemo();
		this.receiveTimeType = req.getReceiveTimeType();
		this.serviceAddressId = req.getServiceAddressId();
		this.couponId = req.getCouponId();

		this.orderType = req.getOrderType();

		this.userId = user.getId();
		this.openId = user.getOpenid();
		this.appid = user.getAppId();
		this.miniopenid = user.getMiniopenid();
		this.miniappid = user.getMiniAppId();
		this.items = req.getItemList();
	}

	@JsonIgnore
	@Transient
	public long getCollocationId(){
		Long result = 0l;
		if(items != null && items.size() > 0) {
			for(OrderItem item : items) {
				if(result!= 0 && item.getCollocationId() != result) {
					return 0l;
				} else if(item != null&&item.getCollocationId()!=null){
					result = item.getCollocationId() ;
				}
			}
		}
		return result;
	}
	@Transient
	public String getCreateDateStr(){
		return DateUtil.dtFormat(new Date(getCreateDate()), "MM-dd HH:mm");
	}
	
	@Transient
	public String getCreateDateStrV3(){
		return DateUtil.dtFormat(new Date(getCreateDate()), "yyyy/MM/dd HH:mm");
	}
	
	private static Map<Integer,String> STATUSMAP = new HashMap<Integer,String>();
	static{
		STATUSMAP.put(ModelConstant.ORDER_STATUS_INIT,"待付款");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_PAYED,"已支付");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_CANCEL ,"已取消");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_REFUNDING ,"退款中");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_SENDED,"已发货");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_RECEIVED,"已签收");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_CANCEL_BACKEND,"已取消");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT,"已取消");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_CONFIRM ,"配货中");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_RETURNED,"已退货");
		STATUSMAP.put(ModelConstant.ORDER_STATUS_REFUNDED  ,"已退款");
	}

	private static Map<Integer,String> SERVICE_ORDER_STATUSMAP = new HashMap<Integer,String>();
	static{
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_INIT,"未接单");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_PAYED,"已支付");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_CANCEL ,"已取消");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_REFUNDING ,"退款中");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_SENDED,"已发货");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_RECEIVED,"已签收");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_CANCEL_BACKEND,"已取消");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT,"已取消");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_CONFIRM ,"已完工");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_RETURNED,"已退货");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_REFUNDED  ,"已退款");
		SERVICE_ORDER_STATUSMAP.put(ModelConstant.ORDER_STATUS_ACCEPTED  ,"已接单");
	}


	@Transient
	public String getStatusStr(){

		String statusStr = "";
		if (ModelConstant.ORDER_TYPE_SERVICE == this.orderType) {
			statusStr = SERVICE_ORDER_STATUSMAP.get(status);
		}else {
			statusStr = STATUSMAP.get(status);
		}
		return statusStr;

	}
	@Transient
	public static String getStatusStr(int status) {
		return STATUSMAP.get(status);
	}
	@Transient
	public void fillProductInfo(Product product){
		setProductId(product.getId());
		setMerchantId(product.getMerchantId());
		if(items != null) {
			setProductName(product.getName()); //改为规则的名字
//			if(items.size() > 1){
//				setProductName(getProductName()+"等"+items.size()+"种商品");
//			}
		}
		setProductPic(product.getMainPicture());
		setProductThumbPic(product.getSmallPicture());
	}
	@Transient
	public void fillAddressInfo(Address address) {

		String regionStr = StringUtils.isEmpty(address.getRegionStr())?"":address.getRegionStr();
		String detailAddress = StringUtils.isEmpty(address.getDetailAddress())?"":address.getDetailAddress();
		String addressStr = regionStr + detailAddress;
		if (detailAddress.contains(regionStr)) {
			addressStr = detailAddress;
		}
		setAddress(addressStr);
		if (ModelConstant.ORDER_TYPE_PROMOTION == orderType) {
			String addr = address.getProvince() + "," + address.getCity() + "," + address.getCounty() + "," + address.getXiaoquName();
			setAddress(addr);
		}
		setTel(address.getTel());
		setXiaoquId(address.getXiaoquId());
		setReceiverName(address.getReceiveName());
		setLat(address.getLatitude());
		setLng(address.getLongitude());
		setXiaoquName(address.getXiaoquName());

	}
	@Transient
	public void fillAgentInfo(Agent agent) {
		setAgentId(agent.getId());
		setAgentName(agent.getName());
		setAgentNo(agent.getAgentNo());
	}

	//FIXME
	/**
	 * 必须在价格已经正确设置以后
	 * @param coupon
	 */
	public void configCoupon(Coupon coupon) {
		if(coupon == null) {
			setCouponId(null);
			setCouponAmount(null);
			return;
		}
		setCouponId(coupon.getId());
		setCouponAmount(coupon.getAmount());
		BigDecimal price = BigDecimal.ZERO;
		if(coupon.getAmount() >= getPrice()) {
			price = new BigDecimal("0.01");
			setPrice(price.floatValue());
		} else {
			price = new BigDecimal(String.valueOf(getPrice())).subtract(new BigDecimal(String.valueOf(coupon.getAmount())));
			setPrice(price.floatValue());
		}
	}

	@Transient
	public void payed(){
		setStatus(ModelConstant.ORDER_STATUS_PAYED);
		setPayDate(new Date());
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void cancel(){
		setCancelDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_CANCEL);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void confirm() {
		setConfirmDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_CONFIRM);
		setGroupStatus(ModelConstant.GROUP_STAUS_FINISH);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void send(int logisticType,	String logisticName, String logisticNo){
		setSendDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_SENDED);
		setLogisticType(logisticType);
		setLogisticName(logisticName);
		setLogisticNo(logisticNo);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void sign(){
		setSignDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_RECEIVED);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void applyRefund(boolean userRequest) {
		setRefundType(userRequest?ModelConstant.REFUND_REASON_GROUP_USER_REFUND:ModelConstant.REFUND_REASON_GROUP_OWNER_REFUND);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void returnGood(String memo) {
		setReturnDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_RETURNED);
		setReturnMemo(memo);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void refunding(boolean groupCancel) {
		setRefundDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_REFUNDING);
		setRefundType(groupCancel ? ModelConstant.REFUND_REASON_GROUP_CANCEL:ModelConstant.REFUND_REASON_GROUP_BACKEND);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void refunding() {
		setRefundDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_REFUNDING);
		setUpdateDate(System.currentTimeMillis());
	}
	@Transient
	public void close() {
		setCloseDate(new Date());
		setStatus(ModelConstant.ORDER_STATUS_CANCEL);//FIXME 该处状态需要重新调整
		setUpdateDate(System.currentTimeMillis());
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getServiceAddressId() {
		return serviceAddressId;
	}

	public void setServiceAddressId(long serviceAddressId) {
		this.serviceAddressId = serviceAddressId;
	}
	public String getProductPic() {
		return productPic;
	}
	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}
	public String getProductThumbPic() {
		return productThumbPic;
	}
	public void setProductThumbPic(String productThumbPic) {
		this.productThumbPic = productThumbPic;
	}
	public long getGroupRuleId() {
		return groupRuleId;
	}
	public void setGroupRuleId(long groupRuleId) {
		this.groupRuleId = groupRuleId;
	}
	public String getGroupRuleName() {
		return groupRuleName;
	}
	public void setGroupRuleName(String groupRuleName) {
		this.groupRuleName = groupRuleName;
	}

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getOrderType() {
		return orderType;
	}
	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public int getLogisticType() {
		return logisticType;
	}


	public void setLogisticType(int logisticType) {
		this.logisticType = logisticType;
	}


	public String getLogisticName() {
		return logisticName;
	}


	public void setLogisticName(String logisticName) {
		this.logisticName = logisticName;
	}


	public String getLogisticNo() {
		return logisticNo;
	}


	public void setLogisticNo(String logisticNo) {
		this.logisticNo = logisticNo;
	}



	public long getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}


	public long getCloseTime() {
		return closeTime;
	}


	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}


	public int getReceiveTimeType() {
		return receiveTimeType;
	}


	public void setReceiveTimeType(int receiveTimeType) {
		this.receiveTimeType = receiveTimeType;
	}


	public String getOpenId() {
		return openId;
	}


	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public long getMerchantId() {
		return merchantId;
	}
	public long getGroupLeaderId() {
		return groupLeaderId;
	}
	public void setGroupLeaderId(long groupLeaderId) {
		this.groupLeaderId = groupLeaderId;
	}
	public String getGroupLeader() {
		return groupLeader;
	}
	public void setGroupLeader(String groupLeader) {
		this.groupLeader = groupLeader;
	}
	public String getGroupLeaderAddr() {
		return groupLeaderAddr;
	}
	public void setGroupLeaderAddr(String groupLeaderAddr) {
		this.groupLeaderAddr = groupLeaderAddr;
	}
	public String getGroupLeaderTel() {
		return groupLeaderTel;
	}
	public void setGroupLeaderTel(String groupLeaderTel) {
		this.groupLeaderTel = groupLeaderTel;
	}
	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}
	public int getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public Date getRefundDate() {
		return refundDate;
	}
	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public Date getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	public Date getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getReturnMemo() {
		return returnMemo;
	}
	public void setReturnMemo(String returnMemo) {
		this.returnMemo = returnMemo;
	}

	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public int getRefundType() {
		return refundType;
	}
	public void setRefundType(int refundType) {
		this.refundType = refundType;
	}
	public Date getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public int getAsyncStatus() {
		return asyncStatus;
	}

	public void setAsyncStatus(int asyncStatus) {
		this.asyncStatus = asyncStatus;
	}

	public Date getAsyncDate() {
		return asyncDate;
	}

	public void setAsyncDate(Date asyncDate) {
		this.asyncDate = asyncDate;
	}

	public int getPingjiaStatus() {
		return pingjiaStatus;
	}

	public void setPingjiaStatus(int pingjiaStatus) {
		this.pingjiaStatus = pingjiaStatus;
	}

	public long getXiaoquId() {
		return xiaoquId;
	}

	public void setXiaoquId(long xiaoquId) {
		this.xiaoquId = xiaoquId;
	}

	public String getGongzhonghao() {
		return gongzhonghao;
	}

	public void setGongzhonghao(String gongzhonghao) {
		this.gongzhonghao = gongzhonghao;
	}

	public float getShipFee() {
		return shipFee;
	}
	public void setShipFee(float shipFee) {
		this.shipFee = shipFee;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public Float getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(Float couponAmount) {
		this.couponAmount = couponAmount;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public long getOperatorUserId() {
		return operatorUserId;
	}
	public void setOperatorUserId(long operatorUserId) {
		this.operatorUserId = operatorUserId;
	}
	public String getOperatorTel() {
		return operatorTel;
	}
	public void setOperatorTel(String operatorTel) {
		this.operatorTel = operatorTel;
	}
	public String getOperatorOpenId() {
		return operatorOpenId;
	}
	public void setOperatorOpenId(String operatorOpenId) {
		this.operatorOpenId = operatorOpenId;
	}
	public String getConfirmer() {
		return confirmer;
	}
	public void setConfirmer(String confirmer) {
		this.confirmer = confirmer;
	}
	public Date getAcceptedDate() {
		return acceptedDate;
	}
	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}
	public String getXiaoquName() {
		return xiaoquName;
	}
	public void setXiaoquName(String xiaoquName) {
		this.xiaoquName = xiaoquName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getCommentQuality() {
		return commentQuality;
	}
	public void setCommentQuality(int commentQuality) {
		this.commentQuality = commentQuality;
	}
	public int getCommentAttitude() {
		return commentAttitude;
	}
	public void setCommentAttitude(int commentAttitude) {
		this.commentAttitude = commentAttitude;
	}
	public int getCommentService() {
		return commentService;
	}
	public void setCommentService(int commentService) {
		this.commentService = commentService;
	}
	public String getCommentImgUrls() {
		return commentImgUrls;
	}
	public void setCommentImgUrls(String commentImgUrls) {
		this.commentImgUrls = commentImgUrls;
	}
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}
	public long getSubType() {
		return subType;
	}
	public void setSubType(long subType) {
		this.subType = subType;
	}
	public String getSubTypeName() {
		return subTypeName;
	}
	public void setSubTypeName(String subTypeName) {
		this.subTypeName = subTypeName;
	}
	public String getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(String imgUrls) {
		this.imgUrls = imgUrls;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentNo() {
		return agentNo;
	}
	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public Long getGroupOrderId() {
		return groupOrderId;
	}
	public void setGroupOrderId(Long groupOrderId) {
		this.groupOrderId = groupOrderId;
	}
	@Transient
	@JsonIgnore
	public List<Long> getProductIds(){
		List<Long> ids = new ArrayList<Long>();
		if(items == null) {
			return ids;
		}
		for(OrderItem item : items) {
			ids.add(item.getProductId());
		}
		return ids;
	}

	@Transient
	@JsonIgnore
	public List<Long> getMerchantIds(){
		List<Long> ids = new ArrayList<Long>();
		if(items == null) {
			return ids;
		}
		for(OrderItem item : items) {
			ids.add(item.getMerchantId());
		}
		return ids;
	}
	public Float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Float totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Float getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(Float discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getSeedStr() {
		return seedStr;
	}
	public void setSeedStr(String seedStr) {
		this.seedStr = seedStr;
	}
	public String getLogisticCode() {
		return logisticCode;
	}
	public void setLogisticCode(String logisticCode) {
		this.logisticCode = logisticCode;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public boolean payable() {
		return ModelConstant.ORDER_STATUS_INIT==getStatus();
	}

	public boolean cancelable() {
		return ModelConstant.ORDER_STATUS_INIT==getStatus();
	}
	public boolean asyncable() {
		return ModelConstant.ORDER_STATUS_CONFIRM==getStatus();
	}
	public boolean sendable() {
		return ModelConstant.ORDER_STATUS_CONFIRM==getStatus();
	}
	public boolean signable() {
		return (ModelConstant.ORDER_STATUS_SENDED == getStatus()
				|| ModelConstant.ORDER_STATUS_CONFIRM == getStatus()
				|| ModelConstant.ORDER_STATUS_CONFIRM == getStatus());
	}
	public boolean returnable() {
		return ModelConstant.ORDER_STATUS_RECEIVED == getStatus();
	}
	public boolean refundable() {
		return (ModelConstant.ORDER_STATUS_CONFIRM == getStatus()
				|| ModelConstant.ORDER_STATUS_RETURNED == getStatus()
				| ModelConstant.ORDER_STATUS_PAYED == getStatus());
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	@Transient
	public String getShowStatus() {

		String showStatus = "";
		if (ModelConstant.ORDER_STATUS_INIT == this.status) {
			showStatus = "1";
		}else if (ModelConstant.ORDER_STATUS_ACCEPTED == this.status) {
			showStatus = "2";
		}else if (ModelConstant.ORDER_STATUS_CONFIRM == this.status) {
			if (StringUtils.isEmpty(this.payDate)) {
				showStatus = "3";
			}else if (ModelConstant.ORDER_PINGJIA_TYPE_N == this.pingjiaStatus) {
				showStatus = "4";
			}
		}else if (ModelConstant.ORDER_PINGJIA_TYPE_Y == this.pingjiaStatus) {
			showStatus = "5";
		}else if (ModelConstant.ORDER_STATUS_PAYED == this.status) {
			if (ModelConstant.ORDER_PINGJIA_TYPE_N == this.pingjiaStatus) {
				showStatus = "4";
			}
		}
		return showStatus;

	}
	public String getMiniopenid() {
		return miniopenid;
	}
	public void setMiniopenid(String miniopenid) {
		this.miniopenid = miniopenid;
	}
	public String getMiniappid() {
		return miniappid;
	}
	public void setMiniappid(String miniappid) {
		this.miniappid = miniappid;
	}
	public Float getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(Float refundAmt) {
		this.refundAmt = refundAmt;
	}

}
