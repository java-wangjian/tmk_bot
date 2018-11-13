(function($){
		var myDate = new Date();	
		var lw = new Date(myDate - 1000 * 60 * 60 * 24 * 1);//最后一个数字30可改
		var lastY = lw.getFullYear();
		var lastM = lw.getMonth()+1;
		var lastD = lw.getDate();
		var startdate=lastY+"-"+(lastM<10 ? "0" + lastM : lastM)+"-"+(lastD<10 ? "0"+ lastD : lastD);//三十天之前日期
//		console.log(startdate)
	
    $.setStartTime = function(){
        $('.startDate').datepicker({
            dateFormat: "yy-mm-dd",
            maxDate: startdate,
            onClose : function(dateText, inst) {
                $( "#endDate" ).datepicker( "show" );
            },
			onSelect:function(dateText, inst) {
                $( "#endDate" ).datepicker( "option","minDate",dateText );
            },
			
        });
    };
    $.setEndTime = function(){
        $(".endDate").datepicker({
            dateFormat: "yy-mm-dd",
            maxDate: startdate,
			defaultDate : new Date(),
            onClose : function(dateText, inst) {
                if (dateText < $("input[name=startDate]").val()){
                  $( "#endDate" ).datepicker( "show" );
				    alert("结束日期不能小于开始日期！");
					//$("#endDate").val(newdate)
                }
            }
        });
    };
    $.date = function(){
        $('.date').datepicker(
            $.extend({showMonthAfterYear:true}, $.datepicker.regional['zh-CN'],
                {'showAnim':'','dateFormat':'yy-mm-dd','changeMonth':'true','changeYear':'true',
                    'showButtonPanel':'true'}
            ));
    };
    $.datepickerjQ = function(){
       $(".ui-datepicker-time").on("click",function(){
           $(".ui-datepicker-css").css("display","block")
        });
        $(".ui-kydtype li").on("click",function(){
            $(".ui-kydtype li").removeClass("on").filter($(this)).addClass("on");
//            getAppCondtion();
        });
        $(".ui-datepicker-quick input").on("click",function(){
            var thisAlt = $(this).attr("alt");
            var dateList = timeConfig(thisAlt);
            $(".ui-datepicker-time").val(dateList);
            $(".ui-datepicker-css").css("display","none");
			 $("#ui-datepicker-div").css("display","none")
//            getAppCondtion()
        });
        $(".ui-close-date").on("click",function(){
            $(".ui-datepicker-css").css("display","none")
			 $("#ui-datepicker-div").css("display","none")
			//inst.dpDiv.css({"display":"none"})
        });
		 $(".startDate").on("click",function(){
            $(".endDate").attr("disabled",false);
        });
	
    }
})(jQuery);

$(function(){
        $.setStartTime();
        $.setEndTime();
        $.datepickerjQ();
		
        var nowDate = new Date();
        timeStr = nowDate.getFullYear() + '-' + (nowDate.getMonth()+1) + '-' + nowDate.getDate();
        nowDate.setDate(nowDate.getDate()+parseInt(-1));
        var endDateStr = nowDate.getFullYear() + '-'+  (nowDate.getMonth()+1) + '-' + nowDate.getDate();
		$(".ui-datepicker-time").attr("value",endDateStr +"-"+ timeStr)
		$("#startDate").attr("value",endDateStr)
		$("#endDate").attr("value",timeStr)
    });


    function timeConfig(time){
		//快捷菜单的控制
        var nowDate = new Date();
        timeStr = '-' + nowDate.getFullYear() + '-' + (nowDate.getMonth()+1) + '-' + nowDate.getDate();
        nowDate.setDate(nowDate.getDate()+parseInt(time));
        var endDateStr = nowDate.getFullYear() + '-'+  (nowDate.getMonth()+1) + '-' + nowDate.getDate();
        if(time == -1){
            endDateStr += '-' + endDateStr;
        }else{
            endDateStr += timeStr;
        }
        dateMess=endDateStr
        console.log(dateMess)
        return endDateStr;
    }

    function datePickers(){
		//自定义菜单
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        var dateList = startDate +'-'+ endDate;  //自选日期符号
        $(".ui-datepicker-time").val(dateList);
        $(".ui-datepicker-css").css("display","none");
//        getAppCondtion(dateList)
		dateMess=dateList
		console.log(dateMess)
    }