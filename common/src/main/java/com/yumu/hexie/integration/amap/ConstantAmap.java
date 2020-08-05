package com.yumu.hexie.integration.amap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 高德地图配置
 */
@Component
public class ConstantAmap {
	
	@Value("${amap.cloudkey}")
	private String cloudKey;
	@Value("${amap.tableid}")
	private String tableid;
	@Value("${amap.list}")
	private String amapList;
	@Value("${amap.page}")
	private String amapPage;
	
	protected static String DEFAULTLIMIT;
	protected static String DEFAULTPAGE;
	protected static String AMAPTABLEID;
	protected static String AMAPCLOUDKEY;
	
	@PostConstruct
	public void init() {
		
		DEFAULTLIMIT = amapList;
		DEFAULTPAGE = amapPage;
		AMAPTABLEID = tableid;
		AMAPCLOUDKEY = cloudKey;
		
	}
}
