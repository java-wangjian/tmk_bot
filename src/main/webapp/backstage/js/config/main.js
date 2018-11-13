var gToken = ""
function getUptoken(){
	$.ajax({
		type:"post",
		url:commonUrl+"/tmk-bot/admin/getToken",
		success:function(data){
			const res = JSON.parse(data)
			gToken = res.result
			configUpload()       
		},
		error:function(data){
			console.log(data)
		} 
	});
}
function configUpload() {
  
  var uploader = Qiniu.uploader({
    disable_statistics_report: false,
//  runtimes: 'html5,flash,html4',
    browse_button: 'pickfiles',
    container: 'container',
    drop_element: 'container',
    max_file_size: '100mb', 
    filters: {
		  mime_types : [ 
        { title : "voice files", extensions : "mp3" }
      ],
      max_file_size : '400kb', //最大只能上传400kb的文件
      prevent_duplicates : true //不允许选取重复文件
    },
    flash_swf_url: 'bower_components/plupload/js/Moxie.swf',
    dragdrop: true,
//  chunk_size: '4mb',
    multi_selection: !(moxie.core.utils.Env.OS.toLowerCase() === "ios"),
    uptoken :gToken,
    domain: 'http://pdr6htoqn.bkt.clouddn.com/',
//  domain: 'http://p6wdw0ynq.bkt.clouddn.com/',
    get_new_uptoken: false,
    //downtoken_url: '/downtoken',
    // unique_names: true,
    // save_key: true,
    // x_vars: {
    //     'id': '1234',
    //     'time': function(up, file) {
    //         var time = (new Date()).getTime();
    //         // do something with 'time'
    //         return time;
    //     },
    // },
    auto_start: true,
    log_level: 5,
    init: {
      'BeforeChunkUpload': function(up, file) {
      },
      'FilesAdded': function(up, files) {
			
      },
      'BeforeUpload': function(up, file) {
			upwait=layer.load(0, {shade: false,content:'<span style="line-height: 80px;color: #666;display: inline-block;text-align: center;width: 100%;">上传中</span>'});
			
      },
      'UploadProgress': function(up, file) {

      },
      'UploadComplete': function() {
        layer.close(upwait)
        console.log('上传完成')
      },
      'FileUploaded': function(up, file, info) {
      	var domain = up.getOption('domain');
        var res = JSON.parse(info.response);
        var sourceLink = domain + res.key; 
//      var tp = {
//      	fileName:res.key,
//      	url:sourceLink
//      }
        voices.push(sourceLink)
        uploadVoice(voices)
        
        
        
      },
      'Error': function(up, err, errTip) {
         layer.close(upwait)
      }
       
    }
  });
  uploader.bind('FileUploaded', function(up,file,info) {
    var domain = up.getOption('domain');
    var res = JSON.parse(info.response)
    var sourceLink = domain + res.key; //获取上传成功后的文件的Url
    console.log(sourceLink);
  });
  var upwait;
  var voices = [];
}
