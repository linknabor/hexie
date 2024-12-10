package com.yumu.hexie.service.shequ.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.constant.ConstantAlipay;
import com.yumu.hexie.integration.wechat.entity.common.WechatResponse;
import com.yumu.hexie.integration.wechat.service.FileService;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil2;
import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;
import com.yumu.hexie.integration.wuye.dto.GetCellDTO;
import com.yumu.hexie.integration.wuye.dto.OtherPayDTO;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.integration.wuye.dto.SignInOutDTO;
import com.yumu.hexie.integration.wuye.req.QueryAlipayConsultRequest;
import com.yumu.hexie.integration.wuye.resp.AlipayMarketingConsult;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.BillStartDate;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.Discounts;
import com.yumu.hexie.integration.wuye.vo.EReceipt;
import com.yumu.hexie.integration.wuye.vo.HexieAddress;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceDetail;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.QrCodePayService;
import com.yumu.hexie.integration.wuye.vo.QrCodePayService.PayCfg;
import com.yumu.hexie.integration.wuye.vo.ReceiptInfo;
import com.yumu.hexie.integration.wuye.vo.ReceiptInfo.Receipt;
import com.yumu.hexie.integration.wuye.vo.SectInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.event.dto.BaseEventDTO;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.promotion.coupon.CouponCombination;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.BankCard;
import com.yumu.hexie.model.user.BankCardRepository;
import com.yumu.hexie.model.user.NewLionUser;
import com.yumu.hexie.model.user.NewLionUserRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.cache.CacheService;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.impl.SystemConfigServiceImpl;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.shequ.req.ReceiptApplicationReq;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.vo.BindHouseQueue;
import com.yumu.hexie.vo.req.QueryFeeSmsBillReq;

@Service("wuyeService")
public class WuyeServiceImpl implements WuyeService {
	
	private static final Logger log = LoggerFactory.getLogger(WuyeServiceImpl.class);
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	@Qualifier("stringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	
	@Autowired
	private BankCardRepository bankCardRepository;
	
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	
	@Autowired
	private GotongService gotongService;
	
	@Autowired
	private NewLionUserRepository newLionUserRepository;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CacheService cacheService;
	
	@Override
	public HouseListVO queryHouse(User user, String sectId) {
		return WuyeUtil.queryHouse(user, sectId).getData();
	}

	@Override
	@Transactional
	public boolean deleteHouse(User user, String houseId) {
		
		BaseResult<HouseListVO> result = WuyeUtil.deleteHouse(user, houseId);
		int totalBind = 0;
		if (result.isSuccess()) {
			// 添加电话到user表
			HouseListVO houseListVO = result.getData();
			if (houseListVO!=null) {
				totalBind = houseListVO.getHou_info().size();
				if (totalBind < 0) {
					totalBind = 0;
				}
			} else {
				totalBind = 0;
			}
			
			if (totalBind == 0) {
				user.setXiaoquId(0l);
				user.setXiaoquName("");
				user.setProvince("");
				user.setCity("");
				user.setCountry("");
				user.setSectId("0");
				user.setCspId("0");
				user.setOfficeTel("");
				user.setTotalBind(totalBind);
				userRepository.save(user);
			}else {
				String currSectId = user.getSectId();	//当前用户所在小区。如果解绑后的房子不包含这个小区了，则要切换去其他小区
				List<HexieHouse> houseList = houseListVO.getHou_info();
				boolean hasCurrSect = false;
				for (HexieHouse hexieHouse : houseList) {
					if (currSectId.equals(hexieHouse.getSect_id())) {
						hasCurrSect = true;	//这种情况不需要切换小区
						break;
					}
				}
				if (!hasCurrSect) {
					HexieHouse hexieHouse = houseList.get(0);
					user.setXiaoquName(hexieHouse.getSect_name());
					user.setProvince(hexieHouse.getProvince_name());
					user.setCity(hexieHouse.getCity_name());
					user.setCountry(hexieHouse.getRegion_name());
					user.setSectId(hexieHouse.getSect_id());
					user.setCspId(hexieHouse.getCsp_id());
					user.setOfficeTel(hexieHouse.getOffice_tel());
					user.setTotalBind(totalBind);
					userRepository.save(user);
				}
			}
			
		} else {
			throw new BizValidateException("解绑房屋失败。");
		}
		
		//清空用户缓存
		cacheService.clearUserCache(cacheService.getCacheKey(user));
		
		return result.isSuccess();
	}
	
	@Override
	public HexieHouse getHouse(User user, String stmtId) {
		return WuyeUtil.getHouse(user, stmtId).getData();
	}
	
	@Override
	public PayWaterListVO queryPaymentList(User user, String startDate, String endDate) {
		return WuyeUtil.queryPaymentList(user, startDate, endDate).getData();
	}

	@Override
	public PaymentInfo queryPaymentDetail(User user, String waterId) {
		return WuyeUtil.queryPaymentDetail(user, waterId).getData();
	}

	@Override
	public BillListVO queryBillList(User user, String payStatus, String startDate, 
			String endDate,String currentPage, String totalCount,String house_id,String sect_id, String regionName) {
		
		String targetUrl = getRegionUrl(regionName);
		return WuyeUtil.queryBillList(user, payStatus, startDate, endDate, currentPage, totalCount,house_id, sect_id, targetUrl).getData();
	}

	@Override
	public PaymentInfo getBillDetail(User user, String stmtId, String anotherbillIds, String regionName) throws Exception {
		
		String targetUrl = getRegionUrl(regionName);
		return wuyeUtil2.getBillDetail(user, stmtId, anotherbillIds, targetUrl).getData();
	}

	@Override
	@Transactional
	public WechatPayInfo getPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception {
		
		User user = prepayRequestDTO.getUser();
		if (user.getId() == 0) {
			log.info("qrcode pay, no user id .");
		}else {
			User currUser = userRepository.findById(user.getId());
			prepayRequestDTO.setUser(currUser);
		}
		if ("1".equals(prepayRequestDTO.getPayType())) {	//银行卡支付
			String remerber = prepayRequestDTO.getRemember();
			if ("1".equals(remerber)) {	//新卡， 需要记住卡号的情况
				
				Assert.hasText(prepayRequestDTO.getCustomerName(), "持卡人姓名不能为空。");
				Assert.hasText(prepayRequestDTO.getAcctNo(), "卡号不能为空。");
				Assert.hasText(prepayRequestDTO.getCertId(), "证件号不能为空。");
				Assert.hasText(prepayRequestDTO.getPhoneNo(), "银行预留手机号不能为空。");
				
				BankCard bankCard = bankCardRepository.findByAcctNo(prepayRequestDTO.getAcctNo());
				if (bankCard == null) {
					bankCard = new BankCard();
				}
				bankCard.setAcctName(prepayRequestDTO.getCustomerName());
				bankCard.setAcctNo(prepayRequestDTO.getAcctNo());
				bankCard.setBankCode("");	//TODO 
				bankCard.setBankName("");	//TODO
				bankCard.setBranchName("");	//TODO
				bankCard.setBranchNo("");	//TODO
				bankCard.setPhoneNo(prepayRequestDTO.getPhoneNo());
				bankCard.setUserId(prepayRequestDTO.getUser().getId());
				bankCard.setUserName(prepayRequestDTO.getUser().getName());
				//支付成功回调的时候还要保存quickToken
				bankCardRepository.save(bankCard);
			} 
			if (!StringUtils.isEmpty(prepayRequestDTO.getCardId())) {	//选卡支付
				BankCard selBankCard = bankCardRepository.findById(Long.valueOf(prepayRequestDTO.getCardId())).get();
				if (StringUtils.isEmpty(selBankCard.getQuickToken())) {
					throw new BizValidateException("未绑定的银行卡。");
				}
				prepayRequestDTO.setQuickToken(selBankCard.getQuickToken());
				prepayRequestDTO.setPhoneNo(selBankCard.getPhoneNo());
			}
		}
		return wuyeUtil2.getPrePayInfo(prepayRequestDTO).getData();
	}
	
	@Override
	public WechatPayInfo getSmsPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception {
		
		User user = prepayRequestDTO.getUser();
		if (user.getId() == 0) {
			log.info("qrcode pay, no user id .");
		}
		return wuyeUtil2.getSmsPrePayInfo(prepayRequestDTO).getData();
	}
	
	@Override
	public BillListVO quickPayInfo(User user, String stmtId, String currPage, String totalCount) throws Exception {
		return wuyeUtil2.quickPayInfo(user, stmtId, currPage, totalCount).getData();
	}

	@Override
	public String queryCouponIsUsed(User user) {

		BaseResult<String> r = WuyeUtil.couponUseQuery(user);
		return r.getResult();
	}

	@Override
	public void updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id, String openid) {
		
		String key = ModelConstant.KEY_INVOICE_APPLICATIONF_FLAG + trade_water_id;
		String applied = redisTemplate.opsForValue().get(key);
		if ("1".equals(applied)) {
			throw new BizValidateException("电子发票已申请，请勿重复操作。");
		}
		BaseResult<String> r = WuyeUtil.updateInvoice(mobile, invoice_title, invoice_title_type, credit_code, trade_water_id, openid);
		if ("99".equals(r.getResult())) {
			throw new BizValidateException("网络异常，请刷新后重试。");
		}
		
		redisTemplate.opsForValue().setIfAbsent(key, "1", 1, TimeUnit.DAYS);
	}

	@Override
	public InvoiceInfo getInvoiceByTradeId(String trade_water_id) {
		BaseResult<InvoiceInfo> baseResult = WuyeUtil.getInvoiceInfo(trade_water_id);
		if (!"00".equals(baseResult.getResult())) {
			throw new BizValidateException(baseResult.getMessage());
		}
		return baseResult.getData();
	}
	
	@Override
	public CellListVO querySectHeXieList(User user, String sect_id, String build_id,
			String unit_id, String data_type, String region_name) {
		try {
			
			String targetUrl = getRegionUrl(region_name);
			return WuyeUtil.getMngHeXieList(user, sect_id, build_id, unit_id, data_type, targetUrl).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	//根据名称模糊查询合协社区小区列表
	@Override
	public CellListVO getVagueSectByName(User user, String sectName, String regionName, String queryAppid) throws Exception {
		log.info("getVagueSectByName, session user : {}", user);
		return wuyeUtil2.getVagueSectByName(user, sectName, regionName, queryAppid).getData();
	}

	@Override
	public HexieUser bindHouseNoStmt(User user, String houseId, String area) throws Exception {
		
		BaseResult<HexieUser> r= wuyeUtil2.bindHouseNoStmt(user, houseId, area);
		if("04".equals(r.getResult())){
			throw new BizValidateException(4, "当前用户已经认领该房屋!");
		}
		if ("05".equals(r.getResult())) {
			throw new BizValidateException(5, "用户当前绑定房屋与已绑定房屋不属于同个小区，暂不支持此功能。");
		}
		if("01".equals(r.getResult())) {
			throw new BizValidateException(1, "账户不存在！");
		}
		if("06".equals(r.getResult())) {
			throw new BizValidateException(6, "面积验证错误，允许误差在±1平方米以内。");
		}
		if("02".equals(r.getResult())) {
			throw new BizValidateException(2, "房屋不存在！");
		}
		cacheService.clearUserCache(cacheService.getCacheKey(user));
		return r.getData();
	}

	/**
	 * 这种情况需要user中有openid，适合公众号的用户
	 * @param user 公众号用户user
	 * @param u 用户绑定房屋的信息
	 */
	@Override
	@Transactional
	public void setDefaultAddress(User user, HexieUser u) {
		
		HexieAddress hexieAddress = new HexieAddress();
		BeanUtils.copyProperties(u, hexieAddress);
		
		addressService.updateDefaultAddress(user, hexieAddress);
		Integer totalBind = user.getTotalBind();
		if (totalBind == null) {
			totalBind = 0;
		}
		if (!StringUtils.isEmpty(u.getTotal_bind())) {
			if (u.getTotal_bind() > 0) {
				totalBind = u.getTotal_bind();	//如果值不为空，说明是跑批程序返回回来的，直接取值即可，如果值是空，走下面的else累加即可
			}
		}
		if (totalBind == 0) {
			totalBind = totalBind + 1;
		}
		user.setTotalBind(totalBind);
		user.setXiaoquName(u.getSect_name());
		user.setProvince(u.getProvince_name());
		user.setCity(u.getCity_name());
		user.setCounty(u.getRegion_name());
		user.setSectId(u.getSect_id());	
		user.setCspId(u.getCsp_id());
		user.setOfficeTel(u.getOffice_tel());
		userRepository.save(user);
		
		//清空用户缓存
		cacheService.clearUserCache(cacheService.getCacheKey(user));
	}
	
	@Override
	public BillListVO queryBillListStd(User user, String startDate, String endDate, String house_id, String regionName) throws Exception {
		
		return wuyeUtil2.queryBillList(user, startDate, endDate, house_id, regionName).getData();
	}
	
	/**
	 * 通过物业交易ID异步绑定房屋
	 * @param bindSwitch
	 * @param user
	 * @param tradeWaterId
	 */
	@Override
	public BillStartDate getBillStartDateSDO(User user, String house_id, String regionName) {
		
		String targetUrl = getRegionUrl(regionName);
		try {
			return WuyeUtil.getBillStartDateSDO(user,house_id,targetUrl).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
		
	}
	
	@Override
	public HexieUser bindHouse(User user, String stmtId, String houseId) {
		
		BaseResult<HexieUser> r = WuyeUtil.bindHouse(user, stmtId, houseId);
		if("04".equals(r.getResult())){
			throw new BizValidateException(4, "当前用户已经认领该房屋!");
		}
		if ("05".equals(r.getResult())) {
			throw new BizValidateException(5, "用户当前绑定房屋与已绑定房屋不属于同个小区，暂不支持此功能。");
		}
		if("01".equals(r.getResult())) {
			throw new BizValidateException(1, "账户不存在！");
		}
		if("06".equals(r.getResult())) {
			throw new BizValidateException(6, "面积验证错误，允许误差在±1平方米以内。");
		}
		if("02".equals(r.getResult())) {
			throw new BizValidateException(2, "房屋不存在！");
		}
		return r.getData();
	}
	
	/**
	 * 通过物业交易ID异步绑定房屋
	 * @param bindSwitch
	 * @param user
	 * @param tradeWaterId
	 * @param bindType 4:交易绑定，5开票绑定
	 */
	@Override
	public void bindHouseByTradeAsync(String bindSwitch, User user, String tradeWaterId, String bindType) {
		
		Assert.hasText(tradeWaterId, "物业交易ID不能为空。 ");
		
		if ("1".equals(bindSwitch)) {
			int retryTimes = 0;
			boolean isSuccess = false;
			
			while(!isSuccess && retryTimes < 3) {
				try {
					Thread.sleep(3000);	//休息3秒，让积分的线程先跑完。
					BindHouseQueue bindHouseQueue = new BindHouseQueue();
					bindHouseQueue.setUser(user);
					bindHouseQueue.setTradeWaterId(tradeWaterId);
					bindHouseQueue.setBindType(bindType);
					
					ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
					String value = objectMapper.writeValueAsString(bindHouseQueue);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_BIND_HOUSE_QUEUE, value);
					isSuccess = true;
					
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					retryTimes++;
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		
	}
	
	/**
	 * 根据户号查询房屋
	 * @param user
	 * @param verNo
	 * @return {@link HexieHouse}
	 */
	@Override
	public HexieHouse getHouseByVerNo(User user, String verNo) {
		
		if (StringUtils.isEmpty(verNo)) {
			throw new BizValidateException("户号不能为空。");
		}
		verNo = verNo.trim();
		if (verNo.length() != 12) {
			throw new BizValidateException("请输入正确的户号。");
		}

		verNo = verNo.replaceAll(" ", "");
		return WuyeUtil.getHouseByVerNo(user, verNo).getData();
	}
	
	/**
	 * 获取需要发送的链接地址
	 * @param regionName
	 * @return
	 */
	private String getRegionUrl(String regionName) {
		
		String targetUrl = "";
		if (!StringUtils.isEmpty(regionName)) {
			RegionUrl regionurl = locationService.getRegionUrlByName(regionName);
			if (regionurl == null) {
				log.info("regionName : " + regionName + " 未能找到相应的配置链接。");
			}else {
				targetUrl = regionurl.getRegionUrl();
			}
			
		}
		return targetUrl;
		
	}
	
	@Async
	@Override
	public void addCouponsFromSeed(User user, List<CouponCombination> list) {

		try {

			for (int i = 0; i < list.size(); i++) {
				couponService.addCouponFromSeed(list.get(i).getSeedStr(), user);
			}

		} catch (Exception e) {

			log.error("add Coupons for wuye Pay : " + e.getMessage());
		}

	}

	@Override
	public Discounts getDiscounts(DiscountViewRequestDTO discountViewRequestDTO) throws Exception {
		
		Discounts discountDetail = wuyeUtil2.getDiscounts(discountViewRequestDTO).getData();
		return discountDetail;
	
	}
	
	@Override
	public String queyrOrder(User user, String orderNo) throws Exception {
		return wuyeUtil2.queryOrder(user, orderNo).getData();
	}
	
	@Override
	public String getPaySmsCode(User user, String cardId) throws Exception {
	
		Assert.hasText(cardId, "卡ID不能为空。");
		BankCard bankCard = bankCardRepository.findById(Long.valueOf(cardId)).get();
		return wuyeUtil2.getPaySmsCode(user, bankCard).getData();
	}
	
	@Override
	public WechatPayInfo requestOtherPay(OtherPayDTO otherPayDTO) throws Exception {

		log.info("otherPayDTO : " + otherPayDTO);
		return wuyeUtil2.requestOtherPay(otherPayDTO).getData();
	}

	@Override
	public QrCodePayService getQrCodePayService(User user) {
		
		long begin = System.currentTimeMillis();
		
		if (StringUtils.isEmpty(user.getTel())) {
			user = userRepository.findById(user.getId());
		}
		QrCodePayService service = new QrCodePayService();
		try {
			service = wuyeUtil2.getQrCodePayService(user).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		List<ServiceOperator> ops = serviceOperatorRepository.findByTypeAndUserId(ModelConstant.SERVICE_OPER_TYPE_SERVICE, user.getId());
		ServiceOperator serviceOperator = null;
		List<PayCfg> serviceList = new ArrayList<>();
		if (ops!=null && !ops.isEmpty()) {
			log.info("ops count : " + ops.size());
			serviceOperator = ops.get(0);
			if (serviceOperator != null) {
				String subTypes = serviceOperator.getSubType();
				log.info("subTypes : " + subTypes);
				if (!StringUtils.isEmpty(subTypes)) {
					Object[]sTypes = subTypes.split(",");
					Collection<Object> collection = Arrays.asList(sTypes);
				
					long end = System.currentTimeMillis();
					log.info("getQrCodePayService before : " + (end - begin));
					
					List<Object> objList = redisTemplate.opsForHash().multiGet(ModelConstant.KEY_CUSTOM_SERVICE, collection);

					if (objList.size() > 0) {
						for (int i = 0; i < sTypes.length; i++) {
							
							log.info("service name : "  + objList.get(i));
							if (StringUtils.isEmpty(objList.get(i))) {
								log.info("service id : " + sTypes[i] + ", cannot find related service name ! will skip !");
								continue;
							}
							PayCfg payCfg = new PayCfg();
							payCfg.setServiceTypeCn((String) objList.get(i));
							payCfg.setServiceId((String)sTypes[i]);
							payCfg.setServiceType("1");
							serviceList.add(payCfg);
						}
					}
					
					end = System.currentTimeMillis();
					log.info("getQrCodePayService loop time : " + (end - begin));
					
				}
				
			}
		}
		List<PayCfg> list = service.getServiceList();
		if (list==null) {
			list = new ArrayList<>();
		}
		list.addAll(serviceList);
		return service;
		
	}
	
	@Override
	public byte[] getQrCode(User user, String qrCodeId) throws Exception {
		
		Assert.hasText(qrCodeId, "二维码ID不能为空。");
		return wuyeUtil2.getQrCode(user, qrCodeId).getData();
		
	}
	
	@Override
	public void signInOut(SignInOutDTO signInOutDTO) throws Exception {
		
		wuyeUtil2.signInOut(signInOutDTO);
		
	}

	@Override
	public CellListVO querySectHeXieList(GetCellDTO getCellDTO) throws Exception {
		
		return wuyeUtil2.getMngHeXieList(getCellDTO).getData();
	}
	
	@Override
	public EReceipt getEReceipt(User user, String tradeWaterId, String sysSource) throws Exception {
		
		return wuyeUtil2.getEReceipt(user, tradeWaterId, sysSource).getData();
	}
	
	@Override
	public CellListVO getCellList(User user, String sectId, String cellAddr) throws Exception {
		
		return wuyeUtil2.queryCellAddr(user, sectId, cellAddr).getData();
	}

	@Override
	public WechatResponse scanEvent4Invoice(BaseEventDTO baseEventDTO) {
		
		return gotongService.sendMsg4ApplicationInvoice(baseEventDTO);
	}
	
	@Deprecated
	@Override
	public WechatResponse scanEvent4Receipt(BaseEventDTO baseEventDTO) {
		
		return gotongService.sendMsg4ApplicationReceipt(baseEventDTO);
	}
	
	/**
	 * 到账消息推送(给物业配置的工作人推送)
	 */
	@Override
	public void registerAndBind(User user, String tradeWaterId, String bindType) {
		
		if (user == null) {
			log.info("user is null, will return ! ");
			return;
		}
		int retryTimes = 0;
		boolean isSuccess = false;
		
		while(!isSuccess && retryTimes < 3) {
			try {
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				
				BindHouseQueue bindHouseQueue = new BindHouseQueue();
				bindHouseQueue.setUser(user);
				bindHouseQueue.setTradeWaterId(tradeWaterId);
				bindHouseQueue.setBindType(bindType);
				String value = objectMapper.writeValueAsString(bindHouseQueue);
				redisTemplate.opsForList().rightPush(ModelConstant.KEY_REGISER_AND_BIND_QUEUE, value);
				isSuccess = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				retryTimes++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					log.error(e.getMessage(), e);
				}
			}
		}
		
	}
	
	/**
	 * 获取当前用户申请过的发票
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<InvoiceDetail> getInvoice(User user, String currPage) throws Exception {
		
		return wuyeUtil2.queryInvoiceByUser(user, currPage).getData();
		
	}
	
	/**
	 * 获取当前用户申请过的发票
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<InvoiceDetail> getInvoiceByTrade(User user, String tradeWaterId) throws Exception {
		return wuyeUtil2.queryInvoiceByTrade(user, tradeWaterId).getData();
	}
	
	/**
	 * 获取远程pdf
	 * @param remoteAddr
	 * @return
	 * @throws Exception 
	 */
	@Override
	public byte[] getInvoicePdf(String remoteAddr) throws Exception {
		if (StringUtils.isEmpty(remoteAddr)) {
			throw new BizValidateException("未能获取到pdf文件，请稍后再试");
		}
		return fileService.downloadFileFromRemote(remoteAddr);
	}
	
	/**
	 * 获取催缴短信用户欠费账单
	 * @param user
	 * @param queryFeeSmsBillReq
	 * @return
	 * @throws Exception
	 */
	@Override
	public PaymentInfo getFeeSmsBill(User user, QueryFeeSmsBillReq queryFeeSmsBillReq) throws Exception {
		
		return wuyeUtil2.getFeeSmsBill(user, queryFeeSmsBillReq).getData();
	}
	
	/**
	 * 获取催缴短信用付费二维码
	 * @param user
	 * @param queryFeeSmsBillReq
	 * @return
	 * @throws Exception
	 */
	@Override
	public Discounts getFeeSmsPayQrCode(User user, QueryFeeSmsBillReq queryFeeSmsBillReq) throws Exception {
		
		return wuyeUtil2.getFeeSmsPayQrCode(user, queryFeeSmsBillReq).getData();
	}
	
	@Override
	public void applyReceipt(User user, ReceiptApplicationReq receiptApplicationReq) throws Exception {
		
//		String tradeWaterId = receiptApplicationReq.getTradeWaterId();
//		String userSysCode = SystemConfigServiceImpl.getSysMap().get(receiptApplicationReq.getAppid());	//是否_guizhou
//		String sysSource = "_sh";
//		if ("_guizhou".equals(userSysCode)) {
//			sysSource = "_guizhou";
//		}
//		String key = ModelConstant.KEY_RECEIPT_APPLICATIONF_FLAG + sysSource + ":" +tradeWaterId;
//		
//		String applied = redisTemplate.opsForValue().get(key);
//		if ("1".equals(applied)) {
//			throw new BizValidateException("电子收据已申请，请勿重复操作。");
//		}
		BaseResult<String> r = wuyeUtil2.applyReceipt(user, receiptApplicationReq);
		if ("99".equals(r.getResult())) {
			throw new BizValidateException(r.getMessage());
		}
//		redisTemplate.opsForValue().setIfAbsent(key, "1", 30, TimeUnit.DAYS);
	}
	
	@Override
	public ReceiptInfo getReceipt(String appid, String receiptId) throws Exception {
		
		String region = "";
		String userSysCode = SystemConfigServiceImpl.getSysMap().get(appid);	//是否_guizhou
		String sysSource = "_sh";
		if ("_guizhou".equals(userSysCode)) {
			sysSource = "_guizhou";
		}
		if ("guizhou".equals(sysSource)) {
			region = "贵州省";
		} else {
			region = "";
		}
		return wuyeUtil2.getReceipt(receiptId, sysSource, region).getData();
		
	}

	@Override
	public List<Receipt> getReceiptList(User user, String page) throws Exception {
		
		return wuyeUtil2.getReceiptList(user, page).getData();
	}
	
	@Override
	public List<HexieHouse> bindHouse4NewLionUser(User user, String mobile) throws Exception {
		
		List<HexieHouse> hexieHouses = null;
		List<NewLionUser> houList = newLionUserRepository.findByMobile(mobile);
		if (houList != null) {
			boolean flag = false;	//是否上线小区
			for (NewLionUser newLionUser : houList) {
				if (!StringUtils.isEmpty(newLionUser.getFdSectId())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				BaseResult<List<HexieHouse>> baseResult = wuyeUtil2.bindHouse4NewLionUser(user, mobile);
				if (baseResult.isSuccess()) {
					hexieHouses = baseResult.getData();
					if (hexieHouses != null && hexieHouses.size() > 0) {
						for (HexieHouse hexieHouse : hexieHouses) {
							HexieUser hexieUser = new HexieUser();
							BeanUtils.copyProperties(hexieHouse, hexieUser);
							setDefaultAddress(user, hexieUser);	//里面已经开了事务，外面不需要。跨类调，事务生效
							//里面的清除缓存不会生效，在外面调一下
							cacheService.clearUserCache(cacheService.getCacheKey(user));
						}
					}
					
				}
			}
		}
		return hexieHouses;
	}
	
	@Override
	public HexieUser queryHouseById(User user, String houseId) throws Exception {
		return wuyeUtil2.queryHouseById(user, houseId).getData();
	}

	@Override
	public SectInfo querySectById(User user, String sectId) throws Exception {
		return wuyeUtil2.querySectById(user, sectId).getData();
	}
	
	@Override
	public AlipayMarketingConsult queryAlipayMarketingConsult(User user, QueryAlipayConsultRequest queryAlipayConsultRequest) throws Exception {
		if (StringUtils.isEmpty(queryAlipayConsultRequest.getAliAppId())) {
			queryAlipayConsultRequest.setAliAppId(ConstantAlipay.APPID);
		}
		return wuyeUtil2.queryAlipayMarketingConsult(user, queryAlipayConsultRequest).getData();
	}

}