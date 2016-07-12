
    
    var model ="logisticsItem"; 
    var title = "物流信息管理";
    var modelName ="物流信息";
    var field = [
    
 	  			{field:'id',title:'id',width:100}
 	  			,
				{field:'orderId',title:'订单id',width:100}
				
 	  			,
				{field:'logisticsType',title:'类型',width:100}
				
 	  			,
				{field:'logisticsNo',title:'编号',width:100}
				
 	  			,
				{field:'receiveTimeType',title:'接收时间类型',width:100}
				
				
	    ];

    init();
    
    function mysave()
    {
 	  			  $('#my_form #orderName').textbox('setValue',$('#my_form #orderId').combobox('getText'));
 	  			  $('#my_form #logisticsName').textbox('setValue',$('#my_form #logisticsId').combobox('getText'));
    	save();
    }
