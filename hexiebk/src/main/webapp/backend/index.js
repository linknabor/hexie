/**
$.fn.datebox.defaults.formatter = function(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	return y+'-'+m+'-'+d + ' 00:00:00';
}
**/
function isString(str){ 
	return (typeof str=='string')&&str.constructor==String; 
} 
var BASEURL = "http://test.e-shequ.com/wechat/hexiebk/";
var PAGEBASEURL = "http://test.e-shequ.com/";
(function($){
	//备份jquery的ajax方法
	var _ajax=$.ajax;
	
	//重写jquery的ajax方法
	$.ajax=function(opt){
		//备份opt中error和success方法
		var fn = {
			error:function(XMLHttpRequest, textStatus, errorThrown){},
			success:function(data, textStatus){}
		}
		if(opt.error){
			fn.error=opt.error;
		}
		if(opt.success){
			fn.success=opt.success;
		}
		
		//扩展增强处理
		var _opt = $.extend(opt,{
			error:function(XMLHttpRequest, textStatus, errorThrown){
				//错误方法增强处理
				fn.error(XMLHttpRequest, textStatus, errorThrown);
			},
			success:function(data, textStatus){
				if(isString(data) && data.indexOf('{')==0 && data.indexOf('success')>0) {
					data = $.parseJSON(data);
					//成功回调方法增强处理
					if(data.success==false&&data.errorCode==40001) {
						if(parent!=null) {
							parent.document.location.href=PAGEBASEURL+"/wechat/backend/login.html";
						} else {
							window.location.href=PAGEBASEURL+"/wechat/backend/login.html";
						}
					}
					if(data.success){
						fn.success(data, textStatus);
					} else {
						fn.error(data, textStatus);
					}
				} else {
					fn.success(data, textStatus);
				}

				
			}
		});
		_ajax(_opt);
	};
})(jQuery);
//构造图片地址
function buildHeadImg(value,row,index){
	if(value!=null){
        return "<img src='"+value+"'  width='100' height='100'/>";
	}
}
function rowDate(value,row,index){
	if(value!=null){
        return formatDate2(new Date(value));
	}
}

function formatDate2(now) {
	var year = now.getFullYear();
    var month = now.getMonth() + 1;
    var date = now.getDate();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds();
    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minute < 10) {
        minute = "0" + minute;
    }
    if (second < 10) {
        second = "0" + second;
    }
    return year + "-" + month + "-" + date + " " + hour + ":" + minute+":"+second;
}
function formatDate(d) {
    var now = new Date(d);
    var year = now.getYear();
    var month = now.getMonth() + 1;
    var date = now.getDate();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var second = now.getSeconds();
    if (month < 10) {
        month = "0" + month;
    }
    if (date < 10) {
        date = "0" + date;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minute < 10) {
        minute = "0" + minute;
    }
    if (second < 10) {
        second = "0" + second;
    }
    return month + "." + date + " " + hour + ":" + minute;
}
function init(){
	$('#myGrid').datagrid({
		title:title,
	    url:BASEURL+'/wechat/backend/'+model+'List.json',
	    columns:[field],
        pagination:true,
	    rownumbers:true,
	    fitColumns:true,
	    singleSelect:true,
	    page:'1',
	    pageSize:'20',
	    method:'POST'
	});
}

function initWithStyle(styler){
	$('#myGrid').datagrid({
		title:title,
	    url:BASEURL+'/wechat/backend/'+model+'List.json',
	    columns:[field],
        pagination:true,
	    rownumbers:true,
	    fitColumns:true,
	    singleSelect:true,
	    rowStyler:styler,
	    page:'1',
	    pageSize:'20',
	    method:'POST'
	});
}

function add(){
	$('#dlg').dialog('open').dialog('setTitle','添加'+modelName);
  	$('#my_form').form('clear');
  	$("input[name='id']").val("0");
  	$('#form_id').hide();
}
function save(){
    $('#my_form').form('submit',{
        url: BASEURL+"/wechat/backend/"+model+".json",

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
                $('#dlg').dialog('close');        // close the dialog
                $('#myGrid').datagrid('reload');    // reload the user data
            }
        }
    });
}
function edit(){
    var row = $('#myGrid').datagrid('getSelected');
    if (row){
        $('#dlg').dialog('open').dialog('setTitle','编辑用户');
        $('#form_id').show();
        $('#my_form').form('load',row);
    }
 }
function destroy(){
    var row = $('#myGrid').datagrid('getSelected');
    $.messager.confirm('提示','其它关联数据的模块将无法正常使用，确认删除该信息？',function(r){
    			if (r){
    				jQuery.ajax({
                        url: BASEURL+'/wechat/backend/'+model+'/delete/'+row.id,
                        type: "get",
                        success: function(msg) {
                             $('#myGrid').datagrid('deleteRow',$('#myGrid').datagrid('getRowIndex',row));
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
function query(){
	if($("#queryForm").form('validate')==false){
		return ;
	}
	var data = $("#queryForm").serializeObject();
	$('#myGrid').datagrid('load', data); 
}
function downloadExcel(){
	window.open(BASEURL+"/wechat/backend/"+model+"/excel");
}
/////////////////////////////////////////////////////////////
Date.prototype.Format = function(fmt) 
{ //author: meizz 
  var o = { 
    "M+" : this.getMonth()+1,                 //月份 
    "d+" : this.getDate(),                    //日 
    "h+" : this.getHours(),                   //小时 
    "m+" : this.getMinutes(),                 //分 
    "s+" : this.getSeconds(),                 //秒 
    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
    "S"  : this.getMilliseconds()             //毫秒 
  }; 
  if(/(y+)/.test(fmt)) 
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
  for(var k in o) 
    if(new RegExp("("+ k +")").test(fmt)) 
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
  return fmt; 
}
///////////////////////////////////////////////////////////////////////////////////
/**
 * @author rongnian.lu gfwu321@163.com qq:122329483
 * @requires jQuery,EasyUI
 * 
 * 
 */
try {
$.fn.serializeObject = function() { 
	var o = {}; 
	var a = this.serializeArray(); 
	$.each(a, function() { 
	if (o[this.name]) { 
	if (!o[this.name].push) { 
	o[this.name] = [ o[this.name] ]; 
	} 
	o[this.name].push(this.value || ''); 
	} else { 
	o[this.name] = this.value || ''; 
	} 
	}); 
	return o; 
};
} catch (e) {
}

////////////////////////////////////////////////

//////////////////////////////菜单/////////////////////////////
function convert(rows){
	function exists(rows, parentId){
		for(var i=0; i<rows.length; i++){
			if (rows[i].id == parentId) return true;
		}
		return false;
	}
	
	var nodes = [];
	// get the top level nodes
	for(var i=0; i<rows.length; i++){
		var row = rows[i];
		if (!exists(rows, row.parentId)){
			nodes.push({
				id:row.id,
				text:row.text,
				iconCls:row.iconCls,
				state:'closed'
			});
		}
	}
	
	var toDo = [];
	for(var i=0; i<nodes.length; i++){
		toDo.push(nodes[i]);
	}
	while(toDo.length){
		var node = toDo.shift();	// the parent node
		// get the children nodes
		for(var i=0; i<rows.length; i++){
			var row = rows[i];
			if (row.parentId == node.id){
				var child = {id:row.id,text:row.text,
						iconCls:row.iconCls,attributes:row.attributes};
				if (node.children){
					node.children.push(child);
				} else {
					node.children = [child];
				}
				toDo.push(child);
			}
		}
	}
	return nodes;
}

$(function(){
	$('#home_tree').tree({
		url: BASEURL+'/wechat/backend/backendMenu/all.json',
		loadFilter: function(rows){
			return convert(rows);
		}
	});
});

function getAllProduct(reg){
	reg.combobox({
	    url:BASEURL+'/wechat/backend/allproduct.json',
	    valueField:'id',
	    textField:'name'
	});
}
function getAllRegion(reg){
	reg.combobox({
	    url:BASEURL+'/wechat/backend/allregion.json',
	    valueField:'id',
	    textField:'name'
	});
}
function getAllMerchant(reg){
	reg.combobox({
	    url:BASEURL+'/wechat/backend/allmerchant.json',
	    valueField:'id',
	    textField:'name'
	});
}


///////////////////////////////菜单END//////////////////////////////

function logout(){
	window.location.href=BASEURL+"/wechat/backend/logout";
}