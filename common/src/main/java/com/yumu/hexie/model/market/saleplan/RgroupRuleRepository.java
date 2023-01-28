package com.yumu.hexie.model.market.saleplan;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yumu.hexie.model.ModelConstant;

public interface RgroupRuleRepository extends JpaRepository<RgroupRule, Long> {
	
	public RgroupRule findById(long ruleId);
	
	@Query("from RgroupRule p where p.status = "+ModelConstant.RULE_STATUS_ON+" and p.endDate<=?1 and p.groupStatus="+ModelConstant.RGROUP_STAUS_GROUPING)
	public List<RgroupRule> findTimeoutGroup(Date date);
	
	public RgroupRule findByIdAndStatusIn(long ruleId, List<Integer> statusList);
	
	@Query(value = "select * from RgroupRule where ownerId = ?1 "
			+ "and if(?2!='', description like CONCAT('%',?2,'%'), 1=1) "
			+ "and status <> " + ModelConstant.RULE_STATUS_DEL + " "
			, countQuery = "select count(1) from RgroupRule where ownerId = ?1 "
			+ "and if(?2!='', description like CONCAT('%',?2,'%'), 1=1) "
			+ "and status <> " + ModelConstant.RULE_STATUS_DEL + " "
			, nativeQuery = true)
	public Page<RgroupRule> findByOwnerIdAndDescriptionLike(long ownerId, String description, Pageable pageable);
	
	@Query(value = "select distinct rule.* from RgroupRule rule "
			+ "join rgroupareaitem item on item.ruleId = rule.id "
			+ "where rule.status in ?1 "
			+ "and item.regionId = ?2 "
			+ "and if(?3!='', description like CONCAT('%',?3,'%'), 1=1) "
			+ "and rule.createDate >= 1659283200000 "	//老版本不要查出来
			, countQuery = "select count(*) from ( select distinct rule.* from RgroupRule rule "
					+ "join rgroupareaitem item on item.ruleId = rule.id "
					+ "where rule.status in ?1 "
					+ "and item.regionId = ?2 "
					+ "and if(?3!='', description like CONCAT('%',?3,'%'), 1=1) "
					+ "and rule.createDate >= 1659283200000 "	//老版本不要查出来
					+ ") a "
			, nativeQuery = true)
	public Page<RgroupRule> findByRegionId(List<Integer> status, long regionId, String title, Pageable pageable);
	
	public List<RgroupRule> findAllByProductId(long productId);
	
	public List<RgroupRule> findByGroupStatus(int groupStatus);
	
	@Transactional
	@Modifying
	@Query(value = "update RgroupRule set status = ?1 where id = ?2 ", nativeQuery = true)
	void updateStatus(int status, long id);
	
	
	//不要修改顺序--团购
	String sqlColumn1 = " rule.id, rule.createDate, rule.startDate, rule.endDate, rule.name, rule.price, "
			+ "rule.currentNum, rule.groupStatus, p.mainPicture, item.areaLeader, item.areaLeaderAddr, item.areaLeaderTel, item.areaLeaderId, "
			+ "rule.groupFinishDate,  rule.freeShippingNum, rule.limitNumOnce, rule.postageFee, 0 as delivered ";
	
	/**
	 * 查询团购信息
	 * @param productType
	 * @param ruleId
	 * @param ruleName
	 * @param groupStatus
	 * @param agentId
	 * @param isDemo
	 * @param pageable
	 * @return
	 */
	@Query(value = "select " + sqlColumn1
			+ "from rgrouprule rule "
			+ "join product p on rule.productId = p.id "
			+ "left join agent a on a.id = p.agentId "
			+ "join rgroupareaitem item on item.productId = p.id "
			+ "left join productplat pp on p.id = pp.productId "
			+ "where p.productType = ?1 "
			+ "and IF (?2!='', rule.id = ?2, 1=1) "
			+ "and IF (?3!='', rule.name like CONCAT('%',?3,'%'), 1=1) "
			+ "and (COALESCE(?4) IS NULL OR (rule.groupStatus IN (?4) )) "
			+ "and IF (?5!='', rule.createDate >= ?5, 1=1) "
			+ "and IF (?6!='', rule.createDate <= ?6, 1=1) "
			+ "and IF (?7!='', a.agentNo = ?7, 1=1) "
			+ "and IF (?8!='', p.demo = ?8, 1=1) "
			+ "and IF (?9!='', item.areaLeaderId = ?9, 1=1) "
			+ "and (COALESCE(?10) IS NULL OR (item.regionId IN (?10) )) "
			+ "and rule.status = " + ModelConstant.RULE_STATUS_ON
			+ " group by rule.id "
			, countQuery = "select rule.id, count(1) from rgrouprule rule "
					+ "join product p on rule.productId = p.id "
					+ "left join agent a on a.id = p.agentId "
					+ "join rgroupareaitem item on item.productId = p.id "
					+ "left join productplat pp on p.id = pp.productId "
					+ "where p.productType = ?1 "
					+ "and IF (?2!='', rule.id = ?2, 1=1) "
					+ "and IF (?3!='', rule.name like CONCAT('%',?3,'%'), 1=1) "
					+ "and (COALESCE(?4) IS NULL OR (rule.groupStatus IN (?4) )) "
					+ "and IF (?5!='', rule.createDate >= ?5, 1=1) "
					+ "and IF (?6!='', rule.createDate <= ?6, 1=1) "
					+ "and IF (?7!='', a.agentNo = ?7, 1=1) "
					+ "and IF (?8!='', p.demo = ?8, 1=1) "
					+ "and IF (?9!='', item.areaLeaderId = ?9, 1=1) "
					+ "and (COALESCE(?10) IS NULL OR (item.regionId IN (?10) )) "
					+ "and rule.status = " + ModelConstant.RULE_STATUS_ON
					+ " group by rule.id "
					, nativeQuery = true)
	Page<Object[]> findByMultiCondRgroup(String productType, String ruleId, String ruleName, List<Integer>groupStatus, 
			String startDate, String endDate, String agentId, String isDemo, String leaderId, List<String>sectList, Pageable pageable);



	//查询团购列表(团长端)
	String sqlColumn2 = " rule.id, rule.createDate, rule.description, rule.descriptionMore, rule.startDate, rule.endDate, rule.price, rule.status, "
			+ "rule.groupStatus, rule.currentNum ";
	@Query(value = "select " + sqlColumn2
			+ "from rgrouprule rule "
			+ "where rule.ownerId = ?1 "
			+ "and IF (?2!='', rule.description like CONCAT('%',?2,'%'), 1=1) "
			+ "and IF (?3='1', rule.status = '1' "
			+ "and rule.startDate <= CURRENT_TIMESTAMP() "
			+ "and rule.endDate >= CURRENT_TIMESTAMP(), "
			+ "IF(?3='2', rule.status = '0' "
			+ "and rule.startDate <= CURRENT_TIMESTAMP() "
			+ "and rule.endDate >= CURRENT_TIMESTAMP(), "
			+ "IF(?3='3', rule.status = '1' "
			+ "and rule.startDate > CURRENT_TIMESTAMP() , "
			+ "IF(?3='4', rule.endDate < CURRENT_TIMESTAMP(), 1=1) ))) "
			+ "and rule.status <> " + ModelConstant.RULE_STATUS_DEL + " "
			, countQuery = "select count(1) from rgrouprule rule "
				+ "where rule.ownerId = ?1 "
				+ "and IF (?2!='', rule.description like CONCAT('%',?2,'%'), 1=1) "
				+ "and IF (?3='1', rule.status = '1' "
				+ "and rule.startDate <= CURRENT_TIMESTAMP() "
				+ "and rule.endDate >= CURRENT_TIMESTAMP(), "
				+ "IF(?3='2', rule.status = '0' "
				+ "and rule.startDate <= CURRENT_TIMESTAMP() "
				+ "and rule.endDate >= CURRENT_TIMESTAMP(), "
				+ "IF(?3='3', rule.status = '1' "
				+ "and rule.startDate > CURRENT_TIMESTAMP() , "
				+ "IF(?3='4', (rule.endDate < CURRENT_TIMESTAMP() or rule.status = '2' ), 1=1) ))) "
				+ "and rule.status <> " + ModelConstant.RULE_STATUS_DEL + " "
			, nativeQuery = true)
	Page<Object[]> findRgroupList(long ownerId, String description, String groupStatus, Pageable pageable);

	String sqlColumn3 = "a.id,a.ownerId,a.ownerName,a.ownerAddr,a.ownerImg,a.ownerTel,a.price,a.description,a.status,a.groupStatus,a.startDate,a.endDate, a.descriptionMore, count( distinct b.id) productNum ";
	@Query(value = "select DISTINCT " + sqlColumn3
			+ "from rgrouprule a "
			+ "join ProductRule b on a.id = b.ruleId "
			+ "join Product c on b.productId = c.id "
			+ "where c.depotId = ?1 "
			+ "group by a.id "
			, nativeQuery = true)
	List<Object[]> queryGroupByDepotId(String depotId);
	
	
	@Query(value = "select " + sqlColumn3
			+ "from rgrouprule a "
			+ "join ProductRule b on a.id = b.ruleId "
			+ "where 1 = 1 "
			+ "and IF (?1!='', a.name like CONCAT('%',?1,'%'), 1=1) "
			+ "and IF (?2!='', a.ownerName like CONCAT('%',?2,'%'), 1=1) "
			+ "and IF (?3>0, a.ownerId = ?3, 1=1) "
			+ "group by a.id "
			, countQuery = "select count(1) from rgrouprule a "
			+ "join ProductRule b on a.id = b.ruleId "
			+ "where 1 = 1 "
			+ "and IF (?1!='', a.name like CONCAT('%',?1,'%'), 1=1) "
			+ "and IF (?2!='', a.ownerName like CONCAT('%',?2,'%'), 1=1) "
			+ "and IF (?3>0, a.ownerId = ?3, 1=1) "
			+ "group by a.id "
			, nativeQuery = true)
	Page<Object[]> queryGroupByOutSid(String name, String ownerName, long ownerId, Pageable pageable);
	
	
	/**
	 * 首页查询正在进行的团购小区列表
	 * @param productType
	 * @param ruleId
	 * @param ruleName
	 * @param groupStatus
	 * @param agentId
	 * @param isDemo
	 * @param pageable
	 * @return
	 */
	@Query(value = "select count(distinct rule.id) as groupCounts, region.id, region.name, region.xiaoquAddress "
			+ "from rgrouprule rule "
			+ "join rgroupareaitem item on item.ruleId = rule.id "
			+ "join region on region.id = item.regionId "
			+ "where rule.status in ?1 "
			+ "and rule.createDate >= 1659283200000 "	//老版本不要查出来
			+ "and IF (?2!='', region.name like CONCAT('%',?2,'%'), 1=1) "
//			+ "and item.ruleCloseTime > ?3 "
			+ "group by region.id, region.name, region.xiaoquAddress "
			+ "order by rule.id desc "
			, countQuery = "select count(*) from ( select count(distinct rule.id), region.id, region.name, region.xiaoquAddress "
					+ "from rgrouprule rule "
					+ "join rgroupareaitem item on item.ruleId = rule.id "
					+ "join region on region.id = item.regionId "
					+ "where rule.status in ?1 "
					+ "and rule.createDate >= 1659283200000 "	//老版本不要查出来
					+ "and IF (?2!='', region.name like CONCAT('%',?2,'%'), 1=1) "
//					+ "and item.ruleCloseTime > ?3 "
					+ "group by region.id, region.name, region.xiaoquAddress "
					+ "order by rule.id desc ) a "
					, nativeQuery = true)
	Page<Object[]> findGroupSects(List<Integer> status, String sectName, Pageable pageable);
	
	@Query(value = "select distinct rule.* from rgroupRule rule "
			+ "join rgroupareaitem item on item.ruleId = rule.id "
			+ "where rule.status in ?1 "
			+ "and item.ruleCloseTime > ?2 "
			+ "and item.regionId = ?3"
			, nativeQuery = true)
	List<RgroupRule> findByAreaItem(List<Integer> status, long currentDate, long regionId);
}
