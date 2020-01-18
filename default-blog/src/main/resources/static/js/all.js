// 失败消息盒
function dangerNotice(notice) {
    $('.dangerNotice').html(notice);
    $('.dangerNoticeAlert').css("display","block");
    var closeNoticeBox = setTimeout(function () {
        $('.dangerNoticeAlert').css("display","none");
    },3000);
}

// 成功消息盒
function successNotice(notice) {
    $('.successNotice').html(notice);
    $('.successNoticeAlert').css("display","block");
    var closeNoticeBox = setTimeout(function () {
        $('.successNoticeAlert').css("display","none");
    },3000);
}

$(function () {


    // 获得访客量，除文章显示界面外其他界面访客量通用
    // window.location.pathname = URL 的路径部分(就是文件地址)
    // window.location.search = 查询(参数)部分
    var pageName = window.location.pathname + window.location.search;
    $.ajax({
        type:'get',
        url:'/getVisitorNumByPageName',
        dataType:'json',
        data:{
            statisticsName:pageName.substring(1)
        },
        success:function (data) {
            if(data['status'] === 103){
                $("#totalVisitors").html(0);
                $("#visitorVolume").html(0);
            } else {
                $("#totalVisitors").html(data['data']['totalVisitors']);
                $("#visitorVolume").html(data['data']['statisticsVisit']);
            }
        },
        error:function () {
        }
    });

    //反馈
    $('.feedbackClick').click(function () {
        $('.feedback').css("display","block")
    });
    $('.feedbackClose').click(function () {
        $('.feedback').css("display","none")
    });
    $('.feedbackFormBtn').click(function () {
        var feedbackFormContent = $('#feedbackFormContent');
        var feedbackFormQQ = $('.feedbackFormQQ');
        if(feedbackFormContent.val().length === 0){
            dangerNotice("反馈内容不能为空哦！")
        } else {
            $.ajax({
                type:'POST',
                url:'/submitFeedback',
                dataType:'json',
                data:{
                    feedbackContent:feedbackFormContent.val(),
                    contactInfo:feedbackFormQQ.val()
                },
                success:function (data) {
                    if(data['status'] === 101){
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else {
                        successNotice("反馈成功，我会尽快解决的！");
                        $('.feedback').css("display","none");
                    }
                },
                error:function () {
                }
            });
        }
    });

    //图片懒加载
    // 获取window的引用:
    var $window = $(window);
    // 获取包含data-src属性的img，并以jQuery对象存入数组:
    var lazyImgs = _.map($('img[data-src]').get(), function (i) {
        return $(i);
    });
    // 定义事件函数:
    var onScroll = function () {
        // 获取页面滚动的高度:
        var wtop = $window.scrollTop();
        // 判断是否还有未加载的img:
        if (lazyImgs.length > 0) {
            // 获取可视区域高度:
            var wheight = $window.height();
            // 存放待删除的索引:
            var loadedIndex = [];
            // 循环处理数组的每个img元素:
            _.each(lazyImgs, function ($i, index) {
                // 判断是否在可视范围内:
                if ($i.offset().top - wtop - 350 < wheight) {
                    // 设置src属性:
                    $i.attr('src', $i.attr('data-src'));
                    // 添加到待删除数组:
                    loadedIndex.unshift(index);
                }
            });
            // 删除已处理的对象:
            _.each(loadedIndex, function (index) {
                lazyImgs.splice(index, 1);
            });
        }
    };
    // 绑定事件:
    $window.scroll(onScroll);
    // 手动触发一次:
    onScroll();

    // 导航栏上的搜索文章
});
