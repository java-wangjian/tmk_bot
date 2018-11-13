var prev = 1;
var isHistory = true;
function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})
    unReadCount()
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
		userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
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
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				$('#simCardUl').html(nomess)
				$('#totalMoney').html("")
//				return;
			}else{
				$('#totalMoney').html("累计充值金额：<span style='color:#333;font-size:0.26rem'>"+res.data.total.totalMoney+"</span>元")
				$.each(res.data.list, function(index,value) {
            		cardStr+= "<li class='accMgrMainLi accMgrItem'>"
            		cardStr+= "<span class='flex2 color999'>"+(index+1)+"</span>"
            		cardStr+= "<span class='flex2 color999'>"+value.sipName+"</span>"
            		cardStr+= "<span class='flex2 color666'>"+value.unitPrice+"</span>"          	
            		cardStr+= "<span class='flex2 color666'>"+parseInt(value.LengthTime/60)+"</span>"
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
		userId:JSON.parse(sessionStorage.getItem('loginMsg')).id,
		page: page,
		per:10,	
		sipId:$('#vosSelect').val(),
		startDate:$('#start').val() == '' ? getNeardays(2) + " 00:00:00" : $('#start').val() + " 00:00:00",
		endDate:$('#end').val() == '' ? getNeardays(0) + " 23:59:59" : $('#end').val() + " 23:59:59" ,
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
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width:1.6rem' src='../images/list_no_mess.png' /></li>"
				console.log(nomess)
				$('#simCardUl_1').html(nomess)
				$('#totalMoney').html("")
//				return;
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
function getVosList(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/userlines/sipinfo",
		data:{ userId:JSON.parse(sessionStorage.getItem('loginMsg')).id },
		success:function(data){
			var res = JSON.parse(data)
			if(res.data.length == 0){
				var nomess = "<li class='vosItem'><img style='width:1.6rem;margin: 0 auto;display: block;margin-top: 0.3rem;' src='../images/sip_no_mess.png' /><button class='addSip0' onclick='addSipOrVos()'>添加线路</button></li>"
				$('#vosBox').html(nomess)
				return;
			}
			var cardStr = ""
            $.each(res.data, function(index,value) {
            	if(value.type == 2){
            		cardStr+= "<div class='vosItem'>"
            		cardStr+= "<p class='vosName'>"+value.gatewayNumbers+"</p>"
            		cardStr+= "<p class='vosIcon'><i class='iconfont icon-dianhua'></i></p>"
            		cardStr+= "<div class='vosItemFlex'>"
            		cardStr+= "<div>"
            		cardStr+= "<p class='marTop01'>剩余话费</p>"
            		cardStr+= "<p><span class='strongs'>"+value.balanceMoney+"</span><span>元</span></p>"
            		cardStr+= "</div>"
            		cardStr+= "<div>"
            		cardStr+= "<p class='marTop01'>收费单价</p>"
            		cardStr+= "<p><span class='strongs'>"+value.unitPrice+"</span><span>元/分钟</span></p>"
            		cardStr+= "</div>"
            		cardStr+= "<div>"
            		cardStr+= "<p class='marTop01'>剩余时长</p>"
            	
            		var leftTime = value.balanceMoney == 0 ? 0 : (value.balanceMoney/value.unitPrice/60).toFixed(2)
            	
            		cardStr+= "<p><span class='strongs'>"+leftTime+"</span><span>小时</span></p>"
            		cardStr+= "</div>"
            		cardStr+= "</div>"
            		cardStr+= "</div>"	
            	}
            					
            });
            $('#vosBox').html(cardStr)          
		},
		error:function(data){
			console.log(data)
		}
	});
}
function searchList(){
	if(daysBetween($('#start').val(),$('#end').val())>30){
		layer.msg('最多选择30天的数据')
		return;
	}
	$("#totalMoney").css("visibility","visible")
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
function changeList(e){
	$("#totalMoney").css("visibility","hidden")
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
function getSipList(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/finance/getSelcectSipLine",
		data:{ userId:JSON.parse(sessionStorage.getItem('loginMsg')).id },
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
