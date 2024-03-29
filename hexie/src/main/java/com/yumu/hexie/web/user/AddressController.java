package com.yumu.hexie.web.user;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.RgroupAreaItemRepository;
import com.yumu.hexie.model.distribution.region.AmapAddress;
import com.yumu.hexie.model.distribution.region.City;
import com.yumu.hexie.model.distribution.region.County;
import com.yumu.hexie.model.distribution.region.Province;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.user.AddressService;
import com.yumu.hexie.service.user.PointService;
import com.yumu.hexie.service.user.RegionService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.service.user.req.AddressReq;
import com.yumu.hexie.vo.QQMapVO;
import com.yumu.hexie.vo.RgroupAddressVO;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.user.resp.RegionInfo;
import com.yumu.hexie.web.user.resp.SharedVo;

@Controller(value = "addressController")
public class AddressController extends BaseController{

    @Inject
    private AddressService addressService;
    @Inject
    private UserService userService;
	@Inject
	private RegionService regionService;
	@Inject
	private RgroupAreaItemRepository rgroupAreaItemRepository;
	@Autowired
	private PointService pointService;
	@Autowired
	private SystemConfigService systemConfigService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/address/delete/{addressId}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> deleteAddress(@ModelAttribute(Constants.USER)User user,@PathVariable long addressId) throws Exception {
		addressService.deleteAddress(addressId, user.getId());
        return BaseResult.successResult("删除地址成功");
    }

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/address/query/{addressId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Address> queryAddressById(@ModelAttribute(Constants.USER)User user,@PathVariable long addressId) throws Exception {
		return BaseResult.successResult(addressService.queryAddressById(addressId));
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/address/default/{addressId}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> defaultAddress(HttpSession session,@ModelAttribute(Constants.USER)User user,@PathVariable long addressId) throws Exception {
		Address addr = addressService.configDefaultAddress(user, addressId);
        if(addr == null) {
        	BaseResult.fail("设置默认地址失败！");
        }
        session.setAttribute(Constants.USER, user);
		return BaseResult.successResult("设置默认地址成功");
    }

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addresses", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<List<Address>> queryAddressList(@ModelAttribute(Constants.USER)User user,
    		@RequestParam(required=false, name="module") String module) throws Exception {
		List<Address> addresses = new ArrayList<>();
		if ("repair".equals(module)) {
			addresses = addressService.queryBindedAddressByUser(user.getId());
		} else if ("rgroup".equals(module)) {
			addresses = addressService.queryBindedAddressByUser(user.getId());
		} else {
			addresses = addressService.queryAddressByUser(user.getId());
		}
		BaseResult<List<Address>> r = BaseResult.successResult(addresses);
		return r;
    }
	
	/**
	 * 获取团购支持的地址
	 * @param user
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/rgroupAddresses/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<List<Address>> queryRgroupAddressList(@ModelAttribute(Constants.USER)User user,
    		@PathVariable(name="ruleId") String ruleId, @RequestParam(name="regionId" , required = false) String regionId) throws Exception {
		List<Address> addresses = addressService.queryRgroupAddressByUser(user.getId(), ruleId, regionId);
		return BaseResult.successResult(addresses);
    }
	
	/**
	 * 获取团购默认地址
	 * @param user
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/rgroup/address/default/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
    public BaseResult<RgroupAddressVO> queryRgroupDefaultAddress(@ModelAttribute(Constants.USER)User user,
    		@PathVariable(name="ruleId") String ruleId) throws Exception {
		RgroupAddressVO rgroupAddressVO = addressService.queryRgroupDefaultAddress(user.getId(), ruleId);
		return BaseResult.successResult(rgroupAddressVO);
    }
	
	@RequestMapping(value = "/addAddress", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<Address> save(HttpSession session,@ModelAttribute(Constants.USER)User user,@RequestBody AddressReq address) throws Exception {
		if(address.getCountyId() == 0){
			return new BaseResult<Address>().failMsg("请重新选择所在区域");
		}
		if(StringUtil.isEmpty(address.getXiaoquName()) || StringUtil.isEmpty(address.getDetailAddress())){
			return new BaseResult<Address>().failMsg("请填写小区和详细地址");
		}
		if (StringUtil.isEmpty(address.getReceiveName()) || StringUtil.isEmpty(address.getTel())) {
			return new BaseResult<Address>().failMsg("请检查真实姓名和手机号码是否正确");
		}
		address.setUserId(user.getId());
		if (StringUtil.isEmpty(address.getAmapId())) {
			address.setAmapId(0l);
		}
		Address addr = addressService.addAddress(address);
		//本方法内调用无法异步
		addressService.fillAmapInfo(addr);
		userService.getById(user.getId());
		if (!systemConfigService.isCardServiceAvailable(user.getAppId())) {
			pointService.updatePoint(user, "50", "zhima-address-"+user.getId()+"-"+address.getId());
		}
		session.setAttribute(Constants.USER, user);
		return new BaseResult<Address>().success(addr);
    }
	
	@RequestMapping(value = "/addAddress4Rgroup", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<Address> save4Rgroup(HttpSession session,@ModelAttribute(Constants.USER)User user,@RequestBody AddressReq address) throws Exception {

		if(StringUtil.isEmpty(address.getSectId())){
			return new BaseResult<Address>().failMsg("请查看当前小区是否开通了团购服务");
		}
		if (StringUtil.isEmpty(address.getDetailAddress())) {
			return new BaseResult<Address>().failMsg("请填写详细地址");
		}
		if (StringUtil.isEmpty(address.getReceiveName())) {
			return new BaseResult<Address>().failMsg("请填写收货人姓名");
		}
		if (StringUtil.isEmpty(address.getReceiveName()) || StringUtil.isEmpty(address.getTel())) {
			return new BaseResult<Address>().failMsg("请填写收货人手机号码");
		}
		address.setUserId(user.getId());
		if (StringUtil.isEmpty(address.getAmapId())) {
			address.setAmapId(0l);
		}
		User currUser = userService.getById(user.getId());
		Address addr = addressService.addAddress4Rgroup(currUser, address);
//		if (!systemConfigService.isCardServiceAvailable(user.getAppId())) {
//			pointService.updatePoint(user, "50", "zhima-address-"+user.getId()+"-"+address.getId());
//		}
		BeanUtils.copyProperties(currUser, user);
		session.setAttribute(Constants.USER, user);
		return new BaseResult<Address>().success(addr);
    }
	
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/regions/{type}/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<Region>> queryRegions(@PathVariable int type,@PathVariable long parentId){
        List<Region> regions = addressService.queryRegions(type, parentId);
        return BaseResult.successResult(regions);
    }

    @RequestMapping(value = "/regionsv2/{type}/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<RegionInfo>> queryRegionsV2(@PathVariable int type,@PathVariable long parentId){
        List<Region> regions = addressService.queryRegions(type, parentId);
        List<RegionInfo> infos = new ArrayList<RegionInfo>();
        for(Region r : regions) {
            infos.add(new RegionInfo(r.getName(),r.getId()));
        }
        return new BaseResult<List<RegionInfo>>().success(infos);
    }
    
	//add by zhangxiaonan for amap
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/amap/{city}/{keyword}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<AmapAddress>> queryAmapYuntuLocal(@PathVariable String city,@PathVariable String keyword){
		return BaseResult.successResult(addressService.queryAmapYuntuLocal(city, keyword));
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/amap/{longitude}/{latitude}/around/", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<AmapAddress>> queryAround(@PathVariable double longitude, @PathVariable double latitude){
		return BaseResult.successResult(addressService.queryAroundByCoordinate(longitude, latitude));
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	@RequestMapping(value = "/getRegionByRuleId/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<SharedVo> queryAddrByShareCode(HttpSession session, @ModelAttribute(Constants.USER)User user,@PathVariable String ruleId) {
		Address address = new Address();
		if(!StringUtil.isEmpty(ruleId)){
			
			List<RgroupAreaItem> list = rgroupAreaItemRepository.findByRuleId(Long.valueOf(ruleId));
			if (list != null && list.size() > 0) {
				
				RgroupAreaItem item = list.get(0);
				
				Region sect = regionService.getRegionInfoById(item.getRegionId());
				address.setXiaoquId(sect.getId());
				address.setXiaoquName(sect.getName());
				address.setXiaoquAddress(sect.getXiaoquAddress());
				
				Region dist = regionService.getRegionInfoById(sect.getParentId());
				address.setCounty(dist.getName());
				address.setCountyId(dist.getId());
				
				Region city = regionService.getRegionInfoById(dist.getParentId());
				address.setCity(city.getName());
				address.setCityId(city.getId());
				
				Region province = regionService.getRegionInfoById(city.getParentId());
				address.setProvince(province.getName());
				address.setProvinceId(province.getId());
				
			}
			
		}
		SharedVo vo = new SharedVo();
		vo.setAddress(address);
		vo.setBuyer(user);
		return new BaseResult<SharedVo>().successResult(vo);
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/province", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Province>> queryProvince(){
		
		return BaseResult.successResult(addressService.queryProvince());
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/city/{provinceId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<City>> queryCity(@PathVariable long provinceId){
		
		return BaseResult.successResult(addressService.queryCity(provinceId));
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/county/{cityId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<County>> queryCounty(@PathVariable long cityId){
		
		return BaseResult.successResult(addressService.queryCounty(cityId));
	}
	
	/**
	 * 团长选取小区列表
	 * @param user
	 * @param regionName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/regions/rgroup/query", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<Region>> queryRegions(@ModelAttribute(Constants.USER)User user,
    		@RequestParam(required = false) String regionName){
        List<Region> regions = regionService.findByNameLikeAndType(regionName);
        return BaseResult.successResult(regions);
    }
	
	/**
	 * 团长添加过的小区列表展示
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/regions/rgroupowner/query", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<List<Region>> queryRgroupOwnerRegions(@ModelAttribute(Constants.USER)User user){
        List<Region> regions = regionService.findByRgroupOwner(user);
        return BaseResult.successResult(regions);
    }
	
	/**
	 * 缓存团长添加的小区
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/regions/rgroupowner/save", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<String> saveOwnerServiceArea(@ModelAttribute(Constants.USER)User user, @RequestBody Region region){
		regionService.saveOwnerServiceArea(user, region);
        return BaseResult.successResult(Constants.PAGE_SUCCESS);
    }
	
	/**
	 * 缓存团长添加的小区
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/regions/rgroupowner/del/{regionId}", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<String> delOwnerServiceArea(@ModelAttribute(Constants.USER)User user, @PathVariable long regionId){
		regionService.delOwnerServiceArea(user, regionId);
        return BaseResult.successResult(Constants.PAGE_SUCCESS);
    }
	
	/**
	 * 缓存团长添加的小区
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/regions/rgroup/sect/save", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<Region> createSect(@ModelAttribute(Constants.USER)User user, @RequestBody QQMapVO mapVO){
		Region region = regionService.createSect(user, mapVO);
        return BaseResult.successResult(region);
    }
}
