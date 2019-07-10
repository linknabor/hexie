package com.yumu.hexie.service.user;

import java.util.List;

import com.yumu.hexie.model.distribution.region.Region;

public interface RegionService {
	
	public Region getRegionInfoByName(String name);
	
	public Region getRegionInfoById(long id);
	
	public Region saveRegion(Region r);
	
	public List<Region> getRegionAll();

}
