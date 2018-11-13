//var port_pre ; 
var allGatewayList = []
var allSipList = []

var adminId = sessionStorage.getItem('adminId')
function init(){
	getGatewayAndPort()
	getAccountMsg()
	$('#curAccount').text(sessionStorage.getItem('adminAccount'))	
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
}
function allSelect(that){
	var status = $(that).children('span')
	if($(that).children('.iconfont').hasClass('icon-xuanze')){	
		$(that).parent().find('.iconfont').each(function(index,val){
			if($(val).attr('userId')==getQueryString('userid')||$(val).attr('userId')==''||!$(val).attr('userId')){
				$(val).removeClass('icon-xuanze').addClass('icon-choose')
			}
		})					
		status.text('全选')
	}else{
		$(that).parent().find('.iconfont').each(function(index,val){
			if($(val).attr('userId')==getQueryString('userid')||$(val).attr('userId')==''||!$(val).attr('userId')){
				$(val).removeClass('icon-choose').addClass('icon-xuanze')
			}
		})
		status.text('取消')
	}
}
function getGatewayAndPort(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/getGatewayAndPort",   //代理商拥有的全部端口
		data:{adminId:sessionStorage.getItem('adminId')},
		success:function(data){
			var res = JSON.parse(data)
			$.each(res, function(index,value) {
				if(value.gatewayType==1){
					allGatewayList.push(value)
				}else if(value.gatewayType==2){
					allSipList.push(value)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function editMsg(that){
	var parent = $(that).parent()
	parent.css('display','none')
	parent.next().css('display','block')
	parent.next().children('input').focus().val($(that).prev().text())
	
}
function blurMsg(that){
	var parent = $(that).parent()
	parent.css('display','none')
	parent.prev().css('display','block')
	parent.prev().children('span').text($(that).val())
}
function changeThis(that){
	var portIndex = $(that).index()
	$(that).parent().siblings('.portsBox').find('.ports:eq('+portIndex+')').find('.iconfont').removeClass('icon-xuanze').addClass('icon-choose')
	var thatEle = $(that).find('.iconfont')
	if(thatEle.attr('userId')==''||thatEle.attr('userId')==getQueryString('userid')){
		thatEle.toggleClass('icon-choose').toggleClass('icon-xuanze')
	}else{		
		layer.msg('该端口已经被分配，请选择其他端口')		
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
			$('#editAccount').text(res.account)
			$('#editCompany').text(res.company)
			$('#editLinkman').text(res.contactPerson)
			$('#editPhone').text(res.contactPhone)
			$('#start').val(res.activeTime)
			$('#end').val(res.validTime)
			$("#city_1").citySelect({
        		prov: res.city.split('-')[0],
        		city: res.city.split('-')[1],
        		required: false
    		});
			
			var proList = ''
			$.each(res.projectList, function(index,value) {
				proList +='<li class="proLi"><span>'+value.projectName+'</span><i onclick="linkpage(this)" proid="'+value.id+'" class="iconfont icon-bianji"></i><i onclick="deletePro(this)" proid="'+value.id+'" class="iconfont icon-shanchu"></i></li>'				
			});
			$('#proUl').html(proList)
			
			$('#portMsgBox').html('')
			$.each(res.gatewayAndPortArr, function(index,value) {
				if(value.gatewayType == 1){
					newItem(value)
				}else if(value.gatewayType == 2){
					newSip(value)
				}
			});
			setGray()
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
//function getGatewayId(that){
//	console.log($(that).children('option:selected').attr('data_port'))
//}
function delPort(that){
	layer.msg('确定要删除该网关吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
      		$(that).parent().remove()
    	}
   });	
}
function addPort(){
	var options = ''
	$.each(allGatewayList, function(index,value) {
		options += "<option gatewayId='"+value.gatewayId+"' data_port='"+JSON.stringify(value)+"' >"+value.gateway+"</option>"
	});
	
	var str = '<div gatewayType="1" class="portWrap">'
		str+= '<p onclick="delPort(this)" class="delPort">X</p>'
		
		
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关编号：</label>'
		str+= '<div class="port01">'
//		str+= '<input class="port02 dk_num" type="text" />'
		str+='<select onclick="changePort(this)" class="port02 dk_num">'+options+'</select>'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关账号：</label>'
		str+= '<div class="port01">'
		str+= '<input class="port02 wg_auth" type="text" />'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关密码：</label>'
		str+= '<div class="port01">'
		str+= '<input class="port02 wg_pwd" type="text" />'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关URL：</label>'
		str+= '<div class="port01">'
		str+= '<input class="port02 wg_num" type="text" />'
		str+= '</div>'
		str+= '</div>'
		str+= '<div class="port03">'
		str+= '<label class="port00">端口信息：</label>'
		str+= '<div class="port012">'
		str+= '<span onclick="changeTab(this)" class="blueBg">拨打端口</span>'
		str+= '<span onclick="changeTab(this)" class="grayBg">转接端口</span>'
		str+= '</div>'
		str+= '</div>'
		str+= '<div class="portsBox">'

		str+= '</div>'
		str+= '<div style="display: none;" class="portsBox">'

		str+= '</div>'
		str+= '</div>'					

		var ts = $(str).appendTo('#portMsgBox')
		
		var havePortlist = JSON.parse($(ts).find('.dk_num').children('option:selected').attr('data_port'))
		
		var portSpanD = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
		var portSpanP = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
		
//		for (var i=0;i<32;i++) {
//			portSpanD += '<span onclick="changeThis(this)" class="ports portD"><i data="'+i+'" class="iconfont icon-choose"></i><span>'+i+'</span></span>'
//			portSpanP += '<span onclick="changeThis(this)" class="ports portD"><i data="'+i+'" class="iconfont icon-choose"></i><span>'+i+'</span></span>'			
//		}
		
		$.each(havePortlist.portList, function(index,value) {
			portSpanD += '<span onclick="changeThis(this)" class="ports portD"><i userId="'+value.userId+'" data="'+value.port+'" class="iconfont icon-choose"></i><span>'+value.port+'</span></span>'
			portSpanP += '<span onclick="changeThis(this)" class="ports portP"><i userId="'+value.userId+'" data="'+value.port+'" class="iconfont icon-choose"></i><span>'+value.port+'</span></span>'
		});
		
		$(ts).find('.wg_auth').val(havePortlist.auth)
		$(ts).find('.wg_pwd').val(havePortlist.pwd)
		$(ts).find('.wg_num').val(havePortlist.url)
		
		var boxs = $(ts).find('.portsBox')
		
		$($(boxs)[0]).html(portSpanD)
		$($(boxs)[1]).html(portSpanP)
		
		setGray()
}
function newItem(msg){
	var str = '<div gatewayType="'+msg.gatewayType+'" class="portWrap">'
		str+= '<p onclick="delPort(this)" class="delPort">X</p>'
		
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关编号：</label>'
		str+= '<div style="border:none" class="port01">'
		str+= "<select class='port02 dk_num'><option data_port='"+JSON.stringify(msg)+"' gatewayId='"+msg.gatewayId+"' >"+msg.gatewayNumbers+"</option></select>"
//		str+= '<select class="port02 dk_num"><option data_port="'+JSON.stringify(msg)+'" gatewayId="'+msg.gatewayId+'" >'+msg.gatewayNumbers+'</option></select>'
//		str+= '<input readonly="readonly" gatewayId="'+msg.gatewayId+'" class="port02 dk_num" type="text" value="'+msg.gatewayNumbers+'"/>'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关账号：</label>'
		str+= '<div class="port01">'
		str+= '<input class="port02 wg_auth" type="text" value="'+msg.auth+'"/>'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关密码：</label>'
		str+= '<div class="port01">'
		str+= '<input class="port02 wg_pwd" type="text" value="'+msg.pwd+'"/>'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关URL：</label>'
		str+= '<div style="border:none" class="port01">'
		str+= '<input readonly="readonly" class="port02 wg_num" type="text" value="'+msg.url+'"/>'
		str+= '</div>'
		str+= '</div>'
		str+= '<div class="port03">'
		str+= '<label class="port00">端口信息：</label>'
		str+= '<div class="port012">'
		str+= '<span onclick="changeTab(this)" class="blueBg">拨打端口</span>'
		str+= '<span onclick="changeTab(this)" class="grayBg">转接端口</span>'
		str+= '</div>'
		str+= '</div>'
		str+= '<div class="portsBox">'

		str+= '</div>'
		str+= '<div style="display: none;" class="portsBox">'

		str+= '</div>'
		str+= '</div>'					
//		var ts = $('#portMsgBox').append(str)
		var ts = $(str).appendTo('#portMsgBox')
		
//		var havePortlist = JSON.parse($(ts).find('.dk_num').children('option:selected').attr('data_port'))  // 等同于msg
		
		
		var portSpanD = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
		var portSpanP = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
		
		for (var i=0;i<msg.allPort.length;i++) {
			var tempStr = '<span onclick="changeThis(this)" class="ports portD"><i userId="'+msg.allPort[i].userId+'" data="'+msg.allPort[i].port+'" class="iconfont icon-choose"></i><span>'+msg.allPort[i].port+'</span></span>'
			
			for (var j=0;j<msg.callPortList.length;j++) {
				if(msg.allPort[i].port==msg.callPortList[j].port){
					tempStr = '<span onclick="changeThis(this)" class="ports portD"><i userId="'+msg.allPort[i].userId+'" data="'+msg.allPort[i].port+'" class="iconfont icon-xuanze"></i><span>'+msg.allPort[i].port+'</span></span>'
				}
			}
			
			portSpanD += tempStr
		}

		for (var i=0;i<msg.allPort.length;i++) {
			var tempStr = '<span onclick="changeThis(this)" class="ports portD"><i userId="'+msg.allPort[i].userId+'" data="'+msg.allPort[i].port+'" class="iconfont icon-choose"></i><span>'+msg.allPort[i].port+'</span></span>'
			for (var j=0;j<msg.transferPortList.length;j++) {
				if(msg.allPort[i].port==msg.transferPortList[j].port){
					tempStr = '<span onclick="changeThis(this)" class="ports portD"><i userId="'+msg.allPort[i].userId+'" data="'+msg.allPort[i].port+'" class="iconfont icon-xuanze"></i><span>'+msg.allPort[i].port+'</span></span>'
				}
			}
			portSpanP += tempStr
		}
		
		
		var boxs = $(ts).find('.portsBox')
		
		$($(boxs)[0]).html(portSpanD)
		$($(boxs)[1]).html(portSpanP)
}
function changePort(that){
	var wrap = $(that).parent().parent().parent()
	var havePortlist = JSON.parse($(that).children('option:selected').attr('data_port'))
	
	var portSpanD = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
	var portSpanP = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
	
//		for (var i=0;i<havePortlist.allPort.length;i++) {
//			var tempStr = '<span onclick="changeThis(this)" class="ports portD"><i data="'+i+'" class="iconfont icon-choose"></i><span>'+i+'</span></span>'
//			for (var j=0;j<msg.callPortList.length;j++) {
//				if(i==msg.callPortList[j]){
//					tempStr = '<span onclick="changeThis(this)" class="ports portD"><i data="'+i+'" class="iconfont icon-xuanze"></i><span>'+i+'</span></span>'
//				}
//			}
//			portSpanD += tempStr
//		}
//
//		for (var i=0;i<havePortlist.allPort.length;i++) {
//			var tempStr = '<span onclick="changeThis(this)" class="ports portD"><i data="'+i+'" class="iconfont icon-choose"></i><span>'+i+'</span></span>'
//			for (var j=0;j<msg.transferPortList.length;j++) {
//				if(i==msg.transferPortList[j]){
//					tempStr = '<span onclick="changeThis(this)" class="ports portD"><i data="'+i+'" class="iconfont icon-xuanze"></i><span>'+i+'</span></span>'
//				}
//			}
//			portSpanP += tempStr
//		}
	$.each(havePortlist.portList, function(index,value) {
		portSpanD += '<span onclick="changeThis(this)" class="ports portD"><i userId="'+value.userId+'" data="'+value.port+'" class="iconfont icon-choose"></i><span>'+value.port+'</span></span>'
		portSpanP += '<span onclick="changeThis(this)" class="ports portP"><i userId="'+value.userId+'" data="'+value.port+'" class="iconfont icon-choose"></i><span>'+value.port+'</span></span>'
	});
	wrap.find('.wg_auth').val(havePortlist.auth)
	wrap.find('.wg_pwd').val(havePortlist.pwd)
	wrap.find('.wg_num').val(havePortlist.url)
	
	var boxs = wrap.find('.portsBox')
		
	$($(boxs)[0]).html(portSpanD)
	$($(boxs)[1]).html(portSpanP)
	
	setGray()
}
function changeTab(that){
	$(that).toggleClass('blueBg').toggleClass('grayBg')
	$(that).siblings().toggleClass('blueBg').toggleClass('grayBg')
	$(that).parent().parent().parent().find('.portsBox').css('display','none')
	var ind = $(that).index()
	$(that).parent().parent().parent().find('.portsBox:eq('+ind+')').css('display','block')
}
function addMoreProject(){
	var tpEle  = "<div class='addMsgWrap'>"
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>项目名称：</label><input maxlength='15' type='' name='' id='newCompanyName' value='' />"
	    tpEle += "</div>"	
	    tpEle += "</div>"	    

	    tpEle += "<div class='addRowBtn'>"
	    tpEle += "<button id='qxCreate' class='but1'>取消</button>"
	    tpEle += "<button class='but2' id='sureCreate' >增加话术</button>"
	    tpEle += "</div>"
	    tpEle += "</div>"		    
	var createBox = layer.open({
		type: 1,
		title: "添加项目",
		shadeClose: false,
		scrollbar: false,
		move: false,
   		area: ['5.2rem', '2.4rem'],
		content: tpEle
	});
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})	
	$('#sureCreate').click(function(){
		confirmProName()
	})
}
function confirmProName(){	  //确定添加	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/addProject",
		data:{
			projectName:$('#newCompanyName').val(),
			userId:getQueryString('userid')
		},
		success:function(data){
			const res = JSON.parse(data)
			layer.closeAll()
			window.location.href='newpro.html?projectID='+res.projectID			
		},
		error:function(data){
			console.log(data)
		}
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
function sendAccountMsg(){
	var gateway = []
	var gatewaysEle = $('.portWrap')
	
	$.each(gatewaysEle, function(index,value) {
		if($(value).attr('gatewayType')=='1'){
			var temp = {}
		
			temp.gatewayId =  $(value).find('.dk_num').children('option:selected').attr('gatewayId')
		
			temp.gateway = $(value).find('.dk_num').val();
			temp.url = $(value).find('.wg_num').val();
		
			temp.auth = $(value).find('.wg_auth').val()   //网关URL
			temp.pwd = $(value).find('.wg_pwd').val()   //网关URL
		
			var ports0 = $(value).find('.portsBox:eq(0)').find('.icon-xuanze')
			var ports1 = $(value).find('.portsBox:eq(1)').find('.icon-xuanze')
			var portList0 = []
			var portList1 = []
			$.each(ports0,function(ind,val){
				if($(val).next().text()!=='取消'){
					portList0.push(parseInt($(val).attr('data')))
				}			
			})
			$.each(ports1,function(ind,val){
				if($(val).next().text()!=='取消'){
					portList1.push(parseInt($(val).attr('data')))
				}			
			})
			temp.callPortList = portList0
			temp.transferPortList = portList1
			temp.gatewayType = $(value).attr('gatewayType')
			gateway.push(temp)
		}else if($(value).attr('gatewayType')=='2'){
			var temp = {} 
			temp.gatewayId =  $(value).find('.sip_num').children('option:selected').attr('gatewayId')
			temp.callCount =  $(value).find('.sip_max').val()			
			temp.gatewayType = $(value).attr('gatewayType')
			gateway.push(temp)
		}
		
	});
	var gtidList = []
//	console.log(gateway)
	for(var i=0;i<gateway.length;i++){
		gtidList.push(gateway[i].gatewayId)
		if(gateway[i].gatewayType == '1'){
			if(gateway[i].callPortList.length == 0 || gateway[i].auth == '' || gateway[i].gatewayURL == '' || gateway[i].pwd == ''){
				layer.msg('网关信息不能为空！')
				return;
			}
			
		}
		if(gateway[i].gatewayType == '2'){
			if(gateway[i].callCount == '' || parseInt(gateway[i].callCount) <= 0 ||!/^[0-9]+$/.test(parseInt(gateway[i].callCount))){
				layer.msg('网关信息不能为空且拨打上限必须大于0！')
				return;
			}
			
		}
	}
	if(isRepeat(gtidList)){
		layer.msg('不能选择重复的网关或者线路！')
		return;
	}
	var msg = {
		userId:getQueryString('userId'),
		company:$('#editCompany').text(),
		contactPerson:$('#editLinkman').text(),
		contactPhone:$('#editPhone').text(),
		activeTime:$('#start').val(),
		validTime:$('#end').val(),
		city:$('#provs').val()+'-'+$('#citys').val(),
		gatewayAndPortArrStr:JSON.stringify(gateway),
//		gatewayAndPortArrStr:gateway,
		adminId:sessionStorage.getItem('adminId'),
	}
	if(msg.company==''||msg.contactPerson==''||msg.contactPhone==''){
		layer.msg('用户信息不能为空')
		return
	}
	if(!/^[\u4e00-\u9fa5]+$/.test(msg.contactPerson)){
		layer.msg('联系人请输入汉字')
		return;
	}
	if(!/^[\u4e00-\u9fa5]+$/.test(msg.company.replace(/\(/,"").replace(/\)/,"").replace(/\（/,"").replace(/\）/,""))){
		layer.msg('公司请输入汉字')
		return;
	}
	
//	var wg_list = []
//	$.each(JSON.parse(msg.gatewayAndPortArrStr), function(index,value) {
//		wg_list.push(value.gateway)
//	});
//	if(isRepeat(wg_list)){
//		layer.msg('网关编号不能重复添加')
//		return;
//	}
	console.log(msg)
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/updateProject",
		data:msg,
		success:function(data){
			var res = JSON.parse(data)
			if(res.result==0){
				layer.msg('编辑成功')
				setTimeout(function(){
           			window.location.href = 'index.html'
           		},1000)
			}else{
				layer.msg('编辑失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function isRepeat(arr){   
    var hash = {};   
    for(var i in arr) {   
        if(hash[arr[i]])   
            return true;   
        hash[arr[i]] = true;   
    }   
    return false;   
} 

function goback(){
	window.location.href = 'index.html'
}
function setGray(){
	var pots = $('.portsBox')
	$.each(pots, function(index,value) {
		$(value).find('.iconfont').each(function(i,val){
			var thisEle = $(val)
//			
			if(thisEle.attr('userId')&&thisEle.attr('userId')!==''&&thisEle.attr('userId')!==getQueryString('userid')){
				thisEle.css('color','#999')   // 不可选
				thisEle.parent().css('color','#999')
			}
		})
	});
}
function newSip(msg){
//	console.log(msg)
	var str = '<div gatewayType="'+msg.gatewayType+'" class="portWrap">'
		str+= '<p onclick="delPort(this)" class="delPort">X</p>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">拨打类型：</label>'
		str+= '<div style="border:none;" class="port01">'
		str+= '<span>sip线路</span>'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">sip编号：</label>'
		str+= '<div class="port01">'
		str+= "<select class='port02 sip_num'><option gatewayId='"+msg.gatewayId+"' >"+msg.gatewayNumbers+"</option></select>"
		str+= '</div>'
		str+= '</div>'
		
		
		
		str+= '<div class="port03">'
		str+= '<label class="port00">拨打上限：</label>'
		str+= '<div style="width:52%" class="port01">'
		str+= '<input max="'+(parseInt(msg.haveUsedCount)+parseInt(msg.callCount))+'" onblur="checkMax(this)" class="port02 sip_max" type="text" value="'+msg.callCount+'"/>'
		str+= '</div>'
		str+= '<span class="ps01"><span class="haveUsedCount">'+msg.haveUsedCount+'</span>/<span class="totals">'+msg.total+'</span></span>'
		str+= '</div>'
		
				
		str+= '</div>'					
		
		var ts = $(str).appendTo('#portMsgBox')
		

}
function addType(){
	var tpEle  = "<div class='addMsgWrap'>"
	
		tpEle += "<div class='selectBoxs'>"										
	    tpEle += "<select id='gatewayType'>"
	    tpEle += "<option value='1' >无线网关</option>"
	    tpEle += "<option value='2' >sip线路</option>"
	    tpEle += "</select>"
	    tpEle += "</div>"	    

	    tpEle += "<div class='addRowBtne'>"
	    tpEle += "<button id='qxCreate' class='but1'>取消</button>"
	    tpEle += "<button class='but2' onclick='confirmPro()'>确定</button>"
	    tpEle += "</div>"
	    
	    tpEle += "</div>"		    
	var createBox = layer.open({
  		type: 1,
  		title: "选择添加类型",
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
  		resize:false,
   		area: ['3rem', '1.8rem'],
  		content: tpEle
	});
	
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})
}
function confirmPro(){
	layer.closeAll();
	switch ($('#gatewayType').val()){
		case '1':
		addPort();
		break;
		case '2':
		addSip();
		break;
	}
	
}
function addSip(){
	var options = ''
	$.each(allSipList, function(index,value) {
		options += "<option gatewayId='"+value.gatewayId+"' data_port='"+JSON.stringify(value)+"' >"+value.gateway+"</option>"
	});
	
	var str = '<div  gatewayType="2" class="portWrap">'
		str+= '<button onclick="removeGateway(this)" class="removeGateway">X</button>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">拨打类型：</label>'
		str+= '<div style="border:none" class="port01">'
		str+= '<span>sip线路</span>'
		str+= '</div>'
		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">网关编号：</label>'
		str+= '<div class="port01">'
		str+= '<select onchange="changeSip(this)" class="port02 sip_num">'+options+'</select>'
		str+= '</div>'
		str+= '</div>'
	
		
//		str+= '<div class="port03">'
//		str+= '<label class="port00">sip前缀：</label>'
//		str+= '<div class="port01">'
//		str+= '<input class="port02 sip_pre" type="text" />'
//		str+= '</div>'
//		str+= '</div>'
		
		str+= '<div class="port03">'
		str+= '<label class="port00">拨打上限：</label>'
		str+= '<div style="width:52%" class="port01">'
		str+= '<input max="" onblur="checkMax(this)" class="port02 sip_max" type="text" />'
		str+= '</div>'
		str+= '<span class="ps01"><span class="haveUsedCount"></span>/<span class="totals"></span></span>'
		str+= '</div>'

		str+= '</div>'	
		
		var ts = $(str).appendTo('#portMsgBox')
		var callCount = JSON.parse($(ts).find('.sip_num').children('option:selected').attr('data_port'))
		$(ts).find('.sip_max').attr('max',callCount.haveUsedCount)
//		$(ts).find('.sip_max').val(callCount.total)
//console.log(callCount)
		$(ts).find('.totals').text(callCount.total)
		$(ts).find('.haveUsedCount').text(callCount.haveUsedCount)
}
function changeSip(that){
	var wrap = $(that).parent().parent().parent()
	
	var callCount = JSON.parse($(that).children('option:selected').attr('data_port'))
	wrap.find('.totals').text(callCount.total)
	wrap.find('.haveUsedCount').text(callCount.haveUsedCount)
	wrap.find('.sip_max').attr('max',callCount.haveUsedCount)
}
function removeGateway(that){
	layer.msg('确定要删除该网关吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
      		$(that).parent().remove()
    	}
   });	
}
function checkMax(that){
//	var thatVal = parseInt($(that).val()) ;
//	var thatMax = parseInt($(that).attr('max'))
//	console.log(thatVal,thatVal + thatMax)
//	if(thatVal>thatVal + thatMax){
//		layer.msg('拨打上限不能超过' + (thatVal + thatMax))
//		$(that).val(thatVal + thatMax)
//	}
	
	if(parseInt($(that).val())){
		if(parseInt($(that).val())>parseInt($(that).attr('max'))){			
			$(that).val($(that).attr('max'))
			if($(that).val()=='0'){
				setTimeout(function(){layer.msg('拨打上限已满，请换其他线路')},100)
			}else{
				layer.msg('拨打上限不能超过' + $(that).attr('max'))
			}
			
		}
	}
	
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
