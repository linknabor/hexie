package com.eshequ.hexie.common;

public class ResultCode {

	private String code;

    private String message;
    
	ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/* 成功状态码 */
	public static ResultCode SUCCESS() {
		return new ResultCode("0000", "成功");
	}
	
	/* 失败状态码 */
	public static ResultCode FAILURE() {
		return new ResultCode("9999", "失败");
	}
	
	public static ResultCode FAILURE(String code, String msg) {
		return new ResultCode(code, msg);
	}
	
	public static ResultCode FAILURE(String msg) {
		return new ResultCode("9999", msg);
	}
	
	
}
