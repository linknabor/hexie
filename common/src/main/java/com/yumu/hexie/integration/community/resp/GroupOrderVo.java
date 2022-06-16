package com.yumu.hexie.integration.community.resp;

import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.RefundRecord;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

/**
 * 描述:
 * 团购订单对象
 * @author jackie
 * @create 2022-04-19 15:40
 */
public class GroupOrderVo {
    private Integer groupNum; //团购号
    private BigInteger id; //订单ID
    private String orderNo; //订单编号
    private Integer status; //订单状态
    private Timestamp payDate; //支付日期
    private BigInteger createDate; //创建日期
    private Integer count; //购买件数
    private Float price; //订单金额
    private String receiverName; //收货人
    private String tel; //收货人电话
    private String address; //收货地址
    private Integer logisticType; //物流类型
    private String memo; //团购备注
    private BigInteger userId; //用户ID
    private Integer refundType;	//退款类型
    
    private String statusCn;
    private String userName; //用户名称
    private String userHead; //用户头像
    private String logisticTypeCn;
    private String orderDate;
    
    private List<OrderItem> orderItems; //商品列表
    private RefundRecord latestRefund;	//退款处理
    private List<RefundRecord> refundRecords;

    public GroupOrderVo() {

    }

    public GroupOrderVo(Integer groupNum, BigInteger id, String orderNo, Integer status, Timestamp payDate, BigInteger createDate, Integer count, Float price, 
    		String receiverName, String tel, String address, Integer logisticType, String memo, BigInteger userId, Integer refundType) {
        this.groupNum = groupNum;
        this.id = id;
        this.orderNo = orderNo;
        this.status = status;
        this.payDate = payDate;
        this.createDate = createDate;
        this.count = count;
        this.price = price;
        this.receiverName = receiverName;
        this.tel = tel;
        this.address = address;
        this.logisticType = logisticType;
        this.memo = memo;
        this.userId = userId;
        this.refundType = refundType;
    }

    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getPayDate() {
        return payDate;
    }

    public void setPayDate(Timestamp payDate) {
        this.payDate = payDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLogisticType() {
        return logisticType;
    }

    public void setLogisticType(Integer logisticType) {
        this.logisticType = logisticType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getStatusCn() {
        return statusCn;
    }

    public void setStatusCn(String statusCn) {
        this.statusCn = statusCn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getLogisticTypeCn() {
        return logisticTypeCn;
    }

    public void setLogisticTypeCn(String logisticTypeCn) {
        this.logisticTypeCn = logisticTypeCn;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}

	public BigInteger getCreateDate() {
		return createDate;
	}

	public void setCreateDate(BigInteger createDate) {
		this.createDate = createDate;
	}

	public RefundRecord getLatestRefund() {
		return latestRefund;
	}

	public void setLatestRefund(RefundRecord latestRefund) {
		this.latestRefund = latestRefund;
	}

	public List<RefundRecord> getRefundRecords() {
		return refundRecords;
	}

	public void setRefundRecords(List<RefundRecord> refundRecords) {
		this.refundRecords = refundRecords;
	}

	@Override
	public String toString() {
		return "GroupOrderVo [groupNum=" + groupNum + ", id=" + id + ", orderNo=" + orderNo + ", status=" + status
				+ ", payDate=" + payDate + ", createDate=" + createDate + ", count=" + count + ", price=" + price
				+ ", receiverName=" + receiverName + ", tel=" + tel + ", address=" + address + ", logisticType="
				+ logisticType + ", memo=" + memo + ", userId=" + userId + ", refundType=" + refundType + ", statusCn="
				+ statusCn + ", userName=" + userName + ", userHead=" + userHead + ", logisticTypeCn=" + logisticTypeCn
				+ ", orderDate=" + orderDate + ", orderItems=" + orderItems + ", latestRefund=" + latestRefund
				+ ", refundRecords=" + refundRecords + "]";
	}

	
    
}
