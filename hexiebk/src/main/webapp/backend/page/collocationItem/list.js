
    
    var model = "collocationItem";
    var title = "订单项管理";
    var modelName = "订单项";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'salePlanType',title:'销售方案类型',width:100},
 	  			{field:'collocation',title:'优惠组合',width:100},
 	  			{field:'salePlanId',title:'销售方案ID',width:100},
 	  			{field:'price',title:'价格',width:100},
 	  			{field:'status',title:'状态',width:100},
 	  			{field:'ruleName',title:'规则名称',width:100},
 	  			{field:'productPic',title:'商品图',width:100},
 	  			{field:'productName',title:'商品名称',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
