//var commonUrl = 'http://47.93.8.246:8080'    //测试服3
var commonUrl = 'http://192.168.1.222:88'
//var commonUrl = 'http://192.168.1.118:8081'
//var commonUrl = 'http://114.116.89.184:8080'
//var commonUrl = ''   
function isLogin(){	
	var loginMsg =  sessionStorage.getItem('loginMsg')
	if(!loginMsg){
		window.location.href='./login.html'
	}else{
		$('#curAccount').text(JSON.parse(sessionStorage.getItem('loginMsg')).account)
	}
}
function login(){
	var loginMsg = {
		'account': $('#account').val().replace(/\s+/g, ""),
		'password': $('#password').val().replace(/\s+/g, "")
	}
	if(loginMsg.account==''||loginMsg.password==''){
		layer.msg('账号或密码不能为空');
		return;
	}
	var loading = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/user/login",
		data:loginMsg,
		success:function(data){
			layer.close(loading);
			var res = JSON.parse(data)
            if(res.code==0){
            	sessionStorage.setItem('isHaveSip',JSON.stringify(res.isHaveSip))
            	sessionStorage.setItem('isHaveGateway',JSON.stringify(res.isHaveGateway))
            	sessionStorage.setItem('switchstatus',JSON.stringify(res.switchstatus))
            	sessionStorage.setItem('loginMsg',JSON.stringify(res.data))
            	window.location.href='./datas.html?tabs=0'
            }else{
            	layer.msg(res.result);
            }			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getNowTime(){
	var time = new Date()
	var nowTime = ''
	var month = time.getMonth()<9 ? '0'+(time.getMonth()+1) : time.getMonth()+1
	var dates = time.getDate()<10 ? '0'+time.getDate() : time.getDate()
	var hour = time.getHours()<10 ? '0'+time.getHours() : time.getHours()
	var min = time.getMinutes()<10 ? '0'+time.getMinutes() : time.getMinutes()
	var sec = time.getSeconds()<10 ? '0'+time.getSeconds() : time.getSeconds()
	nowTime = time.getFullYear()+'-'+month+'-'+dates+' '+hour+':'+min+':'+sec
	return nowTime;
}
function unReadCount(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/msg/unreadcount",
		data:{
			id:JSON.parse(sessionStorage.getItem('loginMsg')).id,
			createTime:JSON.parse(sessionStorage.getItem('loginMsg')).createTime
		},
		success:function(data){
			var res = JSON.parse(data)
			if(res.count!==0){
				$('#unReadCount').text('('+res.count+')')
			}				
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getGrade(num){
	switch (num){
		case 0:
		return '未评级'
		break;
		case 1:
		return 'A'
		break;
		case 2:
		return 'B'
		break;
		case 3:
		return 'C'
		break;
		case 4:
		return 'D'
		break;
		case 5:
		return 'E'
		break;
		case 6:
		return 'F'
		break;
		default:
		return '未评级'
	}
}
function formatSeconds(value) {     //秒转化为时分秒
    var theTime = parseInt(value);
    var theTime1 = 0;// 分
    var theTime2 = 0;// 小时
    if(theTime > 60) {
        theTime1 = parseInt(theTime/60);
        theTime = parseInt(theTime%60);
            if(theTime1 > 60) {
            theTime2 = parseInt(theTime1/60);
            theTime1 = parseInt(theTime1%60);
        }
    }
        var result = ""+parseInt(theTime)+"秒";
        if(theTime1 > 0) {
        	result = ""+parseInt(theTime1)+"分"+result;
        }
        if(theTime2 > 0) {
        	if(theTime2<24){
        		result = ""+parseInt(theTime2)+"小时"+result;
        	}else{
        		result = ""+parseInt(theTime2/24)+"天"+parseInt(theTime2)%24+"小时"+result;
        	}
        	
        }
    return result;
}
function callStatus(num){
	switch (num){
		case 0:
		return '未拨打'
		break;
		case 1:
		return '已接听'   //
		break;
		case 2:
		return '未接通'
		break;
		case 3:
		return '未接通'
		break;
		case 4:
		return '已转接'
		break;
		default:
		return '无数据'
	}
}
function planStatus(num){
	switch (num){
		case 0:
		return '未计划'
		break;
		case 1:
		return '计划中'
		break;
		case 2:
		return '任务完成'   //
		break;
		case 3:
		return '已取消'
		break;
		default:
		return '无数据'
	}
}
function curPlanStatus(num){
	switch (num){
		case 0:
		return '未执行'
		break;
		case 1:
		return '计划中'
		break;
		case 2:
		return '计划完成'
		break;
		case 3:
		return '计划取消'
		break;
		case 4:
		return '计划关闭'
		break;
	}
}
function getNeardays(days){
	var myDate = new Date()
	var lw = new Date(myDate - 1000 * 60 * 60 * 24 * days);
	var lastY = lw.getFullYear();
	var lastM = lw.getMonth()+1;
	var lastD = lw.getDate();
	var dateVal=lastY+"-"+(lastM<10 ? "0" + lastM : lastM)+"-"+(lastD<10 ? "0"+ lastD : lastD);	
	return dateVal;
}
function showLogout(event){
	event.stopPropagation()
	$('#logoutUl').css('display','block')
}
$(document).click(function(){
	$('#logoutUl,.selectBoxUl').css('display','none')
})  

function daysBetween(sDate1,sDate2){
	var time1 = Date.parse(new Date(sDate1));
	var time2 = Date.parse(new Date(sDate2));
	var nDays = Math.abs(parseInt((time2 - time1)/1000/3600/24));
	return  nDays;
};
//function getQueryString(name) { 
//	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
//	var r = window.location.search.substr(1).match(reg); 
//	if (r != null) return unescape(r[2]); return null; 
//} 
 function getQueryString(key){
    var reg = new RegExp("(^|&)"+key+"=([^&]*)(&|$)");
    var result = window.location.search.substr(1).match(reg);
    return result?decodeURIComponent(result[2]):null;
}

function logout(){
	sessionStorage.removeItem('loginMsg')
	window.location.href = './login.html'
}
function simStatus(num){
	switch (num){
		case 0:
		return '正常'
		break;
		case 1:
		return '异常'
		break;
		case -2:
		return '无sim卡'
		break;
		default:
		return '无数据'
	}
}
function simCurr(num){
	switch (num){
		case 0:
		return '使用中'
		break;
		case 1:
		return '空闲'
		break;
		case 3:
		return '可使用'
		break;
		case -2:
		return '空闲'
		break;
		default:
		return '无数据'
	}
}
function simLabel(num){
	switch (num){
		case 1:
		return '拨打'
		break;
		case 2:
		return '转接'
		break;
		default:
		return '无数据'
	}
}
function showProtocol (){
	var tpEle  = "<h1 style='text-align:center;'>盈呼电销机器人外呼平台用户协议</h1>"
		tpEle += "<div style='padding:0 20px'><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;盈呼电销机器人是北京知行信科技有限公司（以下简称“本公司”）的主营产品，本公司通过注册用户应用盈呼电销机器人系统平台（简称“电销平台”）向注册为用户（简称“用户”）合法经营的产品及服务提供外呼服务。在应用相关服务之前，用户应当仔细阅读本协议，并通过在线形式签署：本协议由本公司与用户在线签订，用户选择【同意】按钮或添加已阅读本协议的标记，即表示同意本协议要约之承诺，本协议立即生效。<h2>一、定义</h2>1. 盈呼电销机器人：本公司是为个人或组织提供盈呼电销机器人（商务）外呼功能的第三方平台，是北京知行信科技有限公司拥有经营权的产品。<br />2. 盈呼电销机器人账户（或本文中所称谓的“该账户”）：是本公司为用户提供的电销外呼平台上的唯一身份。用户可以自行设置密码，用户应用该账户登录后可以应用本系统实现客户信息的录入、定时拨打、统计数据等系统功能。<br />3. 外呼：是指用户通过应用本系统外呼功能实现业务需求，该业务应属于国家工商主管部门登记注册的经营范围（含个别经营范围已经过行政审批）内。<br /> 4. 本服务：是指用户应用外呼平台相关功能的过程。<h2>二、用户承诺</h2>1. 用户同意并确认，在实际应用本服务前，用户已充分阅读、理解并接受本协议的全部内容和盈呼电销机器人产品使用手册（附件），同意遵循本协议之所有约定。<br /> 2. 用户同意并确认，本公司有权随时对本协议内容进行单方面的变更，无需另行单独通知用户。变更内容之前的外呼在变更后未到期的，所享受权利继续有效。若用户在本协议内容公告变更后继续应用本服务的，表示用户已充分阅读、理解并接受修改后的协议内容，也将遵循修改后的协议内容应用本服务；若用户不同意修改后的协议内容，用户应停止应用本服务。<br /> 3. 用户同意并确认，在应用本服务时，用户应确保自身拥有完全民事行为能力，在中国大陆地区具有合法开展经营性业务的法人身份或其他组织或个人；若用户在国外，本协议内容不受用户所属国家或地区法律的排斥。不具备前述条件的，用户应立即停止应用本服务。<h2>三、服务条款</h2>1. 在用户申请使用盈呼电销机器人账户时，除了满足本协议第二条内容外，还应具备签署购买协议、支付对价、应用本服务所需的网络条件。<br /> 2. 用户应该根据盈呼电销机器人的要求如实提供必要的信息,提交营业执照、法人身份证明、法人身份证复印件、从事行业资质证明、产品质检材料、税务登记证等合法手续材料，并保证信息的合法性、及时性和有效性。注册成功后，应及时修改登录密码；如用户信息发生变更，应当及时联系本公司更新。<br />  3. 如果盈呼电销机器人发现用户提供的信息存在违法、不真实或无效的可能性，本公司有权删除相关信息或冻结用户的账户直至注销。<br />  4. 用户不得将本站账户借给他人应用，否则该用户应承担由此产生的全部责任，并与实际应用人承担连带责任。为保证平台安全，用户应当妥善保管用户名和密码，如果因为用户的疏忽或管理不善，造成用户名和密码外泄、被盗等后果的，用户应当承担由此所引发的一切损失。<br />5. 审核：用户在盈呼电销机器人发布的所有信息及用途均提交由本公司进行内容审核。<br />  6. 信息规范：用户在应用本服务时所发布的内容应符合网络道德，遵守中华人民共和国的相关法律法规。用户在盈呼电销机器人平台上不得发布以下信息：<br />（1）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；<br />（2）破坏国家宗教政策，宣扬邪教和封建迷信的；<br />（3）破坏民族政策，煽动民族仇恨、民族歧视的；<br />（4）损害国家利益、社会利益、第三人合法权益的；<br />（5）散布谣言，侮辱或者诽谤他人，破坏社会稳定的；<br />（6）散布淫秽、赌博、暴力、恐怖等信息的；<br />（7）超出经营范围经营或者未经行政许可批准经营的；<br />（8）其他违反法律法规的内容。<br />7. 用户如在盈呼电销机器人发布非法内容的，本公司有权对信息进行屏蔽、删除处理，同时对账户采取警告、封号等措施，同时本公司有权将相关事项移交司法机关依法处理。<br />8. 用户发布任何外呼，必须依法取得外呼业务相关资质或资格，独立承担因没有相应的资质或资格而带来的风险和责任，并承担因此给本公司带来的损失。<h2>四、特别限制与责任</h2>1. 用户在应用本服务时应遵守中华人民共和国相关法律法规（不含港澳台地区）、用户所在国家或地区之法令及相关国际惯例，不得将本服务用于任何非法目的（包括用于禁止或限制交易物品的交易），也不以任何非法方式应用本服务。<br /> 2. 用户必须保证外呼涉及的业务真实、合法且不侵犯他人合法权益，否则应承担所有相关法律责任。<br />3. 用户不得利用本服务从事侵害他人合法权益的行为，否则应承担所有相关法律责任，因此导致本公司或本公司雇员受损的，用户应承担赔偿责任。<br /> 4. 上述行为，除其他条款另有规定的以外，还包括但不限于：<br />（1） 侵害他人的人身权益、财产权益、商业秘密、知识产权等合法权益；<br />（2） 违反依法定或约定之保密义务；<br />（3） 冒用他人名义应用本服务；<br />（4） 利用本服务从事非法交易行为，如洗钱、传销、发布虚假信息、贩毒或军火、组织淫秽等非法外呼；<br />（5） 提供赌博资讯或以任何方式引诱他人参与赌博；<br />（6） 进行与用户宣称业务内容不符的外呼，或不真实的外呼；<br />（7） 从事任何可能含有电脑病毒或是可能侵害本服务系统、信息之行为；<br />（8） 在合法经营前，用户所使用客户电话信息应当采用合法途径获得；在合法经营过程中，用户不得采用电销平台就同一目的对同一客户联系三次以上（含三次）。<br />（9） 其他违反法律法规的行为。<h2>五、免责条款</h2> 1. 本公司不对因下述情况导致的任何损害赔偿承担责任，包括但不限于利润、商誉、应用、数据等方面的损失或其他无形损失的损害赔偿。<br />（1） 本公司有权基于单方判断，包含但不限于本公司认为用户已经违反本协议约定及精神，暂停本服务。<br />（2） 本公司在发现异常外呼或有疑义或有违反法律规定或本协议约定之虞时，有权不经通知先行暂停或终止本服务。<br />（3） 在必要时，本公司无需事先通知即可终止提供本服务，并暂停或关闭该账户。<br />（4）本公司基于本协议进行的审查为形式审查，以用户按照平台要求提供相应材料为审查对象，不对该材料的真实性、合法性负责，该责任由用户自行承担。<br />  2. 系统因下列状况无法正常运作，使用户无法应用服务时，本公司不承担任何损害赔偿责任，该状况包括但不限于：<br />（1） 本系统处于公告之系统停机维护期间的；<br />（2） 电信设备出现故障不能进行数据传输的；<br />（3） 因台风、地震、海啸、洪水、停电、战争、恐怖袭击等不可抗力之因素；<br />（4） 由于黑客攻击、电信部门技术调整或故障、网站升级、银行或第三方支付工具方面的问题等原因；<br />（5） 其他不可归责于本公司的原因，造成的服务中断或者延迟。<br />3. 因用户的过错导致的任何损失，由用户承担，本公司不承担任何损害赔偿责任，该过错包括但不限于：遗忘或泄漏密码，密码被他人破解，用户应用的手机或其他终端被他人侵入等。<h2>八、其他</h2>  1. 本协议之效力、解释、变更、执行与争议解决均适用中华人民共和国法律（港澳台地区除外），没有相关法律规定的，参照通用国际商业惯例和（或）行业惯例。本协议部分内容被有管辖权的法院认定为无效的，不因此影响其他内容的效力。<br />2. 因本协议产生之争议，均应依照中华人民共和国法律（港澳台地区除外）予以处理，并由北京市朝阳区人民法院管辖。<br />3. 本协议自发布之日起生效。<br />附件：盈呼电销机器人产品使用手册<p style='text-align:right;'> 北京知行信科技有限公司<br /> 2018年7月31日</p><p></div>"
	layer.open({
  		type: 1,
  		title: "用户协议",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '8rem'],
  		content: tpEle
	});
}
function getMyDate(str) {
    var oDate = new Date(str),
    oYear = oDate.getFullYear(),
    oMonth = oDate.getMonth()+1,
    oDay = oDate.getDate(),
    oHour = oDate.getHours(),
    oMin = oDate.getMinutes(),
    oSen = oDate.getSeconds(),
    oTime = oYear +'-'+ addZero(oMonth) +'-'+ addZero(oDay) +' '+ addZero(oHour) +':'+
addZero(oMin) +':'+addZero(oSen);
    return oTime;
}

function addZero(num){
    if(parseInt(num) < 10){
        num = '0'+num;
    }
    return num;
}

function showList(){
	$('.childList').each(function(index,value){	
		if(sessionStorage.getItem('switchstatus') !== '1'){
			if($(value).find('span').text() == '话术管理'){
				$(value).parent().css('display','none')
			}
		}	
		if(sessionStorage.getItem('isHaveSip') == '0'){
			if($(value).find('span').text() == '账单管理'){
				$(value).parent().css('display','none')
			}
		}	
		if(sessionStorage.getItem('isHaveGateway') == '0'){
			if($(value).find('span').text() == '意向短信' || $(value).find('span').text() == '转接人工'){
				$(value).parent().css('display','none')
			}
		}	
	})
	$('.nav').css('visibility', 'visible')
}
window.onload = function (){
//	if(sessionStorage.getItem('switchstatus') == '0'){
		showList()
//	}	
	$("body").click(function(){
		$("#allGateWay").css('display','none')
		$("#businessUl").css('display','none')
		$("#seleKfUl").css('display','none')
		
	})
	$(".lingdang").mouseenter(function(){
		$(this).attr("src","../images/ulingdang.png")
	}).mouseleave(function(){
		$(this).attr("src","../images/lingdang.png")
	})
}
