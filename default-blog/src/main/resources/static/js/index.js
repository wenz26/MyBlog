$(function () {

    //网站最后更新时间（版本更新需更改）
    var siteLastUpdateTime = '2020年1月13日20点';

    //网站开始时间
    var siteBeginRunningTime = '2020-1-13 20:00:00';

    var i = 1;
    var id;
    // 填充文章
    function putInArticle(data) {
        if (i === 1) {
            $('.articles').empty();
        }
        var articles = $('.articles');

        // data代表要遍历的对象,index代表当前循环的次数,obj为当前循环的对象
        $.each(data, function (index, obj) {
            if (index === 0 && obj['articleId'] === id) {
                return true;
            }

            // 因为还有个分页数据 所以要 -1
            if (index !== (data.length) - 1) {

                if (index === (data.length) - 2) {
                    id = obj['articleId'];
                }

                var center = $('<div class="center">' +
                    '<header class="article-header">' +
                    '<h1 itemprop="name">' +
                    '<a class="article-title" href="' + obj['thisArticleUrl'] +'" target="_blank">' + obj['articleTitle'] +'</a>' +
                    '</h1>' +
                    '<div class="article-meta row">' +
                    '<span class="articleType am-badge am-badge-success">' + obj['articleType'] + '</span>' +
                    '<div class="articlePublishDate">' +
                    '<i class="am-icon-calendar"><a class="linkColor" href="/archives?archiveDay=' + obj['publishDateForThree'] + '"> ' + obj['publishDate'] + '</a></i>' +
                    '</div>' +
                    '<div class="originalAuthor">' +
                    '<i class="am-icon-user"> ' + obj['author'] + '</i>' +
                    '</div>' +
                    '<div class="categories">' +
                    '<i class="am-icon-folder"><a class="linkColor" href="/categories?categoryName=' + obj['articleCategories'] + '"> ' + obj['articleCategories'] + '</a></i>' +
                    '</div>' +
                    '</div>' +
                    '</header>' +
                    '<div class="article-entry am-g">' +

                    '<div class="am-u-sm-3" style="padding: 0 0 0 0;">' +
                    '<div class="img-title" style="margin: 10px 10px 0px 10px;">' +

                    '<a href="' + obj['imageUrl'] +'" target="_blank">' +
                    // '<img class="img" src="https://oss.czodly.top/blog/image/article/common/loading.gif" data-src="' + obj['imageUrl'] +'">' +
                    '<img class="img" src="' + obj['imageUrl'] + '" alt="' + obj['articleTitle'] + '">' +
                    '</a>' +

                    '</div>' +
                    '</div>' +
                    '<div class="am-u-sm-9" style="padding: 0 0 0 0;">' +
                    '<div class="article-entry" style="padding: 0 0 0 0; margin: 0 5% 0 2%">' +
                    obj['articleTabloid'] +
                    '</div>' +
                    '</div>' +

                    '</div>' +
                    '<div class="read-all">' +
                    '<a href="' + obj['thisArticleUrl'] + '" target="_blank">阅读全文 <i class="am-icon-angle-double-right"></i></a>' +
                    '</div>' +
                    '<hr>' +

                    '</div>' +
                    '<hr>');

                articles.append(center);

                var articleTags = $('<div class="article-tags"></div>');
                for (var i = 0; i < obj['articleTags'].length; i++) {
                    var articleTag = $('<i class="am-icon-tag"><a class="tag" href="/tags?tag=' + obj['articleTags'][i] + '"> ' + obj['articleTags'][i] + '</a></i>');
                    articleTags.append(articleTag);
                }

                var star = $('<span class="star"><i class="am-icon-star"> ' + obj['favorites'] + '</i></span>');
                articleTags.append(star);


                var likes = $('<span class="likes"><i class="am-icon-heart"> ' + obj['likes'] + '</i></span>');
                articleTags.append(likes);

                var watch = $('<span class="watch"><i class="am-icon-eye"> ' + obj['watchNum'] + '</i></span>');
                articleTags.append(watch);
                center.append(articleTags);
            }

        });

    }


    // 填充最新评论
    function putInNewComment(data) {
        var newComment = $('.new-comment');
        newComment.empty();

        var listNews = $('<div data-am-widget="list_news" class="am-list-news am-list-news-default" ></div>');

        var newCommentTitle = $('<div class="am-list-news-hd am-cf">' +
            '<a class="newComments">' +
            '<h2 style="color: #110101">最新评论</h2>' +
            '</a>' +
            '</div>');
        listNews.append(newCommentTitle);

        var amListNewsBd = $('<div class="am-list-news-bd"></div>');
        var ul = $('<ul class="fiveNewComments am-list"></ul>');

        $.each(data['result'], function (index, obj) {
            var li = $('<li class="am-g am-list-item-dated">' +
                '<a class="newCommentTitle" target="_blank" href="/article/' + obj['articleId']  + '#p' + obj['id'] + '" class="am-list-item-hd" style="padding-bottom: 5px" title="' + obj['articleTitle'] + '">'+ obj['articleTitle'] +'</a>' +
                '<span class="am-list-date">' + obj['commentDate'] + '</span>' +
                '<div class="new-comment-content" style="margin-bottom: 5px;">' + obj['answerer'] + '：' + obj['commentContent'] + '</div>' +
                '</li>');
            ul.append(li);
        });

        amListNewsBd.append(ul);
        listNews.append(amListNewsBd);
        newComment.append(listNews);

        newComment.append($('<div class="my-row" id="page-father">' +
            '<div class="newCommentPagination">' +
            '</div>' +
            '</div>'));

    }

    var articlePages;
    // 首页文章分页请求
    function ajaxFirst(currentPage) {
        //加载时请求
        $.ajax({
            type: 'POST',
            url: '/myPublishArticles',
            dataType: 'json',
            data: {
                rows:"20",
                pageNum:currentPage
            },
            success: function (data) {
                if (data['status'] === 103) {
                    dangerNotice("服务器异常，获得文章信息失败");
                } else {
                    // 放入数据
                    putInArticle(data['data']);
                    i++;
                    // 回到顶部
                    //scrollTo(0,0);

                    var length = data['data'].length;

                    // 分页（用 阅读更多来代替）
                    // 总页数
                    articlePages = data['data'][length-1]['pages'];
                }
            },
            error: function () {
                alert("获得文章信息失败！");
            }
        })
    }

    var readMore = $('#readMore');
    readMore.click(function () {
        if (i <= articlePages) {
            readMore.attr('disabled',false);
            ajaxFirst(i);
        } else {
            readMore.attr('disabled',true);
            readMore.html("文章已经全部加载完毕咯，您也快去创作属于你的故事吧！ ♪(＾∀＾●)ﾉ");
        }
    });

    function newCommentAjax(currentPage) {
        // 最新评论
        $.ajax({
            type: 'GET',
            url: '/newComment',
            dataType: 'json',
            data: {
                rows: "5",
                pageNum: currentPage
            },
            success: function (data) {
                putInNewComment(data['data']);

                // 分页
                $(".newCommentPagination").paging({
                    rows:data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum:data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages:data['data']['pageInfo']['pages'],//总页数
                    total:data['data']['pageInfo']['total'],//总记录数
                    flag:0,
                    callback: function (currentPage) {
                        newCommentAjax(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取评论信息失败！！！")
            }
        });
    }

    ajaxFirst(1);
    newCommentAjax(1);

    // 网站信息
    $.ajax({
        type: 'GET',
        url: '/getSiteInfo',
        dataType: 'json',
        data: {},
        success: function (data) {
            var siteInfo = $('.site-info');
            siteInfo.empty();
            siteInfo.append('<h5 class="site-title">' +
                '<i class="am-icon-info site-icon"></i>' +
                '<strong style="margin-left: 15px">网站信息</strong>' +
                '</h5>');

            var siteDefault = $('<ul class="site-default"></ul>');
            siteDefault.append('<li>' +
                '<i class="am-icon-file site-default-icon"></i><span class="site-default-word">文章总数</span>：' + data['data']['articleNum'] + ' 篇' +
                '</li>');
            siteDefault.append('<li>' +
                '<i class="am-icon-tags site-default-icon"></i><span class="site-default-word">标签总数</span>：' + data['data']['tagsNum'] + ' 个' +
                '</li>');
            siteDefault.append('<li>' +
                '<i class="am-icon-commenting-o site-default-icon"></i><span class="site-default-word">评论总数</span>：' + data['data']['commentNum'] + ' 条' +
                '</li>');
            siteDefault.append('<li>' +
                '<i class="am-icon-hourglass-1 site-default-icon"></i><span class="site-default-word">当前时间</span>：<span class="siteLocalTime"> </span>' +
                '</li>');
            siteDefault.append('<li>' +
                '<i class="am-icon-pencil-square site-default-icon"></i><span class="site-default-word">网站最后更新</span>：<span class="siteUpdateTime">' + siteLastUpdateTime + '</span>' +
                '</li>');
            siteDefault.append('<li>' +
                '<i class="am-icon-calendar site-default-icon"></i><span class="site-default-word">网站运行天数</span>：<span class="siteRunningTime"> </span>' +
                '</li>');
            siteInfo.append(siteDefault);
        },
        error: function () {
        }
    });

    // 网站运行时间
    // beginTime为建站时间的时间戳
    function siteRunningTime(time) {
        var theTime;
        var strTime = "";

        // 1天
        if (time >= 86400) {
            theTime = parseInt(time / 86400);
            strTime += theTime + "天";
            time -= theTime * 86400;
        }

        // 1小时
        if (time >= 3600) {
            theTime = parseInt(time / 3600);
            strTime += theTime + "时";
            time -= theTime * 3600;
        }

        // 1分钟
        if (time >= 60) {
            theTime = parseInt(time / 60);
            strTime += theTime + "分";
            time -= theTime * 60;
        }
        strTime += time + "秒";

        $('.siteRunningTime').html(strTime);
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

        nowTime += nowDay.getDate() + " ";
        nowTime += nowDay.getHours() + ":";

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

        $('.siteLocalTime').html(nowTime);
    }

    var now = new Date();
    var nowDate = now.getTime();

    // 网站开始运行日期
    var oldDate = new Date(siteBeginRunningTime.replace(/-/g, '/'));
    var time = oldDate.getTime();
    var theTime = parseInt((nowDate - time) / 1000);


    setInterval(function () {
        siteRunningTime(theTime);
        theTime++;

        var nowDay = new Date();
        localTime(nowDay);

    }, 1000);

    // 获取用户头像 和 个人简介（名言）
    $.ajax({
        type: 'GET',
        url: '/getUserIndexInfo',
        dataType: 'json',
        data: {},
        success: function (data) {
            var avatarImgUrl = data['data']['avatarImgUrl'];
            $("#avatarImgUrl").attr("src", avatarImgUrl);
            $("#getAvatarImgUrl").attr("href", avatarImgUrl);

            var personalBrief =  data['data']['personalBrief'];
            var personalBriefSmall = $("#personalBrief");
            if (personalBrief === "null") {
                personalBriefSmall.html("朋友，不必太纠结于当下，也不必太忧虑未来，当你经历过一些事情的时候，眼前的风景已经和从前不一样了。")
            } else {
                personalBriefSmall.html(personalBrief);
            }
        },
        error: function () {

        }

    })


    // 获取热门标签 或者 热门的用户信息哦（待办）

});
