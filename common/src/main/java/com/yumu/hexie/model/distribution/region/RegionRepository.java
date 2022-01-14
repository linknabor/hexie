package com.yumu.hexie.model.distribution.region;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegionRepository extends JpaRepository<Region, Long> {
	Region findById(long id);
    List<Region> findAllByRegionType(int regionType);

	List<Region> findAllByRegionTypeAndParentId(int regionType, long parentId);

	List<Region> findAllByParentIdAndName(long countyId, String xiaoquName);

    List<Region> findByNameAndRegionType(String name, int regionType);
    
    @Query(" from Region  where regionType < 4")
	List<Region> findNeedRegion();
    
    List<Region> findAllBySectId(String sectId);
    
    @Query(nativeQuery=true,value="select id from region  where  sectId in ?1")
	List<String> getRegionBySectid(List<String> sect_ids);
    
	@Query(value = "select r.name, r.parentName, r.sectId from region r join onsaleareaitem item on r.id = item.regionId where item.productId = ?1 ", nativeQuery = true)
	List<Object[]> findByProductId(String productId);
	
	@Query(value = "select r.name, r.parentName, r.sectId from region r join rgroupareaitem item on r.id = item.regionId where item.productId = ?1 ", nativeQuery = true)
	List<Object[]> findByProductId4Rgroup(String productId);
	
	@Query(value = "select distinct r.* from region r join onsaleareaitem item on r.id = item.regionId "
			+ "join product p on item.productId = p.id "
			+ "where item.status = ?1 "
			+ "and (COALESCE(?2) IS NULL OR (item.productId IN (?2) )) "
			+ "and (COALESCE(?3) IS NULL OR (item.productId not in (?3) )) "
			+ "and IF (?4!='', p.agentId = ?4, 1=1) "
			, nativeQuery = true)
	List<Region> findByAgentIdOrProductId(int status, List<String> productIds, List<String> uproductIds, String agentId);
	
	@Query(value = "select distinct r.* from region r join rgroupareaitem item on r.id = item.regionId "
			+ "join product p on item.productId = p.id "
			+ "where item.status = ?1 "
			+ "and (COALESCE(?2) IS NULL OR (item.productId IN (?2) )) "
			+ "and (COALESCE(?3) IS NULL OR (item.productId not in (?3) )) "
			+ "and IF (?4!='', p.agentId = ?4, 1=1) "
			, nativeQuery = true)
	List<Region> findByAgentIdOrProductId4Rgroup(int status, List<String> productIds, List<String> uproductIds, String agentId);

	@Query(value = "select r.id as regionId, r.name, r.parentName, r.sectId, item.productId, item.productName, "
			+ "item.arealeaderId leaderId, item.areaLeader as operName, item.areaLeaderAddr as groupAddr from region r "
			+ "join rgroupareaitem item on r.id = item.regionId "
			+ "where item.productId = ?1 "
			, nativeQuery = true)
	List<Object[]> findRgroupLeader(String productId);
}
