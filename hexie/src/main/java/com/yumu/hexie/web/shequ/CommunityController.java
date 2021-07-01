package com.yumu.hexie.web.shequ;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import com.yumu.hexie.common.util.AppUtil;
import com.yumu.hexie.integration.wechat.service.MsgCfg;
import com.yumu.hexie.integration.wechat.service.TemplateMsgService;
import com.yumu.hexie.integration.wuye.req.CommunityRequest;
import com.yumu.hexie.integration.wuye.req.OpinionRequest;
import com.yumu.hexie.integration.wuye.req.OpinionRequestTemp;
import com.yumu.hexie.service.msgtemplate.WechatMsgService;
import com.yumu.hexie.service.shequ.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.yumu.hexie.common.Constants;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.integration.wechat.service.FileService;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.Annoucement;
import com.yumu.hexie.model.community.CommunityInfo;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadComment;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.CommunityService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import org.springframework.web.multipart.MultipartFile;

@Controller(value = "communityController")
public class CommunityController extends BaseController{
	
	private static final Logger log = LoggerFactory.getLogger(CommunityController.class);
	
	private static final int PAGE_SIZE = 10;
	
    @Value(value = "${tmpfile.dir}")
    private String tmpFileRoot;
    
    @Value(value = "${qiniu.domain}")
    private String domain;

	
	@Inject
	private CommunityService communityService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private SystemConfigService systemConfigService;
	
	@Autowired
	private QiniuUtil qiniuUtil;
	
	@Autowired
	private FileService fileService;

	@Autowired
	private TemplateMsgService templateMsgService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private WechatMsgService wechatMsgService;
	/*****************[BEGIN]帖子********************/
	
	/**
	 * 首页获取帖子列表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/getThreadList/{filter}/{currPage}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<List<Thread>> getThreadList(@ModelAttribute(Constants.USER)User user, @RequestBody Thread thread, 
				@PathVariable String filter,  @PathVariable int currPage ) throws Exception {
		//filter 等于 y表示需要根据用户所在小区进行过滤
		log.info("filter is : " + filter);
		Sort sort = new Sort(Direction.DESC , "stickPriority", "createDate", "createTime");
		List<Thread> list;
		
		Pageable page = PageRequest.of(currPage, PAGE_SIZE, sort);
		//查看本小区的
		if ("y".equals(filter)) {
			if (ModelConstant.THREAD_CATEGORY_SUGGESTION == thread.getThreadCategory()) {
				list = communityService.getThreadListByCategory(user.getId(), thread.getThreadCategory(), String.valueOf(user.getXiaoquId()), page);
			}else {
				list = communityService.getThreadListByCategory(user.getId(), thread.getThreadCategory(), user.getSectId(), page);
			}
		} else {
			list = communityService.getThreadListByCategory(user.getId(), thread.getThreadCategory(), page);
		}

		for (Thread td : list) {
			String attachmentUrl = td.getAttachmentUrl();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				String[] urls = attachmentUrl.split(",");
				List<String> imgLinkList = new ArrayList<>();
				List<String> previewLinkList = new ArrayList<>();
				for (int j = 0; j < (Math.min(urls.length, 3)); j++) {
					String urlKey = urls[j];
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
				}
				td.setImgUrlLink(imgLinkList);
				td.setPreviewLink(previewLinkList);
			}
		}

		for (Thread td : list) {
			String attachmentUrl = td.getAttachmentUrl();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				String[] urls = attachmentUrl.split(",");
				List<String> thumbnailLinkList = new ArrayList<>();
				for (int j = 0; j < (Math.min(urls.length, 3)); j++) {
					String urlKey = urls[j];
					thumbnailLinkList.add(qiniuUtil.getThumbnailLink(urlKey, "3", "0"));
				}
				td.setThumbnailLink(thumbnailLinkList);
			}
		}
		log.debug("list is : " + list);
		return BaseResult.successResult(list);
	}

	/**
	 * 物业端调用 获取业主意见列表
	 * @param currPage
	 * @param category
	 * @param sectId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/getOutSidThreadList/{currPage}/{category}/{sectId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<List<Thread>> getOutSidThreadList(@PathVariable int currPage, @PathVariable int category, @PathVariable String sectId ) throws Exception {
		//filter 等于 y表示需要根据用户所在小区进行过滤
		Sort sort = new Sort(Direction.DESC , "stickPriority", "createDate", "createTime");
		Pageable page = PageRequest.of(currPage, PAGE_SIZE, sort);
		List<Thread> list = communityService.getThreadListByCategory(null, category, sectId, page);

		for (Thread td : list) {
			String attachmentUrl = td.getAttachmentUrl();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				String[] urls = attachmentUrl.split(",");
				List<String> imgLinkList = new ArrayList<>();
				List<String> previewLinkList = new ArrayList<>();
				for (int j = 0; j < (Math.min(urls.length, 3)); j++) {
					String urlKey = urls[j];
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
				}
				td.setImgUrlLink(imgLinkList);
				td.setPreviewLink(previewLinkList);
			}
		}

		for (Thread td : list) {
			String attachmentUrl = td.getAttachmentUrl();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				String[] urls = attachmentUrl.split(",");
				List<String> thumbnailLinkList = new ArrayList<>();
				for (int j = 0; j < (Math.min(urls.length, 3)); j++) {
					String urlKey = urls[j];
					thumbnailLinkList.add(qiniuUtil.getThumbnailLink(urlKey, "3", "0"));
				}
				td.setThumbnailLink(thumbnailLinkList);
			}
		}
		log.debug("list is : " + list);
		return BaseResult.successResult(list);
	}
	
	/**
	 * 新增帖子
	 * @param session
	 * @param thread
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/addThread", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> addThread(HttpSession session, @RequestBody Thread thread) throws Exception{
		User user = (User)session.getAttribute(Constants.USER);
		if(thread.getThreadContent().length()>200){
			return BaseResult.fail("发布信息内容超过200字。");
		}
		communityService.addThread(user, thread);
		try {
			OpinionRequest opinionRequest = new OpinionRequest();
			opinionRequest.setContent("");
			opinionRequest.setThreadId(thread.getThreadId()+"");
			opinionRequest.setReplyId("");
			opinionRequest.setSectName(thread.getUserSectName());
			opinionRequest.setCellAddr(thread.getUserAddress());
			opinionRequest.setSectId(thread.getUserSectId());
			opinionRequest.setThreadContent(thread.getThreadContent());
			opinionRequest.setUserOpenid(user.getOpenid());
			opinionRequest.setOpinionDate(DateUtil.formatDateTimeFromDB(thread.getCreateDate(), thread.getCreateTime()));
			Boolean flag = communityService.sendNotification(user, opinionRequest);
			log.debug("flag : " + flag);
		} catch (Exception e) {
			log.error("sendNotification : " , e);
		}

		//moveImgsFromTencent2Qiniu(thread);	//更新图片的路径
		return BaseResult.successResult("success");
	}
	
	/**
	 * 删除帖子
	 * @param session
	 * @param thread
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/thread/deleteThread", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<?> deleteThread(HttpSession session, @RequestBody Thread thread) throws Exception{
		User user = (User)session.getAttribute(Constants.USER);
		if (StringUtil.isEmpty(thread.getThreadId())) {
			return BaseResult.fail("缺少帖子ID");
		}
		communityService.deleteThread(user, thread.getThreadId());
		return BaseResult.successResult("succeeded");
	}
	
	/**
	 * 查看帖子详细
	 * @param session
	 * @param thread
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/getThreadByThreadId", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Thread> getThreadByThreadId(HttpSession session, @RequestBody Thread thread) throws Exception{
		User user = (User)session.getAttribute(Constants.USER);
		long threadId = thread.getThreadId();
		if (StringUtil.isEmpty(threadId)) {
			return BaseResult.fail("未选中帖子");
		}
		
		Thread ret = communityService.getThreadByTreadId(threadId);
		
		/*
		 * 如果上传文件路径为空，则先更新上传文件路径
		 */
		//moveImgsFromTencent2Qiniu(ret);
			
		String attachmentUrl = ret.getAttachmentUrl();
		if (!StringUtil.isEmpty(attachmentUrl)) {
			String[]urls = attachmentUrl.split(",");
			List<String>imgLinkList = new ArrayList<>();
			List<String>thumbnailLinkList = new ArrayList<>();
			for (String urlKey : urls) {
				imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
				thumbnailLinkList.add(qiniuUtil.getThumbnailLink(urlKey, "3", "0"));
			}
			ret.setImgUrlLink(imgLinkList);
			ret.setThumbnailLink(thumbnailLinkList);
		}
		
		List<ThreadComment> list = communityService.getCommentListByThreadId(threadId);
		for (ThreadComment tc : list) {

			if (tc.getCommentUserId() == user.getId()) {
				tc.setIsCommentOwner("true");
			} else {
				tc.setIsCommentOwner("false");
			}

			String tcAttachmentUrl = tc.getAttachmentUrl();
			if (!StringUtil.isEmpty(tcAttachmentUrl)) {
				String[] urls = tcAttachmentUrl.split(",");
				List<String> imgLinkList = new ArrayList<>();
				List<String> previewLinkList = new ArrayList<>();
				for (String urlKey : urls) {
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
				}
				tc.setImgUrlLink(imgLinkList);
				tc.setPreviewLink(previewLinkList);
			}
		}
		ret.setComments(list);
		
		if (ret.getUserId()==user.getId()) {
			ret.setIsThreadOwner("true");
		}else {
			ret.setIsThreadOwner("false");
		}
		
		ret.setHasUnreadComment("false");
		communityService.updateThread(ret);
		return BaseResult.successResult(ret);
		
	}

	/**
	 * 查看帖子详细(物业端调用)
	 * @param threadId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/getOutSidThreadByThreadId/{threadId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Thread> getOutSidThreadByThreadId(@PathVariable String threadId) throws Exception{
		if (StringUtil.isEmpty(threadId)) {
			return BaseResult.fail("未选中帖子");
		}

		Thread ret = communityService.getThreadByTreadId(Long.parseLong(threadId));

		String attachmentUrl = ret.getAttachmentUrl();
		if (!StringUtil.isEmpty(attachmentUrl)) {
			String[]urls = attachmentUrl.split(",");
			List<String>imgLinkList = new ArrayList<>();
			List<String>thumbnailLinkList = new ArrayList<>();
			for (String urlKey : urls) {
				imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
				thumbnailLinkList.add(qiniuUtil.getThumbnailLink(urlKey, "3", "0"));
			}
			ret.setImgUrlLink(imgLinkList);
			ret.setThumbnailLink(thumbnailLinkList);
		}

		List<ThreadComment> list = communityService.getCommentListByThreadId(Long.parseLong(threadId));
		for (ThreadComment tc : list) {

			tc.setIsCommentOwner("false");
			String tcAttachmentUrl = tc.getAttachmentUrl();
			if (!StringUtil.isEmpty(tcAttachmentUrl)) {
				String[] urls = tcAttachmentUrl.split(",");
				List<String> imgLinkList = new ArrayList<>();
				List<String> previewLinkList = new ArrayList<>();
				for (String urlKey : urls) {
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
				}
				tc.setImgUrlLink(imgLinkList);
				tc.setPreviewLink(previewLinkList);
			}
		}
		ret.setComments(list);
		ret.setIsThreadOwner("false");
		ret.setHasUnreadComment("false");
		communityService.updateThread(ret);
		return BaseResult.successResult(ret);
	}

	/**
	 * 添加评论
	 * @param session
	 * @param comment
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/addComment", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Thread> addComment(HttpSession session, @RequestBody ThreadComment comment) throws Exception{

		User user = (User)session.getAttribute(Constants.USER);

		//更新帖子回复数量及最后评论时间
		Thread thread = communityService.getThreadByTreadId(comment.getThreadId());
		thread.setCommentsCount(thread.getCommentsCount()+1);
		thread.setLastCommentTime(System.currentTimeMillis());
		thread.setHasUnreadComment("true");	//是否有未读评论
		communityService.updateThread(thread);

		comment.setToUserId(thread.getUserId());
		comment.setToUserName(thread.getUserName());
		comment.setToUserReaded("false");

		ThreadComment retComment = communityService.addComment(user, comment);	//添加评论

		String tcAttachmentUrl = retComment.getAttachmentUrl();
		if (!StringUtil.isEmpty(tcAttachmentUrl)) {
			String[]urls = tcAttachmentUrl.split(",");
			List<String> previewLinkList = new ArrayList<String>();
			for (String urlKey : urls) {
				previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
			}
			retComment.setPreviewLink(previewLinkList);
		}

		try {
			OpinionRequest opinionRequest = new OpinionRequest();
			opinionRequest.setContent(retComment.getCommentContent());
			opinionRequest.setThreadId(thread.getThreadId()+"");
			opinionRequest.setReplyId(retComment.getCommentId()+"");
			opinionRequest.setSectName(thread.getUserSectName());
			opinionRequest.setCellAddr(thread.getUserAddress());
			opinionRequest.setSectId(thread.getUserSectId());
			opinionRequest.setThreadContent(thread.getThreadContent());
			opinionRequest.setUserOpenid(user.getOpenid());
			opinionRequest.setOpinionDate(DateUtil.formatDateTimeFromDB(retComment.getCommentDate(), retComment.getCommentTime()));
			Boolean flag = communityService.sendNotification(user, opinionRequest);
			log.debug("flag : " + flag);
		} catch (Exception e) {
			log.error("sendNotification : " , e);
		}


		return BaseResult.successResult(retComment);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/addOutSidComment", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Thread> addOutSidComment(@RequestBody ThreadComment comment) throws Exception{

		//更新帖子回复数量及最后评论时间
		Thread thread = communityService.getThreadByTreadId(comment.getThreadId());
		thread.setCommentsCount(thread.getCommentsCount()+1);
		thread.setLastCommentTime(System.currentTimeMillis());
		thread.setHasUnreadComment("true");	//是否有未读评论
		communityService.updateThread(thread);

		comment.setToUserId(thread.getUserId());
		comment.setToUserName(thread.getUserName());
		comment.setToUserReaded("false");

		ThreadComment retComment = communityService.addComment(null, comment);	//添加评论

		String tcAttachmentUrl = retComment.getAttachmentUrl();
		if (!StringUtil.isEmpty(tcAttachmentUrl)) {
			String[]urls = tcAttachmentUrl.split(",");
			List<String> previewLinkList = new ArrayList<>();
			for (String urlKey : urls) {
				previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
			}
			retComment.setPreviewLink(previewLinkList);
		}

		try {
			User user = userService.getById(thread.getUserId());

			//添加到消息中心
			CommunityRequest request = new CommunityRequest();

			StringBuilder sb = new StringBuilder();
			sb.append("意见标题：").append(thread.getThreadContent()).append("|");
			sb.append("回复内容：").append(retComment.getCommentContent()).append("|");
			sb.append("回复人：").append(comment.getCommentUserName());
			request.setTitle(sb.toString());
			request.setContent(sb.toString());
			request.setSummary(sb.toString());
			request.setAppid(user.getAppId());
			request.setOpenid(user.getOpenid());
			request.setNoticeType(ModelConstant.NOTICE_TYPE2_THREAD);
			String url = wechatMsgService.getMsgUrl(MsgCfg.URL_OPINION_NOTICE);
			url = AppUtil.addAppOnUrl(url, user.getAppId());
			url = url.replaceAll("THREAD_ID", thread.getThreadId()+"");
			request.setUrl(url);

			SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
			request.setPublishDate(df1.format(new Date()));
			noticeService.addOutSidNotice(request);

			//通知业主
			OpinionRequestTemp opinionRequest = new OpinionRequestTemp();
			opinionRequest.setContent(retComment.getCommentContent());
			opinionRequest.setThreadId(thread.getThreadId()+"");
			opinionRequest.setSectName(thread.getUserSectName());
			opinionRequest.setCellAddr(thread.getUserAddress());
			opinionRequest.setOpenId(user.getOpenid());
			opinionRequest.setAppId(user.getAppId());
			opinionRequest.setThreadContent(thread.getThreadContent());
			opinionRequest.setCommMan(comment.getCommentUserName());
			opinionRequest.setOpinionDate(DateUtil.formatDateTimeFromDB(retComment.getCommentDate(), retComment.getCommentTime()));
			String accessToken = systemConfigService.queryWXAToken(user.getAppId());
			templateMsgService.sendOpinionNotificationMessage(opinionRequest, accessToken);
		} catch (Exception e) {
			log.error("/thread/addOutSidComment sendOpinionNotificationMessage :" , e);
		}

		return BaseResult.successResult(retComment);
	}

	/**
	 * 删除评论
	 * @param session
	 * @param comment
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/thread/deleteComment", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult deleteComment(HttpSession session, @RequestBody ThreadComment comment) throws Exception{
		User user = (User)session.getAttribute(Constants.USER);
		communityService.deleteComment(user, comment.getCommentId());	//添加评论

		//更新帖子回复数量
		Thread thread = communityService.getThreadByTreadId(comment.getThreadId());
		thread.setCommentsCount(thread.getCommentsCount()-1);
		communityService.updateThread(thread);
		return BaseResult.successResult("succeeded");
	}

	/**
	 * 上传图片到七牛
	 * @param multiFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/upload", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<String> tradeList(@RequestParam(value = "picture", required = false) MultipartFile multiFile) throws Exception {
		String imgUrl = "";
		log.error(multiFile.getOriginalFilename());
		String fileName = multiFile.getOriginalFilename();
		if(StringUtils.isNoneBlank(fileName)) {
			String currDate = DateUtil.dtFormat(new Date(), "yyyyMMdd");
			String currTime = DateUtil.dtFormat(new Date().getTime(), "HHMMss");
			String kzm = fileName.substring(fileName.lastIndexOf("."));
			String key = currDate + "_" + currTime + "_" + kzm;

			log.error("key:" + key);
			log.error("kzm:" + kzm);

			String uptoken = qiniuUtil.getUpToken();    //获取qiniu上传文件的token
			PutExtra extra = new PutExtra();
			PutRet putRet = IoApi.Put(uptoken, key, multiFile.getInputStream(), extra);
			log.error("exception:" + putRet.getException());
			log.error("response:" + putRet.getResponse());
			if (putRet.getException() == null) {
				imgUrl = domain + key;
			}
		}
		return BaseResult.successResult(imgUrl);
	}



	/***********************************以下是老函数，可能用不上，后续有需要在删除*****************************************/














	/**
	 * 更新帖子 TODO	可能不提供编辑功能
	 * @param session
	 * @param thread
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/thread/updateThread", method = RequestMethod.POST)
	public BaseResult<?> updateThread(HttpSession session, @RequestBody Thread thread) throws Exception{
		communityService.updateThread(thread);
		return BaseResult.successResult("");
	}

	/**
	 * 上传图片到七牛服务器
	 * @param ret
	 * @param uploadIds
	 */
	@SuppressWarnings("rawtypes")
	private void upload2Qiniu(Thread ret, String uploadIds ){
		
		InputStream inputStream = null;
		if (!StringUtil.isEmpty(uploadIds)) {
			
			uploadIds = uploadIds.substring(0, uploadIds.length()-1);	//截掉最后一个逗号
			String[]uploadIdArr = uploadIds.split(",");
			
			String uptoken = qiniuUtil.getUpToken();	//获取qiniu上传文件的token
			
			log.error("qiniu token :" + uptoken);
			
			String currDate = DateUtil.dtFormat(new Date(), "yyyyMMdd");
			String currTime = DateUtil.dtFormat(new Date().getTime(), "HHMMss");
			String tmpPathRoot = tmpFileRoot+File.separator+currDate+File.separator;
			
			File file = new File(tmpPathRoot);
			if (!file.exists()||!file.isDirectory()) {
				file.mkdirs();
			}
			String keyListStr = "";
			String imgHeight = "";
			String imgWidth = "";
			
			PutExtra extra = new PutExtra();
			User user = userService.getById(ret.getUserId());
			String accessToken = systemConfigService.queryWXAToken(user.getAppId());
			try {
				for (int i = 0; i < uploadIdArr.length; i++) {
					
					String uploadId = uploadIdArr[i];
					String tmpPath = tmpPathRoot+currTime+"_"+ret.getThreadId()+"_"+i;
					File imgFile = new File(tmpPath);
					fileService.downloadFile(uploadId, accessToken, imgFile);
					
					String key = currDate+"_"+currTime+"_"+ret.getThreadId()+"_"+i;
					
					if (imgFile.exists() && imgFile.getTotalSpace()>0) {

						PutRet putRet = IoApi.putFile(uptoken, key, imgFile, extra);
						log.error("ret msg is : " + putRet.getException());
						log.error("putRet is : " + putRet.toString());
						
						while (putRet.getException()!=null) {
							putRet = IoApi.putFile(uptoken, key, imgFile, extra);
							java.lang.Thread.sleep(100);
						}
						
						boolean isUploaded = false;
						int counter = 0;
						Map map = null;
						while (!isUploaded && counter <3 ) {

							map = qiniuUtil.getImgs(domain+key);
							Object error = map.get("error");
							if (error != null) {
								log.error((String)error);
								log.error("start to re-upload ...");
								putRet = IoApi.putFile(uptoken, key, imgFile, extra);
								java.lang.Thread.sleep(100);
							}else {
								isUploaded = true;
								break;
							}
							
							counter++;
						}
						
						if (isUploaded) {
							
							keyListStr+=domain+key;
							
							Integer width = (Integer)map.get("width");
							Integer height = (Integer)map.get("height");
							
							imgWidth+=width;
							imgHeight+=height;
							
							if (i!=uploadIdArr.length-1) {
								keyListStr+=",";
								imgWidth+=",";
								imgHeight+=",";
							}
						}
						
					
					}
					
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}finally{
				if (inputStream!=null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
			}
		
			ret.setAttachmentUrl(keyListStr);
			ret.setImgHeight(imgHeight);
			ret.setImgWidth(imgWidth);
			communityService.updateThread(ret);	//更新上传文件路径
		
		}
		
		
	}
	
	private void moveImgsFromTencent2Qiniu(Thread ret){
		
		//从腾讯下载用户上传的图片。并放到图片服务器上。
		String uploadIds = ret.getUploadPicId();
		String attachmentUrls = ret.getAttachmentUrl();
		
		if (StringUtil.isEmpty(attachmentUrls)) {
			
			upload2Qiniu(ret, uploadIds);
			
		}else {
		
			if (!StringUtil.isEmpty(uploadIds)) {
				
				if (attachmentUrls.endsWith(",")) {
					attachmentUrls = attachmentUrls.substring(0, attachmentUrls.length()-1);
				}
				String[]uploadIdArr = uploadIds.split(",");
				String[]urlArr = attachmentUrls.split(",");
				
				if (uploadIdArr.length!=urlArr.length) {
					upload2Qiniu(ret, uploadIds);
				}
			}
		}
		
	}
	

	
	private void moveImgsFromTencent2Qiniu(ThreadComment ret){
				
				//从腾讯下载用户上传的图片。并放到图片服务器上。
		String uploadIds = ret.getUploadPicId();
		String attachmentUrls = ret.getAttachmentUrl();
		
		if (StringUtil.isEmpty(attachmentUrls)) {
			
			upload2Qiniu(ret, uploadIds);
			
		}else {
		
			if (!StringUtil.isEmpty(uploadIds)) {
				
				if (attachmentUrls.endsWith(",")) {
					attachmentUrls = attachmentUrls.substring(0, attachmentUrls.length()-1);
				}
				String[]uploadIdArr = uploadIds.split(",");
				String[]urlArr = attachmentUrls.split(",");
					
					if (uploadIdArr.length!=urlArr.length) {
						upload2Qiniu(ret, uploadIds);
					}
				}
			}
			
		}
	

	
	/**
	 * 刷新页面图片
	 * @param session
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/thread/getImgDetail/{threadId}/{index}/{type}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult getImgDetail(HttpSession session, @PathVariable long threadId, @PathVariable int index, @PathVariable int type){
		
		User user = (User)session.getAttribute(Constants.USER);
		
		if(user == null){
			throw new BizValidateException("请登录");
		}
		
		Thread ret = communityService.getThreadByTreadId(threadId);
		String attachmentUrl = ret.getAttachmentUrl();
		String imgHeight = ret.getImgHeight();
		String imgWidth = ret.getImgWidth();
		
		if(type==1) {
			ThreadComment retcomment = communityService.getThreadCommentByTreadId(threadId);
			attachmentUrl = retcomment.getAttachmentUrl();
			imgHeight = retcomment.getImgHeight();
			imgWidth = retcomment.getImgWidth();
		}
		
		String[]imgUrls = attachmentUrl.split(",");
		String[]heights = imgHeight.split(",");
		String[]widths = imgWidth.split(",");
		
		Map<String,String>map = new HashMap<String, String>();
		map.put("imgUrl", imgUrls[index]);
		map.put("height", heights[index]);
		map.put("width", widths[index]);
		return BaseResult.successResult(map);
	
	}

	/**
	 * 首页获取帖子列表
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/thread/getMyPublish", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<List<Thread>> getMyPublish(@ModelAttribute(Constants.USER)User user) throws Exception {
		
		Sort sort = new Sort(Direction.DESC ,  "lastCommentTime", "createDate", "createTime");
		
		List<Thread>list = new ArrayList<Thread>();
		list = communityService.getThreadListByUserId(user.getId(), sort);
		
		for (int i = 0; i < list.size(); i++) {
			
			Thread td = list.get(i);
			String attachmentUrl = td.getAttachmentUrl();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				
				String[]urls = attachmentUrl.split(",");
				
				List<String>imgLinkList = new ArrayList<String>();
				List<String>previewLinkList = new ArrayList<String>();
				
				for (int j = 0; j < (urls.length>3?3:urls.length); j++) {
					
					String urlKey = urls[j];
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					previewLinkList.add(qiniuUtil.getPreviewLink(urlKey, "1", "0"));
					
				}
				
				td.setImgUrlLink(imgLinkList);
				td.setPreviewLink(previewLinkList);
			}
			if (ModelConstant.THREAD_CATEGORY_STORE == (td.getThreadCategory())) {
				td.setCategoryImgName("img_store_publish");
				td.setCategoryCnName("二手市场");
			}else {
				td.setCategoryImgName("img_chat_publish");
				td.setCategoryCnName("邻里叽歪");
			}
			
//			if (ModelConstant.THREAD_CATEGORY_OUTDOORS.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_outdoors_publish");
//				td.setCategoryCnName("户外运动");
//			}else if (ModelConstant.THREAD_CATEGORY_PETS.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_dog_publish");
//				td.setCategoryCnName("宠物宝贝");
//			}else if (ModelConstant.THREAD_CATEGORY_CATE.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_cate_publish");
//				td.setCategoryCnName("吃货天地");
//			}else if (ModelConstant.THREAD_CATEGORY_STORE.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_store_publish");
//				td.setCategoryCnName("二手市场");
//			}else if (ModelConstant.THREAD_CATEGORY_EDUCATION.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_education_publish");
//				td.setCategoryCnName("亲自教育");
//			}else if (ModelConstant.THREAD_CATEGORY_SPORTS.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_sports_publish");
//				td.setCategoryCnName("运动达人");
//			}else if (ModelConstant.THREAD_CATEGORY_CHAT.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_chat_publish");
//				td.setCategoryCnName("社区杂谈");
//			}else if (ModelConstant.THREAD_CATEGORY_BEAUTIES.equals(td.getThreadCategory())) {
//				td.setCategoryImgName("img_beautifulgirl_publish");
//				td.setCategoryCnName("都市丽人");
//			}
			
		}
		
		for (int i = 0; i < list.size(); i++) {
			
			Thread td = list.get(i);
			String attachmentUrl = td.getAttachmentUrl();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				
				String[]urls = attachmentUrl.split(",");
				
				List<String>imgLinkList = new ArrayList<String>();
				List<String>thumbnailLinkList = new ArrayList<String>();
				
				for (int j = 0; j < (urls.length>3?3:urls.length); j++) {
					
					String urlKey = urls[j];
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					thumbnailLinkList.add(qiniuUtil.getThumbnailLink(urlKey, "3", "0"));
					
				}
				
				td.setImgUrlLink(imgLinkList);
				td.setThumbnailLink(thumbnailLinkList);
			}
			
		}
		
		
		log.debug("list is : " + list);		
		return BaseResult.successResult(list);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/communityInfo/getCommunityInfo", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<List<CommunityInfo>> getCommunityInfo(HttpSession session){
		
		User user = (User)session.getAttribute(Constants.USER);
		Long sect_id = user.getXiaoquId();
		if(sect_id == null || sect_id == 0){
			return BaseResult.fail("您还没有填写小区信息。");
		}
		Sort sort = new Sort(Direction.ASC , "infoType");
		
		List<CommunityInfo>cityList = communityService.getCommunityInfoByCityIdAndInfoType(user.getCityId(), "0",sort);
		List<CommunityInfo>regionList = communityService.getCommunityInfoByRegionId(user.getCountyId(), sort);
		List<CommunityInfo>sectList = communityService.getCommunityInfoBySectId(sect_id, sort);
		
		List<CommunityInfo>retList = new ArrayList<CommunityInfo>();
		for (int i = 0; i < cityList.size(); i++) {
			
			retList.add(cityList.get(i));
		}
		
		for (int i = 0; i < regionList.size(); i++) {
			
			retList.add(regionList.get(i));
		}
		
		for (int i = 0; i < sectList.size(); i++) {
			
			retList.add(sectList.get(i));
		}	
		
		return BaseResult.successResult(retList);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/annoucement/getAnnoucementList", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<List<Annoucement>> getAnnoucementList(HttpSession session){
		
		User user = (User)session.getAttribute(Constants.USER);
		Long sect_id = user.getXiaoquId();;
		if(sect_id == null || sect_id == 0){
			return BaseResult.fail("您还没有填写小区信息。");
		}
		Sort sort = new Sort(Direction.DESC , "annoucementId");
		List<Annoucement> list = communityService.getAnnoucementList(sort);
		
		return BaseResult.successResult(list);
		
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getUnreadComments", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult getUnreadComments(HttpSession session){
		
		User user = (User)session.getAttribute(Constants.USER);
		int unread = 0;
		try {
			
			if(user == null) {
				return BaseResult.successResult(unread);
			}
			
			user = userService.getById(user.getId());
			if(user == null) {
				return BaseResult.successResult(unread);
			}
			
			//TODO 已经在thread表中加了是否有未读评论字段，以后如果数据量大时，直接取thread表中的该字段做判断即可。
			unread = communityService.getUnreadCommentsCount(ModelConstant.COMMENT_STATUS_NORMAL, user.getId());
			
		} catch (Exception e) {

			log.error("getUnreadCommentsError: " + e.getMessage());
		}
		return BaseResult.successResult(unread);
		
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/thread/updateUnreadComment/{threadUserId}/{threadId}", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult updateUnreadComment(HttpSession session, @PathVariable long threadUserId, @PathVariable long threadId){
		
		User user = (User)session.getAttribute(Constants.USER);
		
		if (threadUserId == user.getId()) {
			
			communityService.updateCommentReaded(threadUserId, threadId);
		}
		
		return BaseResult.successResult("succeeded");
		
	}

	/**
	 * 上传图片到七牛服务器
	 * @param ret
	 * @param uploadIds
	 */
	@SuppressWarnings("rawtypes")
	private void upload2Qiniu(ThreadComment ret, String uploadIds ){
		
		InputStream inputStream = null;
		if (!StringUtil.isEmpty(uploadIds)) {
			
			uploadIds = uploadIds.substring(0, uploadIds.length()-1);	//截掉最后一个逗号
			String[]uploadIdArr = uploadIds.split(",");
			
			String uptoken = qiniuUtil.getUpToken();	//获取qiniu上传文件的token
			
			log.error("qiniu token :" + uptoken);
			
			String currDate = DateUtil.dtFormat(new Date(), "yyyyMMdd");
			String currTime = DateUtil.dtFormat(new Date().getTime(), "HHMMss");
			String tmpPathRoot = tmpFileRoot+File.separator+currDate+File.separator;
			
			File file = new File(tmpPathRoot);
			if (!file.exists()||!file.isDirectory()) {
				file.mkdirs();
			}
			String keyListStr = "";
			String imgHeight = "";
			String imgWidth = "";
			
			PutExtra extra = new PutExtra();
			
			User user = userService.getById(ret.getCommentUserId());
			String accessToken = systemConfigService.queryWXAToken(user.getAppId());
			
			try {
				for (int i = 0; i < uploadIdArr.length; i++) {

					String tmpPath = tmpPathRoot+currTime+"_"+ret.getCommentId()+"_"+i;
					File imgFile = new File(tmpPath);
					String uploadId = uploadIdArr[i];
					fileService.downloadFile(uploadId, accessToken, imgFile);
					
					String key = currDate+"_"+currTime+"_"+ret.getCommentId()+"_"+i;
					imgFile = new File(tmpPath);
					
					if (imgFile.exists() && imgFile.getTotalSpace()>0) {

						PutRet putRet = IoApi.putFile(uptoken, key, imgFile, extra);
						log.error("ret msg is : " + putRet.getException());
						log.error("putRet is : " + putRet.toString());
						
						while (putRet.getException()!=null) {
							putRet = IoApi.putFile(uptoken, key, imgFile, extra);
							java.lang.Thread.sleep(100);
						}
						
						boolean isUploaded = false;
						int counter = 0;
						Map map = null;
						while (!isUploaded && counter <3 ) {

							map = qiniuUtil.getImgs(domain+key);
							Object error = map.get("error");
							if (error != null) {
								log.error((String)error);
								log.error("start to re-upload ...");
								putRet = IoApi.putFile(uptoken, key, imgFile, extra);
								java.lang.Thread.sleep(100);
							}else {
								isUploaded = true;
								break;
							}
							
							counter++;
						}
						
						if (isUploaded) {
							
							keyListStr+=domain+key;
							
							Integer width = (Integer)map.get("width");
							Integer height = (Integer)map.get("height");
							
							imgWidth+=width;
							imgHeight+=height;
							
							if (i!=uploadIdArr.length-1) {
								keyListStr+=",";
								imgWidth+=",";
								imgHeight+=",";
							}
						}
						
					
					}
					
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}finally{
				if (inputStream!=null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
			}
		
			ret.setAttachmentUrl(keyListStr);
			ret.setImgHeight(imgHeight);
			ret.setImgWidth(imgWidth);
			communityService.updateThreadComment(ret);	//更新上传文件路径
		
		}
	}
}
