package com.yumu.hexie.service.o2o;

import java.util.List;

import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.model.localservice.HomeBillItem;
import com.yumu.hexie.model.localservice.HomeCart;
import com.yumu.hexie.model.localservice.bill.YunXiyiBill;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.o2o.req.CommonBillReq;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: XiyiService.java, v 0.1 2016年4月11日 上午12:20:06  Exp $
 */
public interface XiyiService {
    
    YunXiyiBill createBill(User user, CommonBillReq req, HomeCart cart);
    JsSign pay(YunXiyiBill bill, User user);
    void update4Payment(PaymentOrder payment);
    void notifyPayed(long billId);
    void cancel(long billId, long userId);
    void signed(long billId, long userId);
    
    void timeout(long billId);
    
    List<YunXiyiBill> queryBills(long userId, int page);
    
    YunXiyiBill queryById(long id);
    List<HomeBillItem> findItems(long billId);
}
