function init(){
	getAccountMsg()
	$('#curAccount').text(sessionStorage.getItem('adminAccount'))	
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
function getAccountMsg(){  //先获取该用户下的所有项目以及项目下的语音  获取到以后赋值that attr('project')
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/findProjectByUserId",
		data:{userId:getQueryString('userid'),adminId:sessionStorage.getItem('adminId')},
		success:function(data){
			var res = JSON.parse(data)
			if(res.projectList[0].switchStatus == 1){
				$('#switchStatus').attr('status','0')
				$('#switchStatus').attr('src','../images/closeStatus.png')
			}else{
				$('#switchStatus').attr('status','1')
				$('#switchStatus').attr('src','../images/openStatus.png')
			}
			
			var proList = ''
			$.each(res.projectList, function(index,value) {
				proList +='<li class="proLi"><span>'+value.projectName+'</span><i onclick="linkpage(this)" proid="'+value.id+'" class="iconfont icon-bianji"></i><i onclick="deletePro(this)" proid="'+value.id+'" class="iconfont icon-shanchu"></i></li>'				
			});
			$('#proUl').html(proList)
			
			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function linkpage(that){
	var proid = $(that).attr('proid')
	window.location.href='newpro.html?projectID='+proid	
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
function changeProStatus(that){
	var status = $(that).attr('status')
	if(status == '1'){
		$(that).attr('status','0')
		$(that).attr('src','../images/closeStatus.png')
	}else{
		$(that).attr('status','1')
		$(that).attr('src','../images/openStatus.png')
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/updateStatus",
		data:{
			"userId":getQueryString('userId'),
			"switchstatus":status
		},
		success:function(data){
			var res = JSON.parse(data)
			if(res.result == 0){
				layer.msg('操作成功')
			}else{
				layer.msg('操作失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
