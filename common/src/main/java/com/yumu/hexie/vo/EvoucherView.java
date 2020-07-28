package com.yumu.hexie.vo;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yumu.hexie.common.util.ConfigUtil;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.QRCodeUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.Evoucher;

public class EvoucherView implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(EvoucherView.class);
	
	private static final String QRCODE_URL = ConfigUtil.get("evoucher_qrcode_url");
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 614045847239876304L;
	
	private String name;
	private String tel;
	private String smallPicture;
	private String endDate;
	private String qrcode;
	private String code;
	private int count;
	private Date consumeDate;
	private BigDecimal actualPrice;
	private BigDecimal oriPrice;
	private int status;
	
	public EvoucherView() {
		super();
	}
	/**
	 * 由于一个订单可能对应多个券，但只显示一个二维码和价格以及商品信息。所以如果有部分券被核销，则显示未核销券的二维码
	 * @param vouchers
	 */
	public EvoucherView(List<Evoucher> vouchers) {
		
		if (vouchers!=null) {
			
			this.actualPrice = BigDecimal.ZERO;
			this.oriPrice = BigDecimal.ZERO;
			
			Map<Integer, List<Evoucher>> map = new HashMap<>();
			map.put(ModelConstant.EVOUCHER_STATUS_NORMAL, new ArrayList<>());
			map.put(ModelConstant.EVOUCHER_STATUS_USED, new ArrayList<>());
			map.put(ModelConstant.EVOUCHER_STATUS_EXPIRED, new ArrayList<>());
			
			for (Evoucher evoucher : vouchers) {
				List<Evoucher> unusedList = map.get(evoucher.getStatus());
				unusedList.add(evoucher);
			}
			
			List<Evoucher> unusedList = map.get(ModelConstant.EVOUCHER_STATUS_NORMAL);
			List<Evoucher> usedList = map.get(ModelConstant.EVOUCHER_STATUS_USED);
			List<Evoucher> expiredList = map.get(ModelConstant.EVOUCHER_STATUS_EXPIRED);
			if (!unusedList.isEmpty()) {	//如果有未使用的券，则以其中第一条的code作为二维码，价格是所有未使用券的累加金额
				for (Evoucher evoucher : unusedList) {
					if (StringUtil.isEmpty(this.code)) {
						this.code = evoucher.getCode();;
						this.name = evoucher.getProductName();
						this.tel = evoucher.getTel();
						this.smallPicture = evoucher.getSmallPicture();
						if (!StringUtil.isEmpty(evoucher.getEndDate())) {
							this.endDate = DateUtil.dtFormat(evoucher.getEndDate(), DateUtil.dttmSimple);
						}
					}
					BigDecimal aPrice = new BigDecimal(String.valueOf(evoucher.getActualPrice()));
					BigDecimal oPrice = new BigDecimal(String.valueOf(evoucher.getOriPrice()));
					actualPrice = actualPrice.add(aPrice);
					oriPrice = oriPrice.add(oPrice);
					count++;
					
				}
				status = ModelConstant.EVOUCHER_STATUS_NORMAL;
				
			}else if (!usedList.isEmpty()) {	//如果没有未使用的券，则显示已使用券的累加金额、名称、电话等，二维码不显示
				for (Evoucher evoucher : usedList) {
					if (StringUtil.isEmpty(this.code)) {
						this.code = evoucher.getCode();;
						this.name = evoucher.getProductName();
						this.tel = evoucher.getTel();
						this.smallPicture = evoucher.getSmallPicture();
						this.consumeDate = evoucher.getConsumeDate();
						if (!StringUtil.isEmpty(evoucher.getEndDate())) {
							this.endDate = DateUtil.dtFormat(evoucher.getEndDate(), DateUtil.dttmSimple);
						}
					}
					BigDecimal aPrice = new BigDecimal(String.valueOf(evoucher.getActualPrice()));
					BigDecimal oPrice = new BigDecimal(String.valueOf(evoucher.getOriPrice()));
					actualPrice = actualPrice.add(aPrice);
					oriPrice = oriPrice.add(oPrice);
					count++;
				}
				status = ModelConstant.EVOUCHER_STATUS_USED;
				
			}else if (!expiredList.isEmpty()) {	//以上两项如果都没有，则显示过期的券
				for (Evoucher evoucher : expiredList) {
					if (StringUtil.isEmpty(this.code)) {
						this.code = evoucher.getCode();;
						this.name = evoucher.getProductName();
						this.tel = evoucher.getTel();
						this.smallPicture = evoucher.getSmallPicture();
						if (!StringUtil.isEmpty(evoucher.getEndDate())) {
							this.endDate = DateUtil.dtFormat(evoucher.getEndDate(), DateUtil.dttmSimple);
						}
					}
					BigDecimal aPrice = new BigDecimal(String.valueOf(evoucher.getActualPrice()));
					BigDecimal oPrice = new BigDecimal(String.valueOf(evoucher.getOriPrice()));
					actualPrice = actualPrice.add(aPrice);
					oriPrice = oriPrice.add(oPrice);
					count++;
				}
				status = ModelConstant.EVOUCHER_STATUS_EXPIRED;
			}
		}
		
		if (ModelConstant.EVOUCHER_STATUS_NORMAL == status) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				QRCodeUtil.createQRCodeToIO(QRCODE_URL + code, "", os);
				String codeStr = new String (Base64.getEncoder().encode(os.toByteArray()));
				this.qrcode = "data:image/jpg;base64," + codeStr;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getSmallPicture() {
		return smallPicture;
	}
	public void setSmallPicture(String smallPicture) {
		this.smallPicture = smallPicture;
	}
	public Date getConsumeDate() {
		return consumeDate;
	}
	public void setConsumeDate(Date consumeDate) {
		this.consumeDate = consumeDate;
	}
	public BigDecimal getActualPrice() {
		return actualPrice;
	}
	public void setActualPrice(BigDecimal actualPrice) {
		this.actualPrice = actualPrice;
	}
	public BigDecimal getOriPrice() {
		return oriPrice;
	}
	public void setOriPrice(BigDecimal oriPrice) {
		this.oriPrice = oriPrice;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
