function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	unReadCount()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
	getAccList(1)
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
	changeRed()
}
function checkThisBtn(that){
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	changeRed()
}
function checkSip(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/istransport",
		data:{userId:JSON.parse(sessionStorage.getItem('loginMsg')).id},
		success:function(data){		
			if(JSON.parse(data).isany){
				addAccount()
			}else{
				layer.msg("当前未设置转接端口，请联系客服")
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function addAccount(){    //添加账户
	var tpEle  = "<div class='singleMsgBox'>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-shouji1'></i>"
	    tpEle += "<input type='' name='' id='serPhone' value='' maxlength='11'  placeholder='请输入电话号码' />"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-yonghu'></i>"
	    tpEle += "<input type='' name='' id='serName' value='' maxlength='10' placeholder='请输入姓名'/>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='display:none' class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-gerenzhongxin'></i>"
	    tpEle += "<input type='' name='' id='serAccount' maxlength='10' value='' placeholder='请输入账号'/>"
	    tpEle += "</div>"
	    tpEle += "<div style='display:none' class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-mima'></i>"
	    tpEle += "<input type='' name='' id='serPwd' value='' maxlength='10' placeholder='请输入密码'/>"
	    tpEle += "</div>"
	    tpEle += "<div style='display:none' class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-007jiqiren'></i>"
	    tpEle += "<input type='' name='' id='serPort' value='' maxlength='9' placeholder='请输入机器人端口'/>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-kefu'></i>"
	    tpEle += "<div class='xialakfBox'>"
	    tpEle += "<p id='identity' onclick='showSelectKF(event)'>客服</p><i class='iconfont icon-xiala'></i>"
	    tpEle += "<ul id='seleKfUl' class='seleKfUl'>"
	    tpEle += "<li onclick='getKFname(event,this)'>客服</li>"
	    tpEle += "</ul>"
	    tpEle += "</div><span style='color:#999;line-height: 0.3rem;margin-top: 0.2rem;display: inline-block;'>提示：外呼任务执行时，A类（有明确意向）客户立即转接人工客服</span></div>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='addServicer()' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "添加转接人工",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		cancelSingle(singleUp)
	}) 
}
function addServicer(){
	var serObj = {
		phone:$('#serPhone').val().replace(/\s+/g, ""),
		supportStaffName:$('#serName').val().replace(/\s+/g, ""),
//		account:$('#serAccount').val().replace(/\s+/g, ""),
		account:new Date().getTime(),
		password:new Date().getTime(),
//		password:$('#serPwd').val().replace(/\s+/g, ""),
		identity:$('#identity').text(),
		parentId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		company:JSON.parse(sessionStorage.getItem('loginMsg')).company,
		createTime:getNowTime(),
//		robotPort:$('#serPort').val().replace(/\s+/g, ""),
		robotPort:new Date().getTime()
	}
	if(!/^1[34578]\d{9}$/.test(serObj.phone)){
		layer.msg('手机号格式不正确')
		return;
	}
//	if(serObj.phone==''||serObj.supportStaffName==''||serObj.account==''||serObj.password==''||serObj.robotPort==''){
	if(serObj.phone==''||serObj.supportStaffName==''){
		layer.msg('客户姓名不能为空！')
		return;
	}
	if(!/^[0-9]*$/.test(serObj.robotPort)){
		layer.msg('机器人端口号只支持最多9位数字')
		return
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/addstaff",
		data:serObj,
		success:function(data){						
			if(JSON.parse(data).code==0){
				layer.closeAll()
//				getAccList(1)
				layer.msg('添加成功！')
				setTimeout(function(){
					window.location.reload()
				},2000)
			}else{
				layer.msg(JSON.parse(data).result)
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
function showSelectKF(e){
	e = e || window.event
	e.stopPropagation()
	$('#seleKfUl').css('display','block')
}
function getKFname(e,that){
	e = e || window.event
	e.stopPropagation()
	$(that).parent().prev().prev().text($(that).text())
	$(that).parent().css('display','none')
}
function getAccList(page){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/stafflist",
		data:{
			id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			'page':page,
			'per':10
		},
		success:function(data){
			var res = JSON.parse(data)
			var tempStr = ''	
			if(res.data.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width: 1.6rem;' src='../images/list_no_mess.png' /></li>"
				$('#accMgrMainUl').html(nomess)
				return;
			}
			$.each(res.data.list, function(index,value) {				
				tempStr += "<li class='accMgrMainLi accMgrItem'>"
				tempStr += "<span class='flex4'><i serid='"+value.id+"' onclick='checkThisBtn(this)' class='iconfont icon-xuanze'></i></span>"		
				tempStr += "<span class='flex3'>"+(index+1)+"</span>"
				tempStr += "<span class='flex6'>"+value.supportStaffName+"</span>"
				tempStr += "<span class='flex6'>"+value.identity+"</span>"
				tempStr += "<span class='flex4'>"+value.phone+"</span>"
				tempStr += "<span class='flex4'>"+value.createTime.split(' ')[0]+"</span>"
				if(value.isActive==1){
					tempStr += "<span class='flex4'><img userid='"+value.id+"' onclick='togSwitch(this)' class='swImg' src='../images/open.png'/></span>"
				}else{
					tempStr += "<span class='flex4'><img userid='"+value.id+"' onclick='togSwitch(this)' class='swImg' src='../images/close.png'/></span>"
				}
				
				tempStr += "<span class='flex6 controlSpan'><span  onclick='editAccount(this)' pwd='"+value.password+"' supportStaffName='"+value.supportStaffName+"' phone='"+value.phone+"' serid='"+value.id+"'>编辑</span><span onclick='delServicerSin("+value.id+")' class='redFont'>删除</span></span>"
				tempStr += "</li>"
			});
            $('#accMgrMainUl').html(tempStr)
             $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize: res.data.count,
				callback: function(current) {
					getAccList(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function delServicerSin(delid){
	var list = []
	list.push(delid)
	delServicer(list)
	
}
function delServicerMult(){
	var list = []
 	$('.accMgrMainLi .icon-choosehandle').each(function(){
 		list.push($(this).attr('serid'))
 	})
 	delServicer(list)
}
function delServicer(list){
	if(list.length==0){
		layer.msg('未选择转接客服')
		return;
	}
	var delObj = {
		id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		staffIDs: JSON.stringify(list)
	}
	layer.msg('确定要删除吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/user/deletestaffs",
				data:delObj,
				success:function(data){
					layer.closeAll()
					if(JSON.parse(data).code==0){
						window.location.reload()
					}else{
						layer.msg('删除失败！')
					}
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
    });	
	
}
function editAccount(that){    //编辑账户	
	var tpEle  = "<div class='singleMsgBox'>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-shouji1'></i>"
	    tpEle += "<input type='' name='' id='serPhone' value='"+$(that).attr('phone')+"' maxlength='11'  placeholder='请输入电话号码' />"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-yonghu'></i>"
	    tpEle += "<input type='' name='' id='serName' value='"+$(that).attr('supportStaffName')+"' maxlength='10' placeholder='请输入姓名'/>"
	    tpEle += "</div>"
	    tpEle += "<div style='display:none' class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-mima'></i>"
	    tpEle += "<input type='' name='' id='serPwd' value='"+$(that).attr('pwd')+"' maxlength='10' placeholder='请输入密码'/>"
	    tpEle += "</div>"
	    tpEle += "<div style='display:none' class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-007jiqiren'></i>"
	    tpEle += "<input type='' name='' id='serPort' value='"+$(that).parent().prev().prev().prev().text()+"' maxlength='10' placeholder='请输入机器人端口'/>"
	    tpEle += "</div>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<i class='iconfont icon-kefu'></i>"
	    tpEle += "<div class='xialakfBox'>"
	    tpEle += "<p id='identity' onclick='showSelectKF(event)'>"+$(that).parent().prev().prev().prev().prev().text()+"</p><i class='iconfont icon-xiala'></i>"
	    tpEle += "<ul id='seleKfUl' class='seleKfUl'>"
	    tpEle += "<li onclick='getKFname(event,this)'>客服</li>"
	    tpEle += "</ul>"
	    tpEle += "</div><span style='color:#999;line-height: 0.3rem;margin-top: 0.2rem;display: inline-block;'>提示：外呼任务执行时，A类（有明确意向）客户立即转接人工客服</span></div>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='editServicer("+$(that).attr('serid')+")' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "编辑转接人工",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		cancelSingle(singleUp)
	}) 
}
function editServicer(serid){
	var serObj={
		phone:$('#serPhone').val().replace(/\s+/g, ""),
		supportStaffName:$('#serName').val().replace(/\s+/g, ""),
//		password:$('#serPwd').val().replace(/\s+/g, ""),
		password:new Date().getTime(),
//		robotPort:$('#serPort').val().replace(/\s+/g, ""),
		robotPort:new Date().getTime(),
		identity:$('#identity').text(),
		id:serid
	}
	if(!/^1[34578]\d{9}$/.test(serObj.phone)){
		layer.msg('手机号格式不正确')
		return;
	}
//	if(serObj.phone==''||serObj.supportStaffName==''||serObj.account==''||serObj.password==''||serObj.robotPort==''){
	if(serObj.phone==''||serObj.supportStaffName==''){
		layer.msg('客户姓名不能为空！！')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/edituserinfo",
		data:serObj,
		success:function(data){
			layer.closeAll()
			if(JSON.parse(data).code==0){
				getAccList(1)
				layer.msg('编辑成功！')
			}else{
				layer.msg('编辑失败！')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function changeRed(){
 	if($('.accMgrMainLi .icon-choosehandle').length == 0){
 		$("#del00").css({"background":"#f2f2f2","color":"#666666"})
 		$("#del00 i").css({"color":"#666666"})
 	}else{
 		$("#del00").css({"background":"rgb(213, 106, 106)","color":"white"})
 		$("#del00 i").css({"color":"white"})
 	}
}
