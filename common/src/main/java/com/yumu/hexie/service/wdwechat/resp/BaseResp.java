package com.yumu.hexie.service.wdwechat.resp;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 5月 星期二
 * @Desc :
 */
public class BaseResp<T> {

    private String code;
    private String message;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //异常时返回
    public static BaseResp fail(String errMsg){
        BaseResp r = new BaseResp();
        r.setCode("0");
        r.setMessage(errMsg);
        return r;
    }

    //成功时返回
    public static  BaseResp success(Object obj){
        BaseResp r = new BaseResp();
        r.setCode("1");
        r.setMessage("处理成功");
        r.setData(obj);
        return r;
    }

    @Override
    public String toString() {
        return "BaseResp{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

