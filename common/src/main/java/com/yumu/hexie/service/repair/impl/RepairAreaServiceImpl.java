package com.yumu.hexie.service.repair.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.repair.vo.QueryRAreaVO;
import com.yumu.hexie.integration.repair.vo.SaveRAreaVO;
import com.yumu.hexie.model.localservice.repair.RepairArea;
import com.yumu.hexie.model.localservice.repair.RepairAreaRepository;
import com.yumu.hexie.service.repair.RepairAreaService;

@Service
public class RepairAreaServiceImpl implements RepairAreaService {
	
	private static Logger logger = LoggerFactory.getLogger(RepairAreaServiceImpl.class);
	
	@Autowired
	private RepairAreaRepository repairAreaRepository;
	
	@Override
	public CommonResponse<Object> getRepairArea(QueryRAreaVO queryRAreaVO) {
		
		logger.info("queryRAreaVO : " + queryRAreaVO);

		CommonResponse<Object> commonResponse = new CommonResponse<>();
		try {
			List<RepairArea> areaList = repairAreaRepository.findBySectIds(queryRAreaVO.getSectIds());
			QueryListDTO<List<RepairArea>> queryListDTO = new QueryListDTO<>();
			queryListDTO.setContent(areaList);
			commonResponse.setData(queryListDTO);
			commonResponse.setResult("00");
		} catch (Exception e) {
			
			logger.info(e.getMessage(), e);
			commonResponse.setErrMsg(e.getMessage());
			commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
		}
		
		return commonResponse;
	}	

	@Transactional
	@Override
	public void saveRepairArea(SaveRAreaVO saveRAreaVO) {
		
		logger.info("saveRAreaVO : " + saveRAreaVO);
		
		//先删后增
		List<RepairArea> areaList = repairAreaRepository.findBySectIds(saveRAreaVO.getDelSectIds());
		for (RepairArea repairArea : areaList) {
			repairAreaRepository.delete(repairArea);
		}
		
		for (RepairArea repairArea : saveRAreaVO.getAreaList()) {
			repairAreaRepository.save(repairArea);
		}
		
	}

}
