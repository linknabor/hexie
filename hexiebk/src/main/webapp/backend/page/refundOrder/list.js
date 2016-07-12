
    
    var model ="refundOrder"; 
    var title = "退款管理";
    var modelName ="退款";
    var field = [
    
 	  			{field:'id',title:'id',width:100} ,
				{field:'orderId',title:'订单',width:100} ,
				{field:'userId',title:'用户',width:100} ,
				{field:'paymentId',title:'支付单ID',width:100} ,
				{field:'createDate',title:'退款申请时间',width:100,formatter:rowDate} ,
				{field:'refundFinishDate',title:'退款完成时间',width:100,formatter:rowDate} ,
				{field:'initiatorType',title:'发起方类型',width:100} ,
				{field:'refundStatus',title:'退款状态',width:100,formatter: function(value,row,index){
                    if(value==0){
 						return '退款开始';
                    } else if(value==2){
 						return '退款成功';
					} else if(value==3){
 						return '退款失败';
					} else if(value==1){
 						return '退款已申请';
					}
				}} ,
				{field:'totalFee',title:'总额',width:100} ,
				{field:'refundFee',title:'退款额',width:100} ,
				{field:'refundNo',title:'退款号',width:100} ,
				{field:'channelPaymentNo',title:'渠道交易号',width:100} ,
				{field:'channelRefundNo',title:'渠道退款号',width:100}
	    ];

    init();
    
    function sync(){
        var row = $('#myGrid').datagrid('getSelected');
        $.messager.confirm('提示','其它关联数据的模块将无法正常使用，确认删除该信息？',function(r){
        			if (r){
        				jQuery.ajax({
        					url: BASEURL+'/wechat/backend/refundOrder/async/'+row.paymentNo,
                            type: "get",
                            success: function(msg) {
                            	alert("状态已同步，需刷新页面查看最新状态");
                                 //$('#myGrid').datagrid('deleteRow',$('#myGrid').datagrid('getRowIndex',row));
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