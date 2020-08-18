package com.yumu.hexie.web.sales;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.commonsupport.info.ProductCategory;
import com.yumu.hexie.model.distribution.OnSaleAreaItem;
import com.yumu.hexie.model.market.saleplan.SalePlan;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.DistributionService;
import com.yumu.hexie.service.sales.CustomOrderService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@Controller(value = "saleController")
public class SaleController extends BaseController{
    @Inject
    private CustomOrderService customOnSaleService;
    @Inject
    private DistributionService distributionService;
	
	@RequestMapping(value = "/onsales/{type}/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<OnSaleAreaItem>> orders(@ModelAttribute(Constants.USER)User user,@PathVariable int type,
			@PathVariable int page) throws Exception {
		return new BaseResult<List<OnSaleAreaItem>>().success(distributionService.queryOnsales(user,type,page));
    }
	
	@RequestMapping(value = "/getOnSaleRule/{ruleId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<SalePlan> getRgroupRule(@PathVariable long ruleId) throws Exception {
		return new BaseResult<SalePlan>().success(customOnSaleService.findSalePlan(ruleId));
    }
	
	
	@RequestMapping(value = "/onsales/v2/{type}/{category}/{page}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<OnSaleAreaItem>> getOnSales(@ModelAttribute(Constants.USER)User user, 
			@PathVariable int type, @PathVariable int page, @PathVariable int category) throws Exception {
		
		return new BaseResult<List<OnSaleAreaItem>>().success(distributionService.queryOnsalesV2(user,type,category,page));
    }
	
	@RequestMapping(value = "/getOnsaleCategory/{type}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<ProductCategory>> getOnSales(@ModelAttribute(Constants.USER)User user, 
			@PathVariable int type) throws Exception {
		
		return new BaseResult<List<ProductCategory>>().success(distributionService.queryCategory(user, type));
    }
	
}
