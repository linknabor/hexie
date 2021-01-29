package com.yumu.hexie.service.hexiemessage.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.integration.oper.mapper.QueryOperRegionMapper;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil2;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.vo.Message;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.hexiemessage.HexieMessage;
import com.yumu.hexie.model.hexiemessage.HexieMessageRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.SmsService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.hexiemessage.HexieMessageService;
import com.yumu.hexie.service.oper.OperService;
import com.yumu.hexie.vo.req.MessageReq;
@Service
public class HexieMessageServiceImpl<T> implements HexieMessageService{
	
	private static Logger logger = LoggerFactory.getLogger(HexieMessageServiceImpl.class);
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private HexieMessageRepository hexieMessageRepository;
	@Autowired
	protected SmsService smsService;
	@Autowired
	private GotongService gotongService;
	@Autowired
	private WuyeUtil2 wuyeUtil2;
	@Autowired
	private QiniuUtil qiniuUtil;
	@Autowired
	private OperService operService;
	
	
	@Value(value = "${tmpfile.dir}")
    private String tmpFileRoot;
	@Value(value = "${qiniu.domain}")
	private String qiniuDomain;
	
	
	/**
	 * 公众号群发消息通知功能
	 */
	@Override
	@Transactional
	public boolean sendMessage(HexieMessage exr) {
		
		boolean success = false;
		boolean successFlag = false;
		String[] wuyeid = exr.getWuyeId().split(",");
		for (int i = 0; i < wuyeid.length; i++) {
			List<User> userList = userRepository.findByWuyeId(wuyeid[i]);
			User user = null;
			if (userList == null || userList.isEmpty()) {
				user = new User();
			}else {
				user = userList.get(0);
			}
			logger.info("will sent wuye message to user : " + user);
			success = saveMessage(exr, user);
			if (success) {
				successFlag = true;	//当前这户，有一个绑定者成功就算成功
			}
		}
		return successFlag;

	}
	
	/**
	 * 公众号群发消息通知功能
	 * @param exr
	 * @param user
	 */
	@Override
	public boolean saveMessage(HexieMessage exr, User user) {
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		HexieMessage hexieMessage = new HexieMessage();
		BeanUtils.copyProperties(exr, hexieMessage);
		hexieMessage.setUserId(user.getId());
		hexieMessage.setDate_time(df.format(new Date()));
		hexieMessage.setWuyeId(user.getWuyeId());
		hexieMessageRepository.save(hexieMessage);
		
		boolean success = true;
		if (!StringUtils.isEmpty(user.getWuyeId())) {
			success = gotongService.sendGroupMessage(user.getOpenid(), user.getAppId(), hexieMessage.getId(), hexieMessage.getContent());
		}else {
			success = false;
		}
		hexieMessage.setSuccess(success);
		hexieMessageRepository.save(hexieMessage);
		return success;
	}
	
	@Override
	public HexieMessage getMessage(long messageId) {
		
		return hexieMessageRepository.findById(messageId).get();
	}
	
	/**
	 * 移动端发送消息(队列操作)
	 * @param user
	 * @param messageReq
	 * @throws Exception 
	 */
	@Override
	public void sendMessageMobile(User user, MessageReq messageReq) throws Exception {
		
		Assert.hasText(messageReq.getSectId(), "小区ID不能为空。");
		
		//先把文件上传到七牛
		String base64Image = messageReq.getImgUrls();
		if (!StringUtils.isEmpty(base64Image)) {
			
			String indexStr = ";base64,";
			int imageLastIndex = base64Image.lastIndexOf(indexStr);
			String imgStr = base64Image.substring(imageLastIndex+indexStr.length(), base64Image.length());
			
			byte[] imageByte = base64img2byte(imgStr);					
			
			String key = upload2qiniu(imageByte);
			messageReq.setImgUrls(qiniuDomain + key);
		}
		wuyeUtil2.sendMessage(user, messageReq);	//推送给community异步处理
	}

	private String upload2qiniu(byte[] imageByte) throws FileNotFoundException, IOException, InterruptedException {
		String currDate = DateUtil.dtFormat(new Date(), "yyyyMMdd");
		String currTime = DateUtil.dtFormat(new Date().getTime(), "HHMMss");
		String tmpPathRoot = tmpFileRoot + File.separator + currDate+File.separator;
		File folder = new File(tmpPathRoot);	//先创建目录
		if (!folder.exists()||!folder.isDirectory()) {
			folder.mkdirs();
		}
		String tmpPath = tmpPathRoot+currTime+"_msg_0";	//如果有多个图片，那么循环编后面的序号
		File imgFile = writeByte2File(imageByte, tmpPath);
		
		String key = currDate+"_"+currTime+"_msg_0";
		
		String uptoken = qiniuUtil.getUpToken();	//获取qiniu上传文件的token
		PutExtra extra = new PutExtra();
		
		int count = 0;
		PutRet putRet = null;
		if (imgFile.exists() && imgFile.getTotalSpace()>0) {
			while (putRet==null || putRet.getException()!=null) {
				if (count == 3) {
					break;
				}
				putRet = IoApi.putFile(uptoken, key, imgFile, extra);
				java.lang.Thread.sleep(1000);
				logger.error("exception msg is : " + putRet.getException());
				logger.error("putRet is : " + putRet.toString());
				count++;
			}
			
		}
		return key;
	}

	/**
	 * byte转文件
	 * @param imageByte
	 * @param tmpPath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private File writeByte2File(byte[] imageByte, String tmpPath) throws FileNotFoundException, IOException {
		File imgFile = new File(tmpPath);
		FileOutputStream imageStream = new FileOutputStream(tmpPath);
		imageStream.write(imageByte);
		imageStream.flush();
		imageStream.close();
		return imgFile;
	}

	/**
	 * 图片base64转byte
	 * @param imgStr
	 * @return
	 */
	private byte[] base64img2byte(String imgStr) {
		// Base64解码
		byte[] imageByte = null;
		try {
			imageByte = Base64.getDecoder().decode(imgStr);      
			for (int i = 0; i < imageByte.length; ++i) {      
				if (imageByte[i] < 0) {// 调整异常数据      
					imageByte[i] += 256;      
				}      
			}      
		} catch (Exception e) {
			 logger.error(e.getMessage(), e);
		}
		return imageByte;
	}

	/**
	 * 根据批次号获取消息内容
	 */
	@Override
	@Cacheable(cacheNames = ModelConstant.KEY_MSG_VIEW_CACHE, key = "#batchNo", unless = "#result == null")
	public HexieMessage getMessageByBatchNo(String batchNo) {

		HexieMessage hexieMessage = null;
		List<HexieMessage> list = hexieMessageRepository.findByBatchNo(batchNo);
		if (!list.isEmpty()) {
			hexieMessage = list.get(0);
		}else {
			try {
				Message message = wuyeUtil2.getMessage(new User(), batchNo).getData();
				hexieMessage = new HexieMessage();
				hexieMessage.setBatchNo(message.getBatchNo());
				hexieMessage.setContent(message.getContent());
				hexieMessage.setImgUrls(message.getImgUrls());
				hexieMessage.setDate_time(message.getDateTime());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (StringUtils.isEmpty(hexieMessage.getBatchNo())) {
				throw new BizValidateException("没有可看查的消息。");
			}
		}
		return hexieMessage;
	}

	/**
	 * 获取发送历史
	 */
	@Override
	public BaseResult<List<Message>> getSendHistory(User user) throws Exception {

		List<QueryOperRegionMapper> list = operService.getRegionListMobile(user, String.valueOf(ModelConstant.SERVICE_OPER_TYPE_MSG_SENDER));
		StringBuffer buffer = new StringBuffer();
		for (QueryOperRegionMapper queryOperRegionMapper : list) {
			buffer.append(queryOperRegionMapper.getSectId()).append(",");
		}
		String sectIds = "";
		if (buffer.length()>0) {
			sectIds = buffer.deleteCharAt(buffer.length() - 1).toString();
			return wuyeUtil2.getMessageHistory(user, sectIds);
		} else {
			BaseResult<List<Message>> baseResult = new BaseResult<>();
			return baseResult;
		}
		
	}
	


}
