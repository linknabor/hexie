
    
    var model = "address";
    var title = "地址管理";
    var modelName = "地址";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'city',title:'城市',width:100},
				{field:'xiaoquName',title:'所属地区',width:100},				
				{field:'userName',title:'用户',width:100},
				{field:'detailAddress',title:'详细地址',width:100},
				{field:'receiveName',title:'收货人姓名',width:100},
				{field:'tel',title:'电话',width:100},
				{field:'main',title:'是否为默认地址',width:100,formatter: function(value,row,index){
                    if(value=='true')
 					return '是';
 					
 					else
 					return '否';
 					}
				}
	    ];

    init();
    function mysave()
    {
    	$('#my_form #userName').textbox('setValue',$('#my_form #userId').combobox('getText'));
    	$('#my_form #xiaoquName').textbox('setValue',$('#my_form #xiaoquId').combobox('getText'));
    	save();
    }