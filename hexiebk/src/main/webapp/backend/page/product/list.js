
	var nowTime = new Date().getTime();
    
    var model = "product";
    var title = "拼单商品";
    var modelName = "拼单商品";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'merchantId',title:'所属商户',width:100},
				{field:'productNo',title:'商品编号',width:100},
				{field:'name',title:'名称',width:100},
				{field:'miniPrice',title:'基准价',width:100},
				{field:'oriPrice',title:'市场价',width:100},
				{field:'singlePrice',title:'单买价',width:100},
				{field:'totalCount',title:'总量',width:100},
				{field:'saledNum',title:'销量',width:100},
				{field:'status',title:'状态',width:100,formatter: function(value,row,index){
                    if(value=='0')
 						return '初始化';
 					else   if(value=='1')
     					return '上架';
 					else   if(value=='2')
     					return '下架';
 					else   if(value=='3')
     					return '删除';
 					else
 					return '未知';
 				}
				},
				{field:'startDate',title:'生效开始时间',width:100,formatter:rowDate},
				{field:'endDate',title:'生效结束时间',width:100,formatter:rowDate},
				{field:'platformFeeRate',title:'平台费率',width:100}
				/*,,
				{field:'serviceDesc',title:'商品描述',width:100}
				/*,
				{field:'otherDesc',title:'其他描述一',width:100},
				{field:'otherDesc1',title:'其他描述二',width:100},
				{field:'descUrl',title:'商品描述页（备用）',width:100}*/
	    ];

    initWithStyle(function(index,row){
		if (row.endDate<nowTime||row.status!=1){
			return 'background-color:pink;color:blue;font-weight:bold;';
		}
	});
    
    
    function myedit(){
    	var row = $('#myGrid').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','编辑商品');
            $('#form_id').show();
        }
    }
    function mysave(){
    	if(!$("input[name='saledNum']").val()){
    		$("input[name='saledNum']").val(0)
    	}
        save();
    }
    function myedit(){
        var row = $('#myGrid').datagrid('getSelected');
        if (row){
            $('#dlg').dialog('open').dialog('setTitle','编辑用户');
            $('#form_id').show();

            var copy = jQuery.extend({}, row);
            copy.startDate=rowDate(copy.startDate);
            copy.endDate=rowDate(copy.endDate);
            $('#my_form').form('load',copy);
            
            $('#my_form  #mainPictureImg').html("");
            $('#my_form  #smallPictureImg').html("");
            $('#my_form  #picturesImg').html("");
        }
     }
	
    function ajaxFileUpload1(){
        if($('#my_form #mainPictureFile').prop('files')[0]==null){
            alert('请选择需要上传的文件');
            return;
        }
        uploadModule.uploadFile($('#my_form #mainPictureFile').prop('files')[0], function (url) {
            var html = '<p><img alt="" src="' + url + '"></p>';
            $('#my_form  #mainPicture').textbox('setValue',''+url);
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
            $('#my_form #smallPicture').textbox('setValue',''+url);
            $('#my_form #smallPictureImg').html(html);
        });
    }
    
    function ajaxFileUpload3(){
        if($('#my_form #picturesFile').prop('files')[0]==null){
            alert('请选择需要上传的文件');
            return;
        }
        uploadModule.uploadFile($('#my_form #picturesFile').prop('files')[0], function (url) {
            var html = '<p><img alt="" src="' + url + '"></p>';
            var cur = $('#my_form #pictures').textbox("getValue");
            if(cur!=''){
            	cur =cur+",";
            }
            $('#my_form #pictures').textbox('setValue',cur+url);
            $('#my_form #picturesImg').html( $('#my_form #picturesImg').html()+html);
        });
    }
    
    getAllMerchant($('#my_form #merchantId'));