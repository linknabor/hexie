
    
    var model = "rgroupUser";
    var title = "团购参与者管理";
    var modelName = "团购参与者";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'userId',title:'用户ID',width:100},
				{field:'ruleId',title:'团购ID',width:100},
				{field:'orderId',title:'订单ID',width:100},
				{field:'first',title:'是否团长',width:100},
				{field:'userName',title:'用户名',width:100},
				{field:'tel',title:'电话',width:100},
				{field:'count',title:'购买分数',width:100},
				{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
