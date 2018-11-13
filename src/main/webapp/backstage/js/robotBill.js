var prev = 1;
var isHistory = true;
function loadFuncOrStyle(){    //初始化样式或函数
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
	getSimList(1)
	getSipList()
	getVosList()
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
function getSimList(page){
	var simCardObj = {
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
			$('#totalBalance').text('累计时长：'+res.data.total.totalBalance+'分钟')
            $('#totalMoney').text('累计金额：'+res.data.total.totalMoney+'元')
            var cardStr = ""
            $.each(res.data.list, function(index,value) {
            	cardStr+= "<li class='accMgrMainLi accMgrItem'>"
            	cardStr+= "<span class='flex2'>"+(index+1)+"</span>"
            	cardStr+= "<span class='flex2'>"+value.sip_name+"</span>"
            	cardStr+= "<span class='flex2'>"+value.unitPrice+"</span>"          	
            	cardStr+= "<span class='flex2'>"+(value.sumMoney/value.unitPrice)+"</span>"
            	cardStr+= "<span class='flex2'>"+value.addTime+"</span>"
            	cardStr+= "<span class='flex2'>"+value.sumMoney+"</span>"
            	cardStr+= "</li>"
            });
            $('#simCardUl').html(cardStr)
//          $('#simCardUl_1').html(cardStr)
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
			if(simCardObj.prev == 1){
				var singleDate = (res.data.list[0].datetime.split(' ')[0])
				$('#start,#end').val(singleDate)
			}
            var cardStr = ""
            $('#totalBalance').text('累计时长：'+res.data.total.totalMinutes+'分钟')
//          $('#totalMoney').text('累计金额：'+res.data.total.totalMoney+'元')
			$('#totalMoney').text('')
            $.each(res.data.list, function(index,value) {
            	cardStr+= "<li class='accMgrMainLi accMgrItem'>"
            	cardStr+= "<span class='flex2'>"+(index+1)+"</span>"
            	cardStr+= "<span class='flex2'>"+value.phone+"</span>"
            	cardStr+= "<span class='flex2'>"+value.sip_name+"</span>"    
            	cardStr+= "<span class='flex2'>"+value.datetime+"</span>"
            	cardStr+= "<span class='flex2'>"+value.unit_price+"</span>"
            	cardStr+= "<span class='flex2'>"+formatSeconds(value.durat_time)+"</span>"
            	cardStr+= "<span class='flex2'>"+value.money+"</span>"          	
            	cardStr+= "</li>"
            });
            $('#simCardUl_1').html(cardStr)
            $('#pagination').paging({
				pageNo: page,
				totalPage: Math.ceil(res.data.count/10),
				totalSize: res.data.count,
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
function getVosList(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/finance/userSipBalance",
		data:{ userId:getQueryString('userId') },
		success:function(data){
			var res = JSON.parse(data)
			var cardStr = ""
            $.each(res.data, function(index,value) {
            	cardStr+= "<div class='vosItem'>"
            	cardStr+= "<p class='vosName'>"+value.sip_name+"</p>"
            	cardStr+= "<p class='vosIcon'><i class='iconfont icon-dianhua'></i></p>"
            	cardStr+= "<p sipId='"+value.sipId+"' class='rechargeIcon' onclick='rechargeBox(this)'>续费充值</p>"
            	cardStr+= "<div class='vosItemFlex'>"
            	cardStr+= "<div>"
            	cardStr+= "<p>剩余话费</p>"
            	cardStr+= "<p><span class='strongs'>"+value.balanceMoney+"</span><span>元</span></p>"
            	cardStr+= "</div>"
            	cardStr+= "<div>"
            	cardStr+= "<p>收费单价</p>"
            	cardStr+= "<p><span class='strongs'>"+value.unitPrice+"</span><span>元/分钟</span></p>"
            	cardStr+= "</div>"
            	cardStr+= "<div>"
            	cardStr+= "<p>剩余时长</p>"
            	
            	var leftTime = value.balanceMoney == 0 ? 0 : (value.balance/60).toFixed(2)
            	
            	cardStr+= "<p><span class='strongs'>"+leftTime+"</span><span>小时</span></p>"
            	cardStr+= "</div>"
            	cardStr+= "</div>"
            	cardStr+= "</div>"					
            });
            $('#vosBox').html(cardStr) 
		},
		error:function(data){
			console.log(data)
		}
	});
}
function rechargeBox(that){
		var tpEle  = "<div class='singleMsgBox'>"
		
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>充值金额：</span>"
	    tpEle += "<input type='' name='' id='money' maxlength='11'  value='1' />"
	    tpEle += "<b>元</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='singleMsgBoxItem'>"
	    tpEle += "<span>单价：</span>"
	    tpEle += "<input type='' name='' id='price' maxlength='10' value='0.1'/>"
	    tpEle += "<b>分钟/元</b>"
	    tpEle += "</div>"
	    
	    tpEle += "<div class='btnGroup1'>"
	    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
	    tpEle += "<button sipId='"+$(that).attr('sipId')+"' onclick='recharge(this)' class='blueBg'>确认</button>"
	    tpEle += "</div></div>"
	    	    
	var singleUp = layer.open({
  		type: 1,
  		title: "充值",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '3.5rem'],
  		content: tpEle
	});
	$('#cancelSingleUp').click(function(){
		cancelSingle(singleUp)
	}) 
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
					unitPrice:price,
					sumMoney:money
				},
				success:function(data){
					var res = JSON.parse(data)
					if(res.code == 0){
						layer.msg('充值成功')
						getSimList(1)
						getSipList()
						getVosList()
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

function cancelSingle(index){
	layer.close(index);
}
function searchList(){
	if(isHistory){
		getSimList(1)
//		$('#totalBalance').css('display','inline')
//		$('#totalMoney').css('display','inline')
	}else{
		getCost(1)
//		$('#totalMoney').css('display','none')
//		$('#totalBalance').css('display','inline')
	}
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
