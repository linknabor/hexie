package com.yumu.hexie.integration.interact;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.interact.req.InteractReq;
import com.yumu.hexie.integration.interact.req.SaveInteractCommentReq;
import com.yumu.hexie.integration.interact.req.SaveInteractInfoReq;
import com.yumu.hexie.integration.interact.resp.InteractCommentResp;
import com.yumu.hexie.integration.interact.resp.InteractInfoResp;
import com.yumu.hexie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package : WechatTerminal
 * @Author :
 * @Date : 2023 8月 星期五
 * @Desc :
 */
@Service
public class InteractUtil {

    @Value("${sysName}")
    private String sysName;
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private RequestUtil requestUtil;

    private static final String INTERACT_LIST_URL = "interact/getInteractListSDO.do";
    private static final String INTERACT_TYPE_URL = "interact/getInteractTypeSDO.do";
    private static final String INTERACT_INFO_URL = "interact/getInteractInfoSDO.do";
    private static final String INTERACT_SAVE_INFO_URL = "interact/saveInteractInfoSDO.do";
    private static final String INTERACT_DEL_INFO_URL = "interact/delInteractInfoSDO.do";
    private static final String INTERACT_COMMENT_LIST_URL = "interact/getInteractCommentListSDO.do";
    private static final String INTERACT_SAVE_COMMENT_URL = "interact/saveInteractCommentSDO.do";
    private static final String INTERACT_DEL_COMMENT_URL = "interact/delInteractCommentSDO.do";
    private static final String INTERACT_SAVE_GRADE_URL = "interact/saveInteractGradeSDO.do";

    /**
     * 查询互动列表
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<List<InteractInfoResp>> getInteractList(User user, InteractReq req) throws Exception {

        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_LIST_URL;
        req.setUserId(String.valueOf(user.getId()));
        req.setAppid(user.getAppId());

        TypeReference<CommonResponse<List<InteractInfoResp>>> typeReference = new TypeReference<CommonResponse<List<InteractInfoResp>>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 获取互动分类代码项
     * @param user
     * @return
     * @throws IOException
     */
    public CommonResponse<List<Map<String, String>>> getInteractType(User user) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_TYPE_URL;
        TypeReference<CommonResponse<List<Map<String, String>>>> typeReference = new TypeReference<CommonResponse<List<Map<String, String>>>>(){};
        return restUtil.exchangeOnUri(requestUrl, null, typeReference);
    }

    /**
     * 保存互动主信息
     * @param user
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> saveInteractInfo(User user, SaveInteractInfoReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_SAVE_INFO_URL;
        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 删除互动主信息
     * @param user
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> delInteractInfo(User user, InteractReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_DEL_INFO_URL;

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 根据互动ID查询主信息
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<InteractInfoResp> getInteractInfo(User user, InteractReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_INFO_URL;
        req.setUserId(String.valueOf(user.getId()));
        req.setAppid(user.getAppId());

        TypeReference<CommonResponse<InteractInfoResp>> typeReference = new TypeReference<CommonResponse<InteractInfoResp>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 查询互动列表
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<List<InteractCommentResp>> getCommentList(User user, InteractReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_COMMENT_LIST_URL;
        req.setUserId(String.valueOf(user.getId()));
        req.setAppid(user.getAppId());

        TypeReference<CommonResponse<List<InteractCommentResp>>> typeReference = new TypeReference<CommonResponse<List<InteractCommentResp>>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 保存回复信息
     * @param user
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<InteractCommentResp> saveInteractComment(User user, SaveInteractCommentReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_SAVE_COMMENT_URL;
        TypeReference<CommonResponse<InteractCommentResp>> typeReference = new TypeReference<CommonResponse<InteractCommentResp>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    /**
     * 删除回复信息
     * @param user
     * @param req
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> delInteractComment(User user, InteractReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_DEL_COMMENT_URL;

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

    public CommonResponse<InteractInfoResp> saveInteractGrade(User user, InteractReq req) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, "");
        requestUrl += INTERACT_SAVE_GRADE_URL;

        TypeReference<CommonResponse<InteractInfoResp>> typeReference = new TypeReference<CommonResponse<InteractInfoResp>>(){};
        return restUtil.exchangeOnUri(requestUrl, req, typeReference);
    }

}
