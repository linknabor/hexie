package com.yumu.hexie.service.page.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.MiniUserPageAccess;
import com.yumu.hexie.model.user.MiniUserPageAccessRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;
import com.yumu.hexie.model.view.BannerRepository;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BgImageRepository;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.BottomIconRepository;
import com.yumu.hexie.model.view.CsHotline;
import com.yumu.hexie.model.view.CsHotlineRepository;
import com.yumu.hexie.model.view.Menu;
import com.yumu.hexie.model.view.MenuRepository;
import com.yumu.hexie.model.view.OrgMenu;
import com.yumu.hexie.model.view.OrgMenuRepository;
import com.yumu.hexie.model.view.PageConfigView;
import com.yumu.hexie.model.view.PageConfigViewRepository;
import com.yumu.hexie.model.view.QrCode;
import com.yumu.hexie.model.view.QrCodeRepository;
import com.yumu.hexie.model.view.WuyePayTabs;
import com.yumu.hexie.model.view.WuyePayTabsRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.vo.menu.GroupMenuInfo;
import com.yumu.hexie.vo.menu.MenuInfo;

@Service("pageConfigService")
public class PageConfigServiceImpl implements PageConfigService {
	
	@Inject
	private PageConfigViewRepository pageConfigViewRepository;
	@Inject
	private BannerRepository bannerRepository;
	@Autowired
	private BottomIconRepository bottomIconRepository;
	@Autowired
	private QrCodeRepository qrCodeRepository;
	@Autowired
	private CsHotlineRepository csHotlineRepository;
	@Autowired
	private BgImageRepository bgImageRepository;
	@Autowired
	private WuyePayTabsRepository wuyePayTabsRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private MiniUserPageAccessRepository miniUserPageAccessRepository;
	@Autowired
	private OrgMenuRepository orgMenuRepository;
	
	/**
	 * 根据banner类型动态获取
	 * @param user
	 * @param bannerType
	 */
	@Override
	public List<Banner> queryBannerType(User user, int bannerType) {
		if (user.getProvinceId() != 0) {
			return bannerRepository.queryByBannerTypeAndUser(user.getProvinceId(), user.getCityId(), user.getCountyId(),
					user.getXiaoquId(), bannerType);
		} else {
			return bannerRepository.queryByBannerTypeAndUser(19, user.getCityId(), user.getCountyId(),
					user.getXiaoquId(), bannerType);
		}

	}

	/**
	 * 到家、洗衣、红包页面配置
	 * @param key
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_PAGECONFIG, key = "#key+'_'+#appId", unless = "#result == null")
	public String findByTempKey(String key, String appId) {
		
		PageConfigView pageConfigView = pageConfigViewRepository.findByTempKeyAndAppId(key, appId);
		if (pageConfigView != null) {
			return pageConfigView.getPageConfig();
		}
		return "";
	}

	/**
	 * 根据不同sys动态获取底部icon。
	 * @param appId
	 * @return
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_BOTTOM_ICON, key = "#appId", unless = "#result == null")
	public List<BottomIcon> getBottomIcon(String appId) {
		Sort sort = new Sort(Direction.ASC, "sort");
		return bottomIconRepository.findByAppId(appId, sort);
	}

	
	/**
	 * 根据不同sys动态获取空白背景地图
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_BGIMAGE, key = "#appId", unless = "#result == null")
	public List<BgImage> getBgImage(String appId) {
		Sort sort = new Sort(Direction.ASC, "type");
		return bgImageRepository.findByAppId(appId, sort);
	}
	
	/**
	 * 动态获取公众号二维码
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_QRCODE, key = "#appId", unless = "#result == null")
	public QrCode getQrCode(String appId) {
		return qrCodeRepository.findByFromSys(appId);
	}
	
	/**
	 * 根据banner类型和appId获取banner
	 * @param bannerType
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_BANNER, key = "#bannerType+'_'+#appId", unless = "#result == null")
	public List<Banner> queryByBannerTypeAndAppId(int bannerType, String appId) {
		Sort sort = new Sort(Direction.ASC, "sortNo");
		return bannerRepository.findByBannerTypeAndStatusAndRegionTypeAndAppId(bannerType, ModelConstant.BANNER_STATUS_VALID, 
				ModelConstant.REGION_ALL, appId, sort);
		
	}
	
	/**
	 * 获取物业缴费选款卡配置
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_WUYEPAY_TABS, key = "#appId", unless = "#result == null")
	public List<WuyePayTabs> getWuyePayTabs(String appId) {
		Sort sort = new Sort(Direction.ASC, "sort");
		return wuyePayTabsRepository.findByAppId(appId, sort);
	}


	/**
	 * 动态获取公众号客服电话
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_CSHOTLINE, key = "#appId", unless = "#result == null")
	public CsHotline getCsHotline(String appId) {
		return csHotlineRepository.findByFromSys(appId);
	}
	
	/**
	 * 动态获取公众号菜单
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_MENU_SECT, key = "#sectId", unless = "#result == null")
	public List<Menu> getMenuBySectId(String sectId) {
		
		Sort sort = new Sort(Direction.ASC, "sort");
		return menuRepository.findBySectId(sectId, sort);
	}
	
	/**
	 * 动态获取公众号菜单
	 * @param appId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_MENU_CSP, key = "#cspId", unless = "#result == null")
	public List<Menu> getMenuByCspId(String cspId) {
		
		Sort sort = new Sort(Direction.ASC, "sort");
		return menuRepository.findByCspId(cspId, sort);
	}
	
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_MENU_DEFAULT, key = "#defaultType", unless = "#result == null")
	public List<Menu> getMenuByDefaultTypeLessThan(int defaultType) {
		
		Sort sort = new Sort(Direction.ASC, "sort");
		return menuRepository.findByDefaultTypeLessThan(defaultType, sort);
	}
	
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_MENU_APP_BINDED, key = "#appid+'_'+#defaultType", unless = "#result == null")
	public List<Menu> getMenuByAppidAndDefaultTypeLessThan(String appid, int defaultType) {
		
		Sort sort = new Sort(Direction.ASC, "sort");
		return menuRepository.findByAppidAndDefaultTypeLessThan(appid, defaultType, sort);
	}
	
	/**
	 * 清楚所有页面配置参数缓存
	 */
	@Override
	@CacheEvict(cacheNames = {ModelConstant.KEY_TYPE_PAGECONFIG, ModelConstant.KEY_TYPE_BOTTOM_ICON, ModelConstant.KEY_TYPE_BGIMAGE, 
			ModelConstant.KEY_TYPE_QRCODE, ModelConstant.KEY_TYPE_BANNER, ModelConstant.KEY_TYPE_WUYEPAY_TABS, ModelConstant.KEY_TYPE_CSHOTLINE}
			, allEntries = true)
	public void updatePageConfig() {

	}
	
	@Override
	@CacheEvict(cacheNames = {ModelConstant.KEY_TYPE_MENU_SECT, ModelConstant.KEY_TYPE_MENU_CSP, ModelConstant.KEY_TYPE_MENU_DEFAULT, 
			ModelConstant.KEY_TYPE_MENU_APP_BINDED}
			, allEntries = true)
	public void updateMenuConfig() {

	}
	
	@Override
	public String getSwtichSectTips(User user, String page) {
		
		String tips = "";
		if(StringUtils.isEmpty(page)) {
			return "";
		}
		String key = ModelConstant.KEY_PAGE_TIPS_SWITCH_SECT + page;
		long offset = user.getId();
		
		Boolean shown = stringRedisTemplate.opsForValue().getBit(key, offset);
		if (!shown) {
			stringRedisTemplate.opsForValue().setBit(key, offset, true);	//用bitmap做，占用的存储空间要小的多
			tips = systemConfigService.getSysConfigByKey("SWITCH_SECT_TIPS_"+page.toUpperCase());
		}
//		Long value = stringRedisTemplate.opsForValue().increment(key);
//		if (value == 1) {
//			tips = systemConfigService.getSysConfigByKey("SWITCH_SECT_TIPS_"+page.toUpperCase());
//		}
		return tips;
	}
	
	/**
	 * 校验小程序用户页面访问权限
	 * @param page
	 * @param roleId
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_MINI_ROLE_PAGE, key = "#page+'_' +#roleId", unless = "#result == null")
	public MiniUserPageAccess getMiniPageAccess(String page, String roleId) {
		return miniUserPageAccessRepository.findByPageAndRoleId(page, roleId);
	}
	
	/**
	 * 机构人员菜单
	 * @param orgRoleId
	 * @return
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_TYPE_ORG_MENU, key = "#orgType+'_'+#orgRoleId" , unless = "#result == null")
	public List<GroupMenuInfo> getOrgMenu(String orgRoleId, String orgType) {

		String reRoleId = "";
		String reOrgType = "";
		if(!"99".equals(orgRoleId)) {
			reRoleId = orgRoleId;
			reOrgType = orgType;
		}

		List<GroupMenuInfo> list = new ArrayList<>();
		List<OrgMenu> parentMenu = orgMenuRepository.findByUserRoleAndLevel(reRoleId, reOrgType, "0", "");
		for(OrgMenu m : parentMenu) {
			GroupMenuInfo group = new GroupMenuInfo();
			group.setGroupName(m.getName());
			List<OrgMenu> subMenu = orgMenuRepository.findByUserRoleAndLevel(reRoleId, reOrgType, "1", m.getCode());
			List<MenuInfo> infos = new ArrayList<>();
			for(OrgMenu sub : subMenu) {
				MenuInfo info = new MenuInfo();
				info.setCode(sub.getCode());
				info.setMenuName(sub.getName());
				info.setMenuIcon(sub.getImage());
				info.setMenuPage(sub.getUrl());
				infos.add(info);
			}
			group.setMenu(infos);
			list.add(group);
		}
		return list;
	}

}
