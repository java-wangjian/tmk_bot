function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})  
	unReadCount()
	getSystemMsg(1)
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
		url:commonUrl+"/tmk-bot/callrecord/history",
		data:sysObj,
		success:function(data){
			var res = JSON.parse(data)
//			console.log(res)
			var tempStr = ''
			$.each(res.data.list, function(index,value) {	
				tempStr += "<li style='text-align:center' class='accMgrMainLi accMgrItem readColor '>"
				tempStr += "<span class='flex3'>"+(index+1)+"</span>"
				tempStr += "<span class='flex3'>"+JSON.parse(sessionStorage.getItem('loginMsg')).account+"</span>"
				tempStr += "<span class='flex3'>"+value.time.split(' ')[0]+"</span>"
				tempStr += "<span class='flex6'>"+value.projects+"</span>"
				tempStr += "<span class='flex3'>"+value.levels+"</span>"
				tempStr += "<span class='flex3'>"+value.minDuart+"s以上</span>"
				tempStr += "<span class='flex6'>"+value.dateInterval.split('~')[0].split(' ')[0]+'~'+value.dateInterval.split('~')[1].split(' ')[0]+"</span>"
				tempStr += "<span class='flex3'>"+value.count+"</span>"
				tempStr += "</li>"
			});
            $('#accMgrMainUl').html(tempStr)
            $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize:res.data.count,
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

