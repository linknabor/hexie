
    
    var model = "yuyueAreaItem";
    var title = "到家服务上架管理";
    var modelName = "到家服务上架";
    var field = [
                   {field:'id',title:'id',width:100},
                {field:'productName',title:'服务名称',width:100},
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
                {field:'regionId',title:'区域ID',width:100},
                {field:'ruleCloseTime',title:'下架时间',width:100,formatter:rowDate},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
                {field:'status',title:'状态',width:100,formatter: function(value,row,index){
	                     if(value=='0'){
	                         return '有效';
	                     }else if(value=='1'){
	                       return '失效';
	                     }else{
	                         return '未知';
	                     }
                     }
                },
                {field:'productType',title:'服务商家',width:100,formatter: function(value,row,index){
                        if(value=='0'){
                            return "阿姨来了";
                        } else if(value=='1'){
                            return "尚匠汽车";
                        }
                         return '未知';
                     }
                  },
        ];

    init();

    $('#my_form #regionType').combobox({
        onSelect: function (record) {
            if(record.value>0) {
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
                    url: BASEURL+'/wechat/backend/yuyueRule/productId/' + record.id,
                    valueField: 'id',
                    textField: 'name'
                }).combobox('clear');
            }
            
        }
    });

    window.onload=function(){
        getAllProduct($("#my_form #productId"));
    };