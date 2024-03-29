package com.yumu.hexie.service.sales;

import java.util.List;

import com.yumu.hexie.integration.eshop.vo.QueryRgroupsVO;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.vo.RgroupOrder;
import com.yumu.hexie.vo.RgroupOrdersVO;

public interface RgroupService {
    //刷新团购状态
    void refreshGroupStatus(RgroupRule rule);

    List<RgroupOrder> queryMyRgroupOrders(long userId, List<Integer> status);

    List<RgroupAreaItem> addProcessStatus(List<RgroupAreaItem> result);

	void noticeArrival(QueryRgroupsVO queryRgroupsVO);

	void refreshGroupDeliveryStatus(RgroupRule rule);

	List<RgroupOrdersVO> queryMyRgroupOrdersV3(long userId, List<Integer> status, String productName, String ruleIdStr,
			List<Integer> itemStatus, int currentPage);

	RgroupOrdersVO queryRgroupOrderDetailV3(long userId, String orderIdStr);

	void refreshGroupStatus4MultiRegions(RgroupRule rule);

}
