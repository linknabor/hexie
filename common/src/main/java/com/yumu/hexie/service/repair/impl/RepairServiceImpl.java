package com.yumu.hexie.service.repair.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yumu.hexie.common.util.StringUtil;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.resp.BaseResult;
import com.yumu.hexie.integration.wuye.resp.HouseListVO;
import com.yumu.hexie.integration.wuye.vo.BaseRequestDTO;
import com.yumu.hexie.integration.wuye.vo.HexieHouse;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.distribution.region.RegionRepository;
import com.yumu.hexie.model.localservice.ServiceOperator;
import com.yumu.hexie.model.localservice.ServiceOperatorRepository;
import com.yumu.hexie.model.localservice.repair.RepairArea;
import com.yumu.hexie.model.localservice.repair.RepairAreaRepository;
import com.yumu.hexie.model.localservice.repair.RepairConstant;
import com.yumu.hexie.model.localservice.repair.RepairOrder;
import com.yumu.hexie.model.localservice.repair.RepairOrderRepository;
import com.yumu.hexie.model.localservice.repair.RepairProject;
import com.yumu.hexie.model.localservice.repair.RepairProjectRepository;
import com.yumu.hexie.model.localservice.repair.RepairSeed;
import com.yumu.hexie.model.localservice.repair.RepairSeedRepository;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSect;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorSectRepository;
import com.yumu.hexie.model.localservice.repair.ServiceOperatorVo;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.AddressRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.common.GotongService;
import com.yumu.hexie.service.common.UploadService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.repair.RepairAssignService;
import com.yumu.hexie.service.repair.RepairService;
import com.yumu.hexie.service.repair.req.RepairCancelReq;
import com.yumu.hexie.service.repair.req.RepairComment;
import com.yumu.hexie.service.repair.resp.RepairListItem;
import com.yumu.hexie.service.sales.BaseOrderService;
import com.yumu.hexie.vo.req.RepairOrderReq;

/**
 * <pre>
 * 维修服务
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: RepairServiceImpl.java, v 0.1 2016年1月1日 上午10:18:13  Exp $
 */
@Service("repairService")
public class RepairServiceImpl implements RepairService {
	
	private static final Logger log = LoggerFactory.getLogger(RepairServiceImpl.class);

    @Inject
    private RepairProjectRepository repairProjectRepository;
    @Inject
    private RepairOrderRepository repairOrderRepository;
    @Inject
    private AddressRepository addressRepository;
    @Inject
    private BaseOrderService baseOrderService;
    @Inject
    private RepairSeedRepository repairSeedRepository;
    @Inject
    private ServiceOperatorRepository serviceOperatorRepository;
    @Inject
    private UploadService uploadService;
    @Inject
    private GotongService gotongService;
    @Inject
    private RepairAssignService repairAssignService;
    @Inject
    private RegionRepository  regionRepository;
    @Inject
    private UserRepository  userRepository;
    @Inject
    private ServiceOperatorSectRepository serviceOperatorSectRepository;
    @Autowired
    private RepairAreaRepository repairAreaRepository;
    @Autowired
    private RepairService repairService;
    
    /**  
     * @param repairType
     * @return
     * @see com.yumu.hexie.service.repair.RepairService#queryProject(int)
     */
    @Override
    public List<RepairProject> queryProject(int repairType) {
        return repairProjectRepository.queryByRepairTypeAndStatus(repairType, RepairConstant.PROJECT_STATUS_AVALIBLE);
    }

    /** 
     * 维修下单
     * @param req
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Long repair(RepairOrderReq req, User user) {
    	
    	log.info("RepairOrderReq : " + req);
    	
    	User currUser = userRepository.findById(user.getId());
        RepairProject project = repairProjectRepository.findById(req.getProjectId()).get();
        Address address = addressRepository.findById(req.getAddressId());
        
        //查询region 
        Region region = regionRepository.findById(address.getXiaoquId());
        if(region != null && StringUtil.isNotEmpty(region.getSectId())){
        	currUser.setSectId(region.getSectId());
        }
        
        //校验小区是否在开通为序服务的范围内
        List<RepairArea> areaList = repairAreaRepository.findBySectId(currUser.getSectId());
        if (areaList == null || areaList.size() == 0) {
			throw new BizValidateException("当前地址 [" + address.getRegionStr() + "]尚未开通维修服务，请联系小区所在物业。");
		}
        RepairOrder order = new RepairOrder(req, currUser, project, address);
        order = repairOrderRepository.save(order);
        uploadService.updateRepairImg(order);
        repairAssignService.assignOrder(order);
        return order.getId();
    }

    /** 
     * @param orderId
     * @param user
     * @return
     * @see com.yumu.hexie.service.repair.RepairService#finish(long, com.yumu.hexie.model.user.User)
     */
    @Override
    public boolean finish(long orderId, User user) {
        RepairOrder order = repairOrderRepository.findById(orderId).get();
        if(order.getUserId() != user.getId()){
            return false;
        }
        if(order.getStatus() == RepairConstant.STATUS_CREATE
                ||order.getStatus() == RepairConstant.STATUS_CANCEL
                ){
            throw new BizValidateException("该维修单无法结束！");
        }
        if(order.canFinish(true)){
            order.finish(true);
            repairOrderRepository.save(order);
        }
        return true;
    }

    /** 
     * @param orderId
     * @param amount
     * @param user
     * @return
     * @throws Exception 
     */
    @Override
    public JsSign requestPay(long orderId, float amount, User user) throws Exception {
        RepairOrder ro = repairOrderRepository.findById(orderId).get();
        ro.setAmount(amount);
        ServiceOrder so = baseOrderService.createRepairOrder(ro, amount);
        return baseOrderService.requestPay(so);
    }

    /** 
     * @param orderId
     * @param amount
     * @param user
     */
    @Override
    public void payOffline(long orderId, float amount, User user) {
        RepairOrder ro = repairOrderRepository.findById(orderId).get();
        ro.payOffline(amount);
        repairOrderRepository.save(ro);
    }

    /** 
     * @param orderId
     * @param user
     * @see com.yumu.hexie.service.repair.RepairService#notifyPaySuccess(long, com.yumu.hexie.model.user.User)
     */
    @Override
    public void notifyPaySuccess(long orderId, User user) {
        RepairOrder ro = repairOrderRepository.findById(orderId).get();
        if(ro.getOrderId()!=null&&ro.getOrderId()!=0&&ro.getUserId() == user.getId()){
            baseOrderService.notifyPayed(ro.getOrderId());
        }
    }

    /** 
     * @param req
     * @param user
     * @see com.yumu.hexie.service.repair.RepairService#cancel(com.yumu.hexie.service.repair.req.RepairCancelReq, com.yumu.hexie.model.user.User)
     */
    @Override
    @Transactional
    public void cancel(RepairCancelReq req, User user) {
        RepairOrder ro = repairOrderRepository.findById(req.getOrderId()).get();
        if(ro.getUserId() == user.getId()){
            ro.cancel(req.getCancelReasonType(), req.getCancelReason());
            repairOrderRepository.save(ro);
            repairSeedRepository.deleteByRepairOrderId(ro.getId());
        }
        
    }

    /** 
     * @param orderId
     * @param user
     * @see com.yumu.hexie.service.repair.RepairService#deleteByUser(long, com.yumu.hexie.model.user.User)
     */
    @Override
    public void deleteByUser(long orderId, User user) {
        RepairOrder ro = repairOrderRepository.findById(orderId).get();
        if(ro.getUserId() == user.getId()){
            ro.deleteByUser();
            repairOrderRepository.save(ro);
        }
    }
    @Override
    public void deleteByOperator(long orderId, User user) {
        RepairOrder ro = repairOrderRepository.findById(orderId).get();
        if(ro.getStatus() != RepairConstant.STATUS_CANCEL
                && ro.getStatus() != RepairConstant.STATUS_FININSH
                        && ro.getStatus() != RepairConstant.STATUS_PAYED
                ){
            throw new BizValidateException("该订单还在处理中，无法删除！");
        }
        List<ServiceOperator>  os = serviceOperatorRepository.findByUserId(user.getId());
        if(os.size()<=0){
            throw new BizValidateException("你不是系统的维修工！");
        } else {
            for(ServiceOperator o : os) {
                if(o.getId() == ro.getOperatorId()) {
                    ro.deleteByOperator();
                    repairOrderRepository.save(ro);
                    break;
                }
            }
        }
    }
    

    /** 
     * @param comment
     * @param user
     * @see com.yumu.hexie.service.repair.RepairService#comment(com.yumu.hexie.service.repair.req.RepairComment, com.yumu.hexie.model.user.User)
     */
    @Override
    public void comment(RepairComment comment, User user) {
        RepairOrder ro = repairOrderRepository.findById(comment.getRepairId()).get();
        if(ro != null && ro.getUserId() == user.getId()){
            ro.comment(comment);
            ro = repairOrderRepository.save(ro);
            uploadService.updateRepairImg(ro);
        }
    }

    /** 
     * @param user
     * @return
     * @see com.yumu.hexie.service.repair.RepairService#queryTop20ByUser(com.yumu.hexie.model.user.User)
     */
    @Override
    public List<RepairListItem> queryTop20ByUser(User user) {
        List<RepairListItem> r = new ArrayList<>();
        List<RepairOrder> orders = repairOrderRepository.queryByUser(user.getId(), PageRequest.of(0, 20));
        for(RepairOrder order : orders) {
            r.add(new RepairListItem(order));
        }     
        return r;
    }

    /** 
     * @param id
     * @return
     * @see com.yumu.hexie.service.repair.RepairService#queryById(long)
     */
    @Override
    public RepairOrder queryById(long id) {
        return repairOrderRepository.findById(id).get();
    }


    /** 
     * @param repairOrderId
     * @param user
     * @see com.yumu.hexie.service.repair.RepairService#accept(long, com.yumu.hexie.model.user.User)
     */
    @Override
    @Transactional
    public void accept(long repairOrderId, User user) {
        RepairOrder ro = repairOrderRepository.findById(repairOrderId).get();
//        List<ServiceOperator> ops = serviceOperatorRepository.findByTypeAndUserId(ModelConstant.SERVICE_OPER_TYPE_WEIXIU, user.getId());
        List<ServiceOperator> ops = serviceOperatorRepository.findByUserIdAndSectIdAndType(user.getId(), ro.getSectId(), ModelConstant.SERVICE_OPER_TYPE_WEIXIU);
        if(ops != null && ops.size() >0) {
            ServiceOperator op = ops.get(0);
            ro.accept(op);
            ro = repairOrderRepository.save(ro);
            gotongService.sendRepairAssignedMsg(ro);
        }
        repairSeedRepository.deleteByRepairOrderId(repairOrderId);
    }

    /** 
     * @param repairOrderId
     * @param user
     * @see com.yumu.hexie.service.repair.RepairService#finishByOperator(long, com.yumu.hexie.model.user.User)
     */
    @Override
    public void finishByOperator(long repairOrderId, User user) {
        RepairOrder ro = repairOrderRepository.findById(repairOrderId).get();
        if(ro.getOperatorUserId() == user.getId() && ro.canFinish(false)){
            ro.finish(false);
            repairOrderRepository.save(ro);
        }
    }

    /** 
     * @param user
     * @param status
     * @return
     * @see com.yumu.hexie.service.repair.RepairService#queryTop20ByOperatorAndStatus(com.yumu.hexie.model.user.User, int)
     */
    @Override
    public List<RepairListItem> queryTop20ByOperatorAndStatus(User user, int status) {
        List<RepairListItem> r = new ArrayList<>();
        if(RepairConstant.ORDER_OP_STATUS_UNACCEPT == status) {
            List<RepairSeed> seeds = repairSeedRepository.findByOperatorUserId(user.getId());
            for(RepairSeed seed : seeds) {
                r.add(new RepairListItem(seed));
            }
        } else {
            List<Integer> statuses = new ArrayList<>();
            if(RepairConstant.ORDER_OP_STATUS_UNFINISH == status) {
                statuses.add(RepairConstant.STATUS_CREATE);
                statuses.add(RepairConstant.STATUS_ACCEPT);
            } else if(RepairConstant.ORDER_OP_STATUS_FINISHED == status) {
                statuses.add(RepairConstant.STATUS_FININSH);
                statuses.add(RepairConstant.STATUS_PAYED);
            }
            List<RepairOrder> orders = repairOrderRepository
                    .queryByOperatorUser(user.getId(),statuses, PageRequest.of(0, 20));
            for(RepairOrder order : orders) {
                r.add(new RepairListItem(order));
            }      
        }
        return r;
    }
    
    @Override
	public Long reassgin(long orderId, User user) {
		
		RepairOrder order = queryById(orderId);
		repairAssignService.assignOrder(order);
        return order.getId();
	}

	@Override
	public Page<RepairOrder> getRepairOderList(BaseRequestDTO<Map<String,String>> baseRequestDTO) {
		Map<String,String> map=baseRequestDTO.getData();
		Sort sort = new Sort(Direction.DESC , "id");
		int currPage=baseRequestDTO.getCurr_page();
		int pageSize=baseRequestDTO.getPage_size();
		Pageable pageable = PageRequest.of(currPage, pageSize, sort);
	    
	    List<String> sectList=baseRequestDTO.getSectList();
		String payType=map.get("payType");
		String  status=map.get("status");
		String finishByUser=map.get("finishByUser");
		String finishByOpeator=map.get("finishByOperator");
		String address=map.get("address");
		String tel=map.get("tel");
		String operatorName=map.get("operatorName");
		String operatorTel=map.get("operatorTel");
		String sectId=map.get("sectIds");
        return repairOrderRepository.getRepairOderList(payType,status,finishByUser,
                finishByOpeator,address,tel,operatorName,operatorTel,sectId,sectList,pageable);
	}

	@Override
	public Page<Object> getServiceoperator(BaseRequestDTO<Map<String, String>> baseRequestDTO) {
		Map<String,String> map=baseRequestDTO.getData();
		Sort sort = new Sort(Direction.DESC , "id");
		int currPage=baseRequestDTO.getCurr_page();
		int pageSize=baseRequestDTO.getPage_size();
		Pageable pageable = PageRequest.of(currPage, pageSize, sort);
	    
	    List<String> sectList=baseRequestDTO.getSectList();
		String name=map.get("name");
		String tel=map.get("tel");
		String sectId=map.get("sectIds");
        return serviceOperatorRepository.getServiceoperator(ModelConstant.SERVICE_OPER_TYPE_WEIXIU, name, tel, sectId, sectList, pageable);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = ModelConstant.KEY_USER_SERVE_ROLE, key = "#baseRequestDTO.data.userId" )
	public int saveRepiorOperator(BaseRequestDTO<ServiceOperatorVo> baseRequestDTO) {
		ServiceOperatorVo vo=baseRequestDTO.getData();
		String sectIds =vo.getSectIds();
		String[] sectids=sectIds.split(",");
		String tel=vo.getTel();
		String name=vo.getName();
		String userId=vo.getUserId();
		String id=vo.getId();
		String cspName=vo.getCspName();
		if(StringUtil.isEmpty(cspName)){
			cspName="";
		}
		ServiceOperator so=new ServiceOperator();
		if(StringUtil.isEmpty(id)){
			User u=userRepository.findById(Long.parseLong(userId));
			
			List<ServiceOperator>  operatorList= serviceOperatorRepository.findByUserId(u.getId());
			if(operatorList.size()>0 && cspName.equals(operatorList.get(0).getCompanyName())){
				return 2;//已存在改用户的维修工

			}
			so.setCreateDate(System.currentTimeMillis());
			so.setName(name);
			so.setTel(tel);
			so.setType(ModelConstant.SERVICE_OPER_TYPE_WEIXIU);
			so.setUserId(u.getId());
			so.setOpenId(u.getOpenid());
			so.setCompanyName(vo.getCspName());
		}else{
			so=serviceOperatorRepository.findById(Long.valueOf(id)).get();
			so.setName(name);
			serviceOperatorSectRepository.deleteByOperatorId(Long.valueOf(id));
		}
		ServiceOperator serviceOperator=serviceOperatorRepository.save(so);
        for (String sectid : sectids) {
            ServiceOperatorSect s = new ServiceOperatorSect();
            s.setSectId(sectid);
            s.setOperatorId(serviceOperator.getId());
            s.setCreateDate(System.currentTimeMillis());
            serviceOperatorSectRepository.save(s);
        }
		return 1;
	}

	@Override
	public Map<String, Object> operatorInfo(BaseRequestDTO<String> baseRequestDTO) {
		Map<String,Object> map=new HashMap<>();
		List<String> sectList=serviceOperatorSectRepository.findByOperatorId(Long.valueOf(baseRequestDTO.getData()));
		ServiceOperator serviceOperator =serviceOperatorRepository.findById(Long.valueOf(baseRequestDTO.getData())).get();
		map.put("sectList", sectList);
		map.put("serviceOperator", serviceOperator);
		return map;
	}

	@Override
	@Transactional
	public void deleteOperator(BaseRequestDTO<Map<String, String>> baseRequestDTO) {
		String operatorId=baseRequestDTO.getData().get("ID");
		String sectId=baseRequestDTO.getData().get("sectId");
		serviceOperatorSectRepository.deleteByOperatorIdAndSectId(Long.valueOf(operatorId),sectId);
	    List<String> list=serviceOperatorSectRepository.findByOperatorId(Long.valueOf(operatorId));
	    if(list.size()==0 || StringUtil.isEmpty(sectId)){
	    	serviceOperatorRepository.deleteById(Long.valueOf(operatorId));
	    }
	}

	@Override
	public int checkTel(BaseRequestDTO<String> baseRequestDTO) {
		List<User> usesrList=userRepository.findByTel(baseRequestDTO.getData());
		if(usesrList.size()<=0){
			return 0;//未查询到用户
		}
		return 1;
	}

	@Override
	public List<String> showSect(String id) {
		return   serviceOperatorSectRepository.findByOperatorId(Long.valueOf(id));
	}

	@Override
	public List<User> getHexieUserInfo(String data) {
		return userRepository.findByTel(data);
	}

	/**
	 * 1.根据用户id查address表，找到bind字段为true的房子取出来
	 * 2.如果步骤1未查询到房屋，则查询community,将community返回的房屋打标记
	 */
	@Override
	public Address getDefaultAddress(User user) {
		
		Address defaultAddr = queryBindedHouse(user);
		if (defaultAddr==null) {
			log.info("未查询到默认绑定房屋的地址。will find house on communiy .");

			BaseResult<HouseListVO> baseResult = WuyeUtil.queryHouse(user, "");
			if (baseResult!=null) {
				HouseListVO houseVo = baseResult.getData();
				if (houseVo !=null ) {
					List<HexieHouse> houseList = houseVo.getHou_info();
					if (houseList!=null && !houseList.isEmpty()) {
						repairService.updateBindedAddress(user, houseList);
					}
				}
			}
			defaultAddr = queryBindedHouse(user);
		}
		
		return defaultAddr;
	}

	/**
	 * 查询绑定过的房屋，并去除其中
	 * @param user
	 * @return
	 */
	private Address queryBindedHouse(User user) {
		Address defaultAddr = null;
		List<Address> addrs = addressRepository.findAllByUserId(user.getId());
		
        if(addrs!=null && !addrs.isEmpty()){
            for(Address addr : addrs) {
                if(addr.isMain()) {
                	defaultAddr = addr;
                    break;
                }
            }
        }
		return defaultAddr;
	}
	
	/**
	 * 将已绑定的房屋的地址的bind字段设置成true
	 */
	@Override
	@Transactional
	public void updateBindedAddress(User user, List<HexieHouse> houseList) {
		
		for (HexieHouse house : houseList) {
			List<Address> addrList = addressRepository.getAddressByuserIdAndAddress(user.getId(), house.getCell_addr());
			if (addrList!=null && !addrList.isEmpty()) {
				for (Address address : addrList) {
					address.setBind(true);
					addressRepository.save(address);
				}
			}
		}
		User ruser = userRepository.getOne(user.getId());
		ruser.setTotalBind(houseList.size());
		userRepository.save(ruser);
		
	}


}
