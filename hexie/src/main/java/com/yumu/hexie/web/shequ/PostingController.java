package com.yumu.hexie.web.shequ;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.posting.vo.QueryPostingVO;
import com.yumu.hexie.integration.posting.vo.SaveCommentVO;
import com.yumu.hexie.service.shequ.PostingService;
import com.yumu.hexie.web.BaseController;

/**
 * ThreadController是servplat查询hexie的内容，PostingController是backmng查询hexie的内容
 * 以后都统一为下面的版本，ThreadController中的版本慢慢更替掉
 * @author david
 *
 */
@RestController
@RequestMapping("/posting")
public class PostingController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(PostingController.class);
	
	@Autowired
	private PostingService postingService;

	@RequestMapping(value = "/get", method = RequestMethod.POST)
	public CommonResponse<Object> getPosting(@RequestBody QueryPostingVO queryPostingVO) {
		
		logger.info("queryPostingVO : " + queryPostingVO);
		return postingService.getPosting(queryPostingVO);
	}
	
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	public CommonResponse<String> getPostingList(@RequestBody Map<String, String> map) {
		
		logger.info("postingId : " + map);
		postingService.delete(map.get("delIds"));
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
	
	@RequestMapping(value = "/comment/get", method = RequestMethod.POST)
	public CommonResponse<Object> getPostingList(@RequestBody QueryPostingVO queryPostingVO) {
		
		logger.info("queryPostingVO : " + queryPostingVO);
		return postingService.getComment(queryPostingVO);
	}
	
	@RequestMapping(value = "/comment/save", method = RequestMethod.POST)
	public CommonResponse<String> addComment(@RequestBody SaveCommentVO saveCommentVO) {
		
		logger.info("saveCommentVO : " + saveCommentVO);
		postingService.addComment(saveCommentVO);
		CommonResponse<String> commonResponse = new CommonResponse<>();
		commonResponse.setResult("00");
		return commonResponse;
	}
}
