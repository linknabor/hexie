
    
	var nowTime = new Date().getTime();
    var model = "rgroupRule";
    var title = "团购管理";
    var modelName = "团购";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'productId',title:'商品ID',width:100},
				{field:'name',title:'名称',width:100},
 	  			{field:'limitNumOnce',title:'限购个数',width:100},
 	  			{field:'groupMaxNum',title:'成团个数',width:100},
 	  			{field:'currentNum',title:'已售个数',width:100},
 	  			
 	  			{field:'supportRegionType',title:'区域类型',width:100},
 	  			{field:'price',title:'特卖价格',width:100},
 	  			{field:'startDate',title:'开始时间',width:100,formatter:rowDate},
 	  			{field:'endDate',title:'结束时间',width:100,formatter:rowDate},
 	  			{field:'timeoutForPay',title:'支付超时时间',width:100},
 	  			{field:'status',title:'规则状态',width:100,formatter: function(value,row,index){
                    if(value=='0')
	 					return '无效';
	 					else   if(value=='1')
	     					return '有效';
	 					else
	 					return '未知';
	 				}
				},
				{field:'groupStatus',title:'团购状态',width:100,formatter: function(value,row,index){
                    if(value=='1')
	 					return '开始';
 					else   if(value=='2')
     					return '成功';
 					else  if(value=='3')
	 					return '已取消';
	 				}
				},
				{field:'ownerName',title:'团长名称',width:100},
				{field:'ownerAddr',title:'团长地址',width:100},
 	  			{field:'postageFee',title:'邮费',width:100},
 	  			{field:'freeShippingNum',title:'包邮个数',width:100}
	    ];

    initWithStyle(function(index,row){
		if (row.endDate<nowTime||row.status!=1){
			return 'background-color:pink;color:blue;font-weight:bold;';
		}
	});

    function myedit(){
    	var row = $('#myGrid').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','编辑商品');
            $('#form_id').show();
            var copy = jQuery.extend({}, row);
            copy.startDate=rowDate(copy.startDate);
            copy.endDate=rowDate(copy.endDate);
            $('#my_form').form('load',copy);
        }
    }
    
    window.onload=function(){
        getAllProduct($("#my_form #productId"));
    };