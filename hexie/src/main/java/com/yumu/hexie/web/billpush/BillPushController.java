package com.yumu.hexie.web.billpush;

import com.yumu.hexie.service.billpush.BillPushService;
import com.yumu.hexie.service.billpush.vo.BillPushDetail;
import com.yumu.hexie.web.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-02-22 12:09
 */
@RestController
public class BillPushController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BillPushController.class);

    @Autowired
    private BillPushService billPushService;

    /**
     * 物业账单通知
     *
     * @param billPushDetail
     * @return
     */
    @RequestMapping(value = "/servplat/billpush/send", method = RequestMethod.POST)
    public String pullWechat(@RequestBody BillPushDetail billPushDetail) {
        logger.info("billpush:--billPushDetail:" + billPushDetail);
        boolean success = billPushService.sendMessage(billPushDetail);
        return Boolean.toString(success);
    }
}
