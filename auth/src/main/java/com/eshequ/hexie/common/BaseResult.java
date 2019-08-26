package com.eshequ.hexie.common;

import java.io.Serializable;

public class BaseResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String respCode;
	private String respDesc;
	private Object result;
	
	//成功 不返回数据直接返回成功信息
    public static BaseResult success() {
    	BaseResult result = new BaseResult();
        result.setResultCode(ResultCode.SUCCESS());
        return result;
    }
    
    //成功 并且加上返回数据
    public static BaseResult success(Object data) {
    	BaseResult result = new BaseResult();
        result.setResultCode(ResultCode.SUCCESS());
        result.setResult(data);
        return result;
    }
    
    // 单返回失败的状态码
    public static BaseResult failure(ResultCode resultCode) {
    	BaseResult result = new BaseResult();
        result.setResultCode(resultCode);
        return result;
    }
    
    // 返回失败的状态码 及 数据
    public static BaseResult failure(ResultCode resultCode, Object data) {
    	BaseResult result = new BaseResult();
        result.setResultCode(resultCode);
        result.setResult(data);
        return result;
    }
    
    public void setResultCode(ResultCode code) {
        this.respCode = code.getCode();
        this.respDesc = code.getMessage();
    }
	
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "BaseResult [respCode=" + respCode + ", respDesc=" + respDesc + ", result=" + result + "]";
	}

	
}
