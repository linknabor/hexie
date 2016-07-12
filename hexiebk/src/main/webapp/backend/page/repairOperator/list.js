
    
    var model = "repairOperator";
    var title = "维修工管理";
    var modelName = "维修工";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			 	  			{field:'fromWuye',title:'fromWuye',width:100},
 	  			{field:'tel',title:'tel',width:100},
 	  			{field:'serviceLevel',title:'serviceLevel',width:100},
 	  			{field:'commentCount',title:'commentCount',width:100},
 	  			{field:'companyName',title:'companyName',width:100},
 	  			{field:'userId',title:'userId',width:100},
 	  			{field:'name',title:'name',width:100},
 	  			{field:'longitude',title:'longitude',width:100},
 	  			{field:'latitude',title:'latitude',width:100},
 	  			{field:'openId',title:'openId',width:100},
 	  			{field:'regionId',title:'regionId',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
