package com.yumu.hexie.web.shequ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.BillStartDate;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.CellVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.BindHouseDTO;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PayWater;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.promotion.coupon.Coupon;
import com.yumu.hexie.model.promotion.coupon.CouponCombination;
import com.yumu.hexie.model.user.BankCard;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.BankCardService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.shequ.vo.PrepayReqVO;
import com.yumu.hexie.web.user.resp.UserInfo;

@Controller(value = "wuyeController")
public class WuyeController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(WuyeController.class);

	@Inject
	private WuyeService wuyeService;
	@Inject
	protected SmsService smsService;
	@Inject
	protected CouponService couponService;
	@Inject
	protected UserService userService;
	@Inject
	protected AddressService addressService;
	@Inject
	protected UserRepository userRepository;
	@Inject
	private SystemConfigService systemConfigService;
	@Autowired
	private PointService pointService;
	@Autowired
	private BankCardService bankCardService;

	/**
	 * 根据用户身份查询其所绑定的房屋
	 */
	/***************** [BEGIN]房产 ********************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hexiehouses", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<HexieHouse>> hexiehouses(@ModelAttribute(Constants.USER) User user) throws Exception {
		
		log.info("user is : " + user);
		if (StringUtil.isEmpty(user.getWuyeId())) {
			return BaseResult.successResult(new ArrayList<HexieHouse>());
		}
		HouseListVO listVo = wuyeService.queryHouse(user);
		if (listVo != null && listVo.getHou_info() != null) {
			return BaseResult.successResult(listVo.getHou_info());
		} else {
			return BaseResult.successResult(new ArrayList<HexieHouse>());
		}
	}

	/**
	 * 房屋解绑
	 * @param user
	 * @param houseId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hexiehouse/delete/{houseId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<String> deleteHouse(HttpSession httpSession, @ModelAttribute(Constants.USER) User user,
			@PathVariable String houseId) throws Exception {
		
		boolean isSuccess = wuyeService.deleteHouse(user, houseId);
		if (isSuccess) {
			httpSession.setAttribute(Constants.USER, user);
			return BaseResult.successResult("解绑房子成功！");
		}else {
			return BaseResult.fail("解绑房子失败！");
		}
	}

	/**
	 * 根据账单编号查询对应的房屋
	 * @param user
	 * @param stmtId
	 * @param house_id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hexiehouse", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<HexieHouse> hexiehouses(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String stmtId, @RequestParam(required = false) String house_id)
			throws Exception {

		if (StringUtil.isEmpty(user.getWuyeId())) {
			// FIXME 后续可调转绑定房子页面
			return BaseResult.successResult(null);
		}
		return BaseResult.successResult(wuyeService.getHouse(user, stmtId));

	}

	/**
	 * 栀子花账单绑定房屋
	 * @param user
	 * @param stmtId
	 * @param houseId
	 * @param area
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addhexiehouse", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<HexieHouse> addHouse(HttpSession httpSession, @ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String stmtId, 
			@RequestParam(required = false) String houseId) throws Exception {
		
		BindHouseDTO dto = wuyeService.bindHouse(user, stmtId, houseId);
		HexieUser u = dto.getHexieUser();
		User currUser = dto.getUser();
		log.info("HexieUser u = " + u);
		if (u != null) {
			currUser = wuyeService.setDefaultAddress(currUser, u);
			if (!systemConfigService.isCardServiceAvailable(currUser.getAppId())) {
				pointService.updatePoint(currUser, "1000", "zhima-house-" + currUser.getId() + "-" + houseId);
			}
			httpSession.setAttribute(Constants.USER, currUser);
		}
		return BaseResult.successResult(u);
	}

	/**
	 * 无账单绑定房屋
	 * @param user
	 * @param houseId
	 * @param area
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addhexiehouse2", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<HexieHouse> addHouseNoStmt(HttpSession httpSession, 
			@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String houseId, 
			@RequestParam(required = false) String area) throws Exception {
		
		BindHouseDTO dto = wuyeService.bindHouseNoStmt(user, houseId, area);
		HexieUser u = dto.getHexieUser();
		User currUser = dto.getUser();
		log.info("HexieUser : " + u);
		if (u != null) {
			currUser = wuyeService.setDefaultAddress(currUser, u);
			if (!systemConfigService.isCardServiceAvailable(currUser.getAppId())) {
				pointService.updatePoint(currUser, "1000", "zhima-house-" + currUser.getId() + "-" + houseId);
			}
			httpSession.setAttribute(Constants.USER, currUser);
		}
		return BaseResult.successResult(u);
	}

	/***************** [END]房产 ********************/
	

	/***************** [BEGIN]缴费记录 ********************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/paymentHistory", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<PayWater>> paymentHistory(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate)
			throws Exception {
		PayWaterListVO listVo = wuyeService.queryPaymentList(user, startDate, endDate);
		if (listVo != null && listVo.getBill_info_water() != null) {
			return BaseResult.successResult(listVo.getBill_info_water());
		} else {
			return BaseResult.successResult(null);
		}
	}

	/**
	 * 查询缴费详情
	 * 
	 * @param session
	 * @param trade_water_id
	 *            流水号
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/queryPaymentDetail/{trade_water_id}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<PaymentInfo> queryPaymentDetail(@ModelAttribute(Constants.USER) User user,
			@PathVariable String trade_water_id) throws Exception {
		PaymentInfo info = wuyeService.queryPaymentDetail(user, trade_water_id);
		if (info != null) {
			return BaseResult.successResult(info);
		} else {
			return BaseResult.successResult(null);
		}
	}
	/***************** [END]缴费记录 ********************/

	/***************** [BEGIN]账单查询 ********************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/billList", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<BillListVO> billList(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String payStatus, @RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate, @RequestParam(required = false) String currentPage,
			@RequestParam(required = false) String totalCount, @RequestParam(required = false) String house_id, 
			@RequestParam(required = false) String sect_id, @RequestParam(required = false) String regionname)
			throws Exception {
		BillListVO listVo = wuyeService.queryBillList(user, payStatus, startDate, endDate, currentPage,
				totalCount, house_id, sect_id, regionname);
		if (listVo != null && listVo.getBill_info() != null) {
			return BaseResult.successResult(listVo);
		} else {
			return BaseResult.successResult(null);
		}
	}
	/***************** [END]账单查询 ********************/
	
	/***************** [BEGIN]无账单查询 ********************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPayListStd", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<BillListVO> getPayListStd(@ModelAttribute(Constants.USER) User user, @RequestParam(required = false) String start_date,
			@RequestParam(required = false) String end_date,  @RequestParam(required = false) String house_id, 
			@RequestParam(required = false) String sect_id, @RequestParam(required = false) String regionname)
			throws Exception {
		BillListVO listVo = wuyeService.queryBillListStd(user, start_date, end_date,house_id,sect_id,regionname);
		if (listVo != null && !listVo.getOther_bill_info().isEmpty()) {
			return BaseResult.successResult(listVo);
		} else {
			return BaseResult.successResult(null);
		}
	}

	/***************** [BEGIN]缴费 ********************/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getBillDetail", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<PaymentInfo> getBillDetail(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String billId, @RequestParam(required = false) String stmtId,
			@RequestParam(required = false) String regionname) {
		
		PaymentInfo paymentInfo = wuyeService.getBillDetail(user, stmtId, billId, regionname);
		return BaseResult.successResult(paymentInfo);
	}

	/**
	 * 创建交易，获取预支付ID
	 * stmtId在快捷支付的时候会用到
	 * @param user
	 * @param prepayReq
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPrePayInfo", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<WechatPayInfo> getPrePayInfo(@ModelAttribute(Constants.USER) User user,
			@RequestBody PrepayReqVO prepayReqVo) throws Exception {
		
		WechatPayInfo result = new WechatPayInfo();
		if (StringUtils.isEmpty(prepayReqVo.getFeeMianBill())) {
			prepayReqVo.setFeeMianBill("0");
		}
		if (StringUtils.isEmpty(prepayReqVo.getFeeMianAmt())) {
			prepayReqVo.setFeeMianAmt("0");
		}
		
		log.info("prepayReqVo : " + prepayReqVo);
		PrepayRequestDTO dto = new PrepayRequestDTO();
		BeanUtils.copyProperties(prepayReqVo, dto);
		dto.setUser(user);
		log.info("prepayRequestDTO : " + dto);
		result = wuyeService.getPrePayInfo(dto);
		return BaseResult.successResult(result);
	}

	/**
	 * 无账单缴费唤起支付
	 * @param user
	 * @param houseId
	 * @param start_date
	 * @param end_date
	 * @param couponUnit
	 * @param couponNum
	 * @param couponId
	 * @param mianBill
	 * @param mianAmt
	 * @param reduceAmt
	 * @param invoice_title_type
	 * @param credit_code
	 * @param invoice_title
	 * @param regionname
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getOtherPrePayInfo", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<WechatPayInfo> getOtherPrePayInfo(@ModelAttribute(Constants.USER) User user,
			@RequestBody PrepayReqVO prepayReq) throws Exception {
		WechatPayInfo result;
		try {
			PrepayRequestDTO dto = new PrepayRequestDTO();
			BeanUtils.copyProperties(prepayReq, dto);
			dto.setUser(user);
			result = wuyeService.getOtherPrePayInfo(dto);
		} catch (Exception e) {
			return BaseResult.fail(e.getMessage());
		}
		return BaseResult.successResult(result);
	}

	/**
	 *  通知支付成功，并获取支付查询的返回结果
	 * @param user
	 * @param billId
	 * @param stmtId
	 * @param tradeWaterId
	 * @param packageId
	 * @param feePrice
	 * @param couponId
	 * @param bindSwitch
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/noticePayed", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> noticePayed(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String tradeWaterId, 
			@RequestParam(required = false) String feePrice, 
			@RequestParam(required = false) String couponId,
			@RequestParam(value ="bind_switch", required = false) String bindSwitch)
			throws Exception {
		
		wuyeService.noticePayed(user, tradeWaterId, couponId, feePrice, bindSwitch);
		return BaseResult.successResult("支付成功。");
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/quickPayBillList/{stmtId}/{currPage}/{totalCount}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<BillListVO> quickPayBillList(@ModelAttribute(Constants.USER) User user,
			@PathVariable String stmtId, @PathVariable String currPage, @PathVariable String totalCount)
			throws Exception {
		BillListVO listVo = wuyeService.quickPayInfo(user, stmtId, currPage, totalCount);
		if (listVo != null) {
			return BaseResult.successResult(listVo);
		} else {
			return BaseResult.successResult(null);
		}
	}

	/***************** [END]缴费 ********************/

	/**
	 * 获取系统参数表中配置的活动时间 如果当前时间为活动时间段内，则: 1.推送短信给用户 2.推送微信模版消息给用户
	 * 3.跳转到成功页提示用户已获取代金券
	 * 
	 * @param session
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/sendNotification", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult sendNotification(HttpSession session) {

		User user = (User) session.getAttribute(Constants.USER);
		String retValue = "false";
		String[] dates = systemConfigService.queryActPeriod();
		if (dates.length == 2) {
			String startDate = dates[0];
			String endDate = dates[1];

			Date sDate = DateUtil.getDateFromString(startDate);
			Date eDate = DateUtil.getDateFromString(endDate);
			Date currDate = new Date();
			if (currDate.after(sDate) && currDate.before(eDate)) {
				retValue = "true";
			}
			/* 如果在活动日期内，则：1发送短信告知用户。2推送微信模版消息 */
			if ("true".equals(retValue)) {
				sendMsg(user);
				sendRegTemplateMsg(user);
			}
		}

		return BaseResult.successResult(retValue);

	}

	@Async
	private void sendMsg(User user) {
		String msg = "您好，欢迎加入合协社区。您已获得价值10元红包一份。感谢您对合协社区的支持。";
		smsService.sendMsg(user, user.getTel(), msg, 11, 3);
	}

	@Async
	private void sendRegTemplateMsg(User user) {
		TemplateMsgService.sendRegisterSuccessMsg(user, systemConfigService.queryWXAToken(user.getAppId()));
	}

	/**
	 * 获取支付物业费时可用的红包
	 * 
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getCouponsPayWuYe", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Coupon>> getCoupons(HttpSession session) {
		User user = (User) session.getAttribute(Constants.USER);
		List<Coupon> list = couponService.findAvaibleCouponForWuye(user.getId());

		if (list == null) {
			list = new ArrayList<Coupon>();
		}
		return BaseResult.successResult(list);

	}

	/**
	 * 为物业缴费成功的用户发放红包
	 * 
	 * @param session
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "sendCoupons4WuyePay", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult sendCoupons(HttpSession session, @RequestParam(required = false) String tradeWaterId,
			@RequestParam(required = false) String feePrice) {

		User user = (User) session.getAttribute(Constants.USER);
		int couponCombination = 1;
		List<CouponCombination> list = couponService.findCouponCombination(couponCombination);

		addCouponsFromSeed(user, list);

		sendPayTemplateMsg(user, tradeWaterId, feePrice);

		return BaseResult.successResult("send succeeded !");
	}

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "updateCouponStatus", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult updateCouponStatus(HttpSession session) {

		if (session == null) {
			return BaseResult.fail("no session info ...");
		}

		User user = (User) session.getAttribute(Constants.USER);
		if (user == null) {
			return BaseResult.fail("user is null ...");
		}
		List<Coupon> list = couponService.findAvaibleCouponForWuye(user.getId());

		if (list.size() > 0) {
			String result = wuyeService.queryCouponIsUsed(user);
			for (int i = 0; i < list.size(); i++) {
				Coupon coupon = list.get(i);
				if ((coupon.getStatus() == ModelConstant.COUPON_STATUS_AVAILABLE)) {

					if (!StringUtil.isEmpty(result)) {

						if ("99".equals(result)) {
							return BaseResult.fail("网络异常，请刷新后重试");
						}

						String[] couponArr = result.split(",");

						for (int j = 0; j < couponArr.length; j++) {
							String coupon_id = couponArr[j];
							try {
								couponService.comsume("20", Integer.parseInt(coupon_id)); // 这里写死20
							} catch (Exception e) {
								log.error("couponId : " + coupon_id + ", " + e.getMessage());
							}
						}

					}

				}

			}
		}

		return BaseResult.successResult("succeeded");

	}

	@Async
	private void addCouponsFromSeed(User user, List<CouponCombination> list) {

		try {

			for (int i = 0; i < list.size(); i++) {
				couponService.addCouponFromSeed(list.get(i).getSeedStr(), user);
			}

		} catch (Exception e) {

			log.error("add Coupons for wuye Pay : " + e.getMessage());
		}

	}

	@Async
	private void sendPayTemplateMsg(User user, String tradeWaterId, String feePrice) {

		TemplateMsgService.sendWuYePaySuccessMsg(user, tradeWaterId, feePrice, systemConfigService.queryWXAToken(user.getAppId()));
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/applyInvoice", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult applyInvoice(@RequestParam(required = false) String mobile,
			@RequestParam(required = false) String invoice_title, @RequestParam(required = false) String yzm,
			@RequestParam(required = false) String trade_water_id,
			@RequestParam(required = false) String invoice_title_type,
			@RequestParam(required = false) String credit_code) {
		
		boolean isCheck = smsService.checkVerificationCode(mobile, yzm);
		if (!isCheck) {
			return new BaseResult<UserInfo>().failMsg("校验失败！");
		} else {
			String result = wuyeService.updateInvoice(mobile, invoice_title, invoice_title_type, credit_code,
					trade_water_id);
			if ("99".equals(result)) {
				return BaseResult.fail("网络异常，请刷新后重试");
			}
			return BaseResult.successResult("succeeded");
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getInvoice", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<InvoiceInfo> getInvoice(HttpServletResponse response, @RequestParam(required = false) String trade_water_id) {
		
		String token = smsService.getRandomToken();
		log.info("random token : " + token);
		if (StringUtils.isEmpty(trade_water_id) || trade_water_id.length() != 18) {
			response.addHeader("Access-Control-Allow-Token", token);
			return BaseResult.fail("请正确填写交易ID。");
		}
		InvoiceInfo invoice = wuyeService.getInvoiceByTradeId(trade_water_id);
		if (invoice == null) {
			response.addHeader("Access-Control-Allow-Token", token);
			return BaseResult.fail("未查询到交易对应的发票信息。");
		}
		token = smsService.saveAndGetInvoiceToken(trade_water_id);
		log.info("token : " + token);
		response.addHeader("Access-Control-Allow-Token", token);
		return BaseResult.successResult(invoice);
		
	}

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/initSession4Test/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult initSessionForTest(HttpSession session, @PathVariable String userId) {

		if (!StringUtil.isEmpty(userId)) {
			User user = userRepository.findOne(Long.valueOf(userId));
			session.setAttribute("sessionUser", user);
		}
		return BaseResult.successResult("succeeded");

	}

	/**
	 * 根据ID查询指定类型的合协社区物业信息
	 * @param user
	 * @param sect_id
	 * @param build_id
	 * @param unit_id
	 * @param data_type
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getHeXieCellById", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<CellVO> getHeXieCellById(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String sect_id, @RequestParam(required = false) String build_id,
			@RequestParam(required = false) String unit_id, @RequestParam(required = false) String data_type,
			@RequestParam(required = false, value = "regionname") String regionName)
			throws Exception {
		CellListVO cellMng = wuyeService.querySectHeXieList(user, sect_id, build_id, unit_id, data_type, regionName);
		if (cellMng != null) {
			return BaseResult.successResult(cellMng);
		} else {
			return BaseResult.successResult(new ArrayList<CellVO>());
		}
	}

	/**
	 * 根据名称模糊查询合协社区小区列表
	 * @param user
	 * @param sect_name
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getVagueSectByName", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<CellVO> getVagueSectByName(@ModelAttribute(Constants.USER) User user,
			@RequestParam(required = false) String sect_name,
			@RequestParam(required = false, value = "regionname") String regionName) throws Exception {

		CellListVO cellMng = wuyeService.getVagueSectByName(user, sect_name, regionName);
		if (cellMng != null) {
			return BaseResult.successResult(cellMng);
		} else {
			return BaseResult.successResult(new ArrayList<CellVO>());
		}
	}

	/**
	 * 查询无账单缴费房子开始日期
	 * @param user
	 * @param house_id
	 * @param regionname
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getBillStartDateSDO", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<BillStartDate> getBillStartDateSDO(@ModelAttribute(Constants.USER) User user,@RequestParam String house_id,@RequestParam String regionname) throws Exception {

		return BaseResult.successResult(wuyeService.getBillStartDateSDO(user,house_id,regionname));
	}
	
	/**
	 * 根据户号添加绑定房屋
	 * @param user
	 * @param houseId
	 * @param area
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/hexiehouse/{verno}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<HexieHouse> hexiehouse(@ModelAttribute(Constants.USER) User user,
			@PathVariable(value = "verno") String verNo) throws Exception {
		
		HexieHouse hexieHouse = wuyeService.getHouseByVerNo(user, verNo);
		log.info(" hexieHouse: " + hexieHouse);
		return BaseResult.successResult(hexieHouse);
	}
	
	/**
	 * 获取用户绑定的银行卡信息
	 * @param user
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/bankCard", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<BankCard>> bankCard(@ModelAttribute(Constants.USER) User user) {
		
		List<BankCard> cardList = bankCardService.getByUserId(user.getId());
		return BaseResult.successResult(cardList);
	}
	
	/**
	 * 获取用户绑定的银行卡信息
	 * @param user
	 */
	@RequestMapping(value = "/unionPayCallBack", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String unionPayCallBack(HttpServletRequest request, @RequestBody(required = false) Map<String, String> map) {
		
		Map<String, String[]> requestMap = request.getParameterMap();
		Iterator<Map.Entry<String, String[]>> it = requestMap.entrySet().iterator();
		StringBuffer buffer = new StringBuffer();
		while (it.hasNext()) {
			Map.Entry<String, String[]> entry = it.next();
			String key = entry.getKey();
			String[]value = entry.getValue();
			List<String> list = Arrays.asList(value);
			StringBuffer bf = new StringBuffer();
			bf.append("value : ");
			for (String string : list) {
				bf.append(string).append(",");
			}
			log.info("key : " + key + ", " + bf);
			buffer.append("key : " + key + ", " + bf).append("\r\n");
		}
		log.info("bodyMap:" + map);
		
		return "bodyMap:" + map + "\r\n" + "requestMap : " + buffer;
	}
	


}
