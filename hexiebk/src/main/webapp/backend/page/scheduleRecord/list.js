
    
    var model = "scheduleRecord";
    var title = "定时任务记录管理";
    var modelName = "定时任务记录";
    var field = [
  	  			{field:'id',title:'id',width:100},
 	  			{field:'type',title:'任务类型',width:100},
 	  			{field:'bizIds',title:'变更业务ID',width:100},
 	  			{field:'errorBizIds',title:'异常业务ID',width:100},
 	  			{field:'errorCount',title:'错误数',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
 	  			{field:'finishDate',title:'结束时间',width:100,formatter:rowDate}
	    ];

    init();
    
    
    function schedule(bizType){
        $.messager.confirm('提示','确认执行定时任务？',function(val){
        			if (val){
        				jQuery.ajax({
                            url: BASEURL+'/wechat/backend/schedule/'+bizType,
                            type: "get",
                            success: function(msg) {
                            	$('#myGrid').datagrid('reload'); 
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                            	if(XMLHttpRequest.status == 401) {
                            	}
                                alert("定时任务请求失败");
                            },
                            complete: function(XMLHttpRequest, textStatus) {
                                this; // 调用本次AJAX请求时传递的options参数
                            }
                        });
        			}
        		});
      }
