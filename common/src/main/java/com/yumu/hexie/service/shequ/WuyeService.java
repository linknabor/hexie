package com.yumu.hexie.service.shequ;

import java.util.List;

import com.yumu.hexie.integration.wuye.dto.DiscountViewRequestDTO;
import com.yumu.hexie.integration.wuye.dto.GetCellDTO;
import com.yumu.hexie.integration.wuye.dto.OtherPayDTO;
import com.yumu.hexie.integration.wuye.dto.PrepayRequestDTO;
import com.yumu.hexie.integration.wuye.dto.SignInOutDTO;
import com.yumu.hexie.integration.wuye.resp.BillListVO;
import com.yumu.hexie.integration.wuye.resp.BillStartDate;
import com.yumu.hexie.integration.wuye.resp.CellListVO;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.resp.PayWaterListVO;
import com.yumu.hexie.integration.wuye.vo.Discounts;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.integration.wuye.vo.HexieUser;
import com.yumu.hexie.integration.wuye.vo.InvoiceInfo;
import com.yumu.hexie.integration.wuye.vo.PaymentInfo;
import com.yumu.hexie.integration.wuye.vo.QrCodePayService;
import com.yumu.hexie.integration.wuye.vo.WechatPayInfo;
import com.yumu.hexie.model.promotion.coupon.CouponCombination;
import com.yumu.hexie.model.user.User;

public interface WuyeService {

	/**
	 * 快捷缴费
	 * @param uesr
	 * @param stmtId
	 * @param currPage
	 * @param totalCount
	 * @return
	 * @throws Exception 
	 */
	BillListVO quickPayInfo(User uesr, String stmtId, String currPage, String totalCount) throws Exception;

	/**
	 * 查询房屋
	 * @param user
	 * @return
	 */
	HouseListVO queryHouse(User user);

	/**
	 * 纸质账单绑定房屋
	 * @param user
	 * @param stmtId
	 * @param houseId
	 * @return
	 */
	HexieUser bindHouse(User user, String stmtId, String houseId);

	/**
	 * 无账单绑定房屋
	 * @param user
	 * @param houseId
	 * @param area
	 * @return
	 */
	HexieUser bindHouseNoStmt(User user, String houseId, String area);

	/**
	 * 删除房产
	 * @param user
	 * @param houseId
	 * @return
	 */
	boolean deleteHouse(User user, String houseId);

	/**
	 * 根据订单查询房产信息
	 * @param user
	 * @param stmtId
	 * @return
	 */
	HexieHouse getHouse(User user, String stmtId);
	
	/**
	 * 用户登录
	 * @param user
	 * @return
	 */
	HexieUser userLogin(User user);
	/**
	 * 缴费记录查询
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	PayWaterListVO queryPaymentList(User user, String startDate, String endDate);

	/**
	 * 缴费详情
	 * @param user
	 * @param waterId
	 * @return
	 */
	PaymentInfo queryPaymentDetail(User user, String waterId);

	/**
	 * 账单记录
	 * @param user
	 * @param payStatus
	 * @param startDate
	 * @param endDate
	 * @param currentPage
	 * @param totalCount
	 * @param house_id
	 * @param sect_id
	 * @return
	 */
	BillListVO queryBillList(User user, String payStatus, String startDate, String endDate,
			String currentPage, String totalCount, String house_id, String sect_id, String regionName);

	/**
	 * 账单详情 anotherbillIds(逗号分隔) 汇总了去支付,来自BillInfo的bill_id
	 * @param user
	 * @param stmtId
	 * @param anotherbillIds
	 * @return
	 * @throws Exception 
	 */
	PaymentInfo getBillDetail(User user, String stmtId, String anotherbillIds, String regionName) throws Exception;

	/**
	 * 物业账单缴费
	 * @param prepayRequestDTO
	 */
	WechatPayInfo getPrePayInfo(PrepayRequestDTO prepayRequestDTO) throws Exception;

	/**
	 * 查询是否已经用过红包
	 * @param user
	 * @return
	 */
	String queryCouponIsUsed(User user);

	/**
	 * 更新电子发票抬头信息
	 * @param user
	 * @param mobile
	 * @param invoice_title
	 * @param invoice_title_type
	 * @param credit_code
	 * @param trade_water_id
	 * @return
	 */
	String updateInvoice(String mobile, String invoice_title, String invoice_title_type, String credit_code, String trade_water_id);

	/**
	 * 根据交易号获取对应房子的发票信息
	 * @param user
	 * @param trade_water_id
	 * @return
	 */
	InvoiceInfo getInvoiceByTradeId(String trade_water_id);

	/**
	 * 根据数据类型查询指定的合协社区物业单元信息
	 * @param user
	 * @param sect_id
	 * @param build_id
	 * @param unit_id
	 * @param data_type
	 * @return
	 */
	CellListVO querySectHeXieList(User user, String sect_id, String build_id, String unit_id, String data_type, String region_name);

	/**
	 * 根据数据类型查询指定的合协社区物业单元信息
	 * @param user
	 * @param sect_id
	 * @param build_id
	 * @param unit_id
	 * @param data_type
	 * @return
	 * @throws Exception 
	 */
	CellListVO querySectHeXieList(GetCellDTO getCellDTO) throws Exception;
	
	
	/**
	 * 根据名称模糊查询合协社区小区列表
	 * @param user
	 * @param sect_name
	 * @return
	 */
	CellListVO getVagueSectByName(User user, String sect_name, String region_name);

	/**
	 * 设置默认地址
	 * @param user
	 * @param u
	 */
	void setDefaultAddress(User user, HexieUser u);
	
	/**
	 * 标准版账单记录
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @param house_id
	 * @param sect_id
	 * @param regionname
	 * @return
	 * @throws Exception 
	 */
	BillListVO queryBillListStd(User user, String startDate, String endDate, String house_id, String regionname) throws Exception;

	/**
	 * 获取无账单开始日期
	 * @param userId
	 * @param house_id
	 * @param regionname
	 * @return
	 */
	BillStartDate getBillStartDateSDO(User user, String house_id, String regionname);

	/**
	 * 异步绑定房屋
	 * @param bindSwitch
	 * @param user
	 * @param tradeWaterId
	 */
	void bindHouseByTradeAsync(String bindSwitch, User user, String tradeWaterId);

	/**
	 * 根据户号获取房屋信息
	 * @param user
	 * @param verNo
	 * @return
	 */
	HexieHouse getHouseByVerNo(User user, String verNo);

	/**
	 * 从种子添加红包
	 * @param user
	 * @param list
	 */
	void addCouponsFromSeed(User user, List<CouponCombination> list);

	/**
	 * 发送物业缴费红包的模板消息
	 * @param user
	 * @param tradeWaterId
	 * @param feePrice
	 */
	void sendPayTemplateMsg(User user, String tradeWaterId, String feePrice);
	
	/**
	 * 获取支付的优惠明细
	 * @param prepayRequestDTO
	 * @throws Exception 
	 */
	Discounts getDiscounts(DiscountViewRequestDTO discountViewRequestDTO) throws Exception;


	/**
	 * 根据物业订单号查询交易结果
	 * @param user
	 * @param orderNo
	 * @throws Exception 
	 */
	String queyrOrder(User user, String orderNo) throws Exception;

	/**
	 * 获取绑卡支付的短信验证码
	 * @param user
	 * @param orderNo
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	String getPaySmsCode(User user, String cardId) throws Exception;

	/**
	 * 其他收入支付
	 * @param otherPayDTO
	 * @return
	 * @throws Exception
	 */
	WechatPayInfo requestOtherPay(OtherPayDTO otherPayDTO) throws Exception;

	/**
	 * 获取二维码支付服务信息
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	QrCodePayService getQrCodePayService(User user);

	/**
	 * 根据二维码ID获取二维码
	 * @param user
	 * @param qrCodeId
	 * @return
	 * @throws Exception
	 */
	byte[] getQrCode(User user, String qrCodeId) throws Exception;

	/**
	 * 签到签退
	 * @param signInOutDTO
	 * @throws Exception
	 */
	void signInOut(SignInOutDTO signInOutDTO) throws Exception;

}
