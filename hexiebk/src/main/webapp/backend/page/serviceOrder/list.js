
    
    var model ="serviceOrder"; 
    var title = "订单管理";
    var modelName ="订单";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'orderNo',title:'订单号',width:100},
				{field:'productName',title:'商品',width:100},
				{field:'groupRuleId',title:'规则ID',width:100},
				{field:'orderType',title:'订单类型',width:100,formatter: function(value,row,index){
                    if(value==0||value==1){
 						return '拼单';
                    } else if(value==2){
 						return '预约单';
                    } else if(value==3){
                    	return '特卖';
                    } else if(value==4){
                    	return '团购';
                    } else if(value==5){
                    	return '本地服务';
					}  else {
 						return '未知';
					}
				}},
				
				{field:'groupId',title:'拼单Id',width:100},
				{field:'userId',title:'用户',width:100},
				{field:'count',title:'个数',width:100},
				{field:'price',title:'需要支付的金额',width:100},
				{field:'fromGroup',title:'是否是团购',width:100},
				{field:'address',title:'服务地址',width:100},
				{field:'memo',title:'备注',width:100},
				{field:'channelPaymentId',title:'微信支付流水号',width:100},
				{field:'tel',title:'手机号',width:100},
				{field:'receiverName',title:'接收名',width:100},
 				{field:'statusStr',title:'状态',width:100},
				{field:'logisticNo',title:'物流编号',width:100},
				{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
				{field:'closeTime',title:'超时时间',width:100,formatter:rowDate},
				{field:'asyncStatus',title:'同步商户',width:100,formatter: function(value,row,index){
                    if(value==0){
 						return '未同步';
                    } else if(value==1){
 						return '已同步';
					}  else {
 						return '未知';
					}
				}},{field:'pingjiaStatus',title:'评价',width:100,formatter: function(value,row,index){
                    if(value==0){
 						return '未评价';
                    } else if(value==1){
 						return '已评价';
					}  else {
 						return '未知';
					}
				}}
	    ];

    init();
    function refund(){
        var row = $('#myGrid').datagrid('getSelected');
        $.messager.confirm('提示','确认进行退款？？',function(r){
        			if (r){
        				jQuery.ajax({
                            url: BASEURL+'/wechat/backend/'+model+'/refund/'+row.id,
                            type: "get",
                            success: function(msg) {
                            	query();
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                alert("退款请求失败");
                            },
                            complete: function(XMLHttpRequest, textStatus) {
                                this; // 调用本次AJAX请求时传递的options参数
                            }
                        });
        			}
        		});
      }
    function async(){
        var row = $('#myGrid').datagrid('getSelected');
        $.messager.confirm('提示','确认配货完成？？？',function(r){
        			if (r){
        				jQuery.ajax({
                            url: BASEURL+'/wechat/backend/'+model+'/async/'+row.id,
                            type: "get",
                            success: function(msg) {
                            	query();
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                alert("退款请求失败");
                            },
                            complete: function(XMLHttpRequest, textStatus) {
                                this; // 调用本次AJAX请求时传递的options参数
                            }
                        });
        			}
        		});
      }
    function toSend(){
    	var row = $('#myGrid').datagrid('getSelected');
        if (row){
	    	$('#sendGood').dialog('open').dialog('setTitle','发货信息');
	      	$('#send_form').form('clear');
	      	$('#send_form #send_orderId').textbox('setValue',row.id);
	      	$('#send_form #send_orderNo').textbox('setValue',row.orderNo);
        }
    }
    function sendGood(){
    	$('#send_form').form('submit',{
            url: BASEURL+"/wechat/backend/"+model+"/sendGoods.json",

            onSubmit: function(){
               return $(this).form('validate');
            	//return validate();
            },
            success: function(result){
                var result = eval('('+result+')');
                if (result.errorMsg){
                    $.messager.show({
                        title: 'Error',
                        msg: result.errorMsg
                    });
                } else {
                    $('#sendGood').dialog('close');        // close the dialog
                    $('#myGrid').datagrid('reload');    // reload the user data
                }
            }
        });
    }
    
    
    function toReturn(){
    	var row = $('#myGrid').datagrid('getSelected');
        if (row){
	    	$('#returnGood').dialog('open').dialog('setTitle','发货信息');
	      	$('#return_form').form('clear');
	      	$('#return_form #return_orderId').textbox('setValue',row.id);
	      	$('#return_form #return_orderNo').textbox('setValue',row.orderNo);
        }
    }
    function returnGood(){
    	$('#return_form').form('submit',{
            url: BASEURL+"/wechat/backend/"+model+"/returnGoods",

            onSubmit: function(){
               return $(this).form('validate');
            },
            success: function(result){
                var result = eval('('+result+')');
                if (result.errorMsg){
                    $.messager.show({
                        title: 'Error',
                        msg: result.errorMsg
                    });
                } else {
                    $('#returnGood').dialog('close');        // close the dialog
                    $('#myGrid').datagrid('reload');    // reload the user data
                }
            }
        });
    }
    
    function query(){
    	if($("#queryForm").form('validate')==false){
    		return ;
    	}
    	if($("#queryForm #id").val()==''){
    		$("#queryForm #id").val(0);
    	}
    	var data = $("#queryForm").serializeObject();
    	$('#myGrid').datagrid('load', data); 
    }