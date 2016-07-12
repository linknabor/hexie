var uploadModule = {
    token: null,
    hasUploadToken: null,
    getUploadToken: function() {
        jQuery.ajax({
            url: BASEURL+'/wechat/api/qiniu/token.json',
            type: "get",
            success: function(msg) {
                 uploadModule.token = msg.result;
                uploadModule.hasUploadToken = true;
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.status);
                alert(XMLHttpRequest.readyState);
                alert(textStatus);
            },
            complete: function(XMLHttpRequest, textStatus) {
                this; // 调用本次AJAX请求时传递的options参数
            }
        });
    },

    uploadFile: function(file, callback, key) {
        if(!uploadModule.token)
        	{
        	uploadModule.getUploadToken();
        	if(!uploadModule.token)
        		return;
        	}
        var formData = new FormData();
        formData.append('token', uploadModule.token);
        formData.append('file', file);
        if (typeof key !== 'undefined') {
            formData.append('key', key);
        }
        //formData.token=uploadModule.token;
        //formData.file=file;
        //if (typeof key !== 'undefined') {
        //    formData.key=key;
        //}

        jQuery.ajax({
            url: 'http://upload.qiniu.com',
            type: 'post',
            data: formData,
            processData : false,
            /**
             *必须false才会自动加上正确的Content-Type
             */
            contentType : false,
            success: function(responseText) {
                callback('http://7xkdq7.com1.z0.glb.clouddn.com/' + responseText.key);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                //alert(XMLHttpRequest.status);
                //alert(XMLHttpRequest.readyState);
                alert('上传文章失败');
            }
        });
    },

    uploadFiles: function(files, callback) {
        var uploadedCount = 0,
            urlMap = {};

        files.forEach(function(file) {
            uploadModule.uploadFile(file, function(url) {
                urlMap[file.name] = url;
                ++uploadedCount === files.length && callback(urlMap);
            });
        });
    }
};
/*
uploadModule.getUploadToken();
*/


    jQuery.ajax({
        url: BASEURL+'/wechat/api/qiniu/token.json',
        type: "GET",
        success: function(msg) {
            uploadModule.token = msg.result;
            uploadModule.hasUploadToken = true;
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
        },
        complete: function(XMLHttpRequest, textStatus) {
            //this; // 调用本次AJAX请求时传递的options参数
        }
    });

