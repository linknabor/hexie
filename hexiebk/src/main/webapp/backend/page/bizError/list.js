
    
    var model = "bizError";
    var title = "定时任务记录管理";
    var modelName = "定时任务记录";
    var field = [
  	  			{field:'id',title:'id',width:100},
 	  			{field:'bizType',title:'bizType',width:100},
 	  			{field:'bizId',title:'业务ID',width:100},
 	  			{field:'level',title:'错误登记',width:100},
 	  			{field:'message',title:'错误信息',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
