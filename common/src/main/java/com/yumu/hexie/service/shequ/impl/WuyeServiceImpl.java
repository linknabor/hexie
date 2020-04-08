package com.yumu.hexie.service.shequ.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.BillStartDate;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.BindHouseDTO;
import com.yumu.hexie.integration.wuye.vo.HexieAddress;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.AddPointQueue;
import com.yumu.hexie.vo.BindHouseQueue;

@Service("wuyeService")
public class WuyeServiceImpl implements WuyeService {
	
	private static final Logger log = LoggerFactory.getLogger(WuyeServiceImpl.class);
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private LocationService locationService;
	
	@Autowired
	private PointService pointService;
	
	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public HouseListVO queryHouse(User user) {
		return WuyeUtil.queryHouse(user).getData();
	}

	@Override
	@Transactional
	public boolean deleteHouse(User user, String houseId) {
		
		User curruser = userService.getById(user.getId());
		BaseResult<String> result = WuyeUtil.deleteHouse(curruser, houseId);
		if (result.isSuccess()) {
			// 添加电话到user表
			String data = result.getData();
			int totalBind = 0;
			if (!StringUtils.isEmpty(data)) {
				totalBind = Integer.valueOf(data);
			}
			if (totalBind < 0) {
				totalBind = 0;
			}
			if (totalBind == 0) {
				userRepository.updateUserByHouse(0l, "", totalBind, "", "", "", "0", "0", "", curruser.getId());
			}else {
				userRepository.updateUserTotalBind(totalBind, curruser.getId());
			}
			
		} else {
			throw new BizValidateException("解绑房屋失败。");
		}
		return result.isSuccess();
	}
	
	@Override
	public HexieHouse getHouse(User user, String stmtId) {
		return WuyeUtil.getHouse(user, stmtId).getData();
	}
	
	@Override
	public HexieUser userLogin(User user) {
		return WuyeUtil.userLogin(user).getData();
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
	public PaymentInfo getBillDetail(User user, String stmtId, String anotherbillIds, String regionName) {
		
		String targetUrl = getRegionUrl(regionName);
		return WuyeUtil.getBillDetail(user, stmtId, anotherbillIds, targetUrl).getData();
	}

	@Override
	public WechatPayInfo getPrePayInfo(User user, String billId,
			String stmtId, String couponUnit, String couponNum, 
			String couponId,String mianBill,String mianAmt, String reduceAmt, String fee_mianBill,String fee_mianAmt,
			String invoice_title_type, String credit_code, String invoice_title,String regionname) throws Exception {
		
		String targetUrl = getRegionUrl(regionname);
		return WuyeUtil.getPrePayInfo(user, billId, stmtId, couponUnit, couponNum, couponId,mianBill,mianAmt, reduceAmt, fee_mianBill,fee_mianAmt,
				invoice_title_type, credit_code, invoice_title,targetUrl)
				.getData();
	}
	
	@Override
	public WechatPayInfo getOtherPrePayInfo(User user, String houseId, String start_date, String end_date,
			String couponUnit, String couponNum, String couponId, String mianBill, String mianAmt,
			String reduceAmt, String invoice_title_type, String credit_code, String invoice_title,String regionname)
			throws Exception {
		
		String targetUrl = getRegionUrl(regionname);
		return WuyeUtil.getOtherPrePayInfo(user, houseId, start_date,end_date, couponUnit, couponNum, couponId,mianBill,mianAmt, reduceAmt, 
				invoice_title_type, credit_code, invoice_title,targetUrl)
				.getData();
	}

	/**
	 * 支付完成后的一些操作
	 * 步骤：
	 *  1.有红包的更新红包状态，
	 *	2.绑定缴费房屋（bindStich==1，需要远程请求，双边事务），
	 *	3.+芝麻，
	 *
	 *其中1,2实时完成,3可异步完成(队列)。
	 *
	 */
	@Transactional
	@Override
	public void noticePayed(User user, String billId, String tradeWaterId, 
			String couponId, String feePrice, String bindSwitch) {
		
		//1.更新红包状态
		if (!StringUtils.isEmpty(couponId)) {
			couponService.comsume(feePrice, Long.valueOf(couponId));
		}
		//2.添加芝麻积分
		if (systemConfigService.isCardServiceAvailable(user.getAppId())) {
			String pointKey = "wuyePay-" + tradeWaterId;
			addPointAsync(user, feePrice, pointKey);
		}else {
			String pointKey = "zhima-bill-" + user.getId() + "-" + billId;
			pointService.updatePoint(user, "10", pointKey);
		}
		//3.绑定所缴纳物业费的房屋
		bindHouseByTradeAsync(bindSwitch, user, tradeWaterId);
		
	}

	@Override
	public BillListVO quickPayInfo(User user, String stmtId, String currPage, String totalCount) {
		return WuyeUtil.quickPayInfo(user, stmtId, currPage, totalCount).getData();
	}

	@Override
	public String queryCouponIsUsed(User user) {

		BaseResult<String> r = WuyeUtil.couponUseQuery(user);
		return r.getResult();
	}

	@Override
	public String updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id) {
		BaseResult<String> r = WuyeUtil.updateInvoice(mobile, invoice_title, invoice_title_type, credit_code, trade_water_id);
		return r.getResult();
	}

	@Override
	public InvoiceInfo getInvoiceByTradeId(String trade_water_id) {
		return WuyeUtil.getInvoiceInfo(trade_water_id).getData();
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
	public CellListVO getVagueSectByName(User user, String sect_name, String region_name) {
		
		try {
			String targetUrl = getRegionUrl(region_name);
			return WuyeUtil.getVagueSectByName(user, sect_name, targetUrl).getData();
      
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public BindHouseDTO bindHouseNoStmt(User user, String houseId, String area) {
		
		User currUser = userService.getById(user.getId());
		BaseResult<HexieUser> r= WuyeUtil.bindHouseNoStmt(currUser, houseId, area);
		if("04".equals(r.getResult())){
			throw new BizValidateException("当前用户已经认领该房屋!");
		}
		if ("05".equals(r.getResult())) {
			throw new BizValidateException("用户当前绑定房屋与已绑定房屋不属于同个小区，暂不支持此功能。");
		}
		if("01".equals(r.getResult())) {
			throw new BizValidateException("账户不存在！");
		}
		if("06".equals(r.getResult())) {
			throw new BizValidateException("建筑面积允许误差在±1平方米以内！");
		}
		BindHouseDTO dto = new BindHouseDTO();
		dto.setHexieUser(r.getData());
		dto.setUser(currUser);
		return dto;
	}

	@Override
	@Transactional
	public User setDefaultAddress(User user, HexieUser u) {

		HexieAddress hexieAddress = new HexieAddress();
		BeanUtils.copyProperties(u, hexieAddress);
		User currUser = userService.getById(user.getId());
		
		addressService.updateDefaultAddress(currUser, hexieAddress);
		Integer totalBind = currUser.getTotalBind();
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
		currUser.setTotalBind(totalBind);
		currUser.setXiaoquName(u.getSect_name());
		currUser.setProvince(u.getProvince_name());
		currUser.setCity(u.getCity_name());
		currUser.setCounty(u.getRegion_name());
		currUser.setSectId(u.getSect_id());	
		currUser.setCspId(u.getCsp_id());
		currUser.setOfficeTel(u.getOffice_tel());
		userRepository.updateUserByHouse(currUser.getXiaoquId(), currUser.getXiaoquName(), 
				currUser.getTotalBind(), currUser.getProvince(), currUser.getCity(), currUser.getCountry(), 
				currUser.getSectId(), currUser.getCspId(), currUser.getOfficeTel(), currUser.getId());
		
		return currUser;
		
	}

	@Override
	public BillListVO queryBillListStd(User user, String startDate, String endDate, String house_id, 
			String sect_id, String regionName) {
		
		String targetUrl = getRegionUrl(regionName);
		return WuyeUtil.queryBillList(user, startDate, endDate,house_id,sect_id,targetUrl).getData();
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
	public BindHouseDTO bindHouse(User user, String stmtId, String houseId) {
		
		User currUser = userService.getById(user.getId());
		BaseResult<HexieUser> r= WuyeUtil.bindHouse(currUser, stmtId, houseId);
		if("04".equals(r.getResult())){
			throw new BizValidateException("当前用户已经认领该房屋!");
		}
		if ("05".equals(r.getResult())) {
			throw new BizValidateException("用户当前绑定房屋与已绑定房屋不属于同个小区，暂不支持此功能。");
		}
		if("01".equals(r.getResult())) {
			throw new BizValidateException("账户不存在！");
		}
		BindHouseDTO dto = new BindHouseDTO();
		dto.setHexieUser(r.getData());
		dto.setUser(currUser);
		return dto;
	}
	
	/**
	 * 通过物业交易ID异步绑定房屋
	 * @param bindSwitch
	 * @param user
	 * @param tradeWaterId
	 */
	@Override
	public void bindHouseByTradeAsync(String bindSwitch, User user, String tradeWaterId) {
		
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
	
	/**
	 * 异步添加积分
	 * @param user
	 * @param feePrice
	 * @param pointKey
	 */
	public void addPointAsync(User user, String feePrice, String pointKey) {
		
		Assert.hasText(feePrice, "缴费金额为空。");

		//防止重复添加卡券积分，半小时内只能提交队列一次。出队时也会校验重复性
		Long increment = redisTemplate.opsForValue().increment(pointKey, 1);
		log.info("addPoint, key[" + pointKey + "], add point[" + feePrice + "], increment : " + increment);
		if (increment == 1) {
			int retryTimes = 0;
			boolean isSuccess = false;
			while(!isSuccess && retryTimes < 3) {
				
				try {
					AddPointQueue addPointQueue = new AddPointQueue();
					addPointQueue.setUser(user);
					addPointQueue.setPoint(feePrice);
					addPointQueue.setKey(pointKey);
					
					ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
					String value = objectMapper.writeValueAsString(addPointQueue);
					redisTemplate.opsForList().rightPush(ModelConstant.KEY_ADD_POINT_QUEUE, value);
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
		redisTemplate.expire(pointKey, 24, TimeUnit.HOURS);	//24小时过期
	
	}

}