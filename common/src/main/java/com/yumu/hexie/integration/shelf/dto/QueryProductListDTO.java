package com.yumu.hexie.integration.shelf.dto;

import java.io.Serializable;

public class QueryProductListDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7009800342095816973L;

	private int totalPages;
	
	private T content;

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Page [totalPages=" + totalPages + ", content=" + content + "]";
	}
	
	
}
