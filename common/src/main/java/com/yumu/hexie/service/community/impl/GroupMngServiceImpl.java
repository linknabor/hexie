package com.yumu.hexie.service.community.impl;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yumu.hexie.common.util.DateUtil;
import com.yumu.hexie.common.util.ObjectToBeanUtils;
import com.yumu.hexie.integration.common.CommonResponse;
import com.yumu.hexie.integration.common.QueryListDTO;
import com.yumu.hexie.integration.community.req.OutSidProductDepotReq;
import com.yumu.hexie.integration.community.req.ProductDepotReq;
import com.yumu.hexie.integration.community.req.QueryGroupReq;
import com.yumu.hexie.integration.community.resp.GroupInfoVo;
import com.yumu.hexie.integration.community.resp.GroupOrderVo;
import com.yumu.hexie.integration.community.resp.GroupProductSumVo;
import com.yumu.hexie.integration.community.resp.GroupSumResp;
import com.yumu.hexie.integration.community.resp.OutSidDepotResp;
import com.yumu.hexie.integration.community.resp.OutSidRelateGroupResp;
import com.yumu.hexie.model.ModelConstant;
import com.yumu.hexie.model.commonsupport.info.Product;
import com.yumu.hexie.model.commonsupport.info.ProductDepot;
import com.yumu.hexie.model.commonsupport.info.ProductDepotRepository;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTags;
import com.yumu.hexie.model.commonsupport.info.ProductDepotTagsRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRepository;
import com.yumu.hexie.model.commonsupport.info.ProductRule;
import com.yumu.hexie.model.commonsupport.info.ProductRuleRepository;
import com.yumu.hexie.model.market.OrderItem;
import com.yumu.hexie.model.market.OrderItemRepository;
import com.yumu.hexie.model.market.RefundRecord;
import com.yumu.hexie.model.market.RefundRecordRepository;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.market.ServiceOrderRepository;
import com.yumu.hexie.model.market.saleplan.RgroupRule;
import com.yumu.hexie.model.market.saleplan.RgroupRuleRepository;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.user.UserRepository;
import com.yumu.hexie.service.community.GroupMngService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.user.UserNoticeService;
import com.yumu.hexie.vo.RgroupVO;
import com.yumu.hexie.vo.RgroupVO.Thumbnail;

/**
 * 描述:
 *
 * @author jackie
 * @create 2022-05-05 21:01
 */
@Service
public class GroupMngServiceImpl implements GroupMngService {

    @Autowired
    private RgroupRuleRepository rgroupRuleRepository;

    @Autowired
    private ProductRuleRepository productRuleRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNoticeService userNoticeService;

    @Autowired
    private ProductDepotRepository productDepotRepository;

    @Autowired
    private ProductDepotTagsRepository productDepotTagsRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private RefundRecordRepository refundRecordRepository;

    @Override
    public List<GroupInfoVo> queryGroupList(User user, QueryGroupReq queryGroupReq) {
        try {
            List<Sort.Order> sortList = new ArrayList<>();
            Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
            sortList.add(order);
            Sort sort = Sort.by(sortList);
            Pageable pageable = PageRequest.of(queryGroupReq.getCurrentPage(), 10, sort);

            Page<Object[]> page = rgroupRuleRepository.findRgroupList(user.getId(), queryGroupReq.getQueryName(), queryGroupReq.getGroupStatus(), pageable);
            List<GroupInfoVo> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupInfoVo.class);

            if (list == null) {
                list = new ArrayList<>();
            }

            for (GroupInfoVo info : list) {
                info.setGroupDate(DateUtil.getSendTime(info.getCreateDate().longValue()));
                //获取团购的图片
                List<RgroupVO.DescriptionMore> listDesc = JSONArray.parseArray(info.getDescriptionMore(), RgroupVO.DescriptionMore.class);
                //是否有图片
                List<String> textList = new ArrayList<>();
                List<String> imagesList = new ArrayList<>();
                if(listDesc != null) {
                    for(RgroupVO.DescriptionMore desc : listDesc) {
                    	if (!StringUtils.isEmpty(desc.getText())) {
                    		textList.add(desc.getText());
						}
                    	if ("2".equals(desc.getType())) {
							if (!StringUtils.isEmpty(desc.getImage())) {
								imagesList.add(desc.getImage());
							}
						}
                    	if ("1".equals(desc.getType())) {
                    		Thumbnail[] thumbs = desc.getThumbnail();
                    		if (thumbs != null && thumbs.length > 0) {
								for (Thumbnail thumb : thumbs) {
									if (thumb != null && !StringUtils.isEmpty(thumb.getUrl())) {
										imagesList.add(thumb.getUrl());
									}
								}
							}
						}
                    }
                }

                info.setDesc(textList.size()==0?"":textList.get(0));
                info.setProductImg(imagesList.size()==0?"":imagesList.get(0));
                info.setImages(imagesList);
                
                if (imagesList.size() == 0) {
                	List<Product> productList = productRepository.findMultiByRuleId(info.getId().longValue());
                	for (Product product : productList) {
                		String[]pics = product.getPictureList();
                		if (pics!=null && pics.length > 0) {
							for (String pic : pics) {
								if (!StringUtils.isEmpty(pic)) {
									imagesList.add(pic);
								}
								
							}
						}
					}
                	info.setImages(imagesList);
				}

                //统计支付的，退款的，取消的，预览的
                float realityAmt = 0;
                float refundAmt = 0;
                List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(info.getId().longValue());
                for (ServiceOrder serviceOrder : serviceOrderList) {
                    if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_PAYED
                            || serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_SENDED
                            || serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_RECEIVED
                            || serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_CONFIRM) {
                    	
                    		realityAmt += serviceOrder.getPrice();
                        
                    }
	                if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_REFUNDED) {
	                	Float refundedAmt = serviceOrder.getRefundAmt()==null?0F:serviceOrder.getRefundAmt();
                		refundAmt += refundedAmt;
	                }
                }

                info.setRealityAmt(realityAmt);
                info.setRefundAmt(refundAmt);

	            int totalOrdered = 0;	//被下单总次数
	  			String totalOrderedStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ORDERED + info.getId());
	  			if (!StringUtils.isEmpty(totalOrderedStr)) {
	  				totalOrdered = Integer.parseInt(totalOrderedStr);
	  			}
	  			
	  			int totalAccessed = 0;	//被下单总次数
	  			String totalAccessedStr = stringRedisTemplate.opsForValue().get(ModelConstant.KEY_RGROUP_GROUP_ACCESSED + info.getId());
	  			if (!StringUtils.isEmpty(totalAccessedStr)) {
	  				totalAccessed = Integer.parseInt(totalAccessedStr);
	  			}
	  			
	  			info.setFollowNum(totalOrdered);
	  			info.setCancelNum(totalOrdered - info.getCurrentNum());
	  			info.setQueryNum(totalAccessed);
	  			
            }
            return list;
        } catch (Exception e) {
            throw new BizValidateException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Boolean updateGroupInfo(User user, String groupId, String operType) {

        if (!"1".equals(operType) && !"3".equals(operType)) {
            throw new BizValidateException("不合法的操作数据类型");
        }
        if (StringUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购编号为空，请刷新重试");
        }

        RgroupRule rgroupRule = rgroupRuleRepository.findById(Long.parseLong(groupId));
        if (rgroupRule!=null) {
            if ("1".equals(operType)) { //结束操作
                //修改团购结束日期
                rgroupRule.setStatus(1);
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.SECOND, -1);
                date = calendar.getTime();
                rgroupRule.setEndDate(date);
            } else {
                //下架团购
                rgroupRule.setStatus(0);
            }
        } else {
            throw new BizValidateException("为查到团购信息，请刷新重试");
        }
        return true;
    }

    @Override
    public CommonResponse<Object> queryProductDepotListPage(OutSidProductDepotReq outSidProductDepotReq) {
        CommonResponse<Object> commonResponse = new CommonResponse<>();
        try {
            List<Sort.Order> orderList = new ArrayList<>();
            Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
            orderList.add(order);
            Sort sort = Sort.by(orderList);

            Pageable pageable = PageRequest.of(outSidProductDepotReq.getCurrentPage(), outSidProductDepotReq.getPageSize(), sort);
            Page<Object[]> page = productDepotRepository.getDepotListPage(outSidProductDepotReq.getProductName(), outSidProductDepotReq.getOwnerName(), pageable);
            List<OutSidDepotResp> list = ObjectToBeanUtils.objectToBean(page.getContent(), OutSidDepotResp.class);

            QueryListDTO<List<OutSidDepotResp>> responsePage = new QueryListDTO<>();
            responsePage.setTotalPages(page.getTotalPages());
            responsePage.setTotalSize(page.getTotalElements());
            responsePage.setContent(list);

            commonResponse.setData(responsePage);
            commonResponse.setResult("00");

        } catch (Exception e) {

            commonResponse.setErrMsg(e.getMessage());
            commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
        }
        return commonResponse;
    }

    @Override
    public CommonResponse<Object> queryRelateGroup(String depotId) {
        CommonResponse<Object> commonResponse = new CommonResponse<>();
        try {

            List<Object[]> rgroupRules = rgroupRuleRepository.queryGroupByDepotId(depotId);
            List<OutSidRelateGroupResp> list = ObjectToBeanUtils.objectToBean(rgroupRules, OutSidRelateGroupResp.class);

            for(OutSidRelateGroupResp resp : list) {
                if (resp.getStatus() == 1) {
                    Date date = new Date();
                    if (resp.getStartDate().getTime() <= date.getTime()
                            && resp.getEndDate().getTime() >= date.getTime()) { //跟团中
                        resp.setStatus_cn("正在跟团中");
                    } else if (resp.getEndDate().getTime() < date.getTime()) {
                        resp.setStatus_cn("已结束");
                    } else if (resp.getStartDate().getTime() > date.getTime()) {
                        resp.setStatus_cn("未开始");
                    }
                } else {
                    resp.setStatus_cn("预览中");
                }
            }
            QueryListDTO<List<OutSidRelateGroupResp>> responsePage = new QueryListDTO<>();
            responsePage.setContent(list);
            commonResponse.setData(responsePage);
            commonResponse.setResult("00");

        } catch (Exception e) {

            commonResponse.setErrMsg(e.getMessage());
            commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
        }
        return commonResponse;
    }

    @Override
    public String delDepotById(String depotId) {
        productDepotRepository.deleteById(Long.parseLong(depotId));
        return "SUCCESS";
    }

    @Override
    public CommonResponse<Object> queryGroupListPage(OutSidProductDepotReq outSidProductDepotReq) {
        CommonResponse<Object> commonResponse = new CommonResponse<>();
        try {
            List<Sort.Order> orderList = new ArrayList<>();
            Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
            orderList.add(order);
            Sort sort = Sort.by(orderList);

            Pageable pageable = PageRequest.of(outSidProductDepotReq.getCurrentPage(), outSidProductDepotReq.getPageSize(), sort);
            Page<Object[]> page = rgroupRuleRepository.queryGroupByOutSid(outSidProductDepotReq.getProductName(), outSidProductDepotReq.getOwnerName(), pageable);
            List<OutSidRelateGroupResp> list = ObjectToBeanUtils.objectToBean(page.getContent(), OutSidRelateGroupResp.class);

            for(OutSidRelateGroupResp resp : list) {
                if (resp.getStatus() == 1) {
                    Date date = new Date();
                    if (resp.getStartDate().getTime() <= date.getTime()
                            && resp.getEndDate().getTime() >= date.getTime()) { //跟团中
                        resp.setStatus_cn("正在跟团中");
                    } else if (resp.getEndDate().getTime() < date.getTime()) {
                        resp.setStatus_cn("已结束");
                    } else if (resp.getStartDate().getTime() > date.getTime()) {
                        resp.setStatus_cn("未开始");
                    }
                } else {
                    resp.setStatus_cn("预览中");
                }
            }
            QueryListDTO<List<OutSidRelateGroupResp>> responsePage = new QueryListDTO<>();
            responsePage.setTotalPages(page.getTotalPages());
            responsePage.setTotalSize(page.getTotalElements());
            responsePage.setContent(list);
            commonResponse.setData(responsePage);
            commonResponse.setResult("00");

        } catch (Exception e) {

            commonResponse.setErrMsg(e.getMessage());
            commonResponse.setResult("99");		//TODO 写一个公共handler统一做异常处理
        }
        return commonResponse;
    }

    @Override
    public String operGroupByOutSid(String groupId, String operType) {
        if (StringUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购编号为空，请刷新重试");
        }
        if (StringUtils.isEmpty(operType)) {
            throw new BizValidateException("操作类型为空，请刷新重试");
        }

        RgroupRule rgroupRule = rgroupRuleRepository.findById(Long.parseLong(groupId));
        if (rgroupRule!=null) {
            if ("0".equals(operType)) { //下架
                rgroupRule.setStatus(0);
            } else if("1".equals(operType)) {
                rgroupRuleRepository.delete(rgroupRule);
                List<ProductRule> productRules = productRuleRepository.findByRuleId(rgroupRule.getId());
                for(ProductRule rule : productRules) {
                    productRuleRepository.delete(rule);
                }
            } else {
                throw new BizValidateException("操作类型为空，请刷新重试");
            }
        } else {
            throw new BizValidateException("为查到团购信息，请刷新重试");
        }
        return "SUCCESS";
    }
    
    public static void main(String[] args) {
		
    	Float f0 = 0.2F;
    	Float f1 = 0.3F;
    	Float f = f0 + f1;
    	DecimalFormat decimalFormat=new DecimalFormat("#.##");
    	String totalAmtStr = decimalFormat.format(f);
    	System.out.println(totalAmtStr);
	}

    @Override
    public GroupSumResp queryGroupSum(User user, String groupId) throws Exception {
        if (StringUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购ID不能为空，请刷新重试");
        }

        //汇总当前团购的有效订单，总金额和退款金额
        GroupSumResp resp = new GroupSumResp();
        List<GroupSumResp.SearchVo> searchVoList = new ArrayList<>();

        List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(Long.parseLong(groupId));
        int validNum = serviceOrderList.size(); //有效订单数
        Float totalAmt = 0F; //总金额
        Float refundAmt = 0F; //退款金额
        for (ServiceOrder serviceOrder : serviceOrderList) {
            if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_CANCEL) {
                validNum = validNum - 1;
                refundAmt += serviceOrder.getPrice();
            }
            totalAmt += serviceOrder.getPrice();
            if (serviceOrder.getStatus() == ModelConstant.ORDER_STATUS_REFUNDED) {
            	Float refunded = serviceOrder.getRefundAmt()==null?0F:serviceOrder.getRefundAmt();
                refundAmt += refunded;
            }
        }
        //有效订单 全部订单-取消的订单
        GroupSumResp.SearchVo searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("有效订单");
        searchVo.setNum(String.valueOf(validNum));
        searchVo.setMessage("有效订单：全部订单-已取消订单");
        searchVoList.add(searchVo);

        //总金额 全部订单的金额，包括取消的订单
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        String totalAmtStr = decimalFormat.format(totalAmt);
        searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("订单总金额");
        searchVo.setNum("¥" + totalAmtStr);
        searchVo.setMessage("订单总金额：所有订单金额的加总（包含已取消订单）");
        searchVoList.add(searchVo);

        //退款金额 全部订单的退款金额
        String refundAmtStr = decimalFormat.format(refundAmt);
        searchVo = new GroupSumResp.SearchVo();
        searchVo.setName("退款金额");
        searchVo.setNum("¥" + refundAmtStr);
        searchVo.setMessage("退款金额：所有订单的退款金额（包含已取消订单）");
        searchVoList.add(searchVo);
        resp.setSearchVoList(searchVoList);

        //查询团下单的商品列表
        GroupSumResp.ProductVo productVo = new GroupSumResp.ProductVo();

        List<Integer> status = new ArrayList<>();
        status.add(ModelConstant.ORDER_STATUS_PAYED);
        status.add(ModelConstant.ORDER_STATUS_CANCEL);
        status.add(ModelConstant.ORDER_STATUS_SENDED);
        status.add(ModelConstant.ORDER_STATUS_RECEIVED);
        status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
        status.add(ModelConstant.ORDER_STATUS_CONFIRM);
        status.add(ModelConstant.ORDER_STATUS_REFUNDING);
        status.add(ModelConstant.ORDER_STATUS_REFUNDED);
        List<Object[]> groupProductSumVos = serviceOrderRepository.findProductSum(Long.parseLong(groupId), status);
        int totalNum = 0;
        int totalVerify = 0;
        List<GroupProductSumVo> vos = new ArrayList<>();
        if (groupProductSumVos != null && groupProductSumVos.size() > 0) {
        	vos = ObjectToBeanUtils.objectToBean(groupProductSumVos, GroupProductSumVo.class);
            for (GroupProductSumVo vo : vos) {
                totalNum += vo.getCount().intValue();
                totalVerify += vo.getVerifyNum().intValue();
            }
		}
        productVo.setProducts(vos);
        productVo.setTotalNum(totalNum);
        productVo.setVerifyNum(totalVerify);

        resp.setProductVo(productVo);
        return resp;
    }

    @Override
    public List<GroupOrderVo> queryGroupOrder(User user, QueryGroupReq queryGroupReq) throws Exception {
        List<Integer> status = new ArrayList<>();
        String verifyStatus = ""; //是否核销
        List<Integer> itemStatus = null;	//子项退款状态,默认0，未退款
        if ("0".equals(queryGroupReq.getOrderStatus())) { //查全部
            status.add(ModelConstant.ORDER_STATUS_PAYED);
            status.add(ModelConstant.ORDER_STATUS_CANCEL);
            status.add(ModelConstant.ORDER_STATUS_SENDED);
            status.add(ModelConstant.ORDER_STATUS_RECEIVED);
            status.add(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
            status.add(ModelConstant.ORDER_STATUS_CONFIRM);
            status.add(ModelConstant.ORDER_STATUS_REFUNDING);
            status.add(ModelConstant.ORDER_STATUS_REFUNDED);
            
        } else if ("1".equals(queryGroupReq.getOrderStatus())) { //查待核销的
            status.add(ModelConstant.ORDER_STATUS_PAYED);
            verifyStatus = "0";
            
            itemStatus = new ArrayList<>();
            itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_PAID);
        } else if ("2".equals(queryGroupReq.getOrderStatus())) { //查退款申请
//            status.add(ModelConstant.ORDER_STATUS_REFUNDING);
//            status.add(ModelConstant.ORDER_STATUS_REFUNDED);
        	
        	status.add(ModelConstant.ORDER_STATUS_PAYED);
        	status.add(ModelConstant.ORDER_STATUS_CONFIRM);
        	status.add(ModelConstant.ORDER_STATUS_SENDED);
            status.add(ModelConstant.ORDER_STATUS_RECEIVED);
            
            itemStatus = new ArrayList<>();
            itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_APPLYREFUND);            
            itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_REFUNDING);
            itemStatus.add(ModelConstant.ORDERITEM_REFUND_STATUS_REFUNDED);
        } else {
            throw new BizValidateException("不合法的参数类型");
        }

        List<Sort.Order> sortList = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
        sortList.add(order);
        Sort sort = Sort.by(sortList);
        Pageable pageable = PageRequest.of(queryGroupReq.getCurrentPage(), 10, sort);

        Page<Object[]> page = serviceOrderRepository.findByGroupRuleIdPage(Long.parseLong(queryGroupReq.getGroupId()), status, verifyStatus, 
        			itemStatus, pageable);
        List<GroupOrderVo> list = ObjectToBeanUtils.objectToBean(page.getContent(), GroupOrderVo.class);
        if (list != null) {
        	Sort refundSort = Sort.by(Direction.DESC, "id");
            for (GroupOrderVo vo : list) {
                vo.setStatusCn(ServiceOrder.getStatusStr(vo.getStatus()));
                
                String orderDate = "";
                if (vo.getPayDate() != null) {
                	orderDate = DateUtil.dttmFormat(vo.getPayDate());
				} else {
					orderDate = DateUtil.dtFormat(vo.getCreateDate().longValue(), DateUtil.dttmSimple);
				}
                vo.setOrderDate(orderDate);

                //0商户派送 1用户自提 2第三方配送
                String logisticType = "商户派送";
                if (vo.getLogisticType() == 1) {
                    logisticType = "用户自提";
                } else if (vo.getLogisticType() == 2) {
                    logisticType = "第三方配送";
                }
                vo.setLogisticTypeCn(logisticType);

                //获取用户昵称和头像
                User userInfo = userRepository.findById(vo.getUserId().longValue());
                if (userInfo != null) {
                    vo.setUserName(userInfo.getNickname());
                    vo.setUserHead(userInfo.getHeadimgurl());
                }

//                List<BuyGoodsVo> buyVos = new ArrayList<>();
                //查询订单下的商品
                ServiceOrder serviceOrder = new ServiceOrder();
                serviceOrder.setId(vo.getId().longValue());
                List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
                vo.setOrderItems(items);
                
                List<RefundRecord> refundRecords = refundRecordRepository.findByOrderId(serviceOrder.getId(), refundSort);
                vo.setRefundRecords(refundRecords);
                if (refundRecords!=null && refundRecords.size()>0) {
					vo.setLatestRefund(refundRecords.get(0));
				}
            }
        }

        return list;
    }

    @Override
    public GroupOrderVo queryGroupOrderDetail(User user, String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new BizValidateException("订单号为空，请刷新重试");
        }
        ServiceOrder serviceOrder = serviceOrderRepository.findById(Long.parseLong(orderId));
        if (serviceOrder == null) {
            throw new BizValidateException("未查到订单，请刷新重试");
        }
        GroupOrderVo groupOrder = new GroupOrderVo();
        BeanUtils.copyProperties(serviceOrder, groupOrder);
        groupOrder.setStatusCn(ServiceOrder.getStatusStr(groupOrder.getStatus()));
        groupOrder.setOrderDate(DateUtil.dttmFormat(serviceOrder.getPayDate()));
        groupOrder.setUserId(BigInteger.valueOf(serviceOrder.getUserId()));
        groupOrder.setId(BigInteger.valueOf(serviceOrder.getId()));

        //0商户派送 1用户自提 2第三方配送
        String logisticType = "商户派送";
        if (groupOrder.getLogisticType() == 1) {
            logisticType = "用户自提";
        } else if (groupOrder.getLogisticType() == 2) {
            logisticType = "第三方配送";
        }
        groupOrder.setLogisticTypeCn(logisticType);

        //获取用户昵称和头像
        User userInfo = userRepository.findById(serviceOrder.getUserId());
        if (userInfo != null) {
            groupOrder.setUserName(userInfo.getNickname());
            groupOrder.setUserHead(userInfo.getHeadimgurl());
        }

        //查询订单下的商品
        List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
        groupOrder.setOrderItems(items);
        
        //查询商品退款记录
        Sort sort = Sort.by(Direction.DESC, "id");
    	List<RefundRecord> records = refundRecordRepository.findByOrderId(Long.valueOf(orderId), sort);
    	if (records != null && records.size() > 0) {
    		groupOrder.setLatestRefund(records.get(0));
		}
        return groupOrder;
    }

    @Override
    @Transactional
    public Boolean handleVerifyCode(User user, String orderId, String code) {
        if (StringUtils.isEmpty(code)) {
            throw new BizValidateException("核销码为空，请重新输入");
        }

        ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(orderId), user.getId());
        if (serviceOrder == null) {
            throw new BizValidateException("未查到订单，请刷新重试");
        }
        List<OrderItem> items = orderItemRepository.findByServiceOrder(serviceOrder);
        boolean flag = false;
        for (OrderItem item : items) {
            if (item.getVerifyStatus() == 0 && item.getCode().equals(code)) {
                item.setVerifyStatus(1);
                orderItemRepository.save(item);
                //修改订单状态为已签收
                serviceOrder.setStatus(ModelConstant.ORDER_STATUS_RECEIVED);
                serviceOrderRepository.save(serviceOrder);
                flag = true;
            }
        }
        if (!flag) {
            throw new BizValidateException("核销码不正确或已经核销，请确认后重试");
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean cancelOrder(User user, String orderId) throws Exception {
        ServiceOrder serviceOrder = serviceOrderRepository.findByIdAndGroupLeaderId(Long.parseLong(orderId), user.getId());
        if (serviceOrder == null) {
            throw new BizValidateException("未查到订单，请刷新重试");
        }
        //取消订单并退款
//        reFunding(serviceOrder);
//        serviceOrder.setStatus(ModelConstant.ORDER_STATUS_CANCEL_MERCHANT);
//        serviceOrderRepository.save(serviceOrder);
        return true;
    }

    @Override
    public Boolean noticeReceiving(User user, String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new BizValidateException("团购编号未知，请刷新重试");
        }
        List<ServiceOrder> list = new ArrayList<>();
        //查询出未提货的订单，前提必须是已经成团
        RgroupRule rgroupRule = rgroupRuleRepository.findById(Long.parseLong(groupId));
        if (rgroupRule!=null) {
            if (rgroupRule.getGroupStatus() == ModelConstant.RGROUP_STAUS_FINISH) {
                List<ServiceOrder> serviceOrderList = serviceOrderRepository.findByGroupRuleId(Long.parseLong(groupId));
                for (ServiceOrder order : serviceOrderList) {
                    if (order.getStatus() == ModelConstant.ORDER_STATUS_PAYED) {
                        list.add(order);
                    }
                }
            }
        }

        if (list.size() == 0) {
            throw new BizValidateException("没有可推送的提货用户");
        }

        for (ServiceOrder order : list) {
            userNoticeService.groupArriaval(order);
        }
        return true;
    }

    @Override
    public List<ProductDepot> queryProductDepotList(User user, String searchValue, int currentPage) {
//        User userInfo = userRepository.findById(user.getId());
//        if(userInfo == null) {
//            throw new BizValidateException("用户不存在");
//        }
//        if(!"03".equals(user.getRoleId())) {
//            throw new BizValidateException("当前用户不是团长，无法操作");
//        }

        List<Sort.Order> sortList = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createDate");
        sortList.add(order);
        Sort sort = Sort.by(sortList);
        Pageable pageable = PageRequest.of(currentPage, 10, sort);

        return productDepotRepository.findByOwnerIdAndNameContaining(user.getId(), searchValue, pageable);
    }

    @Override
    public Boolean delProductDepot(User user, String productId) {
        if(StringUtils.isEmpty(productId)) {
            throw new BizValidateException("商品编号不能为空");
        }
//        User userInfo = userRepository.findById(user.getId());
//        if(userInfo == null) {
//            throw new BizValidateException("用户不存在");
//        }
//        if(!"03".equals(user.getRoleId())) {
//            throw new BizValidateException("当前用户不是团长，无法操作");
//        }
        productDepotRepository.deleteById(Long.parseLong(productId));
        return true;
    }

    @Override
    public ProductDepot operProductDepot(User user, ProductDepotReq productDepotReq) {
        ProductDepot depot = new ProductDepot();
        if(!StringUtils.isEmpty(productDepotReq.getProductId())) { //编辑
            depot = productDepotRepository.findById(Long.parseLong(productDepotReq.getProductId())).get();
        }

        BeanUtils.copyProperties(productDepotReq, depot);
        if(!StringUtils.isEmpty(productDepotReq.getPictures())) {
            String[] strs = productDepotReq.getPictures().split(",");
            depot.setMainPicture(strs[0]);
            depot.setSmallPicture(strs[0]);
        }

        if(StringUtils.isEmpty(productDepotReq.getTotalCount())) {
            depot.setTotalCount(9999999);
        }
        if(!StringUtils.isEmpty(productDepotReq.getTags())) {
            JSONArray jsonArray = new JSONArray();
            String[] strs = productDepotReq.getTags().split(",");
            for(String key : strs) {
                if(!StringUtils.isEmpty(key)) {
                    ProductDepotTags tag = productDepotTagsRepository.findById(Long.parseLong(key)).get();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", tag.getId());
                    jsonObject.put("name", tag.getName());
                    jsonObject.put("color", "#FF9333");
                    jsonArray.add(jsonObject);
                }
            }
            if(jsonArray.size() > 0) {
                depot.setTags(jsonArray.toJSONString());
            }
        }
        depot.setOwnerId(user.getId());
        productDepotRepository.save(depot);
        return depot;
    }

    @Override
    public ProductDepot queryProductDepotDetail(User user, String productId) {
        return productDepotRepository.findById(Long.parseLong(productId)).get();
    }

    @Override
    public Map<String, List<ProductDepotTags>> queryProductDepotTags(User user) {
        List<ProductDepotTags> ownerList = productDepotTagsRepository.findByOwnerId(user.getId());
        List<ProductDepotTags> pubList = productDepotTagsRepository.findByOwnerId(0);
        Map<String, List<ProductDepotTags>> map = new HashMap<>();
        map.put("owner", ownerList);
        map.put("public", pubList);
        return map;
    }

    @Override
    public Boolean saveDepotTag(User user, String tagName) {
        ProductDepotTags tags = new ProductDepotTags();
        tags.setName(tagName);
        tags.setOwnerId(user.getId());
        productDepotTagsRepository.save(tags);
        return true;
    }

    @Override
    public Boolean delDepotTag(User user, String tagId) {
        ProductDepotTags tag = productDepotTagsRepository.findById(Long.parseLong(tagId)).get();
        if(user.getId() != tag.getOwnerId()) {
            throw new BizValidateException("只能操作当前用户的标签");
        }
        productDepotTagsRepository.delete(tag);
        return true;
    }
    
    @Override
    @Transactional
    public List<ProductDepot> saveDepotFromSales(User user, String productIds) {
    	
    	Assert.hasText(productIds, "请选择要导入的商品");
    	
    	String[]productArr = productIds.split(",");
    	if (productArr == null) {
			throw new BizValidateException("请选择要导入的商品");
		}
    	List<Long> products = new ArrayList<>();
    	for (String productId : productArr) {
    		if (StringUtils.isEmpty(productId)) {
				continue;
			}
    		products.add(Long.valueOf(productId));
		}
    	List<Product> productList = productRepository.findAllById(products);
    	List<ProductDepot> depotList = new ArrayList<>();
    	for (Product product : productList) {
    		ProductDepot depot = new ProductDepot();
    		BeanUtils.copyProperties(product, depot, "id", "createDate", "startDate", "endDate");
    		int totalCount = product.getTotalCount();
    		if (totalCount == Integer.MAX_VALUE) {
    			depot.setTotalCount(9999999);
			}
    		depot.setAgentId(0l);
    		depot.setOwnerId(user.getId());
    		productDepotRepository.save(depot);
    		
    		product.setDepotId(depot.getId());
    		productRepository.save(product);
    		
    		depotList.add(depot);
		}
        return depotList;
    }
    
    /**
     * 获取退款申请记录
     * @param user
     * @param productIds
     * @return
     */
    @Override
    public List<RefundRecord> getRefundApply(User user, String oid) {
    	
    	Assert.hasText(oid, "请选择要退款的商品");
    	long orderId = Long.valueOf(oid);
    	Sort sort = Sort.by(Direction.DESC, "id");
    	List<RefundRecord> records = refundRecordRepository.findByOrderId(orderId, sort);
    	return records;
    	
    }
    
    
}
