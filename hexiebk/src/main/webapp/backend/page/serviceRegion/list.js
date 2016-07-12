
    
    var model = "serviceRegion";
    var title = "维修工设置管理";
    var modelName = "维修工设置";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'operatorType',title:'服务商类型',width:100},
 	  			{field:'xiaoquName',title:'小区名字',width:100},
 	  			{field:'operatorName',title:'操作员名字',width:100},
 	  			{field:'operatorId',title:'操作员ID',width:100},
 	  			{field:'xiaoquId',title:'小区ID',width:100},
 	  			{field:'priority',title:'优先级',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
