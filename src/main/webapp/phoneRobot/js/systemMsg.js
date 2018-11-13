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
	getSystemMsg(1)
}
function allCheckAcc(e){
	$(e.target).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	var status = $(e.target).next()
	if(status.text()=='全选'){
		$('.accMgrMainLi .radio').removeClass('icon-xuanze').addClass('icon-choosehandle')
		status.text('取消')
	}else{
		$('.accMgrMainLi .radio').removeClass('icon-choosehandle').addClass('icon-xuanze')		
		status.text('全选')
	}
	isRed ()
}
function checkThisBtn(e,that){
	e = e || window.event
	e.stopPropagation()
	$(that).toggleClass('icon-xuanze').toggleClass('icon-choosehandle')
	isRed ()
}
function getSystemMsg(page){
	var sysObj = {
		'id':JSON.parse(sessionStorage.getItem('loginMsg')).id,
		'page':page,
		'per':10,
		'createTime':JSON.parse(sessionStorage.getItem('loginMsg')).createTime
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/msg/list",
		data:sysObj,
		success:function(data){
			var res = JSON.parse(data)
			var tempStr = ''
			$.each(res.data.list, function(index,value) {	
				if(value.status==1){
					tempStr += "<li content='"+value.content+"' title='"+value.title+"' onclick='checkMsg(this)' class='accMgrMainLi accMgrItem unReadColor '>"
				}else{
					tempStr += "<li content='"+value.content+"' title='"+value.title+"' onclick='checkMsg(this)' class='accMgrMainLi accMgrItem readColor'>"
				}
				tempStr += "<span class='flex2'><i msgid='"+value.id+"' onclick='checkThisBtn(event,this)' class='iconfont icon-xuanze radio'></i></span>"
				tempStr += "<span class='flex3'>"+(index+1)+"</span>"
				tempStr += "<span class='flex6'>"
				if(value.status==1){
					tempStr += "<em class='redPoint'></em>"
				}
				
				tempStr += value.title+"</span>"
				tempStr += "<span class='flex12'>"+value.content+"</span>"
				tempStr += "<span class='flex3'><i onclick='passSingle(event,this)' msgid='"+value.id+"' class='iconfont icon-shanchu redIcon'></i></span>"
				tempStr += "</li>"
			});
            $('#accMgrMainUl').html(tempStr)
            $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize: res.data.count,
				callback: function(current) {
					getSystemMsg(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function checkMsg(that){
	var tpEle  = "<div><h1 style='text-align:center'>"+$(that).attr("title")+"</h1>"
	  	tpEle += "<p style='font-size: 0.2rem;text-indent: 0.4rem;padding:0 0.4rem 0.4rem'>"+$(that).attr("content")+"</p>" 	
	  	tpEle += "</div>"
	layer.open({
  		type: 1,
  		title: "系统消息",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', 'auto'],
  		content: tpEle
	});
}
function passSingle(e,that){
	e = e || window.event
	e.stopPropagation()
	var list = []
	list.push($(that).attr('msgid'))	
	delSystemMsg(list)
}
function passDelList(){
	var list = []
 	$('.accMgrMainLi .icon-choosehandle').each(function(){
 		list.push($(this).attr('msgid'))
 	})
 	delSystemMsg(list)
}
function delSystemMsg(list){  //list为删除的消息id列表
	if(list.length==0){
		layer.msg('未选择消息')
		return
	}
	layer.msg('确定要删除吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'],
    	yes: function (index) {
    		layer.close(index)
    		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/msg/batchDelete",
				data:{
					id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
					msgIDs:JSON.stringify(list)
				},
				success:function(data){
//					getSystemMsg(1)
					window.location.reload()
					layer.msg('删除成功')
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
    });	
	
}
function readSystemMsg(){  //list为删除的消息id列表
	var list = []
 	$('.accMgrMainLi .icon-choosehandle').each(function(){
 		list.push($(this).attr('msgid'))
 	})
 	if(list.length==0){
		layer.msg('未选择消息')
		return
	}
 	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/msg/batchRead",
		data:{
			id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			msgIDs:JSON.stringify(list)
		},
		success:function(data){
			window.location.reload()
//			getSystemMsg(1)			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function isRed () {
	var list = []
 	$('.accMgrMainLi .icon-choosehandle').each(function(){
 		list.push($(this).attr('msgid'))
 	})
	if(list.length == 0){
//		d86b6b
		$("#but01").css({"background":"#F2F2F2","color":"#666666"})
		$("#but02").css({"background":"#F2F2F2","color":"#666666"})
	}else{
		$("#but01").css({"background":"#d86b6b","color":"white"})
		$("#but02").css({"background":"#ff9c00","color":"white"})
	}
}
