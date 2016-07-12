function addTab(title, url){
    if ($('#centerTab').tabs('exists', title)){
        $('#centerTab').tabs('select', title);
    } else {
        var content = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
        $('#centerTab').tabs('add',{
            title:title,
            content:content,
            closable:true
        });
    }
}

$(document).ready(function() {
    $('#home_tree').tree({
        onClick: function(node){
            addTab(node.text,node.attributes.url);
        }
    });
});
