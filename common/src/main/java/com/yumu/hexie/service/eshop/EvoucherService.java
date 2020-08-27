package com.yumu.hexie.service.eshop;

import java.util.List;

import com.yumu.hexie.model.agent.Agent;
import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.EvoucherPageMapper;
import com.yumu.hexie.vo.EvoucherView;

public interface EvoucherService {

	void createEvoucher(ServiceOrder serviceOrder);
	
	void enable(ServiceOrder serviceOrder);

	void consume(User operator, String code, String evouchers) throws Exception;

	EvoucherView getEvoucher(String code);
	
	List<Evoucher> getByUserAndType(User user, int type);
	
	EvoucherView getByOrder(long orderId);
	
	List<EvoucherPageMapper> getByOperator(User user) throws Exception;

	List<ServiceOrder> getEvoucherOrders(User user, List<Integer> status);

	Evoucher getEvoucherByCode(String code);

	Evoucher createSingle4Promotion(Agent agent);

}
