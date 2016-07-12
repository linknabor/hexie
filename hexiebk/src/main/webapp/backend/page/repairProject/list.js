
    
    var model = "repairProject";
    var title = "维修项目管理";
    var modelName = "维修项目";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			 	  			{field:'status',title:'status',width:100},
 	  			{field:'publicProject',title:'publicProject',width:100},
 	  			{field:'name',title:'name',width:100},
 	  			{field:'repairType',title:'repairType',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
