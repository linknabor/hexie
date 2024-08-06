package com.yumu.hexie.service.shequ.impl;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.park.ParkUtil;
import com.yumu.hexie.integration.park.req.PayUserCarInfo;
import com.yumu.hexie.integration.park.req.SaveCarInfo;
import com.yumu.hexie.integration.park.resp.*;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.ParkService;
import com.yumu.hexie.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-18 17:58
 */
@Service("parkService")
public class ParkServiceImpl implements ParkService {

    @Autowired
    private ParkUtil parkUtil;

    @Autowired
    private UserService userService;

    @Override
    public UserCarList getUserCar(User user, String parkId) throws Exception {
        User userDB = userService.getById(user.getId());
        if(userDB == null) {
            return null;
        }
        CommonResponse<UserCarList> commonResponse = parkUtil.getUserCar(userDB, parkId);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public List<ParkInfo> getParkList(User user, String parkName) throws Exception {
        if (!StringUtils.isEmpty(parkName)) {
            try {
                parkName = URLEncoder.encode(parkName,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        CommonResponse<List<ParkInfo>> commonResponse = parkUtil.getParkList(user, parkName);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public List<UserCarList.CarInfo> getCarList(User user, String carNo) throws Exception {
        if (!StringUtils.isEmpty(carNo)) {
            try {
                carNo = URLEncoder.encode(carNo,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        CommonResponse<List<UserCarList.CarInfo>> commonResponse = parkUtil.getCarList(user, carNo);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public Boolean delCar(User user, String carNo) throws Exception {
        if (!StringUtils.isEmpty(carNo)) {
            try {
                carNo = URLEncoder.encode(carNo,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        CommonResponse<Boolean> commonResponse = parkUtil.delCar(user, carNo);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public List<PayCarInfo> getParkPayList(User user) throws Exception {
        CommonResponse<List<PayCarInfo>> commonResponse = parkUtil.getParkPayList(user);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public Boolean addUserCar(User user, SaveCarInfo saveCarInfo) throws Exception {
        CommonResponse<Boolean> commonResponse = parkUtil.addUserCar(user, saveCarInfo);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public PayingDetail getPayingDetail(User user, String carNo, String parkId) throws Exception {
        if (!StringUtils.isEmpty(carNo)) {
            try {
                carNo = URLEncoder.encode(carNo,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        CommonResponse<PayingDetail> commonResponse = parkUtil.getPayingDetail(user, carNo, parkId);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public WechatPayInfo getPrePaying(User user, PayUserCarInfo payUserCarInfo) throws Exception {
        User userDB = userService.getById(user.getId());
        if(userDB == null) {
            return null;
        }
        payUserCarInfo.setUser_id(String.valueOf(userDB.getId()));
        if(ModelConstant.H5_USER_TYPE_ALIPAY.equals(payUserCarInfo.getScanChannel())) {
            payUserCarInfo.setAppid(userDB.getAliappid());
            payUserCarInfo.setOpenid(userDB.getAliuserid());
        } else if(ModelConstant.H5_USER_TYPE_WECHAT.equals(payUserCarInfo.getScanChannel())) {
            payUserCarInfo.setAppid(userDB.getAppId());
            payUserCarInfo.setOpenid(userDB.getOpenid());
        } else {
            throw new BizValidateException("不支持当前支付渠道");
        }
        CommonResponse<WechatPayInfo> commonResponse = parkUtil.getPrePaying(userDB, payUserCarInfo);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public CarBillList getCarBillList(User user, PayUserCarInfo payUserCarInfo) throws Exception {
        CommonResponse<CarBillList> commonResponse = parkUtil.getCarBillList(user, payUserCarInfo);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }

    @Override
    public String getInvoiceQrCode(User user, String trade_water_id) throws Exception {
        CommonResponse<String> commonResponse = parkUtil.getInvoiceQrCode(user, trade_water_id);
        if("99".equals(commonResponse.getResult())) {
            throw new BizValidateException(commonResponse.getErrMsg());
        } else {
            return commonResponse.getData();
        }
    }
}
