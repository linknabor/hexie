package com.yumu.hexie.web.shequ;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import com.yumu.hexie.integration.interact.req.InteractReq;
import com.yumu.hexie.integration.interact.req.SaveInteractCommentReq;
import com.yumu.hexie.integration.interact.req.SaveInteractInfoReq;
import com.yumu.hexie.integration.interact.resp.InteractCommentResp;
import com.yumu.hexie.integration.interact.resp.InteractInfoResp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.CommunityService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import org.springframework.web.multipart.MultipartFile;

@Controller(value = "communityController")
public class CommunityController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(CommunityController.class);
	
    @Value(value = "${qiniu.domain}")
    private String domain;
	@Inject
	private CommunityService communityService;
	@Autowired
	private QiniuUtil qiniuUtil;

	/**
	 * 首页获取帖子列表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/getInteractList", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<List<InteractInfoResp>> getInteractList(@ModelAttribute(Constants.USER)User user, @RequestBody InteractReq req) throws Exception {
		List<InteractInfoResp> list = communityService.getInteractList(user, req);
		log.debug("list is : " + list);
		return BaseResult.successResult(list);
	}

	/**
	 * 获取互动类型
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/getInteractType", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Map<String, String>>> getInteractType(@ModelAttribute(Constants.USER)User user) throws Exception {
		String appid = user.getAppId();
		if(StringUtils.isEmpty(appid)) {
			appid = user.getMiniAppId();
		}
		if(StringUtils.isEmpty(appid)) {
			appid = user.getAliappid();
		}
		List<Map<String, String>> list = communityService.getInteractType(user, appid);
		return BaseResult.successResult(list);
	}

	/**
	 * 新增帖子保存
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/addInteract", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public BaseResult<String> addThread(@ModelAttribute(Constants.USER)User user, @RequestBody SaveInteractInfoReq req) throws Exception{
		
		if(req.getEx_content().length()>200) {
			return BaseResult.fail("发布信息内容超过200字。");
		}
		communityService.addInteract(user, req);
		return BaseResult.successResult("success");
	}
	
	/**
	 * 删除帖子
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/interact/deleteInteract", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<?> deleteInteract(@ModelAttribute(Constants.USER)User user, @RequestBody InteractReq req) throws Exception{
		if (StringUtil.isEmpty(req.getInteractId())) {
			return BaseResult.fail("缺少帖子ID");
		}
		communityService.deleteInteract(user, req);
		return BaseResult.successResult("succeeded");
	}

	/**
	 * 查看帖子详细(主信息)
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/getInteractInfo", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<InteractInfoResp> getInteractInfo(@ModelAttribute(Constants.USER)User user, @RequestBody InteractReq req) throws Exception{
		if (StringUtil.isEmpty(req.getInteractId())) {
			return BaseResult.fail("未选中发布信息");
		}
		InteractInfoResp resp = communityService.getInteractInfoById(user, req);
		return BaseResult.successResult(resp);
	}

	/**
	 * 查看帖子回复信息列表
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/getCommentByInteractId", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<InteractCommentResp> getCommentByInteractId(@ModelAttribute(Constants.USER)User user, @RequestBody InteractReq req) throws Exception{
		if (StringUtil.isEmpty(req.getInteractId())) {
			return BaseResult.fail("未选中发布信息");
		}
		List<InteractCommentResp> resp = communityService.getCommentByInteractId(user, req);
		return BaseResult.successResult(resp);
	}

	/**
	 * 添加评论
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/addComment", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<InteractCommentResp> addComment(@ModelAttribute(Constants.USER)User user, @RequestBody SaveInteractCommentReq req) throws Exception{
		InteractCommentResp resp = communityService.addComment(user, req);	//添加评论
		return BaseResult.successResult(resp);
	}

	/**
	 * 删除评论
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/interact/deleteComment", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult deleteComment(@ModelAttribute(Constants.USER)User user, @RequestBody InteractReq req) throws Exception{
		communityService.deleteComment(user, req);
		return BaseResult.successResult("succeeded");
	}

	/**
	 * 业主评价打分
	 * @param user
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/interact/grade", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<InteractInfoResp> grade(@ModelAttribute(Constants.USER)User user, @RequestBody InteractReq req) throws Exception {
		InteractInfoResp resp = communityService.saveGrade(user, req);
		return BaseResult.successResult(resp);
	}

	/**
	 * 上传图片到七牛
	 * @param multiFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interact/upload", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> interactUpload(@RequestParam(value = "picture", required = false) MultipartFile multiFile) throws Exception {
		String imgUrl = "";
		if (multiFile != null) {
			String fileName = multiFile.getOriginalFilename();
			log.info("interactUpload, file name : " + fileName);
			if(StringUtils.isNoneBlank(fileName)) {
				long timestamp = System.currentTimeMillis();
				String kzm = fileName.substring(0, fileName.lastIndexOf("."));
				Random random = new Random();
	            int r = random.nextInt();
	            String key = timestamp + "_" + r + "_" + kzm;
				
				String uptoken = qiniuUtil.getUpToken();    //获取qiniu上传文件的token
				PutExtra extra = new PutExtra();
				PutRet putRet = IoApi.Put(uptoken, key, multiFile.getInputStream(), extra);
				if (putRet.getException() == null) {
					imgUrl = domain + key;
				}
			}
		}
		return BaseResult.successResult(imgUrl);
	}
	
	}
