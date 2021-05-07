package com.yumu.hexie.service.billpush;

import com.yumu.hexie.service.billpush.vo.BillPushDetail;

public interface BillPushService {

    //物业账单推送
    String sendMessage(BillPushDetail billPushDetail);
}
