package com.yumu.hexie.service.common;

import java.util.List;
import java.util.Map;

import com.yumu.hexie.integration.eshop.mapper.QueryRgroupSectsMapper;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.RgroupRecordsVO;
import com.yumu.hexie.vo.RgroupSubscribeVO;
import com.yumu.hexie.vo.RgroupVO;
import com.yumu.hexie.vo.RgroupVO.RgroupOwnerVO;

public interface RgroupV3Service {


	long saveRgroup(RgroupVO createRgroupReq);
	
	RgroupVO queryRgroupByRule(String rgroupRuleId, boolean isOnsale);

	void updateRgroupStatus(long ruleId, boolean isPub) throws Exception;

	List<RgroupVO> queryOwnerRgroups(User user, String title, int currentPage);

	RgroupRecordsVO queryOrderRecords(String ruleId, int currentPage);

	List<Map<String, String>> getRefundReason();

	List<Product> getProductFromSales(User user, String productName, List<String> excludDepotIds, int currentPage);

	List<QueryRgroupSectsMapper> getGroupSects(User user, String sectName, int currentPage) throws Exception;

	List<RgroupVO> getSectGroups(User user, String regionId, String title, int currentPage) throws Exception;

	RgroupOwnerVO getLeaderInfo(String groupLeaderId);

	List<RgroupVO> getLeadGroups(String groupLeaderId, String title, int currentPage) throws Exception;

	void visitView(User user, String ruleIdStr, String ownerIdStr);

	void subscribe(User user, RgroupSubscribeVO rgroupSubscribeVO);

	void unsubscribe(User user, RgroupSubscribeVO rgroupSubscribeVO);

	void sendPubMsg(String ruleId);

	boolean getUserSubscribe(User user, RgroupSubscribeVO rgroupSubscribeVO);

}
