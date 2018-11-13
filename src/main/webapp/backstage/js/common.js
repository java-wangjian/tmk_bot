//var commonUrl = 'http://47.93.255.115:8080'
//var commonUrl = ''
var commonUrl = 'http://192.168.1.222:88'
//var commonUrl = 'http://192.168.1.156:88'
//var commonUrl = 'http://47.93.37.49:8080'
function login(){
	var loginMsg = {
		'account': $('#account').val().replace(/\s+/g, ""),
		'password': $('#password').val().replace(/\s+/g, "")
	}
	if(loginMsg.account==''||loginMsg.password==''){
		layer.msg('账号或密码不能为空');
		return;
	}
	var loading = layer.load(1, {shade: [0.5,'#000'],scrollbar: false,});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/login/adminLogin",
		data:loginMsg,
		success:function(data){
			layer.close(loading);
			var res = JSON.parse(data)
            if(res.result==-1){
            	layer.msg('账号或密码错误');
            }else if(res.result==-2){
            	layer.msg('账号已到期');
            }else{
            	sessionStorage.setItem('adminAccount',loginMsg.account)
            	sessionStorage.setItem('adminId',res.result)
            	window.location.href='./index.html'
            }			
		},
		error:function(data){
			console.log(data)
		}
	});
}
function showLogout(event){
	event.stopPropagation()
	$('#logoutUl').css('display','block')
}
 $(document).click(function(){
	$('#logoutUl').css('display','none')
}) 
function getQueryString(name) { 
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
	var r = window.location.search.substr(1).match(reg); 
	if (r != null) return unescape(r[2]); return null; 
} 
function getNowTime(){
	var time = new Date()
	var nowTime = ''
	var month = time.getMonth()<9 ? '0'+(time.getMonth()+1) : time.getMonth()+1
	var dates = time.getDate()<10 ? '0'+time.getDate() : time.getDate()
	var hour = time.getHours()<10 ? '0'+time.getHours() : time.getHours()
	var min = time.getMinutes()<10 ? '0'+time.getMinutes() : time.getMinutes()
	var sec = time.getSeconds()<10 ? '0'+time.getSeconds() : time.getSeconds()
	nowTime=time.getFullYear()+'-'+month+'-'+dates+' '+hour+':'+min+':'+sec
	return nowTime;
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
function isRepeat(arr) {
    var hash = {};
    for (var i in arr) {
        if (hash[arr[i]]){
            return true; 
        }
        hash[arr[i]] = true;
    }
    return false;
}