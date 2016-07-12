
    
	var nowTime = new Date().getTime();
    var model = "groupBuyRule";
    var title = "拼单规则";
    var modelName = "拼单规则";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'productId',title:'商品ID',width:100},
				{field:'name',title:'规则名称',width:100},
				{field:'ruleNum',title:'规则个数',width:100},
				{field:'limitNumOnce',title:'每人每次限购个数',width:100},
				{field:'limitOwner',title:'每人发起限制',width:100},
				{field:'limitConcurrent',title:'并发数限制',width:100},
				{field:'price',title:'拼单价格',width:100},
				{field:'groupType',title:'拼单类型',width:100,formatter: function(value,row,index){
                    if(value=='0')
 					return '全城拼';
 					else   if(value=='1')
     					return '邻里拼';
 					else   if(value=='2')
     					return '特殊邻里拼';     					
 					else
 					return '未知';
 				}
				},
				{field:'limitAreaLength',title:'限制范围',width:100},
				{field:'description',title:'描述',width:100},
				{field:'startDate',title:'开始日期',width:100,formatter:rowDate},
				{field:'endDate',title:'结束日期',width:100,formatter:rowDate},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                       if(value=='0')
    					return '无效';
    					else   if(value=='1')
        					return '有效';
    					else
    					return '未知';
    				}
				}
	    ];

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
    initWithStyle(function(index,row){
		if (row.endDate<nowTime||row.status!=1){
			return 'background-color:pink;color:blue;font-weight:bold;';
		}
	});
    function mysave()
    {
    	save();
    }
    
    window.onload=function(){
        getAllProduct($("#my_form #productId"));
    };