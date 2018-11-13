function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
//	$(".tabs").click(function(){
//		$(this).parent().children().css("border-bottom","0.06rem solid #F7F7F7")
//		$(this).css("border-bottom","0.06rem solid #6186CF")
//	})
   	unReadCount()
}
function rePwd(){
	var resetMsg = {
		'account': JSON.parse(sessionStorage.getItem('loginMsg')).account,
		'password': $('#password').val().replace(/\s+/g, ""),
		'newpwd': $('#newpwd').val().replace(/\s+/g, "")		
	}	
	if($('#reNewpwd').val().replace(/\s+/g, "")==''||resetMsg.newpwd==''||resetMsg.password==''){
		layer.msg('新旧密码均不能为空！');
		return;
	}
	if(resetMsg.newpwd.length>12||resetMsg.newpwd.length<6){
		layer.msg('密码为6-12位！');
		return;
	}
	if(resetMsg.password==resetMsg.newpwd){
		layer.msg('新旧密码不能相同！');
		return;
	}
	
	if($('#reNewpwd').val().replace(/\s+/g, "")!==resetMsg.newpwd){
		layer.msg('新密码两次输入不一致！');
		return;
	}	
	var loading = layer.load(1, {shade: [0.5,'#000']});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/chpwd",
		data:resetMsg,
		success:function(data){
			layer.close(loading);
			var res = JSON.parse(data)
            if(res.code==0){
            	layer.msg('修改成功！');
            }else{
            	layer.msg(res.result);
            }			
		},
		error:function(data){
			console.log(data)
		}
	});
}
