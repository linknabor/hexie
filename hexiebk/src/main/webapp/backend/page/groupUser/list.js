
    
    var model = "groupUser";
    var title = "拼单用户管理";
    var modelName = "拼单用户";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'userId',title:'用户ID',width:100},
				{field:'groupId',title:'拼单ID',width:100},
				{field:'createDate',title:'拼单时间',width:100,formatter:rowDate},
				{field:'first',title:'发起人',width:100,formatter: function(value,row,index){
                    if(value==true)
 						return '是';
 					else
 						return '否';
 					}
				},
				{field:'headUrl',title:'用户头像',width:100,formatter:buildHeadImg},
				{field:'userName',title:'用户名称',width:100},
				{field:'tel',title:'手机号',width:100},
				{field:'count',title:'份数',width:100},
				{field:'payed',title:'支付状态',width:100,formatter: function(value,row,index){
                    if(value==true)
 						return '已支付';
 					else
 						return '未支付';
 					}
				},
				{field:'removed',title:'参团状态',width:100,formatter: function(value,row,index){
                    if(value==true)
 						return '已退团';
 					else
 						return '未退团';
 					}
				}
	    ];

    init();
