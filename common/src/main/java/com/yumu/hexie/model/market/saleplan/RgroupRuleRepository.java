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
	
	@Query("from RgroupRule p where p.status = "+ModelConstant.RULE_STATUS_ON+" and p.endDate<=?1 and p.groupStatus="+ModelConstant.RGROUP_STAUS_GROUPING)
	public List<RgroupRule> findTimeoutGroup(Date date);
	

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
			+ "and IF (?7!='', a.id = ?7, 1=1) "
			+ "and IF (?8!='', p.demo = ?8, 1=1) "
			+ "and IF (?9!='', item.areaLeaderId = ?9, 1=1) "
			+ "and (COALESCE(?10) IS NULL OR (item.regionId IN (?10) )) "
			+ "group by rule.id "
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
					+ "and IF (?7!='', a.id = ?7, 1=1) "
					+ "and IF (?8!='', p.demo = ?8, 1=1) "
					+ "and IF (?9!='', item.areaLeaderId = ?9, 1=1) "
					+ "and (COALESCE(?10) IS NULL OR (item.regionId IN (?10) )) "
					+ "group by rule.id "
					, nativeQuery = true)
	Page<Object[]> findByMultiCondRgroup(String productType, String ruleId, String ruleName, List<Integer>groupStatus, 
			String startDate, String endDate, String agentId, String isDemo, String leaderId, List<String>sectList, Pageable pageable);

}
