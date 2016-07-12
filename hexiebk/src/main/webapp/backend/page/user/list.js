
    
    var model = "user";
    var title = "用户管理";
    var modelName = "用户";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'currentEstateName',title:'当前房产',width:100},
				{field:'currentAddrName',title:'当前地址',width:100},
				{field:'name',title:'姓名',width:100},
				{field:'sex',title:'性别',width:100,formatter: function(value,row,index){
                    if(value==null || value=='0')
 					return '女';
 					else   if(value=='1')
     					return '男';
 					else
 					return '男';
 				}
				},
				{field:'tel',title:'手机号',width:100},
				{field:'openid',title:'openID',width:100},
				{field:'headimgurl',title:'头像',width:100,formatter:buildHeadImg}
	    ];

    init();
    function mysave()
    {
    	$('#my_form #xiaoquId').textbox('setValue','1');//临时
    	$('#my_form #currentEstateName').textbox('setValue',$('#my_form #currentEstateId').combobox('getText'));
    	$('#my_form #currentAddrName').textbox('setValue',$('#my_form #currentAddrId').combobox('getText'));
        
    	save();
    }
    
    function syncwechat(){
    	$.messager.confirm('提示','确定从微信同步用户信息，该操作将会消耗一定的时间，请勿频繁操作！',function(r){
			if (r){
				jQuery.ajax({
                    url: BASEURL+'/wechat/backend/'+model+'/syncwechat',
                    type: "get",
                    success: function(msg) {
                    	alert("信息同步完成");
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        alert("同步失败");
                    },
                    complete: function(XMLHttpRequest, textStatus) {
                        this; // 调用本次AJAX请求时传递的options参数
                    }
                });
			}
		});
    }