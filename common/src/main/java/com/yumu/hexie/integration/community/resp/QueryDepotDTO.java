package com.yumu.hexie.integration.community.resp;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.eshop.mapper.SaleAreaMapper;

public class QueryDepotDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1124623558694632939L;
	
	private QueryProDepotResp content;
	private List<SaleAreaMapper> saleArea;
	
	public QueryProDepotResp getContent() {
		return content;
	}
	public void setContent(QueryProDepotResp content) {
		this.content = content;
	}
	public List<SaleAreaMapper> getSaleArea() {
		return saleArea;
	}
	public void setSaleArea(List<SaleAreaMapper> saleArea) {
		this.saleArea = saleArea;
	}
	
	
}
