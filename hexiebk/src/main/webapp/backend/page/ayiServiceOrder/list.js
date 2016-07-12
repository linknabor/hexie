
    
    var model = "ayiServiceOrder";
    var title = "预约管理";
    var modelName = "预约单";
    var field = [
 	  			{field:'id',title:'id',width:100},
				{field:'orderId',title:'订单ID',width:100},
				{field:'strOrderCode',title:'OrderCode',width:100},
				{field:'strServiceTypeName',title:'TypeName',width:100},
				{field:'strServiceAddr',title:'地址',width:100},
				{field:'strSalaryRangeName',title:'Range',width:100},
				{field:'strOrderStatusName',title:'Status',width:100},
				{field:'createDate',title:'创建时间',width:100,formatter:rowDate},
				{field:'strWorkFrequency',title:'频率',width:100},
				{field:'strWorkDuration',title:'工作时长',width:100},
				{field:'dtPlanStartDate',title:'StartDate',width:100},
				{field:'strPlanStartTimeHour',title:'PlanStartTimeHour',width:100}
	    ];

    init();
    