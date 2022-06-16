package com.yumu.hexie.model.market;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.model.BaseModel;
import com.yumu.hexie.model.ModelConstant;

/**
 * 退款记录表
 * @author david
 *
 */
@Entity
public class RefundRecord extends BaseModel {
	
	private static Logger logger = LoggerFactory.getLogger(RefundRecord.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -604606892630217214L;

	private long orderId;	//订单id
	private Float refundAmt;	//本次退款金额
	private int refundCount;	//本次退款商品件数
	private int	applyType;	//页面上选择的申请类型,0我要退款（无需退货）,1我要退货退款
	private String applyReason;	//申请原因
	private String memo;	//申请退款的补充描述或者审核退回原因
	private String items;	//json数组形式,商品名+","+退款金额
	private long userId;	//用户id
	private long ownerId;	//团长id，对应servieOrder表中的groupLeader
	private int refundType;	//退款类型，参考ModelConstant.REFUND_REASON_GROUP_USER_REFUND,3用户发起，4团长发起
	private Integer status;	//0撤销（撤回时变0），1待团长处理，2团长审核通过，3团长审核拒绝，4系统退款中，5退款成功
	private String operatorName;	//操作发起人
	private Date operatorDate;		//操作日期
	private Integer operation;	//操作动作,0申请，1申请撤回，2申请修改,3团长审核通过，4团长审核拒绝,5团长取消商品并退款，6退款完成 
	
	@Transient
	public boolean getCanRefund() {
		/*可以退款的状态*/
		if (ModelConstant.REFUND_STATUS_INIT == status) {
			return true;
		}
		return false;
	}
	
	@Transient
	public List<Map<String, String>> getItemList(){
		String itemStr = items;
		List<Map<String, String>> list = new ArrayList<>();
		if (!StringUtils.isEmpty(itemStr)) {
			TypeReference<List<Map<String, String>>> typeReference = new TypeReference<List<Map<String,String>>>() {};
			try {
				list = JacksonJsonUtil.getMapperInstance(false).readValue(itemStr, typeReference);
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return list;
	}
	
	@Transient
	public String getOperatorDateStr() {
		String dateStr = "";
		if (operatorDate != null) {
			dateStr = DateUtil.dtFormat(operatorDate, DateUtil.dttmSimple);
		}
		return dateStr;
	}
	
	@Transient
	public String getApplyTypeCn() {
		String applyTypeCn = "";
		if (applyType == 0) {
			applyTypeCn = "退款（无需退货）";
		} else if (applyType == 1) {
			applyTypeCn = "退货退款";
		}
		return applyTypeCn;
	}
	
	@Transient
	public String getStatusCn() {
		String statusCn = "";
		if (ModelConstant.REFUND_STATUS_CANCEL == status) {
			statusCn = "申请已撤销";
		} else if (ModelConstant.REFUND_STATUS_INIT == status) {
			statusCn = "待团长处理";
		} else if (ModelConstant.REFUND_STATUS_AUDIT_PASSED == status) {
			statusCn = "申请通过";
		} else if (ModelConstant.REFUND_STATUS_SYS_REFUNDING == status) {
			statusCn = "系统退款中";
		} else if (ModelConstant.REFUND_STATUS_REFUNDED == status) {
			statusCn = "退款成功";
		}
		return statusCn;
	}
	
	@Transient
	public String getOperationCn () {
		String operationCn = "";
		if (ModelConstant.REFUND_OPERATION_USER_APPLY == operation) {
			operationCn = "申请退款";
		} else if (ModelConstant.REFUND_OPERATION_OWNER_APPLY == operation) {
			operationCn = "发起退款";
		} else if (ModelConstant.REFUND_OPERATION_CANCEL == operation) {
			operationCn = "撤回退款申请";
		} else if (ModelConstant.REFUND_OPERATION_MODIFY == operation) {
			operationCn = "修改退款申请";
		} else if (ModelConstant.REFUND_OPERATION_PASS_AUDIT == operation) {
			operationCn = "通过退款申请";
		} else if (ModelConstant.REFUND_OPERATION_REJECT_AUDIT == operation) {
			operationCn = "拒绝退款申请";
		} else if (ModelConstant.REFUND_OPERATION_WITHDRAW_REFUND == operation) {
			operationCn = "取消商品并退款";
		} else if (ModelConstant.REFUND_OPERATION_REFUNDED == operation) {
			operationCn = "退款完成";
		}
		return operationCn;
		
	}
	
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public Float getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(Float refundAmt) {
		this.refundAmt = refundAmt;
	}
	public int getRefundCount() {
		return refundCount;
	}
	public void setRefundCount(int refundCount) {
		this.refundCount = refundCount;
	}
	public int getApplyType() {
		return applyType;
	}
	public void setApplyType(int applyType) {
		this.applyType = applyType;
	}
	public String getApplyReason() {
		return applyReason;
	}
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public int getRefundType() {
		return refundType;
	}
	public void setRefundType(int refundType) {
		this.refundType = refundType;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public Date getOperatorDate() {
		return operatorDate;
	}
	public void setOperatorDate(Date operatorDate) {
		this.operatorDate = operatorDate;
	}
	public Integer getOperation() {
		return operation;
	}
	public void setOperation(Integer operation) {
		this.operation = operation;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
