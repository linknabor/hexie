package com.yumu.hexie.web.sales;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.vo.QueryRgroupsVO;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.DistributionService;
import com.yumu.hexie.service.common.RgroupV3Service;
import com.yumu.hexie.service.sales.CustomOrderService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.vo.RgroupVO;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@SuppressWarnings("unchecked")
@Controller(value = "rgroupController")
public class RgroupController extends BaseController{
    @Inject
    private RgroupService rgroupService;
    @Inject
    private CustomOrderService customRgroupService;
    @Inject
    private DistributionService distributionService;
    @Autowired
    private RgroupV3Service rgroupV3Service;

	@RequestMapping(value = "/rgroups/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupAreaItem>> rgroups(@ModelAttribute(Constants.USER)User user,@PathVariable int page) throws Exception {
		List<RgroupAreaItem> items = distributionService.queryRgroups(user, page);
        return new BaseResult<List<RgroupAreaItem>>().success(rgroupService.addProcessStatus(items));
    }
	
	@RequestMapping(value = "/getRgroupRule/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<RgroupAreaItem> getRgroupRule(@PathVariable long ruleId) throws Exception {
		return BaseResult.successResult(customRgroupService.findSalePlan(ruleId));
    }
	
	
	@RequestMapping(value = "/rgroups/v2/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupAreaItem>> getRgroups(@ModelAttribute(Constants.USER)User user,
				@PathVariable int page) throws Exception {
		
		List<RgroupAreaItem> items = distributionService.queryRgroupsV2(user, page);
        return new BaseResult<List<RgroupAreaItem>>().success(rgroupService.addProcessStatus(items));
    }
	
	/**
	 * 团购到货通知
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/notice/arriaval", method = RequestMethod.POST)
	@ResponseBody
	public CommonResponse<Object> noticeArriaval(@RequestBody QueryRgroupsVO queryRgroupsVO) throws Exception {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			rgroupService.noticeArrival(queryRgroupsVO);
			commonResponse.setData(Constants.PAGE_SUCCESS);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");
		}
        return commonResponse;
    }
	
	@RequestMapping(value = "/rgroups/v3/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Map<String, String>> saveRgroups(@RequestBody RgroupVO createRgroupReq) throws Exception {
		
		long ruleId = rgroupV3Service.saveRgroup(createRgroupReq);
		Map<String, String> map = new HashMap<>();
		map.put("ruleId", String.valueOf(ruleId));
        return new BaseResult<Map<String, String>>().success(map);
    }
	
	@RequestMapping(value = "/rgroups/v3/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupAreaItem>> getRgroupsV3(@ModelAttribute(Constants.USER)User user,
				@PathVariable int page) throws Exception {
		
		List<RgroupAreaItem> items = distributionService.queryRgroupsV2(user, page);
        return new BaseResult<List<RgroupAreaItem>>().success(rgroupService.addProcessStatus(items));
    }
	
	@RequestMapping(value = "/rgroups/v3/queryByRule/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<RgroupVO> getRgroupsByRuleV3(@PathVariable String ruleId) throws Exception {
		
		RgroupVO vo = rgroupV3Service.queryRgroupByRule(ruleId);
        return new BaseResult<RgroupVO>().success(vo);
    }
	
}
