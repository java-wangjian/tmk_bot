var curPage = 1;
var isSearch = false;
var allProject = [];
var allGateway = '';
var loadings;
//var failGatewayNumList = []
var alreadyAdd = true;
function loadFuncOrStyle(){    //初始化样式或函数
	$("#start").datepicker({
    	onSelect:function(dateText,inst){
       		$("#end").datepicker("option","minDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	}).val(getNeardays(2));
	$("#end").datepicker({
    	onSelect:function(dateText,inst){
        	$("#start").datepicker("option","maxDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	}).val(getNeardays(0)); 
	isLogin()
	getAllPortList()
	unReadCount()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
	getAccList(1)
	getAllPro()  //获取用户所有项目
}
function getAllPortList(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateway/getUserGatewayAndPortList",
		data:{
			userId:JSON.parse(sessionStorage.getItem('loginMsg')).id
		},
		success:function(data){
			allGateway = JSON.stringify(JSON.parse(data).normalGatewayList)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getAllPro(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/findProjectNameByUserId",
		data:{
			userId:JSON.parse(sessionStorage.getItem('loginMsg')).id
		},
		success:function(data){
			allProject = JSON.parse(data)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function togSwitch(that){
	var tar = $(that)
	var sta ;
	var sUrl = tar.attr('src').split('images/')[1]
	if(sUrl=='open.png'){
		tar.attr('src','../images/close.png')
		sta = 0
	}else{
		tar.attr('src','../images/open.png')
		sta = 1
	}
	var chStatus = {
		id: tar.attr('userid'),
		operate: sta
	}      
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/editstaffswitch",
		data:chStatus,
		success:function(data){
			console.log(data)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function allCheckAcc(e){
	$(e.target).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	var status = $(e.target).next()
	if(status.text()=='全选'){
		$('.accMgrMainLi i').removeClass('icon-xuanze').addClass('icon-choosehandle')
		status.text('取消')
	}else{
		$('.accMgrMainLi i').removeClass('icon-choosehandle').addClass('icon-xuanze')		
		status.text('全选')
	}
}
function checkThisBtn(that){
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}

function showSelectKF(){
	$('#seleKfUl').css('display','block')
}
function getKFname(that){
	$(that).parent().prev().text($(that).text())
	$(that).parent().css('display','none')
}
function getAccList(page,search){
	var obj = {
		'userId':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'curPage':page,
		'planStatus':2,
		'endTimeStr': $('#end').val() == '' ? $('#end').val() : $('#end').val()+' 23:59:59',           
		'startTimeStr': $('#start').val() == '' ? $('#start').val() : $('#start').val()+' 00:00:00'       
	}
	if(search=='search'){
		isSearch = true
	}
	if(isSearch){
		obj.searchText = $('#searchTxt').val()
	}else{
		obj.searchText = ''
	}
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/findNowPlanList",
		data:obj,
		success:function(data){
			layer.close(loadings)
			var res = JSON.parse(data)
			var tempStr = ''	
			if(res.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#accMgrMainUl').html(nomess)
				return;
			}
			$.each(res.list, function(index,value) {				
				tempStr += "<li class='accMgrMainLi accMgrItem'>"
				
//				tempStr += "<a class='msgCap' href='./currPlan.html?planStatus=2&&planid="+value.planId+"&&planName="+encodeURI(value.planName)+"&&project="+encodeURI(value.projectName)+"'></a>"
				
//				tempStr += "<span class='flex4'><i serid='"+value.id+"' onclick='checkThisBtn(this)' class='iconfont icon-xuanze'></i></span>"		
				tempStr += "<span class='flex3'>"+(index+1)+"</span>"
				tempStr += "<span class='flex4'>"+value.planName+"</span>"
				tempStr += "<span class='flex6'>"+value.projectName+"</span>"
				var color = value.planStatus == 1 ? "color1" : value.planStatus == 2 ? "color2" : value.planStatus == 3 ? "color3" : "color0"
				tempStr += "<span class='flex3 "+ color +"'>"+curPlanStatus(value.planStatus)+"</span>"
				tempStr += "<span class='flex4'>"+value.customerCount+"</span>"	
//				tempStr += "<span class='flex3'>"+value.noThoughCount+"</span>"


				tempStr += "<span class='flex4 timeJust'><span>"+value.endTime.split(' ')[0]+"</span><br / ><span>"+value.endTime.split(' ')[1]+"</span></span>"
			
				tempStr += "<span class='flex12 new00'><a href='./currPlan.html?planStatus=2&&planid="+value.planId+"&&planName="+encodeURI(value.planName)+"&&project="+encodeURI(value.projectName)+"'><span class='child00'>通话详情</span></a><span data='"+JSON.stringify(value)+"' onclick='planMsgs(this)' class='child01'>任务详情</span><span planid='"+value.planId+"' onclick='getTable(this)' class='child00'>任务统计</span></span>"		
				
				tempStr += "<span class='flex6 controlSpan'><span class='child02' onclick='getCustomerList("+JSON.stringify(value)+",0)' ><i class='iconfont icon-zhongxinshiyang'></i>重做任务</span>"
				tempStr += "<span class='child03' onclick='getNoCallCustomerList("+JSON.stringify(value)+",1)' ><i class='iconfont icon-dianhua'></i>拨打未接通</span></span>"
				tempStr += "</li>"
			});
            $('#accMgrMainUl').html(tempStr)
             $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.total/10),
				totalSize: res.total,
				callback: function(current) {
					curPage = current
					getAccList(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
} 
function getTable(that){	  //统计弹窗
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/statisticalPlan",
		data:{
			userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
		    planId:$(that).attr('planid'),
		},
		success:function(data){
			var res = JSON.parse(data)
			var tpEle  = "<table class='tables' border='1px' cellspacing='0' cellpadding='0'>"
	    		tpEle += "<tr class='grayBg'><th>项目名称</th><th>指标</th><th>数量</th><th>百分比</th></tr>"
	    		tpEle += "<tr><td>任务</td><td>总量</td><td>"+res.total.count+"</td><td>"+(parseFloat(res.total.percentage*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td rowspan='2'>执行任务</td><td>已拨打</td><td>"+res.calledCount.count+"</td><td>"+(parseFloat(res.calledCount.percentage*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>未拨打</td><td>"+res.noCalledCount.count+"</td><td>"+(parseFloat(res.noCalledCount.percentage*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td rowspan='2'>接通情况</td><td>已接通</td><td>"+res.passCount.count+"</td><td>"+(parseFloat(res.passCount.percentage*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>未接通</td><td>"+res.noPassCount.count+"</td><td>"+(parseFloat(res.noPassCount.percentage*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td rowspan='2'>转接情况</td><td>转接人工</td><td>"+res.transferCount.count+"</td><td>"+(parseFloat(res.transferCount.percentage*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>已发短信</td><td>暂无</td><td>暂无</td></tr>"
	    		tpEle += "<tr><td rowspan='6'>意向分类</td><td>A&nbsp;(有明确意向)</td><td>"+res.gradeAndGradeCount[0].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[0].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>B&nbsp;(可能有意向)</td><td>"+res.gradeAndGradeCount[1].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[1].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>C&nbsp;&nbsp;&nbsp;&nbsp;(没有拒绝)</td><td>"+res.gradeAndGradeCount[2].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[2].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>D&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(客户忙)</td><td>"+res.gradeAndGradeCount[3].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[3].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>E&nbsp;&nbsp;&nbsp;&nbsp;(明确拒绝)</td><td>"+res.gradeAndGradeCount[4].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[4].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>F&nbsp;&nbsp;&nbsp;&nbsp;(无效客户)</td><td>"+res.gradeAndGradeCount[5].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[5].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "</table>"
	   	    	    
			layer.open({
  				type: 1,
  				title: "任务统计",
  				closeBtn: 1,
  				shadeClose: false,
  				scrollbar: false,
  				move: false,
   				area: ['9.5rem', '8.76rem'],
  				content: tpEle
			});
			
		},
		error:function(data){
			console.log(data)
		}
	});		
}
function getNoCallCustomerList(msg,type){ //0编辑 1拨打未接通
	if(allProject.length == 0){
		layer.msg('未获取到模板，请稍后再试！')
		return;
	}
	var dts = {
		planId: msg.planId,
		userId: JSON.parse(sessionStorage.getItem('loginMsg')).id
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/statisticalPlan",
		data:dts,
		success:function(data){
			var res = JSON.parse(data)
			if(res.noPassCount.count == 0){
				layer.msg('没有未接通的用户！')
				return;
			}
			editCurPlan([],msg,false,type)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getCustomerList(msg,type){
	if(allProject.length == 0){
		layer.msg('未获取到模板，请稍后再试！')
		return;
	}
//	var dts = {
//		planId: msg.planId,
//		userId: JSON.parse(sessionStorage.getItem('loginMsg')).id
//	}
//	$.ajax({
//		type:"post",
//		url:commonUrl+"/tmk-bot/plan/findCustomerIdByPlanId",
//		data:dts,
//		success:function(data){
//			var res = JSON.parse(data)
			editCurPlan([],msg,true,type)
//			editCurPlan(res.customerIdList,msg,true)     //编辑的弹窗
//		},
//		error:function(data){
//			console.log(data)
//		}
//	});
}
function showBusUl(e){
	e = e || window.event
	e.stopPropagation();
	$("#businessUl").css('display','block')
}
function getThisName(that){
	$("#businessUl").css('display','none')
	$("#busName").text($(that).text())
	$("#busName").attr('proid',$(that).attr('proid'))
}
function getThisGateName(that){
	if($(that).attr('gatewayType') == "2"){
		$(".isMessage button:eq(1)").removeClass("whiteBg").addClass("blueBg")
		$(".isMessage button:eq(0)").removeClass("blueBg").addClass("whiteBg")
		$(".isTransfer button:eq(1)").removeClass("whiteBg").addClass("blueBg")
		$(".isTransfer button:eq(0)").removeClass("blueBg").addClass("whiteBg")
	}
	$("#allGateWay").css('display','none')
	$("#gateName").text($(that).text())
	$("#gateName").attr('gatewayId',$(that).attr('gatewayId'))
	$("#gateName").attr('gatewayType',$(that).attr('gatewayType'))
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateway/findGatewayInfo",
		data:{
			userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
			gatewayId: $(that).attr('gatewayId'),
			type: $(that).attr('gatewayType')
		},
		success:function(data){
			layer.close(loadings)
			var res = JSON.parse(data)
			if(res.portList == '1'){
				layer.msg('当前网关信息异常，请选择其他网关或者联系代理商！')
				return ; 
			}
			var callStr ;
			if(res.callCount){ //选择sip线路
				callStr = "<span class='portItem'><i reg='REGISTER_OK' type='"+res.callCount+"' class='iconfont icon-choosehandle'></i>"+res.callCount+"</span>"
				$('#portBox').html(callStr)
			}else{  // 选择网关
				callStr = "<span class='portItem'><span>全选</span><i onclick='allCheckPort(this)' class='iconfont icon-xuanze'></i></span>"
				
				$.each(res.portList,function(index,val){
//					if(val.type==1){
						var isGray = val.reg == 'REGISTER_OK' ? '' : 'grays'
						callStr += "<span class='portItem "+isGray+"'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//					}else{
//						transferStr += "<span class='portItem'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//					}
				})
				$('#portBox').html(callStr)
			}
			
//			$('#portBox_sec').html(transferStr)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function showFirstGate(){
	var first = JSON.parse(allGateway)[0]
	$("#gateName").text(first.gateway)
	$("#gateName").attr('gatewayId',first.gatewayId)
	$("#gateName").attr('gatewayType',first.gatewayType)
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateway/findGatewayInfo",
		data:{
			userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
			gatewayId: first.gatewayId,
			type: first.gatewayType
		},
		success:function(data){
			layer.close(loadings)
			var res = JSON.parse(data)
			if(res.portList == '1'){
				layer.msg('当前网关信息异常，请选择其他网关或者联系代理商！')
				return ; 
			}
			var callStr ;
			if(res.callCount){ //选择sip线路
				callStr = "<span class='portItem'><i reg='REGISTER_OK' type='"+res.callCount+"' class='iconfont icon-choosehandle'></i>"+res.callCount+"</span>"
				$('#portBox').html(callStr)
			}else{  // 选择网关
				callStr = "<span class='portItem'><span>全选</span><i onclick='allCheckPort(this)' class='iconfont icon-xuanze'></i></span>"
				
				$.each(res.portList,function(index,val){
//					if(val.type==1){
						var isGray = val.reg == 'REGISTER_OK' ? '' : 'grays'
						callStr += "<span class='portItem "+isGray+"'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//					}else{
//						transferStr += "<span class='portItem'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//					}
				})
				$('#portBox').html(callStr)
			}
//			$('#portBox').html(callStr)
//			$('#portBox_sec').html(transferStr)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function allCheckPort(that){
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	if($(that).prev().text()=='全选'){
		$(that).parent().siblings('.portItem').children('.iconfont[reg=REGISTER_OK]').removeClass('icon-xuanze').addClass('icon-choosehandle')
		$(that).prev().text('取消')
	}else{
		$(that).parent().siblings('.portItem').children('.iconfont[reg=REGISTER_OK]').removeClass('icon-choosehandle').addClass('icon-xuanze')
		$(that).prev().text('全选')
	}
}
function showAllGateWay(e){
	e = e || window.event
	e.stopPropagation();
	$(".businessUl").css('display','none')
	$("#allGateWay").css('display','block')
}


function editCurPlan(customerIdList,planMsg,isEdit,type){   //添加计划弹窗   
	console.log(planMsg)
	var allproStr = ''
	$.each(allProject, function(index,val) {
		allproStr += "<li proid='"+val.id+"' onclick='getThisName(this)'>"+val.projectName+"</li>"
	});
	
	var gateway = ''
	$.each(JSON.parse(allGateway), function(index,val) {
		gateway += "<li gatewayType='"+val.gatewayType+"' gatewayId='"+val.gatewayId+"' onclick='getThisGateName(this)'>"+val.gateway+"</li>"
	});
	
	var tpEle  = "<div class='callLogBox'>"
	
		tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>任务名称:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='businessBox'>"
		
		if(isEdit){
			tpEle += "<input type='text' class='planName' id='planName' value='' placeholder='请填写任务名称' />"
		}else{
			tpEle += "<input type='text' class='planName' id='planName' maxlength='20' value='"+planMsg.planName+"' placeholder='请填写计划名称' />"
		}		
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"	
		tpEle += "<div class='addPlansItem'>"
		tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>话术模板:</strong>"
		tpEle += "<div class='addPlansContent'>"
		tpEle += "<div class='businessBox'>"
		
		if(isEdit){				    		    		    	
//	    	tpEle += "<p class='rela'><span proid='"+planMsg.projectId+"' class='busName' id='busName' onclick='showBusUl()'>"+planMsg.projectName+"</span> <i class='iconfont icon-xiala xialas'></i></p>"
			tpEle += "<p class='rela'><span proid='' class='busName' id='busName' onclick='showBusUl(event)'>请选择模板</span> <i class='iconfont icon-xiala xialas'></i></p>"
	    	tpEle += "<ul id='businessUl' class='businessUl'>"
	    	tpEle += allproStr
	    	tpEle += "</ul>"	    	
		}else{				    		    		    	
	    	tpEle += "<p class='rela'><span proid='"+planMsg.projectId+"' class='busName' id='busName'>"+planMsg.projectName+"</span></p>"	    	
		}
	
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>拨打日期:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='addPlansDateBox'>"
	    tpEle += "<i class='iconfont icon-rili'></i>"
	    tpEle += "<input class='addPlansDateInput' readonly='readonly' type='text' id='startPlan' value='"+getNowTime().split(' ')[0]+"' placeholder='请选择开始日期'>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>拨打时间:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='timePointBox'><p dt='9' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 09:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='10' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 10:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='11' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 11:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='12' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 12:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='13' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 13:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='14' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 14:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='15' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 15:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='16' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 16:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='17' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 17:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='18' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 18:00</p></div>"
//	    tpEle += "<div class='timePointBox'><p dt='19' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 19:00</p></div>"
//	    tpEle += "<div class='timePointBox'><p dt='20' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 20:00</p></div>"
	    tpEle += "<div class='timePointBox'><p dt='19' onclick='selectTime(this)' class='timePoint grayBg'></p><p class='justTimePos'> 19:00</p><p class='justTimePosF'> 20:00</p></div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
//	    tpEle += "<div class='addPlansItem'>"
//	    tpEle += "<strong class='addPlansLabel'>是否支持打断:</strong>"
//	    tpEle += "<div class='addPlansContent isInterrupt'>"
//	    tpEle += "<button onclick='noOpen()' dt='1' class='addPlansBtn whiteBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
//	    tpEle += "<button dt='2' class='addPlansBtn blueBg'>否 <i class='iconfont icon-cuo'></i></button>"
//	    tpEle += "</div>"
//	    tpEle += "</div>"
	    
	   
	    
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>选择网关:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='businessBox'>"
	    tpEle += "<p class='rela'><span class='busName' id='gateName' onclick='showAllGateWay(event)'>请选择网关</span> <i class='iconfont icon-xiala xialas'></i></p>"
	    tpEle += "<ul id='allGateWay' class='businessUl'>"
	    tpEle += gateway
	    tpEle += "</ul>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong style='line-height: 0.26rem;' class='addPlansLabel'><span class='important'>*</span>端口信息:</strong>"
	    tpEle += "<div id='portBox' class='addPlansContent'>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='height: 0.4rem;' class='addPlansItem'>"
	    tpEle += "<strong style='line-height: 0.4rem;' class='addPlansLabel'>是否转接人工客服:</strong>"
	    tpEle += "<div class='addPlansContent isTransfer'>"
	    if(planMsg.isTransfer == 1){
	    	tpEle += "<button onclick='changeIs(this,1)' dt='1' class='addPlansBtn blueBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
	    	tpEle += "<button onclick='changeIs(this,0)' dt='2' class='addPlansBtn whiteBg'>否 <i class='iconfont icon-cuo'></i></button>"
	    }else{
	    	tpEle += "<button onclick='changeIs(this,1)' dt='1' class='addPlansBtn whiteBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
	    	tpEle += "<button onclick='changeIs(this,0)' dt='2' class='addPlansBtn blueBg'>否 <i class='iconfont icon-cuo'></i></button>"
	    }
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>是否支持短信:</strong>"
	    tpEle += "<div class='addPlansContent isMessage'>"
	    if(planMsg.isSendSMSisSendSMS == 1){
	    	tpEle += "<button onclick='changeIs(this,1)' dt='1' class='addPlansBtn blueBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
	    	tpEle += "<button onclick='changeIs(this,0)' dt='2' class='addPlansBtn whiteBg'>否 <i class='iconfont icon-cuo'></i></button>"
	    }else{
	    	tpEle += "<button onclick='changeIs(this,1)' dt='1' class='addPlansBtn whiteBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
	   	 	tpEle += "<button onclick='changeIs(this,0)' dt='2' class='addPlansBtn blueBg'>否 <i class='iconfont icon-cuo'></i></button>"
	    }	    
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='display:none' class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>转接端口:</strong>"
	    tpEle += "<div id='portBox_sec' class='addPlansContent'>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='display:none' id='serTypeBox' class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>人工客服转接类别:</strong>"
	    tpEle += "<div class='addPlansContent transferGrade'>"
	    tpEle += "<span class='serviceType'><i dt='1' onclick='changeService(this)' class='iconfont icon-choosehandle'></i> <span>A</span></span>"
	    tpEle += "<span class='serviceType'><i dt='2' onclick='changeService(this)' class='iconfont icon-choosehandle'></i> <span>B</span></span>"
	    tpEle += "<span class='serviceType'><i dt='3' onclick='changeService(this)' class='iconfont icon-choosehandle'></i> <span>C</span></span>"
	    tpEle += "<span class='serviceType'><i dt='4' onclick='changeService(this)' class='iconfont icon-choosehandle'></i> <span>D</span></span>"
	    tpEle += "<span class='serviceType'><i dt='5' onclick='changeService(this)' class='iconfont icon-choosehandle'></i> <span>E</span></span>"
	    tpEle += "<span class='serviceType'><i dt='6' onclick='changeService(this)' class='iconfont icon-choosehandle'></i> <span>F</span></span>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "<p class='part05'><strong>提示:</strong> <i id='agree' onclick='protocol(event)' class='iconfont icon-30xuanzhongfangxingfill'></i> 您已同意<span onclick='showProtocol()'>【用户会话】协议</span>，您的外呼任务会在系统记录</p>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelPlanBtn' class='grayBg'>取消</button>"
	    tpEle += "<button id='surePlan' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	var planBox = layer.open({
  		type: 1,
  		title: "设置任务",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['9.4rem', '8rem'],
  		content: tpEle
	});	 
    $("#startPlan").datepicker({dateFormat: 'yy-mm-dd',minDate:getNowTime()});  
    $('#surePlan').click(function(){
    	if(JSON.parse(allGateway).length == 0){
    		layer.msg('暂无可用网关或线路')
    		return;
    	}
    	if(!$('#agree').hasClass('icon-30xuanzhongfangxingfill')){
    		layer.msg('您还未同意用户会话协议')
    		return
    	}
    	if(alreadyAdd){
    		surePlan(customerIdList,planMsg.planId,type)
    	}else{
    		layer.msg('请勿重复点击')
    	}
//  	alreadyAdd = false;   	
    })
	$('#cancelPlanBtn').click(function(){
		cancelSingle(planBox)
	})	
	showFirstGate()
}
function protocol(e){
	$(e.target).toggleClass('icon-30xuanzhongfangxingfill').toggleClass('icon-fangxingweixuanzhong')
}
function changeService(that){
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}
function selectTime(that){
	$(that).toggleClass('blueBg').toggleClass('grayBg')
}
function changeIs (that,type){
	if($(that).parent().prev().text() == "是否转接人工客服:"){
		if($("#gateName").attr("gatewayType") == "2"){
			layer.msg("sip线路暂不支持转接")
			return;
		}
	}
	if($(that).parent().prev().text() == "是否支持短信:"){
		if($("#gateName").attr("gatewayType") == "2"){
			layer.msg("sip线路暂不支持短信")
			return;
		}
	}
	$(that).addClass('blueBg').removeClass('whiteBg')
	$(that).siblings().addClass('whiteBg').removeClass('blueBg')
	if(type=='1'){
		$('#serTypeBox').css('display','none')
	}else if(type=='0'){
		$('#serTypeBox').css('display','none')
	}
}
function changeThisClass(that){
	if($(that).attr('reg')!=='REGISTER_OK'){
		layer.msg('该端口存在异常！')
		return;
	}
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}
function cancelSingle(index){
	layer.close(index);
}
function surePlan(customerIdList,planId,type){     //重新添加计划
	var timePort = $('.timePointBox .blueBg')
	var lts = []
	$.each(timePort, function(index,val) {
		lts.push(parseInt($(val).attr('dt')))
	});
	//判断时间段部分
	var time = new Date()
	var nowTime = ''	
	var hour = time.getHours()<10 ? '0'+time.getHours() : time.getHours()
	var min = time.getMinutes()<10 ? '0'+time.getMinutes() : time.getMinutes()	
	nowTime=hour+':'+min
//	if(lts.length==0){
//		if(time.getHours()<9||time.getHours()>21){     //不在工作时间内
//			layer.msg('当前时间不在工作时间内，请选择时间')
//			return;
//		}
//	}
	var transfer=''
	$.each($('.transferGrade .icon-choosehandle'), function(ind,val) {
		transfer+= $(val).attr('dt')
	});
	
	var portList = []
	$.each($('#portBox .icon-choosehandle[reg]'), function(index,value) {
		portList.push($(value).attr('type'))
	});
	
	var portList_sec = []
	$.each($('#portBox_sec .icon-choosehandle'), function(index,value) {
		portList_sec.push($(value).attr('type'))
	});
	
	var obj = {
		userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		excuteTimeStr:$('#startPlan').val(),
		projectId:$("#busName").attr('proid'),
		timeStr:JSON.stringify(lts),
		isInterrupt:$('.isInterrupt .blueBg').attr('dt'),
		transferGrade:transfer,
		isTransfer:$('.isTransfer .blueBg').attr('dt'),
		isSendSMS:$('.isMessage .blueBg').attr('dt'),
		customerIdListStr:JSON.stringify(customerIdList),
		time:nowTime,
		planName:$('#planName').val().replace(/\s+/g, ""),
		gatewayId:$("#gateName").attr('gatewayId'),
		callPortListStr:portList.join(','),
		isAdd: type == 0 ? 0 : 1,
		batchNo:"",
		planId:planId
//		trancferPortListStr:portList_sec.join(',')
	}
	if(obj.planName==''){
		layer.msg('未填写任务名称')
		return
	}
	if(obj.projectId==''){
		layer.msg('未选择话术')
		return
	}
	if(obj.excuteTimeStr==''){
		layer.msg('未选择拨打日期')
		return
	}
//	if(obj.excuteTimeStr !== getNowTime().split(' ')[0]){
//		if(lts.length ==0){
//			obj.timeStr = JSON.stringify([9,10,11,12,13,14,15,16,17,18,19])
//		}
//	}
//	if(failGatewayNumList.length!==0){  //网关账号密码不一致时 提示
//		layer.msg('网关信息无效，请联系平台') 
//		return;
//	}
	alreadyAdd = false;
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/addPlan",
		data:obj,
		success:function(data){
			layer.close(loadings)
			if(JSON.parse(data).result==0){
				setTimeout(function(){
					getAccList(curPage)
				},3000)		
				layer.closeAll()
				layer.msg('添加成功')					
			}else if(JSON.parse(data).result==2){
				layer.msg('添加时间已过，请重新选择日期或时间')
			}else if(JSON.parse(data).result==3){
				layer.msg('该号码已有任务，请勿重复添加')
			}else if(JSON.parse(data).result==4){
				layer.msg('您选择的时间非工作时间，请选择正确的时间')
			}else if(JSON.parse(data).result==5){
				layer.msg('请选择端口或者联系代理商购买')
			}else if(JSON.parse(data).result==6){
				layer.msg('此任务已在执行中，请重新设置计划或修改当前计划名称')
			}else{
				layer.closeAll()
				layer.msg('添加失败')
			}
			alreadyAdd = true;
		},
		error:function(data){
			console.log(data)
			alreadyAdd = true;
		}
	});
}
function enterSearch(e) {
    if(e.keyCode == 13) {
        getAccList(1,'search')
    }
}
function noOpen(){
	layer.msg('打断功能暂未开放')
}
function planMsgs(that){
	var planMsg = JSON.parse($(that).attr("data"))
	console.log(planMsg)
	
	var tpEle  = "<div style='height:100%;overflow: hidden;'>"
	
		tpEle += "<div style='margin-top:0.2rem' class='planDiv'>"
		tpEle += "<label class='planLabel'>任务名称：</label>"
		tpEle += "<p class='planP'>"+planMsg.planName+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>话术模板：</label>"
		tpEle += "<p class='planP'>"+planMsg.projectName+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>任务量：</label>"
		tpEle += "<p class='planP'>"+planMsg.customerCount+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>创建时间：</label>"
		tpEle += "<p class='planP'>"+planMsg.addTime+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>开始时间：</label>"
		tpEle += "<p class='planP'>"+planMsg.excuteTime+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>最新执行时间：</label>"
		tpEle += "<p class='planP'>"+planMsg.updateTime+"</p>"
		tpEle += "</div>"
		
		
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>拨打时间：</label>"
		tpEle += "<div>"
		
		var timeList = JSON.parse(planMsg.sourceTimeStr)
		for (var i = 9;i<20;i++) {
	    	var colors =  "grayBg"
	    	for (var j = 0;j<timeList.length;j++) {    		
	    		if(i == timeList[j]){
	    			colors = "blueBg"
	    		}	    			    		
	    	}
	    	var num = i < 10 ? '0'+i : i
	    	var ex = i == 19 ? "<p class='justTimePosF'> 20:00</p>" : ""
	    	tpEle += "<div style='margin-top: 0.2rem;' class='timePointBox'><p dt='"+num+"' class='timePoint "+colors+"'></p><p class='justTimePos'> "+num+":00</p>"+ex+"</div>"
	    }
		
		tpEle += "</div>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>网关信息：</label>"
		tpEle += "<p class='planP'>"+planMsg.gatewayNumbers+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div class='planDiv'>"
		tpEle += "<label class='planLabel'>端口信息：</label>"
		var lis = planMsg.callPortListStr.split(",")
		console.log(lis)
		var strs = ""
		for (var i=0;i<lis.length;i++) {
			if(lis[i] !== ""){
				strs += "<span><img class='img00' src='../images/yellowUncheck.png' />"+lis[i]+"</span>"
			}			
		}
		tpEle += "<p class='planP'>"+strs+"</p>"
		tpEle += "</div>"
		
		tpEle += "<div style='display: flex;justify-content: space-around;' class='planDiv'>"
		var yes = "<button style='background:#7F9FD5;border:none;color:white' class='lab01'><i style='font-size: 0.16rem;' class='iconfont icon-querenwancheng'></i>是</button>"
		var no = "<button style='background:#DDDDDD;border:none;color:white' class='lab01'><i class='iconfont icon-cuo'></i>否</button>"
		
		var s1 = planMsg.isSendSMS == 1 ? yes : no
		var s2 = planMsg.isTransfer == 1 ? yes : no
		
		tpEle += "<div><span class='lab00'>是否短信:</span>"+s1+"</div>"
		tpEle += "<div><span class='lab00'>是否转接人工:</span>"+s2+"</div>"
		tpEle += "</div>"
		
	    tpEle += "</div>"
		
	layer.open({
  		type: 1,
  		title: "任务详情",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['8.4rem', '7rem'],
  		content: tpEle
	});
}
