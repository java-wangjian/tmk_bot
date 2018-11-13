function init(){
	$('#curAccount').text(sessionStorage.getItem('adminAccount'))	
	getSpecialList()
	getMainProcess()
	getAssistProcess()
	getFixed()
	setTimeout(function(){
		initSpeBtn()
	},2000)
}
function getAssistProcess(){
	var obj = {
		projectID:getQueryString('projectID'),
		role:2
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){		
			var res = JSON.parse(data).data
			var nodeStr = ""
			$.each(res,function(index,value){
				newAssistItem(value)					
			})
			showAssistNum()
			configUpload('newAssist',false,1)
		},
		error:function(data){
			console.log(data)
		}
	});
}
function showAssistNum(){
	$('.assistNum').each(function(index,value){
		$(value).text('F'+(index+1)+'-')
	})
}
function getMainProcess(){  
	var obj = {
		projectID:getQueryString('projectID'),
		role:1
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){			
			var res = JSON.parse(data).data
			if(!res||res.length==0){
				$('#firstBtn').css('display','block')
			}else{
				$('#firstBtn').css('display','none')
			}
			var nodeStr = ""
			$.each(res,function(index,value){
				nodeStr += "<div class='nodeItem'>"
				nodeStr += "<p class='arrows'>↓</p>"
				nodeStr += "<div class='node inlineblock'>"
				nodeStr += "<p level='"+value.level+"' class='nodeName inlineblock'>"+value.named+"</p>"
				nodeStr += "<div class='nodeControl'>"				
				if(res.length==index+1){
					nodeStr += "<button onclick='addNode(this)' hsData='[]' class='nodeBtn'><i class='iconfont icon-tianjia blue'></i> <span>添加节点</span></button>"
				}
				nodeStr += "<button srcs='"+value.fileID1+"' keyword='"+value.keyword+"' nodeid='"+value.id+"' content='"+value.content1+"' named='"+value.named+"' onclick='editNode(this)' class='nodeBtn'><i class='iconfont icon-bianji blue'></i> <span>编辑节点</span></button>"
				nodeStr += "<button onclick='deleteNode("+value.id+","+value.role+","+value.level+")' class='nodeBtn'><i class='iconfont icon-shanchu red'></i> <span>删除节点</span></button>"
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
function getSpecialList(){
	var obj = {
		projectID:getQueryString('projectID'),
		role:3
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){			
			var res = JSON.parse(data)
			$.each(res.data,function(index,val){
				val.upBtnCount = index
				newSpecialItem(val)
			})
			showSpecial()
		},
		error:function(data){
			console.log(data)
		}
	});
}
function trigTab(e){
	var index = $(e.target)
	if(index.index()==1){
		$("#batchBtn").css('display','block')
	}else{
		$("#batchBtn").css('display','none')
	}
	index.parent().children().css({"border-bottom":"0.04rem solid #FAFAFA","color":"#333333"})
	index.css({"border-bottom":"0.04rem solid #6186CF","color":"#6186CF"})
	$('.main').css('display','none')
	$($('.main')[index.index()]).css('display','block')
}
function deleteNode(nodeid,role,level){
	layer.msg('确定要删除该节点吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
      		$.ajax({
				type:"post",
				url:commonUrl+"/tmk-bot/project/deleteData",
				data:{
					id:nodeid,
					projectID:getQueryString('projectID'),
					role:role,
					level:level
				},
				success:function(data){
					if(JSON.parse(data).code==0){
						layer.msg('删除成功')
						getMainProcess()
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
function editNode(that){
	var tpEle  = "<div class='addMsgWrap'><div>"
	
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>名称：</label><input maxlength='15' class='addNodeInput' type='' name='' id='editNodeName' value='"+$(that).attr('named')+"' />"
	    tpEle += "</div>" 
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>内容：</label><input maxlength='230' class='addNodeInput' type='' name='' id='editNodeContent' value='"+$(that).attr('content')+"' />"
	    tpEle += "</div>" 
	    tpEle += "<div class='addRow'>"
	    tpEle += "<textarea maxlength='230' placeholder='输入关键词，并用空格隔开' class='addNodeKW' id='editNodeKw' name='' rows='' cols=''>"+$(that).attr('keyword')+"</textarea>"
	    tpEle += "</div>" 
	    tpEle += "<div class='addRow'>"
	    
	    tpEle += "<div style='display:none' id='container'><p hsData='[]' id='addSingle' class='newAudioBtn'>+添加语音</p></div>"
	    
	    tpEle+= "<div id='voicesec' class='voiceBox'>"
	    tpEle+= "<div onclick='playVoice(this)' class='voiceImgBox'>"
	    tpEle += "<img class='playing' src='../images/voiceImg.gif' />"
	    tpEle+= "<audio id='audioEle' class='voiceEle' src='"+$(that).attr('srcs')+"'></audio>"
	    tpEle+= "</div>"
	    tpEle+= "<span id='fileName' class='fileName'>"+$(that).attr('srcs').split('com/')[1].split('$$').pop()+"</span><i onclick='hiddenVoice()' class='iconfont icon-shanchu delVoice'></i>"
	    tpEle+= "</div>"
	    tpEle += "</div>"
	    tpEle += "</div>"	    
	    tpEle += "<div class='addRowBtn'>"
	    tpEle += "<button id='qxCreate' class='but1'>取消</button>"
	    tpEle += "<button class='but2' onclick='confirmEditPro("+$(that).attr('nodeid')+")'>确定</button>"
	    tpEle += "</div></div>"	    
	var createBox = layer.open({
  		type: 1,
  		title: "编辑节点",
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5.47rem'],
  		content: tpEle
	});
	
	configUpload('addSingle',false,0)  //初始化上传按钮 
	
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})
}
function confirmEditPro(nodeid){
	var obj={
		id:nodeid,
		projectID:getQueryString('projectID'),
		named:$('#editNodeName').val().replace(/\s+/g, ""),
		keyword:$.trim($('#editNodeKw').val()).replace(/\s+/g, " "),
		content1:$('#editNodeContent').val().replace(/\s+/g, ""),
		fileID1:$('#audioEle').attr('src')
	}
	if(obj.named==''||obj.keyword==''||obj.content1==''||obj.fileID1==''){
		layer.msg('信息或语音文件不能为空')
		return;
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/updateData",
		data:obj,
		success:function(data){			
			if(JSON.parse(data).code==0){
				layer.closeAll()
				layer.msg('编辑成功')
				getMainProcess()
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function showVoice(){
	var urls = JSON.parse($('#addSingle').attr('hsData'))[0]
	
	$('#audioEle').attr('src',urls)
	$('#fileName').text(urls.split('com/')[1].split('$$').pop())
	$('#container').css('display','none')
	$('#voicesec').css('display','block')
}
function hiddenVoice(){
	$('.playing').attr('src','../images/voiceImg.gif')
	$('#addSingle').attr('hsData','[]')
	$('#container').css('display','block')
	$('#voicesec').css('display','none')
	$('#audioEle')[0].pause()
	$('#audioEle').attr('src','')
}
function playVoice(that){	
	var player = $(that).children()[1]  //当前的audio元素
	
	
	var icon = $(that).children()[0]  
	
	player.addEventListener('ended', function () {  
    	$(icon).attr('src','../images/voiceImg.gif')  //控制自身
	}, false);
	var others = $('.voiceEle').not(player)  //除当前audio外	
	for (var i=0;i<others.length;i++) {
		others[i].pause()
		$(others[i]).prev().attr('src','../images/voiceImg.gif')
	}
	if(player.paused) {  
		player.currentTime = 0
        player.play();   
        $(icon).attr('src','../images/playing.gif')  //控制自身
    }else{  
        player.pause(); 
        $(icon).attr('src','../images/voiceImg.gif')  //控制自身
    }  	
}
function delThisVoice(that){
	layer.msg('确定要删除该语音吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
    		 var tars =  $(that).parent().parent().parent()
    		 if(tars.siblings().length<=3){
    		 	tars.parent().children('.addSign').css('display','block')
    		 }
			 tars.remove()   
			
    	}
  });	
}  
function moreAddVoice(eleId,obj){   //点击的哪个添加按钮的id  obj.urls为上传的语音路径 
		var miVoiceStr = "<div class='miVoiceBox'>"
	    miVoiceStr += "<div class='miAudioBox'>"
	    miVoiceStr += "<div onclick='playVoice(this)' class='sec08'>"
	    miVoiceStr += "<img class='playing' src='../images/voiceImg.gif' />"
	    miVoiceStr += "<audio class='voiceEle' src='"+obj.urls+"'></audio>"
	    miVoiceStr += "</div>"
	    miVoiceStr += "<span class='speFileName'>"+obj.urls.split('com/')[1].split('$$').pop()+"</span><span><i onclick='delThisVoice(this)' class='iconfont icon-shanchu delVoice'></i></span>"
	    miVoiceStr += "</div>"
	    miVoiceStr += "<div class='miKwBox'>"
	    miVoiceStr += "<textarea placeholder='语音内容' maxlength='200' class='sec10' name='' rows='' cols=''></textarea>"
	    miVoiceStr += "</div>"
	    miVoiceStr += "</div>"
	    
	    var tars = $("#"+eleId).parent().parent()
		tars.before(miVoiceStr)
//		console.log(tars.siblings().length)
		if(tars.siblings().length>=3){
    		tars.parent().children('.addSign').css('display','none')
    	}
}
function newAssistItem(obj){		
	var assistEditBtnIndex = 'assistEditBtn' + $('#assistContainer').find('.icon-bianji').length
	var newAssistItemStr = "<div class='assistItemBox'>"
	   if(obj.id==''){
	   	newAssistItemStr += "<div class='caps hiddens'></div>"
	   }else{
	   	newAssistItemStr += "<div class='caps shows'></div>"
	   }
	   newAssistItemStr += "<div class='sec00'>"
	   newAssistItemStr += "<div class='sec01'>"
	   newAssistItemStr += "<p class='sec02'><span class='assistNum'>F1-</span>名称：</p>"
	   newAssistItemStr += "<div class='sec03'><input value='"+obj.named+"' class='sec04 editNamed' type='text' /></div>"
	   newAssistItemStr += "</div>"
	   newAssistItemStr += "<div class='sec01'>"
	   newAssistItemStr += "<p class='sec02'>内容：</p>"
	   newAssistItemStr += "<div class='sec03'><input value='"+obj.content1+"' class='sec04 editContent' type='text' /></div>"
	   newAssistItemStr += "</div>"
	   newAssistItemStr += "<div class='sec05'>"
	   newAssistItemStr += "<textarea maxlength='230' placeholder='输入关键词，并以空格隔开' class='sec06 editKw' name='' rows='' cols=''>"+obj.keyword+"</textarea>"
	   newAssistItemStr += "</div>"
	   newAssistItemStr += "<div class='sec07'>"
	   newAssistItemStr += "<div onclick='playVoice(this)' class='sec08'>"
	   newAssistItemStr += "<img class='playing' src='../images/voiceImg.gif' />"
	   newAssistItemStr += "<audio class='voiceEle' src='"+obj.fileID1+"'></audio>"
	   newAssistItemStr += "</div>"
	   newAssistItemStr += "<span class='speFileName00'>"+obj.fileID1.split('com/')[1].split('$$').pop()+"</span><i id='"+assistEditBtnIndex+"' class='iconfont icon-bianji delVoice'></i><i onclick='delAssist(this)' class='iconfont icon-shanchu delVoice'></i>"
	   newAssistItemStr += "</div>"
	   if(obj.id==''){  //将item的id保存到按钮的父元素的父元素上
	   	newAssistItemStr += "<div itemid='"+obj.id+"' type='new' class='saveBox'><div class='shows' ><button onclick='saveItemAssist(this)' class='itemBtn blueBg'>保存</button><button onclick='cancleItemAssist(this)' class='itemBtn grayBg'>取消</button></div><div class='hiddens'><button onclick='hiddenCaps(this)' class='itemBtn blueBg'>编辑</button></div></div>"
	   }else{
	   	newAssistItemStr += "<div itemid='"+obj.id+"' type='edit' class='saveBox'><div class='hiddens' ><button onclick='saveItemAssist(this)' class='itemBtn blueBg'>保存</button><button onclick='cancleItemAssist(this)' class='itemBtn grayBg'>取消</button></div><div class='shows'><button onclick='hiddenCaps(this)' class='itemBtn blueBg'>编辑</button></div></div>"
	   }	   
	   newAssistItemStr += "</div>"
	   newAssistItemStr += "</div>"
	   
	   $('#assistContainer').append(newAssistItemStr)
	   
	   configUpload(assistEditBtnIndex,false,5)
}
function editAssistVoices(eleId,sourceLink){
	console.log($('#'+eleId).prev().prev().children('audio'))
	$('#'+eleId).prev().prev().children('audio').attr('src',sourceLink)
	$('#'+eleId).prev().text(sourceLink.split('com/')[1].split('$$').pop())
}
function delAssist(that){
	var parents = $(that).parent().next()
	layer.msg('确定要删除该辅助问题吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
    		var obj = {
    			id:parents.attr('itemid'),
				projectID:getQueryString('projectID'),
				role:2,
				level:0
    		}
    		if(obj.id==''){
    			parents.parent().parent().remove()
    		}else{
      			$.ajax({
					type:"post",
					url:commonUrl+"/tmk-bot/project/deleteData",
					data:obj,
					success:function(data){
						if(JSON.parse(data).code==0){
							layer.msg('删除成功')
							parents.parent().parent().remove()
						}else{
							layer.msg('删除失败')
						}
			
					},
					error:function(data){
						console.log(data)
					}
				});
				showAssistNum()  
    		}

    	}
   	});
}
function cancleItemAssist(that){
	showCaps(that)
	var parents = $(that).parent().parent()
	if(parents.attr('type')=='new'){
		$(that).parent().parent().parent().parent().remove()
	}else if(parents.attr('type')=='edit'){
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/project/shotData",
			data:{id:parents.attr('itemid')},
			success:function(data){	
				var res = JSON.parse(data).data
//				console.log(res.named)
				parents.parent().find('.editNamed').val(res.named)
				parents.parent().find('.editKw').val(res.keyword)
				parents.parent().find('.editContent').val(res.content1)
			},
			error:function(data){
				console.log(data)
			}
		});
	}
}
function saveItemAssist(that){
	var parents = $(that).parent().parent()
	if(parents.attr('type')=='new'){
		var obj = {
			'named':parents.parent().find('.editNamed').val().replace(/\s+/g, ""),
			'role':2,
			'keyword':$.trim(parents.parent().find('.editKw').val()).replace(/\s+/g, " "),
//			'datetime':getNowTime(),
			'content1':parents.parent().find('.editContent').val().replace(/\s+/g, ""),
			'projectID':getQueryString('projectID'),
			'fileID1':parents.parent().find('.voiceEle').attr('src'),
			'level':0
		}
		if(obj.named==''||obj.keyword==''||obj.content1==''||obj.fileID1==''){
			layer.msg('信息或语音文件不能为空')
			return;
		}
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/project/insertData",
			data:obj,
			success:function(data){	
//				console.log(data)
				if(JSON.parse(data).code==0){
					parents.attr('itemid',JSON.parse(data).id)
					parents.attr('type','edit')
					showCaps(that)
					layer.closeAll()
					layer.msg('保存成功')
				}
			},
			error:function(data){
				console.log(data)
			}
		});
		
	}else if(parents.attr('type')=='edit'){
		var obj = {
			'id':parents.attr('itemid'),
			'projectID':getQueryString('projectID'),
			'named':parents.parent().find('.editNamed').val().replace(/\s+/g, ""),
			'keyword':$.trim(parents.parent().find('.editKw').val()).replace(/\s+/g, " "),
			'content1':parents.parent().find('.editContent').val().replace(/\s+/g, ""),			
			'fileID1':parents.parent().find('.voiceEle').attr('src'),
		}
		if(obj.named==''||obj.keyword==''||obj.content1==''||obj.fileID1==''){
			layer.msg('信息或语音文件不能为空')
			return;
		}
//		console.log(obj)
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/project/updateData",
			data:obj,
			success:function(data){			
				if(JSON.parse(data).code==0){
					showCaps(that)
					layer.closeAll()
					layer.msg('更新成功')
				}
			},
			error:function(data){
				console.log(data)
			}
		});
	}
}
function hiddenCaps(that){
	$(that).parent().parent().parent().parent().children('.caps').css('display','none')
	$(that).parent().prev().css('display','block')
	$(that).parent().css('display','none')
}
function hiddenCaps0(that){
	$(that).parent().parent().parent().parent().children('.caps0').css('display','none')
	$(that).parent().prev().css('display','block')
	$(that).parent().css('display','none')
}
function showCaps(that){
	$(that).parent().parent().parent().parent().children('.caps').css('display','block')
	$(that).parent().next().css('display','block')
	$(that).parent().css('display','none')
}
function showCaps0(that){
	$(that).parent().parent().parent().parent().children('.caps0').css('display','block')
	$(that).parent().next().css('display','block')
	$(that).parent().css('display','none')
}
function moreSpecialBox(){
	if($('.specialQuesItem').length>20){
		layer.msg('特殊问题最多添加20个')
		return;
	}
	var obj = {
		upBtnCount: $('.sec09').length,
		id:'',
		backup:"[]",
		named:'',
		keyword:''
	}
	newSpecialItem(obj)
	showSpecial()
	keepBottom('specialContainer')
}
function keepBottom(ele){
	var div = document.getElementById(ele);
	div.scrollTop=div.scrollHeight
}
function showSpecial(){
	$('.specialNum').each(function(index,value){
		$(value).text('D'+(index+1)+'-')
	})
}
function newSpecialItem(obj){	 //fileName  trigTab
	var miVoiceStr = ""
	$.each(JSON.parse(obj.backup), function(index,value) {
		miVoiceStr += "<div class='miVoiceBox'>"
	    miVoiceStr += "<div class='miAudioBox'>"
	    miVoiceStr += "<div onclick='playVoice(this)' class='sec08'>"
	    miVoiceStr += "<img class='playing' src='../images/voiceImg.gif' />"
	    miVoiceStr += "<audio class='voiceEle' src='"+value.fileID+"'></audio>"
	    miVoiceStr += "</div>"
	    miVoiceStr += "<span class='speFileName'>"+value.fileID.split('com/')[1].split('$$').pop()+"</span><span><i onclick='delSpeVoice(this)' class='iconfont icon-shanchu delVoice'></i></span>"
	    miVoiceStr += "</div>"
	    miVoiceStr += "<div class='miKwBox'>"
	    miVoiceStr += "<textarea placeholder='语音内容' maxlength='200' class='sec10' name='' rows='' cols=''>"+value.content+"</textarea>"
	    miVoiceStr += "</div>"
	    miVoiceStr += "</div>"
	});
	
	var upbtnid = "upBtns"+obj.upBtnCount   //当前上传按钮的id
	
	var newSpecialItemStr = "<div class='specialQuesItem'>"
	
	if(obj.id==''){
	   	newSpecialItemStr += "<div class='caps0 hiddens'></div>"
	   }else{
	   	newSpecialItemStr += "<div class='caps0 shows'></div>"
	   }
	   newSpecialItemStr += "<div class='sec00'>"
	   newSpecialItemStr += "<div class='sec01'>"
	   newSpecialItemStr += "<p class='sec02'><span class='specialNum'></span>名称：</p>"
	   newSpecialItemStr += "<div class='sec03'><input value='"+obj.named+"' class='sec04 quesName' type='text' /></div>"
	   newSpecialItemStr += "</div>"
	   newSpecialItemStr += "<div class='sec05'>"
	   newSpecialItemStr += "<textarea maxlength='230' value='"+obj.keyword+"' placeholder='输入关键词，并以空格隔开' class='sec06 quesKw' name='' rows='' cols=''>"+obj.keyword+"</textarea>"
	   newSpecialItemStr += "</div>"
	   newSpecialItemStr += "<div class='threeVoicesBox'>"
	   
	   newSpecialItemStr += miVoiceStr
	   	   
	   if(JSON.parse(obj.backup).length<3){
	   		newSpecialItemStr += "<div class='miVoiceBox addSign shows'>"
	   }else{
	   		newSpecialItemStr += "<div class='miVoiceBox addSign hiddens'>"
	   }
	   
	   newSpecialItemStr += "<div class='miAudioBox'>"
	   newSpecialItemStr += "</div>"
	   newSpecialItemStr += "<div class='miKwBox'>"
	   newSpecialItemStr += "<button id='"+upbtnid+"' class='sec09'>+</button>"
	   newSpecialItemStr += "</div>"
	   newSpecialItemStr += "</div>"
	   
	   newSpecialItemStr += "</div>"
	  
	   if(obj.id==''){  //将item的id保存到按钮的父元素的父元素上
	   	newSpecialItemStr += "<div itemid='"+obj.id+"' type='new' class='saveBox'><div class='shows' ><button onclick='saveItemSpe(this)' class='itemBtn blueBg'>保存</button><button onclick='cancleItemSpe(this)' class='itemBtn grayBg'>取消</button></div><div class='hiddens'><button onclick='hiddenCaps0(this)' class='itemBtn blueBg'>编辑</button></div></div>"
	   }else{
	   	newSpecialItemStr += "<div itemid='"+obj.id+"' type='edit' class='saveBox'><div class='hiddens' ><button onclick='saveItemSpe(this)' class='itemBtn blueBg'>保存</button><button onclick='cancleItemSpe(this)' class='itemBtn grayBg'>取消</button></div><div class='shows'><button onclick='hiddenCaps0(this)' class='itemBtn blueBg'>编辑</button></div></div>"
	   }	
	  
	   newSpecialItemStr += "</div>"
	   newSpecialItemStr += "</div>"
	   $('#specialContainer').append(newSpecialItemStr)
	   configUpload(upbtnid,false,2)
}
function delSpeVoice(that){
	var box = $(that).parent().parent().parent().parent()
	var isRealDel = $(that).parent().parent().parent().index()
	if(isRealDel==0){
		layer.msg('确定要删除该特殊问题吗？', {
    		time: 20000, 
    		btn: ['确定', '取消'], 
    		yes: function (index) { 
    			layer.close(index)
    			var obj = {
    				id:box.next().attr('itemid'),
					projectID:getQueryString('projectID'),
					role:3,
					level:0
    			}
      			$.ajax({
					type:"post",
					url:commonUrl+"/tmk-bot/project/deleteData",
					data:obj,
					success:function(data){
						if(JSON.parse(data).code==0){
							showCaps0()
							layer.msg('删除成功')
							box.parent().parent().remove()
						}else{
							layer.msg('删除失败')
						}
						showSpecial()
					},
					error:function(data){
						console.log(data)
					}
				});
    		}
   		});
	}else{
		layer.msg('确定要删除该特殊问题吗？', {
    		time: 20000, 
    		btn: ['确定', '取消'], 
    		yes: function (index) { 
    			layer.close(index)
    			
    			var parents = box.next()
				var obj = {
					'id':parents.attr('itemid'),
					'projectID':getQueryString('projectID')
//					'named':parents.parent().find('.quesName').val().replace(/\s+/g, ""),
//					'keyword':$.trim(parents.parent().find('.quesKw').val()).replace(/\s+/g, " ")
				}
		
				var childs = box.children('.miVoiceBox')
		
				if(isRealDel==1){  //从第二个开始删除
//					obj.content1 = $(childs[0]).find('.sec10').val()
//					obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')			
					obj.content2 = ''
					obj.fileID2 = ''
					$(childs[1]).remove()
					obj.content3 = ''
					obj.fileID3 = ''
					$(childs[2]).remove()
				}else if(isRealDel==2){ //从第三个开始删除
//					obj.content1 = $(childs[0]).find('.sec10').val()
//					obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
//					obj.content2 = $(childs[1]).find('.sec10').val()
//					obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
			
					obj.content3 = ''
					obj.fileID3 = ''
					$(childs[2]).remove()
				}			
    			
    			$.ajax({
					type:"post",
					url:commonUrl+"/tmk-bot/project/updateData",
					data:obj,
					success:function(data){			
						if(JSON.parse(data).code==0){
							showCaps0(that)
							layer.closeAll()
							layer.msg('删除成功')
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
}
function cancleItemSpe(that){
	showCaps0(that)
	var parents = $(that).parent().parent()
	if(parents.attr('type')=='new'){
		parents.parent().parent().remove()
	}else if(parents.attr('type')=='edit'){
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/project/shotData",
			data:{id:parents.attr('itemid')},
			success:function(data){	
				var res = JSON.parse(data).data
//				console.log(data)
				parents.parent().find('.quesName').val(res.named)
				parents.parent().find('.quesKw').val(res.keyword)
				
				var addSign = parents.parent().find('.addSign ')
				var threeBox = parents.parent().find('.threeVoicesBox')
				threeBox.find('.miVoiceBox').not('.addSign').remove()
				
//				var mVoi = parents.prev().children('.miVoiceBox')
//				
				
				$.each(JSON.parse(res.backup), function(index,value) {
					
					var miVoiceStr = "<div class='miVoiceBox'>"
	    				miVoiceStr += "<div class='miAudioBox'>"
	    				miVoiceStr += "<div onclick='playVoice(this)' class='sec08'>"
	   		 			miVoiceStr += "<img class='playing' src='../images/voiceImg.gif' />"
	    				miVoiceStr += "<audio class='voiceEle' src='"+value.fileID+"'></audio>"
	    				miVoiceStr += "</div>"
	    				miVoiceStr += "<span class='speFileName'>"+value.fileID.split('com/')[1].split('$$').pop()+"</span><span><i onclick='delSpeVoice(this)' class='iconfont icon-shanchu delVoice'></i></span>"
	    				miVoiceStr += "</div>"
	    				miVoiceStr += "<div class='miKwBox'>"
	    				miVoiceStr += "<textarea placeholder='语音内容' maxlength='200' class='sec10' name='' rows='' cols=''>"+value.content+"</textarea>"
	    				miVoiceStr += "</div>"
	    				miVoiceStr += "</div>"
	    				
	    				addSign.before(miVoiceStr)
					
//					$(mVoi[index]).find('.sec10').val(value.content)
				});
//				parents.parent().find('.quesKw')
			},
			error:function(data){
				console.log(data)
			}
		});
	}
}
function saveItemSpe(that){
	var parents = $(that).parent().parent()
	if(parents.attr('type')=='new'){
		var obj = {
			'named':parents.parent().find('.quesName').val().replace(/\s+/g, ""),
			'role':3,
			'keyword':$.trim(parents.parent().find('.quesKw').val()).replace(/\s+/g, " "),
//			'datetime':getNowTime(),
			'projectID':getQueryString('projectID'),
			'level':0
		}
		var childs = parents.prev().children('.miVoiceBox')
		var voNum = childs.length-1  //新增的item的语音个数
		
		if(voNum==0){
			layer.msg('未上传语音')
			return;
		}else if(voNum==1){
			obj.content1 = $(childs[0]).find('.sec10').val()
			obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
		}else if(voNum==2){
			obj.content1 = $(childs[0]).find('.sec10').val()
			obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
			obj.content2 = $(childs[1]).find('.sec10').val()
			obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
		}else if(voNum==3){
			obj.content1 = $(childs[0]).find('.sec10').val()
			obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
			obj.content2 = $(childs[1]).find('.sec10').val()
			obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
			
			obj.content3 = $(childs[2]).find('.sec10').val()
			obj.fileID3 = $(childs[2]).find('.voiceEle').attr('src')
			
		}		
		if(obj.named==''||obj.keyword==''||obj.content1==''||obj.content2==''||obj.content3==''){
			layer.msg('信息不能为空')
			return;
		}
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/project/insertData",
			data:obj,
			success:function(data){	
//				console.log(data)
				if(JSON.parse(data).code==0){
					parents.attr('itemid',JSON.parse(data).id)
					parents.attr('type','edit')
					showCaps0(that)
					layer.closeAll()
					layer.msg('添加成功')
				}
			},
			error:function(data){
				console.log(data)
			}
		});
		
	}else if(parents.attr('type')=='edit'){
		var obj = {
			'id':parents.attr('itemid'),
			'projectID':getQueryString('projectID'),	
			'named':parents.parent().find('.quesName').val().replace(/\s+/g, ""),
			'keyword':$.trim(parents.parent().find('.quesKw').val()).replace(/\s+/g, " ")
		}
		
		var childs = parents.prev().children('.miVoiceBox')
		var voNum = childs.length-1  //新增的item的语音个数
		
		if(voNum==0){
			layer.msg('未上传语音')  
			return;
		}else if(voNum==1){
			obj.content1 = $(childs[0]).find('.sec10').val()
			obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
		}else if(voNum==2){
			obj.content1 = $(childs[0]).find('.sec10').val()
			obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
			obj.content2 = $(childs[1]).find('.sec10').val()
			obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
		}else if(voNum==3){
			obj.content1 = $(childs[0]).find('.sec10').val()
			obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
			obj.content2 = $(childs[1]).find('.sec10').val()
			obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
			
			obj.content3 = $(childs[2]).find('.sec10').val()
			obj.fileID3 = $(childs[2]).find('.voiceEle').attr('src')
			
		}		
		if(obj.named==''||obj.keyword==''||obj.content1==''||obj.content2==''||obj.content3==''){
			layer.msg('信息不能为空')
			return;
		}
		$.ajax({
			type:"post",
			url:commonUrl+"/tmk-bot/project/updateData",
			data:obj,
			success:function(data){			
				if(JSON.parse(data).code==0){
					showCaps0(that)
					layer.closeAll()
					layer.msg('更新成功')
				}
			},
			error:function(data){
				console.log(data)
			}
		});
	}
}
function addNode(that){
	if($('#nodes').children('.nodeItem').length>10){
		layer.msg('主流程最多只能添加10个')
		return;
	}
	var isHaveVoice = JSON.parse($(that).attr('hsData'))
	var tpEle  = "<div class='addMsgWrap'><div>"
	
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>名称：</label><input maxlength='15' class='addNodeInput' type='' name='' id='newNodeName' value='' />"
	    tpEle += "</div>" 
	    tpEle += "<div class='addRow'>"
	    tpEle += "<label class='addItemTit'>内容：</label><input maxlength='100' class='addNodeInput' type='' name='' id='newNodeContent' value='' />"
	    tpEle += "</div>" 
	    tpEle += "<div class='addRow'>"
	    tpEle += "<textarea maxlength='230' placeholder='输入关键词，并用空格隔开' class='addNodeKW' name='' rows='' cols='' id='newNodeKw'></textarea>"
	    tpEle += "</div>" 
	    tpEle += "<div class='addRow'>"
	    
	    tpEle += "<div id='container'><p hsData='[]' id='addSingle' class='newAudioBtn'>+添加语音</p></div>"
	    
	    tpEle+= "<div style='display:none' id='voicesec' class='voiceBox'>"
	    tpEle+= "<div onclick='playVoice(this)' class='voiceImgBox'>"
	    tpEle += "<img class='playing' src='../images/voiceImg.gif' />"
	    tpEle+= "<audio id='audioEle' class='voiceEle' src=''></audio>"
	    tpEle+= "</div>"
	    tpEle+= "<span id='fileName' class='fileName'></span><i onclick='hiddenVoice()' class='iconfont icon-shanchu delVoice'></i>"
	    tpEle+= "</div>"
	    
//	    if(isHaveVoice.length==0){
//	    	 tpEle += "000"
//	    }else{
//	    	 tpEle += "111"   
//	    }
	   	    
	    tpEle += "</div>"
	    tpEle += "</div>"	    
	    tpEle += "<div class='addRowBtn'>"
	    tpEle += "<button id='qxCreate' class='but1'>取消</button>"
	    tpEle += "<button class='but2' onclick='confirmAddNode()'>确定</button>"
	    tpEle += "</div></div>"	    
	var createBox = layer.open({
  		type: 1,
  		title: "添加节点",
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5.47rem'],
  		content: tpEle
	});
	
	configUpload('addSingle',false,0)  //初始化上传按钮 
	
	$('#qxCreate').click(function(){
		layer.close(createBox)
	})
}
function confirmAddNode(){
	var obj = {
		'named':$('#newNodeName').val().replace(/\s+/g, ""),
		'role':1,
		'keyword':$.trim($('#newNodeKw').val()).replace(/\s+/g, " "),
//		'datetime':getNowTime(),
		'content1':$('#newNodeContent').val().replace(/\s+/g, ""),
		'projectID':getQueryString('projectID'),
		'fileID1':$('#audioEle').attr('src'),
		'level':parseInt($('.nodeName:last').attr('level'))+1
	}
	if(obj.named==''||obj.keyword==''||obj.content1==''||obj.fileID1==''){
		layer.msg('信息或语音文件不能为空')
		return;
	}
	addQues(obj)
}
function addQues(obj){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/insertData",
		data:obj,
		success:function(data){			
			if(JSON.parse(data).code==0){
				layer.closeAll()
				layer.msg('保存成功')
				getMainProcess()
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}

function getFixed(){
	var obj = {
		projectID:getQueryString('projectID'),
		role:4
	}
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/roleData",
		data:obj,
		success:function(data){			
			var res = JSON.parse(data)
//			console.log(res)
//			if(res.code==1){
//				res.data = []
//			}
//			console.log(res.data)		
			var items = $('#fixedBox .specialQuesItem')
			items.find('.quesKw').val('')	
			items.find('.miVoiceBox').not('.addSign').remove()
			items.find('.addSign').css('display','block')
			
			$.each(res.data, function(index,value) {   //value为后台返回的数据中的item
				$.each(items, function(ind,val) {     //val 为页面上的specialQuesItem item   
					if(value.named==$(val).find('.quesName').val()){
						$(val).find('.quesKw').val(value.keyword)						
						var threeBox = $(val).find('.threeVoicesBox')
						threeBox.find('.miVoiceBox').not('.addSign').remove()				
						if(value.fileID3&&value.fileID3.replace(/\s+/g, "")!==''){
							renderFixed(threeBox,value.fileID3,value.content3)
						}
						if(value.fileID2&&value.fileID2.replace(/\s+/g, "")!==''){
							renderFixed(threeBox,value.fileID2,value.content2)
						}
						if(value.fileID1&&value.fileID1.replace(/\s+/g, "")!==''){
							renderFixed(threeBox,value.fileID1,value.content1)
						}																		
					}
				});
			});
		},
		error:function(data){
			console.log(data)
		}
	});
}

function renderFixed(ele,fileID,content){
//	console.log(ele)
	var editIndex = $('.miAudioBox .icon-bianji').length
	var edsid = 'editbtn'+editIndex
	
	var voiceItem = "<div class='miVoiceBox'>"
	  voiceItem  += "<div class='miAudioBox'>"
	  voiceItem  += "<div onclick='playVoice(this)' class='sec080'>"
	  voiceItem  += "<img class='playing' src='../images/voiceImg.gif' />"
	  voiceItem  += "<audio class='voiceEle' src='"+fileID+"'></audio>"
	  voiceItem  += "</div>"
	  voiceItem  += "<span class='speFileName0'>"+fileID.split('com/')[1].split('$$').pop()+"</span><span><i onclick='delFixedVoice(this)' class='iconfont icon-shanchu delVoice'></i><i onclick='editFixedVoice(this)' id='"+edsid+"' class='iconfont icon-bianji delVoice'></i></span>"
	  voiceItem  += "</div>"
	  voiceItem  += "<div class='miKwBox'>"
	  voiceItem  += "<textarea placeholder='语音内容' maxlength='200' class='sec10' name='' rows='' cols=''>"+content+"</textarea>"
	  voiceItem  += "</div>"
	  voiceItem  += "</div>"
	  
	ele.prepend(voiceItem)
	  
	checkMax(ele)
	
	configUpload(edsid,false,4)
}

function initSpeBtn(){
	var btns=['specialBut01','specialBut02','specialBut03','specialBut04','specialBut05','specialBut06','specialBut07'];
	for (var i=0;i<btns.length;i++) {
		configUpload(btns[i],false,3)  
	}
}
function editFixedVoice(that){
	console.log($('.miAudioBox .icon-bianji').length)
}
function newFixed(btnID,sourceLink){
	var editIndex = $('.miAudioBox .icon-bianji').length
	var edsid = 'editbtn'+editIndex
	
	
	var container = $('#'+btnID).parent().parent().parent()
	var voiceItem = "<div class='miVoiceBox'>"
	  voiceItem  += "<div class='miAudioBox'>"
	  voiceItem  += "<div onclick='playVoice(this)' class='sec080'>"
	  voiceItem  += "<img class='playing' src='../images/voiceImg.gif' />"
	  voiceItem  += "<audio class='voiceEle' src='"+sourceLink+"'></audio>"
	  voiceItem  += "</div>"
	  voiceItem  += "<span class='speFileName0'>"+sourceLink.split('com/')[1].split('$$').pop()+"</span><span><i onclick='delFixedVoice(this)' class='iconfont icon-shanchu delVoice'></i><i onclick='editFixedVoice(this)' id='"+edsid+"' class='iconfont icon-bianji delVoice'></i></span>"
	  voiceItem  += "</div>"
	  voiceItem  += "<div class='miKwBox'>"
	  voiceItem  += "<textarea placeholder='语音内容' maxlength='200' class='sec10' name='' rows='' cols=''></textarea>"
	  voiceItem  += "</div>"
	  voiceItem  += "</div>"
	  	  
	container.children('.addSign').before(voiceItem)
	checkMax(container)
	
	configUpload(edsid,false,4)
}
function checkMax(ele){  //需要判断的元素
	if(ele.children('.miVoiceBox').length-1>=ele.attr('max')){
		ele.children('.addSign').css('display','none')
	}else{
		ele.children('.addSign').css('display','block')
	}
}
function delFixedVoice(that){
	layer.msg('确定要删除该特殊问题吗？', {
    	time: 20000, 
    	btn: ['确定', '取消'], 
    	yes: function (index) { 
    		layer.close(index)
    		var pt = $(that).parent().parent().parent()
    		var container = pt.parent()
    		pt.remove()  		
    		checkMax(container)
    	}
   	});
}
function cancleItemFixed(that){
	showCaps0(that)
	getFixed()	
}

function saveItemFixed(that){
	var parents = $(that).parent().parent()					
	var obj = {
		'projectID':getQueryString('projectID'),
		'named':parents.parent().find('.quesName').val().replace(/\s+/g, ""),
		'keyword':$.trim(parents.parent().find('.quesKw').val()).replace(/\s+/g, " "),
		'role':4,
		'level':0		
	}
	
	if(obj.named=='未识别'||obj.named=='未讲话'){
		obj.keyword = obj.named
	}
	var childs = parents.prev().children('.miVoiceBox')
	var voNum = childs.length-1  //新增的item的语音个数
	if(voNum==0){
		layer.msg('未上传语音')
		return;
	}else if(voNum==1){
		obj.content1 = $(childs[0]).find('.sec10').val()
		obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
		obj.content2 = ' '
		obj.fileID2 = ' '
			
		obj.content3 = ' '
		obj.fileID3 = ' '
			
	}else if(voNum==2){
		obj.content1 = $(childs[0]).find('.sec10').val()
		obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
		obj.content2 = $(childs[1]).find('.sec10').val()
		obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
			
		obj.content3 = ' '
		obj.fileID3 = ' '
	}else if(voNum==3){
		obj.content1 = $(childs[0]).find('.sec10').val()
		obj.fileID1 = $(childs[0]).find('.voiceEle').attr('src')
			
		obj.content2 = $(childs[1]).find('.sec10').val()
		obj.fileID2 = $(childs[1]).find('.voiceEle').attr('src')
			
		obj.content3 = $(childs[2]).find('.sec10').val()
		obj.fileID3 = $(childs[2]).find('.voiceEle').attr('src')
			
	}		
	if(obj.named==''||obj.keyword==''||obj.content1==''||obj.content2==''||obj.content3==''){
		layer.msg('信息不能为空')
		return;
	}
	console.log(obj)
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/project/fixedData",
		data:obj,
		success:function(data){			
			if(JSON.parse(data).code==0){
				showCaps0(that)
				layer.closeAll()
				layer.msg('更新成功')
				getFixed()
			}
		},
		error:function(data){
			console.log(data)
		}
	});
}
function editVoices(eleId,sourceLink){
	$('#'+eleId).parent().prev().text(sourceLink.split('com/')[1].split('$$').pop())
	$('#'+eleId).parent().prev().prev().children('.voiceEle').attr('src',sourceLink)
}
function batchTemplate(){    //上传模板弹窗	
	var adminId = JSON.parse(sessionStorage.getItem('adminId'))
	var tpEle  = "<div class='uploadTempBox'>"    
	    tpEle += "<iframe style='display:none' name='message'></iframe>" 
		tpEle += "<form id='klFile' action='' method='post' enctype='multipart/form-data' target='message'>" 
	    tpEle += "<input id='adminId'  type='hidden' name='adminId' value='"+adminId+"' />"	
	    tpEle += "<input id='projectId'  type='hidden' name='projectId' value='"+getQueryString('projectID')+"' />"	
	    tpEle += "<i class='iconfont icon-tianjia addsign0'></i>"
		tpEle += "<input onchange='showFileName(this)' multiple id='tipt' type='file' name='excel'/></form>" 													    
	    tpEle += "</div>"
	    tpEle += "<p id='filesName'>未选择文件</p>"
	    tpEle += "<button onclick='subimtBtn()' class='uploadTemplateBtn'>确认上传</button>"		    
	layer.open({
  		type: 1,
  		title: "批量导入",
  		closeBtn: 1,
  		shadeClose: false,
  		scrollbar: false,
  		move: false,
   		area: ['5.2rem', '5rem'],
  		content: tpEle
	});

}
function showFileName(that){
	$('#filesName').text("已选择"+that.files.length+"个文件")
}
function subimtBtn() { 
	layer.load(1, {shade: [0.5,'#000'],scrollbar: false,});
 	var form = $("#klFile");   //选择from
 	var options = {  
 		url:commonUrl+"/tmk-bot/project/batchInsertData", 
 		type:'post',
 		success:function(data){  
 			layer.closeAll()
 			var res = JSON.parse(data)
 	  		if(res.code == 200){
 	  			getAssistProcess()
 	  			layer.msg(res.explain)
 	  		}else{
 	  			layer.msg(res.explain)
 	  		}
 	  		
  		}
 	};  
 	form.ajaxSubmit(options); 
}