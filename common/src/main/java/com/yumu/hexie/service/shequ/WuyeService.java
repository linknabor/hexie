package com.yumu.hexie.service.shequ;

import javax.xml.bind.ValidationException;

import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PayResult;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;

public interface WuyeService {

	//0. 快捷缴费信息
	public BillListVO quickPayInfo(String stmtId, String currPage, String totalCount);
	// 1.房产列表
	public HouseListVO queryHouse(String userId);
	// 2.绑定房产
	public HexieUser bindHouse(String userId,String stmtId,String houseId);
	//无账单绑定
	public HexieUser bindHouseNoStmt(String userId,String houseId, String area);
	// 3.删除房产
	public BaseResult<String> deleteHouse(String userId,String houseId);
	// 4.根据订单查询房产信息
	public HexieHouse getHouse(String userId,String stmtId);
	// 5.用户登录
	public HexieUser userLogin(String openId);
	// 6.缴费记录查询
	public PayWaterListVO queryPaymentList(String userId,String startDate,String endDate);
	// 7.缴费详情
	public PaymentInfo queryPaymentDetail(String userId,String waterId);
	//status 00,01,02? startDate 2015-02
	// 8.账单记录
	public BillListVO queryBillList(String userId,String payStatus,String startDate,String endDate,String currentPage, String totalCount,String house_id);
	// 9.账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	public PaymentInfo getBillDetail(String userId,String stmtId,String anotherbillIds);
	// 10.缴费
	public WechatPayInfo getPrePayInfo(String userId,String billId,String stmtId, 
				String openId, String couponUnit, String couponNum, 
				String couponId,String mianBill,String mianAmt, String reduceAmt, 
				String invoice_title_type, String credit_code, String mobile, String invoice_title) throws Exception;
	// 11.通知已支付
	public PayResult noticePayed(String userId,String billId,String stmtId, String tradeWaterId, String packageId);
	// 12.查询是否已经用过红包
	public String queryCouponIsUsed(String userId);
	//13.更新电子发票抬头信息
	public String updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id);
	//14.根据交易号获取对应房子的发票信息
	public InvoiceInfo getInvoiceByTradeId(String trade_water_id);
	
	//15.根据数据类型查询指定的合协社区物业单元信息
	public CellListVO querySectHeXieList(String sect_id, String build_id, String unit_id, String data_type);
	
	//16.根据名称模糊查询合协社区小区列表
	public CellListVO getVagueSectByName(String sect_name);
	
	
}
