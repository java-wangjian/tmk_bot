function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	unReadCount()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})  
	$("#start").datepicker({
		dateFormat: 'yy-mm-dd', 
		maxDate: getNowTime().split(' ')[0],
		onSelect:function(dateText,inst){
       		getSingleData(dateText)
    	},
		
	}).val(getNowTime().split(' ')[0]);	
	$("#startF").datepicker({
    	onSelect:function(dateText,inst){
       		$("#endF").datepicker("option","minDate",dateText);
    	},
    	maxDate: getNowTime().split(' ')[0],
    	dateFormat: 'yy-mm-dd'
	}).val(getNeardays(29));
	$("#endF").datepicker({
    	onSelect:function(dateText,inst){
        	$("#startF").datepicker("option","maxDate",dateText);
    	},
    	maxDate: getNowTime().split(' ')[0],
    	dateFormat: 'yy-mm-dd'
	}).val(getNeardays(0));	
	getMultiDaysData(getNeardays(29),getNowTime().split(' ')[0])
    getSingleData(getNowTime().split(' ')[0])
}
function multiDaysData(){
	if($('#startF').val()==''||$('#endF').val()==''){
		layer.msg('请先选择完整日期')
		return;
	}
	if(daysBetween($('#startF').val(),$('#endF').val())>30){
		layer.msg('最多选择30天的数据')
		return;
	}
	getMultiDaysData($('#startF').val(),$('#endF').val())
}
function getMultiDaysData(sDate,eDate){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/statictics/custompic",
		data:{
			id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			startDate:sDate,
			endDate:eDate
		},
		success:function(data){
			if(JSON.parse(data).code!==0){
				layer.msg(JSON.parse(data).result)
				return;
			}
			var res = JSON.parse(data).data
			var dateList = Object.keys(res)
			
			var barDatas = []
			var callCountDatas = []
			var cusList = []
			
			$.each(res, function(index,value) {
//				barDatas.push(formatSeconds(value.callduration).split('秒')[0])
				barDatas.push(parseFloat(value.callduration/60).toFixed(1))
				callCountDatas.push(value.callCount)
				cusList.push(value)
			});			
			
			drawline00(dateList,cusList)			
			drawline01(dateList,callCountDatas)	
			drawBar(dateList,barDatas)
			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getSingleData(date){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/statictics/singledata",
		data:{
			id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			dateStr:date
		},
		success:function(data){
			var res = JSON.parse(data).data
//			if(JSON.parse(data).code==1){
//				layer.msg(JSON.parse(data).result)
				
//			}else{   
				var cus = []
				cus.push(res.customerA,res.customerB,res.customerC,res.customerD,res.customerE,res.customerF)
				drawPie(cus)
				$('#gt30').text(res.gt30)
				$('#lt10gt5').text(res.lt10gt5)
				$('#connCount').text(res.connCount)
				$('#refuseCount').text(res.customerF)
				$('#toStaffCount').text(res.toStaffCount)
				$('#callRate').text(Math.ceil(res.callCount==0? 0 :  res.connCount*100/res.callCount))
				$('#serRate').text(Math.ceil(res.callCount==0? 0 :  res.toStaffCount*100/res.callCount))
				var fm = res.customerA + res.customerB + res.customerC + res.customerD + res.customerE
				$('#aRate').text(Math.ceil(fm == 0 ? 0 :  res.customerA*100 / fm))
//			}			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function drawPie(datas){
	var option = {
		title: {
        	text: '已接通客户分类',     
        	x: '42%',
        	y: '90%'      
    	},
    	tooltip: {
        	trigger: 'item',
        	formatter: "{a} <br/>{b}: {c} ({d}%)"
   	 	},
    	grid: {left: '0%',right: '3%',bottom: '0%',containLabel: true},
    	legend: {
        	orient: 'vertical',
        	x: 'left',
        	data:[{
        		name:'A(有明确意向)',
        		textStyle:{}
        	},{
        		name:'B(可能有意向)',
        		textStyle:{}
        	},{
        		name:'C(没有拒绝)',
        		textStyle:{}
        	},{
        		name:'D(客户忙)',
        		textStyle:{}
        	},{
        		name:'E(明确拒绝)',
        		textStyle:{}
        	}]
    	},
    	series: [{  		
            name:'客户意向',
            type:'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {normal: { show: false, position: 'center'},emphasis: {show: true,textStyle: {fontSize: '20',fontWeight: 'bold'}}},
            labelLine: {normal: {show: false}},
            center: ['60%','45%'],
            data:[
                {value:datas[0], name:'A(有明确意向)'},
                {value:datas[1], name:'B(可能有意向)'},
                {value:datas[2], name:'C(没有拒绝)'},
                {value:datas[3], name:'D(客户忙)'},
                {value:datas[4], name:'E(明确拒绝)'},
//              {value:datas[5], name:'F(无效客户)'}
            ]
       }]
	};
	drawEcharts(option,'pie')
}
function drawEcharts(ops,eleid){   //eleid 是元素id
	var myChart = echarts.init(document.getElementById(eleid));
	myChart.setOption(ops);
}
function drawline00(dateList,dataList){
//	console.log(dataList)
	var series = []
	var alldata = {cus_A:[],cus_B:[],cus_C:[],cus_D:[],cus_E:[],cus_F:[]}
	$.each(dataList, function(index,value) {
		alldata.cus_A.push(value.A)
		alldata.cus_B.push(value.B)
		alldata.cus_C.push(value.C)
		alldata.cus_D.push(value.D)
		alldata.cus_E.push(value.E)
		alldata.cus_F.push(value.F)
	});
	$.each(alldata, function(ind,val) {
		var tp =  {
            name:ind.split('_')[1]+'类',
            type:'line',
            data:val,
            smooth:true,
        }
		series.push(tp)
	});
	var option = {
		 title: {
        	text: '意向客户数量',
        	textStyle: {
        		fontSize:"12"
        	}
    	},
    	tooltip: {
        	trigger: 'axis'
    	},
    	legend: {
    		x: "24%",
       	 	data:['A类','B类','C类','D类','E类','F类']
   	 	},
    	grid: {left: '2%',right: '8%',bottom: '0%',containLabel: true},
    	xAxis: {
        	type: 'category',
        	boundaryGap: false,
        	data: dateList
    	},
    	yAxis: {
        	type: 'value'
    	},
    	series: series
	};
	drawEcharts(option,'line00')
}
function drawline01(dateList,dataList){
	var option = {
		  title: {
        	text: '外呼数量',
        	textStyle: {
        		fontSize:"12"
        	}
    	},
    	tooltip: {
        	trigger: 'axis'
   	 	},
    	xAxis: {
        	type: 'category',
        	boundaryGap: false,
        	data: dateList
    	},
    	grid: {left: '2%',right: '8%',bottom: '0%',containLabel: true},
    	yAxis: {
        	type: 'value'
    	},
    	series:[{
            name:'呼出数量',
            type:'line',
            areaStyle: {normal:{}},
            data:dataList
        }]
	};
	drawEcharts(option,'line01')
}

function drawBar(dateList,datas){
//	console.log(datas)
	var option = {
		grid: {left: '2%',right: '0%',bottom: '0%',containLabel: true},
    	xAxis: {
        	data: dateList   //横坐标
    	},
    	yAxis: {type: 'value',name :'单位：分钟'},
    	itemStyle: {normal: {color: '#8196DC'}},  //柱状颜色
    	series: [{
        	data: datas,
        	type: 'bar',
        	label: {
                normal: {
                    show: true,
                    position: 'top'
                }
            },
    	}]
	};
	drawEcharts(option,'bar')
}