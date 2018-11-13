//未引用的js文件
function loadFuncOrStyle(){    //初始化样式或函数
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
    unReadCount()
	getSystemMsg(1)
}
function getSystemMsg(page){
	const sysObj = {
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
			const res = JSON.parse(data)
			var tempStr = ''
			$.each(res.data.list, function(index,value) {	
				if(value.status==1){
					tempStr += "<li class='accMgrMainLi accMgrItem unReadColor '>"
				}else{
					tempStr += "<li class='accMgrMainLi accMgrItem readColor'>"
				}
				tempStr += "<span class='flex2'><i msgid='"+value.id+"' onclick='checkThisBtn(this)' class='iconfont icon-xuanze radio'></i></span>"
				tempStr += "<span class='flex1'>"+(index+1)+"</span>"
				tempStr += "<span class='flex6'>"
				if(value.status==1){
					tempStr += "<em class='redPoint'></em>"
				}
				
				tempStr += value.title+"</span>"
				tempStr += "<span class='flex12'>"+value.content+"</span>"
				tempStr += "<span class='flex3'><i onclick='passSingle(this)' msgid='"+value.id+"' class='iconfont icon-shanchu redIcon'></i></span>"
				tempStr += "</li>"
			});
            $('#accMgrMainUl').html(tempStr)
            $('#pagination').pagination({
				currentPage: page,
				totalPage: Math.ceil(res.data.count/10),
				isShow: false,
				count: 6,
				prevPageText: "上一页",
				nextPageText: "下一页 ",
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
