package com.yumu.hexie.service.mpqrcode;

import com.yumu.hexie.service.mpqrcode.req.CreateMpQrCodeReq;

public interface MpQrCodeService {

	public String createQrCode(CreateMpQrCodeReq createQrCodeReq) throws Exception;
}
