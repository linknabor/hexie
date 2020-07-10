package com.yumu.hexie.integration.shelf.dto;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.integration.shelf.mapper.SaleAreaMapper;

public class QueryProductDTO<T> implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1104988238986438365L;

	private T content;
	private List<SaleAreaMapper> saleArea;

	public List<SaleAreaMapper> getSaleArea() {
		return saleArea;
	}

	public void setSaleArea(List<SaleAreaMapper> saleArea) {
		this.saleArea = saleArea;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "QueryProductDTO [content=" + content + ", saleArea=" + saleArea + "]";
	}

	
}
