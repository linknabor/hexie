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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadComment;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.CommunityService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;

@Controller
public class ThreadController extends BaseController {

	private static final int PAGE_SIZE = 10;

	@Inject
	private CommunityService communityService;
	
	@Inject
	private UserService userService;
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/getThreadList", method = RequestMethod.GET,produces = "application/json")
	@ResponseBody
	public BaseResult<Map<String,Object>> getThreadList(@RequestParam(required=false) Integer  currPage,@RequestParam(required=false) Integer  pageSize,@RequestParam(required=false) String nickName,
		@RequestParam(required=false) String createDate,@RequestParam String sectIds) {
	//	Sort sort = new Sort(Direction.DESC , "stickPriority", "createDate", "createTime");
	//	Pageable page = new PageRequest(currPage, PAGE_SIZE, sort);
		String[] sect_ids=sectIds.split(",");
		if(!StringUtil.isEmpty(createDate)){
			createDate=createDate.replaceAll("-", "");
		}
		List<Object> list=communityService.getThreadList(nickName,createDate,sect_ids,currPage,pageSize);
		int count=communityService.getThreadListCount(nickName, createDate, sect_ids);
		Map<String,Object> map=new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		return BaseResult.successResult(map);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/deleteThread", method = RequestMethod.GET,produces = "application/json")
	@ResponseBody
	public BaseResult<String> deleteThread(@RequestParam String threadIds) {
		String[] threda_ids=threadIds.split(",");
		communityService.deleteThread(threda_ids);
		return BaseResult.successResult("");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/getThreadDetail", method = RequestMethod.GET,produces = "application/json")
	@ResponseBody
	public BaseResult<Map<String,Object>> getThreadDetail(@RequestParam String threadId) {
		Thread  thread=communityService.getThreadByTreadId(Long.parseLong(threadId));
		List<ThreadComment>  list=communityService.getCommentListByThreadId(Long.parseLong(threadId));
		Map<String,Object> map=new HashMap<>();
		map.put("list", list);
		map.put("thread", thread);
		return BaseResult.successResult(map);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/saveThreadComment", method = RequestMethod.POST,produces = "application/json")
	@ResponseBody
	public BaseResult<String> saveThreadComment(@RequestParam(required = false)String threadId,@RequestParam(required = false)String content,@RequestParam(required = false) String userId,@RequestParam(required = false) String userName) {
		communityService.saveThreadComment(Long.parseLong(threadId),content,Long.parseLong(userId),userName);
		return BaseResult.successResult("");
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servplat/thread/getUserInfo", method = RequestMethod.GET,produces = "application/json")
	@ResponseBody
	public BaseResult<Map<String,Object>> getUserInfo(@RequestParam String userId) {
		User user=userService.getById(Long.parseLong(userId));
		Map<String,Object> map=new HashMap<>();
		map.put("userInfo", user);
		return BaseResult.successResult(map);
	}

}
