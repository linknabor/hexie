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
public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8142555289107865320L;
	
	private int code = Integer.MIN_VALUE;
	
	
	private static Logger logger = LoggerFactory.getLogger(BusinessException.class);
	
	
	public BusinessException() {
		
	}

	public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		logger.error(message, cause);
	}


	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		logger.error(message, cause);
	}


	public BusinessException(String message) {
		super(message);
		logger.error(message);
	}
	
	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
		logger.error(message);
	}


	public BusinessException(Throwable cause) {
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
