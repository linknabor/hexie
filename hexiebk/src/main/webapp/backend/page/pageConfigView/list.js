
    
    var model = "pageConfigView";
    var title = "页面配置管理";
    var modelName = "页面配置";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'tempKey',title:'模板key',width:100},
 	  			{field:'pageConfig',title:'配置',width:400},
 	  			{field:'available',title:'是否生效',width:100},
 	  			{field:'description',title:'描述',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
