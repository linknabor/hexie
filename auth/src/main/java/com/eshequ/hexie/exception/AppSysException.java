/**
 * 
 */
package com.eshequ.hexie.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huym
 *
 */
public class AppSysException extends RuntimeException {
	
	private static Logger logger = LoggerFactory.getLogger(AppSysException.class);
	
	private int code = Integer.MIN_VALUE;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8531279862043501712L;

	public AppSysException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}

	public AppSysException(Throwable cause) {
		super(cause);
		logger.error(cause.getMessage(), cause);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	

}
