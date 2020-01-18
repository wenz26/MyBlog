$(function () {
    var category = "";

    var i = 1;
    var id;
    // 填充标签文章信息
    function putInTagArticleInfo(data) {
        var categoryTimeline = $('.categoryTimeline');
        var timeLine;

        if (i === 1) {
            categoryTimeline.empty();
            timeLine = $('<div class="timeline timeline-wrap"></div>');
            timeLine.append($('<div class="timeline-row">' +
                '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
                '<i class="am-icon-folder"></i>' +
                '</span>' +
                '<h1 class="title  am-animation-slide-top">#' + data['category'] +

                '<div class="am-topbar-form am-topbar-left am-form-inline am-topbar-right" style="margin-top: 0; margin-left: 20px">' +
                '<div class="am-form-group" style="vertical-align: center">' +
                '<input type="date" class="am-form-field am-input-sm" style="border-radius: 6px">' +
                '</div>' +
                '<button class="am-btn am-btn-default am-btn-sm" style="border-radius: 6px">搜索</button>' +
                '</div>' +

                '</h1>' +
                '</div>'));
        } else {
            timeLine = $('.timeline');
            $('.title').removeClass("am-animation-slide-top");
            $('.node').removeClass("am-animation-slide-top");
            $('.content').removeClass("am-animation-slide-top");
            $('.categories-comment').removeClass("am-animation-slide-top");
        }

        $.each(data['result'], function (index, obj) {
            if (index === 0 && obj['articleId'] === id) {
                //alert(id);
                return true;
            }

            if (index === (data['result'].length) - 1) {
                id = obj['articleId'];
            }


            var timelineRowMajor = $('<div class="timeline-row-major"></div>');
            timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

            var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1"></div>');
            content.append($('<header class="am-comment-hd" style="background: #fff">' +
                '<div class="contentTitle am-comment-meta">' +
                '<a href="/article/' + obj['articleId'] + '">' + obj['articleTitle'] + '</a>' +
                '</div>' +
                '</header>'));

            var amCommentBd = $('<div class="am-comment-bd"></div>');
            amCommentBd.append($('<i class="am-icon-calendar"> <a href="/archives?archiveDay=' + obj['publishDateForThree'] + '">' + obj['publishDate'] + '</a></i>' +
                '<i class="am-icon-user"> ' + obj['author'] + '</i>' +
                '<i class="am-icon-folder"> <a href="/categories?categoryName=' + obj['articleCategories'] + '">' + obj['articleCategories'] + '</a></i>'));

            var amCommentBdTags = $('<i class="am-comment-bd-tags am-icon-tag"></i>');
            for (var i = 0; i < obj['articleTags'].length; i++) {
                var tag = $('<a href="/tags?tag=' + obj['articleTags'][i] + '">' + obj['articleTags'][i] + '</a>');
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
        // categoryTimeline.append($('<div>' +
        //     '<button type="button" class="am-btn am-btn-default am-btn-block am-round" id="readMoreCategories">' +
        //     '阅读更多<span class="am-icon-angle-double-down"></span></button>' +
        //     '</div>'));
    }

    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url : window.location.href,
        async:false,
        success: function (data, status, xhr) {
            category = xhr.getResponseHeader("categoryName");
        }
    });

    var categoriesPages;
    var toMorecategoryName;
    function ajaxFirst(currentPage,categoryName) {
        toMorecategoryName = categoryName;
        $.ajax({
            type:'GET',
            url:'/getCategoryArticle',
            dataType:'json',
            data: {
                categoryName: categoryName,
                rows: "15",
                pageNum: currentPage
            },
            success: function (data) {
                putInTagArticleInfo(data['data']);
                i++;

                categoriesPages = data['data']['pageInfo']['pages'];//总页数
            },
            error: function () {
                alert("获取分类文章失败");
            }
        });
    }

    var readMoreCategories = $('#readMoreCategories');
    readMoreCategories.click(function () {
        //alert(i);
        if (i <= categoriesPages) {
            readMoreCategories.attr('disabled',false);
            ajaxFirst(i, toMorecategoryName);
        } else {
            readMoreCategories.attr('disabled',true);
            readMoreCategories.html("文章已经全部加载完毕咯~~~");
        }
    });

    ajaxFirst(1, category);

    $.ajax({
        type: 'GET',
        url: '/findCategoriesNameAndArticleNum',
        dataType: 'json',
        data:{
        },
        success: function (data) {
            var categories = $('.categories');
            categories.empty();

            categories.append($('<div class="categories-title">' +
                '<h3 style="font-size: 20px">分类信息</h3>' +
                '</div>'));

            var totalNum = 0;
            var categoriesComment = $('<div class="categories-comment am-animation-slide-top"></div>');
            $.each(data['data']['result'], function (index, obj) {
                categoriesComment.append($('<div class="category">' +
                    '<span><a class="categoryName">' + obj['categoryName'] + '</a><span class="categoryNum">(' + obj['categoryArticleCountNum'] + ')</span></span>' +
                    '</div>'));
                totalNum += obj['categoryArticleCountNum'];
            });
            categoriesComment.append($('<div class="category">' +
                '<span>共计<span class="categoryNum"><strong style="font-size: 22px">' + totalNum + '</strong></span>篇文章哦</span>' +
                '</div>'));

            categories.append(categoriesComment);

            $('.categoryName').click(function () {
                var $this = $(this);
                var categoryName = $this.html();

                i = 1;
                ajaxFirst(1, categoryName);
                readMoreCategories.attr('disabled',false);
                readMoreCategories.html('阅读更多<span class="am-icon-angle-double-down"></span>');
            })
        },
        error: function () {
            alert("获取分类信息失败");
        }
    });

    // 分类上的时间可选，标签可选（待办）


});
