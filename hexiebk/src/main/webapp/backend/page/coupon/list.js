
    
    var model = "coupon";
    var title = "现金券记录管理";
    var modelName = "现金券记录";
    var field = [
  	  			{field:'id',title:'id',width:100},
 	  			{field:'seedId',title:'种子ID',width:100},
 	  			{field:'userId',title:'用户ID',width:100},
 	  			{field:'ruleId',title:'规则ID',width:100},
 	  			{field:'typeId',title:'现金券类型',width:100},
 	  			{field:'title',title:'现金券标题',width:100},
 	  			{field:'expiredDate',title:'超时日期',width:100,formatter:rowDate},
 	  			{field:'amount',title:'现金券金额',width:100},
 	  			{field:'status',title:'现金券状态',width:100},
 	  			{field:'userName',title:'用户名称',width:100},
 	  			{field:'usageCondition',title:'最小金额',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
