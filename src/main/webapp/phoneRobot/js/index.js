var allProject = [];
var allGateway = ''
var loadings ;
var currPage = 1;
//var failGatewayNumList = []; 
var alreadyAdd = true;
function loadFuncOrStyle(){    //初始化样式或函数 
	isLogin()	
//	initBalanceMessage()
    unReadCount()   
    getAllPro()  //获取用户所有项目
    getAllPortList()  //获取用户网关以及端口号
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
	$(".tabs").click(function(){
		$(this).parent().children().css("border-bottom","0.06rem solid #F7F7F7")
		$(this).css("border-bottom","0.06rem solid #6186CF")
	}) 
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
	//通话记录时间选择器初始化 
	$("#startA").datepicker({
    	onSelect:function(dateText,inst){
       		$("#endA").datepicker("option","minDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	}).val(getNeardays(2));;
	$("#endA").datepicker({
    	onSelect:function(dateText,inst){
        	$("#startA").datepicker("option","maxDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	}).val(getNeardays(0));;	
	showTabs()
//	$("#planList").scroll(function(){  //懒加载
//      var $this =$(this),
//      viewH =$(this).height(),//可见高度
//      contentH =$(this).get(0).scrollHeight,//内容高度
//      scrollTop =$(this).scrollTop();//滚动高度
//      if(scrollTop/(contentH -viewH)>=0.9999){ //到达底部时,加载新内容
//      	console.log(1111)
//      }		
//  });    
    
}
function showTabs(){
	if(!getQueryString('tabs')){   //不存在tabs  trigTab
		window.location.href = window.location.href+'?tabs=0';
		return;
	}
	switch (getQueryString('tabs')){
		case '0':
		getPriSeaList(1)
		$('.main').css('display','none')
		$('.tabs').css('border-bottom', '0.06rem solid #f7f7f7')
		$('.main:eq(0)').css('display','block')
		$('.tabs:eq(0)').css('border-bottom', '0.06rem solid #6186CF')
		break;
		case '1':
		getCallLogByCondition(1)  
		$('.main').css('display','none')
		$('.tabs').css('border-bottom', '0.06rem solid #f7f7f7')
		$('.main:eq(1)').css('display','block')
		$('.tabs:eq(1)').css('border-bottom', '0.06rem solid #6186CF')
		break;
		case '2':
		getVisitList(1)
		$('.main').css('display','none')
		$('.tabs').css('border-bottom', '0.06rem solid #f7f7f7')
		$('.main:eq(2)').css('display','block')
		$('.tabs:eq(2)').css('border-bottom', '0.06rem solid #6186CF')
		break;
		default:
		getPriSeaList(1)
		$('.main').css('display','none')
		$('.tabs').css('border-bottom', '0.06rem solid #f7f7f7')
		$('.main:eq(0)').css('display','block')
		$('.tabs:eq(0)').css('border-bottom', '0.06rem solid #6186CF')
	}
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
//			console.log(allGateway)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function cusAllCheck(e){
	
	$(e.target).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	var status = $(e.target).next()
	if(status.text()=='全选'){
		$('.planListItem i[callStatus!="1"]').removeClass('icon-xuanze').addClass('icon-choosehandle')
		status.text('取消')
	}else{
		$('.planListItem i[callStatus!="1"]').removeClass('icon-choosehandle').addClass('icon-xuanze')		
		status.text('全选')
	}
	changeRed()
}
function calllogAllCheck(e){
	$(e.target).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	var status = $(e.target).next()
	if(status.text()=='全选'){
		$('.calllogListItem i').removeClass('icon-xuanze').addClass('icon-choosehandle')
		status.text('取消')
	}else{
		$('.calllogListItem i').removeClass('icon-choosehandle').addClass('icon-xuanze')		
		status.text('全选')
	}
}
function checkThisBtn(e){
	$(e.target).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}
function cancelAllPlan(){
	layer.msg('确定要取消所有计划吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/plan/deleteAllPlan",
				data:{userId:JSON.parse(sessionStorage.getItem('loginMsg')).id},
				success:function(data){
					getPriSeaList(1)
					if(JSON.parse(data).result==0){		
						layer.msg('取消成功')
					}			
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
    });
}
function pausePlan(that){
	layer.msg('确定停止所有任务？', {
    	time: 20000, 
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		layer.close(index)
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/plan/stop",
				data:{userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,isStop:1},
				success:function(data){
					if(JSON.parse(data).result==0){		
						layer.msg('操作成功')
					}	
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
    });
}
function delSHItem(that,customerId){  //
	if($(that).attr('planStatus')==1){
		layer.msg('该用户正在计划中，无法删除')
		return;
	}
//	layer.msg('确定要删除吗？', {
//  	time: 20000, 
//  	btn: ['确定', '取消'],
//  	yes: function (index) {
//  		layer.close(index)
    		passSingle(customerId)
//  	}
//  });
}
function passSingle(customerId){
	var list = []
	list.push(customerId)	
	delCustomer(list)
}
function passDelList(){
	var list = []
 	$('.planListItem .icon-choosehandle').each(function(){
 		list.push(parseInt($(this).attr('msgid')))
 	})
 	delCustomer(list)
} 
function batchDel(){    //批量删除	
    		var count = $("#count").val().replace(/\s+/g, "")
			if(count==""){
    			passDelList()				
			}else{
				if(!/^[0-9]+$/.test(count)){
					layer.msg('请输入数字'); 
				}else{
    				delCustomerByCount(count); 					
				}
			}	
}
function delCustomerByCount(num){
	layer.msg('确定要删除吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/customer/manyDelete",
				data:{
					userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
					count:num
				},	
				success:function(data){
					if(JSON.parse(data).result==0){
						getPriSeaList(1)		
						layer.msg('删除成功')
					}else if(JSON.parse(data).result==2){
						layer.msg('没有可删除的数据')
					}else{
						layer.msg('删除失败')
					}	
					currPageAllCheck()
				},
				error:function(data){
					console.log(data)
				}
			});			
    	}
    })
	
}
function delCustomer(list){  //list为删除的消息id列表 
	if(list.length==0){
		layer.msg('未选择删除客户！')
		return;
	}
	layer.msg('确定要删除吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/customer/batchDelete",
				data:{
					userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
					idStr:JSON.stringify(list)
				},
				success:function(data){
					if(JSON.parse(data).result==0){
						getPriSeaList(1)		
						layer.msg('删除成功')
					}else{
						layer.msg('删除失败')
					}
					currPageAllCheck()
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
    })
	
}
function createPlan(){    //批量加入计划
	var count = $("#count").val().replace(/\s+/g, "")
	if(count==""){
		var checkList = $('.planListItem .icon-choosehandle')
		if(checkList.length==0){
			layer.msg('未选择客户')
			return;
		}
		var lst = []
		$.each(checkList, function(index,val) {
			lst.push(parseInt($(val).attr('msgid')))
		});
		newPlan(lst,'check','')   //判断check数量是否为0
	}else{
		if(!/^[0-9]+$/.test(count)){
			layer.msg('请输入数字'); 
		}else{
			var lt = []
			lt.push(parseInt(count))
			newPlan(lt,'count','')
		}
	}
	
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
function subimtBtn() { 
	var loading = layer.load(2, {shade: [0.5,'#000'],scrollbar: false,});
 	var form = $("#klFile");   //选择from
 	var options = {  
 		url:commonUrl+"/tmk-bot/customer/batchInsert", 
 		type:'post',
 		success:function(data){  
 			layer.closeAll()
 	  		if(JSON.parse(data).result==0){
 	  			getPriSeaList(1)
   	  			var res = JSON.parse(data)	 
   	  			
   	  			var msg = '导入数据成功！文件中有数据'+res.total+'条,实际导入数据'+res.insertCount+'条，有'+res.haveCount+'条重复和错误数据没有导入'
   	  			var tpEle  = "<div>"
					tpEle += "<span style='font-size: 0.24rem;line-height: 0.6rem;float: left;padding: 0 0.2rem;'>"+ msg +"</span>"
	    			tpEle += "<div class='btnGroup1'>"
	    			tpEle += "<button id='qx00' class='grayBg'>取消</button>"
	    			tpEle += "<button id='success00' class='blueBg'>立即设置任务</button>"
	    			tpEle += "</div></div>"
	    	    
				var singleUp = layer.open({
  					type: 1,
  					title: "提示",
  					closeBtn: 1,
  					shadeClose: false,
  					scrollbar: false,
  					move: false,
   					area: ['5.2rem', '2.6rem'],
  					content: tpEle
				});
				$('#qx00').click(function(){
					layer.closeAll()
				}) 
				$('#success00').click(function(){
					newPlan(res.customerIdList,'check',res.batchNo)
				})  
   	  			
				
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
function showFileName(that){
	$('#filesName').text(that.files[0].name)
}
function singleTemplate(){    //单个模板弹窗		
	var tpEle  = "<div class='singleMsgBox'>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-shouji1'></i>"
	    tpEle += "<input maxlength='11' type='' name='' id='singlePhone' value='' placeholder='请输入手机号'/>"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-yonghu'></i>"
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
	    tpEle += "<button onclick='uploadSingle()' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "单个导入",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '6rem'],
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
				getPriSeaList(1)
				var customerIdList = []
				customerIdList.push(res.result)
				
				var tpEle  = "<div>"
					tpEle += "<span style='font-size: 0.24rem;margin-left: 0.3rem;line-height: 0.6rem;'>数据导入成功</span>"
	    			tpEle += "<div class='btnGroup1'>"
	    			tpEle += "<button id='qx00' class='grayBg'>取消</button>"
	    			tpEle += "<button id='success00' class='blueBg'>立即设置任务</button>"
	    			tpEle += "</div></div>"
	    	    
				var singleUp = layer.open({
  					type: 1,
  					title: "提示",
  					closeBtn: 1,
  					shadeClose: false,
  					scrollbar: false,
  					move: false,
   					area: ['5.2rem', '2.6rem'],
  					content: tpEle
				});
				$('#qx00').click(function(){
					layer.closeAll()
				}) 
				$('#success00').click(function(){
					newPlan(customerIdList,'check','')
				})  
				
			}else if(res.code==1){
				layer.msg('该电话号码已存在系统')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function cancelSingle(index){
	layer.close(index);
}
function getCalllog(calllogid,types,that){
//	console.log($(that).attr('callStatus'),$(that).attr('planStatus'))
//	if($(that).attr('callStatus')!=='1'){
//		layer.msg('没有相关记录')
//		return;
//	}
	if($(that).attr('planStatus')!=='2'){
		layer.msg('没有相关记录')
		return;
	}
	var calls = {
			userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			customerId:calllogid
		}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/callrecord/findLastCallRecordInfo",
		data:calls,
		success:function(data){
			var res = JSON.parse(data)
			console.log(res)
//			getCallDetails(res.callRecordId)
			if(types==0){
				checkCallLLog(calls,res)
			}else if(types==1){
				visitLog(calls,res,$(that).attr('planId'))
			}			
		},
		error:function(data){
			console.log(data)
		}
	});
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
function checkCallLLog(userId,uploadid){	//通话记录弹窗
	getCallDetails(uploadid.callRecordId)
	var tpEle  = "<div class='callLogBox'>"
	    tpEle += "<div class='callLogMsgBox'>"
	    tpEle += "<div class='callLogMsgItem'><label>计划名称：</label><span>"+uploadid.planName+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>信号强度：</label><span>"+uploadid.callSignal+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>呼叫日期：</label><span>"+uploadid.datetime.split(' ')[0]+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>呼叫时长：</label><span>"+formatSeconds(uploadid.durationTime)+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>呼叫行业：</label><span>"+uploadid.projectName+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>意向评定：</label><span>"+getGrade(uploadid.customerGrade)+"</span></div>"
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
	    	tpEle += "<a onclick='exportCallRecordDetail("+uploadid.callRecordId+")' class='callLogExportBtn'><i class='iconfont icon-tubiao05'></i> 导出</a>"
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

function alertEr(){
	layer.msg('没有数据，无法导出')
}
function visitLog(userId,uploadid,planId){  //拜访	
	getCallDetails(uploadid.callRecordId)
	var tpEle  = "<div class='callLogBox'>"
	    tpEle += "<div class='visitMsgBox'>"
	    tpEle += "<div class='callLogMsgItem'><label>计划名称：</label><span>"+uploadid.planName+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>意向评定：</label><span>"+getGrade(uploadid.customerGrade)+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>呼叫日期：</label><span>"+uploadid.datetime+"</span></div>"
	    tpEle += "<div class='callLogMsgItem'><label>呼叫时长：</label><span>"+formatSeconds(uploadid.durationTime)+"</span></div>"
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
	    tpEle += "<label>修改标签:</label>"
	    
	    for (var i=1;i<7;i++) {
	    	var bgs = i == uploadid.customerGrade ? 'checkedBg' : 'unCheckedBg'
	    	tpEle += "<span><b grade='"+i+"' onclick='changeLabel(this)' class='"+bgs+"'></b> <span>"+getGrade(i)+"</span></span>"
	    }
//	    tpEle += "<span><b grade='1' onclick='changeLabel(this)' class='checkedBg'></b> <span>A</span></span>"
//	    tpEle += "<span><b grade='2' onclick='changeLabel(this)' class='unCheckedBg'></b> <span>B</span></span>"
//	    tpEle += "<span><b grade='3' onclick='changeLabel(this)' class='unCheckedBg'></b> <span>C</span></span>"
//	    tpEle += "<span><b grade='4' onclick='changeLabel(this)' class='unCheckedBg'></b> <span>D</span></span>"
//	    tpEle += "<span><b grade='5' onclick='changeLabel(this)' class='unCheckedBg'></b> <span>E</span></span>"
//	    tpEle += "<span><b grade='6' onclick='changeLabel(this)' class='unCheckedBg'></b> <span>F</span></span>"
	    
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelVisitLog' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='addVisitNote("+userId.customerId+","+planId+")' class='blueBg'>确认</button>"
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
function addVisitNote(customerId,planId){
//	console.log(planId)
	var visits = {
		userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		customerId:customerId,
		visitDetails: $('#visitNote').val().replace(/\s+/g, ""),
		grade:$('.labelBox .checkedBg').attr('grade'),
		planId: planId
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
    			getVisitList(1)    
    			getPriSeaList(1)
			}else{
				layer.msg('跟进失败')
			}
		},
		error:function(data){
			console.log(data)  
		}
	});
}
function changeLabel(that){
	$(that).toggleClass('checkedBg').toggleClass('unCheckedBg')
	$(that).parent().siblings().children('b').removeClass('checkedBg').addClass('unCheckedBg')
}
function changeService(that){
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}
function changeService0(that){
	$(that).toggleClass('icon-fangxingweixuanzhong').toggleClass('icon-30xuanzhongfangxingfill')
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
	
//	var portlist = JSON.parse($(that).attr('portList'))
	
//	var callStr = "<span class='portItem'><span>全选</span><i onclick='allCheckPort(this)' class='iconfont icon-xuanze'></i></span>"
//	var transferStr = ''
	
//	$.each(portlist,function(index,val){
//		if(val.type==1){
//			callStr += "<span class='portItem'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//		}else{
//			transferStr += "<span class='portItem'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//		}
//	})
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
//	var portlist = first.portList
	
//	var callStr = "<span class='portItem'><span>全选</span><i onclick='allCheckPort(this)' class='iconfont icon-xuanze'></i></span>"
//	var transferStr = ''
//	$.each(portlist,function(index,val){
//		if(val.type==1){
//			callStr += "<span class='portItem'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//		}else{
//			transferStr += "<span class='portItem'><span>"+val.port+"</span><i reg='"+val.reg+"' type='"+val.port+"' onclick='changeThisClass(this)' class='iconfont icon-xuanze'></i></span>"
//		}
//	})
	
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
function changeThisClass(that){
	if($(that).attr('reg')!=='REGISTER_OK'){
		layer.msg('该端口存在异常！')
		return;
	}
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
}
function showBusUl(e){
	e = e || window.event
	e.stopPropagation();
	$(".businessUl").css('display','none')
	$("#businessUl").css('display','block')
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
		tpEle += "<input type='text' class='planName' id='planName' value='' maxlength='20' placeholder='请填写计划名称' />"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	
	    tpEle += "<div class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'><span class='important'>*</span>话术模板:</strong>"
	    tpEle += "<div class='addPlansContent'>"
	    tpEle += "<div class='businessBox'>"
	    tpEle += "<p class='rela'><span proid='' class='busName' id='busName' onclick='showBusUl(event)'>请选择模板</span> <i class='iconfont icon-xiala xialas'></i></p>"
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
	    
	    tpEle += "<div style='display:none' class='addPlansItem'>"
	    tpEle += "<strong class='addPlansLabel'>转接端口:</strong>"
	    tpEle += "<div id='portBox_sec' class='addPlansContent'>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='height: 0.4rem;' class='addPlansItem'>"
	    tpEle += "<strong style='line-height: 0.4rem;' class='addPlansLabel'>是否转接人工客服:</strong>"
	    tpEle += "<div style='line-height:0.4rem' class='addPlansContent isTransfer'>"
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
    		surePlan(list,type,batchNo)
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
function surePlan(list,type,batchNo){  
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
	
//	var portList_sec = []
//	$.each($('#portBox_sec .icon-choosehandle'), function(index,value) {
//		portList_sec.push($(value).attr('type'))
//	});
//	if(failGatewayNumList.length!==0){
//		layer.msg('网关信息无效，请联系平台')
//		return;
//	}
	if(type=='check'){
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
					getPriSeaList(1)
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
				currPageAllCheck()
			},
			error:function(data){
				alreadyAdd = true;
				console.log(data)
			}
		});
	}else if(type=='count'){
		var count = list[0]
		var obj = {
			userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			excuteTimeStr:$('#startPlan').val(),
			projectId:$("#busName").attr('proid'),
			timeStr:JSON.stringify(lts),
			isInterrupt:$('.isInterrupt .blueBg').attr('dt'),
			transferGrade:transfer,
			isTransfer:$('.isTransfer .blueBg').attr('dt'),
			isSendSMS:$('.isMessage .blueBg').attr('dt'),
			count:count,
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
			layer.msg('未选择拨打时间')
			return
		}
		if(obj.excuteTimeStr !== getNowTime().split(' ')[0]){
			if(lts.length ==0){
				obj.timeStr = JSON.stringify([9,10,11,12,13,14,15,16,17,18,19,20,21])
			}	
		}
		alreadyAdd = false;
		loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/plan/addManyPlan",
			data:obj,
			success:function(data){
				layer.close(loadings)
				if(JSON.parse(data).result==0){
					getPriSeaList(1)
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
				currPageAllCheck()
			},
			error:function(data){
				alreadyAdd = true;
				console.log(data)
			}
		});
	}
}

function getThisTxt(that){
	$(that).parent().prev().children('span').text($(that).text())
	$(that).parent().prev().children('span').attr('type',$(that).attr('type'))
	$(that).parent().css('display','none')
}
function showSelect(event,that){
	event.stopPropagation()
	$('.selectBoxUl').css('display','none')
	$(that).next().css('display','block')
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
	
	if($(player).attr('src')==''){
		layer.msg('未获取到语音文件')
		return;
	}
		
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
function trigTab(e){
	var index = $(e.target).index()
	if(index==0){
		window.location.href = '/phoneRobot/html/index.html?tabs=0'

	}else if(index==1){
		window.location.href = '/phoneRobot/html/index.html?tabs=1'
  
	}else if(index==2){
		window.location.href = '/phoneRobot/html/index.html?tabs=2'
	}
}
function cleanPriSeaBy(){
	$('#planStatus').text('全部').attr('type',0)  
	$('#callStatus').text('全部').attr('type',0)
	$('#grade').text('全部').attr('type',0)
	$('#start,#end').val('')
//	getPriSeaList(1)
}
function cleanCallLog(){
	$('#keywords').val('')
	$('#seconds').text('不限时长').attr('type','0-10000')  
	$('#startA').val('')
	$('#endA').val('')
	$('.selRowItem').find('.iconfont').removeClass('icon-30xuanzhongfangxingfill').addClass('icon-fangxingweixuanzhong')
	$('#startA,#endA').val('')
	getCallLogList(1) 
}
function protocol(e){
	$(e.target).toggleClass('icon-30xuanzhongfangxingfill').toggleClass('icon-fangxingweixuanzhong')
}
function getCallLogList(page){
	var calllogObj = {
		'id':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'page':page,
		'per':10
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/callrecord/list",
		data:calllogObj,
		success:function(data){
			var res = JSON.parse(data)
			console.log(res)
			var calllogStr = ""
			if(res.code!==0){
//				layer.msg(res.result) 
				return
			}
			if(res.data.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#calllogList').html(nomess)
				return;
			}
			$.each(res.data.list, function(index,value) {
				calllogStr += "<li class='calllogListItem'>"
				calllogStr += "<span class='flex3'><i multid='"+value.id+"' onclick='checkThisBtn(event)' class='iconfont icon-xuanze justIcon02'></i></span>"
				calllogStr += "<span class='flex8'>"+value.planName+"</span>"
				calllogStr += "<span class='flex8 flexJust hei32'><span>"+value.customerPhone+"</span><br /><span>"+value.customerName+"</span></span>"
				calllogStr += "<span class='flex3'>"+getGrade(value.customerGrade)+"</span>"
				calllogStr += "<span class='flex3'>"+callStatus(value.status)+"</span>"
				calllogStr += "<span class='flex8'>"+value.projectName+"</span>"
				calllogStr += "<span class='flex3'>"+formatSeconds(value.durationTime)+"</span>"
				calllogStr += "<span class='flex8 flexJust hei32'><span>"+value.datetime.split(' ')[0]+"</span><br /><span>"+value.datetime.split(' ')[1].split('.')[0]+"</span></span>"
				calllogStr += "<span><audio src='"+value.fileID+"' preload='preload'></audio></span>"
				calllogStr += "<span class='flex8'><span onclick='showCalllog("+JSON.stringify(value)+",this)' class='cmtOp'>详情</span><span callStatus='"+value.status+"' logid='"+value.id+"' type='export' onclick='batchSingle(this)' class='cmtOp''>导出</span></span>"
				calllogStr += "</li>"
			});
			$('#calllogList').html(calllogStr)
			
			audiojs.events.ready(function() {
        		audiojs.createAll();
    		});
			
			$('#paginationCalllog').paging({
				pageNo:page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize: res.data.count,
				callback: function(current) {
					getCallLogList(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function batchSingle(that){        //导出单个
	if($(that).attr('callStatus') !== '1'){
		layer.msg('暂无通话详情,无法导出')
		return;
	}
	var list = []
	list.push($(that).attr('logid'))
	if($(that).attr('type')=='export'){
		downloadFile(list)
	}else{
		deleteCalllog(list)
	}
}
function batchMult(type){    //导出
	var list = []
 	$('.calllogListItem .icon-choosehandle').each(function(){
 		list.push(parseInt($(this).attr('multid')))
 	})
 	if(list.length==0){
		layer.msg('未选择数据')
		return
	}
 	if(type=='export'){
 		downloadFile(JSON.stringify(list))
	}else{
		deleteCalllog(list)
	}
}
function batchMultBy(that){    //导出全部
//	var listStr = '['+$(that).attr('idlist')+']'
//	if(JSON.parse(listStr).length==0){
//		layer.msg('没有符合条件的结果')
//		return
//	}
	var listStr = "[]"
	downloadFile(listStr)
}

function downloadFile(idList) {  // 创建from表单 
	if(JSON.parse(idList).length>5000){
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
	
	var levelList = []
	var levelsEle = $('.selRowItem .icon-30xuanzhongfangxingfill')
	levelsEle.each(function(index,value){
		levelList.push(parseInt($(value).attr('level')))
	})
	var reLeves = ''
	if(levelList.length!==0){
		reLeves = JSON.stringify(levelList)
	}
	var input3 = $("<input>");
	input3.attr("type", "hidden");
	input3.attr("name", "levels");
	input3.attr("value",reLeves);
	
	var input4 = $("<input>");
	input4.attr("type", "hidden");
	input4.attr("name", "param");
	input4.attr("value", $('#keywords').val().replace(/\s+/g, ""));
	
	var sc = $('#seconds').attr('type').split('-')
	
	var input5 = $("<input>");
	input5.attr("type", "hidden");
	input5.attr("name", "startSecond");
	input5.attr("value", parseInt(sc[0]));
	
	var input6 = $("<input>");
	input6.attr("type", "hidden");
	input6.attr("name", "endSecond");
	input6.attr("value", parseInt(sc[1]));
	
	var input7 = $("<input>");
	input7.attr("type", "hidden");
	input7.attr("name", "startDate");
	input7.attr("value", $('#startA').val());
	
	var input8 = $("<input>");
	input8.attr("type", "hidden");
	input8.attr("name", "endDate");
	input8.attr("value", $('#endA').val());
	
	form.append(input1);
	form.append(input2);
	form.append(input3);
	form.append(input4);
	form.append(input5);
	form.append(input6);
	form.append(input7);
	form.append(input8);
	$("body").append(form); //将表单放置在web中  

	form.submit(); //表单提交   
}

function exportCalllog(list){
	window.location.href = commonUrl+"/tmk-bot/callrecord/export?id="+JSON.parse(sessionStorage.getItem('loginMsg')).id+"&idList="+JSON.stringify(list)
}
function deleteCalllog(list){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/callrecord/batchDelete",
		data:{
			id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			callRecordIDs:JSON.stringify(list)
		},
		success:function(data){
			if(JSON.parse(data).code==0){
				getCallLogList(1)
				layer.msg('删除成功！')
			}else{
				layer.msg('删除失败！')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getCallLogByCondition(page,type){
	var levelList = []
	var levelsEle = $('.selRowItem .icon-30xuanzhongfangxingfill')
	levelsEle.each(function(index,value){
		levelList.push(parseInt($(value).attr('level')))
	})
	var reLeves = ''
	if(levelList.length!==0){
		reLeves = JSON.stringify(levelList)
	}
	var calllogObj = {
		'userID':JSON.parse(sessionStorage.getItem('loginMsg')).id,
//		startSecond:0,
//		endSecond:$('#seconds').text(),
		'startDate':$('#startA').val(),
		'endDate':$('#endA').val(),
		'param':'',         //关键词  手机号
		'levels': reLeves,        //意向list		
		'page':page,
		'per':10
	}
	var sc = $('#seconds').attr('type').split('-')
	calllogObj.startSecond = parseInt(sc[0])
	calllogObj.endSecond = parseInt(sc[1])
//	console.log(calllogObj)
	if(type == 'ex'){
		calllogObj.param = $('#keywords').val().replace(/\s+/g, "")
		calllogObj.levels = ''
		calllogObj.startDate = ''
		calllogObj.endDate = ''
		calllogObj.startSecond = 0
		calllogObj.endSecond = 10000
	}
	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/callrecord/multiple",
		data:calllogObj,
		success:function(data){
			var res = JSON.parse(data)
			console.log(res)
			if(res.code!==0){
				layer.msg(res.result)
//				return;
			}
			if(res.data.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#calllogList').html(nomess)
				return;
			}
			var calllogStr = ""
			$.each(res.data.list, function(index,value) {
				calllogStr += "<li class='calllogListItem'>"
				calllogStr += "<span class='flex3'><i multid='"+value.id+"' onclick='checkThisBtn(event)' class='iconfont icon-xuanze justIcon02'></i></span>"
				calllogStr += "<span class='flex8'>"+value.planName+"</span>"
				calllogStr += "<span class='flex8 flexJust hei32'><span>"+value.customerPhone+"</span><br /><span>"+value.customerName+"</span></span>"
				calllogStr += "<span class='flex3'>"+getGrade(value.customerGrade)+"</span>"
				calllogStr += "<span class='flex3'>"+callStatus(value.status)+"</span>"
				calllogStr += "<span class='flex8'>"+value.projectName+"</span>"
				calllogStr += "<span class='flex3'>"+formatSeconds(value.durationTime)+"</span>"
				calllogStr += "<span class='flex8 flexJust hei32'><span>"+value.datetime.split(' ')[0]+"</span><br /><span>"+value.datetime.split(' ')[1].split('.')[0]+"</span></span>"
				calllogStr += "<span><audio src='"+value.fileID+"' preload='preload'></audio></span>"
				calllogStr += "<span class='flex8'><span onclick='showCalllog("+JSON.stringify(value)+",this)' class='cmtOp'>详情</span><span callStatus='"+value.status+"' logid='"+value.id+"' type='export' onclick='batchSingle(this)' class='cmtOp''>导出</span></span>"
				calllogStr += "</li>"
			});
			$('#calllogList').html(calllogStr)
			audiojs.events.ready(function() {
        		audiojs.createAll();
    		});
			$('#paginationCalllog').paging({
				pageNo:page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize: res.data.count,
				callback: function(current) {
					if(type == 'ex'){
						getCallLogByCondition(current,type)
					}else{
						getCallLogByCondition(current)
					}
				}
			});
			$("#ids").attr('idlist',res.data.idList).css('display','block')
		},
		error:function(data){
			console.log(data)
		}
	});
}
function showCalllog(msgStr,that){    //通话记录tab查看通话详情 
	if(msgStr.status !== 1){
		layer.msg('未接通用户，暂无通话详情')
		return;
	}
	msgStr.phone='暂无'
	var calls = {
		userId:JSON.parse(sessionStorage.getItem('loginMsg')).id
	}
	msgStr.callRecordId = msgStr.id
	checkCallLLog(calls,msgStr)
}
function getVisitList(page){
	var visitObj = {
		'userId':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'curPage':page
	}	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/visit/findVisitList",
		data:visitObj,
		success:function(data){
			var res = JSON.parse(data)
			if(res.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#visitList').html(nomess)
				return;
			}
			var visitStr = ""
			$.each(res.list, function(index,value) {
				visitStr += "<li class='visitListItem'>"
				visitStr += "<span class='flex4'>"+(index+1)+"</span>"
				visitStr += "<span class='flex4'>"+value.customerPhone+"</span>"
				visitStr += "<span class='flex4'>"+value.customerName+"</span>"
				visitStr += "<span class='flex4'>"+value.company+"</span>	"
				
				
				visitStr += "<span class='flex4'>"+getGrade(value.grade)+"</span>"
				visitStr += "<span class='flex10 lineH92'>"+new Date(value.visitTime).toLocaleString()+"</span>"
				visitStr += "<span class='flex4'>"+value.visitWay+"</span>"
				visitStr += "<span title='"+value.visitDetails+"' class='flex8'>"+value.visitDetails+"</span>"
				visitStr += "</li>"
			});
			$('#visitList').html(visitStr)
			$('#paginationVisit').paging({
				pageNo:page,
				totalPage: Math.ceil(res.total/10),
				totalSize: res.total,
				callback: function(current) {
					getVisitList(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getPriSeaList(page,type){   //type为 ex时 为手机号搜索 其他搜索条件忽略
	var cusObj = {
		'userId':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'curPage':page,
//		'planStatus':$('#planStatus').attr('type'), //计划状态
//		'callEndTimeStr':'',       //通话结束时间
//		'callStartTimeStr':'',     //通话开始时间
		'endTimeStr':$('#end').val()==''?$('#end').val():$('#end').val()+' 23:59:59',           //添加结束时间
		'startTimeStr':$('#start').val()==''?$('#start').val():$('#start').val()+' 00:00:00',        //添加开始时间		
//		'callStatus':$('#callStatus').attr('type'),           //呼叫状态
//		'grade':$('#grade').attr('type'),              //意向标签   
		'customerPhone': '0'
//		'customerPhone':$('#customerPhone').val().replace(/\s+/g, "")      //客户手机号
	}	
	if(type == 'ex'){
		cusObj.customerPhone = $('#customerPhone').val().replace(/\s+/g, "")
//		cusObj.planStatus = '0'
		cusObj.startTimeStr = ''
		cusObj.endTimeStr = ''
	}
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({  
		type:"post",
		url:commonUrl+"/tmk-bot/customer/findCustomer",
		data:cusObj,
		success:function(data){
			layer.close(loadings)
			var res = JSON.parse(data)
			var visitStr = ""
			if(res.customerList.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#planList').html(nomess)
				return;
			}
			$.each(res.customerList, function(index,value) {
				visitStr += "<li class='planListItem'>"
				visitStr += "<span class='flex3'><i callStatus='"+value.planStatus+"' msgid='"+value.id+"' onclick='checkThisBtn0(this)' class='iconfont icon-xuanze justIcon02'></i></span>"
				visitStr += "<span class='flex3'>"+((currPage - 1)*100 + index + 1 )+"</span>"
				visitStr += "<span class='flexJust'><span>"+value.customerPhone+"</span><br /><span>"+value.customerName+"</span></span>"
//				visitStr += "<span class='flexJust'><span>"+value.customerPhone+"</span><span class='status00'>"+planStatus(value.planStatus)+"</span><br /><span>"+value.customerName+"</span></span>"
//				visitStr += "<span class='flex3'>"+callStatus(value.callStatus)+"</span>"
//				visitStr += "<span class='flex3'>"+getGrade(value.grade)+"</span>"			
//				visitStr += "<span class='flex8 flexJust0'><span class='companyName'>【"+value.projectName+"】</span><br /><span class='cmtGray'>"+value.datetime+"</span><br /><span class='cmtGray'>时长："+formatSeconds(value.durationTime)+"</span></span>"
//				visitStr += "<span style='line-height:0.24rem;font-size:0.16rem' class='flex60'><span style='margin-top:0.12rem'>"+value.excuteTime.split(' ')[0]+"</span><br /><span>"+value.excuteTime.split(' ')[1]+"</span></span>"
				visitStr += "<span style='line-height:0.24rem;font-size:0.16rem' class='flex60'><span style='margin-top:0.12rem'>"+value.addTime.split(' ')[0]+"</span><br /><span>"+value.addTime.split(' ')[1]+"</span></span>"
//              visitStr += "<span class='flex4'>"+value.callCount+"</span>"
//              visitStr += "<span class='flex4'>"+value.visitTime.split(' ')[0]+"</span>"
//              visitStr += "<span class='flex4'>"+value.account+"</span>"
//              visitStr += "<span class='flex8 flexJust'><span callStatus='"+value.callStatus+"' planStatus='"+value.planStatus+"' onclick='getCalllog("+value.id+",0,this)' class='cmtBtn'>通话记录</span><br /><span planStatus='"+value.planStatus+"' msgid='"+value.id+"' onclick='newSinglePlan(this)' class='cmtOp'>计划</span><span planId='"+value.planId+"' planStatus='"+value.planStatus+"' callStatus='"+value.callStatus+"'  onclick='getCalllog("+value.id+",1,this)' class='cmtOp'>拜访</span><span planStatus='"+value.planStatus+"' onclick='delSHItem(this,"+value.id+")' class='cmtOp cmtRed'>删除</span></span>"
                visitStr += "<span style='line-height:0.6rem' class='flex8 flexJust'><span planStatus='"+value.planStatus+"' msgid='"+value.id+"' onclick='newSinglePlan(this)' class='cmtOp'>设置任务</span><span planStatus='"+value.planStatus+"' onclick='delSHItem(this,"+value.id+")' class='cmtOp cmtRed'>删除</span></span>"
				visitStr += "</li>"
			});
			$('#planList').html(visitStr)
			$("#page").paging({
				pageNo:page,
				totalPage: Math.ceil(res.total/100),
				totalSize: res.total,
				callback: function(current) {
					currPage = current
					if(type == 'ex'){
						getPriSeaList(current,type)
					}else{
						getPriSeaList(current)
					}
					
					$('#seaAllcheck span').text('全选')
					$('#seaAllcheck i').removeClass('icon-choosehandle').addClass('icon-xuanze')
				}
			})
			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function checkThisBtn0(that){   // cusAllCheck  paginationCalllog 
	
	if($(that).attr('callStatus')==1){
		layer.msg('该用户正在计划中')
	}else{
		$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')	
	}
	changeRed()
}
function newSinglePlan(that){   //正在计划中的不能再添加计划   
	if(allProject.length == 0){
		layer.msg('未获取到模板，请稍后再试！')
		return;
	}
//	if($(that).attr('planStatus')==1){
//		layer.msg('该用户正在计划中，不可再添加计划')
//		return;
//	}
	var list=[];
	list.push(parseInt($(that).attr('msgid')))
	newPlan(list,'check')
}  
function enterSearch(e) {
    if(e.keyCode == 13) {
        if($(e.target).attr('type')=='sea'){
        	getPriSeaList(1,'ex')
        }else{
        	getCallLogByCondition(1,'ex')
        }
    }
}
function noOpen(){
	layer.msg('打断功能暂未开放')
}
function currPageAllCheck(){
	$('#currPageAllCheck').text('全选').prev().removeClass('icon-choosehandle').addClass('icon-xuanze')
	$(".norBtn").css({"background":"none","border":"1px solid white"})
}
function initBalanceMessage(){
	$.ajax({  
		type:"post",
		url:commonUrl+"/tmk-bot/finance/initBalanceMessage",
		data:{
			userId: JSON.parse(sessionStorage.getItem('loginMsg')).id
		},
		success:function(data){
			var res = JSON.parse(data)
//			layer.msg(res.result, {
//  			time: 20000, 
//  			btn: ['确定'],
//  			yes: function (index) {
//  				layer.close(index)
//			
//  			}
// 			});	
		},
		error:function(data){
			console.log(data)
		}
	});
}
function changeRed () {
	if($('.planListItem .icon-choosehandle').length == 0){
		$(".norBtn").css({"background":"none","border":"1px solid white"})
	}else{
		$(".norBtn:eq(0)").css({"background":"#fc9a00","border":"1px solid #fc9a00"})
		$(".norBtn:eq(1)").css({"background":"#d56a6a","border":"1px solid #d56a6a"})
	}
}
function isChange (that){
	if($(that).val().replace(/\s+/g, "") == ""){
		$(".norBtn").css({"background":"none","border":"1px solid white"})
	}else{
		$(".norBtn:eq(0)").css({"background":"#fc9a00","border":"1px solid #fc9a00"})
		$(".norBtn:eq(1)").css({"background":"#d56a6a","border":"1px solid #d56a6a"})
	}
}
