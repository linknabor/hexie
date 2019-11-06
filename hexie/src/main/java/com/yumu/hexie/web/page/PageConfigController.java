/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.web.page;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.BgImage;
import com.yumu.hexie.service.page.PageConfigService;
import com.yumu.hexie.web.BaseController;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: PageConfigController.java, v 0.1 2016年1月18日 上午9:50:32  Exp $
 */
@RestController(value = "pageConfigController")
public class PageConfigController extends BaseController{
    
    @Inject
    private PageConfigService pageConfigService;
    
    @RequestMapping(value = "/pageconfig/{tempKey}", method = RequestMethod.GET )
    public String process(HttpServletRequest request,
            HttpServletResponse response, @PathVariable String tempKey, @RequestParam String fromSys) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        return pageConfigService.findByTempKey(tempKey);
    }
    
    @RequestMapping(value = "/iconList/{fromSys}", method = RequestMethod.PUT )
    public String updateIconBottom(@PathVariable String fromSys) throws Exception {
      
    	pageConfigService.updateBottomIcon();
    	return "success";
    }
    
    @RequestMapping(value = "/bgImage/{type}", method = RequestMethod.GET )
    public List<BgImage> getBgImage(@ModelAttribute(Constants.USER)User user, @PathVariable String type) 
    		throws JsonParseException, JsonMappingException, IOException {
    	
    	return pageConfigService.getBgImage(type, user.getAppId());
    }
}
