package com.yumu.hexie.integration.renovation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.renovation.req.SaveRenovationReq;
import com.yumu.hexie.integration.renovation.resp.RenovationInfoResp;
import com.yumu.hexie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2024 12月 星期五
 * @Desc :
 */
@Service
public class RenovationUtil {
    @Autowired
    private RequestUtil requestUtil;
    @Autowired
    private RestUtil restUtil;

    private static final String QUERY_RENOVATION_LIST_URL = "renovation/getRenovationListSDO.do";
    private static final String QUERY_RENOVATION_INFO_URL = "renovation/getRenovationInfoSDO.do";
    private static final String SAVE_RENOVATION_URL = "renovation/saveRenovationSDO.do";
    private static final String CANCEL_RENOVATION_URL = "renovation/cancelRenovationSDO.do";

    /**
     * 查询用户登记信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<List<RenovationInfoResp>> getRenovationList(User user, String sectId) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_RENOVATION_LIST_URL;

        String openid = user.getOpenid();
        String appid = user.getAppId();
        if(!StringUtils.isEmpty(user.getMiniopenid())) {
            openid = user.getMiniopenid();
            appid = user.getMiniAppId();
        }

        Map<String, String> map = new HashMap<>();
        map.put("openid", openid);
        map.put("appid", appid);
        map.put("sectId", sectId);
        TypeReference<CommonResponse<List<RenovationInfoResp>>> typeReference = new TypeReference<CommonResponse<List<RenovationInfoResp>>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 根据ID查询登记信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<RenovationInfoResp> getRenovationInfo(User user, String registerId) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_RENOVATION_INFO_URL;

        Map<String, String> map = new HashMap<>();
        map.put("register_id", registerId);

        TypeReference<CommonResponse<RenovationInfoResp>> typeReference = new TypeReference<CommonResponse<RenovationInfoResp>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 保存用户登记信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> saveRenovation(User user, SaveRenovationReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += SAVE_RENOVATION_URL;

        String openid = user.getOpenid();
        String appid = user.getAppId();
        if(!StringUtils.isEmpty(user.getMiniopenid())) {
            openid = user.getMiniopenid();
            appid = user.getMiniAppId();
        }
        req.setAppid(appid);
        req.setOpenid(openid);
        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 登记作废
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> cancelRenovation(User user, String registerId) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += CANCEL_RENOVATION_URL;

        Map<String, String> map = new HashMap<>();
        map.put("register_id", registerId);

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

}
