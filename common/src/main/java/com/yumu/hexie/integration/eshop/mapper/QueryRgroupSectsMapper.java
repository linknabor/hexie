package com.yumu.hexie.integration.eshop.mapper;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class QueryRgroupSectsMapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8937552127610792844L;
	private BigInteger groupCounts;
	private BigInteger id;	//regionid
	private String name;	//regionName
	private String address;	//xiaoquAddress
	
	private List<String> groupImages;	//至多4个图片链接

	public QueryRgroupSectsMapper() {
		super();
	}
	

	public QueryRgroupSectsMapper(BigInteger groupCounts, BigInteger id, String name, String address) {
		super();
		this.groupCounts = groupCounts;
		this.id = id;
		this.name = name;
		this.address = address;
	}

	public BigInteger getGroupCounts() {
		return groupCounts;
	}

	public void setGroupCounts(BigInteger groupCounts) {
		this.groupCounts = groupCounts;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getGroupImages() {
		return groupImages;
	}

	public void setGroupImages(List<String> groupImages) {
		this.groupImages = groupImages;
	}
	
	

}
