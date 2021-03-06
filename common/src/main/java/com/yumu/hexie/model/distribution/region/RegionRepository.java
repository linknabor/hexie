package com.yumu.hexie.model.distribution.region;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegionRepository extends JpaRepository<Region, Long> {
    public List<Region> findAllByRegionType(int regionType);
    public List<Region> findAllByRegionType(int regionType,Pageable page);
	
	public List<Region> findAllByRegionTypeAndParentId(int regionType,long parentId);
	public List<Region> findAllByParentId(long parentId);
	
	public List<Region> findAllByParentIdAndName(long countyId,String xiaoquName);
    public List<Region> findByAmapId(long amapId);
    
    public Region findByName(String name);
    
    public List<Region> findAllByNameAndParentName(String name,String parentName);
    
    public List<Region> findByNameAndRegionType(String name,int regionType);
    
    @Query(" from Region  where regionType < 4")
    public List<Region> findNeedRegion();
    
    public List<Region> findAllBySectId(String sectId);
    
    @Query(" from Region  where regionType = 4 and sectId is null ")
    public List<Region> getRegionList();
    
    @Query(nativeQuery=true,value="select id from region  where  sectId in ?1")
	public List<String> getRegionBySectid(List<String> sect_ids);
    
    public List<Region> findBySectIdIn(List<String> sectId);
    
	@Query(value = "select r.name, r.parentName, r.sectId from region r join onsaleareaitem item on r.id = item.regionId where item.productId = ?1 ", nativeQuery = true)
	public List<Object[]> findByProductId(String productId);
	
	@Query(value = "select r.name, r.parentName, r.sectId from region r join rgroupareaitem item on r.id = item.regionId where item.productId = ?1 ", nativeQuery = true)
	public List<Object[]> findByProductId4Rgroup(String productId);
	
	@Query(value = "select distinct r.* from region r join onsaleareaitem item on r.id = item.regionId "
			+ "join product p on item.productId = p.id "
			+ "where item.status = ?1 "
			+ "and (COALESCE(?2) IS NULL OR (item.productId IN (?2) )) "
			+ "and (COALESCE(?3) IS NULL OR (item.productId not in (?3) )) "
			+ "and IF (?4!='', p.agentId = ?4, 1=1) "
			, nativeQuery = true)
	public List<Region> findByAgentIdOrProductId(int status, List<String> productIds, List<String> uproductIds, String agentId);
	
	@Query(value = "select distinct r.* from region r join rgroupareaitem item on r.id = item.regionId "
			+ "join product p on item.productId = p.id "
			+ "where item.status = ?1 "
			+ "and (COALESCE(?2) IS NULL OR (item.productId IN (?2) )) "
			+ "and (COALESCE(?3) IS NULL OR (item.productId not in (?3) )) "
			+ "and IF (?4!='', p.agentId = ?4, 1=1) "
			, nativeQuery = true)
	public List<Region> findByAgentIdOrProductId4Rgroup(int status, List<String> productIds, List<String> uproductIds, String agentId);
	
}
