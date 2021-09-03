package com.yumu.hexie.service.shequ;

import com.yumu.hexie.integration.park.req.PayUserCarInfo;
import com.yumu.hexie.integration.park.req.SaveCarInfo;
import com.yumu.hexie.integration.park.resp.ParkInfo;
import com.yumu.hexie.integration.park.resp.PayCarInfo;
import com.yumu.hexie.integration.park.resp.PayingDetail;
import com.yumu.hexie.integration.park.resp.UserCarList;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.extreinfo.CarInfo;
import com.yumu.hexie.model.user.User;

import java.util.List;

public interface ParkService {

    UserCarList getUserCar(User user) throws Exception;

    List<ParkInfo> getParkList(User user, String parkName) throws Exception;

    List<UserCarList.CarInfo> getCarList(User user, String carNo) throws Exception;

    List<PayCarInfo> getParkPayList(User user, String carNo) throws Exception;

    Boolean addUserCar(User user, SaveCarInfo saveCarInfo) throws Exception;

    PayingDetail getPayingDetail(User user, String carNo, String parkId) throws Exception;

    WechatPayInfo getPrePaying(User user, PayUserCarInfo payUserCarInfo) throws Exception;

}
