package com.yumu.hexie.vo;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private String qrcode;
	private String code;
	private int count;
	
	public EvoucherView() {
		super();
	}
	public EvoucherView(List<Evoucher> vouchers) {
		
		if (vouchers!=null) {
			this.count = vouchers.size();
			for (Evoucher evoucher : vouchers) {
				if (ModelConstant.EVOUCHER_STATUS_NORMAL == evoucher.getStatus() 
						&& evoucher.available()) {
					String code = evoucher.getCode();
					this.code = code;
					break;
				}
			}
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
	
	
	
}
