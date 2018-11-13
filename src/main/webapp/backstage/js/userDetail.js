var prev = 1;
function init(){
	 $("#start").datepicker({
    	onSelect:function(dateText,inst){
       		$("#end").datepicker("option","minDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	});
	$("#end").datepicker({
    	onSelect:function(dateText,inst){
        	$("#start").datepicker("option","maxDate",dateText);
    	},
    	dateFormat: 'yy-mm-dd'
	}); 
	getAccountMsg()
	$('#curAccount').text(sessionStorage.getItem('adminAccount'))	
}

function editMsg(that,type){
	var parent = $(that).parent()
	parent.css('display','none')
	parent.next().css('display','block')
	if(type !== 'spe'){
		parent.next().children('input').focus().val($(that).prev().text())
	}	
	$('#sureBtn').css('display','block')
}
function blurMsg(that){
//	setTimeout(function(){
		var parent = $(that).parent()
		parent.css('display','none')
		parent.prev().css('display','block')
		parent.prev().children('span').text($(that).val())
//		$('#sureBtn').css('display','none')
//	},100)	
}
function getAccountMsg(){  //先获取该用户下的所有项目以及项目下的语音  获取到以后赋值that attr('project')
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/findProjectByUserId",
		data:{userId:getQueryString('userid'),adminId:sessionStorage.getItem('adminId')},
		success:function(data){
			var res = JSON.parse(data)
//			console.log(res)
//			if(res.projectList[0].switchStatus == 1){
//				$('#switchStatus').attr('status','0')
//				$('#switchStatus').attr('src','../images/closeStatus.png')
//			}else{
//				$('#switchStatus').attr('status','1')
//				$('#switchStatus').attr('src','../images/openStatus.png')
//			}
			$('#editAccount').text(res.account)
			$('#editCompany').text(res.company)
			$('#editLinkman').text(res.contactPerson)
			$('#editPhone').text(res.contactPhone)
			$('#editArea').text(res.city)
			$('#start').val(res.activeTime)
			$('#end').val(res.validTime)
		    $('#editTime').text(res.activeTime + " — " + res.validTime)
			$("#city_1").citySelect({
        		prov: res.city.split('-')[0],
        		city: res.city.split('-')[1],
        		required: false
    		});
//			
//			var proList = ''
//			$.each(res.projectList, function(index,value) {
//				proList +='<li class="proLi"><span>'+value.projectName+'</span><i onclick="linkpage(this)" proid="'+value.id+'" class="iconfont icon-bianji"></i><i onclick="deletePro(this)" proid="'+value.id+'" class="iconfont icon-shanchu"></i></li>'				
//			});
//			$('#proUl').html(proList)
//			
//			$('#portMsgBox').html('')
//			$.each(res.gatewayAndPortArr, function(index,value) {
//				if(value.gatewayType == 1){
//					newItem(value)
//				}else if(value.gatewayType == 2){
//					newSip(value)
//				}
//			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function editBaseMsg(){
	var msg = {
		userId:getQueryString('userId'),
		company:$('#editCompany').text(),
		contactPerson:$('#editLinkman').text(),
		contactPhone:$('#editPhone').text(),
		activeTime:$('#start').val(),
		validTime:$('#end').val(),
		city:$('#provs').val()+'-'+$('#citys').val(),
		adminId:sessionStorage.getItem('adminId')
	}
	if(!/^[\u4e00-\u9fa5]+$/.test(msg.company.replace(/\(/,"").replace(/\)/,"").replace(/\（/,"").replace(/\）/,""))){
		layer.msg('公司请输入汉字')
		return;
	}
	if(!/^[\u4e00-\u9fa5]+$/.test(msg.contactPerson)){
		layer.msg('联系人请输入汉字')
		return;
	}
	if(!/^1[345678]\d{9}$/.test(msg.contactPhone)){
		layer.msg('手机号格式不正确')
		return;
	}
	if(msg.company==''||msg.contactPerson==''||msg.contactPhone==''){
		layer.msg('用户信息不能为空')
		return
	}
	
	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/updateProject",
		data:msg,
		success:function(data){
			var res = JSON.parse(data)
			if(res.result==0){
				layer.msg('编辑成功')
				$('#sureBtn').css('display','none')
			}else{
				layer.msg('编辑失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}

function changeTabs(event){
	switch($(event.target).attr('card')){
		case '0':
		window.location.href = "userDetail.html?userId=" + getQueryString('userId')
		break;
		case '1':
		window.location.href = "robot.html?userId=" + getQueryString('userId')
		break;
		case '2':
		window.location.href = "voices.html?userId=" + getQueryString('userId')
		break;
	}
	
}
function resetPwd(){
	layer.msg('确定要重置密码吗？', {
    	time: 20000, //20s后自动关闭
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/admin/resetPassword",
				data:{userId:getQueryString('userid')},
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
function refresh(){
	window.location.reload()
}
