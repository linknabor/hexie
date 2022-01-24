package com.yumu.hexie.service.oper.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.oper.OperUtil;
import com.yumu.hexie.integration.oper.mapper.QueryOperMapper;
import com.yumu.hexie.integration.oper.mapper.QueryOperRegionMapper;
import com.yumu.hexie.integration.oper.vo.QueryOperVO;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSect;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSectRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.oper.OperService;
import com.yumu.hexie.service.user.UserService;
import com.yumu.hexie.vo.OperAuthorization;

@Service
public class OperServiceImpl implements OperService {
	
	@Autowired
	private ServiceOperatorRepository serviceOperatorRepository;
	@Autowired
	private ServiceOperatorSectRepository serviceOperatorSectRepository;
	@Autowired
	private OperUtil operUtil;
	@Autowired
	private UserService userService;
	

	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, key = "#user.id")
	public void authorize(User user, OperAuthorization oa) throws Exception {
		
		Assert.hasText(oa.getTimestamp(), "timestamp is null!");
		Assert.hasText(oa.getType(), "type is null!");
		
		Long ts = Long.valueOf(oa.getTimestamp());
		if (System.currentTimeMillis() - ts > 30*60*1000 ) {
			throw new BizValidateException("授权码已失效。");
		}
		//TODO ? 可能不需要
//		if (!"wx315c7cb4080e5fd8".equals(user.getAppId())) {
//			if (StringUtils.isEmpty(user.getTel())) {
//				throw new BizValidateException("当前用户尚未注册。请先完成会员领卡或手机号注册。");
//			}
//		}
		
		if (ModelConstant.SERVICE_OPER_TYPE_WUYE_FEE_STAFF == Integer.valueOf(oa.getType()) || 
				ModelConstant.SERVICE_OPER_TYPE_OTHER_FEE_STAFF == Integer.valueOf(oa.getType())) {
			BaseResult<String> baseResult = operUtil.operAuthorize(user, oa);	//先请求community
			if (!"00".equals(baseResult.getResult())) {
				throw new BizValidateException("授权失败, errMsg : " + baseResult.getMessage()) ;
			}
		}
		
		List<ServiceOperator> operList = serviceOperatorRepository.findByTypeAndUserId(Integer.valueOf(oa.getType()), user.getId());
		if (!operList.isEmpty()) {
			String[]sectArr = oa.getSectIds().split(",");
			for (ServiceOperator serviceOperator : operList) {
				for (String sectId : sectArr) {
					serviceOperatorSectRepository.deleteByOperatorIdAndSectId(serviceOperator.getId(), sectId);
				}
				String[]newSectArr = oa.getSectIds().split(",");
				for (String sect : newSectArr) {
					ServiceOperatorSect sos = new ServiceOperatorSect();
					sos.setOperatorId(serviceOperator.getId());
					sos.setSectId(sect);
					serviceOperatorSectRepository.save(sos);
				}
				
			}
		}else {
			ServiceOperator so = new ServiceOperator();
			so.setName(user.getName());
			so.setTel(user.getTel());
			so.setUserId(user.getId());
			so.setType(Integer.parseInt(oa.getType()));	//ModelConstant.SERVICE_OPER_TYPE_MSG_SENDER
			so.setOpenId(user.getOpenid());
			serviceOperatorRepository.save(so);
			
			String[]sectArr = oa.getSectIds().split(",");
			for (String sect : sectArr) {
				ServiceOperatorSect sos = new ServiceOperatorSect();
				sos.setOperatorId(so.getId());
				sos.setSectId(sect);
				serviceOperatorSectRepository.save(sos);
			}
		}
		
	}
	
	/**
	 * 获取消息发送操作员列表
	 * @param queryOperVO
	 * @return
	 */
	@Override
	public CommonResponse<Object> getOperList(QueryOperVO queryOperVO) {
		
		Assert.hasText(queryOperVO.getOperType(), "操作员类型不能为空。");
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Order> orderList = new ArrayList<>();
	    	Order order = new Order(Direction.DESC, "id");
	    	orderList.add(order);
	    	Sort sort = Sort.by(orderList);
			
			Pageable pageable = PageRequest.of(queryOperVO.getCurrentPage(), queryOperVO.getPageSize(), sort);
			
			Page<Object[]> page = serviceOperatorRepository.getServOperByType(Integer.valueOf(queryOperVO.getOperType()), 
					queryOperVO.getOperName(), queryOperVO.getOperTel(), "", queryOperVO.getSectIds(), pageable);
			
			List<QueryOperMapper> list = ObjectToBeanUtils.objectToBean(page.getContent(), QueryOperMapper.class);
			QueryListDTO<List<QueryOperMapper>> responsePage = new QueryListDTO<>();
			responsePage.setTotalPages(page.getTotalPages());
			responsePage.setTotalSize(page.getTotalElements());
			list = list==null?new ArrayList<>():list;
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
	 * 获取操作员服务区域列表（平台用）
	 * @param queryOperVO
	 * @return
	 */
	@Override
	public CommonResponse<Object> getRegionList(QueryOperVO queryOperVO) {
		
		Assert.hasText(queryOperVO.getOperId(), "操作员ID不能为空。");
		
		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<Object[]> page = serviceOperatorRepository.getServeRegion(Long.valueOf(queryOperVO.getOperId()), queryOperVO.getSectIds());
			List<QueryOperRegionMapper> list = ObjectToBeanUtils.objectToBean(page, QueryOperRegionMapper.class);
			QueryListDTO<List<QueryOperRegionMapper>> responsePage = new QueryListDTO<>();
			list = list==null?new ArrayList<>():list;
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
	 * 获取操作员服务区域列表（移动端用）
	 * @param queryOperVO
	 * @return
	 * @throws Exception 
	 */
	@Override
	public List<QueryOperRegionMapper> getRegionListMobile(User user, String type) throws Exception {
		
		Assert.hasText(type, "操作人员类型不能为空。");
		
		List<ServiceOperator> soList = serviceOperatorRepository.findByTypeAndUserId(Integer.valueOf(type), user.getId());
		if (soList.isEmpty()) {
			throw new BizValidateException("can't find operator, userId : " + user.getId() + ", type : " + type);
		}
		ServiceOperator so = soList.get(0);
		List<Object[]> page = serviceOperatorRepository.getServeRegion(so.getId(), null);
		List<QueryOperRegionMapper> list = ObjectToBeanUtils.objectToBean(page, QueryOperRegionMapper.class);
		return list;
	}
	
	/**
	 * 取消工作人员授权
	 * @param queryOperVO
	 * @return
	 * @throws Exception 
	 */
	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, key = "#queryOperVO.operUserId")
	public void cancelAuthorize(QueryOperVO queryOperVO) throws Exception {
		
		String operId = queryOperVO.getOperId();
		String operUserId = queryOperVO.getOperUserId();
		String operType = queryOperVO.getOperType();
		
		Assert.hasText(operId, "操作员ID不能为空。");
		Assert.hasText(operType, "操作员类型不能为空。");
		
		User cancelUser = userService.getById(Long.valueOf(operUserId));
		
		if (ModelConstant.SERVICE_OPER_TYPE_WUYE_FEE_STAFF == Integer.valueOf(operType) || 
				ModelConstant.SERVICE_OPER_TYPE_OTHER_FEE_STAFF == Integer.valueOf(operType)) {
			
			StringBuffer sb = new StringBuffer();
			for (String sectId : queryOperVO.getSectIds()) {
				sb.append(sectId).append(",");
			}
			BaseResult<String> baseResult = operUtil.cancelAuthorize(cancelUser, operType, sb.toString());	//先请求community
			if (!"00".equals(baseResult.getResult())) {
				throw new BizValidateException("授权失败, errMsg : " + baseResult.getMessage()) ;
			}
		}
		
		List<ServiceOperator> operList = serviceOperatorRepository.findByTypeAndUserId(Integer.valueOf(operType), 
				Long.valueOf(operUserId));
		
		if (!operList.isEmpty()) {
			List<String> sectList = queryOperVO.getSectIds();
			String[]sectArr = new String[sectList.size()];
			sectArr = sectList.toArray(sectArr);
			for (ServiceOperator serviceOperator : operList) {
				for (String sectId : sectArr) {
					serviceOperatorSectRepository.deleteByOperatorIdAndSectId(serviceOperator.getId(), sectId);
				}
				
				List<String> existSectList = serviceOperatorSectRepository.findByOperatorId(serviceOperator.getId());
				if (existSectList.isEmpty()) {
					serviceOperatorRepository.delete(serviceOperator);
				}
			}
		}
		
	}
	

}
