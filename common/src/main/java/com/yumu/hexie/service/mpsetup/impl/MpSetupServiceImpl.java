package com.yumu.hexie.service.mpsetup.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.yumu.hexie.model.msgtemplate.MsgTempalateRepository;
import com.yumu.hexie.model.msgtemplate.MsgTemplate;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.system.SystemConfigRepository;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.model.view.BgImageRepository;
import com.yumu.hexie.model.view.BottomIcon;
import com.yumu.hexie.model.view.BottomIconRepository;
import com.yumu.hexie.model.view.Menu;
import com.yumu.hexie.model.view.MenuRepository;
import com.yumu.hexie.model.view.QrCode;
import com.yumu.hexie.model.view.QrCodeRepository;
import com.yumu.hexie.model.view.WuyePayTabs;
import com.yumu.hexie.model.view.WuyePayTabsRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.mpsetup.MpSetupService;
import com.yumu.hexie.service.mpsetup.req.MpQueryReq;
import com.yumu.hexie.service.mpsetup.req.MpSetupReq;
import com.yumu.hexie.service.mpsetup.resp.MpQueryResp;
import com.yumu.hexie.service.mpsetup.resp.MpQueryResp.MsgTemplateVO;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.service.shequ.ParamService;

@Service
public class MpSetupServiceImpl implements MpSetupService {
	
	private static Logger logger = LoggerFactory.getLogger(MpSetupServiceImpl.class);
	
	@Autowired
	private QrCodeRepository qrCodeRepository; 
	@Autowired
	private BgImageRepository bgImageRepository;
	@Autowired
	private BottomIconRepository bottomIconRepository;
	@Autowired
	private WuyePayTabsRepository wuyePayTabsRepository;
	@Autowired
	private SystemConfigRepository systemConfigRepository;
	@Autowired
	private MsgTempalateRepository msgTempalateRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private ParamService paramService;
	@Autowired
	private PageConfigService pageConfigService;
	@Autowired
	private WechatMsgService msgTemplateService;
	@Autowired
	private SystemConfigService systemConfigService;
	
	private Map<String, String> templateMap;
	
	@PostConstruct
	public void initMsgTemplateMapping() {
		
		templateMap = new HashMap<>();
		templateMap.put("messageTemplate", "物业平台群发通知模板消息");
		templateMap.put("billPushTemplate", "账单通知模板消息");
		templateMap.put("sendOpinionNotificationMessageTemplate", "业主意见回复模板消息");
		templateMap.put("invoiceApplicationReminderTemplate", "电子发票申请提醒");
		templateMap.put("invoiceFinishTemplate", "电子发票开具完成模板消息");
		templateMap.put("receiptFinishTemplate", "电子收据开具成功消息");
		templateMap.put("payNotifyTemplate", "支付到账通知模板消息");
		templateMap.put("workOrderNotificationTemplate", "工单模板消息");
		
		templateMap.put("messageTemplat2e", "类目模板-物业平台群发通知");
		templateMap.put("billPushTemplate2", "类目模板-账单通知");
		templateMap.put("sendOpinionNotificationMessageTemplate2", "类目模板-业主意见回复");
		templateMap.put("invoiceApplicationReminderTemplate2", "类目模板-电子发票申请提醒");
		templateMap.put("invoiceFinishTemplate2", "类目模板-电子发票开具完成");
		templateMap.put("receiptFinishTemplate2", "类目模板-电子收据开具成功");
		templateMap.put("payNotifyTemplate2", "类目模板-支付到账通知");
		templateMap.put("workOrderNotificationTemplate2", "类目模板-通知业主工单进度");
	}

	@Transactional
	@Override
	public void saveMp(MpSetupReq mpSetupReq) throws Exception {
		
		logger.info("mpSetupReq : " + mpSetupReq);

		String appid = mpSetupReq.getAppid().trim();
		String accessToken = systemConfigService.queryWXAToken(appid);
		if (StringUtils.isEmpty(accessToken)) {
			throw new BizValidateException("请确认当前公众号是否已经授权或者appid是否正确，appid : " + appid);
		}
		String edit = mpSetupReq.getEdit();
		QrCode qrCode = qrCodeRepository.findByFromSys(appid);
		if (qrCode == null) {
			qrCode = new QrCode();
			qrCode.setFromSys(appid);
		}
		qrCode.setQrLink(mpSetupReq.getAppLogo().trim());
		qrCodeRepository.save(qrCode);
		
		if (!"1".equals(edit)) {
			Map<String, Integer> bgImages = new HashMap<>();
			bgImages.put("http://img.e-shequ.cn/Ft7OzEjBMt5Kq3FcMroB-lx6lwOt", 1);
			bgImages.put("http://img.e-shequ.cn/Fq9i4DHCOz0hh5qOuFPO6ldfggPA", 3);
			bgImages.put("http://img.e-shequ.cn/Ftq1onFgQLUQSOutUMycNftyktii", 4);
			bgImages.put("http://img.e-shequ.cn/FocY5yjN1xKzMAjTrr-2q8qUFDp2", 5);
			bgImages.put("http://img.e-shequ.cn/FjPqJk1IZmvw6syJM4yrKmjQbnfF", 6);
			
			Iterator<Map.Entry<String, Integer>> it = bgImages.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String imgUrl = entry.getKey();
				Integer type = entry.getValue();
				
				BgImage bgImage = new BgImage();
				bgImage.setAppId(appid);
				bgImage.setImgUrl(imgUrl);
				bgImage.setType(type);
				bgImageRepository.save(bgImage);
				
			}
			BottomIcon bottomIcon = new BottomIcon();
			bottomIcon.setAppId(appid);
			bottomIcon.setIconClass("footer_logo footer_fuwu_liangyou");
			bottomIcon.setIconLink("https://www.e-shequ.cn/weixin/wuye/index.html?oriApp=" + appid);
			bottomIcon.setIconName("社区");
			bottomIcon.setSort(1);
			bottomIconRepository.save(bottomIcon);
			
			bottomIcon = new BottomIcon();
			bottomIcon.setAppId(appid);
			bottomIcon.setIconClass("footer_logo footer_person_liangyou");
			bottomIcon.setIconLink("https://www.e-shequ.cn/weixin/person/index.html?oriApp=" + appid);
			bottomIcon.setIconName("个人中心");
			bottomIcon.setSort(3);
			bottomIconRepository.save(bottomIcon);
			
			WuyePayTabs wuyePayTabs = new WuyePayTabs();
			wuyePayTabs.setAppId(appid);
			wuyePayTabs.setName("查询缴费");
			wuyePayTabs.setSort("0");
			wuyePayTabs.setValue("d");
			wuyePayTabsRepository.save(wuyePayTabs);
			
			wuyePayTabs = new WuyePayTabs();
			wuyePayTabs.setAppId(appid);
			wuyePayTabs.setName("我的账单");
			wuyePayTabs.setSort("1");
			wuyePayTabs.setValue("b");
			wuyePayTabsRepository.save(wuyePayTabs);
			
			wuyePayTabs = new WuyePayTabs();
			wuyePayTabs.setAppId(appid);
			wuyePayTabs.setName("扫描账单");
			wuyePayTabs.setSort("2");
			wuyePayTabs.setValue("a");
			wuyePayTabsRepository.save(wuyePayTabs);
		}
		
		String defaultSign = mpSetupReq.getDefaultSign().trim();
		String signKey = "DEFAULT_SIGN_" + appid;
		List<SystemConfig> configs = systemConfigRepository.findAllBySysKey(signKey);
		SystemConfig systemConfig = null;
		if (configs == null || configs.isEmpty()) {
			systemConfig = new SystemConfig();
			systemConfig.setSysKey(signKey);
		} else {
			systemConfig = configs.get(0);
		}
		systemConfig.setSysValue(defaultSign);
		systemConfigRepository.save(systemConfig);
		
		String abbr = mpSetupReq.getAbbr().trim();
		if (!StringUtils.isEmpty(abbr)) {
			abbr = abbr.replaceAll("_", "");
		}
		abbr = "_" + abbr;
		String abbrKey = "APP_SYS_" + appid;
		configs = systemConfigRepository.findAllBySysKey(abbrKey);
		if (configs == null || configs.isEmpty()) {
			systemConfig = new SystemConfig();
			systemConfig.setSysKey(abbrKey);
		} else {
			systemConfig = configs.get(0);
		}
		systemConfig.setSysValue(abbr);
		systemConfigRepository.save(systemConfig);
		
		String[]templateIds = mpSetupReq.getTemplateId();
		String[]templateNames = mpSetupReq.getTemplateName();
		String[]templateTypes = mpSetupReq.getTemplateType();
		
		List<Integer> typeList = new ArrayList<>();
		typeList.add(0);
		typeList.add(2);
		List<MsgTemplate> templateList = msgTempalateRepository.findByAppidAndTypeInAndBizType(appid, typeList, 0);
		if (templateList != null && !templateList.isEmpty()) {
			msgTempalateRepository.deleteAll(templateList);
		}
		for (int i = 0; i < templateIds.length; i++) {
			String templateName = templateNames[i].trim();
			String templateId = templateIds[i].trim();
			String type = templateTypes[i].trim();
			Integer templateType = 2;
			if(!StringUtils.isEmpty(type)) {
				templateType = Integer.valueOf(type);
			}
			
			if (StringUtils.isEmpty(templateName) || StringUtils.isEmpty(templateId)) {
				continue;
			}
			MsgTemplate msgTemplate = new MsgTemplate();
			msgTemplate.setAppid(appid);
			msgTemplate.setName(templateName);
			msgTemplate.setRemark(templateMap.get(templateName));
			msgTemplate.setStatus(1);
			msgTemplate.setValue(templateId);
			msgTemplate.setBizType(0);
			msgTemplate.setSubscribeType(0);
			msgTemplate.setType(templateType);
			msgTempalateRepository.save(msgTemplate);
		}
		List<Menu> menuList = menuRepository.findByAppidAndType(appid, "0");
		if (menuList!=null && !menuList.isEmpty()) {
			menuRepository.deleteAll(menuList);
		}
		String[]menus = mpSetupReq.getAppMenu();
		for (int i = 0; i < menus.length; i++) {
			String menuCode = menus[i].trim();
			if ("owner".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("我是业主");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/owner@3x.png");
				menu.setName("我是业主");
				menu.setSort("1");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/myhouse");
				menuRepository.save(menu);
			} else if ("wuyepay".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("物业缴费");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/wuyePay@3x.png");
				menu.setName("物业缴费");
				menu.setSort("2");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/Pay");
				menuRepository.save(menu);
			} else if ("paymentquery".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("缴费查询");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/payrecorder@3x.png");
				menu.setName("缴费查询");
				menu.setSort("3");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/paymentquery");
				menuRepository.save(menu);
			} else if ("repair".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("物业报修");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/repair@3x.png");
				menu.setName("物业报修");
				menu.setSort("5");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/repair");
				menuRepository.save(menu);
			} else if ("service".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("周边服务");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/service@3x.png");
				menu.setName("周边服务");
				menu.setSort("7");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("home/index.html?");
				menuRepository.save(menu);
			} else if ("opinion".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("投诉建议");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/opinion@3x.png");
				menu.setName("投诉建议");
				menu.setSort("4");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/opinionList");
				menuRepository.save(menu);
			} else if ("onsale".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("社区电商");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/onsale@3x.png");
				menu.setName("社区电商");
				menu.setSort("6");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/group/onsales.html?");
				menuRepository.save(menu);
			} else if ("evoucher".equals(menuCode)) {
				Menu menu = new Menu();
				menu.setAppid(appid);
				menu.setCode(menuCode);
				menu.setDefaultType(0);
				menu.setDescription("周边优惠");
				menu.setImage("https://www.e-shequ.cn/weixin/static/images/wuye/evoucher@3x.png");
				menu.setName("周边优惠");
				menu.setSort("8");
				menu.setStatus(1);
				menu.setType("0");
				menu.setUrl("/group/onsales.html?");
				menuRepository.save(menu);
			}
		}
		
		paramService.updateSysParam();
		pageConfigService.updatePageConfig();
		pageConfigService.updateMenuConfig();
		msgTemplateService.refreshCache();
		
	}
	
	@Override
	public MpQueryResp queryMp(MpQueryReq mpQueryReq) throws Exception {
		
		logger.info("mpQueryReq : " + mpQueryReq);
		String appid = mpQueryReq.getAppid().trim();
		Assert.hasText(appid, "appid不能为空。");
		
		
		QrCode qrCode = qrCodeRepository.findByFromSys(appid);
		String appLogo = qrCode.getQrLink();
		MpQueryResp mpQueryResp = new MpQueryResp();
		mpQueryResp.setAppid(appid);
		mpQueryResp.setAppLogo(appLogo);
		
		String signKey = "DEFAULT_SIGN_" + appid;
		List<SystemConfig> sysList = systemConfigRepository.findAllBySysKey(signKey);
		SystemConfig signConfig = sysList.get(0);
		String defaultSign = signConfig.getSysValue();
		mpQueryResp.setDefaultSign(defaultSign);
		
		String abbrKey = "APP_SYS_" + appid;
		sysList = systemConfigRepository.findAllBySysKey(abbrKey);
		signConfig = sysList.get(0);
		String abbr = signConfig.getSysValue();
		mpQueryResp.setAbbr(abbr);
		
		List<String> menus = new ArrayList<>();
		List<Menu> menuList = pageConfigService.getMenuByAppidAndDefaultTypeLessThan(appid, 2);
		for (Menu menu : menuList) {
			menus.add(menu.getCode());
		}
		mpQueryResp.setAppMenu(menus);
		
//		List<MsgTemplate> list = msgTempalateRepository.findByAppidAndTypeAndBizType(appid, 0, 0);
		List<Integer> typeList = new ArrayList<>();
		typeList.add(0);
		typeList.add(2);
		List<MsgTemplate> list = msgTempalateRepository.findByAppidAndTypeInAndBizType(appid, typeList, 0);
		List<MsgTemplateVO> templateVos = new ArrayList<>();
		for (MsgTemplate msgTemplate : list) {
			MsgTemplateVO vo = new MsgTemplateVO();
			vo.setTemplateId(msgTemplate.getValue());
			vo.setTemplateName(msgTemplate.getName());
			vo.setType(msgTemplate.getType());
			templateVos.add(vo);
		}
		mpQueryResp.setTemplates(templateVos);
		
		return mpQueryResp;
	}

}
