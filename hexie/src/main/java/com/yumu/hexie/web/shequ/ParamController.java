/**
 * 
 */
package com.yumu.hexie.web.shequ;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.LogisticsService;
import com.yumu.hexie.service.eshop.PartnerService;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.service.shequ.LocationService;
import com.yumu.hexie.service.shequ.ParamService;
import com.yumu.hexie.web.BaseController;

/**
 * 系统参数控制
 * @author huym
 *
 */
@RequestMapping("/param")
@RestController(value = "paramController")
public class ParamController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ParamController.class);
	
	@Autowired
	private ParamService paramService;
	@Autowired
	private PageConfigService pageConfigService;
	@Autowired
	private WechatMsgService msgTemplateService;
	@Autowired
	private LocationService locationService;
	@Autowired
	private LogisticsService logisticsService;
	@Autowired
	private PartnerService partnerService;
	
	@RequestMapping(value = "/wuye/{oriSys}", method = RequestMethod.GET)
	public void initWuyeParam(HttpServletResponse response, 
			@PathVariable String oriSys,
			@RequestParam(value="info_id", required=false) String infoId, 
			@RequestParam(value="type") String type) throws IOException {
		
		if (StringUtils.isEmpty(infoId)) {
			return;
		}
		
		if (!ModelConstant.PARA_TYPE_CSP.equals(type)
				&& !ModelConstant.PARA_TYPE_SECT.equals(type)) {
			return;
		}
		User user = new User();
		user.setOriSys(oriSys);
		user.setCspId(infoId);
		
		paramService.getWuyeParamAsync(user, type);
		response.getWriter().print("ok");
	}
	
	@RequestMapping(value = "/recache/{type}", method = RequestMethod.GET)
	public String updateSysParam(@RequestParam(value = "syscode") String syscode, @PathVariable String type) {
		
		if (!"hexie".equals(syscode)) {
			return "";
		}
		switch (type) {
		case "sys":
			paramService.updateSysParam();
			break;
		case "page":
			pageConfigService.updatePageConfig();
			break;
		case "msg":
			msgTemplateService.refreshCache();
			break;
		case "location":
			locationService.refreshCache();
			break;
		case "logistics":
			logisticsService.refreshExpressCom();
			break;
		case "partner":
			partnerService.refreshPartnerCache();
			break;
		default:
			logger.info("no such type : " + type);
			break;
		}
		return "success";
	}
	
	}
