package com.yumu.hexie.service.shequ.impl;

import java.util.*;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.interact.InteractUtil;
import com.yumu.hexie.integration.interact.req.InteractReq;
import com.yumu.hexie.integration.interact.req.SaveInteractCommentReq;
import com.yumu.hexie.integration.interact.req.SaveInteractInfoReq;
import com.yumu.hexie.integration.interact.resp.InteractCommentResp;
import com.yumu.hexie.integration.interact.resp.InteractInfoResp;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.common.pojo.dto.ActiveApp;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.CommunityService;

@Service("communityService")
public class CommunityServiceImpl implements CommunityService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QiniuUtil qiniuUtil;

	@Autowired
	private InteractUtil interactUtil;
	
	@Autowired
	private SystemConfigService systemConfigService;

	@Override
	public List<InteractInfoResp> getInteractList(User user, InteractReq req) throws Exception {
		CommonResponse<List<InteractInfoResp>> commonResponse = interactUtil.getInteractList(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		List<InteractInfoResp> list = commonResponse.getData();
		for (InteractInfoResp resp : list) {
			String attachmentUrl = resp.getAttachment_urls();
			if (!StringUtil.isEmpty(attachmentUrl)) {
				String[] urls = attachmentUrl.split(",");
				List<String> imgLinkList = new ArrayList<>();
				List<String> thumbnailLinkList = new ArrayList<>();
				for (int j = 0; j < (Math.min(urls.length, 3)); j++) {
					String urlKey = urls[j];
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					thumbnailLinkList.add(qiniuUtil.getPreviewLink(urlKey, "3", "0"));
				}
				resp.setImgUrlLink(imgLinkList);
				resp.setThumbnailLink(thumbnailLinkList);
			}
			String creatTime = DateUtil.formatDateTimeFromDB(resp.getCreate_date(), resp.getCreate_time());
			Date date = DateUtil.getDateTimeFromString(creatTime);
			String dateStr = DateUtil.getSendTime(date.getTime());
			resp.setFormattedDateTime(dateStr);
		}
		return list;
	}

	@Override
	@CacheEvict(cacheNames = ModelConstant.KEY_INTERACT_TYPE_CFG, key = "#appid")
	public List<Map<String, String>> getInteractType(User user, String appid) throws Exception {
		CommonResponse<List<Map<String, String>>> commonResponse = interactUtil.getInteractType(user);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		return commonResponse.getData();
	}

	@Override
	public void addInteract(User user, SaveInteractInfoReq req) throws Exception {
		User currUser = userRepository.findById(user.getId());
		req.setUser_head(currUser.getHeadimgurl());
		req.setUser_id(String.valueOf(currUser.getId()));
		req.setUser_name(currUser.getNickname());
		req.setSect_id(currUser.getSectId());
		req.setUser_mobile(currUser.getTel());
		
		ActiveApp activeApp = systemConfigService.getActiveApp(currUser);
		String openid = activeApp.getActiveOpenid();
		String appid = activeApp.getActiveAppid();
		
		req.setOpenid(openid);
		req.setAppid(appid);
		req.setEx_source("01"); //公众号
		if (appid.equals(user.getMiniAppId())) {	//如果是小程序用户，来源修改为小程序
			req.setEx_source("05"); //小程序
		}
		req.setEx_group("2"); //默认建议

		CommonResponse<Boolean> commonResponse = interactUtil.saveInteractInfo(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		if(!commonResponse.getData()) {
			throw new BizValidateException("保存发布内容失败");
		}
	}

	@Override
	public void deleteInteract(User user, InteractReq req) throws Exception {
		CommonResponse<Boolean> commonResponse = interactUtil.delInteractInfo(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		if(!commonResponse.getData()) {
			throw new BizValidateException("删除发布内容失败");
		}
	}

	@Override
	public InteractInfoResp getInteractInfoById(User user, InteractReq req) throws Exception {
		User currUser = userRepository.findById(user.getId());
		CommonResponse<InteractInfoResp> commonResponse = interactUtil.getInteractInfo(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		InteractInfoResp resp = commonResponse.getData();
		String attachmentUrl = resp.getAttachment_urls();
		if (!StringUtil.isEmpty(attachmentUrl)) {
			String[]urls = attachmentUrl.split(",");
			List<String> imgLinkList = new ArrayList<>();
			List<String> thumbnailLinkList = new ArrayList<>();
			for (String urlKey : urls) {
				imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
				thumbnailLinkList.add(qiniuUtil.getPreviewLink(urlKey, "3", "0"));
			}
			resp.setImgUrlLink(imgLinkList);
			resp.setThumbnailLink(thumbnailLinkList);
		}

		String creatTime = DateUtil.formatDateTimeFromDB(resp.getCreate_date(), resp.getCreate_time());
		Date date = DateUtil.getDateTimeFromString(creatTime);
		String dateStr = DateUtil.getSendTime(date.getTime());
		resp.setFormattedDateTime(dateStr);

		//是否是所有人
		if(req.getUserId().equals(String.valueOf(currUser.getId()))) {
			resp.setIsThreadOwner("true");
		}
		return resp;
	}

	@Override
	public List<InteractCommentResp> getCommentByInteractId(User user, InteractReq req) throws Exception {
		CommonResponse<List<InteractCommentResp>> commonResponse = interactUtil.getCommentList(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		List<InteractCommentResp> list = commonResponse.getData();
		for (InteractCommentResp resp : list) {
			if(user == null) {
				resp.setIsCommentOwner("false");
			} else {
				if (Objects.equals(resp.getComment_user_id(), String.valueOf(user.getId()))) {
					resp.setIsCommentOwner("true");
				} else {
					resp.setIsCommentOwner("false");
				}
			}

			String tcAttachmentUrl = resp.getAttachment_urls();
			if (!StringUtil.isEmpty(tcAttachmentUrl)) {
				String[] urls = tcAttachmentUrl.split(",");
				List<String> imgLinkList = new ArrayList<>();
				List<String> thumbnailLinkList = new ArrayList<>();
				for (String urlKey : urls) {
					imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
					thumbnailLinkList.add(qiniuUtil.getPreviewLink(urlKey, "3", "0"));
				}
				resp.setImgUrlLink(imgLinkList);
				resp.setThumbnailLink(thumbnailLinkList);
			}
			String creatTime = DateUtil.formatDateTimeFromDB(resp.getComment_date(), resp.getComment_time());
			Date date = DateUtil.getDateTimeFromString(creatTime);
			String dateStr = DateUtil.getSendTime(date.getTime());
			resp.setFmtCommentDateTime(dateStr);
		}
		return list;
	}

	@Override
	public InteractCommentResp addComment(User user, SaveInteractCommentReq req) throws Exception {
		User currUser = userRepository.findById(user.getId());
		req.setComment_user_head(currUser.getHeadimgurl());
		req.setComment_user_id(String.valueOf(currUser.getId()));
		req.setComment_user_name(currUser.getNickname());

		CommonResponse<InteractCommentResp> commonResponse = interactUtil.saveInteractComment(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		InteractCommentResp resp = commonResponse.getData();

		resp.setIsCommentOwner("true");
		String tcAttachmentUrl = resp.getAttachment_urls();
		if (!StringUtil.isEmpty(tcAttachmentUrl)) {
			String[]urls = tcAttachmentUrl.split(",");
			List<String> imgLinkList = new ArrayList<>();
			List<String> thumbnailLinkList = new ArrayList<>();
			for (String urlKey : urls) {
				imgLinkList.add(qiniuUtil.getInterlaceImgLink(urlKey, "1"));
				thumbnailLinkList.add(qiniuUtil.getPreviewLink(urlKey, "3", "0"));
			}
			resp.setImgUrlLink(imgLinkList);
			resp.setThumbnailLink(thumbnailLinkList);
		}

		String creatTime = DateUtil.formatDateTimeFromDB(resp.getComment_date(), resp.getComment_time());
		Date date = DateUtil.getDateTimeFromString(creatTime);
		String dateStr = DateUtil.getSendTime(date.getTime());
		resp.setFmtCommentDateTime(dateStr);
		return resp;
	}

	@Override
	public void deleteComment(User user, InteractReq req) throws Exception {
		CommonResponse<Boolean> commonResponse = interactUtil.delInteractComment(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		if(!commonResponse.getData()) {
			throw new BizValidateException("删除回复内容失败");
		}
	}

	@Override
	public InteractInfoResp saveGrade(User user, InteractReq req) throws Exception {
		CommonResponse<InteractInfoResp> commonResponse = interactUtil.saveInteractGrade(user, req);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		return commonResponse.getData();
	}
}
