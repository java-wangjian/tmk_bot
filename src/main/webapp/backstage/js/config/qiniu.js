//配置上传的js文件
var gToken = ""
getUptoken()
function getUptoken(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/getToken",
		success:function(data){
			const res = JSON.parse(data)
			gToken = res.result
//			configUpload(eleId,type)   
			
		},
		error:function(data){
			console.log(data)
		} 
	});
}
function configUpload(eleId,type,callback) {    //上传按钮id 上传按钮父元素id 是否选择多个文件  上传完成执行事件
    var uploader = Qiniu.uploader({
    disable_statistics_report: false,
//  runtimes: 'html5,flash,html4',
    browse_button: eleId,
//  container: eleParentId,
//  drop_element: eleParentId,
    max_file_size: '100mb', 
    filters: {
		  mime_types : [ 
        { title : "voice files", extensions : "mp3,wav" }
      ],
      max_file_size : '10000kb',
      prevent_duplicates : false 
    },
    flash_swf_url: 'bower_components/plupload/js/Moxie.swf',
    dragdrop: true,
//  chunk_size: '4mb',
    multi_selection: type,
    uptoken :gToken,
//  domain: 'http://pdr6htoqn.bkt.clouddn.com/',
//  domain: 'http://p6wdw0ynq.bkt.clouddn.com/',
    domain:'http://qiniu.91tmk.com/',
//  get_new_uptoken: true,
//  downtoken_url: '/downtoken',
    unique_names: false,
    save_key: false,
    // x_vars: {
    //     'id': '1234',
    //     'time': function(up, file) {
    //         var time = (new Date()).getTime();
    //         // do something with 'time'
    //         return time;
    //     },
    // },
    auto_start: true,
    log_level: 0,
    init: {
      'BeforeChunkUpload': function(up, file) {
      },
      'FilesAdded': function(up, files) {
			
      },
      'BeforeUpload': function(up, file) { //
			upwait=layer.load(0, {shade: false,content:'<span style="line-height: 80px;color: #666;display: inline-block;text-align: center;width: 100%;">上传中</span>'});
			
      },
      'UploadProgress': function(up, file) {

      },
      'UploadComplete': function() {
        layer.close(upwait)
      },
      'FileUploaded': function(up, file, info) {
      	var domain = up.getOption('domain');
        var res = JSON.parse(info.response);
        var sourceLink = domain + res.key;   //单个文件的完整路径

        if(callback==0){   //主流程上传完语音后执行
        	voices=[]
        	voices.push(sourceLink)
        	$('#'+eleId).attr('hsData',JSON.stringify(voices))
        	$('#audioEleEdit').attr('src',sourceLink)
        	$('#audioEle').attr('src',sourceLink)
        	showVoice()
        }else if(callback==1){
        	var obj = {
        		fileID1 :sourceLink,
        		named:'',
        		content1:'',
        		keyword:'',
        		id:''
        	}
        	newAssistItem(obj)
        	showAssistNum()
        	keepBottom('assistContainer')       	
        }else if(callback==2){
        	console.log(eleId)
        	var obj = {
        		urls :sourceLink
        	}
        	moreAddVoice(eleId,obj)
        }else if(callback==3){ 
        	newFixed(eleId,sourceLink)
        }else if(callback==4){ 
        	editVoices(eleId,sourceLink)
        	
        }else if(callback==5){ 
        	editAssistVoices(eleId,sourceLink)
        }        
      },
      'Key': function(up, file) {
            var folder = new Date().getTime()+'$$' ;             
            var temp = folder+file.name;
            var key = temp.replace(/\s|\xA0/g,"")
            return key
      },
      'Error': function(up, err, errTip) {
         layer.close(upwait)
//       console.log(up, err, errTip)
		 if(err.code==-200){
		 	layer.msg('该文件名称与其他文件名冲突，请修改文件名后重试')
		 }
      }       
    }
  });
  uploader.bind('FileUploaded', function(up,file,info) {
    var domain = up.getOption('domain');
    var res = JSON.parse(info.response)
    var sourceLink = domain + res.key; //获取上传成功后的文件的Url
//  console.log(sourceLink);
  });
  var upwait;
  var voices = [];
}
