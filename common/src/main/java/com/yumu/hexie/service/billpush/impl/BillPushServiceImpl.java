package com.yumu.hexie.service.billpush.impl;

import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.billpush.BillPushService;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.shequ.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
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
    @Autowired
    private NoticeService noticeService;

    @Override
    public String sendMessage(BillPushDetail billPushDetail) {
        List<User> userList = userRepository.findByWuyeIdAndAppId(billPushDetail.getWuyeId(), billPushDetail.getAppid());
        User user;
        if (userList == null || userList.isEmpty()) {
            return "用户未注册";
        }else {
            user = userList.get(0);
        }

        //添加消息到消息中心
        CommunityRequest request = new CommunityRequest();
        request.setTitle("物业账单通知");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("房号:").append(billPushDetail.getCellAddr()).append("\n");
            sb.append("账期:").append(billPushDetail.getPeriod()).append("\n");
            sb.append("代缴金额:").append(billPushDetail.getFeePrice());
            request.setContent(sb.toString());
            request.setSummary(sb.toString());
            request.setAppid(user.getAppId());
            request.setOpenid(user.getOpenid());
            request.setNoticeType(12);
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
            request.setPublishDate(df1.format(new Date()));
            noticeService.addOutSidNotice(request);
        } catch (Exception e) {
            logger.error("保存账单推送信息到消息中心失败", e);
        }

        logger.info("will sent wuye message to user : " + user);
        return gotongService.sendBillPush(user.getOpenid(), user.getAppId(), billPushDetail);
    }
}
