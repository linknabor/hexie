package com.yumu.hexie.service.repair;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.model.localservice.repair.RepairArea;
import com.yumu.hexie.model.localservice.repair.RepairAreaRepository;

@Service
public class RepairAreaServiceImpl implements RepairAreaService {

	@Autowired
	private RepairAreaRepository repairAreaRepository;
	
	@Override
	public List<RepairArea> getRepairArea(RepairArea repairArea) {

		return repairAreaRepository.findByCspIdAndSectid(repairArea.getCspId(), repairArea.getSectId());
	}	

	@Transactional
	@Override
	public void saveRepairArea(List<RepairArea> list) {

		RepairArea repairArea = list.get(0);
		List<RepairArea> areaList = repairAreaRepository.findByCspIdAndSectid(repairArea.getCspId(), repairArea.getSectId());
		if (areaList != null && areaList.size() > 0) {
			repairAreaRepository.delete(areaList);	//先把原来保存的这个物业公司的维修区域全部删除
		}
		for (RepairArea area : list) {
			if (StringUtil.isEmpty(area.getSectId())) {
				continue;
			}
			repairAreaRepository.save(area); 
		}
		
	}

}
