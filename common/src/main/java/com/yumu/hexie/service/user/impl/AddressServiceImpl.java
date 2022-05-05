package com.yumu.hexie.service.user.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.amap.AmapUtil;
import com.yumu.hexie.integration.amap.req.DataCreateReq;
import com.yumu.hexie.integration.amap.resp.DataCreateResp;
import com.yumu.hexie.integration.eshop.resp.RgroupRegionsVO;
import com.yumu.hexie.integration.eshop.service.EshopUtil;
import com.yumu.hexie.integration.wuye.vo.HexieAddress;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.AmapAddress;
import com.yumu.hexie.model.distribution.region.AmapAddressRepository;
import com.yumu.hexie.model.distribution.region.City;
import com.yumu.hexie.model.distribution.region.CityRepository;
import com.yumu.hexie.model.distribution.region.County;
import com.yumu.hexie.model.distribution.region.CountyRepository;
import com.yumu.hexie.model.distribution.region.Province;
import com.yumu.hexie.model.distribution.region.ProvinceRepository;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.RegionService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.req.AddressReq;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);
    
    private static Map<String,Long> map = null;
    
    @Inject
    private UserService userService;
    @Inject
    private AddressRepository addressRepository;
    @Inject
    private AmapAddressRepository amapAddressRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private RegionService regionService;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CountyRepository countyRepository;
    @Autowired
    private RgroupAreaItemRepository rgroupAreaItemRepository;
    @Autowired
    private EshopUtil eshopUtil;
    
    @Value("${mainServer}")
    private Boolean mainServer;
    
    @PostConstruct
	public void init() {

    	if (mainServer) {	//BK程序不跑下面的队列轮询
    		return;
    	}
		if(map == null){
			getNeedRegion();
		}
	}
    
    public void getNeedRegion(){
		
		if(map==null){
			map=new HashMap<>();
			List<Region>  regionList=regionRepository.findNeedRegion();
			for (Region region : regionList) {
				map.put(region.getName(), region.getId());
			}
		}
	}
    
    @Override
    public Address addAddress(AddressReq addressReq) {
        log.info("添加地址");
        Region xiaoqu;
        Address address = addressReq.getId() == null ||  addressReq.getId()==0 
                ? new Address() : addressRepository.findById(addressReq.getId().longValue());
        if(StringUtil.isNotEmpty(addressReq.getXiaoquName())) {
            List<Region> xiaoqus = regionRepository.findAllByParentIdAndName(addressReq.getCountyId(),addressReq.getXiaoquName());
            //FIXME 以小区名为准而非小区ID
            if(xiaoqus == null||xiaoqus.size()== 0 ){
                Region county = regionRepository.findById(addressReq.getCountyId());
                xiaoqu = new Region(county.getId(), county.getName(), addressReq.getXiaoquName());
                xiaoqu.setLatitude(0d);
                xiaoqu.setLongitude(0d);
                xiaoqu = regionRepository.save(xiaoqu);
            } else {
                xiaoqu = xiaoqus.get(xiaoqus.size() - 1);
            }
            BeanUtils.copyProperties(addressReq, address);
            address.setXiaoquId(xiaoqu.getId());
            Region county = regionRepository.findById(address.getCountyId());
            Region city = regionRepository.findById(county.getParentId());
            Region province = regionRepository.findById(city.getParentId());
            address.setCountyId(county.getId());
            address.setCounty(county.getName());
            address.setCity(city.getName());
            address.setCityId(city.getId());
            address.setProvince(province.getName());
            address.setProvinceId(province.getId());
            address = addressRepository.save(address);

            List<Address> addrs = addressRepository.findAllByUserId(address.getUserId());
            if(getDefaultAddr(addrs) == null) {
                address.setMain(true);
                User user = userService.getById(address.getUserId());
                configDefaultAddress(user, address.getId());
            }
        } else {
            throw new BizValidateException("地址所在小区信息未填写！");
        }

        return address;
    }
    
    /**
     * 团购添加地址
     */
    @Override
    @Transactional
    public Address addAddress4Rgroup(User user, AddressReq addressReq) {

    	log.info("addAddress4Rgroup : " + addressReq);
    	Address address = new Address();
    	
    	Region xiaoqu = null;
        List<Region> xiaoqus = regionRepository.findAllBySectId(addressReq.getSectId());
        if(xiaoqus == null||xiaoqus.size()== 0 ){
        	xiaoqu = regionRepository.findById(addressReq.getXiaoquId());
        } else {
        	xiaoqu = xiaoqus.get(0);
        }
        if(xiaoqu == null){
        	throw new BizValidateException("未查询到团购所在小区：" + addressReq.getXiaoquName() + "，请确认小区是否开通了团购服务。");
        }
        
        Region county = regionRepository.findById(xiaoqu.getParentId());	//所在区县
        Region city = regionRepository.findById(county.getParentId());	//所在城市
        Region province = regionRepository.findById(city.getParentId());	//所在省市
        
        BeanUtils.copyProperties(addressReq, address);
        address.setXiaoquId(xiaoqu.getId());
        address.setCountyId(xiaoqu.getParentId());
        address.setCounty(xiaoqu.getParentName());
        address.setCityId(city.getId());
        address.setCity(city.getName());
        address.setProvinceId(province.getId());
        address.setProvince(province.getName());
        address.setLongitude(xiaoqu.getLongitude());
        address.setLatitude(xiaoqu.getLatitude());
        address.setSectId(Long.valueOf(addressReq.getSectId()));
        address.setXiaoquName(xiaoqu.getName());
        String xiaoquAddress = xiaoqu.getXiaoquAddress();
        if (!xiaoquAddress.contains(county.getName())) {
        	xiaoquAddress = county.getName() + xiaoquAddress;
		}
        if (!xiaoquAddress.contains(city.getName())) {
			//TODO 理论上不拼城市
		}
        address.setXiaoquAddress(xiaoquAddress);
        
        String detailAddress = xiaoquAddress + addressReq.getDetailAddress();
        address.setDetailAddress(detailAddress);
        address.setUserName(addressReq.getReceiveName());
        List<Address> addrs = addressRepository.findAllByUserId(address.getUserId());
        if(getDefaultAddr(addrs) == null) {
            address.setMain(true);
        }
        addressRepository.save(address);
        
        if (StringUtils.isEmpty(user.getSectId()) || "0".equals(user.getSectId())) {
        	user.setCurrentAddrId(address.getId());
            user.setProvinceId(address.getProvinceId());
            user.setProvince(address.getProvince());
            user.setCity(address.getCity());
            user.setCityId(address.getCityId());
            user.setCounty(address.getCounty());
            user.setCountyId(address.getCountyId());
            user.setXiaoquId(address.getXiaoquId());
            user.setXiaoquName(address.getXiaoquName());
            user.setLatitude(address.getLatitude());
            user.setLongitude(address.getLongitude());
            user.setSectId(addressReq.getSectId());
            user.setCspId(addressReq.getCspId());
            
            log.info("设置用户默认地址[B]" + user.getId() + "--" + user.getCurrentAddrId() + "--" + address.getId());
            userService.save(user);
            log.info("设置用户默认地址[E]" + user.getId() + "--" + user.getCurrentAddrId() + "--" + address.getId());
		}
        return address;
    }
    
    private Address getDefaultAddr(List<Address> addrs) {
        if(addrs == null) {
            return null;
        }
        for(Address addr : addrs) {
            if(addr.isMain()) {
                return addr;
            }
        }
        return null;
    }
    @Async
    public void fillAmapInfo(Address address) {
        log.error("高德地图插入数据！AddressId:" + address.getId());
        AmapAddress amapAddr = null;
        if(address.getAmapId()!=null&&address.getAmapId()!=0){
           amapAddr = AmapUtil.dataSearchId(address.getAmapId());
        }
        if(amapAddr == null&&StringUtil.isNotEmpty(address.getXiaoquName())) {
            DataCreateReq req = new DataCreateReq(address.getXiaoquName(), null, null,
                    address.getCity()+address.getCounty()+address.getAmapDetailAddr(),
                    address.getCity(), address.getCounty(),address.getAmapDetailAddr());
            DataCreateResp resp = AmapUtil.dataManageDataCreate(req);
            if(resp == null || !resp.isSuccess()){
                String errorMsg = "";
                try {
                    errorMsg = "req:"+JacksonJsonUtil.beanToJson(req) + "\n\n";
                    errorMsg = "resp:"+JacksonJsonUtil.beanToJson(resp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                log.error("高德地图插入数据失败！" + errorMsg);
                return;
            }
            try {
                Thread.sleep(5000);//等待地图经纬度生成及索引创建
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            amapAddr = AmapUtil.dataSearchId(resp.get_id());
            if(amapAddr != null) {
                amapAddressRepository.save(amapAddr);
            }
        }
        
        if(amapAddr!=null && amapAddr.getLocation()!=null){
            address = addressRepository.findById(address.getId());
            address.initAmapInfo(amapAddr);

            addressRepository.save(address);
            
            Region xiaoqu = regionRepository.findById(address.getXiaoquId());
            if(xiaoqu != null && Math.abs(xiaoqu.getLatitude()) < 0.1) {
                xiaoqu.setLongitude(address.getLongitude());
                xiaoqu.setLatitude(address.getLatitude());
                xiaoqu.setAmapId(address.getAmapId());
                regionRepository.save(xiaoqu);
            }
            log.error("高德地图更新成功！AmapId:" + address.getAmapId());
        } else {
            log.error("高德地图更新失败！AddressId:" + address.getId());
        }
    }
    @Override
    public Address configDefaultAddress(User user, long addressId) {
        log.error("设置用户默认地址[0]" + user.getId() + "--" + user.getCurrentAddrId() + "--" + addressId);
        Address currentAddr = addressRepository.findById(addressId);
        if(currentAddr == null) {
            return null;
        }
        List<Address> addresses = queryAddressByUser(user.getId());
        Address oldDefaultAddr = getDefaultAddr(addresses);
        if(oldDefaultAddr != null) {
            if(oldDefaultAddr.getId() == addressId) {
                currentAddr = oldDefaultAddr;
            } else {
                oldDefaultAddr.setMain(false);
                addressRepository.save(oldDefaultAddr);
            }
        }
        currentAddr.setMain(true);
        currentAddr = addressRepository.save(currentAddr);
        log.error("设置为默认地址完成:" + currentAddr.getXiaoquName());
        
        user.setCurrentAddrId(currentAddr.getId());
        user.setProvinceId(currentAddr.getProvinceId());
        user.setProvince(currentAddr.getProvince());
        user.setCity(currentAddr.getCity());
        user.setCityId(currentAddr.getCityId());
        user.setCounty(currentAddr.getCounty());
        user.setCountyId(currentAddr.getCountyId());
        user.setXiaoquId(currentAddr.getXiaoquId());
        user.setXiaoquName(currentAddr.getXiaoquName());
        user.setLatitude(currentAddr.getLatitude());
        user.setLongitude(currentAddr.getLongitude());

        log.error("设置用户默认地址[B]" + user.getId() + "--" + user.getCurrentAddrId() + "--" + currentAddr.getId());
        userService.save(user);
        log.error("设置用户默认地址[E]" + user.getId() + "--" + user.getCurrentAddrId() + "--" + currentAddr.getId());
        
        return currentAddr;
    }
    @Override
    public void deleteAddress(long id, long userId) {
        Address addr = addressRepository.findById(id);
//        if(addr.isMain()) {
//            throw new BizValidateException("无法删除默认地址！");
//        }
        if(addr.getUserId() != userId) {
            throw new BizValidateException("无法删除该地址！");
        }
        addressRepository.delete(addr);
    }
    @Override
    public List<Address> queryAddressByUser(long userId) {
        return addressRepository.findAllByUserId(userId);
    }
    @Override
    public List<Region> queryRegions(int type, long regionId) {
        if(type == 1) {
            return regionRepository.findAllByRegionType(1);
        }
        return regionRepository.findAllByRegionTypeAndParentId(type, regionId);
    }
    @Override
    public List<AmapAddress> queryAmapYuntuLocal(String city, String keyword) {
        return AmapUtil.dataSearchLocal(city, keyword);
    }
    @Override
    public Address queryAddressById(long id) {
        return addressRepository.findById(id);
    }
    
    /** 
     * 根据坐标查找周围10个小区
     */
    @Override
    public List<AmapAddress> queryAroundByCoordinate(double longitude, double latitude) {
        return AmapUtil.queryAroundByCoordinate(longitude, latitude);
    }
    @Override
    public Address queryDefaultAddress(User user) {
        if(user.getCurrentAddrId() > 0) {
            return queryAddressById(user.getCurrentAddrId());
        } else {
            List<Address> addrs = addressRepository.findAllByUserId(user.getId());
            if(addrs.size()>0) {
                Address addr = addrs.get(0);
                return configDefaultAddress(user, addr.getId());
            }
            return null;
        }
    }

	@Override
	public List<Address> getAddressByuserIdAndAddress(long id, String cell_addr) {
		return addressRepository.getAddressByuserIdAndAddress(id, cell_addr);
	}

	@Override
	public List<Address> getAddressByMain(long id,boolean main) {
		return addressRepository.getAddressByMain(id,main);
	}

	/**
	 * 根据合协社区用户绑定的房屋设置默认地址
	 * @param user
	 * @param addr
	 */
	@Override
	@Transactional
	public void updateDefaultAddress(User user, HexieAddress addr) {
		
		log.info("start to set default address, user : " + user.getId() + ", tel : " + user.getTel());
		
		boolean result = true;
		List<Address> list = getAddressByuserIdAndAddress(user.getId(), addr.getCell_addr());
		for (Address address : list) {
			if (address.isMain()) {
				log.info("存在重复默认地址:"+address.getDetailAddress()+"---id:"+address.getId());
				result = false;
				break;
			}
		}
		log.info("result : " + result);
		if (result) {
			List<Address> addressList= getAddressByMain(user.getId(), true);
			for (Address address : addressList) {
				if (address != null) {
					address.setMain(false);
					addressRepository.save(address);
					log.info("默认地址设置为不是默认:"+address.getDetailAddress()+"---id:"+address.getId());
				}
			}
			
			List<Region> re = null;
			if (addr.getSect_id() != null) {
				re = regionService.findAllBySectId(addr.getSect_id());
				if(re.size()==0){
					log.info("根据小区id["+addr.getSect_id()+"]未查询到相对应的小区，开始按名字查询，名称：" + addr.getSect_name());
					re = regionRepository.findByNameAndRegionType(addr.getSect_name(), 3);
				}
				if(re.size()==0){
					log.info("未查询到小区！"+addr.getSect_name() + ",开始创建。");
					Region region = new Region();
					region.setName(addr.getSect_name());
					region.setParentId(0);
					region.setParentName(addr.getRegion_name());
					region.setRegionType(ModelConstant.REGION_XIAOQU);
					region.setXiaoquAddress(addr.getSect_addr());
					region.setSectId(addr.getSect_id());
					regionService.saveRegion(region);
					re = new ArrayList<>();
					re.add(region);
				}
			}
			
			Address add = new Address();
			boolean hasAddr = false;
			log.info("list size : " + list.size());
			if (list.size() > 0) {
				add = list.get(0);
				hasAddr = true;
			} else {
				
				if (re != null && re.size()> 0) {
					
					add.setReceiveName(user.getNickname());
					add.setTel(user.getTel());
					add.setUserId(user.getId());
					add.setCreateDate(System.currentTimeMillis());
					add.setXiaoquId(re.get(0).getId());
					add.setXiaoquName(addr.getSect_name());
					add.setDetailAddress(addr.getCell_addr());
					add.setCity(addr.getCity_name());
					Long cityId = map.get(addr.getCity_name());
					if (cityId == null) {
						cityId = 0L;
					}
					add.setCityId(cityId);
					add.setCounty(addr.getRegion_name());
					Long countyId = map.get(addr.getRegion_name());
					if (countyId == null) {
						countyId = 0L;
					}
					add.setCountyId(countyId);
					add.setProvince(addr.getProvince_name());
					Long provinceId = map.get(addr.getProvince_name());
					if (provinceId == null) {
						provinceId = 0L;
					}
					add.setProvinceId(provinceId);
					double latitude = 0;
					double longitude = 0;
					if (user.getLatitude() != null) {
						latitude = user.getLatitude();
					}
	
					if (user.getLongitude() != null) {
						longitude = user.getLongitude();
					}
					add.setLatitude(latitude);
					add.setLongitude(longitude);
					user.setXiaoquId(re.get(0).getId());
					hasAddr = true;
					
				}

			}
			log.info("hasAddr : " + hasAddr);
			if (hasAddr) {
				add.setBind(true);
				add.setMain(true);
				addressRepository.save(add);
			}
			
		}
	}

	/**
	 * 获取用户绑定过房子的地址
	 */
	@Override
	public List<Address> queryBindedAddressByUser(long userId) {
		return addressRepository.findByUserIdAndBind(userId, true);
	}
	
	/**
	 * 获取用户当前团购可用的地址
	 */
	@Override
	public List<Address> queryRgroupAddressByUser(long userId, String ruleId) {
		
		List<Long> supportRegions = new ArrayList<>();
		List<RgroupAreaItem> areaItems = rgroupAreaItemRepository.findByRuleId(Long.valueOf(ruleId));
		for (RgroupAreaItem rgroupAreaItem : areaItems) {
			supportRegions.add(rgroupAreaItem.getRegionId());
		}
		List<Address> availalbe = new ArrayList<>();
		List<Address> allAddr = addressRepository.findAllByUserId(userId);
		for (Address address : allAddr) {
			if (supportRegions.contains(address.getXiaoquId())) {
				availalbe.add(address);
			}
		}
		return availalbe;
	}

	@Override
	public List<Province> queryProvince() {
		
		return provinceRepository.findByStatus(0);
		
	}
	
	@Override
	public List<City> queryCity(long provinceId){
		
		return cityRepository.findByProvinceIdAndStatus(provinceId, 0);
	}
	
	@Override
	public List<County> queryCounty(long cityId) {
		
		return countyRepository.findByCityIdAndStatus(cityId, 0);
	}
	
	/**
	 * 获取用户当前所在小区的地址
	 */
	@Override
	public List<Address> queryBindedAddressByUserAndRegion(User user) {
		return addressRepository.findByUserIdAndXiaoquId(user.getId(), user.getXiaoquId());
	}
	
	/**
	 * 获取团购的小区信息和物业信息
	 * @param user
	 * @param rgroupAreas
	 * @return
	 * @throws Exception 
	 */
	@Override
	public List<RgroupRegionsVO> querySectInfo(User user, List<RgroupAreaItem> rgroupAreas) throws Exception {
		
		StringBuffer bf = new StringBuffer();
		for (RgroupAreaItem rgroupAreaItem : rgroupAreas) {
			Region region = regionService.getRegionInfoById(rgroupAreaItem.getRegionId());
			bf.append(region.getSectId()).append(",");
		}
		bf.deleteCharAt(bf.length()-1);
		return eshopUtil.querySectInfo(user, bf.toString());
	}
	
}
