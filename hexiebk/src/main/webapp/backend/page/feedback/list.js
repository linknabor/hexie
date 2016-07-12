
    
    var model = "feedback";
    var title = "用户反馈";
    var modelName = "用户反馈";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'currentEstateId',title:'当前房产',width:100},
				{field:'currentAddrId',title:'当前地址',width:100},
				{field:'name',title:'姓名',width:100},
				{field:'sex',title:'性别',width:100},
				{field:'tel',title:'手机号',width:100},
				{field:'openid',title:'openID',width:100},
				{field:'headimgurl',title:'头像',width:100,formatter:buildHeadImg}
	    ];

    init();
    