
    
    var model = "orderItem";
    var title = "订单项管理";
    var modelName = "订单项";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			 	  			{field:'count',title:'count',width:100},
 	  			{field:'orderType',title:'orderType',width:100},
 	  			{field:'productPic',title:'productPic',width:100},
 	  			{field:'productThumbPic',title:'productThumbPic',width:100},
 	  			{field:'productId',title:'productId',width:100},
 	  			{field:'amount',title:'amount',width:100},
 	  			{field:'price',title:'price',width:100},
 	  			{field:'serviceOrder',title:'serviceOrder',width:100},
 	  			{field:'ruleName',title:'ruleName',width:100},
 	  			{field:'userId',title:'userId',width:100},
 	  			{field:'ruleId',title:'ruleId',width:100},
 	  			{field:'merchantId',title:'merchantId',width:100},
 	  			{field:'productName',title:'productName',width:100},

 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
