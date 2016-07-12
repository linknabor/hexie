
    
    var model = "backendUser";
    var title = "后台用户管理";
    var modelName = "后台用户";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
				{field:'username',title:'用户名',width:100},
				{field:'password',title:'密码',width:100},
				{field:'role',title:'角色',width:100, formatter:roleName}
	    ];

    function roleName(item){
    	if(item){
        	return item.name;
    	}
    	return "";
    }
    init();
