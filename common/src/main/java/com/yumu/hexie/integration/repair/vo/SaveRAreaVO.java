package com.yumu.hexie.integration.repair.vo;

import java.io.Serializable;
import java.util.List;

import com.yumu.hexie.model.localservice.repair.RepairArea;

public class SaveRAreaVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2747102841149552593L;
	private List<RepairArea> areaList;
	private List<String> delSectIds;
	public List<RepairArea> getAreaList() {
		return areaList;
	}
	public void setAreaList(List<RepairArea> areaList) {
		this.areaList = areaList;
	}
	public List<String> getDelSectIds() {
		return delSectIds;
	}
	public void setDelSectIds(List<String> delSectIds) {
		this.delSectIds = delSectIds;
	}
	@Override
	public String toString() {
		return "SaveRAreaVO [areaList=" + areaList + ", delSectIds=" + delSectIds + "]";
	}
	
	
	
}
