package com.yumu.hexie.service.shequ.impl;

import com.yumu.hexie.integration.park.ParkUtil;
import com.yumu.hexie.integration.park.req.PayUserCarInfo;
import com.yumu.hexie.integration.park.resp.ParkInfo;
import com.yumu.hexie.integration.park.resp.PayCarInfo;
import com.yumu.hexie.integration.park.resp.PayingDetail;
import com.yumu.hexie.integration.park.resp.UserCarList;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.extreinfo.CarInfo;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.ParkService;
import com.yumu.hexie.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public UserCarList getUserCar(User user) throws Exception {
        User userDB = userService.getById(user.getId());
        if(userDB == null) {
            return null;
        }
        return parkUtil.getUserCar(user).getData();
    }

    @Override
    public List<ParkInfo> getParkList(User user, String parkName) throws Exception {
        return parkUtil.getParkList(user, parkName).getData();
    }

    @Override
    public List<UserCarList.CarInfo> getCarList(User user, String carNo) throws Exception {
        return parkUtil.getCarList(user, carNo).getData();
    }

    @Override
    public List<PayCarInfo> getParkPayList(User user, String carNo) throws Exception {
        return parkUtil.getParkPayList(user, carNo).getData();
    }

    @Override
    public Boolean addUserCar(User user, String carNo) throws Exception {
        return parkUtil.addUserCar(user, carNo).getData();
    }

    @Override
    public PayingDetail getPayingDetail(User user, String carNo, String parkId) throws Exception {
        return parkUtil.getPayingDetail(user, carNo, parkId).getData();
    }

    @Override
    public WechatPayInfo getPrePaying(User user, PayUserCarInfo payUserCarInfo) throws Exception {
        return parkUtil.getPrePaying(user, payUserCarInfo).getData();
    }
}
