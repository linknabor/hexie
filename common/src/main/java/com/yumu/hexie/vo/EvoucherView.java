package com.yumu.hexie.vo;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.yumu.hexie.common.util.QRCodeUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.market.Evoucher;

@SuppressWarnings("restriction")
public class EvoucherView implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(EvoucherView.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 614045847239876304L;
	
	private String qrcode;
	private String code;
	private List<Evoucher> vouchers;
	
	public EvoucherView() {
		super();
	}
	public EvoucherView(List<Evoucher> vouchers) {
		
		this.vouchers = vouchers;
		if (vouchers!=null) {
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
				this.qrcode = new String("data:image/jpg;base64," + Base64.encode(os.toByteArray()));
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
	public List<Evoucher> getVouchers() {
		return vouchers;
	}
	public void setVouchers(List<Evoucher> vouchers) {
		this.vouchers = vouchers;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "EvoucherView [qrcode=" + qrcode + ", code=" + code + ", vouchers=" + vouchers + "]";
	}
	
	
	
}
