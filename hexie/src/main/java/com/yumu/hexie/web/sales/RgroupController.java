package com.yumu.hexie.web.sales;

import java.util.List;

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
import com.yumu.hexie.service.common.PublishService;
import com.yumu.hexie.service.sales.CustomOrderService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.vo.CreateRgroupReq;
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
    private PublishService publishService;

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
	
	@RequestMapping(value = "/rgroups/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> saveRgroups(@RequestBody CreateRgroupReq createRgroupReq) throws Exception {
		
		publishService.saveRgroup(createRgroupReq);
        return new BaseResult<String>().success(Constants.PAGE_SUCCESS);
    }
	
	
}
