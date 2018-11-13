function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
    unReadCount()
	getSimList(1)	
}
//function togSwitch(that){
//	var tar = $(that)
//	var switchs = {id:tar.attr('cardid')}
//	var sUrl = tar.attr('src').split('images/')[1]
//	if(sUrl=='open.png'){
//		tar.attr('src','../images/close.png')
//		switchs.isActive = 0
//	}else{
//		tar.attr('src','../images/open.png')
//		switchs.isActive = 1
//	}
//	console.log(switchs)
//	$.ajax({
//		type:"post",
//		url:commonUrl+"/tmk-bot/simcard/switch",
//		data:switchs,
//		success:function(data){
//			const res = JSON.parse(data)
//			if(res.code==0){
//				layer.msg('操作成功')
//			}else{
//				layer.msg('操作失败')
//			}
//		},
//		error:function(data){
//			console.log(data)
//		}
//	});
//}
function getSimList(page){
	var simCardObj = {
		id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		page: page,
		per:10		
	}
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/simcard/list",
		data:simCardObj,
		success:function(data){
			layer.close(loadings)
			var res = JSON.parse(data)
			console.log(res)
            var cardStr = ""
            $.each(res.data.list, function(index,value) {
            	cardStr+= "<li class='accMgrMainLi accMgrItem'>"
            	cardStr+= "<span class='flex2'>"+(index+1)+"</span>"
            	cardStr+= "<span class='flex2'>"+value.gatewayNode+"</span>"
//          	cardStr+= "<span class='flex6'>"+value.gatewayUrl+"</span>"
            	cardStr+= "<span class='flex2'>"+value.port+"</span>"
            	if(value.phone==0){
            		cardStr+= "<span class='flex2'>固话</span>"
            	}else{
            		cardStr+= "<span class='flex2'>"+value.phone+"</span>"
            	}            	
            	cardStr+= "<span class='flex2'>"+simLabel(value.label)+"</span>"
            	cardStr+= "<span class='flex2'>"+simStatus(value.status)+"</span>"
            	cardStr+= "<span class='flex2'>"+simCurr(value.now)+"</span>"
//          	if(value.isActive==0){
//          		cardStr+= "<span class='flex4'><img cardid='"+value.id+"' onclick='togSwitch(this)' class='swImg' src='../images/close.png'/></span>"
//          	}else{
//          		cardStr+= "<span class='flex4'><img cardid='"+value.id+"' onclick='togSwitch(this)' class='swImg' src='../images/open.png'/></span>"
//          	}
//          	cardStr+= "<span class='flex6 controlSpan'><span cardid='"+value.id+"' onclick='editCardPrefixBox(this)'>编辑</span><span onclick='restart("+value.id+")'>重启端口</span></span>"
            	cardStr+= "</li>"
            });
            $('#simCardUl').html(cardStr)
            $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize: res.data.count,
				callback: function(current) {
					getSimList(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function editCardPrefixBox(that){
	var tpEle  = "<div class='singleMsgBox'>"
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<input type='' name='' id='prefixName' value='"+$(that).parent().prev().prev().text()+"' />"
	    tpEle += "</div>"
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='changePredix("+$(that).attr('cardid')+")' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "编辑前缀",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '2.5rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		layer.close(singleUp)
	}) 
}
function restart(cardid){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/simcard/restart",
		data:{id:cardid},
		success:function(data){
			var res = JSON.parse(data)
			if(res.code==0){
				layer.msg('重启成功')
			}
            
		},
		error:function(data){
			console.log(data)
		}
	});
}
function changePredix(cardid){
	var chFixObj = {
		id: cardid,
		prefix: $('#prefixName').val().replace(/\s+/g, "")
	}
	if(chFixObj.prefix==''){
		layer.msg('前缀不能为空')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/simcard/edit",
		data:chFixObj,
		success:function(data){
			var res = JSON.parse(data)
			if(res.code==0){
				getSimList(1)
				layer.closeAll()
				layer.msg('编辑成功')
			}
            
		},
		error:function(data){
			console.log(data)
		}
	});
}
