package com.yumu.hexie.integration.wdwechat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.service.wdwechat.req.WdCenterReq;
import com.yumu.hexie.service.wdwechat.resp.BaseResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Package : Wechat
 * @Author :
 * @Date : 2023 6月 星期五
 * @Desc :
 */
@Service
public class WdWechatUtil {

    public final static String BaseUrl = "https://guangming-center-api.mart4meta.com/";
    public final static String SyncUserInfoUrl = "/api/v1/center/syncInfo";

    @Autowired
    private RestUtil restUtil;

    public BaseResp<WdCenterReq> sycnWdUserInfo(Map<String, Object> map) throws Exception {
        String requestUrl = BaseUrl + SyncUserInfoUrl;
        TypeReference<BaseResp<WdCenterReq>> typeReference = new TypeReference<BaseResp<WdCenterReq>>(){};
        return restUtil.exchangeOnBody(requestUrl, map, typeReference);
    }


}
