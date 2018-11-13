var curPage = 1;
var isSearch = false;
var allProject = [];
var allGateway = '';
var loadings ;
var switchLoading  ;
//var failGatewayNumList = []
var alreadyAdd = true;
function loadFuncOrStyle(){    //初始化样式或函数
	getAllPortList()
	isLogin()
	unReadCount()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
	setTimeout(function(){
		getAccList(curPage)
	},2000)	
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
//			failGatewayNumList = JSON.parse(data).failGatewayNumList
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
//	if(tar.attr('status')=='4'){
//		layer.msg('计划已经取消，不可开始')
//		return;
//	}
	var sta ;
	var sUrl = tar.attr('src').split('images/')[1]
	if(sUrl=='open.png'){
		tar.attr('src','../images/close.png')
		sta = 4
	}else{
		tar.attr('src','../images/open.png')
		sta = 0
	}
	var chStatus = {
		planId: tar.attr('planid'),
		planStatus: sta,
		userId: JSON.parse(sessionStorage.getItem('loginMsg')).id
	}  
	switchLoading = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/hold",
		data:chStatus,
		success:function(data){
			if(JSON.parse(data).result==5){
				layer.msg('暂无可使用端口')
			}
			setTimeout(function(){
				getAccList(curPage)
			},2000)		
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
		'planStatus':1
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
			layer.close(switchLoading)
			var res = JSON.parse(data)
			var tempStr = ''	
			if(res.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#accMgrMainUl').html(nomess)
				return;
			}
			$.each(res.list, function(index,value) {				
				tempStr += "<li class='accMgrMainLi accMgrItem'>"
				
//				tempStr += "<a class='msgCap' href='./currPlan.html?planStatus=1&&planid="+value.planId+"&&planName="+encodeURI(value.planName)+"&&project="+encodeURI(value.projectName)+"' ></a>"
				
//				tempStr += "<span class='flex4'><i serid='"+value.id+"' onclick='checkThisBtn(this)' class='iconfont icon-xuanze'></i></span>"		
				tempStr += "<span class='flex3'>"+(index+1)+"</span>"
				tempStr += "<span class='flex6'>"+value.planName+"</span>"
				tempStr += "<span class='flex6'>"+value.projectName+"</span>"


				
				tempStr += "<span class='flex6 timeJust'><span>"+value.excuteTime.split(' ')[0]+"</span><br / ><span>"+value.excuteTime.split(' ')[1]+"</span></span>"
				var color = value.planStatus == 1 ? "color2" : value.planStatus == 0 ? "color1" : value.planStatus == 3 ? "color3" : "color0"

				tempStr += "<span class='flex4 "+ color +"'>"+curPlanStatus(value.planStatus)+"</span>"
				
				if(value.planStatus !== 4){
					tempStr += "<span class='flex4'><img status='"+value.planStatus+"' planid='"+value.planId+"' onclick='togSwitch(this)' class='swImg' src='../images/open.png'/></span>"
				}else{
					tempStr += "<span class='flex4'><img status='"+value.planStatus+"' planid='"+value.planId+"' onclick='togSwitch(this)' class='swImg' src='../images/close.png'/></span>"
				}		
				tempStr += "<span class='flex12 new00'><a href='./currPlan.html?planStatus=1&&planid="+value.planId+"&&planName="+encodeURI(value.planName)+"&&project="+encodeURI(value.projectName)+"'><span class='child00'>通话详情</span></a><span data='"+JSON.stringify(value)+"' onclick='planMsgs(this)' class='child01'>任务详情</span><span planid='"+value.planId+"' onclick='getTable(this)' class='child00'>任务统计</span></span>"
				
				tempStr += "<span class='flex6 controlSpan'><span class='child02' status='"+value.planStatus+"' onclick='editCurPlan("+JSON.stringify(value)+",this)' ><i class='iconfont icon-bianji'></i>编辑</span><span class='child03' status='"+value.planStatus+"' planid='"+value.planId+"' onclick='canclePlan(this)'><i class='iconfont icon-quxiao'></i>取消</span>"
				tempStr += "</span>"
				
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
function showAllGateWay(e){
	e = e || window.event
	e.stopPropagation();
	$(".businessUl").css('display','none')
	$("#allGateWay").css('display','block')
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
function editCurPlan(planMsg,that){   //添加计划弹窗     type为count时  取list的第一项为总数量	
	if(!planMsg.sourceTimeStr){
		planMsg.sourceTimeStr = "[]"
	}
	var timeList = JSON.parse(planMsg.sourceTimeStr)
//	if(planMsg.status < 3){
//		layer.msg('当前任务开启中，无法编辑')
//		return;
//	}
	if(allProject.length == 0){
		layer.msg('未获取到模板，请稍后再试！')
		return;
	}
	var target = $(that)
	if(target.attr('status') !== '4'){
		layer.msg('任务处于开启状态,无法编辑，关闭任务后重试')
		return;
	}
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
	    tpEle += "<strong class='addPlansLabel'>任务名称:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div style='border:none' class='businessBox'>"
		tpEle += "<input readonly='readonly' type='text' class='planName' id='planName' value='"+planMsg.planName+"' placeholder='"+planMsg.planName+"' />"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>话术模板:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div style='border:none' class='businessBox noBorder'>"
	    tpEle += "<p class='rela'><span proid='"+planMsg.projectId+"' class='busName' id='busName'>"+planMsg.projectName+"</span> </p>"
	    
//	    tpEle += "<p class='rela'><span proid='"+planMsg.projectId+"' class='busName' id='busName' onclick='showBusUl()'>"+planMsg.projectName+"</span> <i class='iconfont icon-xiala xialas'></i></p>"
//	    tpEle += "<ul id='businessUl' class='businessUl'>"
//	    tpEle += allproStr
//	    tpEle += "</ul>"	
	    	
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
	    for (var i = 9;i<20;i++) {
	    	var colors =  "grayBg"
	    	for (var j = 0;j<timeList.length;j++) {    		
	    		if(i == timeList[j]){
	    			colors = "blueBg"
	    		}	    			    		
	    	}
	    	var num = i < 10 ? '0'+i : i
	    	var ex = i == 19 ? "<p class='justTimePosF'> 20:00</p>" : ""
	    	tpEle += "<div class='timePointBox'><p dt='"+num+"' onclick='selectTime(this)' class='timePoint "+colors+"'></p><p class='justTimePos'> "+num+":00</p>"+ex+"</div>"
	    }
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
	    tpEle += "<p class='rela'><span style='text-indent:0.2rem' class='busName' id='gateName' onclick='showAllGateWay(event)'>请选择网关</span> <i class='iconfont icon-xiala xialas'></i></p>"
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
	    
	    tpEle += "<div style='display:none' class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>转接端口:</strong>"
	    tpEle += "<div id='portBox_sec' class='addPlansContent'>"
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
    		surePlan(planMsg.planId,planMsg.excuteCount,$('#busName').attr('proid'))
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
function changeThisClass(that){
	if($(that).attr('reg')!=='REGISTER_OK'){
		layer.msg('该端口存在异常！')
		return;
	}
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}
function protocol(e){
	$(e.target).toggleClass('icon-30xuanzhongfangxingfill').toggleClass('icon-fangxingweixuanzhong')
}
function surePlan(planId,excuteCount,projectId){  
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
		id:planId,
		timeStr:JSON.stringify(lts),
		isInterrupt:$('.isInterrupt .blueBg').attr('dt'),
		transferGrade:transfer,
		isSendSMS:$('.isMessage .blueBg').attr('dt'),
		isTransfer:$('.isTransfer .blueBg').attr('dt'),
		exuteTimeHHMM:nowTime,
//		planName:$('#planName').val().replace(/\s+/g, ""),
		excuteCount:excuteCount,
		projectId:projectId,
		gatewayId:$("#gateName").attr('gatewayId'),
		callPortListStr:portList.join(','),
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
	if(obj.excuteTimeStr !== getNowTime().split(' ')[0]){
		if(lts.length ==0){
			obj.timeStr = JSON.stringify([9,10,11,12,13,14,15,16,17,18,19,20,21])
		}	
	}
//	if(failGatewayNumList.length!==0){
//		layer.msg('网关信息无效，请联系平台') 
//		return;
//	}
	alreadyAdd = false;
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/updatePlanInfo",
		data:obj,
		success:function(data){
			layer.close(loadings)
			if(JSON.parse(data).result==0){
				setTimeout(function(){
					getAccList(curPage)
				},2000)								
				layer.closeAll()
				layer.msg('编辑成功')					
			}else if(JSON.parse(data).result==2){
				layer.msg('任务时间已过')  
			}else if(JSON.parse(data).result==4){
				layer.msg('您选择的时间非工作时间，请选择正确的时间')
			}else if(JSON.parse(data).result==5){
				layer.msg('暂无可使用端口')
			}else{
				layer.closeAll()
				layer.msg('编辑失败')
			}
			alreadyAdd = true;
		},
		error:function(data){
			console.log(data)
			alreadyAdd = true;
		}
	});
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
function cancelSingle(index){
	layer.close(index);
}
function canclePlan(that){
	var target = $(that)
	if(target.attr('status')=='3'){
		layer.msg('计划已经取消，请勿重复操作')
		return;
	}
	var obj = {
		userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
		id:target.attr('planid'),
		isDelete:1
	}
	layer.msg('确定要取消吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		layer.close(index)
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/plan/deletePlan",
				data:obj,
				success:function(data){
					if(JSON.parse(data).result==0){
						layer.msg('取消成功')
						getAccList(curPage)
					}
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
  });		
}
function getTable(that){	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/statisticalPlan",
		data:{
			userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
		    planId:$(that).attr('planid'),
		},
		success:function(data){
			var res = JSON.parse(data)
//			console.log(res)
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
	    		tpEle += "<tr><td>E&nbsp;&nbsp;&nbsp;(明确拒绝)</td><td>"+res.gradeAndGradeCount[4].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[4].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>F&nbsp;&nbsp;&nbsp;(无效客户)</td><td>"+res.gradeAndGradeCount[5].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[5].percentage)*100).toFixed(2))+"%</td></tr>"
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
function enterSearch(e) {
    if(e.keyCode == 13) {
        getAccList(1,'search')
    }
}
function noOpen(){
	layer.msg('打断功能暂未开放')
}


function singleTemplate(){    //单个模板弹窗		
	var tpEle  = "<div class='singleMsgBox'>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-shouji1'></i>"
	    tpEle += "<input maxlength='11' type='' name='' id='singlePhone' value='' placeholder='请输入手机号'/>"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i style='font-size:0.14rem' class='iconfont icon-yonghu'></i>"
	    tpEle += "<input maxlength='10' type='' name='' id='singleName' value='' placeholder='请输入名称'/>"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-qiye2'></i>"
	    tpEle += "<input maxlength='20' type='' name='' id='singleCompany' value='' placeholder='请输入企业名称'/>"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItemTxt'>"
	    tpEle += "<textarea maxlength='100' id='singleRemark' placeholder='输入备注' name='' rows='' cols=''></textarea>"
	    tpEle += "</div>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='uploadSingle()' class='blueBg'>设置任务</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "单个导入",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['4.6rem', '5.8rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		cancelSingle(singleUp)
	}) 
}
function uploadSingle(){
	var single = {
		userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
		customerPhone: $('#singlePhone').val().replace(/\s+/g, ""),
		customerName: $('#singleName').val().replace(/\s+/g, ""),
		company: $('#singleCompany').val().replace(/\s+/g, ""),
		note: $('#singleRemark').val().replace(/\s+/g, "")
	}
	if(single.customerPhone==''){
		layer.msg('手机号不能为空')
		return;
	}
//	if(!/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(single.customerPhone)){
	if(!/^1[345678]\d{9}$/.test(single.customerPhone)){
		layer.msg('手机号格式不正确')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/customer/addCustomer",
		data:single,
		success:function(data){
			var res = JSON.parse(data)
			layer.closeAll()
			if(res.code==0){				
				var customerIdList = []
				customerIdList.push(res.result)
				newPlan(customerIdList,'check','')  
			}else if(res.code==1){
				layer.msg('该电话号码已存在系统')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function newPlan(list,type,batchNo){   //添加计划弹窗     type为count时  取list的第一项为总数量	  batchNo:是否是批量上传
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
		tpEle += "<input style='text-indent: 0.2rem;' type='text' class='planName' maxlength='20' id='planName' value='' placeholder='请填写计划名称' />"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>话术模板:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='businessBox'>"
	    tpEle += "<p class='rela'><span proid='' style='text-indent:0.2rem' class='busName' id='busName' onclick='showBusUl(event)'>请选择模板</span> <i class='iconfont icon-xiala xialas'></i></p>"
	    tpEle += "<ul id='businessUl' class='businessUl'>"
	    tpEle += allproStr
	    tpEle += "</ul>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    
//	    tpEle += "<div class='addPlansItem'>"
//	    tpEle += "<strong class='addPlansLabel'>主叫号码:</strong>"
//	    tpEle += "<div class='addPlansContent'>"
//	    tpEle += "<span><b onclick='changeLabel(this)' class='checkedBg'></b> <span>全选</span></span>"
//	    tpEle += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
//	    tpEle += "<span><b onclick='changeLabel(this)' class='unCheckedBg'></b> <span>1388888888</span></span>"
//	    tpEle += "</div>"
//	    tpEle += "</div>"
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>拨打日期:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='addPlansDateBox'>"
	    tpEle += "<i class='iconfont icon-rili'></i>"
	    tpEle += "<input class='addPlansDateInput' readonly='readonly' type='text' id='startPlan' value='' placeholder='请选择开始日期'>"
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
//	    tpEle += "<button  dt='2' class='addPlansBtn blueBg'>否 <i class='iconfont icon-cuo'></i></button>"
//	    tpEle += "</div>"
//	    tpEle += "</div>"
	    
	    
	    
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>选择网关:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='businessBox'>"
	    tpEle += "<p class='rela'><span style='text-indent:0.2rem' class='busName' id='gateName' onclick='showAllGateWay(event)'>请选择网关</span> <i class='iconfont icon-xiala xialas'></i></p>"
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
	    
	    tpEle += "<div style='display:none' class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>转接端口:</strong>"
	    tpEle += "<div id='portBox_sec' class='addPlansContent'>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='height: 0.4rem;' class='addPlansItem'>"
	    tpEle += "<strong style='line-height: 0.4rem;' class='addPlansLabel'>是否转接人工客服:</strong>"
	    tpEle += "<div class='addPlansContent isTransfer'>"
	    tpEle += "<button onclick='changeIs(this,1)' dt='1' class='addPlansBtn whiteBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
	    tpEle += "<button onclick='changeIs(this,0)' dt='2' class='addPlansBtn blueBg'>否 <i class='iconfont icon-cuo'></i></button>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>是否支持短信:</strong>"
	    tpEle += "<div class='addPlansContent isMessage'>"
	    tpEle += "<button onclick='changeIs(this,1)' dt='1' class='addPlansBtn whiteBg'>是 <i class='iconfont icon-querenwancheng'></i></button>"
	    tpEle += "<button onclick='changeIs(this,0)' dt='2' class='addPlansBtn blueBg'>否 <i class='iconfont icon-cuo'></i></button>"
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
    		surePlans(list,type,batchNo)
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
function surePlans(list,type,batchNo){  
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
//	if(type=='check'){
		var obj = {
			userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			excuteTimeStr:$('#startPlan').val(),
			projectId:$("#busName").attr('proid'),
			timeStr:JSON.stringify(lts),
			isInterrupt:$('.isInterrupt .blueBg').attr('dt'),
			transferGrade:transfer,
			isTransfer:$('.isTransfer .blueBg').attr('dt'),
			isSendSMS:$('.isMessage .blueBg').attr('dt'),
			customerIdListStr:JSON.stringify(list),
			time:nowTime,
			planName:$('#planName').val().replace(/\s+/g, ""),
			gatewayId:$("#gateName").attr('gatewayId'),
			callPortListStr:portList.join(','),
			isAdd: 0,
			batchNo:batchNo,
			planId:""
//			trancferPortListStr:portList_sec.join(',')
		}

		
		if(obj.planName==''){
			layer.msg('未填写任务名称')
			return
		}
		if(obj.projectId==''){
			layer.msg('未选择话术')
			return
		}
//		if(obj.callPortListStr.length==0||obj.trancferPortListStr.length==0){
//			layer.msg('未选择呼叫端口或者转接端口')
//			return
//		}
		if(obj.excuteTimeStr==''){
			layer.msg('未选择拨打日期')
			return
		}
//		if(obj.excuteTimeStr !== getNowTime().split(' ')[0]){
//			if(lts.length ==0){
//				obj.timeStr = JSON.stringify([9,10,11,12,13,14,15,16,17,18,19,20,21])
//			}			
//		}
		alreadyAdd = false;
		loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/plan/addPlan",
			data:obj,
			success:function(data){
				layer.close(loadings)
				if(JSON.parse(data).result==0){
					layer.closeAll()
					layer.msg('添加成功')	
					setTimeout(function(){
						getAccList(curPage)
					},2000)		
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
				alreadyAdd = true;
				console.log(data)
			}
		});
//	}
}
function batchTemplate(){    //上传模板弹窗	
	var userId = JSON.parse(sessionStorage.getItem('loginMsg')).id
	var tpEle  = "<p id='filesName'></p><div class='uploadTempBox'>"    
	    tpEle += "<iframe style='display:none' name='message'></iframe>" 
		tpEle += "<form id='klFile' action='' method='post' enctype='multipart/form-data' target='message'>" 
	    tpEle += "<input id='userId'  type='hidden' name='userId' value='"+userId+"' />"	
	    tpEle += "<i class='iconfont icon-tianjia addsign0'></i>"
		tpEle += "<input onchange='showFileName(this)' id='tipt' type='file' name='excel' accept='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel'/></form>" 													    
	    tpEle += "</div>"
	    tpEle += "<div class='tishi'><i class='iconfont icon-tishi'></i><br /><span>文件格式必须是.xlsx扩展名</span><br /><span>必须是模板的内容规则</span></div>"
	    tpEle += "<p class='downTempLinkBox'><a href='http://diting-picture.ditingai.com/customer_model.xlsx'><i class='iconfont icon-customer'></i>下载客户模板</a></p>"
	    tpEle += "<button onclick='subimtBtn()' class='uploadTemplateBtn'>确认上传</button>"		    
	layer.open({
  		type: 1,
  		title: "批量导入",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5.6rem'],
  		content: tpEle
	});
	$(".uploadTempBox").mouseenter(function(){		
		$(this).css("border","0.08rem dashed #bfd5f6")
		$(this).find(".icon-tianjia").css("color","#bfd5f6")
	}).mouseleave(function(){
		$(this).css("border","0.08rem dashed #e6e6e6")
		$(this).find(".icon-tianjia").css("color","#e6e6e6")
	})
}
function showFileName(that){
	$('#filesName').text(that.files[0].name)
}
function subimtBtn() { 
	var loading = layer.load(1, {shade: [0.5,'#000'],scrollbar: false,});
 	var form = $("#klFile");   //选择from
 	var options = {  
 		url:commonUrl+"/tmk-bot/customer/batchInsert", 
 		type:'post',
 		success:function(data){  
 			layer.closeAll()
 	  		if(JSON.parse(data).result==0){
   	  			var res = JSON.parse(data)	  	  											
				layer.confirm('批量导入：本次操作'+res.total+'条数据,实际导入'+res.insertCount+'条数据，有'+res.haveCount+'条为错误或重复数据', {
  					btn: ['立即设置任务'] //按钮
				}, function(){
					newPlan(res.customerIdList,'check',res.batchNo) 
				});  
 	  		}else if(JSON.parse(data).result==1){
 	  			layer.msg('文件格式不正确')
 	  		}else if(JSON.parse(data).result==2){
 	  			layer.msg('文件错误，请下载模板后重新导入')
 	  		}else{
 	  			layer.msg('请选择正确的文件后重试')
 	  		}
  		}
 	};  
 	form.ajaxSubmit(options); 
}
function planMsgs(that){
	var planMsg = JSON.parse($(that).attr("data"))
	
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
   		area: ['8.4rem', '7.3rem'],
  		content: tpEle
	});
}