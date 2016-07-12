
    
    var model = "collocation";
    var title = "订单项管理";
    var modelName = "订单项";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'freeShipAmount',title:'freeShipAmount',width:100},
 	  			{field:'salePlanType',title:'salePlanType',width:100},
 	  			{field:'itemIds',title:'itemIds',width:100},
 	  			{field:'title',title:'title',width:100},
 	  			{field:'timeoutForPay',title:'timeoutForPay',width:100},
 	  			{field:'status',title:'status',width:100},
 	  			{field:'satisfyAmount',title:'satisfyAmount',width:100},
 	  			{field:'items',title:'items',width:100},
 	  			{field:'shipAmount',title:'shipAmount',width:100},
 	  			{field:'discountAmount',title:'discountAmount',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
