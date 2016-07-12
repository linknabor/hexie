
    
    var model = "message";
    var title = "消息管理";
    var modelName = "消息";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'title',title:'标题',width:100},
                  {field:'msgType',title:'消息类型',width:100,formatter: function(value,row,index){
                      if(value==null)
                      	return '未知';
                      else	if(value=='0')
   					   return '公告';
   					  else   if(value=='1')
       					return '资讯';
   					  else
   					    return '未知';
   				}
  				},
				{field:'contentType',title:'内容类型',width:100,formatter: function(value,row,index){
                    if(value==null)
                    	return '未知';
                    else	if(value=='0')
 						return '健康类';
 					else   if(value=='1')
     					return '文化娱乐类';
 					else   if(value=='2')
     					return '社区时事类';
 					else   if(value=='3')
     					return '教育类';
 					else   if(value=='4')
     					return '居家生活类';
 					else   if(value=='5')
     					return '母婴';
 					else   if(value=='6')
     					return '汽车';
 					else   if(value=='7')
     					return '其他';
 					else
 					return '未知';
 				}
				},
				{field:'title',title:'标题',width:100},
				{field:'from',title:'链接',width:100},
				{field:'regionType',title:'支持区域类型',width:100,formatter: function(value,row,index){
                    if(value=='0')
 					return '全国';
 					else   if(value=='1')
     					return '省';
 					else   if(value=='2')
     					return '市';
 					else   if(value=='3')
     					return '区县';
 					else   if(value=='4')
     					return '小区';
 					else
 					return '未知';
 				}
				},
				{field:'regionName',title:'区域',width:100},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value=='0')
 					return '有效';
 					else   if(value=='1')
     					return '无效';
 					else
 					return '未知';
 					}
				},
				{field:'first',title:'置顶',width:100,formatter: function(value,row,index){
                    if(value=='true')
 						return '是';
 					else
 						return '否';
 					}
				},
				{field:'publishDate',title:'发布时间',width:100},
				{field:'image',title:'图片',width:100}
	    ];

    init();
    $('#my_form #regionType').combobox({
    	onSelect: function (record) {
    		if(record.value<=5) {
    			$('#my_form #regionId').combobox({
                    disabled: false,
                    url: BASEURL+'/wechat/backend/region/' + record.value,
                    valueField: 'id',
                    textField: 'name'
                }).combobox('clear');
    		}
        }
    });
    
    
    function ajaxFileUpload(){
        if($('#my_form #contentFile').prop('files')[0]==null){
            alert('请选择需要上传的文件');
            return;
        }
        uploadModule.uploadFile($('#my_form #contentFile').prop('files')[0], function (url) {
            var html = '<p><img alt="" src="' + url + '"></p>',
                editorData = CKEDITOR.instances['content_content'].getData();
            CKEDITOR.instances['content_content'].setData(editorData + html);
        });
    }

    function mysave(){
    	$('#my_form #content').val(CKEDITOR.instances['content_content'].getData());
    	 save();
    }
    function myadd(){
    	$('#dlg').dialog('open').dialog('setTitle','添加'+modelName);
      	$('#my_form').form('clear');
      	var publishDate = formatDate2(new Date());
      	$("#my_form input[name='publishDate']").val(publishDate);
      	$("#my_form input[name='id']").val("0");
      	$('#form_id').hide();
    }

    function myedit(){
    	var row = $('#myGrid').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','编辑用户');
            $('#form_id').show();
            $('#my_form').form('load',row);
            CKEDITOR.instances['content_content'].setData(row.content);
        }
    }
    function ajaxFileUpload1(){
        if($('#my_form #mainPictureFile').prop('files')[0]==null){
            alert('请选择需要上传的文件');
            return;
        }
        uploadModule.uploadFile($('#my_form #mainPictureFile').prop('files')[0], function (url) {
            var html = '<p><img alt="" src="' + url + '"></p>';
            $('#my_form  #image').textbox('setValue',''+url);
            $('#my_form  #mainPictureImg').html(html);
        });
    }
    function ajaxFileUpload2(){
        if($('#my_form #smallPictureFile').prop('files')[0]==null){
            alert('请选择需要上传的文件');
            return;
        }
        uploadModule.uploadFile($('#my_form #smallPictureFile').prop('files')[0], function (url) {
            var html = '<p><img alt="" src="' + url + '"></p>';
            $('#my_form  #smallImage').textbox('setValue',''+url);
            $('#my_form  #smallPictureImg').html(html);
        });
    }
    
    $(document).ready(function(){
    	$('#my_form #content_content').ckeditor();//控件1
    });