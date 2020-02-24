$(function () {
    var tag = "";
    var timeRange = "";
    var category = "";
    var categories;

    // 添加所有标签
    function putAllTags(data) {
        var amContainer = $('.am-container');
        amContainer.empty();

        var adminContent = $('<div class="admin-content"></div>');
        adminContent.append('<div class="am-cf am-padding">' +
            '<strong class="am-text-primary am-text-lg">标签</strong> / <small>Tags</small>' +
            '<h1 class="title am-animation-slide-top" style="font-size: 17px; margin-left: 70%; margin-bottom: 0">' +
            '目前总计有<span class="archivesNum">' + data['tagsCount'] + '</span>个标签哦~~' +
            '</h1>' +
            '</div>');

        var tags = $('<div id="tags"></div>');
        adminContent.append(tags);
        amContainer.append(adminContent);

        var tags_ul = $('<ul class="sm-block-grid-2 md-block-grid-4 lg-block-grid-4 am-margin gallery-list"></ul>');

        $.each(data['result'], function (index, obj) {
            tags_ul.append($('<li style="padding-bottom: 15px">' +
                '<a href="/tags?tag=' + obj['tagName'] + '">' +
                '<img class="am-img-thumbnail am-img-bdrs" src="' + obj['imageUrl'] + '" alt="' + obj['tagName'] + '" style="object-fit: cover; width: 250px; height: 250px" />' +
                '<div class="gallery-title"><strong style="font-size: 16px">' + obj['tagName'] + '</strong></div>' +
                '<div class="gallery-desc">该标签有<strong style="font-size: 16px">' + obj['tagArticleCountNum'] + '</strong>篇文章</div>' +
                '</a>' +
                '</li>'));
        });
        tags.append(tags_ul);
        tags.append($('<div class="my-row" id="page-father">' +
            '<div id="pagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));
    }

    // 添加该标签的所有文章信息
    function putInTagArticleInfo(data, selectTimeRange, selectCategory, categories) {
        var amContainer = $('.am-container');
        amContainer.empty();

        var amG = $('<div class="am-g"></div>');
        var siteInner = $('<div class="site-inner"></div>');

        amG.append(siteInner);
        amContainer.append(amG);

        var timeLine = $('<div class="timeline timeline-wrap"></div>');

        timeLine.append($('<div class="timeline-row">' +
            '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
            '<i class="am-icon-tag"></i>' +
            '</span>' +
            '<h1 class="title am-animation-slide-top"># '+ data['tag'] + '</h1>' +
            '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['tagArticleNum'] + '</strong>&nbsp;篇文章)</span>' +

            '<select id="select-publicDate">' +
            '<option class="publicDateOption" value="all">时间不限</option>' +
            '<option class="publicDateOption" value="oneDay">最近一天</option>' +
            '<option class="publicDateOption" value="oneWeek">最近一周</option>' +
            '<option class="publicDateOption" value="oneMonth">最近一月</option>' +
            '<option class="publicDateOption" value="threeMonth">最近三月</option>' +
            '</select>' +

            '<select id="select-categories">' +
            '</select>' +

            '</div>'));


        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
            siteInner.append(timeLine);

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
                    '<i class="am-icon-user"> <a href="/person?personName=' + obj['author'] + '" title="' + obj['author'] + '">' + obj['author'] + '</a></i>' +
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

            siteInner.append(timeLine);
            siteInner.append($('<div class="my-row" id="page-father">' +
                '<div id="pagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }


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

        selectCategories.change(function () {
            category = selectCategories.val();
            ajaxArticleByTag(1);
            selectCategories.val(category);
        });



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
            ajaxArticleByTag(1);
            selectPublicDate.val(timeRange);

        });

    }

    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url: window.location.href,
        async: false,
        success: function (data, status, xhr) {
            tag = xhr.getResponseHeader("tag");
        }
    });

    // 加载tags页时请求
    function ajaxFirst(currentPage) {
        $.ajax({
            type: 'GET',
            url: '/findAllTags',
            dataType: 'json',
            data:{
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putAllTags(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#pagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxFirst(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取标签失败哦");
            }
        });
    }

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

    // 获取某个标签的文章值
    function ajaxArticleByTag(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getTagArticle',
            dataType: 'json',
            data:{
                tag: tag,
                timeRange: timeRange,
                category: category,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInTagArticleInfo(data['data'], timeRange, category, categories);
                scrollTo(0,0);//回到顶部

                //分页
                $("#pagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxArticleByTag(currentPage);
                    }
                });

            },
            error: function () {
                alert("获取某个标签的文章失败")
            }
        });
    }

    // 进入页面时看看是不是 获取标签的请求
    if (tag !== null && tag !== "" && typeof(tag) !== "undefined") {
        ajaxArticleByTag(1);
    } else {
        ajaxFirst(1);
    }

});
