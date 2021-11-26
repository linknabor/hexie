package com.yumu.hexie.service.sales;

import java.util.List;

import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.vo.RgroupOrder;

public interface RgroupService {
	 List<RgroupOrder> queryMyRgroupOrders(long userId,List<Integer> status);
	
	 List<RgroupAreaItem> addProcessStatus(List<RgroupAreaItem> result);

}
