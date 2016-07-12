
    
    var model = "couponSeed";
    var title = "现金券种子管理";
    var modelName = "现金券种子";
    var field = [
  	  			{field:'title',title:'现金券标题',width:300,formatter:titleFormatter},
 	  			{field:'seedType',title:'现金券类型',width:100},
 	  			{field:'status',title:'状态',width:100},
 	  			{field:'rate',title:'概率',width:80},
 	  			{field:'seedStr',title:'标识串',width:200},
  	  			{field:'id',title:'操作',width:200,formatter:showRulesFormatter},
 	  			{field:'totalCount',title:'现金券个数',width:100},
 	  			{field:'totalAmount',title:'现金券金额',width:100},
 	  			{field:'receivedAmount',title:'发放个数',width:100},
 	  			{field:'receivedCount',title:'发放金额',width:100},
 	  			{field:'usedAmount',title:'已用金额',width:100},
 	  			{field:'usedCount',title:'已用个数',width:100},
 	  			{field:'bizId',title:'种子ID',width:100},
 	  			{field:'description',title:'描述',width:100},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
 	  			{field:'startDate',title:'开始时间',width:100,formatter:rowDate},
 	  			{field:'endDate',title:'结束时间',width:100,formatter:rowDate}
	    ];

    
    
    
    init();
	$('#couponTypeGrid').datagrid({
		url:BASEURL+'/wechat/backend/couponTypeList.json',
	    columns:[[
	            {field:'id',title:'id',width:30},
 	  			{field:'title',title:'现金券名称',width:100},
 	  			{field:'amount',title:'现金券金额',width:100},
 	  			{field:'status',title:'模板状态',width:100,formatter:function(v){
 	  				if(v==0){
 	  					return '可用';
 	  				} else {
 	  					return '失效';
 	  				}
 	  			}},
 	  			{field:'usageCondition',title:'最小可用金额',width:100},
  	  			{field:'usedCount',title:'操作',width:200,formatter:couponTypeBtn}
	 	]],
	    collapsible:true,
        pagination:true,
	    rownumbers:true,
	    fitColumns:true,
	    singleSelect:true,
	    page:'1',
	    pageSize:'20',
	    method:'POST'
	});
    $('#rulesGrid').datagrid({
	    url:BASEURL+'/wechat/backend/couponRuleList.json',
	    columns:[[
	            {field:'id',title:'操作',width:200,formatter:couponRuleBtn},
 	  			{field:'title',title:'规则标题',width:100},
 	  			{field:'totalCount',title:'现金券个数',width:100},
 	  			{field:'totalAmount',title:'现金券总金额',width:100},
 	  			{field:'receivedAmount',title:'发放个数',width:100},
 	  			{field:'receivedCount',title:'发放金额',width:100},
 	  			{field:'usedAmount',title:'已用金额',width:100},
 	  			{field:'usedCount',title:'已用个数',width:100},
 	  			{field:'typeId',title:'现金券模板ID',width:100},
 	  			{field:'couponType',title:"现金券模板",formatter:couponTypeNameFormatter},
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
 	  			{field:'startDate',title:'开始时间',width:100,formatter:rowDate},
 	  			{field:'endDate',title:'结束时间',width:100,formatter:rowDate}
	              ]],
        pagination:true,
  	    rownumbers:true,
  	    fitColumns:true,
  	    singleSelect:true,
  	    page:'1',
  	    pageSize:'20',
  	    method:'POST'
	});
    function couponTypeNameFormatter(value,row,index) {
    	if(row.couponType!=null) {
    		return row.couponType.title;
    	} else {
    		return "xx";
    	}
    }
    
    
    
    
    
    function titleFormatter(value,row,index) {
    	if(row.status == 2) {
    		return "<span style=\"background:#ff4466\">"+value+"(已失效)</span>";
    	} else {
    		return value;
    	}
    }
    function showRulesFormatter(value,row,index) {
    	return "<a href=\"#\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-remove'\" onclick=\"showCouponRules("+index+")\">查看详细规则</a>";
    }

    function couponTypeBtn(value,row,index) {
    	return "<a href=\"#\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-remove'\" onclick=\"addRule("+index+")\">添加到现金券活动</a>";
    }

    function couponRuleBtn(value,row,index){
    	return "<a href=\"#\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-remove'\" onclick=\"removeRule("+index+")\">删除</a>" +
    			"<a href=\"#\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-remove'\" onclick=\"invalidRule("+index+")\">失效</a>";
    }
    
    function showCouponRules(index){
    	var raw = $('#myGrid').datagrid("getRows")[index];
    	$('#seed_title').text(raw.title);
    	$('#seed_id').text(raw.id);
    	$('#rulesGrid').datagrid("load",{couponSeedId:raw.id});
    	$('#rules_dialog').dialog('open');
    	$('#rules_dialog_content').layout("collapse","east");
    }
    
    function myedit(){
        var row = $('#myGrid').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','编辑');
            $('#form_id').show();

            var copy = jQuery.extend({}, row);
            copy.startDate=rowDate(copy.startDate);
            copy.endDate=rowDate(copy.endDate);
            $('#my_form').form('load',copy);
        }
     }
    
    function addRule(index) {
    	var rowType = $('#couponTypeGrid').datagrid('getRows')[index];
    	$('#ruleEditor').dialog('open').dialog('setTitle','添加规则');
      	$('#ruleEditor_form').form('clear');
      	$('#ruleEditor_form #rule_seedId').textbox('setValue',$('#seed_id').text());
      	$('#ruleEditor_form #rule_seedName').textbox('setValue',$('#seed_title').text());
      	$('#ruleEditor_form #rule_typeId').textbox('setValue',rowType.id);
      	$('#ruleEditor_form #rule_typeName').textbox('setValue',rowType.title);
    }
    function removeRule(idx) {
    	$.messager.confirm('提示','若该规则内现金券已被领取，则无法删除，只能将其失效！',function(r){
			if (r){
				var row = $('#rulesGrid').datagrid('getRows')[idx];
				jQuery.ajax({
                    url: BASEURL+'/wechat/backend/couponRule/delete/'+row.id,
                    type: "get",
                    success: function(msg) {
                         $('#rulesGrid').datagrid('deleteRow',idx);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                    	if(XMLHttpRequest.status == 401) {
                    		
                    	}
                        alert("删除失败");
                    },
                    complete: function(XMLHttpRequest, textStatus) {
                        this; // 调用本次AJAX请求时传递的options参数
                    }
                });
			}
		});
    }
    function invalidRule(idx) {
    	$.messager.confirm('提示','确认失效该现金券规则！',function(r){
			if (r){
				var row = $('#rulesGrid').datagrid('getRows')[idx];
				jQuery.ajax({
                    url: BASEURL+'/wechat/backend/couponRule/invalid/'+row.id,
                    type: "get",
                    success: function(msg) {
                    	row.status = 2;
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                    	if(XMLHttpRequest.status == 401) {
                    		
                    	}
                        alert("删除失败");
                    },
                    complete: function(XMLHttpRequest, textStatus) {
                        this; // 调用本次AJAX请求时传递的options参数
                    }
                });
			}
		});
    }
    
    function addRuleSubmit(){
    	$('#ruleEditor_form').form('submit',{
            url: BASEURL+"/wechat/backend/couponRule.json",

            onSubmit: function(){
               return $(this).form('validate');
            	//return validate();
            },
            success: function(result){
                var result = eval('('+result+')');
                if (!result.success){
                	var emsg = result.message;
                	if(emsg==null&&emsg=='') {
                		emsg = "处理失败！";
                	}
                    $.messager.show({
                        title: 'Error',
                        msg: emsg
                    });
                    
                } else {
                    $('#ruleEditor').dialog('close');        // close the dialog
                    $('#myGrid').datagrid('reload',result.result.seedId);    // reload the user data
                }
            }
        });
    }
    

//	$(document).ready(function(){
//		$('#bodyId').layout("collapse","south");
//	});