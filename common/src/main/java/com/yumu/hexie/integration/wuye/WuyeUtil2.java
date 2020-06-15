package com.yumu.hexie.integration.wuye;

import java.io.IOException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;
import com.yumu.hexie.integration.wuye.dto.OtherPayDTO;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.integration.wuye.dto.SignInOutDTO;
import com.yumu.hexie.integration.wuye.req.BillDetailRequest;
import com.yumu.hexie.integration.wuye.req.BillStdRequest;
import com.yumu.hexie.integration.wuye.req.DiscountViewRequest;
import com.yumu.hexie.integration.wuye.req.OtherPayRequest;
import com.yumu.hexie.integration.wuye.req.PaySmsCodeRequest;
import com.yumu.hexie.integration.wuye.req.PrepayRequest;
import com.yumu.hexie.integration.wuye.req.QrCodePayServiceRequest;
import com.yumu.hexie.integration.wuye.req.QrCodeRequest;
import com.yumu.hexie.integration.wuye.req.QueryOrderRequest;
import com.yumu.hexie.integration.wuye.req.QuickPayRequest;
import com.yumu.hexie.integration.wuye.req.SignInOutRequest;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.vo.Discounts;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.QrCodePayService;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.BankCard;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;

/**
 * 新的WuyeUtil
 * http的请求都改用restTemplate，旧的WuyeUtil中的方法，会慢慢代替掉,最后全部去掉旧版本的httpclient
 * @author david
 *
 */
@Component
public class WuyeUtil2 {
	
	@Autowired
	private RestUtil restUtil;
	
	@Autowired
	private RequestUtil requestUtil;
	
	private static final String QUICK_PAY_URL = "quickPaySDO.do"; // 快捷支付
	private static final String WX_PAY_URL = "wechatPayRequestSDO.do"; // 微信支付请求
	private static final String DISCOUNT_URL = "getBillPayDetailSDO.do";	//获取优惠明细
	private static final String CARD_PAY_SMS_URL = "getCardPaySmsCodeSDO.do";	//获取优惠明细
	private static final String QUERY_ORDER_URL = "queryOrderSDO.do";	//订单查询
	private static final String BILL_DETAIL_URL = "getBillInfoMSDO.do"; // 获取账单详情
	private static final String BILL_LIST_STD_URL = "getPayListStdSDO.do"; // 获取账单列表
	private static final String OTHER_PAY_URL = "otherPaySDO.do";	//其他收入支付
	private static final String QRCODE_PAY_SERVICE_URL = "getServiceSDO.do";	//二维码支付服务信息
	private static final String QRCODE_URL = "getQRCodeSDO.do";	//二维码支付服务信息
	private static final String SIGN_IN_OUT_URL = "signInSDO.do";	//二维码支付服务信息
	
	/**
	 * 标准版查询账单
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param house_id
	 * @param sect_id
	 * @param regionurl
	 * @return
	 * @throws Exception 
	 */
	public BaseResult<BillListVO> queryBillList(User user, String startDate, String endDate, String houseId, String regionName) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, regionName);
		requestUrl += BILL_LIST_STD_URL;
		
		BillStdRequest billStdRequest = new BillStdRequest();
		billStdRequest.setWuyeId(user.getWuyeId());
		billStdRequest.setHouseId(houseId);
		billStdRequest.setStartDate(startDate);
		billStdRequest.setEndDate(endDate);
		
		TypeReference<CommonResponse<BillListVO>> typeReference = new TypeReference<CommonResponse<BillListVO>>(){};
		CommonResponse<BillListVO> hexieResponse = restUtil.exchange(requestUrl, billStdRequest, typeReference);
		BaseResult<BillListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	
	
	/**
	 * 账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	 * @param user
	 * @param stmtId
	 * @param anotherbillIds
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public BaseResult<PaymentInfo> getBillDetail(User user, String stmtId,String anotherbillIds, String regionName) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, regionName);
		requestUrl += BILL_DETAIL_URL;
		BillDetailRequest billDetailRequest = new BillDetailRequest();
		billDetailRequest.setWuyeId(user.getWuyeId());
		billDetailRequest.setStmtId(stmtId);
		billDetailRequest.setBillId(anotherbillIds);
		
		TypeReference<CommonResponse<PaymentInfo>> typeReference = new TypeReference<CommonResponse<PaymentInfo>>(){};
		CommonResponse<PaymentInfo> hexieResponse = restUtil.exchange(requestUrl, billDetailRequest, typeReference);
		BaseResult<PaymentInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
	}
	
	/**
	 * 账单快捷缴费
	 * @param user
	 * @param stmtId
	 * @param currPage
	 * @param totalCount
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public BaseResult<BillListVO> quickPayInfo(User user, String stmtId, String currPage, String totalCount) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUICK_PAY_URL;
		
		QuickPayRequest quickPayRequest = new QuickPayRequest();
		quickPayRequest.setStmtId(stmtId);
		quickPayRequest.setCurrPage(currPage);
		quickPayRequest.setTotalCount(totalCount);
		
		TypeReference<CommonResponse<BillListVO>> typeReference = new TypeReference<CommonResponse<BillListVO>>(){};
		CommonResponse<BillListVO> hexieResponse = restUtil.exchange(requestUrl, quickPayRequest, typeReference);
		BaseResult<BillListVO> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
	}
	

	/**
	 * 专业版缴费
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> getPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception {

		
		User user = prepayRequestDTO.getUser();
		String appid = user.getAppId();
		String fromSys = requestUtil.getSysName();
		if (!StringUtils.isEmpty(appid)) {
			//TODO 下面静态引用以后改注入
			fromSys = SystemConfigServiceImpl.getSysMap().get(appid);
		}
		String requestUrl = requestUtil.getRequestUrl(user, prepayRequestDTO.getRegionName());
		requestUrl += WX_PAY_URL;
		
		PrepayRequest prepayRequest = new PrepayRequest(prepayRequestDTO);
		prepayRequest.setFromSys(fromSys);
		prepayRequest.setAppid(user.getAppId());
		
		TypeReference<CommonResponse<WechatPayInfo>> typeReference = new TypeReference<CommonResponse<WechatPayInfo>>(){};
		CommonResponse<WechatPayInfo> hexieResponse = restUtil.exchange(requestUrl, prepayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}

	/**
	 * 获取优惠支付明细
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<Discounts> getDiscounts(DiscountViewRequestDTO discountViewRequestDTO) throws Exception {
		
		User user = discountViewRequestDTO.getUser();
		String requestUrl = requestUtil.getRequestUrl(user, discountViewRequestDTO.getRegionName());
		requestUrl += DISCOUNT_URL;
		
		DiscountViewRequest discountViewRequest = new DiscountViewRequest(discountViewRequestDTO);
		TypeReference<CommonResponse<Discounts>> typeReference = new TypeReference<CommonResponse<Discounts>>(){};
		CommonResponse<Discounts> hexieResponse = restUtil.exchange(requestUrl, discountViewRequest, typeReference);
		BaseResult<Discounts> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 获取优惠支付明细
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> queryOrder(User user, String orderNo) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QUERY_ORDER_URL;
		
		QueryOrderRequest queryOrderRequest = new QueryOrderRequest();
		queryOrderRequest.setOrderNo(orderNo);
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchange(requestUrl, queryOrderRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 获取优惠支付明细
	 * @param prepayRequestDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> getPaySmsCode(User user, BankCard bankCard) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += CARD_PAY_SMS_URL;
		
		PaySmsCodeRequest paySmsCodeRequest = new PaySmsCodeRequest();
		paySmsCodeRequest.setMobile(bankCard.getPhoneNo());
		paySmsCodeRequest.setQuickToken(bankCard.getQuickToken());
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchange(requestUrl, paySmsCodeRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 其他收入支付
	 * @param otherPayDTO
	 * @return
	 * @throws Exception
	 */
	public BaseResult<WechatPayInfo> requestOtherPay(OtherPayDTO otherPayDTO) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(otherPayDTO.getUser(), "");
		requestUrl += OTHER_PAY_URL;
		OtherPayRequest otherPayRequest = new OtherPayRequest(otherPayDTO);
		
		TypeReference<CommonResponse<WechatPayInfo>> typeReference = new TypeReference<CommonResponse<WechatPayInfo>>(){};
		CommonResponse<WechatPayInfo> hexieResponse = restUtil.exchange(requestUrl, otherPayRequest, typeReference);
		BaseResult<WechatPayInfo> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
	}
	
	/**
	 * 获取二维码支付服务详情
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<QrCodePayService> getQrCodePayService(User user) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QRCODE_PAY_SERVICE_URL;

		QrCodePayServiceRequest qrCodePayServiceRequest = new QrCodePayServiceRequest();
		qrCodePayServiceRequest.setOpenid(user.getOpenid());
		qrCodePayServiceRequest.setTel(user.getTel());
		
		TypeReference<CommonResponse<QrCodePayService>> typeReference = new TypeReference<CommonResponse<QrCodePayService>>(){};
		CommonResponse<QrCodePayService> hexieResponse = restUtil.exchange(requestUrl, qrCodePayServiceRequest, typeReference);
		BaseResult<QrCodePayService> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 获取二维码
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<byte[]> getQrCode(User user, String qrCodeId) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(user, "");
		requestUrl += QRCODE_URL;

		QrCodeRequest qrCodeRequest = new QrCodeRequest();
		qrCodeRequest.setQrCodeId(qrCodeId);
		
		TypeReference<CommonResponse<byte[]>> typeReference = new TypeReference<CommonResponse<byte[]>>(){};
		CommonResponse<byte[]> hexieResponse = restUtil.exchange4Resource(requestUrl, qrCodeRequest, typeReference);
		BaseResult<byte[]> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	/**
	 * 获取二维码
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public BaseResult<String> signInOut(SignInOutDTO signInOutDTO) throws Exception {
		
		String requestUrl = requestUtil.getRequestUrl(signInOutDTO.getUser(), "");
		requestUrl += SIGN_IN_OUT_URL;

		SignInOutRequest signInOutRequest = new SignInOutRequest();
		BeanUtils.copyProperties(signInOutDTO, signInOutRequest);
		
		TypeReference<CommonResponse<String>> typeReference = new TypeReference<CommonResponse<String>>(){};
		CommonResponse<String> hexieResponse = restUtil.exchange(requestUrl, signInOutRequest, typeReference);
		BaseResult<String> baseResult = new BaseResult<>();
		baseResult.setData(hexieResponse.getData());
		return baseResult;
		
	}
	
	
	
}
