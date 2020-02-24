$(function () {
    var archive="";

    var i = 1;
    var id;
    var oldArray;
    // 填充归档文章
    function putInArchivesArticleInfo(data) {
        var categoryTimeline = $('.categoryTimeline');
        var timeLine;
        var strArray;

        if (i === 1) {
            categoryTimeline.empty();
            timeLine = $('<div class="timeline timeline-wrap"></div>');
            timeLine.append('<div class="timeline-row">' +
                '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
                '<i class="am-icon-calendar"></i>' +
                '</span>' +
                '<h1 class="title  am-animation-slide-top"># o(∩_∩)o</h1>' +
                '</div>');
            strArray = new Array();
        } else {
            timeLine = $('.timeline');
            $('.title').removeClass("am-animation-slide-top");
            $('.node').removeClass("am-animation-slide-top");
            $('.nodeYear').removeClass("am-animation-slide-top");
            $('.content').removeClass("am-animation-slide-top");
            $('.categories-comment').removeClass("am-animation-slide-top");
            strArray = oldArray;
        }

        $.each(data['result'], function (index, obj) {
            if (index === 0 && obj['articleId'] === id) {
                //alert(id);
                return true;
            }

            if (index === (data['result'].length) - 1) {
                id = obj['articleId'];
                oldArray = strArray;
                //alert(oldArray.toString())
            }

            var year = obj['publishDateForThree'].substring(0, 4);
            var month = obj['publishDateForThree'].substring(5, 7);

            if (data['showMonth'] === 'hide') {
                // $.inArray() 函数用于在数组中查找指定值，并返回它的索引值（如果没有找到，则返回-1）
                if ($.inArray(year, strArray) == -1) {

                    strArray.push(year);
                    timeLine.append($('<div class="timeline-row-major">' +
                        '<span class="node am-animation-slide-top am-animation-delay-1"></span>' +
                        '<div class="nodeYear am-animation-slide-top am-animation-delay-1">' + year + '年</div>' +
                        '</div>'));
                }
            } else {
                if ($.inArray(year, strArray) == -1) {

                    strArray.push(year);
                    timeLine.append($('<div class="timeline-row-major">' +
                        '<span class="node am-animation-slide-top am-animation-delay-1"></span>' +
                        '<div class="nodeYear am-animation-slide-top am-animation-delay-1">' + year + '年&nbsp;' + month + '月</div>' +
                        '</div>'));
                }
            }

            var timelineRowMajor = $('<div class="timeline-row-major"></div>');
            timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

            var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');
            content.append($('<header class="am-comment-hd" style="background: #fff">' +
                '<div class="contentTitle am-comment-meta">' +
                '<a href="/article/' + obj['articleId'] + '" title="' + obj['articleTitle'] + '" target="_blank">' + obj['articleTitle'] + '</a>' +
                '</div>' +
                '</header>'));

            var amCommentBd = $('<div class="am-comment-bd"></div>');
            amCommentBd.append($('<i class="am-icon-calendar"> <a href="/archives?archiveDay=' + obj['publishDateForThree'] + '" title="' + obj['publishDate'] + '">' + obj['publishDate'] + '</a></i>' +
                '<i class="am-icon-user"> <a href="/person?personName=' + obj['author'] + '" title="' + obj['author'] + '">' + obj['author'] + '</a></i>' +
                '<i class="am-icon-folder"> <a href="/categories?categoryName=' + obj['articleCategories'] + '" title="' + obj['articleCategories'] + '">' + obj['articleCategories'] + '</a></i>'));

            var amCommentBdTags = $('<i class="am-comment-bd-tags am-icon-tag"></i>');
            for (var i = 0; i < obj['articleTags'].length; i++) {
                var tag = $('<a href="/tags?tag=' + obj['articleTags'][i] + '" title="' + obj['articleTags'][i] + '">' + obj['articleTags'][i] + '</a>');
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

        categoryTimeline.append(timeLine);
    }

    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url: window.location.href,
        async: false,
        success: function (data, status, xhr) {
            archive = xhr.getResponseHeader("archiveDay");
        }
    });

    var archivePages;
    var toMoreArchiveDay;
    function ajaxFirst(currentPage,archiveDay) {
        toMoreArchiveDay = archiveDay;
        $.ajax({
            type: 'GET',
            url: '/getArchiveArticle',
            dataType: 'json',
            data: {
                archiveDay: archiveDay,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInArchivesArticleInfo(data['data']);
                i++;

                archivePages = data['data']['pageInfo']['pages']; //总页数
            },
            error:function () {
                alert("获取归档文章失败");
            }
        });
    }

    var readMoreArchives = $('#readMoreArchives');
    readMoreArchives.click(function () {
        //alert(i);
        if (i <= archivePages) {
            readMoreArchives.attr('disabled',false);
            ajaxFirst(i, toMoreArchiveDay);
        } else {
            readMoreArchives.attr('disabled',true);
            readMoreArchives.html("文章已经全部加载完毕咯~~~");
        }
    });

    ajaxFirst(1, archive);

    // 获得归档日期以及该归档日期下的文章数量
    $.ajax({
        type: 'GET',
        url: '/findArchiveNameAndArticleNum',
        dataType: 'json',
        data:{
        },
        success: function (data) {
            var categories = $('.categories');
            categories.empty();

            categories.append($('<div class="categories-title">' +
                '<h3 style="font-size: 20px">归档信息</h3>' +
                '</div>'));

            var totalNum = 0;
            var categoriesComment = $('<div class="categories-comment am-animation-slide-top"></div>');
            $.each(data['data']['result'], function (index, obj) {
                categoriesComment.append($('<div class="category">' +
                    '<span><a class="categoryName">' + obj['archiveDate'] + '</a><span class="categoryNum">(' + obj['archiveArticleNum'] + ')</span></span>' +
                    '</div>'));
                totalNum += obj['archiveArticleNum'];
            });
            categoriesComment.append($('<div class="category">' +
                '<span>共计<span class="categoryNum"><strong style="font-size: 22px">' + totalNum + '</strong></span>篇文章哦</span>' +
                '</div>'));

            categories.append(categoriesComment);

            $('.categoryName').click(function () {
                var $this = $(this);
                var archiveDay = $this.html();

                i = 1;
                ajaxFirst(1, archiveDay);
                readMoreArchives.attr('disabled',false);
                readMoreArchives.html('阅读更多<span class="am-icon-angle-double-down"></span>');
            })
        },
        error: function () {
            alert("获取归档信息失败");
        }
    });

    // 归档上的其他选择信息可选，分类标签可选（待办）
});
