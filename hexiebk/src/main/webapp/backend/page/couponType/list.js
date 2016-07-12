
    
    var model = "couponType";
    var title = "现金券模板管理";
    var modelName = "现金券模板";
    var field = [
  	  			{field:'id',title:'id',width:100},
 	  			{field:'title',title:'现金券名称',width:100},
 	  			{field:'amount',title:'现金券金额',width:100},
 	  			{field:'status',title:'模板状态',width:100,formatter:function(v){
 	  				if(v==0){
 	  					return '可用';
 	  				} else {
 	  					return '失效';
 	  				}
 	  			}},
 	  			{field:'usageCondition',title:'最小可用金额',width:100},
 	  			{field:'availableForAll',title:'是否全局现金券',width:100},
 	  			{field:'productId',title:'指定商品ID',width:100},
 	  			{field:'productTag',title:'产品标签（暂时无效）',width:100},
  	  			{field:'onSaleType',title:'特卖商品类型',width:100},
  	  			{field:'receivedCount',title:'领取个数',width:100},
  	  			{field:'usedCount',title:'已用个数',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();
