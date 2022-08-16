package com.yumu.hexie.service.user;

import java.util.List;

import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.vo.QQMapVO;

public interface RegionService {
	
	public List<Region> findByNameAndParentId(String name,Long parentId);
	
	public Region getRegionInfoById(long id);
	
	public Region saveRegion(Region r);
	
	public List<Region> getRegionAll();
	
	public List<Region> findAllBySectId(String sectId);

	public List<Region> findByNameLikeAndType(String name);

	List<Region> findByRgroupOwner(User user);

	void saveOwnerServiceArea(User user, Region region);

	void delOwnerServiceArea(User user, long regionId);

	Region createSect(User user, QQMapVO mapVO);
	
}
