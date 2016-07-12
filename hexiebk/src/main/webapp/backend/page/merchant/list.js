
    
    var model = "merchant";
    var title = "商户管理";
    var modelName = "商户";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'name',title:'商户名',width:100},
				{field:'merchantNo',title:'商户编号',width:100},
				{field:'address',title:'商家地址',width:100},
				{field:'description',title:'描述',width:100},
				{field:'platformFeeRate',title:'平台费率',width:100},
				{field:'enterDate',title:'入驻时间',width:100},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value==null || value=='0')
 					return '无效';
 					else   if(value=='1')
     					return '有效';
 					else
 					return '未知';
 				}
				}
	    ];

    init();
    