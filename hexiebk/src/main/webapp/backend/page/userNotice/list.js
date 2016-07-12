

var model = "userNotice";
var title = "用户消息";
var modelName = "用户消息";
var field = [
  			{field:'id',title:'id',width:100},
			{field:'userId',title:'用户',width:100},
			{field:'noticeType',title:'消息类型',width:100,formatter: function(value,row,index){
                if(value==null||value=='0')
					return '订单消息';
					else if(value=='1')
					return '商品消息';
					else 
						return '后台通知';
				}
			},
			{field:'title',title:'标题',width:100},
			{field:'content',title:'业务内容',width:100},
			{field:'isLink',title:'是否为链接',width:100,formatter: function(value,row,index){
                if(value==null||value=='0')
					return '否';
					else 
						return '是';
				}
			},
			{field:'noticeDate',title:'消息日期',width:100},
			{field:'createDate',title:'创建日期',width:100}
    ];

init();
function mysave()
{
	$('#my_form #userName').textbox('setValue',$('#my_form #userId').combobox('getText'));
	save();
}