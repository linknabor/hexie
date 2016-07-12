
    
    var model ="serviceAreaItem"; 
    var title = "拼单区域管理";
    var modelName ="拼单区域";
    var field = [
    
 	  			{field:'id',title:'id',width:100}
 	  			,
				{field:'regionId',title:'区域',width:100}
				
 	  			,
				{field:'regionType',title:'区域类型',width:100}
				
 	  			,
				{field:'productType',title:'产品类型',width:100}
				
 	  			,
				{field:'regionids',title:'完整区域号',width:100}
				
 	  			,
				{field:'regionnames',title:'完整区域名',width:100}
				
 	  			,
				{field:'productId',title:'商品',width:100}
				
 	  			,
 	  			{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value==null||value=='0')
 					return '失效';
 					else   if(value=='1')
     					return '可用';
 					else
 					return '失效';
 				}
				}
 	  			,
				{field:'hot',title:'热门服务',width:100}
				
 	  			,
				{field:'sort',title:'显示排序',width:100}
				
 	  			,
				{field:'createDate',title:'创建时间',width:100}
				
				
	    ];

    init();
    
    function mysave()
    {
 	  			  $('#my_form #regionName').textbox('setValue',$('#my_form #regionId').combobox('getText'));
 	  			  $('#my_form #productName').textbox('setValue',$('#my_form #productId').combobox('getText'));
    	save();
    }

    window.onload=function(){
        getAllRegion($('#my_form #regionId'));
        getAllProduct($("#my_form #productId"));
    };