package com.yumu.hexie.web.shequ;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.park.req.PayUserCarInfo;
import com.yumu.hexie.integration.park.req.SaveCarInfo;
import com.yumu.hexie.integration.park.resp.*;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.ParkService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-08-18 17:41
 */
@RestController
@RequestMapping("/park")
public class ParkController extends BaseController {

    @Autowired
    private ParkService parkService;

    /**
     * 停车场首页
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getIndexCar", method = RequestMethod.GET)
    public BaseResult<UserCarList> getIndexCar(@ModelAttribute(Constants.USER) User user, @RequestParam(required = false) String parkId) throws Exception {
        UserCarList userCarList = parkService.getUserCar(user, parkId);
        return BaseResult.successResult(userCarList);
    }

    /**
     * 获取停车场信息
     * @param user
     * @param parkName
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getParkList", method = RequestMethod.GET)
    public BaseResult<List<ParkInfo>> getParkList(@ModelAttribute(Constants.USER) User user, @RequestParam(required = false) String parkName) throws Exception {
        List<ParkInfo> parkInfos = parkService.getParkList(user, parkName);
        return BaseResult.successResult(parkInfos);
    }

    /**
     * 根据车牌号模糊查询车辆
     * @param user
     * @param carNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getVagueCar", method = RequestMethod.GET)
    public BaseResult<List<UserCarList.CarInfo>> getVagueCar(@ModelAttribute(Constants.USER) User user, @RequestParam(required = false) String carNo) throws Exception {
        List<UserCarList.CarInfo> parkInfos = parkService.getCarList(user, carNo);
        return BaseResult.successResult(parkInfos);
    }

    /**
     * 根据车牌删除车辆
     * @param user
     * @param carNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delCar/{carNo}", method = RequestMethod.POST)
    public BaseResult<Boolean> delCar(@ModelAttribute(Constants.USER) User user, @PathVariable String carNo) throws Exception {
        Boolean flag = parkService.delCar(user, carNo);
        return BaseResult.successResult(flag);
    }

    /**
     * 获取缴费记录
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getParkPayList", method = RequestMethod.GET)
    public BaseResult<List<PayCarInfo>> getParkPayList(@ModelAttribute(Constants.USER) User user) throws Exception {
        List<PayCarInfo> parkInfos = parkService.getParkPayList(user);
        return BaseResult.successResult(parkInfos);
    }

    /**
     * 添加车牌号
     * @param user
     * @param saveCarInfo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addUserCar", method = RequestMethod.POST)
    public BaseResult<Boolean> addUserCar(@ModelAttribute(Constants.USER) User user, @RequestBody SaveCarInfo saveCarInfo) throws Exception {
        Boolean flag = parkService.addUserCar(user, saveCarInfo);
        return BaseResult.successResult(flag);
    }

    /**
     * 唤起支付前的详情页
     * @param user
     * @param carNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getPayingDetail", method = RequestMethod.GET)
    public BaseResult<PayingDetail> getPayingDetail(@ModelAttribute(Constants.USER) User user, @RequestParam String carNo, @RequestParam String parkId) throws Exception {
        PayingDetail payingDetail = parkService.getPayingDetail(user, carNo, parkId);
        return BaseResult.successResult(payingDetail);
    }

    /**
     * 创建交易，获取预支付ID
     * @param user
     * @param payUserCarInfo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getPrePaying", method = RequestMethod.POST)
    public BaseResult<WechatPayInfo> getPaying(@ModelAttribute(Constants.USER) User user, @RequestBody PayUserCarInfo payUserCarInfo) throws Exception {
        WechatPayInfo wechatPayInfo = parkService.getPrePaying(user, payUserCarInfo);
        return BaseResult.successResult(wechatPayInfo);
    }

    /**
     * 根据交易ID查询交易详情
     * @param user
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getPayDetailById", method = RequestMethod.GET)
    public BaseResult<PayDetail> getPayDetailById(@ModelAttribute(Constants.USER) User user, @RequestParam String orderId) throws Exception {
        PayDetail payDetail = parkService.getPayDetailById(user, orderId);
        return BaseResult.successResult(payDetail);
    }

    /**
     * 根据车牌查询停车费账单
     * @param user
     * @param payUserCarInfo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCarBill", method = RequestMethod.POST)
    public BaseResult<CarBillList> getCarBill(@ModelAttribute(Constants.USER) User user, @RequestBody PayUserCarInfo payUserCarInfo) throws Exception {
        CarBillList carBillList = parkService.getCarBillList(user, payUserCarInfo);
        return BaseResult.successResult(carBillList);
    }

}
