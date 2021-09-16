package com.yumu.hexie.web.mpqrcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.service.mpqrcode.MpQrCodeService;
import com.yumu.hexie.service.mpqrcode.req.CreateMpQrCodeReq;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@RestController(value = "qrCodeController")
public class MpQrCodeController extends BaseController {

	@Autowired
	private MpQrCodeService mpQrCodeService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/mpqrcode/create", method = RequestMethod.POST)
	public BaseResult<String> createQrCode(@RequestBody CreateMpQrCodeReq createQrCodeReq) throws Exception {
		
		return BaseResult.successResult(mpQrCodeService.createQrCode(createQrCodeReq));
    }
	
}
