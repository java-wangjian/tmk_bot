var form = null;
var projectArr = {};
var isFrist = true;
function loadFuncOrStyle() { //初始化样式或函数 
    isLogin()
    layui.use('form', function () {
        form = layui.form; //只有执行了这一步，部分表单元素才会自动修饰成功
    });
    unReadCount()
    $(".parentList").click(function () {
        $(this).next().toggleClass('transIcon0').toggleClass('transIcon90')
        $(this).parent().next().toggleClass('show').toggleClass('hidden')
    })
    getAccList(1)
}


function getAccList(page) {
    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/sms/listSMS",
        data: {
            'userId': JSON.parse(sessionStorage.getItem('loginMsg')).id,
            'page': page,
            'per': 10
        },
        success: function (data) {
            var res = JSON.parse(data)
            if(res.data.list.length == 0){
				var nomess = "<li><img style='margin: 0 auto;display: block;margin-top: 1rem;width: 1.8rem;' src='../images/list_no_mess.png' /></li>"
				$('#accMgrMainUl').html(nomess)
				return;
			}
            //var res = data
            $('#accMgrMainUl').html('');
            $.each(res.data.list, function (index, value) {
                var tempStr = ''
                tempStr += "<li class='accMgrMainLi accMgrItem'>"
                tempStr += "<span class='flex3'>" + (index + 1) + "</span>"
                tempStr += "<span class='flex6'>" + value.name + "</span>"
                tempStr += "<span class='flex4'>" + value.projectName + "</span>"
                tempStr += "<span class='flex4'>" + JSON.parse(value.grade).join("/") + "</span>"

                if (value.status == 1) {
                    tempStr += "<span class='flex4'><img smsid='" + value.id + "' onclick='togSwitch(this)' class='swImg' src='../images/open.png'/></span>"
                } else {
                    tempStr += "<span class='flex4'><img smsid='" + value.id + "' onclick='togSwitch(this)' class='swImg' src='../images/close.png'/></span>"
                }
				tempStr += "<span title='"+value.content+"' class='flex8'>"+value.content+"</span>"
                tempStr += "<span class='flex6 controlSpan'><span  onclick='getProjectArr(this, editAccount, \"edite\")' name='" + value.name + "' content='" + value.content + "'id='" + value.id + "' grade =' " + JSON.stringify(value.grade) + "' projectId='" + value.projectId + "' projectName='" + value.projectName + "'>编辑</span><span onclick='delServicerSin(" + value.id + ")' class='redFont'>删除</span></span>"
               
                tempStr += "</li>"
                $('#accMgrMainUl').append($(tempStr))
            });


            $('#pagination').paging({
                pageNo: page,
                totalPage: Math.ceil(res.data.count / 10),
                totalSize: res.data.count,
                callback: function (current) {
                    getAccList(current)
                }
            });
        },
        error: function (data) {
            console.log(data)
        }
    });
}

function togSwitch(that) {
    var tar = $(that)
    var sta;
    var sUrl = tar.attr('src').split('images/')[1]
    if (sUrl == 'open.png') {
        tar.attr('src', '../images/close.png')
        sta = 0
    } else {
        tar.attr('src', '../images/open.png')
        sta = 1
    }
    var chStatus = {
        smsId: tar.attr('smsid'),
        operate: sta
    }
    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/sms/switchSMS",
        data: chStatus,
        success: function (data) {
            console.log(data)
        },
        error: function (data) {
            console.log(data)
        }
    });
}

// 删除提示框
function delServicerSin(delid) {
    layer.msg('确定要删除吗？', {
        time: 20000,
        btn: ['确定', '取消'],
        yes: function (index) {
            layer.close(index)
            delServicer(delid)
        }
    });
}
//  删除ajax
function delServicer(delid) {
    var delObj = {
        id: JSON.parse(sessionStorage.getItem('loginMsg')).id,
        smsId: delid
    }

    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/sms/deleteSMS",
        data: delObj,
        success: function (data) {
            layer.closeAll()
            if (JSON.parse(data).code == 0) {
                getAccList(1)
                layer.msg('删除成功！')
            } else {
                layer.msg('删除失败！')
            }
        },
        error: function (data) {
            console.log(data)
        }
    });
}

function changeText(that) {
    var len = $(that).val().length;
    $("#last").html(len + '/' + (70 - len));
}

function addAccount(that, selEle) {
    var tpEle = "<form class='singleMsgBox layui-form' lay-filter='editForm'>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<span class='singleMsgBoxItemLabel'>模板名称:</span>"
    tpEle += '<div style="float: right;margin-right: 14px;width: 3.94rem;border: 1px solid #eeeeee;"><input name="name" class="layui-input"/></div>'
    tpEle += "</div>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<span class='singleMsgBoxItemLabel'>选择话术:</span>"
    tpEle += selEle;
    tpEle += "</div>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<span class='singleMsgBoxItemLabel'>选择标签:</span>"
    tpEle += '<div id="gradebox" style="display: inline-block"></div>'
    tpEle += "</div>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<div class='singleMsgBoxItemLabel'>短信内容:</div><br/>"
    tpEle += '<textarea name="content" class="singleMsgBoxTextarea" placeholder="请在此输入短信内容（最多70个字）" maxlength="70" class="layui-textarea" oninput="changeText(this)"></textarea>'
    tpEle += '</div>'
    tpEle += '<p style="text-align: right; padding-bottom: 10px;padding-right: 0.2rem;margin-top: 0.1rem;" id="last">0/70</p>'
    tpEle += "<div class='btnGroup1'>"
    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
    tpEle += "<button lay-submit lay-filter='demo1'id='submit' class='blueBg'>确认</button>"
    tpEle += "</div></form>"

    var singleUp = layer.open({
        type: 1,
        title: "添加短信模板",
        closeBtn: 1,
        shadeClose: false,
        scrollbar: false,
        move: false,
        area: ['6.2rem', '7.7rem'],
        content: tpEle
    });
    form.render();
    form.on('select(project)', function (data) {
        getGradeArr(data.value)
    });

    $('#cancelSingleUp').click(function () {
        cancelSingle(singleUp)
    })

    //监听提交
    form.on('submit(demo1)', function (data) {
        if ($(this).hasClass("disabled")) {
            return;
        }
        var field = data.field;
        var grade = [];
        for (var i in field) {
            if (!field[i]) {
                layer.msg('各项信息不能为空！');
                break;
                return false;
            }
            if (i.indexOf("grade") > -1) {
                if (field[i] == 'on') {
                    grade.push(i.replace("grade[", "").replace("]", ""))
                }
                delete field[i]
            }
        }
        if (grade.length <= 0) {
            layer.msg('各项信息不能为空！');
            return false;
        }
        var newField = {
            name: field['name'],
            content: field['content'],
            grade: JSON.stringify(grade),
            userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
            projectId: field["project"],
            projectName: projectArr[field["project"]]
        }
        addServicer(newField)

        return false;
    });
}

function addServicer(data) {
    $("#submit").addClass('disabled');
    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/sms/insertSMS",
        data: data,
        success: function (data) {
            $("#submit").removeClass('disabled');          
            if (JSON.parse(data).code == 0) {
                getAccList(1)
                layer.msg('添加成功')
                layer.closeAll()
            } else if(JSON.parse(data).code == 404) {
                layer.msg('各项信息不能为空！')
            }
        },
        error: function (data) {
            console.log(data)
        }
    });
}

function getProjectArr(that, callback, type) {
    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/project/allName",
        data: {
            userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
        },
        success: function (data) {
            var res = JSON.parse(data)
           // var res = data
            var disabled = type === 'edite' ? 'disabled' : ''
            var selEle = '<div style="display: inline-block; width: 4rem;">'
            selEle += '<select lay-filter="project" name="project" ' + disabled + '>'
            $.each(res.list, function (index, value) {
                projectArr[value.id] = value.projectName;
                selEle += '<option value="' + value.id + '">' + value.projectName + '</option>'
            })
            selEle += '</select></div>'
            if(type == 'edite') {
            	getGradeArr($(that).attr("id"), that, type)
            } else {
            	getGradeArr(res.list[0]['id'], that, type)               
            }           
            callback(that, selEle)
        },
        error: function (data) {
            console.log(data)
        }
    });
}

function getGradeArr(projectId, that, type) {
    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/project/projectGrade",
        data: {
            projectId: projectId,
            userId: JSON.parse(sessionStorage.getItem('loginMsg')).id,
        },
        success: function (data) {
            var res = JSON.parse(data)
            //var res = data
            var arr = ['A','B','C','D','E','F']
            var tpEle = ''

            var newGrades = unite(res.grades, $(that).attr("grade"))
            $.each(arr, function(index, item) {
                tpEle += '<input type="checkbox" name="grade['+item+']" lay-skin="primary" title="'+item+'"' + (newGrades.indexOf(item) > -1 ? "disabled checked" : "") +'>'
            })    
            
            $("#gradebox").html(tpEle)
            form.render();
        },
        error: function (data) {
            console.log(data)
        }
    });
}

//编辑账户	
function editAccount(that, selEle) {
    grade = $(that).attr("grade");
    var tpEle = "<form class='singleMsgBox layui-form' lay-filter='editForm'>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<span class='singleMsgBoxItemLabel'>模板名称:</span>"
    tpEle += '<input name="name" disabled/>'
    tpEle += "</div>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<span class='singleMsgBoxItemLabel'>选择项目:</span>"
    tpEle += selEle;
    tpEle += "</div>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<span class='singleMsgBoxItemLabel'>选择标签:</span>"
    tpEle += '<div id="gradebox" style="display: inline-block"></div>'
    tpEle += "</div>"
    tpEle += "<div class='singleMsgBoxItem'>"
    tpEle += "<div class='singleMsgBoxItemLabel'>短信内容:</div><br/>"
    tpEle += '<textarea name="content" class="singleMsgBoxTextarea" placeholder="请在此输入短信内容（最多70个字）" maxlength="70" class="layui-textarea" oninput="changeText(this)"></textarea>'
    tpEle += '</div>'
    tpEle += '<p style="text-align: right; padding-bottom: 10px;padding-right: 0.2rem;margin-top: 0.1rem;" id="last"></p>'
    tpEle += '<input style="display:none" name="id"/>'
    tpEle += "<div class='btnGroup1'>"
    tpEle += "<button id='cancelSingleUp' class='grayBg'>取消</button>"
    tpEle += "<button lay-submit lay-filter='demo1' class='blueBg'>确认</button>"
    tpEle += "</div></form>"

    var singleUp = layer.open({
        type: 1,
        title: "编辑短信模板",
        closeBtn: 1,
        shadeClose: false,
        scrollbar: false,
        move: false,
        area: ['6.4rem', '7.7rem'],
        content: tpEle
    });
    form.render();
    //表单初始赋值
    form.val('editForm', {
        "id": $(that).attr("id"),
        "name": $(that).attr("name"),
        "project": $(that).attr("projectId"),
        "content": $(that).attr("content")
    })
    $("#last").html($(that).attr("content").length + '/' + (70 - $(that).attr("content").length))
    $('#cancelSingleUp').click(function () {
        cancelSingle(singleUp)
    })

    //监听提交
    form.on('submit(demo1)', function (data) {
        var field = data.field;
        var grade = [];
        for (var i in field) {
            if (i.indexOf("grade") > -1) {
                if (field[i] == 'on') {
                    grade.push(i.replace("grade[", "").replace("]", ""))
                }
                delete field[i]
            }
        }
        var newField = {
            id: field['id'],
            content: field['content'],
            grade: JSON.stringify(grade),
        }
        if(isFrist){
        	console.log(newField)
            editServicer(newField)
        }
		isFrist = false;
        return false;
    });
}

function editServicer(data) {
    $.ajax({
        type: "post",
        url: commonUrl + "/tmk-bot/sms/updateSMS",
        data: data,
        success: function (data) {
            layer.closeAll()
            if (JSON.parse(data).code == 0) {
                getAccList(1)
                layer.msg('编辑成功！')
            } else {
                layer.msg('编辑失败！')
            }
            isFrist = true;
        },
        error: function (data) {
            console.log(data)
        }
    });
}


function unite(arr1, arr2, arr3) {   
    //使用reduce 合并数组  
    var args=Array.from(arguments);//调用unite函数的参数   
    var newArr=args.reduce(function(prev,cur){//prev>>初始值 cur>>当前的值  
      return prev.concat(cur);  
    });  
    //去除数组中的重复值  
   var outArr=newArr.filter(function(value,index){//value 当前的值 index>>当前值的索引  
   //因为indexOf()返回value出现的第一个索引位置 index是当前值得索引位置  
   return newArr.indexOf(value)==index;  
 });   
  return outArr;  
}