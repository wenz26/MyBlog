$(function () {
    var category = "";
    var timeRange = "";

    // 填充标签文章信息
    function putInTagArticleInfo(data, selectTimeRange, categoryName) {
        var categoryTimeline = $('.categoryTimeline');

        categoryTimeline.empty();
        var timeLine = $('<div class="timeline timeline-wrap"></div>');
        timeLine.append($('<div class="timeline-row">' +
            '<span class="node" style="-webkit-box-sizing: content-box;-moz-box-sizing: content-box;box-sizing: content-box;">' +
            '<i class="am-icon-folder"></i>' +
            '</span>' +
            '<h1 class="title am-animation-slide-top">#' + data['category'] + '</h1>' +

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
                '这里空空如也' +
                '</div>'));
            categoryTimeline.append(timeLine);

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
            categoryTimeline.append($('<div class="my-row" id="page-father">' +
                '<div id="pagination">' +
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
            ajaxFirst(1, categoryName);
            selectPublicDate.val(timeRange);

        });
    }

    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url : window.location.href,
        async:false,
        success: function (data, status, xhr) {
            category = xhr.getResponseHeader("categoryName");
        }
    });

    function ajaxFirst(currentPage, categoryName) {
        $.ajax({
            type:'GET',
            url:'/getCategoryArticle',
            dataType:'json',
            data: {
                categoryName: categoryName,
                timeRange: timeRange,
                rows: "8",
                pageNum: currentPage
            },
            success: function (data) {
                putInTagArticleInfo(data['data'], timeRange, categoryName);
                scrollTo(0,0);//回到顶部

                //分页
                $("#pagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxFirst(currentPage, categoryName);
                    }
                });

            },
            error: function () {
                alert("获取分类文章失败");
            }
        });
    }


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
                timeRange = "";
                ajaxFirst(1, categoryName);
            })
        },
        error: function () {
            alert("获取分类信息失败");
        }
    });

    // 分类上的时间可选，标签可选（待办）


});
