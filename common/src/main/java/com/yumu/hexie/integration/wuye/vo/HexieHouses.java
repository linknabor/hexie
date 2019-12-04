package com.yumu.hexie.integration.wuye.vo;

import java.io.Serializable;
import java.util.List;

public class HexieHouses implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7529282683369860714L;
	
	private List<HexieHouse> houses;

	public List<HexieHouse> getHouses() {
		return houses;
	}

	public void setHouses(List<HexieHouse> houses) {
		this.houses = houses;
	}
	
	
}
