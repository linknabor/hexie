package com.yumu.hexie.integration.eshop.dto;

import java.io.Serializable;
import java.util.List;

public class QueryCouponCfgDTO<T, V> implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1104988238986438365L;

	private T content;
	private List<V> support;
	private List<V> notSupport;
	
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
	public List<V> getSupport() {
		return support;
	}
	public void setSupport(List<V> support) {
		this.support = support;
	}
	public List<V> getNotSupport() {
		return notSupport;
	}
	public void setNotSupport(List<V> notSupport) {
		this.notSupport = notSupport;
	}
	@Override
	public String toString() {
		return "QueryCouponCfgDTO [content=" + content + ", support=" + support + ", notSupport=" + notSupport + "]";
	}
	
	
}
