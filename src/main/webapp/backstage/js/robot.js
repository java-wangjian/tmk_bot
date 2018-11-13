var prev = 1;
var isHistory = false; 
var allSipList = []
var allGatewayList = []
function init(){
	$('#curAccount').text(sessionStorage.getItem('adminAccount'))	
	getGatewayAndPort()
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
	getSipList()
	getGatewayList()
//	getSimList(1)   //充值历史
	getCost(1)
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
function getGatewayList(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/findProjectByUserId",
		data:{ userId:getQueryString('userId') ,adminId: sessionStorage.getItem('adminId')},
		success:function(data){
			var res = JSON.parse(data)
			var cardStr = ""
			if(res.gatewayAndPortArr.length == 0){
				var nomess = "<li class='vosItem'><img style='height: 1.2rem;margin: 0 auto;display: block;margin-top: 0.3rem;' src='../images/sip_no_mess.png' /><button class='addSip0' onclick='addSipOrVos()'>添加线路</button></li>"
				$('#vosBox').html(nomess)
				return;
			}
            $.each(res.gatewayAndPortArr, function(index,value) {
            	if(value.gatewayType == 1){
            		cardStr+= "<div gatewayId='"+value.gatewayId+"' type='1' class='vosItem'>"
            		cardStr+= "<button class='delGateway' onclick='delGateway(this)'>X</button>"
            		cardStr+= "<button gatewayMsg='"+JSON.stringify(value)+"' style='background:#FFA800' onclick='editGateway(this)' class='editSipBtn'><i class='iconfont icon-bianji'></i> 编辑</button>"
            		cardStr+= "<p class='vosName'>网关</p>"
            		cardStr+= "<p class='vosIcon'><i class='iconfont icon-dianhua'></i></p>"
            		cardStr+= "<p>"
            		cardStr+= "<span class='mark mark1'></span><span>转接</span>"
            		cardStr+= "<span class='mark mark2'></span><span>拨打</span>"
            		cardStr+= "</p>"
            		cardStr+= "<div class='portBox'>"            
            		
            		var allPortsList = []
            		
            		for (var k = 0;k<value.callPortList.length;k++) {
            			value.callPortList[k].portType = 1
						allPortsList.push(value.callPortList[k])
					}
            		for (var m = 0;m<value.transferPortList.length;m++) {
            			value.transferPortList[m].portType = 2
						allPortsList.push(value.transferPortList[m])
					}
            		for (var i=0;i<value.allPort.length;i++) {
						var tempStr = '<span class="portIconBox"><i userId="'+value.allPort[i].userId+'" data="'+value.allPort[i].port+'" class="imgIcon unClick"></i><span class="portNum">'+value.allPort[i].port+'</span></span>'
			
						for (var j = 0;j<allPortsList.length;j++) {
							if(value.allPort[i].port == allPortsList[j].port){
								var showColor = allPortsList[j].portType == 1 ? 'yellowUncheck' : 'blueUncheck'
								tempStr = "<span class='portIconBox'><i userId='"+value.allPort[i].userId+"' data='"+value.allPort[i].port+"' class='imgIcon "+showColor+"'></i><span class='portNum'>"+value.allPort[i].port+"</span></span>"
							}
						}			
						cardStr += tempStr
					}           		
            		cardStr+= "</div>"
            		cardStr+= "</div>"																	
            	}else{
            		cardStr+= "<div gatewayId='"+value.gatewayId+"' type='2' class='vosItem'>"
            		cardStr+= "<button class='delGateway' onclick='delGateway(this)'>X</button>"
            		cardStr+= "<button sipMsg='"+JSON.stringify(value)+"' onclick='editSip(this)' class='editSipBtn'><i class='iconfont icon-bianji'></i>编辑</button>"
            		cardStr+= "<p class='vosName'>"+value.gatewayNumbers+"</p>"
            		cardStr+= "<p class='vosIcon'><i class='iconfont icon-dianhua'></i></p>"
            		cardStr+= "<p sipName='"+value.gatewayNumbers+"' sipId='"+value.gatewayId+"' class='rechargeIcon' onclick='rechargeBox(this)'>续费充值</p>"
            		cardStr+= "<div class='vosItemFlex'>"
            		cardStr+= "<div>"
            		cardStr+= "<p>剩余话费</p>"
            		cardStr+= "<p><span class='strongs'>"+value.balanceMoney+"</span><span>元</span></p>"
            		cardStr+= "</div>"
            		cardStr+= "<div>"
            		cardStr+= "<p>收费单价</p>"
            		
            		var price = value.unitPrice == '' ? '0' : value.unitPrice 
            		
            		cardStr+= "<p><span class='strongs'>"+price+"</span><span>元/分钟</span></p>"
            		cardStr+= "</div>"
            		cardStr+= "<div>"
            		cardStr+= "<p>剩余时长</p>"
            	
            		var leftTime = value.balanceMoney == 0 ? 0 : (value.leftover/3600).toFixed(2)
            	
            		cardStr+= "<p><span class='strongs'>"+leftTime+"</span><span>小时</span></p>"
            		cardStr+= "</div>"
            		cardStr+= "</div>"
            		cardStr+= "</div>"	   
            	}
            });
            $('#vosBox').html(cardStr) 
//			getVosList() 
		},
		error:function(data){
			console.log(data)
		}
	});
}
function setGray(){
	var pots = $('#portBox')
	$.each(pots, function(index,value) {
		$(value).find('.portIconBox').each(function(i,val){
			var thisEle = $(val).children('i')	
			if(thisEle.attr('userId') && thisEle.attr('userId') !== ''&&thisEle.attr('userId') !== getQueryString('userId')){
				thisEle.next().css('color','#999')
				thisEle.removeClass('whiteUncheck').addClass('unClick')
			}
		})
	});
}

function getSipList(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateway/getUserSipData",
		data:{ userId:getQueryString('userId') },
		success:function(data){
			var res = JSON.parse(data)
			var cardStr = "<option value=''>全部</option>"		
            $.each(res.data, function(index,value) {
            	cardStr+= "<option value='"+value.sip_id+"'>"+value.sip_name+"</option>"				
            });
            $('#vosSelect').html(cardStr)          
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getSimList(page){
	var simCardObj = {
		adminId:sessionStorage.getItem('adminId'),
		userId:getQueryString('userId'),
		page: page,
		per:10,	
		sipId:$('#vosSelect').val(),
		startDate:$('#start').val() == '' ? '' : $('#start').val() + " 00:00:00",
		endDate:$('#end').val() == '' ? '' : $('#end').val() + " 23:59:59"
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/finance/userRechargeRecord",
		data:simCardObj,
		success:function(data){
			var res = JSON.parse(data)
			
            var cardStr = ""
            if(res.data.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;' src='../images/list_no_mess.png' /></li>"
				$('#simCardUl').html(nomess)
				$('#totalMoney').html("")
				return;
			}else{
				$('#totalMoney').html("累计充值金额：<span style='color:#333;font-size:0.26rem'>"+res.data.total.totalMoney+"</span>元")
            	$.each(res.data.list, function(index,value) {
            		cardStr+= "<li class='accMgrMainLi accMgrItem'>"
            		cardStr+= "<span class='flex2 color999'>"+(index+1)+"</span>"
            		cardStr+= "<span class='flex2 color999'>"+value.sipName+"</span>"
            		cardStr+= "<span class='flex2 color666'>"+value.unitPrice+"</span>"          	
            		cardStr+= "<span class='flex2 color666'>"+(value.sumMoney/value.unitPrice).toFixed(2)+"</span>"
            		cardStr+= "<span class='flex2 color999'>"+value.addTime+"</span>"
            		cardStr+= "<span class='flex2 color666'>"+value.sumMoney+"</span>"
            		cardStr+= "</li>"
            	});
           	 	$('#simCardUl').html(cardStr)
			}
            
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
function getCost(page){
	var simCardObj = {
		userId:getQueryString('userId'),
		page: page,
		per:10,	
		sipId:$('#vosSelect').val(),
		startDate:$('#start').val() == '' ? '' : $('#start').val() + " 00:00:00",
		endDate:$('#end').val() == '' ? '' : $('#end').val() + " 23:59:59" ,
		prev:prev++
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/finance/userCostRecord",
		data:simCardObj,
		success:function(data){
			var res = JSON.parse(data)
//			if(simCardObj.prev == 1){
//				var singleDate = (res.data.list[0].datetime.split(' ')[0])
//				$('#start,#end').val(singleDate)
//			}
            var cardStr = ""
            
			if(res.data.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;' src='../images/list_no_mess.png' /></li>"
				$('#simCardUl_1').html(nomess)
				$('#totalMoney').html("")
				return;
			}else{
				$('#totalMoney').html("累计消费金额：<span style='color:#333;font-size:0.26rem'>"+res.data.totalMoney+"</span>元")
            	$.each(res.data.list, function(index,value) {
            		cardStr+= "<li class='accMgrMainLi accMgrItem'>"
            		cardStr+= "<span class='flex2'>"+(index+1)+"</span>"
            		cardStr+= "<span class='flex2'>"+value.phone+"</span>"
            		cardStr+= "<span class='flex2'>"+value.sip_name+"</span>"  
            		var dates = new Date(value.datetime).toLocaleString()
            		cardStr+= "<span class='flex2' style='line-height: 0.28rem;'>"+dates.split(' ')[0]+"<br />"+dates.split(' ')[1]+"</span>"
            		cardStr+= "<span class='flex2'>"+value.unit_price+"</span>"
            		cardStr+= "<span class='flex2'>"+formatSeconds(value.durat_time)+"</span>"
            		cardStr+= "<span class='flex2'>"+value.money+"</span>"          	
            		cardStr+= "</li>"
            	});
            	$('#simCardUl_1').html(cardStr)
			}
			
            $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.data.total/10),
				totalSize: res.data.total,
				callback: function(current) {
					getCost(current)
				}
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}
function changeList(e){
	e = e || window.event
	$(e.target).addClass('checkedTabs').removeClass('unCheckedTabs')
	$(e.target).siblings().addClass('unCheckedTabs').removeClass('checkedTabs')
	if($(e.target).attr('tabindex') == '0'){
		isHistory = true
		$('#start,#end').val('')
		$('#table0').css('display','block')
		$('#table1').css('display','none')
		getSimList(1)
	}else{
		isHistory = false
		$('#table0').css('display','none')
		$('#table1').css('display','block')
		getCost(1)
	}
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
function searchList(){
	if(isHistory){
		getSimList(1)
	}else{
		getCost(1)
	}
}
function rechargeBox(that){
		var tpEle  = "<div class='singleMsgBox'>"
		
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>充值金额：</span>"
	    tpEle += "<input type='' name='' id='money' maxlength='11'  value='1' />"
	    tpEle += "<b>元</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='margin-top:0.2rem' class='singleMsgBoxItem'>"
	    tpEle += "<span>单价：</span>"
	    tpEle += "<input type='' name='' id='price' maxlength='10' value='0.1'/>"
	    tpEle += "<b>分钟/元</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button sipName='"+$(that).attr('sipName')+"' sipId='"+$(that).attr('sipId')+"' onclick='recharge(this)' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "充值",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '3rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		layer.close(singleUp)
	}) 
}
function recharge(that){
	var money = $('#money').val()
	var price = $('#price').val()
	if(!isNumber(money)){
		layer.msg('充值金额不是数字')
		return
	}
	if(parseFloat(money) <= 0){
		layer.msg('充值金额必须大于0')
		return
	}
	if(!isNumber(price) || price == '0'){
		layer.msg('单价必须是不为零的数字')
		return
	}
	var finTime = Math.floor(parseFloat(money)/parseFloat(price))
	layer.msg('确定充值（充值时长'+finTime+'分钟）？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.closeAll()
      		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/finance/rechargeSipBalanceToUser",
				data:{
					adminId:sessionStorage.getItem('adminId'),
					userId:getQueryString('userId'),
					sipId:$(that).attr('sipId'),
					sipName:$(that).attr('sipName'),
					unitPrice:price,
					rechargePrice:money
				},
				success:function(data){
					var res = JSON.parse(data)
					if(res.code == 0){
						layer.msg('充值成功')
						getSimList(1)
						getSipList()
						getGatewayList()
					}else{
						layer.msg(res.result)
					}
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
   	});
}
function isNumber(val){
    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if(regPos.test(val) || regNeg.test(val)){
        return true;
    }else{
        return false;
    }

}
function editSip(that){
	var sipMsg = JSON.parse($(that).attr('sipMsg'))
	console.log(sipMsg)
	var tpEle  = "<div class='singleMsgBox'>"
		
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>线路名称：</span>"
	    tpEle += "<b>"+sipMsg.gatewayNumbers+"</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='margin-top: 0.2rem;'  class='singleMsgBoxItem'>"
	    tpEle += "<span>拨打上限：</span>"
	    tpEle += "<input max='"+(parseInt(sipMsg.haveUsedCount)+parseInt(sipMsg.callCount))+"' type='' name='' id='maxCall' maxlength='10' value='"+sipMsg.callCount+"'/>"
	    tpEle += "<b>"+sipMsg.haveUsedCount+"/"+sipMsg.total+"</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button sipId='"+sipMsg.gatewayId+"' onclick='sureSip(this)' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "编辑线路",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '3rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		layer.close(singleUp)
	}) 
}
function sureSip(that){
	if(!isNumber($('#maxCall').val())){
		layer.msg("拨打上限只能是数字")
		return;
	}
	if(parseInt($('#maxCall').val()) > parseInt($('#maxCall').attr('max'))){
		layer.msg("拨打上限不能超过" + $('#maxCall').attr('max'))
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateways/updateProject",
		data:{
			userId:getQueryString('userId'),
			gatewayId:$(that).attr('sipId'),
			gatewayType:2,
			callCount:parseInt($('#maxCall').val())
		},
		success:function(data){
			var res = JSON.parse(data)
			if(res.result == 0){
				layer.closeAll()
				layer.msg('编辑成功')
				getGatewayList()
			}else{
				layer.msg('编辑失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function editGateway(that){
	var gatewayMsg = JSON.parse($(that).attr('gatewayMsg'))
	var tpEle  = "<div class='singleMsgBox'>"
		
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关编号：</span>"
	    tpEle += "<b>"+gatewayMsg.gatewayNumbers+"</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关账号：</span>"
	    tpEle += "<input type='' name='' id='auth' maxlength='10' value='"+gatewayMsg.auth+"'/>"
	    tpEle += "<b></b>"
	    tpEle += "</div>"
	    
	     tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关密码：</span>"
	    tpEle += "<input type='' name='' id='pwd' maxlength='10' value='"+gatewayMsg.pwd+"'/>"
	    tpEle += "<b></b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关URL：</span>"
	    tpEle += "<b id='url'>"+gatewayMsg.url+"</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>端口信息：</span>"
	    tpEle += "</div>"
	    
	    tpEle += "<p><span class='mark mark2'></span><span>拨打</span><span class='mark mark1'></span><span>转接</span></p>"
	    
	    tpEle+= "<div style='height: 1.4rem;' id='portBox' class='portBox'>"
        
        
        var allPortsList = []
            		
        for (var k = 0;k<gatewayMsg.callPortList.length;k++) {
            gatewayMsg.callPortList[k].portType = 1
			allPortsList.push(gatewayMsg.callPortList[k])
		}
        for (var m = 0;m<gatewayMsg.transferPortList.length;m++) {
            gatewayMsg.transferPortList[m].portType = 2
			allPortsList.push(gatewayMsg.transferPortList[m])
		}
        for (var i=0;i<gatewayMsg.allPort.length;i++) {
			var tempStr = "<span onclick='changeThis(this)' class='portIconBox'><i userId='"+gatewayMsg.allPort[i].userId+"' data='"+gatewayMsg.allPort[i].port+"' class='imgIcon whiteUncheck'></i><span class='portNum'>"+gatewayMsg.allPort[i].port+"</span></span>"
			
			for (var j = 0;j<allPortsList.length;j++) {
				if(gatewayMsg.allPort[i].port == allPortsList[j].port){
					var showColor = allPortsList[j].portType == 1 ? 'yellowUncheck' : 'blueUncheck'
					tempStr = "<span onclick='changeThis(this)' class='portIconBox'><i userId='"+gatewayMsg.allPort[i].userId+"' data='"+gatewayMsg.allPort[i].port+"' class='imgIcon "+showColor+"'></i><span class='portNum'>"+gatewayMsg.allPort[i].port+"</span></span>"
				}
			}			
			tpEle += tempStr
		}           
        
        
        
        
        tpEle+= "</div>"
        
        tpEle+= "<div style='margin-top:0.1rem'>"
        tpEle+= "<button id='delPort' style='display:none' onclick='delPort()' class='editGatewayBtn left shortBtn redBg'>删除</button>"       
        tpEle+= "<button onclick='addToCall()' class='editGatewayBtn right longBtn yellowBg'>添加至拨打端口</button>"
        tpEle+= "<button onclick='addToTrans()' class='editGatewayBtn right longBtn blueBg'>添加至转接端口</button>"
        tpEle+= "</div>"
	    
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button sipId='"+gatewayMsg.gatewayId+"' onclick='editGatewayAjax(this)' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "编辑网关",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['8rem', '6.8rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		layer.close(singleUp)
	}) 
	setGray()
}
function setGray(){
	var pots = $('#portBox')
	$.each(pots, function(index,value) {
		$(value).find('.portIconBox').each(function(i,val){
			var thisEle = $(val).children('i')	
			if(thisEle.attr('userId') && thisEle.attr('userId') !== ''&&thisEle.attr('userId') !== getQueryString('userId')){
				thisEle.next().css('color','#999')
				thisEle.removeClass('whiteUncheck').addClass('unClick')
			}
		})
	});
}
function addSipOrVos (){
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
		addGateway()
		break;
		case '2':
		addSip()
		break;
	}
	
}
function addSip(){
	
	var options = ''
	
	var all = allSipList.concat()

//	console.log(all)

	var haveGateway = $("#vosBox").children('.vosItem[type=2]')
	for(var i = 0;i<all.length;i++){
		
		for(var j =0;j<haveGateway.length;j++) {
			if(parseInt($(haveGateway[j]).attr('gatewayId')) == parseInt(all[i].gatewayId)){
				all.splice(i,1)
				i = i-1;
				j = j-1;
				break;
			}
		}
		
	}
	
	$.each(all, function(index,value) {
		options += "<option gatewayId='"+value.gatewayId+"' data_port='"+JSON.stringify(value)+"' >"+value.gateway+"</option>"	
	});
	
	var tpEle  = "<div id='sipInfo' class='singleMsgBox'>"
		
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span class='left'>线路名称：</span>"
	    tpEle += "<div class='left'><select style='width: 2rem;' onchange='changeSip(this)' class='port02 sip_num'>"+options+"</select></div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div style='margin-top: 0.2rem;' class='singleMsgBoxItem'>"
	    tpEle += "<span>拨打上限：</span>"
	    tpEle += "<input class='sip_max' max='' type='' name='' id='maxCall' maxlength='10' value=''/>"
	    tpEle += "<b class='haveUsedCount'></b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button onclick='sureEditSip()' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "新增线路",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '3rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		layer.close(singleUp)
	}) 
	var wrap = $("#sipInfo")
	var callCount = JSON.parse(wrap.find('.sip_num').children('option:selected').attr('data_port'))
	wrap.find('.sip_max').attr('max',callCount.haveUsedCount)
	wrap.find('.haveUsedCount').text(callCount.haveUsedCount + "/" + callCount.total)
}
function changeSip(that){
	var wrap = $(that).parent().parent().parent()
	
	var callCount = JSON.parse($(that).children('option:selected').attr('data_port'))
	wrap.find('.sip_max').attr('max',callCount.haveUsedCount)
	wrap.find('.haveUsedCount').text(callCount.haveUsedCount + "/" + callCount.total)
}
function sureEditSip(){    //新增线路请求
	if(!isNumber($('#maxCall').val())){
		layer.msg("拨打上限只能是数字")
		return;
	}
	if(parseInt($('#maxCall').val()) > parseInt($('#maxCall').attr('max'))){
		layer.msg("拨打上限不能超过" + $('#maxCall').attr('max'))
		return;
	}
	var data = {
		userId:getQueryString('userId'),
		gatewayId: $("#sipInfo").find('.sip_num').children('option:selected').attr('gatewayId'),
		gatewayType:2,
		callCount:parseInt($('#maxCall').val())
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateways/updateProject",
		data:data,
		success:function(data){
			var res = JSON.parse(data)
			if(res.result == 0){
				layer.closeAll()
				layer.msg('添加成功')
				getGatewayList()
			}else{
				layer.msg('添加失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function addGateway(){
	var options = ''	
	var all = allGatewayList.concat()	
	var haveGateway = $("#vosBox").children('.vosItem[type=1]')
	for(var i = 0;i<all.length;i++){
		
		for(var j =0;j<haveGateway.length;j++) {
			if(parseInt($(haveGateway[j]).attr('gatewayId')) == parseInt(all[i].gatewayId)){
				all.splice(i,1)
				i = i-1;
				j = j-1;
				break;
			}
		}
		
	}
	$.each(all, function(index,value) {
			options += "<option gatewayId='"+value.gatewayId+"' data_port='"+JSON.stringify(value)+"' >"+value.gateway+"</option>"		
	});
	
	var tpEle  = "<div id='gatewayInfo' class='singleMsgBox'>"
		
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span class='left'>网关编号：</span>"
	    tpEle += "<div class='left'><select style='width: 2rem;' onchange='changePort(this)' class='port02 dk_num'>"+options+"</select></div>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关账号：</span>"
	    tpEle += "<input class='wg_auth' type='' name='' id='auth' maxlength='10' value=''/>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关密码：</span>"
	    tpEle += "<input class='wg_pwd' type='' name='' id='pwd' maxlength='10' value=''/>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>网关URL：</span>"
	    tpEle += "<input class='wg_url' type='' name='' id='url' maxlength='10' value=''/>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>端口信息：</span>"
	    tpEle += "</div>"
	    
	    tpEle += "<p><span class='mark mark2'></span><span>拨打</span><span class='mark mark1'></span><span>转接</span></p>"
	    
	    tpEle+= "<div style='height: 1.4rem;' id='portBox' class='portBox'>"
                 
        
        
        tpEle+= "</div>"
        
        tpEle+= "<div style='margin-top:0.1rem'>"
        tpEle+= "<button id='delPort' style='display:none' onclick='delPort()' class='editGatewayBtn left shortBtn redBg'>删除</button>"       
        tpEle+= "<button onclick='addToCall()' class='editGatewayBtn right longBtn yellowBg'>添加至拨打端口</button>"
        tpEle+= "<button onclick='addToTrans()' class='editGatewayBtn right longBtn blueBg'>添加至转接端口</button>"
        tpEle+= "</div>"
	    
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button  onclick='addNewGateway()' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "新增网关",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['8rem', '6.8rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		layer.close(singleUp)
	}) 
	
	var wrap = $('#gatewayInfo')
	var gatewayInfo = JSON.parse(wrap.find('.dk_num').children('option:selected').attr('data_port'))
	wrap.find('.dk_num').children('option:selected').attr('data_port')
	wrap.find('.wg_auth').val(gatewayInfo.auth)
	wrap.find('.wg_pwd').val(gatewayInfo.pwd)
	wrap.find('.wg_url').val(gatewayInfo.url)
	
	var tempStr = ""
	for (var i=0;i<gatewayInfo.portList.length;i++) {
//		if(thisEle.attr('userId') && thisEle.attr('userId') !== ''&&thisEle.attr('userId') !== getQueryString('userId')){}
		tempStr += "<span onclick='changeThis(this)' class='portIconBox'><i userId='"+gatewayInfo.portList[i].userId+"' data='"+gatewayInfo.portList[i].port+"' class='imgIcon whiteUncheck'></i><span class='portNum'>"+gatewayInfo.portList[i].port+"</span></span>"
	}
	$("#portBox").html(tempStr)
//      var allPortsList = []
//          		
//      for (var k = 0;k<gatewayMsg.callPortList.length;k++) {
//          gatewayMsg.callPortList[k].portType = 1
//			allPortsList.push(gatewayMsg.callPortList[k])
//		}
//      for (var m = 0;m<gatewayMsg.transferPortList.length;m++) {
//          gatewayMsg.transferPortList[m].portType = 2
//			allPortsList.push(gatewayMsg.transferPortList[m])
//		}
//      for (var i=0;i<gatewayMsg.allPort.length;i++) {
//			var tempStr = "<span onclick='changeThis(this)' class='portIconBox'><i userId='"+gatewayMsg.allPort[i].userId+"' data='"+gatewayMsg.allPort[i].port+"' class='imgIcon whiteUncheck'></i><span class='portNum'>"+gatewayMsg.allPort[i].port+"</span></span>"
//			
//			for (var j = 0;j<allPortsList.length;j++) {
//				if(gatewayMsg.allPort[i].port == allPortsList[j].port){
//					var showColor = allPortsList[j].portType == 1 ? 'yellowUncheck' : 'blueUncheck'
//					tempStr = "<span onclick='changeThis(this)' class='portIconBox'><i userId='"+gatewayMsg.allPort[i].userId+"' data='"+gatewayMsg.allPort[i].port+"' class='imgIcon "+showColor+"'></i><span class='portNum'>"+gatewayMsg.allPort[i].port+"</span></span>"
//				}
//			}			
//			tpEle += tempStr
//		}           
//      
      
	
	setGray()
}
function changePort(that){
	var wrap = $('#gatewayInfo')
	var gatewayInfo = JSON.parse(wrap.find('.dk_num').children('option:selected').attr('data_port'))
	console.log(gatewayInfo)
	wrap.find('.wg_auth').val(gatewayInfo.auth)
	wrap.find('.wg_pwd').val(gatewayInfo.pwd)
	wrap.find('.wg_url').val(gatewayInfo.url)
	var tempStr = ""
	for (var i=0;i<gatewayInfo.portList.length;i++) {
//		if(thisEle.attr('userId') && thisEle.attr('userId') !== ''&&thisEle.attr('userId') !== getQueryString('userId')){}
		tempStr += "<span onclick='changeThis(this)' class='portIconBox'><i userId='"+gatewayInfo.portList[i].userId+"' data='"+gatewayInfo.portList[i].port+"' class='imgIcon whiteUncheck'></i><span class='portNum'>"+gatewayInfo.portList[i].port+"</span></span>"
	}
	$("#portBox").html(tempStr)
	
//	var wrap = $(that).parent().parent().parent()
//	var havePortlist = JSON.parse($(that).children('option:selected').attr('data_port'))
//	
//	var portSpanD = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
//	var portSpanP = '<span onclick="allSelect(this)" class="ports"><i class="iconfont icon-choose"></i><span>全选</span></span>'
//	
//	$.each(havePortlist.portList, function(index,value) {
//		portSpanD += '<span onclick="changeThis(this)" class="ports portD"><i userId="'+value.userId+'" data="'+value.port+'" class="iconfont icon-choose"></i><span>'+value.port+'</span></span>'
//		portSpanP += '<span onclick="changeThis(this)" class="ports portP"><i userId="'+value.userId+'" data="'+value.port+'" class="iconfont icon-choose"></i><span>'+value.port+'</span></span>'
//	});
//	wrap.find('.wg_auth').val(havePortlist.auth)
//	wrap.find('.wg_pwd').val(havePortlist.pwd)
//	wrap.find('.wg_num').val(havePortlist.url)
//	
//	var boxs = wrap.find('.portsBox')
//		
//	$($(boxs)[0]).html(portSpanD)
//	$($(boxs)[1]).html(portSpanP)
//	
	setGray()
}
function delGateway(that){
	layer.msg('确定删除吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.closeAll()
      		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/gateways/deleteProject",
				data:{
					userId:getQueryString('userId'),
					gatewayId: $(that).parent().attr('gatewayId')
				},
				success:function(data){
					var res = JSON.parse(data)
					if(res.result == 0){
						layer.closeAll()
						layer.msg('删除成功')
						getGatewayList()
					}else{
						layer.msg('添加失败')
					}
				},
				error:function(data){
					console.log(data)
				}
			});
    	}
   	});
}
function changeThis(that){
	var thatEle = $(that).find('.imgIcon')   //图标元素
	if(thatEle.attr('userId')==''||thatEle.attr('userId')==getQueryString('userid')){
		if(thatEle.hasClass('whiteUncheck')){
			thatEle.removeClass('whiteUncheck').addClass('whiteChecked')
		}else if(thatEle.hasClass('whiteChecked')){
			thatEle.removeClass('whiteChecked').addClass('whiteUncheck')
		}
		if(thatEle.hasClass('yellowUncheck')){
			thatEle.removeClass('yellowUncheck').addClass('yellowChecked')
			$("#delPort").css('display','block')
		}else if(thatEle.hasClass('yellowChecked')){
			thatEle.removeClass('yellowChecked').addClass('yellowUncheck')
		}
		if(thatEle.hasClass('blueUncheck')){
			thatEle.removeClass('blueUncheck').addClass('blueChecked')
			$("#delPort").css('display','block')
		}else if(thatEle.hasClass('blueChecked')){
			thatEle.removeClass('blueChecked').addClass('blueUncheck')
		}
	}else{		
		layer.msg('该端口已经被分配，请选择其他端口')		
	}
}
function addToCall(){
	$("#portBox .whiteChecked").removeClass('whiteChecked').addClass('yellowUncheck')
}
function addToTrans(){
	$("#portBox .whiteChecked").removeClass('whiteChecked').addClass('blueUncheck')
}
function delPort(){
	$("#portBox .blueChecked").removeClass('blueChecked').addClass('whiteUncheck')
	$("#portBox .yellowChecked").removeClass('yellowChecked').addClass('whiteUncheck')
}
function editGatewayAjax(that){
	var callList = []
	var transList = []
	$("#portBox .yellowUncheck").each(function(index,val){
		callList.push(parseInt($(val).attr('data')))
	})
	$("#portBox .blueUncheck").each(function(index,val){
		transList.push(parseInt($(val).attr('data')))
	})
	if(callList.length==0 && transList.length==0){
		layer.msg('拨打端口和转接端口不能都为空')
		return
	}
	var data = {
		gatewayId: $(that).attr('sipId'),
		userId:getQueryString('userId'),
		gatewayType:1,
		url:$("#url").text(),
		auth:$("#auth").val(),
		pwd:$("#pwd").val(),
		portOnStr:JSON.stringify(callList),
		transProtStr:JSON.stringify(transList)
	}
	
	console.log(data)
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateways/updateProject",
		data:data,
		success:function(data){
			var res = JSON.parse(data)
			if(res.result == 0){
				layer.closeAll()
				layer.msg('编辑成功')
				getGatewayList()
			}else{
				layer.msg('编辑失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function addNewGateway(){
	var callList = []
	var transList = []
	$("#portBox .yellowUncheck").each(function(index,val){
		callList.push(parseInt($(val).attr('data')))
	})
	$("#portBox .blueUncheck").each(function(index,val){
		transList.push(parseInt($(val).attr('data')))
	})
	if(callList.length==0 && transList.length==0){
		layer.msg('拨打端口和转接端口不能都为空')
		return
	}
	var data = {
		gatewayId: $("#gatewayInfo .dk_num").children('option:selected').attr('gatewayId'),
		userId:getQueryString('userId'),
		gatewayType:1,
		url:$("#url").val(),
		auth:$("#auth").val(),
		pwd:$("#pwd").val(),
		portOnStr:JSON.stringify(callList),
		transProtStr:JSON.stringify(transList)
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/gateways/updateProject",
		data:data,
		success:function(data){
			var res = JSON.parse(data)
			if(res.result == 0){
				layer.closeAll()
				layer.msg('添加成功')
				getGatewayList()
			}else{
				layer.msg('添加失败')
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}

