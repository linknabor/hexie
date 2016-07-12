
    
    var model = "smsHis";
    var title = "短信记录管理";
    var modelName = "短信记录";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'phone',title:'手机号',width:100},
				{field:'messageType',title:'消息类型',width:100},
				{field:'code',title:'业务码',width:100},
				{field:'msg',title:'消息内容',width:100},
				{field:'userName',title:'用户',width:100},
				{field:'createDate',title:'创建时间',width:100}
	    ];

    init();
    function mysave()
    {
    	$('#my_form #userName').textbox('setValue',$('#my_form #userId').combobox('getText'));
    	save();
    }