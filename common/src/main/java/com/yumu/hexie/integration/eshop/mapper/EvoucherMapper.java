package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.model.ModelConstant;
import org.springframework.util.StringUtils;

public class EvoucherMapper implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -14651975225356427L;

    private String code;    //which can convert to qrcode.
    @JsonProperty("order_id")
    private long orderId;    //serviceOrder id 购买的订单号
    private int status;    //0不可用，1可用，2过期

    private String status_cn;
    @JsonProperty("actual_price")
    private float actualPrice;    //实际售价
    @JsonProperty("ori_price")
    private float oriPrice;    //原价

    private long userId;    //下单用户id
    private String tel;        //下单用户手机号
    private String openid;    //下单用户openid

    private long productId;    //优惠产品ID
    @JsonProperty("product_name")
    private String productName;    //优惠项目名称

    @JsonProperty("begin_date")
    private Date beginDate;    //生效日期
    @JsonProperty("end_date")
    private Date endDate;    //过期日期
    @JsonProperty("consume_date")
    private Date consumeDate;    //使用日期

    private long operatorUserId;    //操作人id
    @JsonProperty("operator_name")
    private String operatorName;    //操作人
    private String operatorTel;    //操作人手机号

    private long agentId;        //代理商ID
    @JsonProperty("agent_name")
    private String agentName;    //代理商名称
    @JsonProperty("agent_no")
    private String agentNo;        //代理商编号

    private long merchantId;    //商户ID
    @JsonProperty("merchant_name")
    private String merchantName;    //商户名称

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if (!StringUtils.isEmpty(status)) {
            if (ModelConstant.EVOUCHER_STATUS_NORMAL == status) {
                this.status_cn = "正常";
            } else if (ModelConstant.EVOUCHER_STATUS_USED == status) {
                this.status_cn = "已使用";
            } else if (ModelConstant.EVOUCHER_STATUS_EXPIRED == status) {
                this.status_cn = "过期";
            } else if (ModelConstant.EVOUCHER_STATUS_INVALID == status) {
                this.status_cn = "不可用";
            }
        }
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getConsumeDate() {
        return consumeDate;
    }

    public void setConsumeDate(Date consumeDate) {
        this.consumeDate = consumeDate;
    }

    public long getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(long operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorTel() {
        return operatorTel;
    }

    public void setOperatorTel(String operatorTel) {
        this.operatorTel = operatorTel;
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

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public float getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(float actualPrice) {
        this.actualPrice = actualPrice;
    }

    public float getOriPrice() {
        return oriPrice;
    }

    public void setOriPrice(float oriPrice) {
        this.oriPrice = oriPrice;
    }

    public String getStatus_cn() {
        return status_cn;
    }

    public void setStatus_cn(String status_cn) {
        this.status_cn = status_cn;
    }

    @Override
    public String toString() {
        return "EvoucherMapper{" +
                "code='" + code + '\'' +
                ", orderId=" + orderId +
                ", status=" + status +
                ", status_cn='" + status_cn + '\'' +
                ", actualPrice=" + actualPrice +
                ", oriPrice=" + oriPrice +
                ", userId=" + userId +
                ", tel='" + tel + '\'' +
                ", openid='" + openid + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", consumeDate=" + consumeDate +
                ", operatorUserId=" + operatorUserId +
                ", operatorName='" + operatorName + '\'' +
                ", operatorTel='" + operatorTel + '\'' +
                ", agentId=" + agentId +
                ", agentName='" + agentName + '\'' +
                ", agentNo='" + agentNo + '\'' +
                ", merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                '}';
    }
}
