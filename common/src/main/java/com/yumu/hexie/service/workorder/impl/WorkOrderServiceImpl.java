package com.yumu.hexie.service.workorder.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.OrderNoUtil;
import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.integration.workorder.WorkOrderUtil;
import com.yumu.hexie.integration.workorder.resp.OrderDetailVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrderServiceVO;
import com.yumu.hexie.integration.workorder.resp.WorkOrdersVO;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.workorder.WorkOrderService;
import com.yumu.hexie.service.workorder.req.WorkOrderReq;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {
	
	private static Logger logger = LoggerFactory.getLogger(WorkOrderServiceImpl.class);
	
	@Autowired
	private WorkOrderUtil workOrderUtil;
	
	@Autowired
	private QiniuUtil qiniuUtil;
	
	@Value(value = "${qiniu.domain}")
    private String domain;
	
	public static void main(String[] args) {
		
		String str = DateUtil.dtFormat(System.currentTimeMillis(), DateUtil.dttmSimple);
		System.out.println(str);
	}
	
	/**
	 * 首页获取工单服务
	 * @param user
	 * @throws Exception 
	 */
	@Override
	public WorkOrderServiceVO getService(User user, String sectId) throws Exception {
		
		if (StringUtil.isEmpty(user.getSectId()) || "0".equals(user.getSectId()) || StringUtil.isEmpty(sectId)) {
			throw new BizValidateException("您暂未绑定房屋，请前往“我是业主”进行操作！");
		}
		CommonResponse<WorkOrderServiceVO> commonResponse =	workOrderUtil.getService(user, sectId);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		return commonResponse.getData(); 
	
	}
	
	/**
	 * 业主添加工单
	 * @param workOrderReq
	 * @throws Exception 
	 */
	@Override
	public void addWorkOrder(User user, WorkOrderReq workOrderReq) throws Exception {
		
		logger.info("workOrderReq : " + workOrderReq);
		
		List<File> fileList = convertMultipartFiles(workOrderReq.getFileList());
		List<String> imgPathList = uploadFiles(fileList);
		workOrderReq.setImages(imgPathList);
		CommonResponse<String> commonResponse =	workOrderUtil.addWorkOrder(user, workOrderReq);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
	}
	
	/**
	 * 查询用户工单
	 * @param user
	 * @return 
	 * @throws Exception
	 */
	@Override
	public WorkOrdersVO queryWorkOrder(User user) throws Exception {
		
		CommonResponse<WorkOrdersVO> commonResponse = workOrderUtil.queryWorkOrder(user);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		return commonResponse.getData(); 
	}
	
	/**
	 * 查询工单明细
	 * @param user
	 * @param orderId
	 * @throws Exception
	 */
	@Override
	public OrderDetailVO getOrderDetail(User user, String orderId) throws Exception{
		
		logger.info("queryWorkOrderDetail, orderId : " + orderId);
		
		Assert.hasText(orderId, "工单ID不能为空");
		CommonResponse<OrderDetailVO> commonResponse = workOrderUtil.getOrderDetail(user, orderId);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		OrderDetailVO vo = commonResponse.getData();
		vo.getOrderDetail().initImages(qiniuUtil);	//初始化预览图
		return vo;
	}
	
	/**
	 * 撤消工单
	 * @param user
	 * @param orderId
	 * @throws Exception
	 */
	@Override
	public void reverseOrder(User user, String orderId, String reason) throws Exception{
		
		Assert.hasText(orderId, "工单ID不能为空");
		Assert.hasText(reason, "驳回原因不能为空");
		
		CommonResponse<String> commonResponse = workOrderUtil.reverseOrder(user, orderId, reason);
		if (!"00".equals(commonResponse.getResult())) {
			throw new BizValidateException(commonResponse.getErrMsg());
		}
		
	}
	
	/**
	 * 上传文件到qiniu
	 * @param fileList
	 * @return
	 */
	private List<String> uploadFiles(List<File> fileList) {
		
		if (fileList == null || fileList.isEmpty()) {
			return null;
		}
		String uptoken = qiniuUtil.getUpToken();	//获取qiniu上传文件的token
		PutExtra extra = new PutExtra();
		List<String> imagePathList = new ArrayList<>(fileList.size());
		
		Date currDate = new Date();
		String date = DateUtil.dtFormat(currDate, "yyyyMMdd");
		String time = DateUtil.dtFormat(currDate.getTime(), "HHMMss");
		String random = OrderNoUtil.getRandomStr();
		
		for (int i = 0; i < fileList.size(); i++) {
			
			String key = date+"_"+time+"_"+random+"_"+i;
			PutRet putRet = IoApi.putFile(uptoken, key, fileList.get(i), extra);
			if (putRet.getException() != null) {
				logger.error(putRet.getException().getMessage(), putRet.getException());
				throw new BizValidateException("上传图片失败：" + putRet.getException().getMessage());
			}
			String imgPath = domain + key;
			imagePathList.add(imgPath);
		}
		
		return imagePathList;
	}
	
	/**
	 * 批量转换multipart文件格式
	 * @param multipartFiles
	 * @return
	 * @throws Exception
	 */
	private List<File> convertMultipartFiles(MultipartFile[]multipartFiles) throws Exception {
		
		List<File> fileList = new ArrayList<>();
		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				fileList.add(multipartFile2File(multipartFile));
			}
		}
		return fileList;
		
	}
	
	/**
	 * multipartFile转file
	 * @param multipartFile
	 * @return
	 * @throws Exception
	 */
	private File multipartFile2File(MultipartFile multipartFile) throws Exception {
		
		File file = null;
		String originalFilename = multipartFile.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        String prefix = "";
        String postfix = "";
        if (filename.length == 1) {
        	prefix = filename[0];
		} else {
			prefix = filename[0];
			postfix = filename[1];
		}
        file = File.createTempFile(prefix, postfix);
        multipartFile.transferTo(file);
        file.deleteOnExit();
        return file;
	}
	

}
