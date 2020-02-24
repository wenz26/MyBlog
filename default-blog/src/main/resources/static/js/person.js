$(function () {
    var personName = "";
    var articleTitle = "";

    var articleShow = $('.articleShow');
    var attentionShow = $('.attentionShow');
    var fanShow = $('.fanShow');

    articleShow.css("background-color", "#e6e6e6");

    $('.personBtnShow').click(function () {
        var flag = $(this).attr('class').substring(36);
        $('#articleShow, #attentionShow, #fanShow').css("display","none");
        $("#" + flag).css("display","block");

        $('.personBtnShow').removeAttr("style");
        $("." + flag).css("background-color", "#e6e6e6");
    });

    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url: window.location.href,
        async: false,
        success: function (data, status, xhr) {
            personName = xhr.getResponseHeader("personName");
        }

    });

    // 添加用户详情
    function putInPersonInfo(data) {
        var articleTimeline = $('.articleTimeline');
        articleTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');

        if (data['user']['username'] === data['user']['currentUsername']) {
            timeLine.append($('<div class="timeline-row" style="height: 80px" id="u' + data['user']['userId'] + '">' +
                '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                '</div>' +
                '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                // '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                '<br><a href="/user"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px">前往个人主页' +
                '</button></a>' +

                '<input class="formInput" type="text" id="articleTitle" placeholder="请输入文章标题" style="margin-left: 50px"/>' +
                '<button class="am-btn am-btn-secondary am-round codeButton" id="attentionRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
                '<button class="am-btn am-btn-default am-round codeButton" id="attentionReset" style="margin-left: 5px"><strong>重置</strong></button>' +

                '</div>'));
        } else {
            if (data['user']['isAttention'] === false) {
                timeLine.append($('<div class="timeline-row" style="height: 80px" id="u' + data['user']['userId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                    '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                    // '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                    '<br><button class="personAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px"><span class="am-icon-plus"></span>&nbsp;加关注' +
                    '</button>' +

                    '<input class="formInput" type="text" id="articleTitle" placeholder="请输入文章标题" style="margin-left: 50px"/>' +
                    '<button class="am-btn am-btn-secondary am-round codeButton" id="attentionRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
                    '<button class="am-btn am-btn-default am-round codeButton" id="attentionReset" style="margin-left: 5px"><strong>重置</strong></button>' +

                    '</div>'));
            } else {
                timeLine.append($('<div class="timeline-row" style="height: 80px" id="u' + data['user']['userId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                    '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                    //'&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                    '<br><button class="personAttentionBtn am-btn am-btn-secondary am-btn-sm" style="border-radius: 6px" disabled><span class="am-icon-check"></span>&nbsp;已关注' +
                    '</button>' +

                    '<input class="formInput" type="text" id="articleTitle" placeholder="请输入文章标题" style="margin-left: 50px"/>' +
                    '<button class="am-btn am-btn-secondary am-round codeButton" id="attentionRetrieve" style="margin-left: 5px"><strong>查询</strong></button>' +
                    '<button class="am-btn am-btn-default am-round codeButton" id="attentionReset" style="margin-left: 5px"><strong>重置</strong></button>' +

                    '</div>'));
            }
        }

        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
            articleTimeline.append(timeLine);

        } else {
            $.each(data['result'], function (index, obj) {

                var timelineRowMajor = $('<div class="timeline-row-major"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');
                content.append($('<header class="am-comment-hd" style="background: #fff">' +
                    '<div class="contentTitle am-comment-meta">' +
                    '<a href="/article/' + obj['articleId'] +'" title="' + obj['articleTitle'] + '" target="_blank">' + obj['articleTitle'] + '</a>' +
                    '</div>' +
                    '</header>'));

                var amCommentBd = $('<div class="am-comment-bd"></div>');
                amCommentBd.append($('<i class="am-icon-calendar"> <a href="/archives?archiveDay=' + obj['publishDateForThree'] + '" title="' + obj['publishDate'] + '">' + obj['publishDate'] + '</a></i>' +
                    '<i class="am-icon-user" ><a href="/person?personName=' + obj['author'] + '" title="' + obj['author'] + '"> ' + obj['author'] + '</a></i>' +
                    '<i class="am-icon-folder"> <a href="/categories?categoryName=' + obj['articleCategories'] + '" title="' + obj['articleCategories'] + '">' + obj['articleCategories'] + '</a></i>'));

                var amCommentBdTags = $('<i class="am-comment-bd-tags am-icon-tag"></i>');
                for (var i = 0; i < obj['articleTags'].length; i++) {

                    var tag;
                    if (obj['articleTags'][i] === data['tag']) {
                        tag = $('<a href="/tags?tag=' + obj['articleTags'][i] + '" title="' + obj['articleTags'][i] + '"><span class="am-badge am-badge-warning am-round">' + obj['articleTags'][i] + '</span></a>');
                    } else {
                        tag = $('<a href="/tags?tag=' + obj['articleTags'][i] + '" title="' + obj['articleTags'][i] + '">' + obj['articleTags'][i] + '</a>');
                    }
                    amCommentBdTags.append(tag);

                    if (i !== (obj['articleTags'].length - 1)) {
                        amCommentBdTags.append(",");
                    }
                }

                amCommentBd.append(amCommentBdTags);
                content.append(amCommentBd);
                timelineRowMajor.append(content);
                timeLine.append(timelineRowMajor);
            });

            articleTimeline.append(timeLine);
            articleTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="pagination">' +
                '<ul class="am-pagination  am-pagination-centered ">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

        var personAttentionBtn = $('.personAttentionBtn');
        personAttentionBtn.click(function () {
            var attentionId = $(this).parent().attr("id").substring(1);

            $.ajax({
                type: 'GET',
                url: '/insertUserAttention',
                dataType: 'json',
                data: {
                    attentionId: attentionId
                },
                success: function (data) {
                    if (data['status'] === 101) {
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else if (data['status'] === 1401) {
                        personAttentionBtn.removeClass("am-btn-default");
                        personAttentionBtn.addClass("am-btn-secondary");
                        personAttentionBtn.attr("disabled", true);
                        personAttentionBtn.html("<span class='am-icon-check'></span>&nbsp;已关注");
                        successNotice("关注成功");

                        ajaxPersonInfo(1);
                        ajaxAttention(1);
                        ajaxFan(1);
                    } else if (data['status'] === 1402) {
                        dangerNotice("关注用户失败，请刷新后重试");
                    }
                },
                error: function () {
                    alert("关注用户失败，请刷新后重试")
                }
            });
        });

        $('#attentionRetrieve').click(function () {
            articleTitle = $('#articleTitle').val();
            if (articleTitle === "" || articleTitle === null || articleTitle.length === 0) {
                alert("查询内容不能为空")
            } else {
                ajaxPersonInfo(1);

            }
        });

        $('#attentionReset').click(function () {
            articleTitle = "";
            ajaxPersonInfo(1);
        });

    }

    // 获取用户详情
    function ajaxPersonInfo(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getPersonInfo',
            dataType: 'json',
            async: false,
            data:{
                personName: personName,
                articleTitle: articleTitle,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                if (data['code'] === 508) {
                    var amContainer = $('.am-container');
                    amContainer.empty();
                    var amG = $('<div class="am-g"></div>');
                    var siteInner = $('<div class="site-inner"></div>');

                    amG.append(siteInner);
                    amContainer.append(amG);

                    siteInner.append($('<div class="timeline timeline-wrap">' +
                        '<div class="noNews">' +
                        '该用户尚未发表过文章' +
                        '</div>' +
                        '</div>'))
                } else {
                    putInPersonInfo(data['data']);
                    scrollTo(0,0);//回到顶部

                    //分页
                    $("#pagination").paging({
                        rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                        pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                        pages: data['data']['pageInfo']['pages'],//总页数
                        total: data['data']['pageInfo']['total'],//总记录数
                        callback: function(currentPage){
                            ajaxPersonInfo(currentPage);
                        }
                    });

                    $('#articleTitle').val(articleTitle);
                }
            },
            error: function () {
                alert("获取用户详情失败")
            }
        });
    }

    ajaxPersonInfo(1);


    // 填充关注的信息
    function putInAttentionInfo(data) {
        var attentionTimeline = $('.attentionTimeline');
        attentionTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');

        if (data['user']['userId'] === data['user']['myUserId']) {
            timeLine.append($('<div class="timeline-row" style="height: 80px" id="b' + data['user']['userId'] + '">' +
                '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                '</div>' +
                '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                // '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                '<br><a href="/user"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px; float: none; width: 180px; margin-left: 55px">前往个人主页' +
                '</button></a>' +

                '</div>'));
        } else {
            if (data['user']['isAttention'] === false) {
                timeLine.append($('<div class="timeline-row" style="height: 80px" id="b' + data['user']['userId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                    '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                    // '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                    '<br><span><span><button class="personAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px; float: none; width: 180px; margin-left: 55px">' +
                    '<span class="am-icon-plus"></span>&nbsp;加关注' +
                    '</button></span></span>' +

                    '</div>'));
            } else {
                timeLine.append($('<div class="timeline-row" style="height: 80px" id="b' + data['user']['userId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                    '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                    //'&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                    '<br><span><span><button class="personAttentionBtn am-btn am-btn-secondary am-btn-sm" style="border-radius: 6px; float: none; width: 180px; margin-left: 55px" disabled>' +
                    '<span class="am-icon-check"></span>&nbsp;已关注' +
                    '</button></span></span>' +

                    '</div>'));
            }
        }


        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
            attentionTimeline.append(timeLine);

        } else {

            $.each(data['result'], function (index, obj) {

                var personalBrief;
                if (obj['attentionPersonalBrief'] === null || obj['attentionPersonalBrief'] === "" || typeof(obj['attentionPersonalBrief']) === "undefined" ) {
                    personalBrief = "朋友，不必太纠结于当下，也不必太忧虑未来，当你经历过一些事情的时候，眼前的风景已经和从前不一样了。";
                } else {
                    personalBrief = obj['attentionPersonalBrief'];
                }

                var timelineRowMajor = $('<div class="timeline-row-major" id="g'+ obj['attentionUserId'] +'"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');



                if (obj['attentionUserId'] === data['user']['myUserId']) {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                        '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                        '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                        '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                        '<a href="/user" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="margin-top: 33px; margin-right: 20px">前往个人主页' +
                        '</button></a>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<span>'+ personalBrief +'</span>' +
                        '</div>' +
                        '</div>'));
                } else {
                    if (obj['isAttention'] === true) {
                        content.append($('<div style="min-height: 90px">' +
                            '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                            '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="personAttentionBtn am-btn am-btn-secondary am-btn-sm" style="margin-top: 33px; margin-right: 20px" disabled>' +
                            '<span class="am-icon-check"></span>&nbsp;已关注' +
                            '</button>' +
                            '<br>' +
                            '<div class="myAttentionDisplay">' +
                            '<span>'+ personalBrief +'</span>' +
                            '</div>' +
                            '</div>'));
                    } else if (obj['isAttention'] === false) {
                        content.append($('<div style="min-height: 90px">' +
                            '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                            '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="personAttentionBtn am-btn am-btn-default am-btn-sm" style="margin-top: 33px; margin-right: 20px">' +
                            '<span class="am-icon-plus"></span>&nbsp;加关注' +
                            '</button>' +
                            '<br>' +
                            '<div class="myAttentionDisplay">' +
                            '<span>'+ personalBrief +'</span>' +
                            '</div>' +
                            '</div>'));
                    } else if (obj['isAttention'] === "myself") {
                        content.append($('<div style="min-height: 90px" id="d'+ obj['userAttentionId'] +'">' +
                            '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                            '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="cancelAttentionBtn am-btn am-btn-default am-btn-sm" style="margin-top: 33px; margin-right: 20px">' +
                            '<span class="am-icon-times"></span>&nbsp;取消关注' +
                            '</button>' +
                            '<br>' +
                            '<div class="myAttentionDisplay">' +
                            '<span>'+ personalBrief +'</span>' +
                            '</div>' +
                            '</div>'));
                    }

                }

                timelineRowMajor.append(content);
                timeLine.append(timelineRowMajor);

            });

            attentionTimeline.append(timeLine);
            attentionTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="attentionPagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

        var personAttentionBtn = $('.personAttentionBtn');
        personAttentionBtn.click(function () {
            var attentionUserId = $(this).parent().parent().parent().attr("id").substring(1);
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

                        ajaxPersonInfo(1);
                        ajaxAttention(1);
                        ajaxFan(1);
                    } else if (data['status'] === 1402) {
                        dangerNotice("关注用户失败，请刷新后重试");
                    }
                },
                error: function () {
                    alert("关注用户失败，请刷新后重试")
                }
            });
        });


        var cancelAttentionBtn = $('.cancelAttentionBtn')
        cancelAttentionBtn.click(function () {
            var attentionId = $(this).parent().attr("id").substring(1);

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

                        ajaxPersonInfo(1);
                        ajaxAttention(1);
                        ajaxFan(1);
                    }
                },
                error: function () {
                    alert("取消关注失败")
                }
            });
        });

    }

    // 获取关注请求
    function ajaxAttention(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getSomeOneAttention',
            dataType: 'json',
            data:{
                username: personName,
                type: 1,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInAttentionInfo(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#attentionPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxAttention(currentPage);
                    }
                });

            },
            error: function () {
                alert("获取关注失败哦");
            }
        });
    }

    ajaxAttention(1);

    // 填充粉丝的信息
    function putInFanInfo(data) {
        var fanTimeline = $('.fanTimeline');
        fanTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');

        if (data['user']['userId'] === data['user']['myUserId']) {
            timeLine.append($('<div class="timeline-row" style="height: 80px" id="x' + data['user']['userId'] + '">' +
                '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                '</div>' +
                '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                // '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                '<br><a href="/user"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px; float: none; width: 180px; margin-left: 55px">前往个人主页' +
                '</button></a>' +

                '</div>'));
        } else {
            if (data['user']['isAttention'] === false) {
                timeLine.append($('<div class="timeline-row" style="height: 80px" id="x' + data['user']['userId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                    '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                    // '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                    '<br><span><span><button class="fanPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="border-radius: 6px; float: none; width: 180px; margin-left: 55px">' +
                    '<span class="am-icon-plus"></span>&nbsp;加关注' +
                    '</button></span></span>' +

                    '</div>'));
            } else {
                timeLine.append($('<div class="timeline-row" style="height: 80px" id="x' + data['user']['userId'] + '">' +
                    '<div class="personImg am-u-sm-2 am-u-lg-1">' +
                    '<img src="' + data['user']['avatarImgUrl'] + '" style="object-fit: cover">' +
                    '</div>' +
                    '<h1 class="title am-animation-slide-top" style="margin-left: 50px; margin-bottom: 5px">'+ data['user']['username'] + '</h1>' +

                    '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + data['user']['countAttention'] + '</strong></span>' +
                    '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + data['user']['countFan'] + '</strong></span>' +
                    '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + data['user']['articleNum'] + '</strong></span>' +

                    //'&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['user']['articleNum'] + '</strong>&nbsp;篇文章)</span>' +
                    '<br><span><span><button class="fanPersonAttentionBtn am-btn am-btn-secondary am-btn-sm" style="border-radius: 6px; float: none; width: 180px; margin-left: 55px" disabled>' +
                    '<span class="am-icon-check"></span>&nbsp;已关注' +
                    '</button></span></span>' +

                    '</div>'));
            }
        }


        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
            fanTimeline.append(timeLine);

        } else {

            $.each(data['result'], function (index, obj) {

                var personalBrief;
                if (obj['attentionPersonalBrief'] === null || obj['attentionPersonalBrief'] === "" || typeof(obj['attentionPersonalBrief']) === "undefined" ) {
                    personalBrief = "朋友，不必太纠结于当下，也不必太忧虑未来，当你经历过一些事情的时候，眼前的风景已经和从前不一样了。";
                } else {
                    personalBrief = obj['attentionPersonalBrief'];
                }

                var timelineRowMajor = $('<div class="timeline-row-major" id="f'+ obj['attentionUserId'] +'"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');



                if (obj['attentionUserId'] === data['user']['myUserId']) {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                        '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                        '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                        '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                        '<a href="/user" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="margin-top: 33px; margin-right: 20px">前往个人主页' +
                        '</button></a>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<span>'+ personalBrief +'</span>' +
                        '</div>' +
                        '</div>'));
                } else {
                    if (obj['isAttention'] === true) {
                        content.append($('<div style="min-height: 90px">' +
                            '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                            '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="fanPersonAttentionBtn am-btn am-btn-secondary am-btn-sm" style="margin-top: 33px; margin-right: 20px" disabled>' +
                            '<span class="am-icon-check"></span>&nbsp;已关注' +
                            '</button>' +
                            '<br>' +
                            '<div class="myAttentionDisplay">' +
                            '<span>'+ personalBrief +'</span>' +
                            '</div>' +
                            '</div>'));
                    } else if (obj['isAttention'] === false) {
                        content.append($('<div style="min-height: 90px">' +
                            '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                            '<img src="' + obj['attentionAvatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['attentionUsername'] + '" title="' + obj['attentionUsername'] + '" target="_blank">'+ obj['attentionUsername'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="fanPersonAttentionBtn am-btn am-btn-default am-btn-sm" style="margin-top: 33px; margin-right: 20px">' +
                            '<span class="am-icon-plus"></span>&nbsp;加关注' +
                            '</button>' +
                            '<br>' +
                            '<div class="myAttentionDisplay">' +
                            '<span>'+ personalBrief +'</span>' +
                            '</div>' +
                            '</div>'));
                    }
                }

                timelineRowMajor.append(content);
                timeLine.append(timelineRowMajor);

            });

            fanTimeline.append(timeLine);
            fanTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="fanPagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

        var fanPersonAttentionBtn = $('.fanPersonAttentionBtn');
        fanPersonAttentionBtn.click(function () {
            var attentionUserId = $(this).parent().parent().parent().attr("id").substring(1);
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

                        ajaxPersonInfo(1);
                        ajaxAttention(1);
                        ajaxFan(1);

                    } else if (data['status'] === 1402) {
                        dangerNotice("关注用户失败，请刷新后重试");
                    }
                },
                error: function () {
                    alert("关注用户失败，请刷新后重试")
                }
            });
        });

    }

    // 获取粉丝请求
    function ajaxFan(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getSomeOneAttention',
            dataType: 'json',
            data:{
                username: personName,
                type: 2,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInFanInfo(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#fanPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxFan(currentPage);
                    }
                });

            },
            error: function () {
                alert("获取粉丝失败哦");
            }
        });
    }

    ajaxFan(1);



});
