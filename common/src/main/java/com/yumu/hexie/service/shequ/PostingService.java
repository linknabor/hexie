package com.yumu.hexie.service.shequ;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.posting.vo.QueryPostingSummaryVO;
import com.yumu.hexie.integration.posting.vo.QueryPostingVO;
import com.yumu.hexie.integration.posting.vo.SaveCommentVO;

public interface PostingService {

	CommonResponse<Object> getPosting(QueryPostingVO queryPostingVO);

	void delete(String delIds);

	CommonResponse<Object> getComment(QueryPostingVO queryPostingVO);

	void addComment(SaveCommentVO saveCommentVO);

	CommonResponse<Object> getPostingSummary(QueryPostingSummaryVO queryPostingSummaryVO);
}
