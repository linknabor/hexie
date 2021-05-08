package com.yumu.hexie.web.shequ;

import java.util.ArrayList;
import java.util.List;

import com.yumu.hexie.integration.wuye.resp.BaseResponse;
import com.yumu.hexie.integration.wuye.resp.BaseResponseDTO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.model.community.Message;
import com.yumu.hexie.model.community.MessageSect;
import com.yumu.hexie.service.exception.IntegrationBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.yumu.hexie.common.Constants;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.model.community.Notice;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.shequ.NoticeService;
import com.yumu.hexie.web.BaseController;
import com.yumu.hexie.web.BaseResult;
import com.yumu.hexie.web.shequ.vo.NoticeVO;

@RestController
@RequestMapping("/notice")
public class NoticeController extends BaseController {

	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private QiniuUtil qiniuUtil;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/get/{page}", method = RequestMethod.GET)
	public BaseResult<List<NoticeVO>> getNotice(@ModelAttribute(Constants.USER)User user, @PathVariable int page) {
		
		List<Notice> noticeList = noticeService.getNotice(user, page);
		List<NoticeVO> noticeVoList = new ArrayList<>(noticeList.size());
		noticeList.forEach(notice->noticeVoList.add(new NoticeVO(notice, qiniuUtil)));
		return BaseResult.successResult(noticeVoList);
	}

	/**
	 * 运营端查询
	 * @param baseRequestDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getList", method = RequestMethod.POST)
	public BaseResponseDTO<?> getNoticeList(@RequestBody BaseRequestDTO<Notice> baseRequestDTO) throws Exception{
		Page<Notice> page;
		try {
			page = noticeService.queryNotice(baseRequestDTO);
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), page);
	}

	/**
	 * 运营端查询明细
	 * @param baseRequestDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getDetail", method = RequestMethod.POST)
	public BaseResponseDTO<?> getNoticeDetail(@RequestBody BaseRequestDTO<String> baseRequestDTO) throws Exception{
		Notice notice;
		try {
			notice = noticeService.findOne(Long.parseLong(baseRequestDTO.getData()));
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId(), notice);
	}

	/**
	 * 运营端保存
	 * @param baseRequestDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public BaseResponseDTO<?> saveNotice(@RequestBody BaseRequestDTO<Notice> baseRequestDTO) throws Exception{
		try {
			noticeService.saveNotice(baseRequestDTO);
		} catch (Exception e) {
			throw new IntegrationBizException(e.getMessage(), e, baseRequestDTO.getRequestId());
		}
		return BaseResponse.success(baseRequestDTO.getRequestId());
	}






	
}
