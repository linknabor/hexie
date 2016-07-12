
    
    var model = "groupBuyGroup";
    var title = "拼单组管理";
    var modelName = "拼单";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			{field:'raiId',title:'拼单类型ID',width:100},
 	  			{field:'ruleId',title:'拼单规则ID',width:100}, 	  			
				{field:'ownerName',title:'发起人',width:100},
				{field:'ownerCityName',title:'发起人城市',width:100},
				{field:'ownerXiaoquName',title:'发起人区域',width:100},
				{field:'groupNo',title:'拼单号',width:100},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value=='1')
 						return '进行中';
 					else   if(value=='2')
     					return '拼单成功';
 					else	if(value=='3')
 						return '拼单失败'
 					else
 						return '未知';
 				}
				},
				{field:'createDate',title:'发起时间',width:100,formatter:rowDate},
				{field:'closeTime',title:'截止时间',width:100,formatter:rowDate},
				{field:'ruleNum',title:'需要人数',width:100},
				{field:'usedNum',title:'参与人数',width:100}
	    ];

    init();
    
    function refresh()
    {
    	var row = $('#myGrid').datagrid('getSelected');
        $.messager.confirm('提示','其它关联数据的模块将无法正常使用，确认删除该信息？',function(r){
        			if (r){
        				jQuery.ajax({
                            url: BASEURL+'/wechat/backend/'+model+'/refresh/'+row.id,
                            type: "get",
                            success: function(msg) {
                            	alert("刷新成功");
                                 //$('#myGrid').datagrid('deleteRow',$('#myGrid').datagrid('getRowIndex',row));
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                            	alert("刷新失败");
                            },
                            complete: function(XMLHttpRequest, textStatus) {
                                this; // 调用本次AJAX请求时传递的options参数
                            }
                        });
        			}
        		});
    }
    