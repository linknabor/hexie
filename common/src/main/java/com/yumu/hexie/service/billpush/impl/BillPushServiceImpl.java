package com.yumu.hexie.service.billpush.impl;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.billpush.BillPushService;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;
import com.yumu.hexie.service.common.GotongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-02-22 14:30
 */
@Service
public class BillPushServiceImpl implements BillPushService {

    private static Logger logger = LoggerFactory.getLogger(BillPushServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GotongService gotongService;

    @Override
    public boolean sendMessage(BillPushDetail billPushDetail) {
        boolean success = false;
        boolean successFlag = false;

        List<User> userList = userRepository.findByWuyeId(billPushDetail.getWuyeId());
        User user;
        if (userList == null || userList.isEmpty()) {
            user = new User();
        }else {
            user = userList.get(0);
        }
        logger.info("will sent wuye message to user : " + user);
        success = gotongService.sendBillPush(user.getOpenid(), user.getAppId(), billPushDetail);

        if (success) {
            successFlag = true;	//当前这户，有一个绑定者成功就算成功
        }
        return successFlag;
    }
}
