
	var nowTime = new Date().getTime();
    
    var model = "rgroupAreaItem";
    var title = "团购上架管理";
    var modelName = "团购上架";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'productName',title:'商品名称',width:100},
				{field:'ruleId',title:'规则Id',width:100},
				{field:'regionType',title:'支持区域类型',width:100,formatter: function(value,row,index){
                    if(value=='0')
 					return '全国';
 					else   if(value=='1')
     					return '省';
 					else   if(value=='2')
     					return '市';
 					else   if(value=='3')
     					return '区县';
 					else   if(value=='4')
     					return '小区';
 					else
 					return '未知';
 				}
				},
                {field:'ruleCloseTime',title:'下架时间',width:100,formatter:rowDate},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
				{field:'regionId',title:'区域ID',width:100},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value=='0'){
     					return '有效';
				}else   if(value=='1'){
         					return '失效';
     					}else{
         					return '未知';
     					}
     				}
				}
	    ];

    initWithStyle(function(index,row){
		if (row.ruleCloseTime<nowTime||row.status!=0){
			return 'background-color:pink;color:blue;font-weight:bold;';
		}
	});
    $('#my_form #regionType').combobox({
    	onSelect: function (record) {
    		if(record.value>=0) {
    			$('#my_form #regionId').combobox({
                    disabled: false,
                    url: BASEURL+'/wechat/backend/region/' + (record.value),
                    valueField: 'id',
                    textField: 'name'
                }).combobox('clear');
    		}
    		
        }
    });
    $('#my_form #productId').combobox({
    	onSelect: function (record) {
    		if(record.id>0) {
    			$('#my_form #ruleId').combobox({
                    disabled: false,
                    url: BASEURL+'/wechat/backend/rgroupRule/productId/' + record.id,
                    valueField: 'id',
                    textField: 'name'
                }).combobox('clear');
    		}
    		
        }
    });
    window.onload=function(){
        getAllProduct($("#my_form #productId"));
    };