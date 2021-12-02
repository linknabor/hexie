package com.yumu.hexie.integration.eshop.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.ServiceOrder;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-12-01 16:14
 */
public class OrderDetailResp {

    private OrderResp order;
    private List<OrderSubResp> details;

    public static class OrderResp {
        private long id; //订单ID
        private String address; //送货地址
        private int count; //购买数量
        private String logisticNo; //运单号
        private String logisticName; //快速公司
        private String logisticType; //物流类型
        private String agentName; //服务商名称
        private String productName; //商品名称
        private int status; //订单状态
        private String statusCn; //订单状态中文
        private String tel; //手机号
        private String xiaoquName; //小区名称
        private Long createDate; //订单时间
        private Date payDate; //支付时间
        private Date refundDate;	//退款时间
        private Date sendDate;	//发货时间
        private Float totalAmount; //金额
        private String receiverName; //购买人
        private float shipFee; //快递费
        private Float couponAmount; //优惠金额
        private String memo; //用户下单时的描述信息
        private String imgUrls; //用户上传的图片地址

        public OrderResp(ServiceOrder order) {
            BeanUtils.copyProperties(order, this);
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getLogisticNo() {
            return logisticNo;
        }

        public void setLogisticNo(String logisticNo) {
            this.logisticNo = logisticNo;
        }

        public String getLogisticName() {
            return logisticName;
        }

        public void setLogisticName(String logisticName) {
            this.logisticName = logisticName;
        }

        public String getLogisticType() {
            return logisticType;
        }

        public void setLogisticType(String logisticType) {
            this.logisticType = logisticType;
        }

        public String getAgentName() {
            return agentName;
        }

        public void setAgentName(String agentName) {
            this.agentName = agentName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusCn() {
            return statusCn;
        }

        public void setStatusCn(String statusCn) {
            this.statusCn = statusCn;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getXiaoquName() {
            return xiaoquName;
        }

        public void setXiaoquName(String xiaoquName) {
            this.xiaoquName = xiaoquName;
        }

        public Long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Long createDate) {
            this.createDate = createDate;
        }

        public Float getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Float totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public float getShipFee() {
            return shipFee;
        }

        public void setShipFee(float shipFee) {
            this.shipFee = shipFee;
        }

        public Float getCouponAmount() {
            return couponAmount;
        }

        public void setCouponAmount(Float couponAmount) {
            this.couponAmount = couponAmount;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getImgUrls() {
            return imgUrls;
        }

        public void setImgUrls(String imgUrls) {
            this.imgUrls = imgUrls;
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

        @Override
        public String toString() {
            return "OrderResp{" +
                    "id=" + id +
                    ", address='" + address + '\'' +
                    ", count=" + count +
                    ", logisticNo='" + logisticNo + '\'' +
                    ", logisticName='" + logisticName + '\'' +
                    ", logisticType='" + logisticType + '\'' +
                    ", agentName='" + agentName + '\'' +
                    ", productName='" + productName + '\'' +
                    ", status=" + status +
                    ", statusCn='" + statusCn + '\'' +
                    ", tel='" + tel + '\'' +
                    ", xiaoquName='" + xiaoquName + '\'' +
                    ", createDate=" + createDate +
                    ", payDate=" + payDate +
                    ", refundDate=" + refundDate +
                    ", sendDate=" + sendDate +
                    ", totalAmount=" + totalAmount +
                    ", receiverName='" + receiverName + '\'' +
                    ", shipFee=" + shipFee +
                    ", couponAmount=" + couponAmount +
                    ", memo='" + memo + '\'' +
                    ", imgUrls='" + imgUrls + '\'' +
                    '}';
        }
    }

    public static class OrderSubResp {
        private String productName;
        private Integer count;
        private Float amount;
        private String productPic;

        public OrderSubResp(OrderItem item) {
            BeanUtils.copyProperties(item, this);
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Float getAmount() {
            return amount;
        }

        public void setAmount(Float amount) {
            this.amount = amount;
        }

        public String getProductPic() {
            return productPic;
        }

        public void setProductPic(String productPic) {
            this.productPic = productPic;
        }

        @Override
        public String toString() {
            return "OrderSubResp{" +
                    "productName='" + productName + '\'' +
                    ", count=" + count +
                    ", amount=" + amount +
                    ", productPic='" + productPic + '\'' +
                    '}';
        }
    }

    public OrderResp getOrder() {
        return order;
    }

    public void setOrder(OrderResp order) {
        this.order = order;
    }

    public List<OrderSubResp> getDetails() {
        return details;
    }

    public void setDetails(List<OrderSubResp> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "OrderDetailResp{" +
                "order=" + order +
                ", details=" + details +
                '}';
    }
}
