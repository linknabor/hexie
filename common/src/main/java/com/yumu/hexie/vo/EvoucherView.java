package com.yumu.hexie.vo;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.QRCodeUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.Evoucher;

public class EvoucherView implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(EvoucherView.class);
	
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
	
	public EvoucherView() {
		super();
	}
	public EvoucherView(List<Evoucher> vouchers) {
		
		if (vouchers!=null) {
			this.count = vouchers.size();
			this.actualPrice = BigDecimal.ZERO;
			this.oriPrice = BigDecimal.ZERO;
			for (Evoucher evoucher : vouchers) {
				if (ModelConstant.EVOUCHER_STATUS_NORMAL == evoucher.getStatus() 
						&& evoucher.available()) {
					
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
				}
			}
			actualPrice = actualPrice.setScale(2, RoundingMode.HALF_UP);
			oriPrice = oriPrice.setScale(2, RoundingMode.HALF_UP);
		}
		
		if (!StringUtil.isEmpty(code)) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				QRCodeUtil.createQRCodeToIO(code, "", os);
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
	
	
}
