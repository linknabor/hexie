package com.yumu.hexie.service.evoucher;

import java.util.List;

import com.yumu.hexie.model.market.Evoucher;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.User;

public interface EvoucherService {

	void createEvoucher(ServiceOrder serviceOrder);

	void enable(ServiceOrder serviceOrder);

	void consume(User operator, String code, String evouchers) throws Exception;

	List<Evoucher> getEvoucher(String code);
	
	List<Evoucher> getByUser(User user);
	
	List<Evoucher> getByOrder(long orderId);

	List<ServiceOrder> getEvoucherOrders(User user, List<Integer> status);


}
