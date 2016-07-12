
    
    var model = "region";
    var title = "区域管理";
    var modelName = "区域";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'name',title:'名称',width:100},
				{field:'regionType',title:'支持区域类型',width:100,formatter: function(value,row,index){
                    if(value=='0')
 					return '省';
 					else   if(value=='1')
     					return '市';
 					else   if(value=='2')
     					return '区县';
 					else   if(value=='3')
     					return '乡镇';
 					else   if(value=='4')
     					return '全国';
 					else
 					return '未知';
 				}
				},
				{field:'imageUrl',title:'图片',width:100},
				{field:'description',title:'描述',width:100},
				{field:'parentName',title:'上一级名称',width:100}
	    ];

    init();
    
    $('#my_form #regionType').combobox({
    	onSelect: function (record) {
    		if(record.value>0) {
    			$('#my_form #parentId').combobox({
                    disabled: false,
                    url: BASEURL+'/wechat/backend/region/' + (record.value-1),
                    valueField: 'id',
                    textField: 'name'
                }).combobox('clear');
    		}
    		
        }
    });
