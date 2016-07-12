
    
    var model = "homeService";
    var title = "到家服务配置";
    var modelName = "到家服务配置";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'isHandpick',title:'是否精选',width:100,formatter: function(value,row,index){
	                    if(value=='1'){
		 					return '是';                    	
	                    }else if(value=='0'){
		     				return '否';                    	
	                    }else{
		 					return '未知';	 						
		 				}
	 				}
				},
				{field:'status',title:'是否有效',width:100,formatter: function(value,row,index){
	                    if(value=='1'){
		 					return '有效';                    	
	                    }else if(value=='0'){
		     				return '无效';                    	
	                    }else{
		 					return '未知';	 						
		 				}
	 				}
				},				
 	  			{field:'serviceType',title:'商品类型',width:100,formatter: function(value,row,index){
	                    if(value=='0'){
	                    	return "洗车";
	                    } else if(value=='1'){
	                    	return "鲜花";
	                    } else if(value=='2'){
	                    	return "家政";
	                    } else if(value=='3'){
	                    	return "洗衣";
	                    } else if(value=='4'){
	                    	return "装修维修";
	                    } else if(value=='5'){
	                    	return "家电";
	                    } else if(value=='6'){
	                    	return "宠物";
	                    } else if(value=='7'){
	                    	return "厨师";
	                    } else if(value=='8'){
	                    	return "美容";
	                    } 
	 					return '未知';
	 				}
 	  			},
				{field:'price',title:'服务价格',width:100},
				{field:'picture',title:'服务封面图片',width:100},
				{field:'serviceName',title:'服务名称',width:100},
				{field:'serviceUrl',title:'服务连接',width:100},
				{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
    