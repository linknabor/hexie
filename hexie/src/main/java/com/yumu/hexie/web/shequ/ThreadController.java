package com.yumu.hexie.web.shequ;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadComment;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.CommunityService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@Controller
public class ThreadController extends BaseController {

	@Inject
	private CommunityService communityService;
	
	@Inject
	private UserService userService;
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/getThreadList", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResult<Map<String,Object>> getThreadList(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			Sort sort = new Sort(Direction.DESC , "stickPriority", "createDate", "createTime");
			int currPage=baseRequestDTO.getCurr_page();
			int pageSize=baseRequestDTO.getPage_size();
			Pageable pageable = new PageRequest(currPage, pageSize, sort);
			String nickName=baseRequestDTO.getData().get("nickName");
			String createDate=baseRequestDTO.getData().get("createDate");
			String sectId=baseRequestDTO.getData().get("sectIds");
			if(!StringUtil.isEmpty(createDate)){
				createDate=createDate.replaceAll("-", "");
			}
			Page<Thread> page=communityService.getThreadList(nickName,createDate,sectId,baseRequestDTO.getSectList(),pageable);
			Map<String,Object> map=new HashMap<>();
			map.put("list", page.getContent());
			map.put("count", page.getTotalElements());	
			return BaseResult.successResult(map);
		}
		return BaseResult.fail("签名错误!");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/deleteThread", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResult<String> deleteThread(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			String threadIds=baseRequestDTO.getData().get("threadIds");
			String[] threda_ids=threadIds.split(",");
			communityService.deleteThread(threda_ids);
			return BaseResult.successResult("");
		}
		return BaseResult.fail("签名错误!");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/getThreadDetail", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResult<Map<String,Object>> getThreadDetail(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			String  threadId=baseRequestDTO.getData().get("threadId");
			Thread  thread=communityService.getThreadByTreadId(Long.parseLong(threadId));
			List<ThreadComment>  list=communityService.getCommentListByThreadId(Long.parseLong(threadId));
			Map<String,Object> map=new HashMap<>();
			map.put("list", list);
			map.put("thread", thread);
			return BaseResult.successResult(map);
		}
		return BaseResult.fail("签名错误!");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/saveThreadComment", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResult<String> saveThreadComment(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			Map<String,String> map=baseRequestDTO.getData();
			String threadId=map.get("threadId");
			String content=map.get("content");
			String userId=map.get("userId");
			String userName=map.get("userName");
			communityService.saveThreadComment(Long.parseLong(threadId),content,Long.parseLong(userId),userName);	
			return BaseResult.successResult("");
		}
		return BaseResult.fail("签名错误!");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/getUserInfo", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResult<Map<String,Object>> getUserInfo(@RequestBody BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		if("hexie-servplat".equals(baseRequestDTO.getSign())){
			String userId=baseRequestDTO.getData().get("userId");
			User user=userService.getById(Long.parseLong(userId));
			Map<String,Object> map=new HashMap<>();
			map.put("userInfo", user);
			return BaseResult.successResult(map);
		}
		return BaseResult.fail("签名错误!");
	}

}
