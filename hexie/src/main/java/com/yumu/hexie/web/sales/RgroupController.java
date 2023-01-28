package com.yumu.hexie.web.sales;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.eshop.mapper.QueryRgroupSectsMapper;
import com.yumu.hexie.integration.eshop.vo.QueryRgroupsVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.DistributionService;
import com.yumu.hexie.service.common.RgroupV3Service;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.sales.CustomOrderService;
import com.yumu.hexie.service.sales.RgroupService;
import com.yumu.hexie.service.search.SearchService;
import com.yumu.hexie.vo.RgroupRecordsVO;
import com.yumu.hexie.vo.RgroupSubscribeVO;
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
    @Autowired
    private SearchService searchService;
    
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
	
	/**
	 * 3版团购新增、编辑
	 * @param createRgroupReq
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/save", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Map<String, String>> saveRgroups(@RequestBody RgroupVO createRgroupReq) throws Exception {
		
		long ruleId = rgroupV3Service.saveRgroup(createRgroupReq);
		Map<String, String> map = new HashMap<>();
		map.put("ruleId", String.valueOf(ruleId));
        return new BaseResult<Map<String, String>>().success(map);
    }
	
	/**
	 * 3版团购发布上架
	 * @param createRgroupReq
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/pub/{ruleId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Map<String, String>> pubRgroups(@ModelAttribute(Constants.USER)User user, @PathVariable long ruleId) throws Exception {
		
		rgroupV3Service.updateRgroupStatus(ruleId, true);
		Map<String, String> map = new HashMap<>();
		map.put("ruleId", String.valueOf(ruleId));
        return new BaseResult<Map<String, String>>().success(map);
    }
	
	/**
	 * 3版团购查询列表
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupVO>> getRgroupsV3(@ModelAttribute(Constants.USER)User user, @RequestParam(required = false) String title,
				@PathVariable int page) throws Exception {
		
		List<RgroupVO> voList = rgroupV3Service.queryOwnerRgroups(user, title, page);
        return new BaseResult<List<RgroupVO>>().success(voList);
	}
	
	/**
	 * 3版团购根据团购id查询
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/queryByRule/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<RgroupVO> getRgroupsByRuleV3(@PathVariable String ruleId) throws Exception {
		
		RgroupVO vo = rgroupV3Service.queryRgroupByRule(ruleId, false);
        return new BaseResult<RgroupVO>().success(vo);
    }
	
	/**
	 * 3版本团购根据id查询已发布的团购
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/queryOnSales/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<RgroupVO> getRgroupsOnSales(@PathVariable String ruleId) throws Exception {
		
		RgroupVO vo = rgroupV3Service.queryRgroupByRule(ruleId, false);
        return new BaseResult<RgroupVO>().success(vo);
    }
	
	/**
	 * 3版本团购根据id查询已发布的团购
	 * @param ruleId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/orderRecords/{ruleId}/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<RgroupRecordsVO> getOrderRecords(@PathVariable String ruleId, @PathVariable int page) throws Exception {
		
		RgroupRecordsVO vo = rgroupV3Service.queryOrderRecords(ruleId, page);
        return new BaseResult<RgroupRecordsVO>().success(vo);
    }
	
	/**
	 * 获取退款原因
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/refund/reason", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Map<String, String>>> getRefundReason() throws Exception {
        return new BaseResult<List<Map<String, String>>>().success(rgroupV3Service.getRefundReason());
    }
	
	/**
	 * 获取退款原因
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/productFromsales", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Product>> getProductFromSales(@ModelAttribute(Constants.USER)User user, @RequestParam int currentPage, 
			@RequestParam String searchValue, @RequestParam String excludeDepotIds) throws Exception {
		
		List<String> excludeIdList = null;
		if (!StringUtils.isEmpty(excludeDepotIds)) {
			String[]excludeIds = new String[0];
			excludeIds = excludeDepotIds.split(",");
			excludeIdList = Arrays.asList(excludeIds);
		}
        return new BaseResult<List<Product>>().success(rgroupV3Service.getProductFromSales(user, searchValue, excludeIdList, currentPage));
    }
	
	/**
	 * 获取有正在进行团购的小区列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/sects", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<QueryRgroupSectsMapper>> getGroupSects(@ModelAttribute(Constants.USER)User user, @RequestParam(required = false) String sectName,
			@RequestParam int page) throws Exception {
		
        return new BaseResult<List<QueryRgroupSectsMapper>>().success(rgroupV3Service.getGroupSects(user, sectName, page));
    }
	
	/**
	 * 获取当前小区的团购列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/sect/groups", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<RgroupVO>> getSectGroups(@ModelAttribute(Constants.USER)User user, @RequestParam(required = false) String regionId,
			@RequestParam(required = false) String title, @RequestParam int page) throws Exception {
		
		if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(title.trim())) {
			String key = ModelConstant.KEY_RGROUP_SECT_TITLE_SEARCH + user.getMiniopenid();
			searchService.save(key, title);
		}
        return new BaseResult<List<RgroupVO>>().success(rgroupV3Service.getSectGroups(user, regionId, title, page));
    }
	
	/**
	 * 查询团购搜索历史记录
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/searchHistory/{type}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Object> searchHistory(@ModelAttribute(Constants.USER)User user, @PathVariable(required = true) String type) throws Exception {
		
		String keyType = "";
		if ("0".equals(type)) {
			keyType = ModelConstant.KEY_RGROUP_SECT_TITLE_SEARCH;
		} else if ("1".equals(type)) {
			keyType = ModelConstant.KEY_RGROUP_LEADER_TITLE_SEARCH;
		} else {
			throw new BizValidateException("unknow search type : " + type);
		}
		String key = keyType + user.getMiniopenid();
		Set<String> set = searchService.get(key);
        return new BaseResult<Object>().success(set);
    }
	
	/**
	 * 删除团购搜索历史记录
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/delSearchHistory/{type}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Object> deleteSearchHistory(@ModelAttribute(Constants.USER)User user, @PathVariable(required = true) String type) throws Exception {

		String keyType = "";
		if ("0".equals(type)) {
			keyType = ModelConstant.KEY_RGROUP_SECT_TITLE_SEARCH;
		} else if ("1".equals(type)) {
			keyType = ModelConstant.KEY_RGROUP_LEADER_TITLE_SEARCH;
		} else {
			throw new BizValidateException("unknow search type : " + type);
		}
		String key = keyType + user.getMiniopenid();
		searchService.removeAll(key);
        return new BaseResult<Object>().success(Constants.PAGE_SUCCESS);
    }
	
	/**
	 * 获取团长信息
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/leader/{leaderId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Object> getLeaderInfo(@PathVariable String leaderId) throws Exception {
		
        return new BaseResult<Object>().success(rgroupV3Service.getLeaderInfo(leaderId));
    }
	
	/**
	 * 获取团长下的团购
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/rgroups/v3/leader/groups", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Object> getLeaderInfo(@ModelAttribute(Constants.USER)User user, @RequestParam(required = false) String leaderId, @RequestParam(required = false) String title, 
			@RequestParam(required = false) int page) throws Exception {
		
		if (!StringUtils.isEmpty(title) && !StringUtils.isEmpty(title.trim())) {
			String key = ModelConstant.KEY_RGROUP_LEADER_TITLE_SEARCH + user.getMiniopenid();
			searchService.save(key, title);
		}
		
        return new BaseResult<Object>().success(rgroupV3Service.getLeadGroups(leaderId, title, page));
    }
	
	/**
	 * 团购访问统计
	 * @param user
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/rgroups/v3/visitView", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Object> visitView(@ModelAttribute(Constants.USER)User user, @RequestBody Map<String, String> map) {
		
		rgroupV3Service.visitView(user, map.get("ruleId"), map.get("ownerId"));
		return new BaseResult<Object>().success(Constants.PAGE_SUCCESS);
    }
	
	/**
	 * 用户订阅
	 * @param user
	 * @param rgroupSubscribeVO
	 * @return
	 */
	@RequestMapping(value = "/rgroups/v3/subscribe", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Object> subscribe(@ModelAttribute(Constants.USER)User user, @RequestBody RgroupSubscribeVO rgroupSubscribeVO) {
		
		rgroupV3Service.subscribe(user, rgroupSubscribeVO);
		return new BaseResult<Object>().success(Constants.PAGE_SUCCESS);
    }
	
	/**
	 * 用户取消订阅
	 * @param user
	 * @param rgroupSubscribeVO
	 * @return
	 */
	@RequestMapping(value = "/rgroups/v3/unsubscribe", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Object> unsubscribe(@ModelAttribute(Constants.USER)User user, @RequestBody RgroupSubscribeVO rgroupSubscribeVO) {
		
		rgroupV3Service.unsubscribe(user, rgroupSubscribeVO);
		return new BaseResult<Object>().success(Constants.PAGE_SUCCESS);
    }
	
	/**
	 * 用户订阅
	 * @param user
	 * @param rgroupSubscribeVO
	 * @return
	 */
	@RequestMapping(value = "/rgroups/v3/userSubscribe", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Object> userSubscribe(@ModelAttribute(Constants.USER)User user, @RequestBody RgroupSubscribeVO rgroupSubscribeVO) {
		
		boolean b = rgroupV3Service.getUserSubscribe(user, rgroupSubscribeVO);
		return new BaseResult<Object>().success(b);
    }
	
	/**
     * 邀请团长
     * @param user
     * @param productDepotReq
     * @return
     * @throws Exception 
     */
	@RequestMapping(value = "/groupLeader/invitation/{code}", method = RequestMethod.POST)
	@ResponseBody
    public BaseResult<String> scanInvitation(@ModelAttribute(Constants.USER) User user, @PathVariable String code) throws Exception {
    	
    	rgroupV3Service.inviteGroupLeader(user, code);
        return BaseResult.successResult(Constants.PAGE_SUCCESS);
    }
	
}
