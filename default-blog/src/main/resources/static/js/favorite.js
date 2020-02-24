$(function () {
    var favoriteArticleId = "";
    var favoriteArticleTitle = "";
    var articleTitle = "";

    // 添加该用户的所有收藏的文章
    function putInFavoriteArticleInfo(data) {
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
            '<h1 class="title am-animation-slide-top">收藏的文章</h1>' +
            '&nbsp;&nbsp;&nbsp;<span style="font-size: 15px; color: #b0b0b0;">(共计&nbsp;<strong style="font-size: 19px">' + data['articleFavoriteNum'] + '</strong>&nbsp;篇文章)</span>' +
            //'<a href="javascript:;" id="search" title="查询" style="float: right; margin-right: 10px"><span class="am-icon-search"></span></a>' +
            '<button class="am-btn am-btn-default am-round codeButton" style="float: right; margin-left: 10px" id="favoriteReset"><strong>重置</strong></button>' +
            '<button class="am-btn am-btn-secondary am-round codeButton" style="float: right; margin-left: 10px" id="favoriteRetrieve"><strong>查询</strong></button>' +
            '<input class="formInput" type="text" id="articleTitle" placeholder="请输入已收藏文章的标题" style="float: right;"/>' +
            '</div>'));

        if (data['result'].length === 0) {
            timeLine.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
        } else {

            $.each(data['result'], function (index, obj) {

                var timelineRowMajor = $('<div class="timeline-row-major"></div>');
                timelineRowMajor.append($('<span class="node am-animation-slide-top am-animation-delay-1"></span>'));

                var content = $('<div class="content am-comment-main am-animation-slide-top am-animation-delay-1" id="f' + obj['id'] +'"></div>');
                content.append($('<a href="javascript:;" class="cwz-cancel" title="取消收藏"><span class="cancelIcon am-icon-times-circle-o"></span></a>'));

                content.append($('<header class="am-comment-hd" style="background: #fff">' +
                    '<div class="contentTitle am-comment-meta">' +
                    '<a href="/article/' + obj['article']['articleId'] +'" target="_blank" title="' + obj['article']['articleTitle'] + '">' + obj['article']['articleTitle'] + '</a>' +
                    '</div>' +
                    '</header>'));

                var amCommentBd = $('<div class="am-comment-bd"></div>');
                amCommentBd.append($('<i class="am-icon-calendar"> <a href="/archives?archiveDay=' + obj['article']['publishDateForThree'] + '" title="' + obj['article']['publishDate'] + '">' + obj['article']['publishDate'] + '</a></i>' +
                    '<i class="am-icon-user"> <a href="/person?personName=' + obj['article']['author'] + '" title="' + obj['article']['author'] + '">' + obj['article']['author'] + '</a></i>' +
                    '<i class="am-icon-folder"> <a href="/categories?categoryName=' + obj['article']['articleCategories'] + '" title="' + obj['article']['articleCategories'] + '">' + obj['article']['articleCategories'] + '</a></i>'));

                var amCommentBdTags = $('<i class="am-comment-bd-tags am-icon-tag"></i>');
                for (var i = 0; i < obj['article']['articleTags'].length; i++) {
                    var tag = $('<a href="/tags?tag=' + obj['article']['articleTags'][i] + '" title="' + obj['article']['articleTags'][i] + '">' + obj['article']['articleTags'][i] + '</a>');
                    amCommentBdTags.append(tag);

                    if (i !== (obj['article']['articleTags'].length - 1)) {
                        amCommentBdTags.append(",");
                    }
                }

                amCommentBd.append(amCommentBdTags);
                content.append(amCommentBd);
                timelineRowMajor.append(content);
                timeLine.append(timelineRowMajor);
            });
        }

        siteInner.append(timeLine);
        siteInner.append($('<div class="my-row" id="page-father">' +
            '<div id="pagination">' +
            '<ul class="am-pagination  am-pagination-centered">' +
            '</ul>' +
            '</div>' +
            '</div>'));

        /*var cwzCancel = $('.cwz-cancel');
        var cancelIcon = $('.cancelIcon')
        cwzCancel.mouseover(function () {
            cancelIcon.removeClass("am-icon-times-circle-o");
            cancelIcon.addClass("am-icon-times-circle");
        }).mouseout(function () {
            cancelIcon.removeClass("am-icon-times-circle");
            cancelIcon.addClass("am-icon-times-circle-o");
        });*/

        $('.cwz-cancel').click(function () {
            var $this = $(this);
            favoriteArticleId = $this.parent().attr("id").substring(1);
            favoriteArticleTitle = $this.parent().find('.contentTitle').find("a").html();
            //alert(favoriteArticleId + " " + favoriteArticleTitle)

            $('#cancelFavoriteArticleContent').html('你确定要取消文章名为：<span class="am-btn-danger">' +
                favoriteArticleTitle +'</span>&nbsp;的文章收藏吗？<br>取消之后还能再次收藏');
            $('#cancelFavoriteArticleAlter').modal('open');

        });

        $('#favoriteRetrieve').click(function () {
            articleTitle = $('#articleTitle').val();
            if (articleTitle === "" || articleTitle === null || articleTitle.length === 0) {
                alert("查询内容不能为空")
            } else {
                ajaxFavoriteArticle(1);

            }
        });

        $('#favoriteReset').click(function () {
            articleTitle = "";
            ajaxFavoriteArticle(1);
        });
    }

    // 获取用户的所有收藏的文章
    function ajaxFavoriteArticle(currentPage) {
        $.ajax({
            type: 'POST',
            url: '/getFavoriteArticleByUser',
            dataType: 'json',
            async: false,
            data:{
                rows: "8",
                pageNum: currentPage,
                articleTitle: articleTitle
            },
            success: function (data) {
                putInFavoriteArticleInfo(data['data']);
                scrollTo(0,0);//回到顶部

                //分页
                $("#pagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function(currentPage){
                        ajaxFavoriteArticle(currentPage);
                    }
                });

                $('#articleTitle').val(articleTitle);

            },
            error: function () {
                alert("获取用户的所有收藏的文章失败")
            }
        });
    }

    $('.sureCancelFavoriteArticleBtn').click(function () {
        $.ajax({
            type: 'GET',
            url: '/cancelFavoriteArticle',
            dataType: 'json',
            data:{
                id: favoriteArticleId
            },
            success: function (data) {
                successNotice("取消收藏文章成功");
                ajaxFavoriteArticle(1);
            },
            error: function () {
                alert("取消收藏文章失败")
            }
        });
    });

    ajaxFavoriteArticle(1);



    // 设置日期选择框不可选择今天过后的日期
    /*function checkDate() {
        var nowTemp = new Date();
        var nowDay = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0).valueOf();
        var nowMoth = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), 1, 0, 0, 0, 0).valueOf();
        var nowYear = new Date(nowTemp.getFullYear(), 0, 1, 0, 0, 0, 0).valueOf();
        console.log("nowTemp:" + nowTemp + ", " + "nowYear:" + nowYear + ", " + "nowMoth:" + nowMoth + ", " + "nowDay:" + nowDay);

        var $createDate = $('#createDate');
        var checkin = $createDate.datepicker({
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
    checkDate();*/



});
