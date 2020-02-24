$(function () {
    var deletePublicArticleId="";
    var deleteDraftArticleId="";
    var deleteCommentManage="";
    var myAttentionSearch = "";
    var myFanSearch = "";

    var category = "choose";
    var categories;
    var firstPublicDate = "";
    var lastPublicDate = "";
    var publishArticleTitle = "";
    var articleType = "choose";

    var draftArticleTitle = "";

    var commentArticleTitle = "";
    var commentToContent = "";
    var firstCommentDate = "";
    var lastCommentDate = "";
    var searchUsername = "";

    var firstCommentMsgDate = "";
    var lastCommentMsgDate = "";
    var commentMsgRead = "choose";

    var firstArticleThumbsUpDate = "";
    var lastArticleThumbsUpDate = "";
    var articleThumbsUpRead = "choose";

    var firstCommentThumbsUpDate = "";
    var lastCommentThumbsUpDate = "";
    var commentThumbsUpRead = "choose";


    $('.userList .clickLi').click(function () {
        var flag = $(this).attr('class').substring(8);
        $('#personalDate,#basicSetting,#publishArticle,#draftArticle,#myCommentManage,#commentMessage,#articleByThumbsUp,#commentByThumbsUp,#myAttention,#myFan').css("display","none");
        $("#" + flag).css("display","block");
    });

    $('.basicSetting').click(function () {
        $('#phone').val("");
        $('#authCode').val("");
        $('#password').val("");
        $('#surePassword').val("");
    });

    // 获得个人信息
    function getUserPersonalInfo() {
        $.ajax({
            type: 'POST',
            url: '/getUserPersonalInfo',
            dataType: 'json',
            data:{
            },
            success:function (data) {
                if(data['status'] === 101){
                    $.get("/toLogin",function(data,status,xhr){
                        window.location.replace("/login");
                    });
                } else {
                    $('#username').attr("value",data['data']['username']);
                    var personalPhone = data['data']['phone'];
                    $('#personalPhone').html(personalPhone.substring(0,3) + "****" + personalPhone.substring(7));
                    $('#trueName').attr("value",data['data']['trueName']);
                    $('#birthday').attr("value",data['data']['birthday']);
                    var gender = data['data']['gender'];
                    if(gender === "male"){
                        $('.genderTable input').eq(0).attr("checked","checked");
                    } else {
                        $('.genderTable input').eq(1).attr("checked","checked");
                    }
                    $('#email').attr("value",data['data']['email']);
                    $('#personalBrief').val(data['data']['personalBrief']);

                    $('#headPortrait').attr("src",data['data']['avatarImgUrl']);
                }
            },
            error:function () {
                alert("获取个人信息失败！！！")
            }
        });
    }

    // 设置日期选择框不可选择今天过后的日期
    function checkDate(id) {
        //alert(id)
        var nowTemp = new Date();
        var nowDay = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0).valueOf();
        var nowMoth = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), 1, 0, 0, 0, 0).valueOf();
        var nowYear = new Date(nowTemp.getFullYear(), 0, 1, 0, 0, 0, 0).valueOf();
        console.log("nowTemp:" + nowTemp + ", " + "nowYear:" + nowYear + ", " + "nowMoth:" + nowMoth + ", " + "nowDay:" + nowDay);

        var $birthday = $(id);
        var checkin = $birthday.datepicker({
            onRender: function(date, viewMode) {
                // 默认 days 视图，与当前日期比较
                var viewDate = nowDay;

                switch (viewMode) {
                    // moths 视图，与当前月份比较
                    case 1:
                        viewDate = nowMoth;
                        break;
                    // years 视图，与当前年份比较
                    case 2:
                        viewDate = nowYear;
                        break;
                }
                return date.valueOf() > viewDate ? 'am-disabled' : '';
            }
        }).on('changeDate.datepicker.amui', function(ev) {
            checkin.close();
        }).data('amui.datepicker');
    }

    checkDate('#birthday');


    // 保存个人资料
    var savePersonalDateBtn = $('#savePersonalDateBtn');
    var username = $('#username');
    var trueName = $('#trueName');
    var birthday = $('#birthday');
    var gender = $('.genderTable input');
    var email = $('#email');
    var reg = /^([a-zA-Z]|[0-9])(\w|-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
    var personalBrief = $('#personalBrief');
    savePersonalDateBtn.click(function () {
        var emailValue = email.val();
        var usernameValue = username.val();
        var genderValue = "male";
        if(usernameValue.length === 0){
            dangerNotice("昵称不能为空");
        } else if(!gender[0].checked && !gender[1].checked){
            dangerNotice("性别不能为空");
        } else if (emailValue !== "" && emailValue.length !== 0 && !reg.test(emailValue)) {
            dangerNotice("邮箱格式不对哦");
        } else {
            if(gender[0].checked){
                genderValue = "male";
            } else {
                genderValue = "female";
            }
            console.log(birthday.val());
            $.ajax({
                type: 'POST',
                url: '/savePersonalDate',
                dataType: 'json',
                data:{
                    username: username.val(),
                    trueName: trueName.val(),
                    birthday: birthday.val(),
                    gender: genderValue,
                    email: email.val(),
                    personalBrief: personalBrief.val()
                },
                success:function (data) {
                    if(data['status'] === 101){
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else {
                        if(data['status'] === 503){
                            successNotice("更改成功,重新登录后生效（如果记住账号的就不必重新登录，请等待刷新）");
                            setTimeout(function () {
                                //window.location.reload();
                                location.reload();
                            },3000);
                        } else if (data['status'] === 501){
                            dangerNotice("更改失败，昵称太长啦");
                        } else if (data['status'] === 502){
                            dangerNotice("更改失败，昵称不能为空");
                        } else if (data['status'] === 504){
                            successNotice("更改个人信息成功");
                        } else if (data['status'] === 505){
                            dangerNotice("该昵称已被占用");
                        } else {
                            dangerNotice("更改个人信息失败");
                        }
                    }
                },
                error:function () {
                    alert("更改个人信息请求失败")
                }
            });
        }
    });

    getUserPersonalInfo();

    var phone = $('#phone');
    var authCode = $('#authCode');
    var password = $('#password');
    var surePassword = $('#surePassword');

    phone.blur(function () {
        var pattren = /^1[345789]\d{9}$/;
        var phoneValue = phone.val();
        if(pattren.test(phoneValue)){
            phone.removeClass("wrong");
            phone.addClass("right");
        } else {
            phone.removeClass("right");
            phone.addClass("wrong");
        }
    });
    phone.focus(function () {
        $('.notice').css("display","none");
    });

    // 定义发送时间间隔(s)
    var my_interval;
    my_interval = 60;
    var timeLeft = my_interval;
    //重新发送计时函数
    var timeCount = function() {
        window.setTimeout(function() {
            if(timeLeft > 0) {
                timeLeft -= 1;
                $('#authCodeBtn').html(timeLeft + "秒重新发送");
                timeCount();
            } else {
                $('#authCodeBtn').html("重新发送");
                timeLeft=60;
                $("#authCodeBtn").attr('disabled',false);
            }
        }, 1000);
    };
    //发送短信验证码
    $('#authCodeBtn').click(function () {
        $('.notice').css("display","none");
        $('#authCodeBtn').attr('disabled',true);
        var phoneLen = phone.val().length;
        if(phoneLen === 0){
            dangerNotice("手机号不能为空");
            $('#authCodeBtn').attr('disabled',false);
        } else {
            if(phone.hasClass("right")){
                $.ajax({
                    type: 'GET',
                    url: '/code/sms',
                    dataType: 'json',
                    data:{
                        mobile: $('#phone').val(),
                        sign: "changePassword"
                    },
                    success:function (data) {
                        if(parseInt(data['status']) === 0) {
                            successNotice("短信验证码发送成功");
                            timeCount();
                        } else {
                            dangerNotice("短信验证码发送异常");
                        }
                    },
                    error:function () {
                        alert("短信验证码请求失败");
                    }
                });
            } else {
                dangerNotice("手机号不正确");
                $('#authCodeBtn').attr('disabled',false);
            }
        }

    });

    //修改密码
    $('#changePasswordBtn').click(function () {
        $('.notice').css("display","none");
        if(phone.val().length === 0){
            dangerNotice("手机号不能为空");
        } else if (phone.hasClass("wrong")){
            dangerNotice("手机号不正确");
        } else if (authCode.val().length === 0){
            dangerNotice("验证码不能为空");
        } else if (password.val().length === 0){
            dangerNotice("新密码不能为空");
        } else if (surePassword.val().length === 0){
            dangerNotice("确认密码不能为空");
        } else{
            if (password.val() !== surePassword.val()){
                dangerNotice("确认密码不正确");
            } else {
                $.ajax({
                    type: 'POST',
                    url: '/changePassword',
                    dataType: 'json',
                    data:{
                        phone: phone.val(),
                        authCode: authCode.val(),
                        newPassword: password.val()
                    },
                    success: function (data) {
                        if(data['status'] === 902){
                            dangerNotice("验证码不正确")
                        } else if (data['status'] === 508){
                            dangerNotice("手机号不存在")
                        } else if(data['status'] === 901){
                            dangerNotice("手机号不正确");
                        } else if (data['status'] === 1203) {
                            dangerNotice("验证码已过期");
                        } else {
                            successNotice("密码修改成功,请重新登录（如果记住账号的就不必重新登录，下次登录时生效）");
                            setTimeout(function () {
                                location.reload();
                            },3000);
                        }
                    },
                    error:function () {
                        dangerNotice("修改密码失败");
                    }
                })
            }
        }
    });

    $.ajax({
        type: "GET",
        url: "/findCategoriesName",
        async: false,
        data:{
        },
        dataType: "json",
        success:function (data) {
            categories = data['data'];

        },
        error:function () {
        }

    });

    // 填充用户发布文章
    function putInPublishArticleManagement(data, selectCategory, categories, selectArticleType) {

        var publishArticleMessageTable = $('.publishArticleMessageTable');
        publishArticleMessageTable.empty();

        publishArticleMessageTable.append($('<div class="contentTop">' +
            '目前已发布文章总数：<span class="categoryNum">' + data['total'] + '</span>' +
            '<br>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="publishArticleTitle" placeholder="请输入文章标题"/>' +

            '<select id="select-articleType">' +
            '<option class="articleTypeOption" value="choose">文章类型</option>' +
            '<option class="articleTypeOption" value="原创">原创</option>' +
            '<option class="articleTypeOption" value="转载">转载</option>' +
            '</select>' +

            '<select id="select-categories">' +
            '</select>' +

            '<input class="articleInput" type="text" id="firstPublicDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastPublicDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="publicArticleRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="publicArticleReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 250px">文章标题</th><th style="width: 180px">发布时间</th><th style="width: 120px">文章类型</th>' +
            '<th style="width: 200px">文章分类</th><th style="width: 250px">文章标签</th><th style="width: 80px">喜欢数</th>' +
            '<th style="width: 80px">收藏数</th><th style="width: 80px">浏览量</th><th style="width: 200px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="a' + obj['articleId'] + '">' +
                '<td><a href="article/' + obj['articleId'] + '" target="_blank" title="'+ obj['articleTitle'] +'">' + obj['articleTitle'] + '</a></td>' +
                '<td>' + obj['publishDate'] + '</td>' +
                '<td>' + obj['articleType'] + '</td>' +
                '<td>' + obj['articleCategories'] + '</td>' +
                '<td>' + obj['articleTags'] + '</td>' +
                '<td><span class="am-badge am-badge-success am-round">' + obj['likes'] + '</span></td>' +
                '<td><span class="am-badge am-badge-success am-round">' + obj['favorites'] + '</span></td>' +
                '<td><span class="am-badge am-badge-success am-round">' + obj['visitNum'] + '</span></td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="articlePublishManagementBtn articleEditor am-btn am-btn-secondary am-round">编辑</button>' +
                '<button class="articlePublishDeleteBtn articleDelete am-btn am-btn-danger am-round">删除</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        publishArticleMessageTable.append(table);
        publishArticleMessageTable.append($('<div class="my-row" id="page-father">' +
            '<div id="articlePublicManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.articlePublishManagementBtn').click(function () {
            var $this = $(this);
            var id = $this.parent().parent().parent().attr("id").substring(1);
            // window.location.replace("/editor?id=" + id);
            // window.location.href = "/editor?id=" + id
            window.open("/editor?id=" + id, "_blank");
        });

        $('.articlePublishDeleteBtn').click(function () {
            var $this = $(this);
            deletePublicArticleId = $this.parent().parent().parent().attr("id").substring(1);
            var deletePublicArticleName = $this.parent().parent().parent().children("td").eq(0).find("a").html();

            $('#deletePublicAlterContent').html('你确定要删除文章标题为：<span class="am-btn-danger">' + deletePublicArticleName +'</span>&nbsp;的文章吗？<br>删除了就无法恢复了呦!');

            $('#deletePublicAlter').modal('open');
        });

        checkDate('#firstPublicDate');
        checkDate('#lastPublicDate');

        var selectCategories = $('#select-categories');
        selectCategories.append($('<option class="categoriesOption" value="choose">文章分类</option>'));
        for(var i = 0; i < categories.length; i++){
            selectCategories.append($('<option class="categoriesOption" value="' + categories[i] + '">' + categories[i] + '</option>'));
        }
        var categoriesOption = selectCategories.find('.categoriesOption');

        for (var j = 0; j < categoriesOption.length; j++) {
            if (selectCategory === categoriesOption.eq(j).val()) {
                categoriesOption[j].selected = true;
            }
        }

        var articleTypeToSelect = $('#select-articleType');
        var articleTypeOption = articleTypeToSelect.find('.articleTypeOption');
        if (selectArticleType === "choose") {
            articleTypeOption[0].selected = true;
        } else if (selectArticleType === "原创") {
            articleTypeOption[1].selected = true;
        } else if (selectArticleType === "转载") {
            articleTypeOption[2].selected = true;
        } else {
            articleTypeOption[0].selected = true;
        }

        var selectFirstPublicDate = $('#firstPublicDate');
        var selectLastPublicDate = $('#lastPublicDate');
        selectFirstPublicDate.change(function () {
           if (selectFirstPublicDate.val() === null || selectFirstPublicDate.val() === "" || typeof(selectFirstPublicDate.val()) === "undefined" ) {
               selectLastPublicDate.attr('disabled', true);
           } else {
               selectLastPublicDate.attr('disabled', false);
           }
           selectLastPublicDate.attr("value","");
        });
        selectLastPublicDate.change(function () {
            var first = selectFirstPublicDate.val();
            var last = selectLastPublicDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastPublicDate.attr("value","");
            }
        });


        $('#publicArticleRetrieve').click(function () {
            publishArticleTitle = $('#publishArticleTitle').val();
            articleType = articleTypeToSelect.val();
            category = selectCategories.val();
            firstPublicDate = selectFirstPublicDate.val();
            lastPublicDate = selectLastPublicDate.val();
            getPublicArticleManagement(1);
        });

        $('#publicArticleReset').click(function () {
            firstPublicDate = "";
            lastPublicDate = "";
            publishArticleTitle = "";
            articleType = "choose";
            category = "choose";

            getPublicArticleManagement(1);
        });

        $('#publishArticleTitle').val(publishArticleTitle);
        articleTypeToSelect.val(articleType);
        selectCategories.val(category);
        selectFirstPublicDate.attr("value",firstPublicDate);
        selectLastPublicDate.attr("value",lastPublicDate);

        if (selectFirstPublicDate.val() === null || selectFirstPublicDate.val() === "" || typeof(selectFirstPublicDate.val()) === "undefined" ) {
            selectLastPublicDate.attr('disabled', true);
        }
    }

    // 获得已发布文章管理
    function getPublicArticleManagement(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getPublishArticleManagementByUser',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                articleTitle: publishArticleTitle,
                articleType: articleType,
                articleCategory: category,
                firstDate: firstPublicDate,
                lastDate: lastPublicDate
            },
            success: function (data) {
                putInPublishArticleManagement(data['data'], category, categories, articleType);
                scrollTo(0,0);//回到顶部

                //分页
                $("#articlePublicManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getPublicArticleManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取已发布文章信息失败")
            }
        });
    }

    // 删除已发布文章
    $('.surePublicArticleDeleteBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/deleteArticle',
            dataType: 'json',
            data:{
                id: deletePublicArticleId
            },
            success: function (data) {
                if (data['status'] === 201) {
                    dangerNotice("删除已发布文章失败")
                } else {
                    successNotice("删除已发布文章成功");
                    getPublicArticleManagement(1);
                }
            },
            error: function () {
                alert("删除已发布文章失败")
            }
        });
    });

    getPublicArticleManagement(1);


    // 填充用户草稿箱文章信息
    function putInDraftArticleManagement(data) {

        var draftArticleMessageTable = $('.draftArticleMessageTable');
        draftArticleMessageTable.empty();

        draftArticleMessageTable.append($('<div class="contentTop">' +
            '目前草稿箱文章总数：<span class="categoryNum">' + data['total'] + '</span>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="draftArticleTitle" placeholder="请输入文章标题"/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="draftArticleRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="draftArticleReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 300px">文章标题</th>' +
            '<th style="width: 300px">更新时间</th>' +
            '<th style="width: 300px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="d' + obj['articleId'] + '">' +
                '<td>' + obj['articleTitle'] + '</a></td>' +
                '<td>' + obj['updateDate'] + '</td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="articleDraftManagementBtn articleEditor am-btn am-btn-secondary am-round">编辑</button>' +
                '<button class="articleDraftDeleteBtn articleDelete am-btn am-btn-danger am-round">删除</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        draftArticleMessageTable.append(table);
        draftArticleMessageTable.append($('<div class="my-row" id="page-father">' +
            '<div id="articleDraftManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.articleDraftManagementBtn').click(function () {
            var $this = $(this);
            var id = $this.parent().parent().parent().attr("id").substring(1);
            // window.location.replace("/editor?id=" + id);
            // window.location.href = "/editor?id=" + id
            window.open("/editor?draftId=" + id, "_blank");
        });

        $('.articleDraftDeleteBtn').click(function () {
            var $this = $(this);
            deleteDraftArticleId = $this.parent().parent().parent().attr("id").substring(1);
            var deleteDraftArticleName = $this.parent().parent().parent().children("td").eq(0).html();

            $('#deleteDraftAlterContent').html('你确定要删除文章标题为：<span class="am-btn-danger">' + deleteDraftArticleName +'</span>&nbsp;的文章吗？<br>删除了就无法恢复了呦!');

            $('#deleteDraftAlter').modal('open');
        });

        $('#draftArticleRetrieve').click(function () {
            draftArticleTitle = $('#draftArticleTitle').val();
            getDraftArticleManagement(1);
        });

        $('#draftArticleReset').click(function () {
            draftArticleTitle = "";
            getDraftArticleManagement(1);
        });

        $('#draftArticleTitle').val(draftArticleTitle);
    }

    // 获得草稿箱文章管理
    function getDraftArticleManagement(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getDraftArticleManagementByUser',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                articleTitle: draftArticleTitle
            },
            success: function (data) {
                putInDraftArticleManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#articleDraftManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getDraftArticleManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取已发布文章信息失败")
            }
        });
    }

    // 删除草稿箱文章
    $('.sureDraftArticleDeleteBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/deleteArticle',
            dataType: 'json',
            data:{
                id: deleteDraftArticleId
            },
            success: function (data) {
                if (data['status'] === 201) {
                    dangerNotice("删除草稿箱文章失败")
                } else {
                    successNotice("删除草稿箱文章成功");
                    getDraftArticleManagement(1);
                }
            },
            error: function () {
                alert("删除草稿箱文章失败")
            }
        });
    });

    getDraftArticleManagement(1);


    // 填充用户评论管理（我的评论）
    function putInMyCommentManagement(data) {

        var myCommentManageMessageTable = $('.myCommentManageMessageTable');
        myCommentManageMessageTable.empty();

        myCommentManageMessageTable.append($('<div class="contentTop">' +
            '目前我的评论总数：<span class="categoryNum">' + data['total'] + '</span>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="commentArticleTitle" placeholder="请输入文章标题"/>' +

            '<input class="articleInput" type="text" id="commentToContent" placeholder="请输入评论内容"/>' +

            '<input class="articleInput" type="text" id="firstCommentDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastCommentDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="commentRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="commentReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 400px">文章标题</th><th style="width: 500px">回复对象（主评论或文章）</th>' +
            '<th style="width: 500px">评论内容</th><th style="width: 300px">评论日期</th>' +
            '<th style="width: 100px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="m' + obj['id'] + '">' +
                '<td><a href="/article/' + obj['articleId'] +'" target="_blank" title="'+ obj['articleTitle'] +'">' + obj['articleTitle'] + '</a></td>' +
                '<td><a href="/article/' + obj['articleId'] + '#p' + obj['pId'] + '" target="_blank" title="'+ obj['parentObject'] +'">' + obj['parentObject'] + '</a></td>' +
                '<td><a href="/article/' + obj['articleId'] + '#p' + obj['id'] + '" target="_blank" title="'+ obj['commentContent'] +'">' + obj['commentContent'] + '</a></td>' +
                '<td>' + obj['commentDate'] + '</td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="commentManageDeleteBtn articleDelete am-btn am-btn-danger am-round">删除</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        myCommentManageMessageTable.append(table);
        myCommentManageMessageTable.append($('<div class="my-row" id="page-father">' +
            '<div id="myCommentManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.commentManageDeleteBtn').click(function () {
            var $this = $(this);
            deleteCommentManage = $this.parent().parent().parent().attr("id").substring(1);
            var deleteMyCommentName = $this.parent().parent().parent().children("td").eq(2).find("a").html();
            var deleteMyCommentObject = $this.parent().parent().parent().children("td").eq(1).find("a").html();

            $('#deleteMyCommentContent').html('你确定要删除回复对象为：<span class="am-btn-secondary">' + deleteMyCommentObject +
                '</span><br>且评论内容为：<span class="am-btn-danger">' + deleteMyCommentName +
                '</span>&nbsp;的评论吗？<br>删除了就无法恢复了呦!');
            $('#deleteMyComment').modal('open');
        });


        checkDate('#firstCommentDate');
        checkDate('#lastCommentDate');

        var selectFirstCommentDate = $('#firstCommentDate');
        var selectLastCommentDate = $('#lastCommentDate');
        selectFirstCommentDate.change(function () {
            if (selectFirstCommentDate.val() === null || selectFirstCommentDate.val() === "" || typeof(selectFirstCommentDate.val()) === "undefined" ) {
                selectLastCommentDate.attr('disabled', true);
            } else {
                selectLastCommentDate.attr('disabled', false);
            }
            selectLastCommentDate.attr("value","");
        });
        selectLastCommentDate.change(function () {
            var first = selectFirstCommentDate.val();
            var last = selectLastCommentDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastCommentDate.attr("value","");
            }
        });


        $('#commentRetrieve').click(function () {
            commentArticleTitle = $('#commentArticleTitle').val();
            commentToContent = $('#commentToContent').val();
            firstCommentDate = selectFirstCommentDate.val();
            lastCommentDate = selectLastCommentDate.val();
            getMyCommentManagement(1);
        });

        $('#commentReset').click(function () {
            commentArticleTitle = "";
            commentToContent = "";
            firstCommentDate = "";
            lastCommentDate = "";

            getMyCommentManagement(1);
        });

        $('#commentArticleTitle').val(commentArticleTitle);
        $('#commentToContent').val(commentToContent);
        selectFirstCommentDate.attr("value",firstCommentDate);
        selectLastCommentDate.attr("value",lastCommentDate);

        if (selectFirstCommentDate.val() === null || selectFirstCommentDate.val() === "" || typeof(selectFirstCommentDate.val()) === "undefined" ) {
            selectLastCommentDate.attr('disabled', true);
        }
    }

    // 获得我的评论管理
    function getMyCommentManagement(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/findAllCommentByUser',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                articleTitle: commentArticleTitle,
                commentContent: commentToContent,
                firstDate: firstCommentDate,
                lastDate: lastCommentDate,
                searchUsername: searchUsername
            },
            success: function (data) {
                putInMyCommentManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#myCommentManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getMyCommentManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取我的评论信息失败")
            }
        });
    }

    // 删除我的评论
    $('.sureMyCommentDeleteBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/deleteCommentByUser',
            dataType: 'json',
            data:{
                id: deleteCommentManage
            },
            success: function (data) {
                if (data['status'] === 1006) {
                    dangerNotice("删除评论失败")
                } else {
                    successNotice("删除评论成功");
                    getMyCommentManagement(1);
                }
            },
            error: function () {
                alert("删除评论失败")
            }
        });
    });

    getMyCommentManagement(1);


    // 填充用户评论
    function putInCommentInfo(data, selectCommentMsgRead) {
        var msgContent = $('.msgContent');
        msgContent.empty();

        msgContent.append($(' <div class="msgReadTop">' +
            '未读消息：<span class="msgIsReadNum">' + data['msgIsNotReadNum'] + '</span>' +
            '<a class="msgIsRead">全部标记为已读</a>' +

            '<div style="margin-top: 10px;">' +

            '<select id="select-commentMsgRead" style="margin-left: 0">' +
            '<option class="commentMsgReadOption" value="choose">查看全部</option>' +
            '<option class="commentMsgReadOption" value="1">只看未读</option>' +
            '<option class="commentMsgReadOption" value="0">只看已读</option>' +
            '</select>' +

            '<input class="articleInput" type="text" id="firstCommentMsgDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastCommentMsgDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="commentMsgRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="commentMsgReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        if(data['result'].length === 0){
            msgContent.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));

        } else {

            $.each(data['result'], function (index, obj) {
                var msgRead = $('<div class="msgRead" id="c' + obj['id'] + '"></div>');
                var msgReadSign = $('<span class="msgReadSign"></span>');
                if(obj['isRead'] === 1){
                    msgRead.append(msgReadSign);
                }
                msgRead.append($('<span class="am-badge msgType">评论</span>'));

                if(obj['pId'] === 0){
                    msgRead.append($('<span class="msgHead">' +
                        '<a class="msgPerson" href="/person?personName=' + obj['answerer'] + '" title="' + obj['answerer'] + '" target="_blank">' + obj['answerer'] + '</a>评论了你的博文&nbsp;&nbsp;&nbsp;&nbsp;<a class="msgPerson">' + obj['articleTitle'] + '</a>' +
                        '</span>'));
                    msgRead.append($('<div class="msgTxt">' +
                        '<span><a class="articleTitle" href="/article/' + obj['articleId'] + '#p' + obj['id'] + '" target="_blank">' + obj['commentContent'] + '</a></span>' +
                        '<span class="msgDate">' + obj['commentDate'] + '</span>' +
                        '</div><hr>'));

                } else {
                    msgRead.append($('<span class="msgHead">' +
                        '<a class="msgPerson" href="/person?personName=' + obj['answerer'] + '" title="' + obj['answerer'] + '" target="_blank">' + obj['answerer'] + '</a>回复了你的评论 &nbsp;&nbsp;&nbsp;&nbsp;<a class="msgPerson">' + obj['articleTitle'] + '</a>' +
                        '</span>'));

                    msgRead.append($('<div class="msgTxt">' +
                        '<span>回复&nbsp;<a class="msgPerson">@'+ obj['respondent'] +'</a>：<a class="articleTitle" href="/article/' + obj['articleId'] + '#p' + obj['id'] + '" target="_blank">' + obj['commentContent'] + '</a></span>' +
                        '<span class="msgDate">' + obj['commentDate'] + '</span>' +
                        '</div>' +
                        '<div class="msgTxt">' +
                        '主评论&nbsp;&nbsp;<span style="font-size: 14px; color: #9f9f9f">'+ obj['pRespondent'] +'：' + obj['pComment'] + '</span>' +
                        '</div>' +
                        '<hr>'));
                }
                msgContent.append(msgRead);
            });

            msgContent.append($('<div class="my-row" id="commentPageFather">' +
                '<div id="commentPagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));


        }

        //已读一条消息
        $('.articleTitle').click(function () {
            var parent = $(this).parent().parent().parent();
            var isRead = true;
            var num = $('.msgIsReadNum').html();
            if(parent.find($('.msgReadSign')).length !== 0){
                isRead = false;
            }
            if(isRead === false){
                var id = parent.attr('id').substring(1);
                $.ajax({
                    type: 'GET',
                    url: '/readThisMsg',
                    dataType: 'json',
                    data:{
                        id: id,
                        msgType: 1
                    },
                    success:function (data) {
                        if (data['status'] === 507 || data['status'] === 1) {
                            dangerNotice("已读一条评论失败")
                        }
                    },
                    error:function () {
                        alert("已读一条评论失败")
                    }
                });
                // 去掉未读红点
                parent.find($('.msgReadSign')).removeClass('msgReadSign');
                // 未读消息减1
                $('.msgIsReadNum').html(--num);

                // 去掉左侧栏未读消息
                if(num === 0){
                    $('.commentNotReadNum').remove();
                } else {
                    $('.commentNotReadNum').html(num);
                }

                // 去掉导航栏下拉框未读消息
                var newsNum = $('.newsNum').html();
                --newsNum;
                if(newsNum === 0){
                    $('.newsNum').remove();
                } else {
                    $('.newsNum').html(newsNum);
                }
            }
        });

        //全部标记为已读
        $('.msgIsRead').click(function () {
            var num = $('.msgIsReadNum').html();
            if(num !== 0){
                $.ajax({
                    type: 'GET',
                    url: '/readAllMsg',
                    dataType: 'json',
                    data:{
                        msgType:1
                    },
                    success:function (data) {
                        if(data['status'] === 101){
                            $.get("/toLogin",function(data,status,xhr){
                                window.location.replace("/login");
                            });
                        } else {
                            $('.msgIsReadNum').html(0);
                            $('.msgContent').find($('.msgReadSign')).removeClass('msgReadSign');

                            $('.commentNotReadNum').remove();
                            var allNum = $('.newsNum').html();
                            if(allNum === num){
                                $('.newsNum').remove();
                            } else {
                                $('.newsNum').html(allNum - num);
                            }
                        }
                    },
                    error:function () {
                        alert("已读全部评论失败")
                    }
                });
            }
        });


        checkDate('#firstCommentMsgDate');
        checkDate('#lastCommentMsgDate');

        var commentMsgReadToSelect = $('#select-commentMsgRead');
        var commentMsgReadOption = commentMsgReadToSelect.find('.commentMsgReadOption');

        if (selectCommentMsgRead === "choose") {
            commentMsgReadOption[0].selected = true;

        } else if (selectCommentMsgRead === "1") {
            commentMsgReadOption[1].selected = true;

        } else if (selectCommentMsgRead === "0") {
            commentMsgReadOption[2].selected = true;

        } else {
            commentMsgReadOption[0].selected = true;

        }

        var selectFirstCommentMsgDate = $('#firstCommentMsgDate');
        var selectLastCommentMsgDate = $('#lastCommentMsgDate');
        selectFirstCommentMsgDate.change(function () {
            if (selectFirstCommentMsgDate.val() === null || selectFirstCommentMsgDate.val() === "" || typeof(selectFirstCommentMsgDate.val()) === "undefined" ) {
                selectLastCommentMsgDate.attr('disabled', true);
            } else {
                selectLastCommentMsgDate.attr('disabled', false);
            }
            selectLastCommentMsgDate.attr("value","");
        });

        selectLastCommentMsgDate.change(function () {
            var first = selectFirstCommentMsgDate.val();
            var last = selectLastCommentMsgDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastCommentMsgDate.attr("value","");
            }
        });


        $('#commentMsgRetrieve').click(function () {
            commentMsgRead = commentMsgReadToSelect.val();
            firstCommentMsgDate = selectFirstCommentMsgDate.val();
            lastCommentMsgDate = selectLastCommentMsgDate.val();
            getUserComment(1);
        });

        $('#commentMsgReset').click(function () {
            firstCommentMsgDate = "";
            lastCommentMsgDate = "";
            commentMsgRead = "choose";
            getUserComment(1);
        });

        commentMsgReadToSelect.val(commentMsgRead);
        selectFirstCommentMsgDate.attr("value",firstCommentMsgDate);
        selectLastCommentMsgDate.attr("value",lastCommentMsgDate);

        if (selectFirstCommentMsgDate.val() === null || selectFirstCommentMsgDate.val() === "" || typeof(selectFirstCommentMsgDate.val()) === "undefined" ) {
            selectLastCommentMsgDate.attr('disabled', true);
        }

    }

    //获得评论
    function getUserComment(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getUserComment',
            dataType: 'json',
            data:{
                rows: "8",
                pageNum: currentPage,
                commentMsgRead: commentMsgRead,
                firstDate: firstCommentMsgDate,
                lastDate: lastCommentMsgDate
            },
            success:function (data) {
                if(data['status'] === 101){
                    $.get("/toLogin",function(data,status,xhr){
                        window.location.replace("/login");
                    });
                }
                putInCommentInfo(data['data'], commentMsgRead);
                scrollTo(0,0);//回到顶部

                //分页
                $("#commentPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getUserComment(currentPage);
                    }
                });
            },
            error:function () {
                alert("获取评论信息失败");
            }
        })
    }

    // 点击评论管理
    $('.commentMessage').click(function () {
        var msgContent = $('.msgContent');
        msgContent.empty();
        getUserComment(1);
    });

    // 填充用户文章点赞
    function putInArticleThumbsUp(data, selectArticleThumbsUpRead) {
        var msgContent = $('.articleByThumbsUpContent');
        msgContent.empty();

        msgContent.append($(' <div class="msgReadTop">' +
            '未读消息：<span class="msgIsReadNum">' + data['msgIsNotReadNum'] + '</span>' +
            '<a class="msgIsRead">全部标记为已读</a>' +

            '<div style="margin-top: 10px;">' +

            '<select id="select-articleThumbsUpRead" style="margin-left: 0">' +
            '<option class="articleThumbsUpReadOption" value="choose">查看全部</option>' +
            '<option class="articleThumbsUpReadOption" value="1">只看未读</option>' +
            '<option class="articleThumbsUpReadOption" value="0">只看已读</option>' +
            '</select>' +

            '<input class="articleInput" type="text" id="firstArticleThumbsUpDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastArticleThumbsUpDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="articleThumbsUpRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="articleThumbsUpReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        if(data['result'].length === 0){
            msgContent.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
        } else {

            $.each(data['result'], function (index, obj) {
                var msgRead = $('<div class="msgRead" id="t' + obj['id'] + '"></div>');
                var msgReadSign = $('<span class="msgReadSign"></span>');
                if(obj['isRead'] === 1){
                    msgRead.append(msgReadSign);
                }
                msgRead.append($('<span class="am-badge msgType">文章点赞</span>'));

                msgRead.append($('<span class="msgHead"><a class="msgPerson" href="/person?personName=' + obj['praisePeople'] + '" title="' + obj['praisePeople'] + '" target="_blank">' + obj['praisePeople'] + '</a>点赞了你的博文</span>'));
                msgRead.append($('<div class="msgTxt">' +
                    '<span><a class="articleTitle" href="/article/' + obj['articleId'] + '" target="_blank">' + obj['articleTitle'] + '</a></span>' +
                    '<span class="msgDate">' + obj['likeDate'] + '</span>' +
                    '</div>' +
                    '<hr>'));

                msgContent.append(msgRead);
            });

            msgContent.append($('<div class="my-row" id="thumbsUpPage">' +
                '<div class="thumbsUpPagination">' +
                '</div>' +
                '</div>'));
        }

        //已读一条消息
        $('.articleTitle').click(function () {
            var parent = $(this).parent().parent().parent();
            var isRead = true;
            var num = $('.msgIsReadNum').html();
            if(parent.find($('.msgReadSign')).length !== 0){
                isRead = false;
            }
            if(isRead === false){
                var id = parent.attr('id').substring(1);
                $.ajax({
                    type: 'GET',
                    url: '/readThisArticleThumbsUp',
                    dataType: 'json',
                    data:{
                        id: id,
                    },
                    success:function (data) {
                        if (data['status'] === 202) {
                            dangerNotice("已读一条文章点赞信息失败")
                        }
                    },
                    error:function () {
                        alert("已读一条文章点赞信息失败")
                    }
                });
                // 去掉未读红点
                parent.find($('.msgReadSign')).removeClass('msgReadSign');
                // 未读消息减1
                $('.msgIsReadNum').html(--num);

                // 去掉左侧栏未读消息
                if(num === 0){
                    $('.articleThumbsUpNum').remove();
                } else {
                    $('.articleThumbsUpNum').html(num);
                }

                // 去掉导航栏下拉框未读消息
                var newsNum = $('.newsNum').html();
                --newsNum;
                if(newsNum === 0){
                    $('.newsNum').remove();
                } else {
                    $('.newsNum').html(newsNum);
                }
            }
        });

        //全部标记为已读
        $('.msgIsRead').click(function () {
            var num = $('.msgIsReadNum').html();
            if(num !== 0){
                $.ajax({
                    type: 'GET',
                    url: '/readAllArticleThumbsUp',
                    dataType: 'json',
                    data:{
                    },
                    success:function (data) {
                        if(data['status'] === 101){
                            $.get("/toLogin",function(data,status,xhr){
                                window.location.replace("/login");
                            });
                        } else if (data['status'] === 202) {
                            dangerNotice("已读全部文章点赞信息失败")
                        } else {
                            $('.msgIsReadNum').html(0);
                            $('.articleByThumbsUpContent').find($('.msgReadSign')).removeClass('msgReadSign');

                            $('.articleThumbsUpNum').remove();
                            var allNum = $('.newsNum').html();
                            if(allNum === num){
                                $('.newsNum').remove();
                            } else {
                                $('.newsNum').html(allNum - num);
                            }
                        }
                    },
                    error:function () {
                        alert("已读全部文章点赞信息失败")
                    }
                });
            }
        });

        checkDate('#firstArticleThumbsUpDate');
        checkDate('#lastArticleThumbsUpDate');

        var articleThumbsUpReadToSelect = $('#select-articleThumbsUpRead');
        var articleThumbsUpReadOption = articleThumbsUpReadToSelect.find('.articleThumbsUpReadOption');

        if (selectArticleThumbsUpRead === "choose") {
            articleThumbsUpReadOption[0].selected = true;

        } else if (selectArticleThumbsUpRead === "1") {
            articleThumbsUpReadOption[1].selected = true;

        } else if (selectArticleThumbsUpRead === "0") {
            articleThumbsUpReadOption[2].selected = true;

        } else {
            articleThumbsUpReadOption[0].selected = true;

        }

        var selectFirstArticleThumbsUpDate = $('#firstArticleThumbsUpDate');
        var selectLastArticleThumbsUpDate = $('#lastArticleThumbsUpDate');
        selectFirstArticleThumbsUpDate.change(function () {
            if (selectFirstArticleThumbsUpDate.val() === null || selectFirstArticleThumbsUpDate.val() === "" || typeof(selectFirstArticleThumbsUpDate.val()) === "undefined" ) {
                selectLastArticleThumbsUpDate.attr('disabled', true);
            } else {
                selectLastArticleThumbsUpDate.attr('disabled', false);
            }
            selectLastArticleThumbsUpDate.attr("value","");
        });

        selectLastArticleThumbsUpDate.change(function () {
            var first = selectFirstArticleThumbsUpDate.val();
            var last = selectLastArticleThumbsUpDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastArticleThumbsUpDate.attr("value","");
            }
        });

        $('#articleThumbsUpRetrieve').click(function () {
            articleThumbsUpRead = articleThumbsUpReadToSelect.val();
            firstArticleThumbsUpDate = selectFirstArticleThumbsUpDate.val();
            lastArticleThumbsUpDate = selectLastArticleThumbsUpDate.val();
            getArticleThumbsUp(1);
        });

        $('#articleThumbsUpReset').click(function () {
            firstArticleThumbsUpDate = "";
            lastArticleThumbsUpDate = "";
            articleThumbsUpRead = "choose";
            getArticleThumbsUp(1);
        });

        articleThumbsUpReadToSelect.val(articleThumbsUpRead);
        selectFirstArticleThumbsUpDate.attr("value",firstArticleThumbsUpDate);
        selectLastArticleThumbsUpDate.attr("value",lastArticleThumbsUpDate);

        if (selectFirstArticleThumbsUpDate.val() === null || selectFirstArticleThumbsUpDate.val() === "" || typeof(selectFirstArticleThumbsUpDate.val()) === "undefined" ) {
            selectLastArticleThumbsUpDate.attr('disabled', true);
        }
    }

    //获得文章点赞信息
    function getArticleThumbsUp(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getArticleThumbsUp',
            dataType: 'json',
            data:{
                rows: "8",
                pageNum: currentPage,
                articleThumbsUpRead: articleThumbsUpRead,
                firstDate: firstArticleThumbsUpDate,
                lastDate: lastArticleThumbsUpDate
            },
            success:function (data) {
                if(data['status'] === 101){
                    $.get("/toLogin",function(data,status,xhr){
                        window.location.replace("/login");
                    });
                }

                putInArticleThumbsUp(data['data'], articleThumbsUpRead);
                scrollTo(0,0);//回到顶部

                //分页
                $(".thumbsUpPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getArticleThumbsUp(currentPage);
                    }
                });
            },
            error:function () {
                alert("获得文章点赞信息失败");
            }
        })
    }

    // 点击文章点赞
    $('.articleByThumbsUp').click(function () {
        var msgContent = $('.articleByThumbsUpContent');
        msgContent.empty();
        getArticleThumbsUp(1);
    });


    // 填充用户评论点赞
    function putInCommentThumbsUp(data, selectCommentThumbsUpRead) {
        var msgContent = $('.commentByThumbsUpContent');
        msgContent.empty();

        msgContent.append($(' <div class="msgReadTop">' +
            '未读消息：<span class="msgIsReadNum">' + data['msgIsNotReadNum'] + '</span>' +
            '<a class="msgIsRead">全部标记为已读</a>' +

            '<div style="margin-top: 10px;">' +

            '<select id="select-commentThumbsUpRead" style="margin-left: 0">' +
            '<option class="commentThumbsUpReadOption" value="choose">查看全部</option>' +
            '<option class="commentThumbsUpReadOption" value="1">只看未读</option>' +
            '<option class="commentThumbsUpReadOption" value="0">只看已读</option>' +
            '</select>' +

            '<input class="articleInput" type="text" id="firstCommentThumbsUpReadDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastCommentThumbsUpReadDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="commentThumbsUpReadRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="commentThumbsUpReadReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        if(data['result'].length === 0){
            msgContent.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
        } else {

            $.each(data['result'], function (index, obj) {
                var msgRead = $('<div class="msgRead" id="u' + obj['id'] + '"></div>');
                var msgReadSign = $('<span class="msgReadSign"></span>');
                if(obj['isRead'] === 1){
                    msgRead.append(msgReadSign);
                }
                msgRead.append($('<span class="am-badge msgType">评论点赞</span>'));

                msgRead.append($('<span class="msgHead">' +
                    '<a class="msgPerson" href="/person?personName=' + obj['praisePeople'] + '" title="' + obj['praisePeople'] + '" target="_blank">' + obj['praisePeople'] + '</a>点赞了你的评论&nbsp;&nbsp;&nbsp;&nbsp;<a class="msgPerson">' + obj['articleTitle'] + '</a>' +
                    '</span>'));
                msgRead.append($('<div class="msgTxt">' +
                    '<span>评论&nbsp;<a class="msgPerson">'+ obj['answerer'] +'</a>：<a class="articleTitle" href="/article/' + obj['articleId'] + '#p' + obj['commentId'] + '" target="_blank">' + obj['commentContent'] + '</a></span>' +
                    '<span class="msgDate">' + obj['likeDate'] + '</span>' +
                    '</div><hr>'));

                msgContent.append(msgRead);
            });
            msgContent.append($('<div class="my-row" id="thumbsUpPage">' +
                '<div class="commentThumbsUpPagination">' +
                '</div>' +
                '</div>'));
        }

        //已读一条消息
        $('.articleTitle').click(function () {
            var parent = $(this).parent().parent().parent();
            var isRead = true;
            var num = $('.msgIsReadNum').html();
            if(parent.find($('.msgReadSign')).length !== 0){
                isRead = false;
            }
            if(isRead === false){
                var id = parent.attr('id').substring(1);
                $.ajax({
                    type: 'GET',
                    url: '/readThisCommentThumbsUp',
                    dataType: 'json',
                    data:{
                        id: id,
                    },
                    success:function (data) {
                        if (data['status'] === 1005) {
                            dangerNotice("已读一条评论点赞信息失败")
                        }
                    },
                    error:function () {
                        alert("已读一条评论点赞信息失败")
                    }
                });
                // 去掉未读红点
                parent.find($('.msgReadSign')).removeClass('msgReadSign');
                // 未读消息减1
                $('.msgIsReadNum').html(--num);

                // 去掉左侧栏未读消息
                if(num === 0){
                    $('.commentThumbsUpNum').remove();
                } else {
                    $('.commentThumbsUpNum').html(num);
                }

                // 去掉导航栏下拉框未读消息
                var newsNum = $('.newsNum').html();
                --newsNum;
                if(newsNum === 0){
                    $('.newsNum').remove();
                } else {
                    $('.newsNum').html(newsNum);
                }
            }
        });

        //全部标记为已读
        $('.msgIsRead').click(function () {
            var num = $('.msgIsReadNum').html();
            if(num !== 0){
                $.ajax({
                    type: 'GET',
                    url: '/readAllCommentThumbsUp',
                    dataType: 'json',
                    data:{
                    },
                    success:function (data) {
                        if(data['status'] === 101){
                            $.get("/toLogin",function(data,status,xhr){
                                window.location.replace("/login");
                            });
                        } else if (data['status'] === 1005) {
                            dangerNotice("已读全部评论点赞信息失败")
                        } else {
                            $('.msgIsReadNum').html(0);
                            $('.commentByThumbsUpContent').find($('.msgReadSign')).removeClass('msgReadSign');

                            $('.commentThumbsUpNum').remove();
                            var allNum = $('.newsNum').html();
                            if(allNum === num){
                                $('.newsNum').remove();
                            } else {
                                $('.newsNum').html(allNum - num);
                            }
                        }
                    },
                    error:function () {
                        alert("已读全部评论点赞信息失败")
                    }
                });
            }
        });


        checkDate('#firstCommentThumbsUpReadDate');
        checkDate('#lastCommentThumbsUpReadDate');

        var commentThumbsUpReadToSelect = $('#select-commentThumbsUpRead');
        var commentThumbsUpReadOption = commentThumbsUpReadToSelect.find('.commentThumbsUpReadOption');

        if (selectCommentThumbsUpRead === "choose") {
            commentThumbsUpReadOption[0].selected = true;

        } else if (selectCommentThumbsUpRead === "1") {
            commentThumbsUpReadOption[1].selected = true;

        } else if (selectCommentThumbsUpRead === "0") {
            commentThumbsUpReadOption[2].selected = true;

        } else {
            commentThumbsUpReadOption[0].selected = true;

        }

        var selectFirstCommentThumbsUpReadDate = $('#firstCommentThumbsUpReadDate');
        var selectLastCommentThumbsUpReadDate = $('#lastCommentThumbsUpReadDate');
        selectFirstCommentThumbsUpReadDate.change(function () {
            if (selectFirstCommentThumbsUpReadDate.val() === null || selectFirstCommentThumbsUpReadDate.val() === "" || typeof(selectFirstCommentThumbsUpReadDate.val()) === "undefined" ) {
                selectLastCommentThumbsUpReadDate.attr('disabled', true);
            } else {
                selectLastCommentThumbsUpReadDate.attr('disabled', false);
            }
            selectLastCommentThumbsUpReadDate.attr("value","");
        });

        selectLastCommentThumbsUpReadDate.change(function () {
            var first = selectFirstCommentThumbsUpReadDate.val();
            var last = selectLastCommentThumbsUpReadDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastCommentThumbsUpReadDate.attr("value","");
            }
        });


        $('#commentThumbsUpReadRetrieve').click(function () {
            commentThumbsUpRead = commentThumbsUpReadToSelect.val();
            firstCommentThumbsUpDate = selectFirstCommentThumbsUpReadDate.val();
            lastCommentThumbsUpDate = selectLastCommentThumbsUpReadDate.val();
            getCommentThumbsUp(1);
        });

        $('#commentThumbsUpReadReset').click(function () {
            firstCommentThumbsUpDate = "";
            lastCommentThumbsUpDate = "";
            commentThumbsUpRead = "choose";
            getCommentThumbsUp(1);
        });

        commentThumbsUpReadToSelect.val(commentThumbsUpRead);
        selectFirstCommentThumbsUpReadDate.attr("value",firstCommentThumbsUpDate);
        selectLastCommentThumbsUpReadDate.attr("value",lastCommentThumbsUpDate);

        if (selectFirstCommentThumbsUpReadDate.val() === null || selectFirstCommentThumbsUpReadDate.val() === "" || typeof(selectFirstCommentThumbsUpReadDate.val()) === "undefined" ) {
            selectLastCommentThumbsUpReadDate.attr('disabled', true);
        }
    }

    //获得用户评论点赞信息
    function getCommentThumbsUp(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getCommentThumbsUp',
            dataType: 'json',
            data:{
                rows: "8",
                pageNum: currentPage,
                commentThumbsUpRead: commentThumbsUpRead,
                firstDate: firstCommentThumbsUpDate,
                lastDate: lastCommentThumbsUpDate
            },
            success:function (data) {
                if(data['status'] === 101){
                    $.get("/toLogin",function(data,status,xhr){
                        window.location.replace("/login");
                    });
                }
                putInCommentThumbsUp(data['data'], commentThumbsUpRead);
                scrollTo(0,0);//回到顶部

                //分页
                $(".commentThumbsUpPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getCommentThumbsUp(currentPage);
                    }
                });
            },
            error:function () {
                alert("获得评论点赞信息失败");
            }
        })
    }

    // 点击评论点赞
    $('.commentByThumbsUp').click(function () {
        var msgContent = $('.commentByThumbsUpContent');
        msgContent.empty();
        getCommentThumbsUp(1);
    });


    // 填充我的关注信息
    function putInMyAttention(data) {
        var myAttentionInfos = $('.myAttentionInfos');
        myAttentionInfos.empty();

        myAttentionInfos.append($('<div class="contentTop">' +
            '我的关注：<span class="categoryNum">' + data['count'] + '</span>' +
            '<button class="am-btn am-btn-default am-round codeButton" style="float: right; margin-left: 10px" id="myAttentionReset"><strong>重置</strong></button>' +
            '<button class="am-btn am-btn-secondary am-round codeButton" style="float: right; margin-left: 10px" id="myAttentionRetrieve"><strong>查询</strong></button>' +
            '<input class="formInput" type="text" id="myAttentionSearch" placeholder="搜索我的关注" style="float: right;"/>' +
            '</div>'));

        if (data['result'].length === 0) {
            myAttentionInfos.append('<div class="noNews">没有搜索到相关内容 ~~~~(>_<)~~~~</div>');
        } else {

            $.each(data['result'], function (index, obj) {
                var attentionPersonalBrief;
                if (obj['attentionPersonalBrief'] === null || obj['attentionPersonalBrief'] === "" || typeof(obj['attentionPersonalBrief']) === "undefined" ) {
                    attentionPersonalBrief = "朋友，不必太纠结于当下，也不必太忧虑未来，当你经历过一些事情的时候，眼前的风景已经和从前不一样了。";
                } else {
                    attentionPersonalBrief = obj['attentionPersonalBrief'];
                }

                var myAttentionInfo = $('<div class="myAttentionInfo" id="x' + obj['userAttentionId'] + '"></div>');

                myAttentionInfo.append($('<div style="min-height: 100px" id="w' + obj['attentionUserId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                    '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 30px; margin-bottom: 5px; margin-top: 15px">' +
                    '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                    '<button class="personAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px; margin-bottom: 5px; margin-left: 50px">' +
                    '<span class="am-icon-times"></span>&nbsp;取消关注' +
                    '</button>' +
                    '<br>' +
                    '<div class="myAttentionDisplay">' +
                    '<span>'+ attentionPersonalBrief +'</span>' +
                    '</div>' +
                    '</div>'));

                myAttentionInfos.append(myAttentionInfo);

            });
            myAttentionInfos.append($('<div class="my-row" id="thumbsUpPage">' +
                '<div class="myAttentionPagination">' +
                '</div>' +
                '</div>'));

        }

        var personAttentionBtn = $('.personAttentionBtn')
        personAttentionBtn.click(function () {
            var attentionId = $(this).parent().parent().attr("id").substring(1);

            $.ajax({
                type: 'GET',
                url: '/deleteUserAttention',
                dataType: 'json',
                data: {
                    attentionId: attentionId
                },
                success: function (data) {
                    if (data['status'] === 101) {
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else {
                        successNotice("取消关注成功")
                        ajaxMyAttention(1);
                    }
                },
                error: function () {
                    alert("取消关注失败")
                }
            });
        });

        $('#myAttentionRetrieve').click(function () {
            myAttentionSearch = $('#myAttentionSearch').val();
            if (myAttentionSearch === "" || myAttentionSearch === null || myAttentionSearch.length === 0) {
                alert("查询内容不能为空")
            } else {
                ajaxMyAttention(1);

            }
        });

        $('#myAttentionReset').click(function () {
            myAttentionSearch = "";
            ajaxMyAttention(1);
        });
    }

    // 获取我的关注信息
    function ajaxMyAttention(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getUserUserAttention',
            dataType: 'json',
            async: false,
            data:{
                inquireName: myAttentionSearch,
                type: 1,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInMyAttention(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $(".myAttentionPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxMyAttention(currentPage);
                    }
                });

                $('#myAttentionSearch').val(myAttentionSearch);

            },
            error: function () {
                alert("获取我的关注信息失败")
            }
        });
    }

    // 点击我的关注信息
    $('.myAttention').click(function () {
        ajaxMyAttention(1);
    });


    // 填充我的粉丝信息
    function putInMyFan(data) {
        var myFanInfos = $('.myFanInfos');
        myFanInfos.empty();

        myFanInfos.append($('<div class="contentTop">' +
            '我的粉丝：<span class="categoryNum">' + data['count'] + '</span>' +
            '<button class="am-btn am-btn-default am-round codeButton" style="float: right; margin-left: 10px" id="myFanReset"><strong>重置</strong></button>' +
            '<button class="am-btn am-btn-secondary am-round codeButton" style="float: right; margin-left: 10px" id="myFanRetrieve"><strong>查询</strong></button>' +
            '<input class="formInput" type="text" id="myFanSearch" placeholder="搜索我的粉丝" style="float: right;"/>' +
            '</div>'));

        if (data['result'].length === 0) {
            myFanInfos.append('<div class="noNews">没有搜索到相关内容 ~~~~(>_<)~~~~</div>');
        } else {

            $.each(data['result'], function (index, obj) {
                var attentionPersonalBrief;
                if (obj['attentionPersonalBrief'] === null || obj['attentionPersonalBrief'] === "" || typeof(obj['attentionPersonalBrief']) === "undefined" ) {
                    attentionPersonalBrief = "朋友，不必太纠结于当下，也不必太忧虑未来，当你经历过一些事情的时候，眼前的风景已经和从前不一样了。";
                } else {
                    attentionPersonalBrief = obj['attentionPersonalBrief'];
                }

                var myAttentionInfo = $('<div class="myAttentionInfo" id="r' + obj['userAttentionId'] + '"></div>');

                if (obj['isAttention'] === true) {
                    myAttentionInfo.append($('<div style="min-height: 100px" id="z' + obj['attentionUserId'] + '">' +
                        '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 30px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                        '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                        '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                        '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                        '<button class="personAttentionBtn am-btn am-btn-secondary am-btn-sm" style="border-radius: 6px; margin-bottom: 5px; margin-left: 50px" disabled>' +
                        '<span class="am-icon-check"></span>&nbsp;已关注' +
                        '</button>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<span>'+ attentionPersonalBrief +'</span>' +
                        '</div>' +
                        '</div>'));
                } else {
                    myAttentionInfo.append($('<div style="min-height: 100px" id="w' + obj['attentionUserId'] + '">' +
                        '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 30px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                        '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                        '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                        '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                        '<button class="personAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px; margin-bottom: 5px; margin-left: 50px">' +
                        '<span class="am-icon-plus"></span>&nbsp;加关注' +
                        '</button>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<span>'+ attentionPersonalBrief +'</span>' +
                        '</div>' +
                        '</div>'));
                }

                myFanInfos.append(myAttentionInfo);

            });
            myFanInfos.append($('<div class="my-row" id="thumbsUpPage">' +
                '<div class="myFanPagination">' +
                '</div>' +
                '</div>'));

        }

        var personAttentionBtn = $('.personAttentionBtn');
        personAttentionBtn.click(function () {
            var attentionUserId = $(this).parent().attr("id").substring(1);
            var $button = $(this);

            $.ajax({
                type: 'GET',
                url: '/insertUserAttention',
                dataType: 'json',
                data: {
                    attentionId: attentionUserId
                },
                success: function (data) {
                    if (data['status'] === 101) {
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else if (data['status'] === 1401) {
                        $button.removeClass("am-btn-default");
                        $button.addClass("am-btn-secondary");
                        $button.attr("disabled", true);
                        $button.html("<span class='am-icon-check'></span>&nbsp;已关注");
                        successNotice("关注成功");
                    } else if (data['status'] === 1402) {
                        dangerNotice("关注用户失败");
                    }
                },
                error: function () {
                    alert("关注用户失败")
                }
            });
        });

        $('#myFanRetrieve').click(function () {
            myFanSearch = $('#myFanSearch').val();
            if (myFanSearch === "" || myFanSearch === null || myFanSearch.length === 0) {
                alert("查询内容不能为空")
            } else {
                ajaxMyFan(1);

            }
        });

        $('#myFanReset').click(function () {
            myFanSearch = "";
            ajaxMyFan(1);
        });
    }

    // 获取我的粉丝信息
    function ajaxMyFan(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getUserUserAttention',
            dataType: 'json',
            async: false,
            data:{
                inquireName: myFanSearch,
                type: 2,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInMyFan(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $(".myFanPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxMyFan(currentPage);
                    }
                });

                $('#myFanSearch').val(myFanSearch);

            },
            error: function () {
                alert("获取我的粉丝信息失败")
            }
        });
    }

    // 点击我的粉丝信息
    $('.myFan').click(function () {
        ajaxMyFan(1);
    });


});

//更改头像
function imgChange(e) {
    var dom =$("input[id^='imgTest']")[0];
    var reader = new FileReader();
    reader.onload = (function (file) {
        return function (e) {
            $.ajax({
                type:'POST',
                url:'/uploadHead',
                dataType:'json',
                data:{
                    avatarImgUrl:this.result
                },
                success:function (data) {
                    if(data['status'] === 101){
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else {
                        if(data['status'] === 0){
                            $('#headPortrait').attr("src", data['data']);
                            successNotice("更改头像成功");
                        } else {
                            dangerNotice("更改头像失败")
                        }
                    }

                },
                error:function () {
                    alert("更换头像请求失败")
                }
            });
        };
    })(e.target.files[0]);
    reader.readAsDataURL(e.target.files[0]);
}
