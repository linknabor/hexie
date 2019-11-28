package com.yumu.hexie.service.shequ.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import com.yumu.hexie.integration.wuye.vo.HexieAddress;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.region.RegionUrl;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.WuyeQueueTask;
import com.yumu.hexie.service.shequ.WuyeService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.CouponService;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.BindHouseQueue;

@Service("wuyeService")
public class WuyeServiceImpl implements WuyeService {
	
	private static final Logger log = LoggerFactory.getLogger(WuyeServiceImpl.class);
	
	private static Map<String,Long> map=null;
	
	@Autowired
	private RegionRepository regionRepository;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PointService pointService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private WuyeQueueTask wuyeQueueTask;
	
	@Autowired
	private LocationService locationService;
	
	@Override
	public HouseListVO queryHouse(User user) {
		return WuyeUtil.queryHouse(user).getData();
	}

	@PostConstruct
	public void init() {
		getNeedRegion();
		wuyeQueueTask.bindHouseByQueue();
	}
	
	@Override
	public BaseResult<String> deleteHouse(User user, String houseId) {
		BaseResult<String> r = WuyeUtil.deleteHouse(user, houseId);
		return r;
	}
	
	@Override
	public HexieHouse getHouse(User user, String stmtId) {
		return WuyeUtil.getHouse(user, stmtId).getData();
	}
	
	@Override
	public HexieHouse getHouse(String userId, String stmtId, String house_id) {
		// TODO Auto-generated method stub
		return null;
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
			String endDate,String currentPage, String totalCount,String house_id,String sect_id) {
		return WuyeUtil.queryBillList(user, payStatus, startDate, endDate, currentPage, totalCount,house_id,sect_id).getData();
	}

	@Override
	public PaymentInfo getBillDetail(User user, String stmtId, String anotherbillIds) {
		return WuyeUtil.getBillDetail(user, stmtId, anotherbillIds).getData();
	}

	@Override
	public WechatPayInfo getPrePayInfo(User user, String billId,
			String stmtId, String couponUnit, String couponNum, 
			String couponId,String mianBill,String mianAmt, String reduceAmt, 
			String invoice_title_type, String credit_code, String invoice_title,String regionname) throws Exception {
		
		RegionUrl regionurl = locationService.getRegionUrlByName(regionname);
		return WuyeUtil.getPrePayInfo(user, billId, stmtId, couponUnit, couponNum, couponId,mianBill,mianAmt, reduceAmt, 
				invoice_title_type, credit_code, invoice_title,regionurl.getRegionUrl())
				.getData();
	}
	
	@Override
	public WechatPayInfo getOtherPrePayInfo(User user, String houseId, String start_date, String end_date,
			String couponUnit, String couponNum, String couponId, String mianBill, String mianAmt,
			String reduceAmt, String invoice_title_type, String credit_code, String invoice_title,String regionname)
			throws Exception {
		RegionUrl regionurl = locationService.getRegionUrlByName(regionname);
		return WuyeUtil.getOtherPrePayInfo(user, houseId, start_date,end_date, couponUnit, couponNum, couponId,mianBill,mianAmt, reduceAmt, 
				invoice_title_type, credit_code, invoice_title,regionurl.getRegionUrl())
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
		String pointKey = "zhima-bill-" + user.getId() + "-" + billId;
		pointService.addZhima(user, 10, pointKey);
		
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
	public String updateInvoice(User user, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id) {
		BaseResult<String> r = WuyeUtil.updateInvoice(user, invoice_title, invoice_title_type, credit_code, trade_water_id);
		return r.getResult();
	}

	@Override
	public InvoiceInfo getInvoiceByTradeId(User user, String trade_water_id) {
		return WuyeUtil.getInvoiceInfo(user, trade_water_id).getData();
	}
	
	@Override
	public CellListVO querySectHeXieList(User user, String sect_id, String build_id,
			String unit_id, String data_type) {
		try {
			return WuyeUtil.getMngHeXieList(user, sect_id, build_id, unit_id, data_type).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	//根据名称模糊查询合协社区小区列表
	@Override
	public CellListVO getVagueSectByName(User user, String sect_name) {
		try {
			BaseResult<CellListVO> s = WuyeUtil.getVagueSectByName(user, sect_name);
			log.info(s.getResult());
			return WuyeUtil.getVagueSectByName(user, sect_name).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public HexieUser bindHouseNoStmt(User user, String houseId, String area) {
		
		BaseResult<HexieUser> r= WuyeUtil.bindHouseNoStmt(user, houseId, area);
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
		return r.getData();
	}

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
			totalBind = u.getTotal_bind();	//如果值不为空，说明是跑批程序返回回来的，直接取值即可，如果值是空，走下面的else累加即可
		}else {
			totalBind = totalBind+1;
		}
		
		user.setTotalBind(totalBind);
		user.setXiaoquName(u.getSect_name());
		user.setProvince(u.getProvince_name());
		user.setCity(u.getCity_name());
		user.setCounty(u.getRegion_name());
		user.setSectId(u.getSect_id());	
		user.setCspId(u.getCsp_id());
		user.setOfficeTel(u.getOffice_tel());
		userService.save(user);
		
	}

	public void getNeedRegion(){
		
		try {
			if(map==null){
				map=new HashMap<>();
				List<Region>  regionList=regionRepository.findNeedRegion();
				for (Region region : regionList) {
					map.put(region.getName(), region.getId());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public BillListVO queryBillListStd(User user, String startDate, String endDate, String house_id, 
			String sect_id, String regionName) {
		RegionUrl regionurl = locationService.getRegionUrlByName(regionName);
		return WuyeUtil.queryBillList(user, startDate, endDate,house_id,sect_id,regionurl.getRegionUrl()).getData();
	}
	
	/**
	 * 通过物业交易ID异步绑定房屋
	 * @param bindSwitch
	 * @param user
	 * @param tradeWaterId
	 */
	@Override
	public BillStartDate getBillStartDateSDO(User user, String house_id, String regionName) {
		RegionUrl regionurl = locationService.getRegionUrlByName(regionName);
		try {
			return WuyeUtil.getBillStartDateSDO(user,house_id,regionurl.getRegionUrl()).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
		
	}



	
	@Override
	public HexieUser bindHouse(User user, String stmtId, String houseId) {
		BaseResult<HexieUser> r= WuyeUtil.bindHouse(user, stmtId, houseId);
		if("04".equals(r.getResult())){
			throw new BizValidateException("当前用户已经认领该房屋!");
		}
		if ("05".equals(r.getResult())) {
			throw new BizValidateException("用户当前绑定房屋与已绑定房屋不属于同个小区，暂不支持此功能。");
		}
		if("01".equals(r.getResult())) {
			throw new BizValidateException("账户不存在！");
		}
		return r.getData();
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
				}
			}
		}
		
	}
	
}