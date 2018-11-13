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
function feedback(){
	var fbObj = {
		id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		title:'默认标题',
		content: $('#fdContent').val(),
		createTime:getNowTime(),
		msgType:1
	}
	if(fbObj.content.replace(/\s+/g, "")==''){
		layer.msg('您还未输入任何反馈内容')
		return;
	}
	var loading = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/msg/feedback",
		data:fbObj,
		success:function(data){
			layer.close(loading);
			var res = JSON.parse(data)
            if(res.code==0){
            	layer.msg('反馈成功')
            	$('#fdContent').val('')
            }else{
            	layer.msg(res.result)
            }			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function changeTxtCount(){
	$('#currTxtCount').text($('#fdContent').val().length) 
}
