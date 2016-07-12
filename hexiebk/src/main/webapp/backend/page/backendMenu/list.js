
    
    var model = "backendMenu";
    var title = "菜单管理管理";
    var modelName = "菜单管理";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'text',title:'名字',width:100},
 	  			{field:'permissions',title:'权限',width:100},
 	  			{field:'url',title:'链接',width:100},
 	  			{field:'parentId',title:'父菜单',width:100},
 	  			{field:'iconCls',title:'标签',width:100},
 	  			{field:'sortNo',title:'排序',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
    
    
	$(function(){
		$('#tt').tree({
			url: BASEURL+'/wechat/backend/backendMenuList.json',
			loadFilter: function(rows){
				return convert(rows.rows);
			}
		});
	});
	function sync(){
		$('#tt').tree('reload');
	}

