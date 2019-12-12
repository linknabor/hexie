/**
 * 
 */
package com.yumu.hexie.web.shequ;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.ParamService;
import com.yumu.hexie.web.BaseController;

/**
 * 系统参数控制
 * @author huym
 *
 */
@RequestMapping("/param")
@RestController(value = "paramController")
public class ParamController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ParamController.class);
	
	@Autowired
	private ParamService paramService;
	
	@RequestMapping(value = "/wuye/{oriSys}", method = RequestMethod.GET)
	public void initWuyeParam(HttpServletResponse response, 
			@PathVariable String oriSys,
			@RequestParam(value="info_id") String infoId, 
			@RequestParam(value="type") String type) throws IOException {
		
		Runnable runnable = ()->{
			try {
				//先休息1分钟，因为平台重新加载参数需要时间
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			User user = new User();
			user.setOriSys(oriSys);
			paramService.cacheWuyeParam(user, infoId, type);
		};
		Thread t = new Thread(runnable);
		t.start();
		response.getWriter().print("ok");
	}
	
	@RequestMapping(value = "/sys", method = RequestMethod.POST)
	public String updateSysParam(@RequestParam(value = "code") String code) {
		
		if (!"hexie".equals(code)) {
			return "";
		}
		paramService.updateSysParam();
		return "success";
	}
	
}
