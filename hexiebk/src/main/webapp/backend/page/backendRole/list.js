
    
    var model = "backendRole";
    var title = "后台用户角色管理";
    var modelName = "后台用户角色";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
				{field:'name',title:'角色名',width:100},
				{field:'permissions',title:'权限',width:100}
	    ];

    init();
