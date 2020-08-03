package com.yumu.hexie.integration.common;

import java.io.Serializable;

public class QueryListDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7009800342095816973L;

	private int totalPages;
	private long totalSize;
	
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

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		return "QueryListDTO [totalPages=" + totalPages + ", totalSize=" + totalSize + ", content=" + content + "]";
	}

	
}
