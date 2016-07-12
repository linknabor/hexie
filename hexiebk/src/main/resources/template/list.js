
    
    var model = "#model#";
    var title = "#name#管理";
    var modelName = "#name#";
    var field = [
 	  			{field:'id',title:'id',width:100},
 	  			#fields#
 	  			{field:'createDate',title:'创建时间',width:100,formatter:rowDate}
	    ];

    init();