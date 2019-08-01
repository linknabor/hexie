package com.yumu.hexie.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.wuye.resp.BaseResponseDTO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Message;
import com.yumu.hexie.model.community.MessageSect;
import com.yumu.hexie.model.user.Feedback;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.MessageService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResponse;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.user.req.ReplyReq;

@Controller(value = "messageController")
public class MessageController extends BaseController {
	
	private static final int PAGE_SIZE = 10;
	@Inject
	private MessageService messageService;
	
	private static Map<Integer, String> msgTypeMap = new HashMap<>();
	
	@PostConstruct
	public void initMsgTypeMapping() {
		
		msgTypeMap.put(0, "wuye");
		msgTypeMap.put(1, "yewei");
		msgTypeMap.put(2, "juwei");
		msgTypeMap.put(3, "bianmin");
		msgTypeMap.put(9, "pingtai");
	}
	
	/**
	 * 移动端查询消息列表
	 * @param user
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/messages/{currentPage}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Map<String, List<Message>>> messages(@ModelAttribute(Constants.USER)User user, @PathVariable int currentPage)
			throws Exception {
		
		Map<String, List<Message>> map = new HashMap<>();
		Iterator<Entry<Integer, String>> it = msgTypeMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer, String> entry = it.next();
			Integer msgType = entry.getKey();
			String msgTypeValue = entry.getValue();
			List<Message> messageList = messageService.queryMessagesByUserAndType(user, msgType, currentPage, PAGE_SIZE);
			map.put(msgTypeValue, messageList);
		}
		return BaseResult.successResult(map);
	}

	//消息详情
	@RequestMapping(value = "/messageDetail/{messageId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<Message> getMessageDetail(@ModelAttribute(Constants.USER) User user,@PathVariable long messageId)
			throws Exception {
		Message message = messageService.findOne(messageId);
		return BaseResult.successResult(message);
	}
	

	//feedback
	@RequestMapping(value = "/feedbacks/{messageId}", method = RequestMethod.GET)
	@ResponseBody
	public BaseResult<List<Feedback>> queryFeedback(@PathVariable long messageId)
			throws Exception {
		//暂时没分页，需要分页
		return BaseResult.successResult(messageService.queryReplays(messageId, 0, 20));
	}

	@RequestMapping(value = "/pushFeedback", method = RequestMethod.POST)
	@ResponseBody
	public BaseResult<Feedback> pushFeedback(@ModelAttribute(Constants.USER)User user,@RequestBody ReplyReq req)
			throws Exception {
		return BaseResult.successResult(messageService.reply(user.getId(),user.getNickname(),user.getHeadimgurl(), req.getMessageId(), req.getContent()));
	}
	
	/**
	 * 管理端查询消息列表
	 * @param baseRequestDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/messages", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseDTO<?> messages(@RequestBody BaseRequestDTO<Message> baseRequestDTO)
			throws Exception {

		Page<Message> page = null;
		try {
			page = messageService.queryMessages(baseRequestDTO);
		} catch (Exception e) {
			return BaseResponse.fail(baseRequestDTO.getRequestId(), e.getMessage());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), page);
	}
	
	/**
	 * 管理端新增消息
	 * @param baseRequestDTO
	 * @return
	 */
	@RequestMapping(value = "/saveMessage", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseDTO<String> saveMessage(@RequestBody BaseRequestDTO<Message> baseRequestDTO) {
		
		try {
			messageService.saveMessage(baseRequestDTO);
		} catch (Exception e) {
			return BaseResponse.fail(baseRequestDTO.getRequestId(), e.getMessage());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId());
	}
	
	/**
	 * 管理端信息详情
	 * @param user
	 * @param messageId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/messageDetail", method = RequestMethod.POST)
	@ResponseBody
	public BaseResponseDTO<Message> getMessageDetail(@RequestBody BaseRequestDTO<String> baseRequestDTO)
			throws Exception {
		Message message = messageService.findOne(Long.valueOf(baseRequestDTO.getData()));
		List<MessageSect> list = messageService.queryMessageSectList(Long.valueOf(baseRequestDTO.getData()));
		List<String> sectList = new ArrayList<String>(list.size());
		for (MessageSect messageSect : list) {
			sectList.add(String.valueOf(messageSect.getSectId()));
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), message, sectList);
	}
	
	
}
