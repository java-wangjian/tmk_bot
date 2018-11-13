//var port_pre ;

var allGatewayList = []
var allSipList = []

function init(){
	getGatewayAndPort()
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
	$("#city_1").citySelect({
        prov: "北京",
        required: false
    });
}
//function allSelect(that){
//	var status = $(that).children('span')
//	if($(that).children('.iconfont').hasClass('icon-xuanze')){
//		$(that).parent().find('.iconfont').removeClass('icon-xuanze').addClass('icon-choose')		
//		status.text('全选')
//	}else{
//		$(that).parent().find('.iconfont').removeClass('icon-choose').addClass('icon-xuanze')
//		status.text('取消')
//	}
//}
function allSelect(that){
	var status = $(that).children('span')
	if($(that).children('.iconfont').hasClass('icon-xuanze')){	
		$(that).parent().find('.iconfont').each(function(index,val){
			if($(val).attr('userId')==''||!$(val).attr('userId')){
				$(val).removeClass('icon-xuanze').addClass('icon-choose')
			}
		})					
		status.text('全选')
	}else{
		$(that).parent().find('.iconfont').each(function(index,val){
			if($(val).attr('userId')==''||!$(val).attr('userId')){
				$(val).removeClass('icon-choose').addClass('icon-xuanze')
			}
		})
		status.text('取消')
	}
}
function getGatewayAndPort(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/getGatewayAndPort",
		data:{adminId:sessionStorage.getItem('adminId')},
		success:function(data){
			const res = JSON.parse(data)
			$.each(res, function(index,value) {
				if(value.gatewayType==1){
					allGatewayList.push(value)
				}else if(value.gatewayType==2){
					allSipList.push(value)
				}
			});
//			port_pre = res			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function changeThis(that){
	var thatEle = $(that).find('.iconfont')
	if(thatEle.attr('userId')==''||thatEle.attr('userId')==getQueryString('userid')){
		thatEle.toggleClass('icon-choose').toggleClass('icon-xuanze')
	}else{		
		layer.msg('该端口已经被分配，请选择其他端口')		
	}
//	$(that).find('.iconfont').toggleClass('icon-choose').toggleClass('icon-xuanze')
}
function changeTab(that){
	$(that).toggleClass('blueBg').toggleClass('grayBg')
	$(that).siblings().toggleClass('blueBg').toggleClass('grayBg')
	$(that).parent().parent().parent().find('.portsBox').css('display','none')
	var ind = $(that).index()
	$(that).parent().parent().parent().find('.portsBox:eq('+ind+')').css('display','block')
}
function addPort(){
	var options = ''
	$.each(allGatewayList, function(index,value) {
//		console.log(value.portList)
//		options += '<option data_port="'+JSON.stringify(value.portList)+'" >'+value.gateway+'</option>'
		options += "<option gatewayId='"+value.gatewayId+"' data_port='"+JSON.stringify(value)+"' >"+value.gateway+"</option>"
	});
	
	var str = '<div gatewayType="1" class="portWrap">'
		str+= '<button onclick="removeGateway(this)" class="removeGateway">X</button>'
		str+= '<div class="port03">'
		str+= '<label class="port00">网关编号：</label>'
		str+= '<div class="port01">'
//		str+= '<input class="port02 dk_num" type="text" />'
		str+='<select onchange="changePort(this)" class="port02 dk_num">'+options+'</select>'
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
function sendAccountMsg(){
	var portsList = $('.portWrap')
	var gateway = []
	$.each(portsList, function(index,value) {
		
		if($(value).attr('gatewayType')=='1'){
			var temp = {} 
			temp.gatewayId =  $(value).find('.dk_num').children('option:selected').attr('gatewayId')
			temp.gatewayURL = $(value).find('.wg_num').val()   //网关URL
		
			temp.auth = $(value).find('.wg_auth').val()   //网关URL
			temp.pwd = $(value).find('.wg_pwd').val()   //网关URL
		
			var ports = [] 
			var porti = []
			$(value).find('.portD').children('.icon-xuanze').each(function(ind,val){
				ports.push(parseInt($(val).attr('data')))
			})
			$(value).find('.portP').children('.icon-xuanze').each(function(ind,val){
				porti.push(parseInt($(val).attr('data')))
			})
			temp.callPortList = ports  //拨打端口
			temp.transferPortList = porti
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
		account: $('#account').val(),
		addUser:1,
		company:$('#company').val(),
		contactPerson:$('#linkman').val(),
		contactPhone:$('#phone').val(),
		activeTime:$('#start').val(),
		validTime:$('#end').val(),
		gatewayArrStr:JSON.stringify(gateway),
//		gatewayArrStr:gateway,
		password:123456,
		adminId:sessionStorage.getItem('adminId'),
		city:$('#provs').val()+'-'+$('#citys').val(),
	}
	if(msg.account==''||msg.company==''||msg.linkman==''||msg.validTime==''||msg.activeTime==''){
		layer.msg('基本信息不能为空')
		return;
	}
	if(!/^(?=.{6,16})(?=.*[a-z])(?=.*[0-9])[0-9a-z]*$/.test(msg.account)){  
		layer.msg('账户只能是6-16位数字字母的组合')
		return;
	}
	if(!/^[\u4e00-\u9fa5]+$/.test(msg.contactPerson)){
		layer.msg('联系人请输入汉字')
		return;
	}
	if(!/^[\u4e00-\u9fa5]+$/.test(msg.company.replace(/\(/,"").replace(/\)/,"").replace(/\（/,"").replace(/\）/,""))){
		layer.msg('公司请输入汉字')
		return;
	}
	if(!/^1[345678]\d{9}$/.test(msg.contactPhone)){
		layer.msg('手机号格式不正确')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/addUser",
		data:msg,
		success:function(data){
			var res = JSON.parse(data)
           	console.log(res.userId)
           	if(res.code==402){
           		layer.msg('该账号已存在')
           		return
           	}
           	if(res.code==405){
           		layer.msg('用户到期时间不能比代理商到期时间晚')
           		return
           	}
            addProName(res.userId)
            getGatewayAndPort()
		},
		error:function(data){
			console.log(data)
		}
	});
}
function goback(){
	window.location.href = 'index.html'
}
function addProName(userId){
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
		title: "创建账户",
		shadeClose: false,
		scrollbar: false,
		move: false,
   		area: ['5.2rem', '2.4rem'],
		content: tpEle
	});
	$('#qxCreate').click(function(){
		window.location.href = 'index.html'
	})	
	$('#sureCreate').click(function(){
		confirmProName(userId)
	})
}
function confirmProName(userId){	  //确定添加	
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/addProject",
		data:{
			projectName:$('#newCompanyName').val(),
			userId:userId
		},
		success:function(data){
			const res = JSON.parse(data)
			console.log(res)
			layer.closeAll()
			window.location.href='newpro.html?projectID='+res.projectID			
		},
		error:function(data){
			console.log(data)
		}
	});
	
}
function changePort(that){
	var wrap = $(that).parent().parent().parent()
	
	var havePortlist = JSON.parse($(that).children('option:selected').attr('data_port'))
	
//	console.log(havePortlist)
	
	var portSpanD = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
	var portSpanP = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
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
function setGray(){
	var pots = $('.portsBox')
	$.each(pots, function(index,value) {
		$(value).find('.iconfont').each(function(i,val){
			var thisEle = $(val)	
			if(thisEle.attr('userId')&&thisEle.attr('userId')!==''){
				thisEle.css('color','#999')
				thisEle.parent().css('color','#999')
			}
		})
	});
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
		str+= '<input max="" onblur="checkMax(this)" class="port02 sip_max" type="text" value=""/>'
		str+= '</div>'
		str+= '<span class="ps01"><span class="haveUsedCount"></span>/<span class="totals"></span></span>'
		str+= '</div>'

		str+= '</div>'	
		
		var ts = $(str).appendTo('#portMsgBox')
		var callCount = JSON.parse($(ts).find('.sip_num').children('option:selected').attr('data_port'))
		$(ts).find('.totals').text(callCount.total)
		$(ts).find('.haveUsedCount').text(callCount.haveUsedCount)
		$(ts).find('.sip_max').attr('max',callCount.haveUsedCount)
}
function changeSip(that){
	var wrap = $(that).parent().parent().parent()
	
	var callCount = JSON.parse($(that).children('option:selected').attr('data_port'))
//	console.log(callCount)
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
	if(parseInt($(that).val())){
		if(parseInt($(that).val())>parseInt($(that).attr('max'))){
			if($(that).val()=='0'){
				setTimeout(function(){layer.msg('拨打上限已满，请换其他线路')},100)
			}else{
				layer.msg('拨打上限不能超过' + $(that).attr('max'))
			}
		}
	}
	
}
