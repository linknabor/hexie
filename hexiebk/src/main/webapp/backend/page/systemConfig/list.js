
    
    var model = "systemConfig";
    var title = "系统配置管理";
    var modelName = "系统配置";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			 	  			{field:'sysKey',title:'sysKey',width:100},
 	  			{field:'sysValue',title:'sysValue',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
