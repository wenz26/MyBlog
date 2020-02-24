$(function () {
    var searchKeyWordsValue = "";
    var timeRange = "";

    // 导航栏上的搜索文章
    var searchKeyWordsContent = $('#searchKeyWordsContent');
    var searchKeyWordsBtn = $('#searchKeyWordsBtn');

    var articleShow = $('.articleShow');
    var userShow = $('.userShow');
    var tagsShow = $('.tagsShow');
    var categoriesShow = $('.categoriesShow');

    articleShow.css("background-color", "#e6e6e6");

    $('.searchBtnShow').click(function () {
        var flag = $(this).attr('class').substring(36);
        $('#articleShow, #userShow, #tagsShow, #categoriesShow').css("display","none");
        $("#" + flag).css("display","block");

        $('.searchBtnShow').removeAttr("style");
        $("." + flag).css("background-color", "#e6e6e6");
    });

    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url : window.location.href,
        async:false,
        data: {
            type: "1"
        },
        success: function (data, status, xhr) {
            searchKeyWordsValue = xhr.getResponseHeader("keyWords");
        }
    });

    // 填充搜索历史
    function PutInHistorySearch(data) {
        var searchHistory = $('.search-history');
        searchHistory.empty();

        if (data['searchHistory'] === null || typeof(data['searchHistory']) === "undefined") {
            searchHistory.append($('<div class="noNews">' +
                '这里没有历史记录呀 0_0' +
                '</div>'));
        } else {
            $.each(data['searchHistory'], function (index, obj) {

                var value = obj['value'];

                if (value.length > 13) {
                    var valueSub0To13 = value.substring(0, 13);

                    searchHistory.append($('<div class="cwz-history" id="h' + obj['key'] + '">' +
                        '<span class="am-icon-clock-o" style="margin-right: 5px"></span>' +
                        '<span class="keyWordsValue" style="font-size: 14px;" title="' + obj['value'] + '">' + valueSub0To13 + '. . .</span>' +
                        '<span class="delete-history am-icon-times"></span>' +
                        '</div>'));

                } else {
                    searchHistory.append($('<div class="cwz-history" id="h' + obj['key'] + '">' +
                        '<span class="am-icon-clock-o" style="margin-right: 5px"></span>' +
                        '<span class="keyWordsValue" style="font-size: 14px;" title="' + obj['value'] + '">' + obj['value'] + '</span>' +
                        '<span class="delete-history am-icon-times"></span>' +
                        '</div>'));

                }

            });

            $('.cwz-history').click(function () {
                var keyWords = $(this).find('.keyWordsValue').attr("title");
                window.open("/search?keyWords=" + keyWords);
            });

            $('.delete-history').click(function () {
                var $this = $(this);
                var key = $this.parent().attr("id").substring(1);

                $.ajax({
                   type: 'GET',
                    url: '/deleteOneHistory',
                    dataType: 'json',
                    async: false,
                    data: {
                        key: key
                    },
                    success: function (data) {
                        $this.parent().remove();
                    },
                    error: function () {
                        dangerNotice("删除搜索历史失败");
                    }
                });

                return false;
            });
        }

    }

    $('.span-history').click(function () {
        var cwzHistory = $('.search-history').find('.cwz-history');

        if (cwzHistory.length > 0) {
            $('#deleteAllHistory').modal('open');
        } else {
            var tie = $('.noNews').html();
            if (tie === "你还未登录，无法查看搜索历史") {
                dangerNotice("你还未登录，无法清空搜索历史");

            } else if (tie === "这里没有历史记录呀 0_0") {
                dangerNotice("你已经清空过历史啦");

            } else {
                dangerNotice("清空搜索历史失败");
            }

        }

    });

    $('.sureAllHistoryDeleteBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/deleteAllHistory',
            dataType: 'json',
            data: {
            },
            success: function (data) {
                if (data['status'] === 101) {
                    dangerNotice("你还未登录，无法清空搜索历史");
                } else {
                    successNotice("清空搜索历史成功");
                    ajaxHistorySearch();
                }
            },
            error: function () {
                dangerNotice("清空搜索历史失败")
            }
        });
    });


    // 获取搜索历史
    function ajaxHistorySearch() {
        $.ajax({
           type: 'GET',
            url: '/searchHistory',
            dataType: 'json',
            data: {
            },
            success: function (data) {
                if (data['status'] === 101) {
                    var searchHistory = $('.search-history');
                    searchHistory.empty();
                    searchHistory.append($('<div class="noNews">' +
                        '你还未登录，无法查看搜索历史' +
                        '</div>'));
                } else {
                    PutInHistorySearch(data['data']);
                }
            },
            error: function () {
                alert("获取搜索历史");
            }

        });
    }

    ajaxHistorySearch();


    // 填充文章搜索的信息
    function putInSearchArticleInfo(data, selectTimeRange) {
        var articleTimeline = $('.articleTimeline');
        articleTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');
        timeLine.append($('<div class="timeline-row">' +
            '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
            '<i class="am-icon-file-text"></i>' +
            '</span>' +
            '<h1 class="title am-animation-slide-top">共计 ' + data['count'] +
            ' 个结果</h1>' +

            '<select id="select-publicDate">' +
            '<option class="publicDateOption" value="all">时间不限</option>' +
            '<option class="publicDateOption" value="oneDay">最近一天</option>' +
            '<option class="publicDateOption" value="oneWeek">最近一周</option>' +
            '<option class="publicDateOption" value="oneMonth">最近一月</option>' +
            '<option class="publicDateOption" value="threeMonth">最近三月</option>' +
            '</select>' +

            '</div>'));

        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '什么都没搜到啊 T_T' +
                '</div>'));
            articleTimeline.append(timeLine);

        } else {

            $.each(data['result'], function (index, obj) {

                var timelineRowMajor = $('<div class="timeline-row-major"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');
                content.append($('<header class="am-comment-hd" style="background: #fff">' +
                    '<div class="contentTitle am-comment-meta">' +
                    '<a href="/article/' + obj['articleId'] + '" title="' + obj['articleTitle'] + '" target="_blank">' + obj['articleTitle'] + '</a>' +
                    '</div>' +
                    '</header>'));

                var amCommentBd = $('<div class="am-comment-bd"></div>');
                amCommentBd.append($('<i class="am-icon-calendar"> <a href="/archives?archiveDay=' + obj['publishDateForThree'] + '" title="' + obj['publishDate'] + '" target="_blank">' + obj['publishDate'] + '</a></i>' +
                    '<i class="am-icon-user"> <a href="/person?personName=' + obj['author'] + '" title="' + obj['author'] + '" target="_blank">' + obj['author'] + '</a></i>' +
                    '<i class="am-icon-folder"> <a href="/categories?categoryName=' + obj['articleCategories'] + '" title="' + obj['articleCategories'] + '" target="_blank">' + obj['articleCategories'] + '</a></i>'));

                var amCommentBdTags = $('<i class="am-comment-bd-tags am-icon-tag"></i>');
                for (var i = 0; i < obj['articleTags'].length; i++) {
                    var tag = $('<a href="/tags?tag=' + obj['articleTags'][i] + '" title="' + obj['articleTags'][i] + '" target="_blank">' + obj['articleTags'][i] + '</a>');
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
                '<div id="searchArticlePagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

        var selectPublicDate = $('#select-publicDate');
        var publicDateOption = selectPublicDate.find('.publicDateOption');

        if (selectTimeRange === "all") {
            publicDateOption[0].selected = true;
        } else if (selectTimeRange === "oneDay") {
            publicDateOption[1].selected = true;
        } else if (selectTimeRange === "oneWeek") {
            publicDateOption[2].selected = true;
        } else if (selectTimeRange === "oneMonth") {
            publicDateOption[3].selected = true;
        } else if (selectTimeRange === "threeMonth") {
            publicDateOption[4].selected = true;
        } else {
            publicDateOption[0].selected = true;
        }

        selectPublicDate.change(function () {
            timeRange = selectPublicDate.val();
            ajaxSearchArticle(1);
            selectPublicDate.val(timeRange);

        });


    }

    // 获取文章搜索请求
    function ajaxSearchArticle(currentPage) {
        console.log(timeRange);
        $.ajax({
            type: 'POST',
            url: '/searchByKeyWords',
            dataType: 'json',
            data:{
                keyWords: searchKeyWordsValue,
                timeRange : timeRange,
                type: 1,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInSearchArticleInfo(data['data']['articleJSON'], timeRange);
                scrollTo(0,0);//回到顶部

                //分页
                $("#searchArticlePagination").paging({
                    rows: data['data']['articleJSON']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['articleJSON']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['articleJSON']['pageInfo']['pages'],//总页数
                    total: data['data']['articleJSON']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxSearchArticle(currentPage);
                    }
                });

                var searchKeyWords =  data['data']['searchKeyWords'];

                if (searchKeyWords.length > 28) {
                    var substring0To28 = searchKeyWords.substring(0, 28);
                    $('.searchResult').html('以下是【 '+ substring0To28 +'. . . 】的搜索结果');
                    searchKeyWordsContent.val(substring0To28);

                    var substring28To30 = searchKeyWords.substring(28, 30);
                    $('.categories-title').append($('<span style="color: #999; font-size: 15px">【 ' + substring28To30 + ' 】' +
                        '及其后面的字符均被忽略，因为本博客的查询限制在28个字符以内。</span>'));

                } else {

                    $('.searchResult').html('以下是【 '+ searchKeyWords +' 】的搜索结果');
                    searchKeyWordsContent.val(searchKeyWords);
                }

            },
            error: function () {
                alert("获取文章搜索失败哦");
            }
        });
    }

    ajaxSearchArticle(1);


    // 填充用户搜索的信息
    function putInSearchUserInfo(data) {
        var userTimeline = $('.userTimeline');
        userTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');
        timeLine.append($('<div class="timeline-row">' +
            '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
            '<i class="am-icon-user"></i>' +
            '</span>' +
            '<h1 class="title am-animation-slide-top">共计 ' + data['count'] +
            ' 个结果</h1>' +
            '</div>'));

        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '什么都没搜到啊 T_T' +
                '</div>'));
            userTimeline.append(timeLine);

        } else {

            $.each(data['result'], function (index, obj) {

                var personalBrief;
                if (obj['personalBrief'] === null || obj['personalBrief'] === "" || typeof(obj['personalBrief']) === "undefined" ) {
                    personalBrief = "朋友，不必太纠结于当下，也不必太忧虑未来，当你经历过一些事情的时候，眼前的风景已经和从前不一样了。";
                } else {
                    personalBrief = obj['personalBrief'];
                }

                var timelineRowMajor = $('<div class="timeline-row-major" id="u'+ obj['userId'] +'"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');

                if (obj['userId'] === data['myUserId']) {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['avatarImgUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/person?personName=' + obj['username'] + '" title="' + obj['username'] + '" target="_blank">'+ obj['username'] + '</a></h1>' +

                        '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                        '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                        '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                        '<a href="/user" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm">前往个人主页' +
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
                            '<img src="' + obj['avatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['username'] + '" title="' + obj['username'] + '" target="_blank">'+ obj['username'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="personAttentionBtn am-btn am-btn-secondary am-btn-sm" disabled>' +
                            '<span class="am-icon-check"></span>&nbsp;已关注' +
                            '</button>' +
                            '<br>' +
                            '<div class="myAttentionDisplay">' +
                            '<span>'+ personalBrief +'</span>' +
                            '</div>' +
                            '</div>'));
                    } else {
                        content.append($('<div style="min-height: 90px">' +
                            '<div class="personImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                            '<img src="' + obj['avatarImgUrl'] + '" style="object-fit: cover">' +
                            '</div>' +
                            '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                            '<a href="/person?personName=' + obj['username'] + '" title="' + obj['username'] + '" target="_blank">'+ obj['username'] + '</a></h1>' +

                            '<span class="userContentInfoFirst">关注<strong style="font-size: 15px"> ' + obj['countAttention'] + '</strong></span>' +
                            '<span class="userContentInfo">粉丝<strong style="font-size: 15px"> ' + obj['countFan'] + '</strong></span>' +
                            '<span class="userContentInfoLast">文章<strong style="font-size: 15px"> ' + obj['articleNum'] + '</strong></span>' +

                            '<button class="personAttentionBtn am-btn am-btn-default am-btn-sm">' +
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

            userTimeline.append(timeLine);
            userTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="searchUserPagination">' +
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

                    } else if (data['status'] === 1402) {
                        dangerNotice("关注用户失败");
                    }
                },
                error: function () {
                    alert("关注用户失败")
                }
            });
        });

    }

    // 获取用户搜索请求
    function ajaxSearchUser(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/searchByKeyWords',
            dataType: 'json',
            data:{
                keyWords: searchKeyWordsValue,
                type: 2,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInSearchUserInfo(data['data']['userJSON']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#searchUserPagination").paging({
                    rows: data['data']['userJSON']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['userJSON']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['userJSON']['pageInfo']['pages'],//总页数
                    total: data['data']['userJSON']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxSearchUser(currentPage);
                    }
                });

            },
            error: function () {
                alert("获取用户搜索失败哦");
            }
        });
    }

    ajaxSearchUser(1);


    // 填充标签搜索的信息
    function putInSearchTagsInfo(data) {
        var tagsTimeline = $('.tagsTimeline');
        tagsTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');
        timeLine.append($('<div class="timeline-row">' +
            '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
            '<i class="am-icon-tags"></i>' +
            '</span>' +
            '<h1 class="title am-animation-slide-top">共计 ' + data['count'] +
            ' 个结果</h1>' +
            '</div>'));

        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '什么都没搜到啊 T_T' +
                '</div>'));
            tagsTimeline.append(timeLine);

        } else {

            $.each(data['result'], function (index, obj) {

                var timelineRowMajor = $('<div class="timeline-row-major" id="t'+ obj['id'] +'"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');

                if (obj['imageUrl'] === null || typeof(obj['imageUrl']) === "undefined") {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<div class="categoryDiv"><div style="color: white"><i class="am-icon-tags"></i></div></div>' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/tags?tag=' + obj['tagName'] + '" title="' + obj['tagName'] + '" target="_blank">'+ obj['tagName'] + '</a></h1>' +
                        '<a href="/tags?tag=' + obj['tagName'] + '" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm">前往查看' +
                        '</button></a>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<n>共计有 <strong style="font-size: 15px">'+ obj['tagArticleCountNum'] +'</strong> 篇文章</span>' +
                        '</div>' +
                        '</div>'));

                } else {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="tagsImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['imageUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/tags?tag=' + obj['tagName'] + '" title="' + obj['tagName'] + '" target="_blank">'+ obj['tagName'] + '</a></h1>' +
                        '<a href="/tags?tag=' + obj['tagName'] + '" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm">前往查看' +
                        '</button></a>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<n>共计有 <strong style="font-size: 15px">'+ obj['tagArticleCountNum'] +'</strong> 篇文章</span>' +
                        '</div>' +
                        '</div>'));

                }

                timelineRowMajor.append(content);
                timeLine.append(timelineRowMajor);

            });

            tagsTimeline.append(timeLine);
            tagsTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="searchTagsPagination">' +
                '<ul class="am-pagination am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

    }

    // 获取标签搜索请求
    function ajaxSearchTags(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/searchByKeyWords',
            dataType: 'json',
            data:{
                keyWords: searchKeyWordsValue,
                type: 3,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInSearchTagsInfo(data['data']['tagsJSON']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#searchTagsPagination").paging({
                    rows: data['data']['tagsJSON']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['tagsJSON']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['tagsJSON']['pageInfo']['pages'],//总页数
                    total: data['data']['tagsJSON']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxSearchTags(currentPage);
                    }
                });

            },
            error: function () {
                alert("获取标签搜索失败哦");
            }
        });
    }

    ajaxSearchTags(1);


    // 填充分类搜索的信息
    function putInSearchCategoriesInfo(data) {
        var categoryTimeline = $('.categoryTimeline');
        categoryTimeline.empty();

        var timeLine = $('<div class="timeline timeline-wrap"></div>');
        timeLine.append($('<div class="timeline-row">' +
            '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
            '<i class="am-icon-th-large"></i>' +
            '</span>' +
            '<h1 class="title am-animation-slide-top">共计 ' + data['count'] +
            ' 个结果</h1>' +
            '</div>'));

        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '什么都没搜到啊 T_T' +
                '</div>'));
            categoryTimeline.append(timeLine);

        } else {

            $.each(data['result'], function (index, obj) {

                var timelineRowMajor = $('<div class="timeline-row-major" id="c'+ obj['id'] +'"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');

                if (obj['imageUrl'] === null || typeof(obj['imageUrl']) === "undefined") {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<div class="categoryDiv"><div style="color: white"><i class="am-icon-th-large"></i></div></div>' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/categories?categoryName=' + obj['categoryName'] + '" title="' + obj['categoryName'] + '" target="_blank">'+ obj['categoryName'] + '</a></h1>' +
                        '<a href="/categories?categoryName=' + obj['categoryName'] + '" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm">前往查看' +
                        '</button></a>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<n>共计有 <strong style="font-size: 15px">'+ obj['articleNum'] +'</strong> 篇文章</span>' +
                        '</div>' +
                        '</div>'));

                } else {
                    content.append($('<div style="min-height: 90px">' +
                        '<div class="tagsImg am-u-sm-2 am-u-lg-1" style="margin-top: 15px">' +
                        '<img src="' + obj['imageUrl'] + '" style="object-fit: cover">' +
                        '</div>' +
                        '<h1 class="title am-animation-slide-top" style="margin-left: 60px; margin-bottom: 5px; margin-top: 15px">' +
                        '<a href="/categories?categoryName=' + obj['categoryName'] + '" title="' + obj['categoryName'] + '" target="_blank">'+ obj['categoryName'] + '</a></h1>' +
                        '<a href="/categories?categoryName=' + obj['categoryName'] + '" target="_blank"><button class="myselfPersonAttentionBtn am-btn am-btn-default am-btn-sm">前往查看' +
                        '</button></a>' +
                        '<br>' +
                        '<div class="myAttentionDisplay">' +
                        '<n>共计有 <strong style="font-size: 15px">'+ obj['articleNum'] +'</strong> 篇文章</span>' +
                        '</div>' +
                        '</div>'));

                }

                timelineRowMajor.append(content);
                timeLine.append(timelineRowMajor);

            });

            categoryTimeline.append(timeLine);
            categoryTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="searchCategoryPagination">' +
                '<ul class="am-pagination am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

    }

    // 获取分类搜索请求
    function ajaxSearchCategory(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/searchByKeyWords',
            dataType: 'json',
            data:{
                keyWords: searchKeyWordsValue,
                type: 4,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInSearchCategoriesInfo(data['data']['categoriesJSON']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#searchCategoryPagination").paging({
                    rows: data['data']['categoriesJSON']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['categoriesJSON']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['categoriesJSON']['pageInfo']['pages'],//总页数
                    total: data['data']['categoriesJSON']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxSearchCategory(currentPage);
                    }
                });

            },
            error: function () {
                alert("获取分类搜索失败哦");
            }
        });
    }

    ajaxSearchCategory(1);

    searchKeyWordsBtn.click(function () {
        search()
    });


    function search() {
        var searchKeyWordsContentVal = searchKeyWordsContent.val();
        if (searchKeyWordsContentVal === null || searchKeyWordsContentVal === "" || searchKeyWordsContentVal.length === 0) {
            dangerNotice("搜索内容不能为空")
        } else {

            window.location.replace("/search?keyWords=" + searchKeyWordsContentVal);
        }
    }

});
