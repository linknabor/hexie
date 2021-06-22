package com.yumu.hexie.integration.workorder.resp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;

public class OrderDetail implements Serializable {

//	private static final String IMG_PREVIEW_URL = "https://images.weserv.nl/?url=";
	/**
	 * 
	 */
	private static final long serialVersionUID = 2934790398357119234L;

	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("workorder_status")
	private String workOrderStatus;
	@JsonProperty("workorder_status_cn")
	private String workOrderStatusCn;
	@JsonProperty("dist_type")
	private String distType;
	@JsonProperty("dist_type_cn")
	private String distTypeCn;
	@JsonProperty("workorder_source")
	private String workOrderSource;
	@JsonProperty("workorder_source_cn")
	private String workOrderSourceCn;
	@JsonProperty("workorder_type")
	private String workOrderType;
	@JsonProperty("workorder_type_cn")
	private String workOrderTypeCn;
	
	@JsonProperty("serve_address")
	private String serveAddress;
	private String content;
	@JsonProperty("image_urls")
	private String imageUrls;
	
	@JsonProperty("sect_name")
	private String sectName;
	@JsonProperty("csp_name")
	private String cspName;
	@JsonProperty("corp_id")
	private String corpid;
	
	@JsonProperty("cust_name")
	private String custName;
	@JsonProperty("cust_contact")
	private String custConcact;
	@JsonProperty("cust_openid")
	private String custOpenid;
	
	@JsonProperty("create_date")
	private String createDate;
	@JsonProperty("create_time")
	private String createTime;
	
	private String assigner;
	@JsonProperty("assigner_contact")
	private String assignerContact;
	@JsonProperty("assign_date")
	private String assignDate;
	@JsonProperty("assign_time")
	private String assignTime;
	
	private String acceptor;
	@JsonProperty("acceptor_contact")
	private String acceptorContact;
	@JsonProperty("accept_date")
	private String acceptDate;
	@JsonProperty("accept_time")
	private String acceptTime;
	
	private String finisher;
	@JsonProperty("finish_date")
	private String finishDate;
	@JsonProperty("finish_time")
	private String finishTime;
	
	@JsonProperty("payorder_id")
	private String payOrderId;
	@JsonProperty("order_amt")
	private String orderAmt;
	@JsonProperty("pay_method")
	private String payMethod;
	@JsonProperty("pay_method_cn")
	private String payMethodCn;
	
	@JsonIgnore
	private QiniuUtil qiniuUtil;
	private List<String> imgList;			//实际图
	private List<String> previewImgList;	//预览图
	private List<String> thumbnailImgList;	//缩略图
	
	public void initImages(QiniuUtil qiniuUtil) {
		this.qiniuUtil = qiniuUtil;
		if (!StringUtils.isEmpty(imageUrls)) {
			String[]imgArr = imageUrls.split(",");
			imgList = Arrays.asList(imgArr);
			List<String> thumbnailList = new ArrayList<>(imgList.size());
			List<String> previewList = new ArrayList<>(imgList.size());
			imgList.forEach(img->{
				thumbnailList.add(qiniuUtil.getThumbnailLink(img, "3", "0"));
				previewList.add(qiniuUtil.getPreviewLink(img, "1", "0"));
			});
			setThumbnailImgList(thumbnailList);
			setPreviewImgList(previewList);
		}
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getWorkOrderStatus() {
		return workOrderStatus;
	}
	public void setWorkOrderStatus(String workOrderStatus) {
		this.workOrderStatus = workOrderStatus;
	}
	public String getDistType() {
		return distType;
	}
	public void setDistType(String distType) {
		this.distType = distType;
	}
	public String getWorkOrderSource() {
		return workOrderSource;
	}
	public void setWorkOrderSource(String workOrderSource) {
		this.workOrderSource = workOrderSource;
	}
	public String getWorkOrderType() {
		return workOrderType;
	}
	public void setWorkOrderType(String workOrderType) {
		this.workOrderType = workOrderType;
	}
	public String getServeAddress() {
		return serveAddress;
	}
	public void setServeAddress(String serveAddress) {
		this.serveAddress = serveAddress;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(String imageUrls) {
		this.imageUrls = imageUrls;
	}
	public String getSectName() {
		return sectName;
	}
	public void setSectName(String sectName) {
		this.sectName = sectName;
	}
	public String getCspName() {
		return cspName;
	}
	public void setCspName(String cspName) {
		this.cspName = cspName;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getAcceptDate() {
		return acceptDate;
	}
	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}
	public String getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}
	public String getFinisher() {
		return finisher;
	}
	public void setFinisher(String finisher) {
		this.finisher = finisher;
	}
	public String getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getPayOrderId() {
		return payOrderId;
	}
	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}
	public String getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(String orderAmt) {
		this.orderAmt = orderAmt;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public String getWorkOrderStatusCn() {
		return workOrderStatusCn;
	}
	public void setWorkOrderStatusCn(String workOrderStatusCn) {
		this.workOrderStatusCn = workOrderStatusCn;
	}
	public String getDistTypeCn() {
		return distTypeCn;
	}
	public void setDistTypeCn(String distTypeCn) {
		this.distTypeCn = distTypeCn;
	}
	public String getWorkOrderSourceCn() {
		return workOrderSourceCn;
	}
	public void setWorkOrderSourceCn(String workOrderSourceCn) {
		this.workOrderSourceCn = workOrderSourceCn;
	}
	public String getWorkOrderTypeCn() {
		return workOrderTypeCn;
	}
	public void setWorkOrderTypeCn(String workOrderTypeCn) {
		this.workOrderTypeCn = workOrderTypeCn;
	}
	public String getPayMethodCn() {
		return payMethodCn;
	}
	public void setPayMethodCn(String payMethodCn) {
		this.payMethodCn = payMethodCn;
	}
	public String getCorpid() {
		return corpid;
	}
	public void setCorpid(String corpid) {
		this.corpid = corpid;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustConcact() {
		return custConcact;
	}
	public void setCustConcact(String custConcact) {
		this.custConcact = custConcact;
	}
	public String getCustOpenid() {
		return custOpenid;
	}
	public void setCustOpenid(String custOpenid) {
		this.custOpenid = custOpenid;
	}
	public String getAssigner() {
		return assigner;
	}
	public void setAssigner(String assigner) {
		this.assigner = assigner;
	}
	public String getAssignerContact() {
		return assignerContact;
	}
	public void setAssignerContact(String assignerContact) {
		this.assignerContact = assignerContact;
	}
	public String getAssignDate() {
		return assignDate;
	}
	public void setAssignDate(String assignDate) {
		this.assignDate = assignDate;
	}
	public String getAssignTime() {
		return assignTime;
	}
	public void setAssignTime(String assignTime) {
		this.assignTime = assignTime;
	}
	public String getAcceptorContact() {
		return acceptorContact;
	}
	public void setAcceptorContact(String acceptorContact) {
		this.acceptorContact = acceptorContact;
	}
	public List<String> getThumbnailImgList() {
		return thumbnailImgList;
	}

	public void setThumbnailImgList(List<String> thumbnailImgList) {
		if (thumbnailImgList==null || thumbnailImgList.isEmpty()) {
			return;
		}
		this.thumbnailImgList = thumbnailImgList;
	}
	public List<String> getPreviewImgList() {
		return previewImgList;
	}

	public void setPreviewImgList(List<String> previewImgList) {
		if (previewImgList==null || previewImgList.isEmpty()) {
			return;
		}
		this.previewImgList = previewImgList;
	}
	public List<String> getImgList() {
		return imgList;
	}
	public void setImgList(List<String> imgList) {
		this.imgList = imgList;
	}
	public String getCreateDateStr() {
		
		return DateUtil.formatFromDB(createDate, createTime);
	}
	
	public String getAssignDateStr() {
		
		return DateUtil.formatFromDB(assignDate, assignTime);
	}
	
	public String getAcceptDateStr() {
		
		return DateUtil.formatFromDB(acceptDate, acceptTime);
	}
	
	public String getFinishDateStr() {
		
		return DateUtil.formatFromDB(finishDate, finishTime);
	}
	
	@Override
	public String toString() {
		return "OrderDetailVO [orderId=" + orderId + ", workOrderStatus=" + workOrderStatus + ", workOrderStatusCn="
				+ workOrderStatusCn + ", distType=" + distType + ", distTypeCn=" + distTypeCn + ", workOrderSource="
				+ workOrderSource + ", workOrderSourceCn=" + workOrderSourceCn + ", workOrderType=" + workOrderType
				+ ", workOrderTypeCn=" + workOrderTypeCn + ", serveAddress=" + serveAddress + ", content=" + content
				+ ", imageUrls=" + imageUrls + ", sectName=" + sectName + ", cspName=" + cspName + ", corpid=" + corpid
				+ ", custName=" + custName + ", custConcact=" + custConcact + ", custOpenid=" + custOpenid
				+ ", assigner=" + assigner + ", assignerContact=" + assignerContact + ", assignDate=" + assignDate
				+ ", assignTime=" + assignTime + ", acceptor=" + acceptor + ", acceptorContact=" + acceptorContact
				+ ", acceptDate=" + acceptDate + ", acceptTime=" + acceptTime + ", finisher=" + finisher
				+ ", finishDate=" + finishDate + ", finishTime=" + finishTime + ", payOrderId=" + payOrderId
				+ ", orderAmt=" + orderAmt + ", payMethod=" + payMethod + ", payMethodCn=" + payMethodCn + "]";
	}
	
	
	
}
