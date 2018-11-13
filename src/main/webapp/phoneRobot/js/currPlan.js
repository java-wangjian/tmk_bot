//var curPage = 1;
var loadings ;
var isSearch = false;
var planName = decodeURI(getQueryString('planName'))
var projectName = decodeURI(getQueryString('project')) 
var planid = getQueryString('planid')
var planStatus = getQueryString('planStatus')

function loadFuncOrStyle(){    //初始化样式或函数 
	isLogin()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
	$("#startA").datepicker({
    	onSelect:function(dateText,inst){
       		$("#endA").datepicker("option","minDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	});
	$("#endA").datepicker({
    	onSelect:function(dateText,inst){
        	$("#startA").datepicker("option","maxDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	});
	
    getAccList(1)
}
function changeService0(that){
	$(that).toggleClass('icon-fangxingweixuanzhong').toggleClass('icon-30xuanzhongfangxingfill')
}
function showSelect(event,that){
	event.stopPropagation()
	$(that).next().css('display','block')
}
function getThisTxt(that){
	$(that).parent().prev().children('span').text($(that).text())
	$(that).parent().prev().children('span').attr('sec',$(that).attr('sec'))
	$(that).parent().css('display','none')
}
function getAccList(page,search){
	var obj = {
		'planId':planid,
		'userId':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'curPage':page,
		'planStatus':planStatus
	}
	if(search=='search'){
		isSearch = true
	}
	if(isSearch){
		var levelList = []
		var levelsEle = $('.selRowItem .icon-30xuanzhongfangxingfill')
		levelsEle.each(function(index,value){
			levelList.push(parseInt($(value).attr('level')))
		})
		var sec = $('#seconds').attr('sec')
		var duringTimeRang = ''
		if(sec=='0'){
			duringTimeRang = ''
		}else if(sec=='1'){
			duringTimeRang = 10
		}else if(sec=='2'){
			duringTimeRang = '10-30'
		}
		else if(sec=='3'){
			duringTimeRang = 30
		}
		obj.gradeListStr = JSON.stringify(levelList)
		obj.duringTimeRang = duringTimeRang
		obj.startTimeStr = $('#startA').val()
		obj.endTimeStr = $('#endA').val()
		obj.searchPhone = $('#keywords').val()
	}else{
		obj.gradeListStr = '[]'
		obj.duringTimeRang = ''
		obj.startTimeStr = ''
		obj.endTimeStr = ''
		obj.searchPhone = ''
	}
	loadings = layer.load(2, {shade: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/customer/findCustomerByPlanId",
		data:obj,
		success:function(data){
			layer.close(loadings)
			var res = JSON.parse(data)
			$('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.total/10),
				totalSize: res.total,
				callback: function(current) {
//					curPage = current
					getAccList(current)
				}
			});
			var tempStr = ''	
			if(res.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width: 1.6rem;' src='../images/list_no_mess.png' /></li>"
				$('#calllogList').html(nomess)
				return;
			}
			$.each(res.list, function(index,value) {	
				var customerName = value.customerName == '' ? '暂无信息'  : value.customerName
				var customerCompany = value.customerCompany == '' ? '暂无信息'  : value.customerCompany
				
				tempStr += "<li class='calllogListItem'>"									
				tempStr += "<span class='flex8 lineH92'>"+planName+"</span>"
				tempStr += "<span class='flex8 double'><span class='firstSpan'>"+value.customerPhone+"</span><br /><span class='firstSpan'>"+customerName+"</span><br /><span>"+customerCompany+"</span></span>"
				
				tempStr += "<span class='flex3'>"+getGrade(value.grade)+"</span>"
				tempStr += "<span class='flex8 lineH92'>"+projectName+"</span>"
//				tempStr += "<span class='flex8 double'>"
//				tempStr += "<span class='firstSpan'><span>命中关键词：</span><span>公司在哪里......</span></span>"
//				tempStr += "<br />"
//				tempStr += "<span><span>有效对话轮数：</span><span>6</span></span>"
//				tempStr += "</span>"
				if(value.datetime){
					var dates = value.datetime.split(" ")
					tempStr += "<span class='flex8 double double0'><span class='firstSpan0'>"+dates[0]+"</span><br /><span class='firstSpan0'>"+dates[1]+"</span></span>"
				}else{
					tempStr += "<span style='color:#999' class='flex8 lineH92'>暂无</span>"
				}
				
				
				tempStr += "<span class='flex3'>"+callStatus(value.callStatus)+"</span>"
//				tempStr += "<span class='flex8 lineH92'><audio src='"+value.fileID+"' preload='preload'></audio></span>"
				tempStr += "<span class='flex8 cz'>"
				tempStr += "<span onclick='getDetails(this,0)' callStatus='"+value.callStatus+"' customerId='"+value.customerId+"' callrecordId='"+value.callrecordId+"' >通话记录</span>"
				if(planStatus == 2){
					tempStr += "<span onclick='getDetails(this,1)' callStatus='"+value.callStatus+"' customerId='"+value.customerId+"' callrecordId='"+value.callrecordId+"' style='margin-left:0.2rem;margin-right:0.2rem'>拜访</span>"
				}
//				tempStr +="<span callStatus='"+value.status+"' logid='"+value.id+"' type='export' onclick='batchSingle(this)'>导出</span>"
				tempStr += "</span>"
				tempStr += "</li>"				
			});
            $('#calllogList').html(tempStr)
//          audiojs.events.ready(function() {
//      		audiojs.createAll();
//  		});
            
			
		},
		error:function(data){
			console.log(data)
			
		}
	});
}
function getTable(){	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/plan/statisticalPlan",
		data:{
			userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
		    planId:planid,
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
	    		tpEle += "<tr><td>E&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(明确拒绝)</td><td>"+res.gradeAndGradeCount[4].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[4].percentage)*100).toFixed(2))+"%</td></tr>"
	    		tpEle += "<tr><td>F&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(无效客户)</td><td>"+res.gradeAndGradeCount[5].calledCount+"</td><td>"+((parseFloat(res.gradeAndGradeCount[5].percentage)*100).toFixed(2))+"%</td></tr>"
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
function resetCondition(){
	isSearch = false;
	$('.selRowItem .iconfont').removeClass('icon-30xuanzhongfangxingfill').addClass('icon-fangxingweixuanzhong')
	$('#startA').val('')
	$('#endA').val('')
	$('#keywords').val('')
	$('#seconds').text('不限时长').attr('sec',0)
//	getAccList(1)
}
function exportCurPlan(){
	var obj = {
		'planId':planid,
		'userId':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'planName':planName,
		'projectName':projectName
	}
	if(isSearch){
		var levelList = []
		var levelsEle = $('.selRowItem .icon-30xuanzhongfangxingfill')
		levelsEle.each(function(index,value){
			levelList.push(parseInt($(value).attr('level')))
		})
		var sec = $('#seconds').attr('sec')
		var duringTimeRang = ''
		if(sec=='0'){
			duringTimeRang = ''
		}else if(sec=='1'){
			duringTimeRang = 10
		}else if(sec=='2'){
			duringTimeRang = '10-30'
		}
		else if(sec=='3'){
			duringTimeRang = 30
		}
		obj.gradeListStr = JSON.stringify(levelList)
		obj.duringTimeRang = duringTimeRang
		obj.startTimeStr = $('#startA').val()
		obj.endTimeStr = $('#endA').val()
		obj.searchPhone = $('#keywords').val()
	}else{
		obj.gradeListStr = '[]'
		obj.duringTimeRang = ''
		obj.startTimeStr = ''
		obj.endTimeStr = ''
		obj.searchPhone = ''
	}
	window.location.href = commonUrl+"/tmk-bot/customer/exportExcel?planId="+obj.planId+"&userId="+obj.userId+"&planName="+obj.planName+"&projectName="+obj.projectName+"&gradeListStr="+obj.gradeListStr+"&duringTimeRang="+obj.duringTimeRang+"&startTimeStr="+obj.startTimeStr+"&endTimeStr="+obj.endTimeStr+"&searchPhone="+obj.searchPhone
	
}
function getDetails(that,type){  // type 0:详情      1 拜访
	var target = $(that)
	if(target.attr('callStatus')!=='1'){
		layer.msg('未接通用户，暂无通话详情')
		return;
	}
	var obj = {
		'userId':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'callRecordId':target.attr('callrecordId'),
		'planId': planid,
		'customerId': target.attr('customerId')
		
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/callrecord/findCallRecordByCallRecordId",
		data:obj,
		success:function(data){
			var res = JSON.parse(data)
			if(res.result==1){
				layer.msg('未查到记录')
			}else{
				if(type == 0){
					checkCallLLog(obj.callRecordId,res)
				}else{
					visitLog(target.attr('callrecordId'),target.attr('customerId'),res,planid)
				}
				
			}			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function alertEr(){
	layer.msg('没有数据，无法导出')
}
function visitLog(callrecordId,customerId,uploadid,planId){  //拜访	
	getCallDetails(callrecordId)
	var tpEle  = "<div class='callLogBox'>"
	    tpEle += "<div class='visitMsgBox'>"
	    tpEle += "<div class='callLogMsgItem'><label>任务名称：</label><span>"+uploadid.planName+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>AI意向：</label><span>"+getGrade(uploadid.grade)+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>呼叫日期：</label><span>"+uploadid.datetime+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>通话时间：</label><span>"+formatSeconds(uploadid.durationTime)+"</span></div>"
	    tpEle += "</div>"
	    tpEle += "<div class='callLogContentBox'>"
	    tpEle += "<div class='callLogContent'>"
	    tpEle += "<ul id='callLog' class='mainCallLog'>"
	   
	    tpEle += "</ul>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "<div class='visitMark'>"
	    tpEle += "<div class='visitTxtBox'>"
	    tpEle += "<input maxlength='50' class='visitTxt' id='visitNote' placeholder='输入跟进记录'/>"
	    tpEle += "</div>"
	    tpEle += "<div class='labelBox'>"
	    tpEle += "<label>人工意向:</label>"
	    
	    for (var i=1;i<7;i++) {
	    	var bgs = i == uploadid.visitGrade ? 'checkedBg' : 'unCheckedBg'
	    	tpEle += "<span><b grade='"+i+"' onclick='changeLabel(this)' class='"+bgs+"'></b> <span>"+getGrade(i)+"</span></span>"
	    }
	    
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelVisitLog' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='addVisitNote("+customerId+")' class='blueBg'>确认</button>"
	    tpEle += "</div>"
	    tpEle += "</div>"	   	    
	var visitBox = layer.open({
  		type: 1,
  		title: "拜访",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['8.4rem', '9.2rem'],
  		content: tpEle
	});
	$('#cancelVisitLog').click(function(){
		cancelSingle(visitBox)
	})
} 
function changeLabel(that){
	$(that).toggleClass('checkedBg').toggleClass('unCheckedBg')
	$(that).parent().siblings().children('b').removeClass('checkedBg').addClass('unCheckedBg')
}
function addVisitNote(customerId){
	var visits = {
		userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		customerId:customerId,
		visitDetails: $('#visitNote').val().replace(/\s+/g, ""),
		grade:$('.labelBox .checkedBg').attr('grade'),
		planId: planid
	}
	if(!visits.grade){
		layer.msg('请选择人工意向')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/visit/addVisit",
		data:visits,
		success:function(data){
			layer.closeAll()
			var res = JSON.parse(data)
			if(res.result==0){
				layer.msg('跟进成功')
    			getAccList(1)
			}else{
				layer.msg('跟进失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function checkCallLLog(callrecordId,uploadid){	//通话记录弹窗
	var tpEle  = "<div class='callLogBox'>"
	    tpEle += "<div class='callLogMsgBox'>"
	    tpEle += "<div class='callLogMsgItem'><label>任务名称：</label><span>"+uploadid.planName+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>信号强度：</label><span>"+uploadid.callSignal+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>通话时间：</label><span>"+uploadid.datetime+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>通话时长：</label><span>"+formatSeconds(uploadid.durationTime)+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>话术模板：</label><span>"+uploadid.projectName+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>意向评定：</label><span>"+getGrade(uploadid.grade)+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>被叫号码：</label><span>"+uploadid.customerPhone+"</span></div>"
	    tpEle += "</div>"
	    tpEle += "<div class='callLogContentBox'>"
	    tpEle += "<div class='callLogContent'>"
	    tpEle += "<ul id='callLog' class='mainCallLog'>"
	    
	    tpEle += "</ul>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    if(parseInt(uploadid.id)==-1){
	    	tpEle += "<a onclick='alertEr()' class='callLogExportBtn'><i class='iconfont icon-tubiao05'></i> 导出</a>"
	    }else{
	    	tpEle += "<a onclick='exportCallRecordDetail("+callrecordId+")' class='callLogExportBtn'><i class='iconfont icon-tubiao05'></i> 导出</a>"
	    }
	    tpEle += "<div id='textaudio1'></div>"
	    tpEle += "</div>"
	   	    	    
	layer.open({
  		type: 1,
  		title: "通话记录",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['8.4rem', '8.62rem'],
  		content: tpEle
	});
	var wxAudio = new Wxaudio({
		ele: '#textaudio1',
		title: '',
		disc: '',
		src: uploadid.fileID,
		width: '320px'
	});
	getCallDetails(callrecordId)
}
function cancelSingle(index){
	layer.close(index);
}
function exportCallRecordDetail(callRecordId) {  // 创建from表单 
	$("#downloadform").remove();
	var form = $("<form>"); // 定义一个form表单   
	form.attr("id", "downloadform");
	form.attr("style", "display:none");
	form.attr("target", "");
	form.attr("method", "get");
	form.attr("action", commonUrl+"/tmk-bot/callrecord/detailExcel");
	
	var input1 = $("<input>");
	input1.attr("type", "hidden");
	input1.attr("name", "id");
	input1.attr("value", callRecordId);
	
//	var input2 = $("<input>");
//	input2.attr("type", "hidden");
//	input2.attr("name", "id");
//	input2.attr("value", JSON.parse(sessionStorage.getItem('loginMsg')).id);
	
	form.append(input1);
//	form.append(input2);
	$("body").append(form); //将表单放置在web中  

	form.submit(); //表单提交   
}

function downloadFile(idList) {  // 创建from表单 
	if(idList.length>5000){
		layer.msg('最多只能导出五千条数据！')
		return
	}
	$("#downloadform").remove();
	var form = $("<form>"); // 定义一个form表单   
	form.attr("id", "downloadform");
	form.attr("style", "display:none");
	form.attr("target", "");
	form.attr("method", "post");
	form.attr("action", commonUrl+"/tmk-bot/callrecord/export");
	
	var input1 = $("<input>");
	input1.attr("type", "hidden");
	input1.attr("name", "idList");
	input1.attr("value", idList);
	
	var input2 = $("<input>");
	input2.attr("type", "hidden");
	input2.attr("name", "id");
	input2.attr("value", JSON.parse(sessionStorage.getItem('loginMsg')).id);
	
	form.append(input1);
	form.append(input2);
	$("body").append(form); //将表单放置在web中  

	form.submit(); //表单提交   
}

function getCallDetails(callrecordId){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/callrecord/detail",
		data:{
			id:callrecordId
		},
		success:function(data){			
			var res = JSON.parse(data)
//			console.log(res)
			$.each(res.data, function(index,val) {
				if(val.fileWord!=='开场白'){
					createAbox(val.fileURL,val.fileWord,val.datetime)
				}	
				if(val.recordWord!==''){
					createQbox(val.recordURL,val.recordWord,val.datetime)
				}
							
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function createQbox(voice,txt,time){
	var tpEle = "<li>"
	    tpEle += "<p class='qHead'>Q</p>"
	    tpEle += "<div class='msgBox msgBoxQ'>"
	    tpEle += "<span>"+time.split('.')[0]+"</span>"
	    tpEle += "<img class='jiao' src='../images/qjiao.png'/>"
	    tpEle += "<div>"+txt+"</div>"
	    tpEle += "</div>"
	    tpEle += "</li>"
	$('#callLog').append(tpEle)
}
function createAbox(voice,txt,time){
	var tpEle = "<li>"
	    tpEle += "<p class='aHead'>A</p>"
	    tpEle += "<div class='msgBox msgBoxA'>"
	    tpEle += "<span>"+time.split('.')[0]+"</span>"
	    tpEle += "<img class='jiao' src='../images/ajiao.png'/>"
	    tpEle += "<div class='palyWrap'>"
	    tpEle += "<div class='playBox'>"
	    tpEle += "<img onclick='playVoice(this)' class='playing' src='../images/voiceImg.gif' />"
	    tpEle += "<audio class='voiceEle' src='"+voice+"'></audio>"
	    tpEle += "</div>"
	    tpEle += txt
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</li>"
	$('#callLog').append(tpEle)
}
function playVoice(that){	
//	var audios = $('.voiceEle')  //控制其他audio归零
//	for (var i=0;i<audios.length;i++) {
//		audios[i].currentTime = 0;
//		$(audios[i]).prev().attr('src','../images/voiceImg.gif')
//	}	
// 
//	$(that).attr('src','../images/playing.gif')  //控制自身

//	
//	console.log($(player).not(this))

//	$(player).not(this).each(function(){   
//		$(this)[0].pause();    
//		$(this)[0].currentTime = 0.0;  
//	});
	
	var player = $(that).next()[0]   //当前的audio元素
	var others = $('.voiceEle').not(player)  //除当前audio外
		
	player.addEventListener('ended', function () {  
    	$(that).attr('src','../images/voiceImg.gif')
	}, false);
	
	for (var i=0;i<others.length;i++) {
		others[i].pause()
//		others[i].currentTime = 0;  //设置这个360浏览器会异常
		$(others[i]).prev().attr('src','../images/voiceImg.gif')
	}
	if(player.paused) {  
		player.currentTime = 0
        player.play();   
        $(that).attr('src','../images/playing.gif')  //控制自身
    }else{  
        player.pause(); 
        $(that).attr('src','../images/voiceImg.gif')  //控制自身
    }  	
}