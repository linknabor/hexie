
    
    var model = "operation";
    var title = "操作记录管理";
    var modelName = "操作记录";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'bizType',title:'类型',width:100,formatter:function(value,row,index){
					 if(value=='1')
		 					return '订单';
		 			 else
		 					return '未知';
				}},
 	  			{field:'operate',title:'操作',width:100,formatter:function(value,row,index){
					 if(row.bizType=='1'){
						 if(value=='1'){
							 return '订单创建';
						 } else if(value=='2'){
							 return '订单请求支付';
						 } else if(value=='3'){
							 return '订单支付状态变更';
						 } else if(value=='4'){
							 return '订单取消';
						 } else if(value=='5'){
							 return '订单同步到商户';
						 } else if(value=='6'){
							 return '订单发货';
						 } else if(value=='7'){
							 return '订单签收';
						 } else if(value=='8'){
							 return '订单评论';
						 } else if(value=='9'){
							 return '订单退款请求';
						 } else if(value=='10'){
							 return '订单退货';
						 } else if(value=='11'){
							 return '订单退款成功';
						 }
				 				
					 }
					}},
 	  			{field:'bizId',title:'业务ID',width:100},
 	  			{field:'fromStatusStr',title:'初始状态',width:100},
 	  			{field:'endStatusStr',title:'结束状态',width:100},
 	  			{field:'opUserType',title:'用户类型',width:100,formatter:function(value,row,index){
					 if(value=='1')
		 				return '用户';
		 			 else if(value=='2')
		 				return '系统';
		 			 else if(value=='3')
		 				 return '管理员';
		 			 else if(value=='4')
		 				 return '微信';
				}},
 	  			{field:'opUserId',title:'用户ID',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
				
	    ];

    init();
