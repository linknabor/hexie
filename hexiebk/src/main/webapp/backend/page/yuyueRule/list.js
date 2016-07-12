
    
    var model = "yuyueRule";
    var title = "到家服务规则管理";
    var modelName = "到家服务规则";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'productId',title:'商品ID',width:100},
				{field:'name',title:'名称',width:100},
				{field:'serviceNo',title:'需要服务次数',width:100},
 	  			{field:'productType',title:'服务商家',width:100,formatter: function(value,row,index){
	                    if(value=='0'){
	                    	return "阿姨来了";
	                    } else if(value=='1'){
	                    	return "尚匠汽车";
	                    } else if(value == '2'){
	                    	return "flowerPlus";
	                    }
	 					return '未知';
	 				}
 	  			},
 	  			{field:'limitNumOnce',title:'限购个数',width:100},
 	  			{field:'supportRegionType',title:'区域类型',width:100},
 	  			{field:'price',title:'价格',width:100},
 	  			{field:'startDate',title:'开始时间',width:100,formatter:rowDate},
 	  			{field:'endDate',title:'结束时间',width:100,formatter:rowDate},
 	  			{field:'timeoutForPay',title:'支付超时时间',width:100},
 	  			{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value=='0')
	 					return '无效';
	 					else   if(value=='1')
	     					return '有效';
	 					else
	 					return '未知';
	 				}
				},
// 	  			{field:'postageFee',title:'邮费',width:100},
// 	  			{field:'freeShippingNum',title:'包邮个数',width:100}
	    ];

    init();

    window.onload=function(){
        getAllProduct($("#my_form #productId"));
    };
