
    
    var model = "repairSeed";
    var title = "维修通知管理";
    var modelName = "维修通知";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			 	  			{field:'time',title:'time',width:100},
 	  			{field:'repairOrderId',title:'repairOrderId',width:100},
 	  			{field:'operatorId',title:'operatorId',width:100},
 	  			{field:'orderDate',title:'orderDate',width:100},
 	  			{field:'projectId',title:'projectId',width:100},
 	  			{field:'operatorUserId',title:'operatorUserId',width:100},
 	  			{field:'projectName',title:'projectName',width:100},
 	  			{field:'repairType',title:'repairType',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
