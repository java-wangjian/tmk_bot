var loadings ;
var options = {
	proid : '',
	tabs : '0'
}
function loadFuncOrStyle(){    //初始化样式或函数
	isLogin()
	unReadCount()
	getAllPro()
	$(".parentList").click(function(){
		$(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
		$(this).parent().next().toggleClass('show').toggleClass('hidden')
	})  
}
function getAllPro(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/findProjectNameByUserId",
		data:{
			userId:JSON.parse(sessionStorage.getItem('loginMsg')).id
		},
		success:function(data){
			var res = JSON.parse(data)
			$.each(res, function(index,val) {
				var styles = index == 0 ? 'voiceListLiCheck' : 'voiceListLiUnCheck'
				var voices = '<li proid="'+val.id+'" onclick="getThisVoice(this)" class="voiceListLi '+styles+'"><i class="iconfont icon-huatong"></i><span title="'+val.projectName+'">'+val.projectName+'</span></li>'
				$('#voiceListUl')[0].innerHTML += voices
			});
			options.proid = $('.voiceListLiCheck').attr('proid')
			getMainProcess()
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getThisVoice(that){
	$(that).removeClass('voiceListLiUnCheck').addClass('voiceListLiCheck').siblings('li').removeClass('voiceListLiCheck').addClass('voiceListLiUnCheck')
	options.proid = $(that).attr('proid')
	showVoiceMsg ()
}
function getAssistProcess(){
	var obj = {
		projectID:options.proid,
		role:2
	}
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){	
			layer.close(loadings)
			var res = JSON.parse(data).data
			var tableStr = '<tr><th>序号</th><th>录音名</th><th>录音内容</th><th>关键词</th><th>最新编辑时间</th><th>操作</th></tr>'
			$.each(res, function(index,value) {
				tableStr += '<tr><td class="grayBg wid10">'+(index+1)+'</td>'
				tableStr += '<td><span>'+value.fileID1.split('com/')[1].split('$$').pop()+'</span><div onclick="playVoice(this)" class="voiceEleBox"><img class="ajiao" src="../images/ajiao.png" /><img class="noPlayGif" src="../images/voiceImg.gif" /><audio class="voiceEle" controls="controls" src="'+value.fileID1+'" /></div></td>'
				var dts = new Date(value.datetime).toLocaleString().split(' ')
				tableStr += '<td>'+value.content1+'</td><td>'+value.keyword+'</td><td>'+dts[0]+'<br />'+dts[1]+'</td>'
				tableStr += "<td proid='"+value.projectID+"' editid='"+value.id+"' keyword='"+value.keyword+"' files='"+value.fileID1+"' content='"+value.content1+"' class='editKwBtn' onclick='editKW(this)'>关键词编辑</td></tr>"
			});
			$('#table1').html(tableStr)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function getMainProcess(){  
	var obj = {
		projectID:options.proid,
		role:1
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){			
			var res = JSON.parse(data).data
			var nodeStr = ""
			$.each(res,function(index,value){
				nodeStr += "<div class='nodeItem'>"
				nodeStr += "<p class='arrows'>↓</p>"
				nodeStr += "<div class='node inlineblock'>"
				nodeStr += "<p proid='"+value.projectID+"' editid='"+value.id+"' keyword='"+value.keyword+"' files='"+value.fileID1+"' content='"+value.content1+"' onclick='checkKW(this)' level='"+value.level+"' class='nodeName inlineblock'>"+value.named+"</p>"
				nodeStr += "<div class='nodeControl'>"				
				nodeStr += "</div>"
				nodeStr += "</div>"
				nodeStr += "</div>"						
			})
			$('#nodes').html(nodeStr)
		},
		error:function(data){
			console.log(data)
		}
	});
	
}
function changeTabs(that){
	$(that).removeClass('othersTabs').addClass('currTabs').siblings('li').removeClass('currTabs').addClass('othersTabs')
	var cardId = $(that).attr('tabs')
	options.tabs = cardId
	$('#card' + cardId).css('display','block').siblings('.voiceContent').css('display','none')
	showVoiceMsg ()
}
function showVoiceMsg (){
	switch (options.tabs) {
		case '0' :
			getMainProcess()
		break;
		case '1' :
			getAssistProcess()
		break;
		case '2' :
			getSpecialList()
		break;
		case '3' :
			getFixed()
		break;
	}
}
//function getSpecialList(){  //多轮回话
//	var obj = {
//		projectID:options.proid,
//		role: 3
//	}
//	$.ajax({
//		type:"post",
//		url:commonUrl+"/tmk-bot/project/roleData",
//		data:obj,
//		success:function(data){			
//			var res = JSON.parse(data).data
//			var tableStr = '<tr><th>关键词</th><th>录音文件</th><th>机器人文字</th><th>关键词（可编辑）</th><th>操作</th></tr>'
//			$.each(res, function(index,value) {
//				var fileCount = value.fileID3 !== ' ' ? 3 : value.fileID2 !== ' ' ? 2 : value.fileID1 !== ' ' ? 1 : 0
//				for (var i = 1;i <= fileCount; i++) {
//					var files = 'fileID' + i;
//					var contents = 'content' + i;
//					tableStr += '<tr>'
//					if(i == 1){
//						tableStr += '<td class="grayBg" rowspan="'+fileCount+'">'+value.named+'</td>'
//					}	
//					tableStr += '<td><span>'+value[files].split('com/')[1].split('$$').pop()+'</span><div class="voiceEleBox"><img class="ajiao" src="../images/ajiao.png" /><img onclick="playVoice(this)" class="noPlayGif" src="../images/voiceImg.gif" /><audio class="voiceEle" controls="controls" src="'+value[files]+'" /></div></td>'
//
//					tableStr += '<td>'+value[contents]+'</td>'
//					
//					if(i == 1){
//						tableStr += '<td rowspan="'+fileCount+'">'+value.keyword+'</td>'
//					}
//					tableStr += "<td proid='"+value.projectID+"' editid='"+value.id+"' keyword='"+value.keyword+"' files='"+value[files]+"' content='"+value[contents]+"' class='editKwBtn' onclick='editKW(this)'>编辑关键字</td>"
//					tableStr += '</tr>'
//				}								
//			});
//			$('#table2').html(tableStr)
//		},
//		error:function(data){
//			console.log(data)
//		}
//	});
//	
//}
function getFixed(){    //特殊问题
	var obj = {
		projectID:options.proid,
		role: 4
	}
	loadings = layer.load(2, {shade: [0.3,'#666'],scrollbar: false});
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){	
			layer.close(loadings)
			var res = JSON.parse(data).data
			var tableStr = '<tr><th>类别</th><th>录音文件</th><th>机器人文字</th><th>关键词</th><th>操作</th></tr>'
			$.each(res, function(index,value) {
				if(value.named == "打断关键词"){
					return true;
				}
				var fileCount = value.fileID3 !== ' ' ? 3 : value.fileID2 !== ' ' ? 2 : value.fileID1 !== ' ' ? 1 : 0
				for (var i = 1;i <= fileCount; i++) {
					var files = 'fileID' + i;
					var contents = 'content' + i;
					tableStr += '<tr>'
					if(i == 1){
						tableStr += '<td class="grayBg" rowspan="'+fileCount+'">'+value.named+'</td>'
					}	
					tableStr += '<td><span>'+value[files].split('com/')[1].split('$$').pop()+'</span><div onclick="playVoice(this)" class="voiceEleBox"><img class="ajiao" src="../images/ajiao.png" /><img class="noPlayGif" src="../images/voiceImg.gif" /><audio class="voiceEle" controls="controls" src="'+value[files]+'" /></div></td>'
					
					tableStr += '<td>'+value[contents]+'</td>'
				
					if(i == 1){
						tableStr += "<td rowspan='"+fileCount+"'>"+value.keyword+"</td>"					
					}
					if(value.named == "未识别" || value.named == "未讲话"){
						tableStr += "<td class=''>不可编辑</td>"
					}else{
						tableStr += "<td proid='"+value.projectID+"' editid='"+value.id+"' keyword='"+value.keyword+"' files='"+value[files]+"' content='"+value[contents]+"' class='editKwBtn' onclick='editKW(this)'>编辑关键词</td>"
					}
					
					tableStr += '</tr>'
				}								
			});
			$('#table3').html(tableStr)
		},
		error:function(data){
			console.log(data)
		}
	});
	
}
function editKW(that){
	var content = $(that).attr('content')
	var files = $(that).attr('files')
	var keyword = $(that).attr('keyword')
		
	var tpEle  = '<div style="margin: 10px 0;font-size: 0.18rem;"><label class="pars01">录音名称：</label><span>'+files.split('com/')[1].split('$$').pop()+'</span></div>'
	
//		tpEle += '<div class="popBoxVoice"><label class="pars01">录音：</label><div style="width:1.6rem" class="voiceEleBox"><img class="ajiao" src="../images/ajiao.png" /><img onclick="playVoice(this)" class="noPlayGif" src="../images/voiceImg.gif" /><audio class="voiceEle" controls="controls" src="'+files+'" /></div></div>'
		tpEle += "<div class='popBoxVoice'><label class='pars01'>录音：</label><div id='textaudio1'></div></div>"
		
		tpEle += "<div><label class='pars01'>录音内容：</label><div style='box-shadow:none;height:1rem;background:none' class='textareaBox'><p style='margin-top: 6px;float: left;'>"+content+"</p></div></div>"
		tpEle += "<div><label class='pars01'>关键字：</label><div class='textareaBox'><textarea maxlength='230' id='editNodeKw'>"+keyword+"</textarea></div></div>"
    	tpEle += "<span style='line-height: 0.4rem;margin-left: 0.3rem;color: #999;'>提示：关键词之间用“空格”区分，只支持文字、字母、数字</span>"
	    tpEle += "<div class='addRowBtn'>"
	    tpEle += "<button id='qxCreate' class='but1'>取消</button>"
	    tpEle += "<button proid='"+$(that).attr('proid')+"' editid='"+$(that).attr('editid')+"' class='but2' onclick='editKWS(this)'>确定</button>"
	    tpEle += "</div>"	    
	    
	var createBox = layer.open({
  		type: 1,
  		title: "编辑关键词",
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['9.47rem', '7rem'],
  		content: tpEle
	});
	
	var wxAudio = new Wxaudio({
		ele: '#textaudio1',
		title: '',
		disc: '',
		src: files,
		width: '4rem'
	});
	
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})
}
function editKWS(that){
	var obj={
		id:$(that).attr('editid'),
		projectID:$(that).attr('proid'),		
		keyword:$.trim($('#editNodeKw').val()).replace(/\s+/g, " ")
	}
	if(obj.keyword==''){
		layer.msg('关键字不能为空')
		return;
	}
	var patrn = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘’，。、]/im;
	if(patrn.test(obj.keyword)){
		layer.msg('关键字不能包含特殊符号')
		return
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/updateData",
		data:obj,
		success:function(data){			
			if(JSON.parse(data).code==0){
				layer.closeAll()
				layer.msg('编辑成功')
				showVoiceMsg()
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function playVoice(that){	

	var player = $(that).find('.voiceEle')[0]   //当前的audio元素
	var others = $('.voiceEle').not(player)  //除当前audio外
	var playGif = $(that).find('.voiceEle').prev()
	
	if($(player).attr('src')==''){
		layer.msg('未获取到语音文件')
		return;
	}
		
	player.addEventListener('ended', function () {  
    	playGif.attr('src','../images/voiceImg.gif')
	}, false);
	
	for (var i=0;i<others.length;i++) {
		others[i].pause()
//		others[i].currentTime = 0;  //设置这个360浏览器会异常
		$(others[i]).prev().attr('src','../images/voiceImg.gif')
	}
	if(player.paused) {  
		player.currentTime = 0
        player.play();   
        playGif.attr('src','../images/playing.gif')  //控制自身  
    }else{  
        player.pause(); 
        playGif.attr('src','../images/voiceImg.gif')  //控制自身
    }  	
}
function checkKW(that){
	var content = $(that).attr('content')
	var files = $(that).attr('files')
	var keyword = $(that).attr('keyword')
		
	var tpEle  = '<div style="margin: 0.23rem 0 0;font-size: 0.16rem;"><label class="pars01">录音名称：</label><span>'+files.split('com/')[1].split('$$').pop()+'</span></div>'
	
//		tpEle += '<div class="popBoxVoice"><label class="pars01">录音：</label><div style="width:1.6rem" class="voiceEleBox"><img class="ajiao" src="../images/ajiao.png" /><img onclick="playVoice(this)" class="noPlayGif" src="../images/voiceImg.gif" /><audio class="voiceEle" controls="controls" src="'+files+'" /></div></div>'
		tpEle += "<div class='popBoxVoice'><label class='pars01'>录音：</label><div id='textaudio1'></div></div>"
		
		tpEle += "<div><label class='pars01'>录音内容：</label><div class='just01'><p style='margin-top: 6px;float: left;'>"+content+"</p></div></div>"
    	    
	       
	    
	var createBox = layer.open({
  		type: 1,
  		title: $(that).text(),
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.1rem', '3.8rem'],
  		content: tpEle
	});
	
	var wxAudio = new Wxaudio({
		ele: '#textaudio1',
		title: '',
		disc: '',
		src: files,
		width: '4rem'
	});
	
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})
}