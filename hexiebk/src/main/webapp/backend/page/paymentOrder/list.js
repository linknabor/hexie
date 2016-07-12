
    
    var model = "paymentOrder";
    var title = "支付记录";
    var modelName = "支付记录";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'orderId',title:'订单ID',width:100},
				{field:'userId',title:'用户ID',width:100},
				{field:'merchantId',title:'商家ID',width:100},
				{field:'payAccount',title:'支付账号',width:100},
				{field:'paymentNo',title:'支付单号',width:100},
				{field:'channelPaymentId',title:'渠道流水单号',width:100},
				{field:'price',title:'支付金额',width:100},
				{field:'status',title:'支付状态',width:100,formatter: function(value,row,index){
                    if(value==1){
 						return '支付已提交';
                    } else if(value==2){
 						return '支付成功';
					} else if(value==3){
 						return '支付失败';
					} else if(value==4){
 						return '支付取消';
					} else if(value==5){
 						return '退款中';
					}
				}},
				{field:'createDate',title:'支付时间',width:100,formatter:rowDate},
	    ];

    init();
    function sync(){
        var row = $('#myGrid').datagrid('getSelected');
        $.messager.confirm('提示','其它关联数据的模块将无法正常使用，确认删除该信息？',function(r){
        			if (r){
        				jQuery.ajax({
                            url: BASEURL+'/wechat/backend/notifyPayed/'+row.orderId,
                            type: "get",
                            success: function(msg) {
                            	alert("状态已同步，需刷新页面查看最新状态");
                                 //$('#myGrid').datagrid('deleteRow',$('#myGrid').datagrid('getRowIndex',row));
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                alert("删除失败");
                            },
                            complete: function(XMLHttpRequest, textStatus) {
                                this; // 调用本次AJAX请求时传递的options参数
                            }
                        });
        			}
        		});
      }
    
	
	
    