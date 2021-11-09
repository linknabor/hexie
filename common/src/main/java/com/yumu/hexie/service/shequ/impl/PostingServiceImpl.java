package com.yumu.hexie.service.shequ.impl;

import java.util.*;

import javax.transaction.Transactional;

import com.yumu.hexie.integration.posting.vo.QueryPostingSummaryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.shequ.PostingService;

@Service
public class PostingServiceImpl implements PostingService {

	private static Logger logger = LoggerFactory.getLogger(PostingServiceImpl.class);

	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private ThreadCommentRepository threadCommentRepository;

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
	 * @param delIds
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
			
			List<Object[]> page = threadCommentRepository.getCommentList(Long.parseLong(queryPostingVO.getId()));
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
		comment.setCommentUserId(Long.parseLong(saveCommentVO.getUserId()));
		comment.setCommentUserName(saveCommentVO.getUserName());
		threadCommentRepository.save(comment);
	}

	@Override
	public CommonResponse<Object> getPostingSummary(QueryPostingSummaryVO queryPostingSummaryVO) {
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			if(queryPostingSummaryVO.getSectIds() == null || queryPostingSummaryVO.getSectIds().isEmpty()) {
				throw new BizValidateException("查询范围有误");
			}
			String[] sectIds = queryPostingSummaryVO.getSectIds().toArray(new String[0]);

			//总数
			List<Thread> listCount = threadRepository.findThreadCount(queryPostingSummaryVO.getStartDate(), queryPostingSummaryVO.getEndDate(), sectIds);
			logger.error("listCount :" + listCount);
			//回复数
			List<Thread> listComm = threadRepository.findThreadCommentCount(queryPostingSummaryVO.getStartDate(), queryPostingSummaryVO.getEndDate(), sectIds);
			//未回复数
			List<Thread> listNoComm = threadRepository.findThreadNoCommentCount(queryPostingSummaryVO.getStartDate(), queryPostingSummaryVO.getEndDate(), sectIds);
			//转工单数
			List<Thread> listRectified = threadRepository.findThreadRectified(queryPostingSummaryVO.getStartDate(), queryPostingSummaryVO.getEndDate(), sectIds);

			List<Map<String, String>> list = new ArrayList<>();
			for(String key : sectIds) {
				String posting_num = "0";
				String posting_normal_num = "0";
				String posting_abnormal_num = "0";
				String posting_rectify_num = "0";

				for(Thread t : listCount) {
					if(key.equals(t.getUserSectId())) {
						posting_num = String.valueOf(t.getCommentsCount());
						break;
					}
				}

				for(Thread t : listComm) {
					if(key.equals(t.getUserSectId())) {
						posting_normal_num = String.valueOf(t.getCommentsCount());
						break;
					}
				}

				for(Thread t : listNoComm) {
					if(key.equals(t.getUserSectId())) {
						posting_abnormal_num = String.valueOf(t.getCommentsCount());
						break;
					}
				}

				for(Thread t : listRectified) {
					if(key.equals(t.getUserSectId())) {
						posting_rectify_num = String.valueOf(t.getCommentsCount());
						break;
					}
				}

				Map<String, String> map = new HashMap<>();
				map.put("sect_id", key);
				map.put("posting_num", posting_num);
				map.put("posting_normal_num", posting_normal_num);
				map.put("posting_abnormal_num", posting_abnormal_num);
				map.put("posting_rectify_num", posting_rectify_num);
				list.add(map);
			}

			logger.info("posing list : " + list);
			commonResponse.setData(list);
			commonResponse.setResult("00");
		} catch (Exception e) {
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		return commonResponse;
	}

}
