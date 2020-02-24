$(function () {
    var deleteArticleId="";
    var deleteCommentId="";
    var articleCategoryId = "";
    var articleCategoryName = "";
    var articleTagName = "";
    var userLogId = "";
    var statusCode = "";

    var category = "choose";
    var categories;
    var firstPublicDate = "";
    var lastPublicDate = "";
    var publishArticleTitle = "";
    var articleType = "choose";
    var articleDraft = "choose";

    var commentArticleTitle = "";
    var commentToContent = "";
    var firstCommentDate = "";
    var lastCommentDate = "";
    var searchUsername = "";

    var categorySearch = "";
    var firstCategoryCreateDate = "";
    var lastCategoryCreateDate = "";

    var tagSearch = "";
    var firstTagCreateDate = "";
    var lastTagCreateDate = "";

    var phoneSearch = "";
    var usernameSearch = "";
    var genderSearch = "choose";
    var firstLoginDate = "";
    var lastLoginDate = "";

    var loginLogUsername = "";
    var loginLogType = "";
    var firstLoginLogDate = "";
    var lastLoginLogDate = "";

    var operatingLogUsername = "";
    var operatingLogType = "";
    var firstOperatingLogDate = "";
    var lastOperatingLogDate = "";

    var firstFeedBackDate = "";
    var lastFeedBackDate = "";
    var feedBackRead = "choose";



    $('.superAdminList .superAdminClick').click(function () {
        var flag = $(this).attr('class').substring(16);
        $('#statistics,#articleManagement,#commentMessage,#articleCategories,#articleTags,#userManagement,#userLoginMore,' +
            '#userOperating,#returnCodeHelpPage,#databaseManagement,#interfaceManagement,#userFeedback').css("display","none");
        $("#" + flag).css("display","block");
    });

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

    // 获取统计信息
    function getStatisticsInfo() {
        $.ajax({
            type: 'GET',
            url: '/getStatisticsInfo',
            dataType: 'json',
            data:{
            },
            success: function (data) {
                $('.allVisitor').html(data['data']['allVisitor']);
                $('.yesterdayVisitor').html(data['data']['yesterdayVisitor']);
                $('.visitorVolume').html(data['data']['visitorVolume']);
                $('.allUser').html(data['data']['allUser']);
                $('.articleNum').html(data['data']['articleNum']);
                $('.tagsNum').html(data['data']['tagsNum']);
                $('.categoriesNum').html(data['data']['categoriesNum']);
                $('.commentNum').html(data['data']['commentNum']);

                chartToVisitWeekDay(data['data']);
            },
            error: function () {
                alert("获取仪表盘信息失败");
            }
        });
    }
    //获得仪表盘信息
    getStatisticsInfo();

    function chartToVisitWeekDay(obj) {

        var DAYS = (function(){
            // 7天
            var arr = [];
            var newdate = new Date();//当前日期
            var year = newdate.getFullYear();//当前年份
            var month = newdate.getMonth()+1;//当前月份
            var now = newdate.getTime();//今天时间戳
            var nowDay = newdate.getDate();//今天是一个月的第几天(1-32)
            var oneDayTime = 60*60*24*1000;
            for(var i=1; i<=7; i++){
                var d = new Date(now - i*oneDayTime);
                var dd = d.getDate();
                if((nowDay -i) < 1) {
                    month = d.getMonth() + 1;
                }
                arr.push(year+'-'+month+'-'+(dd < 10 ? '0' + dd : dd + ''));
            }
            return arr.reverse();
        })();

        var DATA = (function () {
            var arr = [];
            var newdate = new Date();//当前日期
            var now = newdate.getTime();//今天时间戳
            var oneDayTime = 60*60*24*1000;

            for (var i=1; i<=7; i++) {
                var d = new Date(now - i * oneDayTime);
                var day = d.getDay();
                switch (day) {
                    case 0 : arr.push(obj['SUNDAY']);
                        break;
                    case 1 : arr.push(obj['MONDAY']);
                        break;
                    case 2 : arr.push(obj['TUESDAY']);
                        break;
                    case 3 : arr.push(obj['WEDNESDAY']);
                        break;
                    case 4 : arr.push(obj['THURSDAY']);
                        break;
                    case 5 : arr.push(obj['FRIDAY']);
                        break;
                    case 6 : arr.push(obj['SATURDAY']);
                        break;
                }
                //alert(arr)
            }
            return arr.reverse();
        })();

        var ctx = document.getElementById("chartToVisitWeekDay").getContext("2d");
        var data = {
            labels: DAYS,
            datasets: [
                {
                    label: "访问量",
                    backgroundColor: "rgba(0, 0, 0, 0.1)",//线条填充色
                    pointBackgroundColor:"rgba(255,48,48,0.2)",//定点填充色
                    data: DATA
                }
            ]
        };
        var options = {
            responsive: true,
            title: {
                display: true,
                text: '最近7天内网站访问量一览表'
            },
            scaleFontStyle: "normal",
        };

        new Chart(ctx,{
            type: 'line',
            data: data,
            options: options
        });
    }

    // 设置当前时间
    function localTime(nowDay) {
        var nowTime = "";
        nowTime += nowDay.getFullYear() + "-";

        if ((nowDay.getMonth() + 1).toString().length === 1) {
            nowTime += "0" + (nowDay.getMonth() + 1) + "-";
        } else {
            nowTime += (nowDay.getMonth() + 1) + "-";
        }

        if (nowDay.getDate().toString().length === 1) {
            nowTime += "0" + nowDay.getDate() + " ";
        } else {
            nowTime += nowDay.getDate() + " ";
        }

        if (nowDay.getHours().toString().length === 1) {
            nowTime += "0" + nowDay.getHours() + ":";
        } else {
            nowTime += nowDay.getHours() + ":";
        }


        if (nowDay.getMinutes().toString().length === 1) {
            nowTime += "0" + nowDay.getMinutes() + ":";
        } else {
            nowTime += nowDay.getMinutes() + ":";
        }

        if (nowDay.getSeconds().toString().length === 1) {
            nowTime += "0" + nowDay.getSeconds() + " ";
        } else {
            nowTime += nowDay.getSeconds() + " ";
        }

        var day = nowDay.getDay();
        switch (day) {
            case 0 : nowTime += "星期日";
                break;
            case 1 : nowTime += "星期一";
                break;
            case 2 : nowTime += "星期二";
                break;
            case 3 : nowTime += "星期三";
                break;
            case 4 : nowTime += "星期四";
                break;
            case 5 : nowTime += "星期五";
                break;
            case 6 : nowTime += "星期六";
                break;
        }

        $('#today').html(nowTime);
    }

    setInterval(function () {
        localTime(new Date());
    }, 1000);


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

    // 填充用户所有文章信息
    function putInArticleManagement(data, selectCategory, categories, selectArticleType, selectArticleDraft) {

        var articleManagementTable = $('.articleManagementTable');
        articleManagementTable.empty();

        articleManagementTable.append($('<div class="contentTop">' +
            '目前用户已写的文章总数：<span class="categoryNum">' + data['total'] + '</span>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="publishArticleTitle" placeholder="请输入文章标题"/>' +

            '<select id="select-articleType">' +
            '<option class="articleTypeOption" value="choose">文章类型</option>' +
            '<option class="articleTypeOption" value="原创">原创</option>' +
            '<option class="articleTypeOption" value="转载">转载</option>' +
            '</select>' +

            '<select id="select-categories">' +
            '</select>' +

            '<select id="select-articleDraft">' +
            '<option class="articleDraftOption" value="choose">文章状态</option>' +
            '<option class="articleDraftOption" value="1">已发布</option>' +
            '<option class="articleDraftOption" value="0">草稿</option>' +
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
            '<th style="width: 300px">文章标题</th><th style="width: 180px">发布时间</th><th style="width: 150px">文章作者</th><th style="width: 120px">文章类型</th>' +
            '<th style="width: 200px">文章分类</th><th style="width: 250px">文章标签</th><th style="width: 80px">喜欢数</th>' +
            '<th style="width: 80px">收藏数</th><th style="width: 80px">浏览量</th><th style="width: 100px">文章状态</th><th style="width: 100px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            if (obj['draft'] === 1) {

                tables.append('<tr id="a' + obj['articleId'] + '">' +
                    '<td><a href="article/' + obj['articleId'] + '" target="_blank" title="'+ obj['articleTitle'] +'">' + obj['articleTitle'] + '</a></td>' +
                    '<td>' + obj['publishDate'] + '</td>' +
                    '<td>' + obj['author'] + '</td>' +
                    '<td>' + obj['articleType'] + '</td>' +
                    '<td>' + obj['articleCategories'] + '</td>' +
                    '<td>' + obj['articleTags'] + '</td>' +
                    '<td><span class="am-badge am-badge-success am-round">' + obj['likes'] + '</span></td>' +
                    '<td><span class="am-badge am-badge-success am-round">' + obj['favorites'] + '</span></td>' +
                    '<td><span class="am-badge am-badge-success am-round">' + obj['visitNum'] + '</span></td>' +
                    '<td><span class="am-badge am-badge-secondary am-round" style="font-size: 15px">已发布</span></td>' +
                    '<td>' +
                    '<div class="am-dropdown" data-am-dropdown>' +
                    '<button class="articleDeleteBtn articleDelete am-btn am-btn-danger am-round">删除</button>' +
                    '</div>' +
                    '</td>' +
                    '</tr>');
            } else {

                tables.append('<tr id="a' + obj['articleId'] + '">' +
                    '<td><a>' + obj['articleTitle'] + '</a></td>' +
                    '<td>文章尚未发布</td>' +
                    '<td>' + obj['author'] + '</td>' +
                    '<td>无</td>' +
                    '<td>无</td>' +
                    '<td>无</td>' +
                    '<td><span class="am-badge am-badge-success am-round">0</span></td>' +
                    '<td><span class="am-badge am-badge-success am-round">0</span></td>' +
                    '<td><span class="am-badge am-badge-success am-round">0</span></td>' +
                    '<td><span class="am-badge am-badge-warning am-round" style="font-size: 15px">草稿</span></td>' +
                    '<td>' +
                    '<div class="am-dropdown" data-am-dropdown>' +
                    '<button class="articleDeleteBtn articleDelete am-btn am-btn-danger am-round" disabled>删除</button>' +
                    '</div>' +
                    '</td>' +
                    '</tr>');
            }

        });

        table.append(tables);
        articleManagementTable.append(table);
        articleManagementTable.append($('<div class="my-row" id="page-father">' +
            '<div id="articleManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.articleDeleteBtn').click(function () {
            var $this = $(this);
            deleteArticleId = $this.parent().parent().parent().attr("id").substring(1);
            var deletePublicArticleName = $this.parent().parent().parent().children("td").eq(0).find("a").html();

            $('#articleDeleteContent').html('你确定要删除文章标题为：<span class="am-btn-danger">' + deletePublicArticleName +'</span>&nbsp;的文章吗？' +
                '<br>删除了就无法恢复了呦!<br>管理员请慎重决定哦！');
            $('#articleDelete').modal('open');
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

        var articleDraftToSelect = $('#select-articleDraft');
        var articleDraftOption = articleDraftToSelect.find('.articleDraftOption');
        if (selectArticleDraft === "choose") {
            articleDraftOption[0].selected = true;

        } else if (selectArticleDraft === "1") {
            articleDraftOption[1].selected = true;

        } else if (selectArticleDraft === "0") {
            articleDraftOption[2].selected = true;

        } else {
            articleDraftOption[0].selected = true;

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
            articleDraft = articleDraftToSelect.val();

            getArticleManagement(1);
        });

        $('#publicArticleReset').click(function () {
            firstPublicDate = "";
            lastPublicDate = "";
            publishArticleTitle = "";
            articleType = "choose";
            category = "choose";
            articleDraft = "choose";

            getArticleManagement(1);
        });

        $('#publishArticleTitle').val(publishArticleTitle);
        articleTypeToSelect.val(articleType);
        selectCategories.val(category);
        selectFirstPublicDate.attr("value",firstPublicDate);
        selectLastPublicDate.attr("value",lastPublicDate);
        articleDraftToSelect.val(articleDraft);

        if (selectFirstPublicDate.val() === null || selectFirstPublicDate.val() === "" || typeof(selectFirstPublicDate.val()) === "undefined" ) {
            selectLastPublicDate.attr('disabled', true);
        }

    }

    // 获得文章管理
    function getArticleManagement(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getArticleManagement',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                articleTitle: publishArticleTitle,
                articleType: articleType,
                articleCategory: category,
                firstDate: firstPublicDate,
                lastDate: lastPublicDate,
                draft: articleDraft
            },
            success: function (data) {
                putInArticleManagement(data['data'], category, categories, articleType, articleDraft);
                scrollTo(0,0);//回到顶部

                //分页
                $("#articleManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getArticleManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取文章信息失败")
            }
        });
    }

    // 删除文章
    $('.sureArticleDeleteBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/deleteArticle',
            dataType: 'json',
            data:{
                id: deleteArticleId
            },
            success: function (data) {
                if (data['status'] === 201) {
                    dangerNotice("删除文章失败")
                } else {
                    successNotice("删除文章成功");
                    getArticleManagement(1);
                }
            },
            error: function () {
                alert("删除文章失败")
            }
        });
    });

    getArticleManagement(1);


    // 填充评论管理
    function putInCommentManagement(data) {

        var commentMessageTable = $('.commentMessageTable');
        commentMessageTable.empty();

        commentMessageTable.append($('<div class="contentTop">' +
            '目前用户的评论总数：<span class="categoryNum">' + data['total'] + '</span>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="commentArticleTitle" placeholder="请输入文章标题"/>' +

            '<input class="articleInput" type="text" id="commentToContent" placeholder="请输入评论内容"/>' +

            '<input class="articleInput" type="text" id="searchUsername" placeholder="请输入评论者"/>' +

            '<input class="articleInput" type="text" id="firstCommentDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastCommentDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="commentRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="commentReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 250px">文章标题</th><th style="width: 255px">回复对象（主评论或文章）</th>' +
            '<th style="width: 170px">评论者</th><th style="width: 170px">被回复者</th>' +
            '<th style="width: 250px">评论内容</th><th style="width: 250px">评论日期</th>' +
            '<th style="width: 100px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="m' + obj['id'] + '">' +
                '<td><a href="/article/' + obj['articleId'] +'" target="_blank" title="'+ obj['articleTitle'] +'">' + obj['articleTitle'] + '</a></td>' +
                '<td><a href="/article/' + obj['articleId'] + '#p' + obj['pId'] + '" target="_blank" title="'+ obj['parentObject'] +'">' + obj['parentObject'] + '</a></td>' +
                '<td>' + obj['answerer'] + '</td>' +
                '<td>' + obj['respondent'] + '</td>' +
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
        commentMessageTable.append(table);
        commentMessageTable.append($('<div class="my-row" id="page-father">' +
            '<div id="commentManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.commentManageDeleteBtn').click(function () {
            var $this = $(this);
            deleteCommentId = $this.parent().parent().parent().attr("id").substring(1);
            var deleteCommentName = $this.parent().parent().parent().children("td").eq(4).find("a").html();
            var deleteCommentObject = $this.parent().parent().parent().children("td").eq(1).find("a").html();

            $('#deleteCommentContent').html('你确定要删除回复对象为：<span class="am-btn-secondary">' + deleteCommentObject +
                '</span><br>且评论内容为：<span class="am-btn-danger">' + deleteCommentName +
                '</span>&nbsp;的评论吗？<br>删除了就无法恢复了呦!<br>管理员请慎重决定哦！');
            $('#deleteComment').modal('open');
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
            searchUsername = $('#searchUsername').val();
            firstCommentDate = selectFirstCommentDate.val();
            lastCommentDate = selectLastCommentDate.val();

            getCommentManagement(1);
        });

        $('#commentReset').click(function () {
            commentArticleTitle = "";
            commentToContent = "";
            searchUsername = "";
            firstCommentDate = "";
            lastCommentDate = "";

            getCommentManagement(1);
        });

        $('#commentArticleTitle').val(commentArticleTitle);
        $('#commentToContent').val(commentToContent);
        $('#searchUsername').val(searchUsername);
        selectFirstCommentDate.attr("value",firstCommentDate);
        selectLastCommentDate.attr("value",lastCommentDate);

        if (selectFirstCommentDate.val() === null || selectFirstCommentDate.val() === "" || typeof(selectFirstCommentDate.val()) === "undefined" ) {
            selectLastCommentDate.attr('disabled', true);
        }
    }

    // 获得评论管理
    function getCommentManagement(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/findAllComment',
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
                putInCommentManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#commentManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getCommentManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取评论信息失败")
            }
        });
    }

    // 删除我的评论
    $('.sureCommentDeleteBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/deleteOneCommentById',

            dataType: 'json',
            data:{
                id: deleteCommentId
            },
            success: function (data) {
                if (data['status'] === 1006) {
                    dangerNotice("删除评论失败")
                } else {
                    successNotice("删除评论成功");
                    getCommentManagement(1);
                }
            },
            error: function () {
                alert("删除评论失败")
            }
        });
    });

    getCommentManagement(1);


    // 填充文章分类管理
    function putInArticleCategoriesManagement(data) {

        var articleCategoriesTable = $('.articleCategoriesTable');
        articleCategoriesTable.empty();

        articleCategoriesTable.append($('<div class="contentTop">' +
            '目前文章的分类总数：<span class="categoryNum">' + data['total'] + '</span>' +
            '<div class="updateCategory">' +
            '<a class="addCategory"><i class="am-icon-plus-square"></i> 添加分类</a>' +
            '</div>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="categorySearch" placeholder="请输入分类名称"/>' +

            '<input class="articleInput" type="text" id="firstCategoryCreateDate" placeholder="初始日期(创建日期)" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastCategoryCreateDate" placeholder="结束日期(创建日期)" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="categoryRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="categoryReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 200px">分类名称</th><th style="width: 300px">分类描述</th><th style="width: 200px">创建日期</th>' +
            '<th style="width: 200px">文章总数</th><th style="width: 200px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="c' + obj['id'] + '">' +
                '<td><a href="/categories?categoryName='+ obj['categoryName'] +'" target="_blank" title="'+ obj['categoryName'] +'">' + obj['categoryName'] + '</a></td>' +
                '<td>' + obj['description'] + '</td>' +
                '<td>' + obj['createDate'] + '</td>' +
                '<td><span class="am-badge am-badge-success am-round">' + obj['articleNum'] + '</span></td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="articleCategoriesManageUpdateBtn articleDelete am-btn am-btn-secondary am-round" style="margin-right: 10px;">编辑</button>' +
                '<button class="articleCategoriesManageDeleteBtn articleDelete am-btn am-btn-danger am-round">删除</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        articleCategoriesTable.append(table);
        articleCategoriesTable.append($('<div class="my-row" id="page-father">' +
            '<div id="articleCategoriesManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.addCategory').click(function () {
            $('#addCategory').modal('open');
        });

        $('.articleCategoriesManageUpdateBtn').click(function () {
            var $this = $(this);
            articleCategoryId = $this.parent().parent().parent().attr("id").substring(1);

            $.ajax({
                type: 'GET',
                url: '/findOneCategory',
                dataType: 'json',
                async: false,
                data: {
                    id: articleCategoryId
                },
                success: function (data) {
                    $('#updateCategoryName').val(data['data'].categoryName);
                    $('#updateCategoryDescription').val(data['data'].description);
                },
                error: function () {
                    alert("获取要修改的文章分类信息失败！");
                }
            });

            $('#updateCategory').modal('open');

        });


        $('.articleCategoriesManageDeleteBtn').click(function () {
            var $this = $(this);
            articleCategoryId = $this.parent().parent().parent().attr("id").substring(1);
            articleCategoryName = $this.parent().parent().parent().children("td").eq(0).find("a").html();

            $('#deleteCategoryContent').html('你确定要删除分类名为：<span class="am-btn-danger">' + articleCategoryName +
                '</span>&nbsp;的分类吗？<br>删除了就无法恢复了呦!<br>管理员请慎重决定哦！');
            $('#deleteCategory').modal('open');
        });


        checkDate('#firstCategoryCreateDate');
        checkDate('#lastCategoryCreateDate');

        var selectFirstCategoryCreateDate = $('#firstCategoryCreateDate');
        var selectLastCategoryCreateDate = $('#lastCategoryCreateDate');
        selectFirstCategoryCreateDate.change(function () {
            if (selectFirstCategoryCreateDate.val() === null || selectFirstCategoryCreateDate.val() === "" || typeof(selectFirstCategoryCreateDate.val()) === "undefined" ) {
                selectLastCategoryCreateDate.attr('disabled', true);
            } else {
                selectLastCategoryCreateDate.attr('disabled', false);
            }
            selectLastCategoryCreateDate.attr("value","");
        });
        selectLastCategoryCreateDate.change(function () {
            var first = selectFirstCategoryCreateDate.val();
            var last = selectLastCategoryCreateDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastCategoryCreateDate.attr("value","");
            }
        });


        $('#categoryRetrieve').click(function () {
            categorySearch = $('#categorySearch').val();
            firstCategoryCreateDate = selectFirstCategoryCreateDate.val();
            lastCategoryCreateDate = selectLastCategoryCreateDate.val();

            getArticleCategoriesManagement(1);
        });

        $('#categoryReset').click(function () {
            categorySearch = "";
            firstCategoryCreateDate = "";
            lastCategoryCreateDate = "";

            getArticleCategoriesManagement(1);
        });

        $('#categorySearch').val(categorySearch);
        selectFirstCategoryCreateDate.attr("value",firstCategoryCreateDate);
        selectLastCategoryCreateDate.attr("value",lastCategoryCreateDate);

        if (selectFirstCategoryCreateDate.val() === null || selectFirstCategoryCreateDate.val() === "" || typeof(selectFirstCategoryCreateDate.val()) === "undefined" ) {
            selectLastCategoryCreateDate.attr('disabled', true);
        }

    }

    // 获得文章分类管理
    function getArticleCategoriesManagement(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/getArticleCategories',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                categorySearch: categorySearch,
                firstDate: firstCategoryCreateDate,
                lastDate: lastCategoryCreateDate
            },
            success: function (data) {
                putInArticleCategoriesManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#articleCategoriesManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getArticleCategoriesManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取文章分类信息失败")
            }
        });
    }

    // 添加文章分类
    $('.sureAddCategoryBtn').click(function () {
        var addCategoryName = $('#addCategoryName').val();
        var addCategoryDescription = $('#addCategoryDescription').val();

        if (addCategoryDescription === '' || addCategoryName.length === 0) {
            addCategoryDescription = null;
        }

        if (addCategoryName === '' || addCategoryName === null || typeof(addCategoryName) === "undefined" || addCategoryName.length === 0) {
            dangerNotice("分类名称不能为空");
        } else {
            $.ajax({
                type: 'POST',
                url: '/updateCategory',
                dataType: 'json',
                async: false,
                data: {
                    categoryName: addCategoryName,
                    description: addCategoryDescription,
                    type: 1
                },
                success: function (data) {
                    if (data['status'] === 406) {
                        $('#addCategoryName').val('');
                        $('#addCategoryDescription').val('');
                        dangerNotice("分类名已存在，添加文章分类失败");
                    } else if (data['status'] === 401) {
                        $('#addCategoryName').val('');
                        $('#addCategoryDescription').val('');
                        successNotice("添加文章分类成功");
                        getArticleCategoriesManagement(1);
                    }
                },
                error: function () {
                    alert("添加文章分类失败")
                }
            })
        }
    });

    // 修改文章分类
    $('.sureUpdateCategoryBtn').click(function () {
        var updateCategoryName = $('#updateCategoryName').val();
        var updateCategoryDescription = $('#updateCategoryDescription').val();

        $.ajax({
            type: 'POST',
            url: '/updateCategory',
            dataType: 'json',
            async: false,
            data:{
                categoryName: updateCategoryName,
                description: updateCategoryDescription,
                type: 3
            },
            success: function (data) {
                if (data['status'] === 404) {
                    dangerNotice("分类不存在，修改文章分类失败");
                } else if (data['status'] === 402) {
                    successNotice("修改文章分类成功");
                    getArticleCategoriesManagement(1);
                }
            },
            error: function () {
                alert("修改文章分类失败")
            }
        });
    });

    // 删除文章分类
    $('.sureDeleteCategoryBtn').click(function () {
        $.ajax({
            type: 'POST',
            url: '/updateCategory',
            dataType: 'json',
            async: false,
            data:{
                categoryName: articleCategoryName,
                type: 2
            },
            success: function (data) {
                if (data['status'] === 405) {
                    dangerNotice("分类下存在文章，删除文章分类失败");
                } else if (data['status'] === 404) {
                    dangerNotice("分类不存在，删除文章分类失败");
                } else if (data['status'] === 403) {
                    successNotice("删除文章分类成功");
                    getArticleCategoriesManagement(1);
                }
            },
            error: function () {
                alert("删除文章分类失败")
            }
        });
    });

    getArticleCategoriesManagement(1);



    // 填充文章标签管理
    function putInArticleTagsManagement(data) {

        var articleTagsTable = $('.articleTagsTable');
        articleTagsTable.empty();

        articleTagsTable.append($('<div class="contentTop">' +
            '目前文章的标签总数：<span class="categoryNum">' + data['tagsCount'] + '</span>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="tagSearch" placeholder="请输入标签名称"/>' +

            '<input class="articleInput" type="text" id="firstTagCreateDate" placeholder="初始日期(创建日期)" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastTagCreateDate" placeholder="结束日期(创建日期)" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="tagRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="tagReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 300px">标签名称</th><th style="width: 300px">创建日期</th>' +
            '<th style="width: 300px">文章总数</th><th style="width: 300px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="t' + obj['id'] + '">' +
                '<td><a href="/tags?tag='+ obj['tagName'] +'" target="_blank" title="'+ obj['tagName'] +'">' + obj['tagName'] + '</a></td>' +
                '<td>' + obj['createDate'] + '</td>' +
                '<td><span class="am-badge am-badge-success am-round">' + obj['tagArticleCountNum'] + '</span></td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="articleTagsManageDeleteBtn articleDelete am-btn am-btn-danger am-round">删除</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        articleTagsTable.append(table);
        articleTagsTable.append($('<div class="my-row" id="page-father">' +
            '<div id="articleTagsManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.articleTagsManageDeleteBtn').click(function () {
            var $this = $(this);
            articleTagName = $this.parent().parent().parent().children("td").eq(0).find("a").html();

            $('#deleteTagContent').html('你确定要删除标签名为：<span class="am-btn-danger">' + articleTagName +
                '</span>&nbsp;的标签吗？<br>删除了就无法恢复了呦!<br>管理员请慎重决定哦！');
            $('#deleteTag').modal('open');
        });

        checkDate('#firstTagCreateDate');
        checkDate('#lastTagCreateDate');

        var selectFirstTagCreateDate = $('#firstTagCreateDate');
        var selectLastTagCreateDate = $('#lastTagCreateDate');
        selectFirstTagCreateDate.change(function () {
            if (selectFirstTagCreateDate.val() === null || selectFirstTagCreateDate.val() === "" || typeof(selectFirstTagCreateDate.val()) === "undefined" ) {
                selectLastTagCreateDate.attr('disabled', true);
            } else {
                selectLastTagCreateDate.attr('disabled', false);
            }
            selectLastTagCreateDate.attr("value","");
        });
        selectLastTagCreateDate.change(function () {
            var first = selectFirstTagCreateDate.val();
            var last = selectLastTagCreateDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastTagCreateDate.attr("value","");
            }
        });


        $('#tagRetrieve').click(function () {
            tagSearch = $('#tagSearch').val();
            firstTagCreateDate = selectFirstTagCreateDate.val();
            lastTagCreateDate = selectLastTagCreateDate.val();

            getArticleTagsManagement(1);
        });

        $('#tagReset').click(function () {
            tagSearch = "";
            firstTagCreateDate = "";
            lastTagCreateDate = "";

            getArticleTagsManagement(1);
        });

        $('#tagSearch').val(tagSearch);
        selectFirstTagCreateDate.attr("value",firstTagCreateDate);
        selectLastTagCreateDate.attr("value",lastTagCreateDate);

        if (selectFirstTagCreateDate.val() === null || selectFirstTagCreateDate.val() === "" || typeof(selectFirstTagCreateDate.val()) === "undefined" ) {
            selectLastTagCreateDate.attr('disabled', true);
        }

    }

    // 获得文章标签管理
    function getArticleTagsManagement(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/getArticleTags',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                tagSearch: tagSearch,
                firstDate: firstTagCreateDate,
                lastDate: lastTagCreateDate
            },
            success: function (data) {
                putInArticleTagsManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#articleTagsManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getArticleTagsManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取文章标签信息失败");
            }
        });
    }

    // 删除文章标签
    $('.sureDeleteTagBtn').click(function () {
        $.ajax({
            type: 'POST',
            url: '/deleteTags',
            dataType: 'json',
            async: false,
            data:{
                tagName: articleTagName,
                type: 2
            },
            success: function (data) {
                if (data['status'] === 306) {
                    dangerNotice("标签下存在文章，删除文章标签失败");
                } else if (data['status'] === 305) {
                    dangerNotice("标签不存在，删除文章标签失败");
                } else if (data['status'] === 304) {
                    successNotice("删除文章标签成功");
                    getArticleTagsManagement(1);
                }
            },
            error: function () {
                alert("删除文章标签失败")
            }
        });
    });

    getArticleTagsManagement(1);

    // 填充用户管理
    function putInUserManagement(data, selectGenderSearch) {

        var userManagementTable = $('.userManagementTable');
        userManagementTable.empty();

        userManagementTable.append($('<div class="contentTop">' +
            '目前网站的用户总数：<span class="categoryNum">' + data['total'] + '</span>' +

            '<div style="margin-top: 10px;">' +
            '<input class="formInput" type="text" id="phoneSearch" placeholder="请输入手机号码"/>' +

            '<input class="articleInput" type="text" id="usernameSearch" placeholder="请输入用户名"/>' +

            '<select id="select-genderSearch">' +
            '<option class="genderSearchOption" value="choose">性别</option>' +
            '<option class="genderSearchOption" value="male">男</option>' +
            '<option class="genderSearchOption" value="female">女</option>' +
            '</select>' +

            '<input class="articleInput" type="text" id="firstLoginDate" placeholder="初始日期(最后登录日期)" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastLoginDate" placeholder="结束日期(最后登录日期)" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="LoginRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="LoginReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 200px">手机号码</th><th style="width: 200px">用户名</th><th style="width: 200px">真实姓名</th>' +
            '<th style="width: 70px">性别</th><th style="width: 200px">邮箱</th><th style="width: 180px">出生年月</th>' +
            '<th style="width: 200px">最后登录时间</th><th style="width: 100px">文章总数</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {
            var gender;
            if (obj['gender'] === "male") {
                gender = "男";
            } else {
                gender = "女";
            }

            tables.append('<tr id="u' + obj['id'] + '">' +
                '<td>' + obj['phone'] + '</td>' +
                '<td><a href="/person?personName=' + obj['username'] + '" title="' + obj['username'] + '" target="_blank">' + obj['username'] + '</a></td>' +
                '<td>' + obj['trueName'] + '</td>' +
                '<td>' + gender + '</td>' +
                '<td>' + obj['email'] + '</td>' +
                '<td>' + obj['birthday'] + '</td>' +
                '<td>' + obj['recentlyLanded'] + '</td>' +
                '<td><span class="am-badge am-badge-success am-round">' + obj['articleNum'] + '</span></td>' +
                '</tr>');
        });

        table.append(tables);
        userManagementTable.append(table);
        userManagementTable.append($('<div class="my-row" id="page-father">' +
            '<div id="userManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));


        checkDate('#firstLoginDate');
        checkDate('#lastLoginDate');

        var genderSearchToSelect = $('#select-genderSearch');
        var genderSearchOption = genderSearchToSelect.find('.genderSearchOption');
        if (selectGenderSearch === "choose") {
            genderSearchOption[0].selected = true;

        } else if (selectGenderSearch === "male") {
            genderSearchOption[1].selected = true;

        } else if (selectGenderSearch === "female") {
            genderSearchOption[2].selected = true;

        } else {
            genderSearchOption[0].selected = true;

        }

        var selectFirstLoginDate = $('#firstLoginDate');
        var selectLastLoginDate = $('#lastLoginDate');
        selectFirstLoginDate.change(function () {
            if (selectFirstLoginDate.val() === null || selectFirstLoginDate.val() === "" || typeof(selectFirstLoginDate.val()) === "undefined" ) {
                selectLastLoginDate.attr('disabled', true);
            } else {
                selectLastLoginDate.attr('disabled', false);
            }
            selectLastLoginDate.attr("value","");
        });
        selectLastLoginDate.change(function () {
            var first = selectFirstLoginDate.val();
            var last = selectLastLoginDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastLoginDate.attr("value","");
            }
        });


        $('#LoginRetrieve').click(function () {
            phoneSearch = $('#phoneSearch').val();
            usernameSearch = $('#usernameSearch').val();
            genderSearch = genderSearchToSelect.val();
            firstLoginDate = selectFirstLoginDate.val();
            lastLoginDate = selectLastLoginDate.val();

            getUserManagement(1);
        });

        $('#LoginReset').click(function () {
            phoneSearch = "";
            usernameSearch = "";
            genderSearch = "choose";
            firstLoginDate = "";
            lastLoginDate = "";

            getUserManagement(1);
        });

        $('#phoneSearch').val(phoneSearch);
        $('#usernameSearch').val(usernameSearch);
        genderSearchToSelect.val(genderSearch);
        selectFirstLoginDate.attr("value",firstLoginDate);
        selectLastLoginDate.attr("value",lastLoginDate);

        if (selectFirstLoginDate.val() === null || selectFirstLoginDate.val() === "" || typeof(selectFirstLoginDate.val()) === "undefined" ) {
            selectLastLoginDate.attr('disabled', true);
        }
    }

    // 获得用户管理
    function getUserManagement(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/findAllUser',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                phoneSearch: phoneSearch,
                usernameSearch: usernameSearch,
                genderSearch: genderSearch,
                firstDate: firstLoginDate,
                lastDate: lastLoginDate
            },
            success: function (data) {
                putInUserManagement(data['data'], genderSearch);
                scrollTo(0,0);//回到顶部

                //分页
                $("#userManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getUserManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取用户信息失败")
            }
        });
    }

    getUserManagement(1);


    // 填充用户登录日志管理
    function putInUserLogLoginManagement(data) {

        var userLoginMoreTable = $('.userLoginMoreTable');
        userLoginMoreTable.empty();

        /*userLoginMoreTable.append($('<div class="contentTop">' +
            '目前用户登录日志总数：<span class="categoryNum">' + data['tagsCount'] + '</span>' +
            '</div>'));*/
        userLoginMoreTable.append($('<div style="margin-top: 10px; margin-bottom: 10px;">' +
            '<input class="formInput" type="text" id="loginLogUsername" placeholder="请输入用户名"/>' +

            '<input class="articleInput" type="text" id="loginLogType" placeholder="请输入系统模块"/>' +

            '<input class="articleInput" type="text" id="firstLoginLogDate" placeholder="初始日期(记录时间)" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastLoginLogDate" placeholder="结束日期(记录时间)" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="loginLogRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="loginLogReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 150px">日志序号</th><th style="width: 200px">用户名</th><th style="width: 200px">系统模块</th>' +
            '<th style="width: 200px">操作类型</th><th style="width: 200px">用户IP</th><th style="width: 200px">记录时间</th>' +
            '<th style="width: 100px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="l' + obj['id'] + '">' +
                '<td>' + obj['id'] + '</td>' +
                '<td>' + obj['logUsername'] + '</td>' +
                '<td>' + obj['logModule'] + '</td>' +
                '<td>' + obj['logOperation'] + '</td>' +
                '<td>' + obj['logIp'] + '</td>' +
                '<td>' + obj['createDate'] + '</td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="userLogLoginLookBtn articleDelete am-btn am-btn-secondary am-round">查看详情</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        userLoginMoreTable.append(table);
        userLoginMoreTable.append($('<div class="my-row" id="page-father">' +
            '<div id="userLogLoginManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.userLogLoginLookBtn').click(function () {
            var $this = $(this);
            userLogId = $this.parent().parent().parent().attr("id").substring(1);

            $.ajax({
                type: 'GET',
                url: '/getUserLogOneById',
                dataType: 'json',
                async: false,
                data: {
                    id: userLogId
                },
                success: function (data) {
                    $('#userLogLoginLookLogUsername').val(data['data'].logUsername);
                    $('#userLogLoginLookLogModule').val(data['data'].logModule);
                    $('#userLogLoginLookLogOperation').val(data['data'].logOperation);
                    $('#userLogLoginLookLogMethod').val(data['data'].logMethod);
                    $('#userLogLoginLookLogParams').val(data['data'].logParams);
                    $('#userLogLoginLookLogIp').val(data['data'].logIp);
                    $('#userLogLoginLookLogAddress').val(data['data'].logAddress);
                    $('#userLogLoginLookLogStatus').val(data['data'].logStatus);
                    $('#userLogLoginLookLogTimeConsuming').val(data['data'].logTimeConsuming);
                    $('#userLogLoginLookCreateDate').val(data['data'].createDate);
                },
                error: function () {
                    alert("获取某条用户登录日志信息失败");
                }
            });

            $('#userLogLoginLook').modal('open');
        });

        checkDate('#firstLoginLogDate');
        checkDate('#lastLoginLogDate');

        var selectFirstLoginLogDate = $('#firstLoginLogDate');
        var selectLastLoginLogDate = $('#lastLoginLogDate');
        selectFirstLoginLogDate.change(function () {
            if (selectFirstLoginLogDate.val() === null || selectFirstLoginLogDate.val() === "" || typeof(selectFirstLoginLogDate.val()) === "undefined" ) {
                selectLastLoginLogDate.attr('disabled', true);
            } else {
                selectLastLoginLogDate.attr('disabled', false);
            }
            selectLastLoginLogDate.attr("value","");
        });
        selectLastLoginLogDate.change(function () {
            var first = selectFirstLoginLogDate.val();
            var last = selectLastLoginLogDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastLoginLogDate.attr("value","");
            }
        });


        $('#loginLogRetrieve').click(function () {
            loginLogUsername = $('#loginLogUsername').val();
            loginLogType = $('#loginLogType').val();
            firstLoginLogDate = selectFirstLoginLogDate.val();
            lastLoginLogDate = selectLastLoginLogDate.val();

            getUserLogLoginManagement(1);
        });

        $('#loginLogReset').click(function () {
            loginLogUsername = "";
            loginLogType = "";
            firstLoginLogDate = "";
            lastLoginLogDate = "";

            getUserLogLoginManagement(1);
        });

        $('#loginLogUsername').val(loginLogUsername);
        $('#loginLogType').val(loginLogType);
        selectFirstLoginLogDate.attr("value",firstLoginLogDate);
        selectLastLoginLogDate.attr("value",lastLoginLogDate);

        if (selectFirstLoginLogDate.val() === null || selectFirstLoginLogDate.val() === "" || typeof(selectFirstLoginLogDate.val()) === "undefined" ) {
            selectLastLoginLogDate.attr('disabled', true);
        }

    }

    // 获得用户登录日志管理
    function getUserLogLoginManagement(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getAllUserLog',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                type: 1,
                logUsername: loginLogUsername,
                logModule: loginLogType,
                firstDate: firstLoginLogDate,
                lastDate: lastLoginLogDate
            },
            success: function (data) {
                putInUserLogLoginManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#userLogLoginManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getUserLogLoginManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取用户登录日志信息失败")
            }
        });
    }

    getUserLogLoginManagement(1);


    // 填充用户操作日志管理
    function putInUserLogOperatingManagement(data) {

        var userOperatingTable = $('.userOperatingTable');
        userOperatingTable.empty();

        /*userLoginMoreTable.append($('<div class="contentTop">' +
            '目前用户登录日志总数：<span class="categoryNum">' + data['tagsCount'] + '</span>' +
            '</div>'));*/
        userOperatingTable.append($('<div style="margin-top: 10px; margin-bottom: 10px;">' +
            '<input class="formInput" type="text" id="operatingLogUsername" placeholder="请输入用户名"/>' +

            '<input class="articleInput" type="text" id="operatingLogType" placeholder="请输入系统模块"/>' +

            '<input class="articleInput" type="text" id="firstOperatingLogDate" placeholder="初始日期(记录时间)" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastOperatingLogDate" placeholder="结束日期(记录时间)" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="operatingLogRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="operatingLogReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>'));

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 150px">日志序号</th><th style="width: 200px">用户名</th><th style="width: 200px">系统模块</th>' +
            '<th style="width: 200px">操作类型</th><th style="width: 200px">用户IP</th><th style="width: 200px">记录时间</th>' +
            '<th style="width: 100px">操作</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="l' + obj['id'] + '">' +
                '<td>' + obj['id'] + '</td>' +
                '<td>' + obj['logUsername'] + '</td>' +
                '<td>' + obj['logModule'] + '</td>' +
                '<td>' + obj['logOperation'] + '</td>' +
                '<td>' + obj['logIp'] + '</td>' +
                '<td>' + obj['createDate'] + '</td>' +
                '<td>' +
                '<div class="am-dropdown" data-am-dropdown>' +
                '<button class="userLogOperatingLookBtn articleDelete am-btn am-btn-secondary am-round">查看详情</button>' +
                '</div>' +
                '</td>' +
                '</tr>');
        });

        table.append(tables);
        userOperatingTable.append(table);
        userOperatingTable.append($('<div class="my-row" id="page-father">' +
            '<div id="userLogOperatingManagementPagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        $('.userLogOperatingLookBtn').click(function () {
            var $this = $(this);
            userLogId = $this.parent().parent().parent().attr("id").substring(1);

            $.ajax({
                type: 'GET',
                url: '/getUserLogOneById',
                dataType: 'json',
                async: false,
                data: {
                    id: userLogId
                },
                success: function (data) {
                    $('#userLogOperatingLookLogUsername').val(data['data'].logUsername);
                    $('#userLogOperatingLookLogModule').val(data['data'].logModule);
                    $('#userLogOperatingLookLogOperation').val(data['data'].logOperation);
                    $('#userLogOperatingLookLogMethod').val(data['data'].logMethod);
                    $('#userLogOperatingLookLogParams').val(data['data'].logParams);
                    $('#userLogOperatingLookLogIp').val(data['data'].logIp);
                    $('#userLogOperatingLookLogAddress').val(data['data'].logAddress);
                    $('#userLogOperatingLookLogStatus').val(data['data'].logStatus);
                    $('#userLogOperatingLookLogTimeConsuming').val(data['data'].logTimeConsuming);
                    $('#userLogOperatingLookCreateDate').val(data['data'].createDate);
                },
                error: function () {
                    alert("获取某条用户操作日志信息失败");
                }
            });

            $('#userLogOperatingLook').modal('open');
        });

        checkDate('#firstOperatingLogDate');
        checkDate('#lastOperatingLogDate');

        var selectFirstOperatingLogDate = $('#firstOperatingLogDate');
        var selectLastOperatingLogDate = $('#lastOperatingLogDate');
        selectFirstOperatingLogDate.change(function () {
            if (selectFirstOperatingLogDate.val() === null || selectFirstOperatingLogDate.val() === "" || typeof(selectFirstOperatingLogDate.val()) === "undefined" ) {
                selectLastOperatingLogDate.attr('disabled', true);
            } else {
                selectLastOperatingLogDate.attr('disabled', false);
            }
            selectLastOperatingLogDate.attr("value","");
        });
        selectLastOperatingLogDate.change(function () {
            var first = selectFirstOperatingLogDate.val();
            var last = selectLastOperatingLogDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastOperatingLogDate.attr("value","");
            }
        });


        $('#operatingLogRetrieve').click(function () {
            operatingLogUsername = $('#operatingLogUsername').val();
            operatingLogType = $('#operatingLogType').val();
            firstOperatingLogDate = selectFirstOperatingLogDate.val();
            lastOperatingLogDate = selectLastOperatingLogDate.val();

            getUserLogOperatingManagement(1);
        });

        $('#operatingLogReset').click(function () {
            operatingLogUsername = "";
            operatingLogType = "";
            firstOperatingLogDate = "";
            lastOperatingLogDate = "";

            getUserLogOperatingManagement(1);
        });

        $('#operatingLogUsername').val(operatingLogUsername);
        $('#operatingLogType').val(operatingLogType);
        selectFirstOperatingLogDate.attr("value",firstOperatingLogDate);
        selectLastOperatingLogDate.attr("value",lastOperatingLogDate);

        if (selectFirstOperatingLogDate.val() === null || selectFirstOperatingLogDate.val() === "" || typeof(selectFirstOperatingLogDate.val()) === "undefined" ) {
            selectLastOperatingLogDate.attr('disabled', true);
        }

    }

    // 获得用户操作日志管理
    function getUserLogOperatingManagement(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getAllUserLog',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                type: 2,
                logUsername: operatingLogUsername,
                logModule: operatingLogType,
                firstDate: firstOperatingLogDate,
                lastDate: lastOperatingLogDate
            },
            success: function (data) {
                putInUserLogOperatingManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#userLogOperatingManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getUserLogOperatingManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取用户操作日志信息失败")
            }
        });
    }

    getUserLogOperatingManagement(1);


    // 填充返回码帮助页管理
    function putInCodeReturnManagement(data) {

        var returnCodeHelpPageTable = $('.returnCodeHelpPageTable');
        returnCodeHelpPageTable.empty();

        var table = $('<table class="table am-table am-table-bd am-table-striped admin-content-table am-animation-slide-right" style="word-wrap: break-word;"></table>');
        table.append($('<thead>' +
            '<tr>' +
            '<th style="width: 300px">状态码</th><th style="width: 300px">状态简介</th><th style="width: 300px">状态描述</th>' +
            '</tr>' +
            '</thead>'));

        var tables = $('<tbody class="tables"></tbody>');
        $.each(data['result'], function (index, obj) {

            tables.append('<tr id="o' + obj['id'] + '">' +
                '<td>' + obj['code'] + '</td>' +
                '<td>' + obj['introduction'] + '</td>' +
                '<td>' + obj['message'] + '</td>' +
                '</tr>');
        });

        table.append(tables);
        returnCodeHelpPageTable.append(table);
        returnCodeHelpPageTable.append($('<div class="my-row" id="page-father">' +
            '<div id="codeReturnManagementPagination">' +
            '<ul class="am-pagination am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));
    }

    // 获得返回码帮助页管理
    function getCodeReturnManagement(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/selectSomeCodeTypes',
            dataType: 'json',
            data: {
                rows: "8",
                pageNum: currentPage,
                code: statusCode
            },
            success: function (data) {
                putInCodeReturnManagement(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#codeReturnManagementPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback:function(currentPage){
                        getCodeReturnManagement(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取返回码帮助页信息失败")
            }
        });
    }

    $('#codeRetrieve').click(function () {
        statusCode = $('#statusCode').val();
        if (statusCode === "" || statusCode === null || statusCode.length === 0) {
            dangerNotice("查询信息不能为空")
        } else {
            getCodeReturnManagement(1);
        }
        $('#statusCode').val(statusCode);
    });

    $('#codeReset').click(function () {
        statusCode = "";
        $('#statusCode').val(statusCode);
        getCodeReturnManagement(1);
    });

    getCodeReturnManagement(1);


    // 填充反馈信息
    function putInAllFeedback(data, selectFeedBackRead) {
        var feedbackInfos = $('.feedbackInfos');
        feedbackInfos.empty();

        feedbackInfos.append($(' <div class="msgReadTop">' +
            '&nbsp;&nbsp;未读消息：<span class="msgIsReadNum">' + data['feedbackNotRead'] + '</span>' +
            '<a class="msgIsRead" style="margin-right: 10px">全部标记为已读</a>' +

            '<div style="margin-top: 10px;">' +

            '<select id="select-feedBackRead" style="margin-left: 0">' +
            '<option class="feedBackReadOption" value="choose">查看全部</option>' +
            '<option class="feedBackReadOption" value="1">只看未读</option>' +
            '<option class="feedBackReadOption" value="0">只看已读</option>' +
            '</select>' +

            '<input class="articleInput" type="text" id="firstFeedBackDate" placeholder="初始日期" readonly/>&nbsp;&nbsp;--' +
            '<input class="articleInput" type="text" id="lastFeedBackDate" placeholder="结束日期" readonly/>' +

            '<button class="am-btn am-btn-secondary am-round codeButton" id="feedBackRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
            '<button class="am-btn am-btn-default am-round codeButton" id="feedBackReset" style="margin-left: 5px"><strong>重置</strong></button>' +

            '</div>' +

            '</div>'));

        if(data['result'].length === 0){
            feedbackInfos.append('<div class="noNews">这里空空如也</div>');
        } else {

            $.each(data['result'], function (index, obj) {
                var feedbackInfo = $('<div class="feedbackInfo" id="f' + obj['id'] + '"></div>');

                var msgReadSign = $('<span class="msgReadSign"></span>');
                if(obj['isRead'] === 1){
                    feedbackInfo.append(msgReadSign);
                }

                feedbackInfo.append('<div class="feedbackInfoTitle">' +
                    '反馈者：<span class="feedbackName">' + obj['person'] + '</span>' +
                    '<span class="feedbackTime">' + obj['feedbackDate'] + '</span>' +
                    '</div>');

                var feedbackInfoContent = $('<div class="feedbackInfoContent"></div>');
                feedbackInfoContent.append($('<span class="feedbackInfoContentWord">反馈内容：</span><span>' + obj['feedbackContent'] + '</span>'));

                if (obj['isRead'] === 1) {
                    feedbackInfoContent.append($('<button class="feedbackReadMsg am-btn am-btn-secondary am-round ">已读</button>'));
                }

                feedbackInfo.append(feedbackInfoContent);

                var feedbackInfoContact = $('<div class="feedbackInfoContact"></div>');
                if(obj['contactInfo'] !== ""){
                    feedbackInfoContact.append('<span class="contactInfo">联系方式：</span>' + obj['contactInfo']);
                } else {
                    feedbackInfoContact.append('<span class="contactInfo">联系方式：</span>' + '无');
                }

                feedbackInfo.append(feedbackInfoContact);
                feedbackInfos.append(feedbackInfo);
            });
            feedbackInfos.append($('<div class="my-row" id="page-father">' +
                '<div id="feedbackPagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }


        // 已读一条反馈消息
        $('.feedbackReadMsg').click(function () {
            var parent = $(this).parent().parent();
            var isRead = true;
            var num = $('.msgIsReadNum').html();
            if(parent.find($('.msgReadSign')).length !== 0){
                isRead = false;
            }
            if(isRead === false){
                var id = parent.attr('id').substring(1);
                $.ajax({
                    type: 'GET',
                    url: '/readOneFeedBackRecord',
                    dataType: 'json',
                    data:{
                        id: id,
                    },
                    success:function (data) {
                        if (data['status'] === 1303) {
                            dangerNotice("已读一条反馈消息失败")
                        }
                    },
                    error:function () {
                        alert("已读一条反馈消息失败")
                    }
                });
                // 去掉未读红点
                parent.find($('.msgReadSign')).removeClass('msgReadSign');

                // 去掉 已读 按钮
                parent.find($('.feedbackReadMsg')).remove();

                // 未读消息减1
                $('.msgIsReadNum').html(--num);

                // 去掉左侧栏未读消息
                if(num === 0){
                    $('.feedbackNum').remove();
                } else {
                    $('.feedbackNum').html(num);
                }

                // 去掉导航栏下拉框未读消息
                var superAdminNum = $('.superAdminNum').html();
                --superAdminNum;
                if(superAdminNum === 0){
                    $('.superAdminNum').remove();
                } else {
                    $('.superAdminNum').html(superAdminNum);
                }
            }
        });


        //全部反馈消息标记为已读
        $('.msgIsRead').click(function () {
            var num = $('.msgIsReadNum').html();
            if(num !== 0){
                $.ajax({
                    type: 'GET',
                    url: '/readAllFeedBack',
                    dataType: 'json',
                    data:{
                    },
                    success:function (data) {
                        if(data['status'] === 101){
                            $.get("/toLogin",function(data,status,xhr){
                                window.location.replace("/login");
                            });
                        } else if (data['status'] === 1303) {
                            dangerNotice("已读全部反馈消息失败")
                        } else {
                            $('.msgIsReadNum').html(0);
                            $('.feedbackInfos').find($('.msgReadSign')).removeClass('msgReadSign');
                            $('.feedbackInfos').find($('.feedbackReadMsg')).remove();

                            $('.feedbackNum').remove();

                            var superAdminNum = $('.superAdminNum').html();
                            if(superAdminNum === num){
                                $('.superAdminNum').remove();
                            } else {
                                $('.superAdminNum').html(superAdminNum - num);
                            }
                        }
                    },
                    error:function () {
                        alert("已读全部反馈消息失败")
                    }
                });
            }
        });

        checkDate('#firstFeedBackDate');
        checkDate('#lastFeedBackDate');

        var feedBackReadToSelect = $('#select-feedBackRead');
        var feedBackReadOption = feedBackReadToSelect.find('.feedBackReadOption');

        if (selectFeedBackRead === "choose") {
            feedBackReadOption[0].selected = true;

        } else if (selectFeedBackRead === "1") {
            feedBackReadOption[1].selected = true;

        } else if (selectFeedBackRead === "0") {
            feedBackReadOption[2].selected = true;

        } else {
            feedBackReadOption[0].selected = true;

        }

        var selectFirstFeedBackDate = $('#firstFeedBackDate');
        var selectLastFeedBackDate = $('#lastFeedBackDate');
        selectFirstFeedBackDate.change(function () {
            if (selectFirstFeedBackDate.val() === null || selectFirstFeedBackDate.val() === "" || typeof(selectFirstFeedBackDate.val()) === "undefined" ) {
                selectLastFeedBackDate.attr('disabled', true);
            } else {
                selectLastFeedBackDate.attr('disabled', false);
            }
            selectLastFeedBackDate.attr("value","");
        });

        selectLastFeedBackDate.change(function () {
            var first = selectFirstFeedBackDate.val();
            var last = selectLastFeedBackDate.val();

            if (new Date(last.substring(0, 4), last.substring(5, 7) - 1, last.substring(8, 10)) <
                new Date(first.substring(0, 4), first.substring(5, 7) - 1, first.substring(8, 10))) {
                dangerNotice("时间范围选择错误，请重新选择");
                selectLastFeedBackDate.attr("value","");
            }
        });


        $('#feedBackRetrieve').click(function () {
            feedBackRead = feedBackReadToSelect.val();
            firstFeedBackDate = selectFirstFeedBackDate.val();
            lastFeedBackDate = selectLastFeedBackDate.val();
            getAllFeedback(1);
        });

        $('#feedBackReset').click(function () {
            feedBackRead = "choose";
            firstFeedBackDate = "";
            lastFeedBackDate = "";

            getAllFeedback(1);
        });

        feedBackReadToSelect.val(feedBackRead);
        selectFirstFeedBackDate.attr("value",firstFeedBackDate);
        selectLastFeedBackDate.attr("value",lastFeedBackDate);

        if (selectFirstFeedBackDate.val() === null || selectFirstFeedBackDate.val() === "" || typeof(selectFirstFeedBackDate.val()) === "undefined" ) {
            selectLastFeedBackDate.attr('disabled', true);
        }

    }

    //获得反馈信息
    function getAllFeedback(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/getAllFeedback',
            dataType: 'json',
            data:{
                rows: 8,
                pageNum: currentPage,
                feedBackRead: feedBackRead,
                firstDate: firstFeedBackDate,
                lastDate: lastFeedBackDate
            },
            success:function (data) {
                putInAllFeedback(data['data'], feedBackRead);
                scrollTo(0,0);//回到顶部

                //分页
                $("#feedbackPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        getAllFeedback(currentPage);
                    }
                });
            },
            error:function () {
                alert("获取反馈信息失败");
            }
        });
    }

    $('.superAdminList .userFeedback').click(function () {
        getAllFeedback(1);
    })




});
