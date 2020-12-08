package com.yumu.hexie.service.shequ.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.posting.mapper.QueryCommentMapper;
import com.yumu.hexie.integration.posting.mapper.QueryPostingMapper;
import com.yumu.hexie.integration.posting.vo.QueryPostingVO;
import com.yumu.hexie.integration.posting.vo.SaveCommentVO;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.community.Thread;
import com.yumu.hexie.model.community.ThreadComment;
import com.yumu.hexie.model.community.ThreadCommentRepository;
import com.yumu.hexie.model.community.ThreadRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.PostingService;

@Service
public class PostingServiceImpl implements PostingService {
	
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private ThreadCommentRepository threadCommentRepository;
	@Autowired
	private GotongService gotongService;
	
	
	@Override
	public CommonResponse<Object> getPosting(QueryPostingVO queryPostingVO) {

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "createDateTime");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
	    	
	    	List<String>sectIds = queryPostingVO.getSectIds();
	    	if (sectIds != null) {
	    		if (sectIds.isEmpty()) {
					sectIds = null;
				}
			}
			Pageable pageable = PageRequest.of(queryPostingVO.getCurrentPage(), queryPostingVO.getPageSize(), sort);
			Page<Object[]> page = threadRepository.getThreadList(ModelConstant.THREAD_CATEGORY_SUGGESTION, queryPostingVO.getUserName(), 
					queryPostingVO.getStartDate(), queryPostingVO.getEndDate(), queryPostingVO.getSectId(), 
					sectIds, pageable);
			
			List<QueryPostingMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryPostingMapper.class);
			
			QueryListDTO<List<QueryPostingMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			responsePage.setContent(list);
			
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 根据ID删除帖子
	 * @param postingId
	 */
	@Override
	@Transactional
	public void delete(String delIds) {
		
		Assert.hasText(delIds, "id不能为空。");
		
		String[]delArr = delIds.split(",");
		
		for (String delid : delArr) {
			Optional<Thread> optional = threadRepository.findById(Long.valueOf(delid));
			if (!optional.isPresent()) {
				throw new BizValidateException("未找到帖子，id : " + delid);
			}
			Thread t = optional.get();
			t.setThreadStatus(ModelConstant.THREAD_STATUS_DELETED);
			threadRepository.save(t);
		}
		
	}
	
	/**
	 * 获取帖子评论
	 */
	@Override
	public CommonResponse<Object> getComment(QueryPostingVO queryPostingVO) {
		
		Assert.hasText(queryPostingVO.getId(), "id不能为空。");

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			
			List<Object[]> page = threadCommentRepository.getCommentList(Long.valueOf(queryPostingVO.getId()));
			List<QueryCommentMapper> list = ObjectToBeanUtils.objectToBean(page, QueryCommentMapper.class);
			QueryListDTO<List<QueryCommentMapper>> responsePage = new QueryListDTO<>();
			responsePage.setContent(list);
			
			commonResponse.setData(responsePage);
			commonResponse.setResult("00");
			
		} catch (Exception e) {
			
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}
	
	/**
	 * 添加评论
	 * @param saveCommentVO
	 */
	@Override
	@Transactional
	public void addComment(SaveCommentVO saveCommentVO) {
		
		Assert.hasText(saveCommentVO.getId(), "id不能为空。");
		Assert.hasText(saveCommentVO.getContent(), "评论内容不能为空。");
		Assert.hasText(saveCommentVO.getUserId(), "评论用户id不能为空。");
		Assert.hasText(saveCommentVO.getUserName(), "评论用户姓名不能 为空。");
		
		Optional<Thread> optional = threadRepository.findById(Long.valueOf(saveCommentVO.getId()));
		if (!optional.isPresent()) {
			throw new BizValidateException("未查询到意见，id: " + saveCommentVO.getId());
		}
		
		Thread thread = optional.get();
		thread.setCommentsCount(thread.getCommentsCount()+1);
		thread.setLastCommentTime(System.currentTimeMillis());
		thread.setHasUnreadComment("true");	//是否有未读评论
		threadRepository.save(thread);
		
		ThreadComment comment = new ThreadComment();
		comment.setThreadId(thread.getThreadId());
		comment.setCommentContent(saveCommentVO.getContent());
		comment.setToUserId(thread.getUserId());
		comment.setToUserName(thread.getUserName());
		comment.setToUserReaded("false");
		comment.setCommentDateTime(System.currentTimeMillis());
		comment.setCommentDate(DateUtil.dtFormat(new Date(), "yyyyMMdd"));
		comment.setCommentTime(DateUtil.dtFormat(new Date().getTime(), "HHMMss"));
		comment.setCommentUserId(Long.valueOf(saveCommentVO.getUserId()));
		comment.setCommentUserName(saveCommentVO.getUserName());
		threadCommentRepository.save(comment);
		
//		if (comment.getCommentUserId() != thread.getUserId()) {
//			gotongService.sendPostingReplyMsg(thread);
//		}
		
	}
	
}
