
    
    var model = "yuyueOrder";
    var title = "预约管理";
    var modelName = "预约单";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'productName',title:'产品',width:100},
				{field:'userId',title:'用户ID',width:100},
				{field:'orderNo',title:'订单编号',width:100},
				{field:'address',title:'地址',width:100},
				{field:'tel',title:'电话',width:100},
				{field:'receiverName',title:'客户名',width:100},
				{field:'createDate',title:'预约时间',width:100,formatter:rowDate},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value==null || value=='1')
                    	return '预约成功';
 					else   if(value=='2')
     					return '预约失败';
 					else if(value=='3')
 						return '预约超时';
 					else if(value=='4')
 						return '服务完成';
 				}
				}
	    ];

    init();
    