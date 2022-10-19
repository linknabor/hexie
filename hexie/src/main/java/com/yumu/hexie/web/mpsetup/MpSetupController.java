package com.yumu.hexie.web.mpsetup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.service.mpsetup.MpSetupService;
import com.yumu.hexie.service.mpsetup.req.MpQueryReq;
import com.yumu.hexie.service.mpsetup.req.MpSetupReq;
import com.yumu.hexie.web.BaseController;

@RestController("mpSetupController")
public class MpSetupController extends BaseController {
	
	@Autowired
	private MpSetupService mpSetupService;
	
	@RequestMapping(value = "/wechatmp/save", method = RequestMethod.POST)
	public CommonResponse<String> createMp(@RequestBody MpSetupReq mpSetupReq) {
		
		CommonResponse<String> commonResponse = new CommonResponse<>();
		try {
			mpSetupService.saveMp(mpSetupReq);
			commonResponse.setData(Constants.SERVICE_SUCCESS);
			commonResponse.setErrMsg("");
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");
			return commonResponse;
		}
		return commonResponse;
    }
	
	@RequestMapping(value = "/wechatmp/query", method = RequestMethod.GET)
	public CommonResponse<Object> getMp(@RequestBody MpQueryReq mpQueryReq) throws Exception {
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			String json = mpSetupService.queryMp(mpQueryReq);
			commonResponse.setData(json);
			commonResponse.setErrMsg("");
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");
			return commonResponse;
		}
		return commonResponse;
    }

}
