function dateFormatter(value,row,index){
    if (row.createDate){
        return new Date(row.createDate);
    } else {
        return value;
    }
}
var dataCache = {};
function catFormatter(value,row,index){
    if(dataCache['key'+row.subCategoryId]){
        return dataCache['key'+row.subCategoryId];
    } else {
        return row.subCategoryId;
    }
}
function query(){
    $('#articleGrid').datagrid('load',{
            "articleId":$('#articleId').numberbox('getValue')==""?0:$('#articleId').numberbox('getValue'),
            "queryCategoryId":$('#queryCategoryId').combobox('getValue')==""?0:$('#queryCategoryId').numberbox('getValue'),
            "createFrom":$('#createFrom').datebox('getValue'),
            "createTo":$('#createTo').datebox('getValue')
        }
    );
}
function newContent(){
    $('#dlg').dialog('open').dialog('setTitle','添加文章');
    $('#content_form').form('clear');
}

function initContent(){
    var row = $('#articleGrid').datagrid('getSelected');
    $.messager.confirm('提示','其它关联该文章的模块将无法正常使用，确认删除这篇文章？',function(r){
        if (r){
            jQuery.ajax({
                url: BASEURL+'/wechat/backend/initHtmlWithDB/'+row.id+'/to/'+row.id,
                type: "get",
                success: function(msg) {
                    alert('文章初始化成功：'+row.id);
                    //$('#articleGrid').datagrid('deleteRow',$('#articleGrid').datagrid('getRowIndex',row));
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    alert('文章初始化失败');
                },
                complete: function(XMLHttpRequest, textStatus) {
                    this; // 调用本次AJAX请求时传递的options参数
                }
            });
        }
    });
}
function deleteContent(){
    var row = $('#articleGrid').datagrid('getSelected');
    $.messager.confirm('提示','其它关联该文章的模块将无法正常使用，确认删除这篇文章？',function(r){
        if (r){
            jQuery.ajax({
                url: BASEURL+'/wechat/backend/content/delete/'+row.id,
                type: "get",
                success: function(msg) {
                    $('#articleGrid').datagrid('deleteRow',$('#articleGrid').datagrid('getRowIndex',row));
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    //alert(XMLHttpRequest.status);
                    //alert(XMLHttpRequest.readyState);
                    // alert(textStatus);
                },
                complete: function(XMLHttpRequest, textStatus) {
                    this; // 调用本次AJAX请求时传递的options参数
                }
            });
        }
    });
}

function editContent(){
    var row = $('#articleGrid').datagrid('getSelected');
    if (row){
        $('#dlg').dialog('open').dialog('setTitle','Edit User');
        $('#content_form').form('load',row);
        CKEDITOR.instances['content_content'].setData(row.content);
        $('#categorySelect').combobox('setValue',row.subCategoryId);
    }
}

function saveContent(){
    $('#content').val(CKEDITOR.instances['content_content'].getData());
    $('#content_form').form('submit',{
        url: BASEURL+"/wechat/backend/content.json",

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
                $('#dlg').dialog('close');        // close the dialog
                $('#dg').datagrid('reload');    // reload the user data
            }
        }
    });
}

function initEditor() {
    $('#content_content').ckeditor();//控件1
}

function ajaxFileUpload(){
    if($('#file').prop('files')[0]==null){
        alert('请选择需要上传的文件');
        return;
    }
    uploadModule.uploadFile($('#file').prop('files')[0], function (url) {
        var html = '<p><img alt="" src="' + url + '"></p>',
            editorData = CKEDITOR.instances['content_content'].getData();
        CKEDITOR.instances['content_content'].setData(editorData + html);
    });
}
function ajaxFileUploadWithCover(){
    if($('#file').prop('files')[0]==null){
        alert('请选择需要上传的文件');
        return;
    }
    uploadModule.uploadFile($('#file').prop('files')[0], function (url) {
        var html = '<p><img alt="" src="' + url + '" width="100%"></p>',
            editorData = CKEDITOR.instances['content_content'].getData();
        CKEDITOR.instances['content_content'].setData(html+editorData);
        $('#coverUrl').val(url);
    });
}
function chooseCategory(rec){
    $("#categoryId").val(rec.pid);
    $("#subCategoryId").val(rec.id);
}

function loadCategory(){
    jQuery.ajax({
        url: BASEURL+'/wechat/backend/content/category/query.json',
        type: "POST",
        success: function(categories) {
            $('#categorySelect').combobox('loadData',categories);
            $('#queryCategoryId').combobox('loadData',categories);
            categories.forEach(function(v){
                dataCache['key'+ v.id]= v.name;
            });
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert('文章分类初始化失败');
        },
        complete: function(XMLHttpRequest, textStatus) {
            //this; // 调用本次AJAX请求时传递的options参数
        }
    });
}

$(document).ready(function(){
    initEditor();
    $('#categorySelect').combobox({onSelect:chooseCategory});
    loadCategory();
});
