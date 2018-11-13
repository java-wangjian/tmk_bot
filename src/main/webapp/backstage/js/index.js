var adminId = sessionStorage.getItem('adminId')
var curPage = 1;
function init(){
	findUserList(curPage)
	$('#curAccount').text(sessionStorage.getItem('adminAccount'))
}
function findUserList(page,isSearch){
	if(!adminId){
		window.location.href='./login.html'
	}
	var getList = {
		'adminId':adminId,
		'curPage':page
	}
	if(isSearch=='search'){
		getList.account=$('#keyAccount').val()
	}else{
		getList.account=''
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/findUserList",
		data:getList,
		success:function(data){
			var res = JSON.parse(data)
			
			var tempStr = ''										
			$.each(res.list, function(index,value) {				
				tempStr += "<li class='cusItem'>"
				tempStr += "<span class='tit03'>"+value.id+"</span>"
				tempStr += "<span class='tit01'>"+value.userAccount+"</span>"
				tempStr += "<span class='tit03'>"+value.company+"</span>"
				var status = value.isActive==0?'已关闭':value.isActive==1?'使用中':value.isActive==2?'未激活':'已到期'
				tempStr += "<span class='tit03 stas'>"+status+"</span>"
				var mou = []
				for (var i=0;i<value.project.length;i++) {
					mou.push(value.project[i].projectName)
				}
				var ststus = value.project[0] ? value.project[0].switchStatus ==1? 'addIcon' : '' : ''
				tempStr += "<span class='tit02 "+ststus+"'>"+mou.join(',')+"</span>"
				tempStr += "<span class='tit01'>"+value.createTime.split(' ')[0]+"</span>"
				tempStr += "<span class='tit01'>"+value.validTime.split(' ')[0]+"</span>"
				tempStr += "<span class='tit03'>"+value.adminAccount+"</span>"
				if(value.isStart==1){
					tempStr += "<span class='tit03'><img userid='"+value.id+"' onclick='togSwitch(this)' class='swImg' src='../images/open.png'/></span>"
				}else{
					tempStr += "<span class='tit03'><img userid='"+value.id+"' onclick='togSwitch(this)' class='swImg' src='../images/close.png'/></span>"
				}
				tempStr += "<span><a class='editBtn' href='./userDetail.html?userId="+value.id+"' >编辑</a></span>"
//				tempStr += "<span><a class='editBtn' href='./editAccount.html?userId="+value.id+"' >编辑</a><a href='./recharge.html?userId="+value.id+"' class='editBtn'>充值</a></span>"
//				tempStr += "<span><a href='./editAccount.html?userId="+value.id+"' ><span class='tit03 editBtn'>编辑</span></a><b class='tit03 editBtn'>充值</b></span>"
				
				
//				tempStr += "<span userid='"+value.id+"' company='"+value.company+"' project='[]' class='tit03 editBtn' onclick='thisVoiceAndKw(this)'>编辑</span>"
				tempStr += "</li>"
			});
            $('#cusUl').html(tempStr)
            $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.total/10),
				totalSize: res.total,				
				callback: function(current) {
					curPage = current
					findUserList(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}


//function caculateCost(){
//	if(!isNumber($('#money').val())){
//		layer.msg('充值金额不是数字')
//		return
//	}
//	if(!isNumber($('#price').val()) || $('#price').val() == '0'){
//		layer.msg('单价必须是不为零的数字')
//		return
//	}
//	$('#finTime').text(parseFloat($('#money').val())/parseFloat($('#price').val()))
//}
//function recharge(){
//	if(!isNumber($('#money').val())){
//		layer.msg('充值金额不是数字')
//		return
//	}
//	if(!isNumber($('#price').val()) || $('#price').val() == '0'){
//		layer.msg('单价必须是不为零的数字')
//		return
//	}
//	var finTime = parseFloat($('#money').val())/parseFloat($('#price').val())
//	layer.msg('确定充值（充值时长'+finTime+'分钟）？', {
//  	time: 20000, 
//  	btn: ['确定', '取消'], 
//  	yes: function (index) { 
//  		
//
//  	
//  	}
// 	});
//}

function isNumber(val){
    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if(regPos.test(val) || regNeg.test(val)){
        return true;
    }else{
        return false;
    }

}

function cancelSingle(index){
	layer.close(index);
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
		userId: tar.attr('userid'),
		isActive: sta
	}      
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/active",
		data:chStatus,
		success:function(data){
			findUserList(curPage)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getAccount(){
	var loading = layer.load(1, {shade: [0.5,'#000'],scrollbar: false,});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/getAccount",
		data:{'adminId':adminId},
		success:function(data){
			layer.close(loading);
			var res = JSON.parse(data)
           	createCus(res.result)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function createCus(account){    //新建账号弹窗
	var tpEle  = "<div class='addMsgWrap'><div id='autoSec' class='autoSec'><div class='addRow'>"
	    tpEle += "<label class='addItemTit'>账户名称：</label><span id='newAcc'>"+account+"</span>"
	    tpEle += "</div>"
	    tpEle += "<div style='margin-top: 0.2rem;' class='addRow'>"
	    tpEle += "<label class='addItemTit'>到期时间：</label><input value='' readonly='readonly' type='text' id='startC' placeholder='点击选择到期时间'>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>企业名称：</label><input maxlength='15' type='' name='' id='newCompanyName' value='' />"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>添加项目：</label><input maxlength='15' type='' name='' id='projectName' value='' />"
	    tpEle += "</div>" 
	    
//	    tpEle += "<div class='addRow'>"
//	    tpEle += "<label class='addItemTit'>添加项目：</label><input maxlength='15' class='addProject' type='' name='' id='' value='' />"
//	    tpEle += "<span class='addAudioSpan'><i class='iconfont icon-icon--'></i> <span hsData='[]' class='addHsSpan' onclick='addHSWindow(this)'>添加话术录音</span></span>"
//	    tpEle += "</div>" 
	    
	    tpEle += "</div>"	    
//	    tpEle += "<div class='addRow' style='margin-top: 0.2rem'>"
//	    tpEle += "<button onclick='addMoreHs()' class='addmore'><i class='iconfont icon-tianjia'></i><span>添加更多项目和录音</span></button>"
//	    tpEle += "</div>"
	    tpEle += "<div class='addRowBtn'>"
	    tpEle += "<button id='qxCreate' class='but1'>取消</button>"
	    tpEle += "<button class='but2' onclick='confirmPro()'>增加话术</button>"
	    tpEle += "</div>"
	    tpEle += "</div>"		    
	var createBox = layer.open({
  		type: 1,
  		title: "<span class='popStyle'><i class='iconfont icon-tianjia'></i> 创建账户</span>",
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5rem'],
  		content: tpEle
	});
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})
	 $(document).ready(function(){  
    	$("#startC").datepicker({dateFormat: 'yy-mm-dd'});   
    }); 
}	
//function addMoreHs(){
//	var mores =  "<div class='addRow'>"
//	    mores += "<label class='addItemTit'>添加项目：</label><input maxlength='15' class='addProject' type='' name='' id='' value='' />"
//	    mores += "<span class='addAudioSpan'><i class='iconfont icon-icon--'></i> <span hsData='[]' class='addHsSpan' onclick='addHSWindow(this)'>添加话术录音</span></span>"
//	    mores += "</div>" 
//	$('#autoSec').append(mores)
//}
function addMoreEdit(userid){
	var mores =  "<div class='addRow'>"
	    mores += "<label class='addItemTit'>添加项目：</label><input maxlength='15' class='addProject' type='' name='' id='' value='' />"
	    mores += "<span class='addAudioSpan'><i class='iconfont icon-icon--'></i> <span hsData='[]' class='addHsSpan' userid='"+userid+"' onclick='addNewPro(this)'>添加项目</span></span>"
	    mores += "</div>" 
	$('.proList').before(mores)
}
function addNewPro(that){
	var isHaveName = false;
	var thisAllPro = []
	
	$('.proItemLi').find('span').each(function(index,val){
		thisAllPro.push(val.innerText)
	})
	var obj = {
		userId:$(that).attr('userid'),
		projectName:$(that).parent().prev().val().replace(/\s+/g, "")
	}
	if(obj.projectName==''){
		layer.msg('项目名不能为空')
		return
	}
	$.each(thisAllPro, function(index,value) {
		if(obj.projectName==value){
			isHaveName = true
		}
	});
	if(isHaveName){
		layer.msg('新建项目不能与已有项目重名！')
		return
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/addProject",
		data:obj,
		success:function(data){
			var res = JSON.parse(data)
			layer.closeAll()
			window.location.href='newpro.html?projectID='+res.data.projectID			
		},
		error:function(data){
			console.log(data)
		}
	});
	
}
function confirmPro(){	  //确定添加
	if($('#startC').val()==''){
		layer.msg('到期时间不能为空！')
		return;
	}
	var newDate = $('#startC').val()+' 00:00:00'
	var allVoiceEle = $('.addHsSpan')
	var newAccount = {
		'account':$('#newAcc').text(),
		'password':'123456',
		'company':$('#newCompanyName').val(),
		'validTime':newDate,
		'adminId':adminId,
		'projectName': $('#projectName').val().replace(/\s+/g, ""),
	}
	if(newAccount.company==''||newAccount.projectName==''){
		layer.msg('企业名和项目名都不能为空')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/addUser",
		data:newAccount,
		success:function(data){
			var res = JSON.parse(data)
			layer.closeAll()
			window.location.href='newpro.html?projectID='+res.data.projectID			
		},
		error:function(data){
			console.log(data)
		}
	});
//	
//	const loading = layer.load(1, {shade: [0.5,'#000'],scrollbar: false,});
	
}
function confirmEditPro(userid){	
	if($('#startE').val()==''){
		layer.msg('到期时间不能为空！')
		return;
	}
	
	var newDate = $('#startE').val()+' 00:00:00'
//	var voiceAndKw = [];
//	var voiceAndKwEdit = [];
//	var allAddVoiceEle = $('.addHsSpan')
//	var flag = true;
//	$.each(allAddVoiceEle, function(index,val) {
//		if($(val).parent().prev().val().replace(/\s+/g, "")==''){
//			flag = false
//		}
//	});
//	if(!flag){
//		layer.msg('项目名不能为空')
//		return
//	}
//	$.each(allAddVoiceEle, function(index,val) {
//		var proItem = {
//			"projectName":$(val).parent().prev().val(),
//			"voiceAndKw":$(val).attr('hsData')
//		}
//		voiceAndKw.push(proItem)
//	});
//	var allEditVoiceEle = $('.editicon')
//	$.each(allEditVoiceEle, function(index,val) {
//		var proItem = {
//			"id":$(val).attr('proid'),
//			"voiceAndKw":$(val).attr('hsData')
//		}
//		voiceAndKwEdit.push(proItem)
//	});
//	
	var editAccount = {
		'userId':userid,
//		'password':'123456',
		'validTime':newDate,
//		'newVoiceAndkeyword':JSON.stringify(voiceAndKw),
//		'oldVoiceAndkeyword':JSON.stringify(voiceAndKwEdit)
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/updateProject",
		data:editAccount,
		success:function(data){
			layer.closeAll()
			layer.msg('更新成功')
			findUserList(curPage)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function thisVoiceAndKw(that){  //先获取该用户下的所有项目以及项目下的语音  获取到以后赋值that attr('project')
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/findProjectByUserId",
		data:{userId:JSON.parse($(that).attr('userid')) },
		success:function(data){
			var res = JSON.parse(data)
			console.log(res)
			$(that).attr('project',JSON.stringify(data))
			editCus(that)    //获取到所有语音以后  调用编辑账号窗口
		},
		error:function(data){
			console.log(data)
		}
	});

}
function deletePro(that){  //删除成功也要更新list 否则userList不会更新
	layer.msg('确定要删除吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
      		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/project/deleteProject",
				data:{
					adminId:adminId,
					projectId:$(that).attr('proid')
				},
				success:function(data){
					if(JSON.parse(data).result==0){
						findUserList(curPage)
						$(that).parent().remove()
					}else{
						layer.msg('删除失败')
					}
					
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
   	});
}
function linkpage(that){
	var proid = $(that).attr('proid')
	window.location.href='newpro.html?projectID='+proid	
}
function editCus(that){     //编辑账号弹窗
	var oldPro = JSON.parse($(that).attr('project'))  //已经有的项目list
	var oldProStr = ''
	$.each(oldPro, function(index,value) {
		oldProStr+="<li class='proItemLi'><span>"+value.projectName+"</span><i onclick='linkpage(this)' proid='"+value.id+"' hsData='"+JSON.stringify(value.voiceAndKw)+"' class='iconfont icon-bianji editicon'></i><i onclick='deletePro(this)' proid='"+value.id+"' class='iconfont icon-shanchu deleteicon'></i></li>"
	});	
	var target = $(that).parent() 
	var newdate = target.children('span:eq(6)').text()
	var tpEle  = "<div class='addMsgWrap'><div class='autoSec0'><div class='addRow'>"
	    tpEle += "<label class='addItemTit'>账户名称：</label><span>"+target.children('span:eq(1)').text()+"</span>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>企业名称：</label><span>"+$(that).attr('company')+"</span>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>密码重置：</label><button id='"+$(that).attr('userid')+"' class='resetPwd' onclick='resetPwd(this)'>重置密码</button>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>到期时间：</label><input type='text' id='startE' readonly='readonly' value='"+newdate+"'>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRow proList'>"
	    tpEle += "<ul>"
	    tpEle += oldProStr 
	    tpEle +="</ul>"
	    tpEle += "</div>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRow' style='margin-top: 0.2rem'>"
	    tpEle += "<button onclick='addMoreEdit("+$(that).attr('userid')+")' class='addmore'><i class='iconfont icon-tianjia'></i><span>添加更多项目和录音</span></button>"
	    tpEle += "</div>"
	    tpEle += "<div class='addRowBtn'>"
	    tpEle += "<button class='but1' onclick='closeEdit()'>取消</button>"
	    tpEle += "<button class='but2'  onclick='confirmEditPro("+$(that).attr('userid')+")'>确定</button>"
	    tpEle += "</div>"
	    tpEle += "</div>"		    
	layer.open({
  		type: 1,
  		title: "<span class='popStyle'><i class='iconfont icon-tianjia'></i> 编辑账户</span>",
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5.47rem'],
  		content: tpEle
	});
	 $(document).ready(function(){  
    	$("#startE").datepicker({dateFormat: 'yy-mm-dd'});   
    }); 
}
function resetPwd(that){
	layer.msg('确定要重置密码吗？', {
    	time: 20000, //20s后自动关闭
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/admin/resetPassword",
				data:{userId:$(that).attr('id')},
				success:function(data){
					if(JSON.parse(data).result==0){layer.msg('重置密码成功');}					
				},
				error:function(data){
					console.log(data)
				}
			});
    		layer.close(index)
    	}
    });
}
function closeEdit(editWindow){
	layer.closeAll();
}
function addHSWindow(that){   //that为拿到的目标元素 并临时存下       添加文件窗口	
	var addBtnStr = "<div class='addHSItemBox'><div class='voiceBox'></div><div class='kwArea'><i class='iconfont icon-tianjia bigAddIcon'></i>"
		addBtnStr+= "<div class='inputFile'  id='container'><p class='pickfiles' id='pickfiles'></p></div>"
		addBtnStr+="</div></div>"	
	var haveVoice = JSON.parse($(that).attr('hsData'))	
	var haveVoiceStr = ''
	$.each(haveVoice, function(index,value) {
		haveVoiceStr+= "<div class='addHSItemBox hsItemBox'>"
	    haveVoiceStr+= "<div class='voiceBox'>"
	    haveVoiceStr+= "<div onclick='playVoice(this)' class='voiceImgBox'>"
	    haveVoiceStr += "<img class='playing' src='../images/voiceImg.gif' />"
	    haveVoiceStr+= "<audio class='voiceEle' src='"+value.voice+"'></audio>"
	    haveVoiceStr+= "</div>"
	    haveVoiceStr+= "<i onclick='delThisVoice(this)' class='iconfont icon-shanchu delVoice'></i>"
	    haveVoiceStr+= "</div>"
	    haveVoiceStr+= "<div class='kwArea'>"
	    haveVoiceStr+= "<textarea class='kwInArea' name='' rows='' cols=''>"+value.kws+"</textarea>"
	    haveVoiceStr+= "</div></div>"		   
	});
	var tpEl  = "<div id='uploadBox' class='uploadBox'>"	    
		tpEl += addBtnStr
		tpEl += haveVoiceStr
	    tpEl += "</div>"		    
	var saveLayer;
	saveLayer = layer.open({
  		type: 1,
  		title: "<p><span class='popStyle'><i class='iconfont icon-tianjia'></i> 编辑话术模板</span><button id='saveUpload' class='savevoice'>保存</button><button id='cancelUploadBtn' class='cancelvoice'>取消</button></p>",
  		shadeClose: false,
  		scrollbar: false,
  		closeBtn: 0,
  		move: false,
   		area: ['15.62rem', '6.94rem'],
  		content: tpEl
	});
	
	getUptoken()   //上传成功以后会调用 uploadVoice(voices)
	
	$('#cancelUploadBtn').click(function(){  //取消保存语音及关键词
		layer.close(saveLayer);
	})
	$('#saveUpload').click(function(){  // 保存语音及关键词
		saveUpload(saveLayer,that)
	})
}
function cancelUpload(saveLayer){
	layer.close(saveLayer);
}
function saveUpload(index,thatEle){
	var hsList = []
	var voAndKw = $('.hsItemBox')
	
	for(var i=0;i<voAndKw.length;i++) {
		var th = $(voAndKw[i])
		var tempObj = {
			"voice":th.find('.voiceEle').attr('src'),
			"kws":$.trim(th.find('.kwInArea').val()).replace(/\s+/g, ",").split(',')
		}
		hsList.push(tempObj)
	}
	$(thatEle).attr('hsData',JSON.stringify(hsList))
	layer.close(index);
}
function uploadVoice(voices){   //循环渲染
	var voiceItem = "";
	$.each(voices, function(index,value) {
		voiceItem = ""
		voiceItem+= "<div class='addHSItemBox hsItemBox'>"
	    voiceItem+= "<div class='voiceBox'>"
	    voiceItem+= "<div onclick='playVoice(this)' class='voiceImgBox'>"
	    voiceItem += "<img class='playing' src='../images/voiceImg.gif' />"
	    voiceItem+= "<audio class='voiceEle' src='"+value+"'></audio>"
	    voiceItem+= "</div>"
	    voiceItem+= "<span class='fileName'>"+value.split('com/')[1]+"</span><i onclick='delThisVoice(this)' class='iconfont icon-shanchu delVoice'></i>"
	    voiceItem+= "</div>"
	    voiceItem+= "<div class='kwArea'>"
	    voiceItem+= "<textarea class='kwInArea'  maxlength='100' name='' rows='' cols=''></textarea>"
	    voiceItem+= "</div></div>"		   
	});		
	
	$('#uploadBox').html($('#uploadBox').html()+voiceItem)	
	getUptoken()
}
function delThisVoice(that){  //移除本条语音
	$(that).parent().parent().remove()
}
function playVoice(that){	
	var player = $(that).children()[1]  //当前的audio元素
	var icon = $(that).children()[0]  //当前的audio元素
	var others = $('.voiceEle').not(player)  //除当前audio外	
	for (var i=0;i<others.length;i++) {
		others[i].pause()
		$(others[i]).prev().attr('src','../images/voiceImg.gif')
	}
	if(player.paused) {  
		player.currentTime = 0
        player.play();   
        $(icon).attr('src','../images/playing.gif')  //控制自身
    }else{  
        player.pause(); 
        $(icon).attr('src','../images/voiceImg.gif')  //控制自身
    }  	
}
function login(){
	var loginMsg = {
		'account': $('#account').val().replace(/\s+/g, ""),
		'password': $('#password').val().replace(/\s+/g, "")
	}
	if(loginMsg.account==''||loginMsg.password==''){
		layer.msg('账号或密码不能为空');
		return;
	}
	var loading = layer.load(1, {shade: [0.5,'#000'],scrollbar: false,});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/login/adminLogin",
		data:loginMsg,
		success:function(data){
			layer.close(loading);
			var res = JSON.parse(data)
            if(res.result==1){
            	layer.msg('账号或密码错误');
            }else{
            	sessionStorage.setItem('adminId',res.result)
            	window.location.href='./index.html'
            }			
		},
		error:function(data){
			console.log(data)
		}
	});
}