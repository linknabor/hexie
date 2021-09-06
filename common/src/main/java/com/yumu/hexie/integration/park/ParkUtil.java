package com.yumu.hexie.integration.park;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.RequestUtil;
import com.yumu.hexie.integration.common.RestUtil;
import com.yumu.hexie.integration.park.req.PayUserCarInfo;
import com.yumu.hexie.integration.park.req.SaveCarInfo;
import com.yumu.hexie.integration.park.resp.*;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-18 17:47
 */
@Service
public class ParkUtil {

    @Autowired
    private RequestUtil requestUtil;
    @Autowired
    private RestUtil restUtil;

    private static final String QUERY_PARK_MORE_URL = "park/getUserParkMoreSDO.do";
    private static final String QUERY_PARK_LIST_URL = "park/getParkListSDO.do";
    private static final String QUERY_USER_CAR_LIST_URL = "park/getCarListSDO.do";
    private static final String DEL_USER_CAR_URL = "park/delUserCarSDO.do";
    private static final String QUERY_USER_PAY_CAR_LIST_URL = "park/getUserPayCarListSDO.do";
    private static final String ADD_USER_CAR_URL = "park/addUserCarSDO.do";
    private static final String QUERY_USER_PAYING_DETAIL_URL = "park/getPayingDetailSDO.do";
    private static final String GET_USER_PRE_PAY_URL = "park/getUserPrePaySDO.do";
    private static final String GET_PAY_DETAIL_URL = "park/getPayDetailByIdSDO.do";
    /**
     * 查询用户车辆、停车场和规则信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<UserCarList> getUserCar(User user) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_PARK_MORE_URL;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(user.getId()));
        map.put("appid", String.valueOf(user.getAppId()));

        TypeReference<CommonResponse<UserCarList>> typeReference = new TypeReference<CommonResponse<UserCarList>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 查询停车场信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<List<ParkInfo>> getParkList(User user, String parkName) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_PARK_LIST_URL;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(user.getId()));
        map.put("parkName", parkName);

        TypeReference<CommonResponse<List<ParkInfo>>> typeReference = new TypeReference<CommonResponse<List<ParkInfo>>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 查询用户车辆信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<List<UserCarList.CarInfo>> getCarList(User user, String carNo) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_USER_CAR_LIST_URL;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(user.getId()));
        map.put("appid", user.getAppId());
        map.put("carNo", carNo);

        TypeReference<CommonResponse<List<UserCarList.CarInfo>>> typeReference = new TypeReference<CommonResponse<List<UserCarList.CarInfo>>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 查询用户车辆信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> delCar(User user, String carNo) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += DEL_USER_CAR_URL;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(user.getId()));
        map.put("appid", user.getAppId());
        map.put("carNo", carNo);

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 查询用户支付信息
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<List<PayCarInfo>> getParkPayList(User user, String carNo) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_USER_PAY_CAR_LIST_URL;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(user.getId()));
        map.put("appid", user.getAppId());
        map.put("carNo", carNo);

        TypeReference<CommonResponse<List<PayCarInfo>>> typeReference = new TypeReference<CommonResponse<List<PayCarInfo>>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 添加客户车牌
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<Boolean> addUserCar(User user, SaveCarInfo saveCarInfo) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += ADD_USER_CAR_URL;

        saveCarInfo.setUser_id(String.valueOf(user.getId()));
        saveCarInfo.setAppid(user.getAppId());

        TypeReference<CommonResponse<Boolean>> typeReference = new TypeReference<CommonResponse<Boolean>>(){};
        return restUtil.exchangeOnUri(requestUrl, saveCarInfo, typeReference);
    }

    /**
     * 支付前的支付详情
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<PayingDetail> getPayingDetail(User user, String carNo, String parkId) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += QUERY_USER_PAYING_DETAIL_URL;

        Map<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(user.getId()));
        map.put("appid", user.getAppId());
        map.put("parkId", parkId);
        map.put("carNo", carNo);

        TypeReference<CommonResponse<PayingDetail>> typeReference = new TypeReference<CommonResponse<PayingDetail>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }

    /**
     * 获取统一支付ID
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<WechatPayInfo> getPrePaying(User user, PayUserCarInfo payUserCarInfo) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += GET_USER_PRE_PAY_URL;

        payUserCarInfo.setAppid(user.getAppId());
        payUserCarInfo.setUser_id(String.valueOf(user.getId()));
        payUserCarInfo.setOpenid(user.getOpenid());

        TypeReference<CommonResponse<WechatPayInfo>> typeReference = new TypeReference<CommonResponse<WechatPayInfo>>(){};
        return restUtil.exchangeOnUri(requestUrl, payUserCarInfo, typeReference);
    }

    /**
     * 根据交易ID查询交易详情
     * @param user
     * @return
     * @throws Exception
     */
    public CommonResponse<PayDetail> getPayDetailById(User user, String orderId) throws Exception {
        String requestUrl = requestUtil.getRequestUrl(user, null);
        requestUrl += GET_PAY_DETAIL_URL;

        Map<String, String> map = new HashMap<>();
        map.put("appid", user.getAppId());
        map.put("user_id", String.valueOf(user.getId()));
        map.put("order_id", orderId);
        TypeReference<CommonResponse<PayDetail>> typeReference = new TypeReference<CommonResponse<PayDetail>>(){};
        return restUtil.exchangeOnUri(requestUrl, map, typeReference);
    }
}
