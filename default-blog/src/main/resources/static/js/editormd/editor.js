$(function () {

    var flag = 1;
    var aCategory = "";

    $('#originalAuthorHide').hide();
    $('.articleUrlHide').hide();

    var fnClose = function(e){
        e.returnValue = '您输入的内容尚未保存，确定离开此页面吗？';
    };
    window.addEventListener('beforeunload',fnClose);


    var testEditor;

    testEditor = editormd("my-editormd", { //注意1：这里的就是上面的DIV的id属性值
        width: "100%",
        height: 740,
        syncScrolling: true, //设置双向滚动
        path: "../../../lib/", //lib目录的路径
        previewTheme : "dark", //代码块使用dark主题
        codeFold : true,
        emoji:true,
        tocm : true, // Using [TOCM]
        tex : true, // 开启科学公式TeX语言支持，默认关闭
        flowChart : true, // 开启流程图支持，默认关闭
        sequenceDiagram : true, // 开启时序/序列图支持，默认关闭,
        htmlDecode : true, //不过滤标签
        imageUpload : true, //上传图片
        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp","JPG","JPEG","GIF","PNG","BMP","WEBP"],
        imageUploadURL : "/uploadImage",
        onload:function () {
            // console.log('onload', this);
        },
        saveHTMLToTextarea: true, //注意3：这个配置，方便post提交表单
        toolbarIcons : function () {
            return ["bold","del","italic","quote","|","h1","h2","h3","h4","h5","h6","|","list-ul","list-ol","hr","|","link","image","code","code-block","table","datetime","html-entities","emoji","|","watch","preview","fullscreen","clear","search","|","help","info"]
        }
    });


    var draftBtn = $('.draftBtn');
    var publishBtn = $('.publishBtn');
    var articleTitle = $('#zhy-editor-title');
    var articleContent = $('#my-editormd-html-code');
    var noticeBoxTitle = $('.notice-box-title');
    var noticeBoxContent = $('.notice-box-content');
    var noticeBox = $('.notice-box');

    publishBtn.click(function () {
        var articleTitleValues =  articleTitle.val();
        var articleContentValues = articleContent.val();
        if(articleTitleValues.length === 0){
            noticeBoxTitle.show();
        } else if (articleContentValues.length === 0){
            noticeBoxContent.show();
        } else{
            $('#my-alert').modal();
            $.ajax({
                type: "GET",
                url: "/findCategoriesName",
                async: false,
                data:{
                },
                dataType: "json",
                success:function (data) {
                    var selectCategories = $('#select-categories');
                    selectCategories.empty();
                    selectCategories.append($('<option class="categoriesOption" value="choose">请选择</option>'));
                    for(var i = 0; i < data['data'].length; i++){
                        selectCategories.append($('<option class="categoriesOption" value="' + data['data'][i] + '">' + data['data'][i] + '</option>'));
                    }
                    if(typeof(aCategory) !== "undefined" && aCategory !== "" && aCategory.length > 0){
                        selectCategories.val(aCategory);
                    }
                },
                error:function () {
                }
            });
        }
        // 定时关闭错误提示框
        var closeNoticeBox = setTimeout(function () {
            noticeBox.hide();
        },3000);
    });

    draftBtn.click(function () {
        var articleTitleValues =  articleTitle.val();
        var articleContentValues = articleContent.val();
        if(articleTitleValues.length === 0){
            noticeBoxTitle.show();
        } else if (articleContentValues.length === 0){
            noticeBoxContent.show();
        } else{
            $.ajax({
                type: "POST",
                url: "/saveArticle",
                dataType: "json",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                data:{
                    id : $('.draftBtn').attr("id"),
                    articleTitle : articleTitle.val(),
                    articleContent : articleContent.val(),
                    articleHtmlContent : testEditor.getHTML()
                },
                success:function (data) {
                    if(data['status'] === 101){
                        window.removeEventListener('beforeunload',fnClose);
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else if(data['status'] === 205) {
                        alert("你还没有保存文章的权限哦")
                    } else if(data['status'] === 206) {
                        alert("服务器异常")
                    } else {
                        window.removeEventListener('beforeunload',fnClose);
                        draftSuccessPutIn(data['data']);
                    }
                },
                error:function () {
                    alert("保存文章异常")
                }
            });
        }

        // 定时关闭错误提示框
        var closeNoticeBox = setTimeout(function () {
            noticeBox.hide();
        },3000);
    });

    function publishSuccessPutIn(data) {
        $('#removeDiv').html('');
        var sec = $('<div id="all"></div>');
        var success = $('<div class="success"></div>');
        var successBox = $('<div class="success-box"></div>');
        var successArticleTitle = $('<div class="successArticleTitle"><span>' + data.articleTitle + '</span></div>');
        var successWord = $('<div class="success-word"><p><i class="am-success am-icon-check-square-o" style="color: #5eb95e"></i> 发布成功</p></div>');
        var successTimeAndUser = $('<div class="success-time-user">' +
            '<p><i class="am-icon-calendar"></i>&nbsp;' + data.updateDate + '&nbsp;&nbsp;&nbsp;&nbsp;<i class="am-icon-user"></i>&nbsp;' + data.author + '</p>' +
            '</div>');
        var successBtn = $('<div class="successBtn">' +
            '<a href="/editor" class="reWriteBtn am-btn am-btn-danger am-round">写新文章</a>' +
            '<a href="' + data.articleUrl + '" class="lookArticleBtn am-btn am-btn-danger am-round">查看文章</a>' +
            '</div>');

        successBox.append(successArticleTitle);
        successBox.append(successWord);
        successBox.append(successTimeAndUser);
        successBox.append(successBtn);
        success.append(successBox);
        sec.append(success);
        $('#removeDiv').append(sec);
    }

    function draftSuccessPutIn(data) {
        $('#removeDiv').html('');
        var sec = $('<div id="all"></div>');
        var success = $('<div class="success"></div>');
        var successBox = $('<div class="success-box"></div>');
        var successArticleTitle = $('<div class="successArticleTitle"><span>' + data.articleTitle + '</span></div>');
        var successWord = $('<div class="success-word"><p><i class="am-success am-icon-check-square-o" style="color: #5eb95e"></i> 保存成功</p></div>');
        var successTimeAndUser = $('<div class="success-time-user">' +
            '<p><i class="am-icon-calendar"></i>&nbsp;' + data.updateDate + '&nbsp;&nbsp;&nbsp;&nbsp;<i class="am-icon-user"></i>&nbsp;' + data.author + '</p>' +
            '</div>');
        var successBtn = $('<div class="successBtn">' +
            '<a href="/" class="reWriteBtn am-btn am-btn-danger am-round">回到首页</a>' +
            '<a href="/user" class="lookArticleBtn am-btn am-btn-danger am-round">个人首页</a>' +
            '</div>');

        successBox.append(successArticleTitle);
        successBox.append(successWord);
        successBox.append(successTimeAndUser);
        successBox.append(successBtn);
        success.append(successBox);
        sec.append(success);
        $('#removeDiv').append(sec);
    }

    // 验证是否有权限写博客（凡是登录进来的用户都能写，这里先不做判断，没登录的会跳到登录页面（这里要注意session失效时间））


    // 获得草稿文章（每次进入文章显示界面，都会执行一次，如果是修改文章）
    $.ajax({
        type: "GET",
        url: "/getDraftArticle",
        async: false,
        dataType:"json",
        data:{
        },
        success:function (data) {
            if(data['status'] === 0){
                console.log("这是登录超时重写的文章或者已发布文章或者草稿箱里的文章");
                $('#zhy-editor-title').val(data['data']['articleTitle']);
                $('#my-editormd-markdown-doc').html(data['data']['articleContent']);
                $('#articleImage').attr("src", data['data']['imageUrl']);

                if (typeof(data['data']['articleType']) !== "undefined" && data['data']['articleType'] !== ""){
                    $('#select-type').val(data['data']['articleType']);
                }

                $('#originalAuthor').val(data['data']['originalAuthor']);
                $('#articleUrl').val(data['data']['articleUrl']);
                if(data['data']['articleType'] === "转载"){
                    $('#originalAuthorHide').show();
                    $('.articleUrlHide').show();
                }

                aCategory = data['data']['articleCategories'];
                var tags = data['data']['articleTags'];
                var tag = $('.tag');
                for(var i in tags){
                    tag.append($('<div style="display: inline-block;"><p class="tag-name" contenteditable="true">' + tags[i] + '</p>' +
                        '<i class="am-icon-times removeTag" style="color: #CCCCCC"></i></div>'));
                }
                var articleId = data['data']['articleId'];
                if(articleId !== 0){
                    $('.surePublishBtn ').attr("id", articleId);
                    $('.draftBtn').attr("id", articleId);
                }
            } else {
                console.log("这是新写文章");
            }
        },
        error:function () {
            alert("登录超时再次登录成功，获取原来的文章信息失败，请回到首页重新操作！！！");
        }
    });

    // 插入标签
    var addTagsBtn = $('.addTagsBtn');
    var $wrapper = $('.tag');

    var appendPanel = function() {
        // contenteditable="true" 段落可编辑
        var panel = $('<div style="display: inline-block;"><p class="tag-name" contenteditable="true"></p>' +
            '<i class="am-icon-times removeTag" style="color: #CCCCCC"></i></div>');
        $wrapper.append(panel);
        $('.tag-name').click(function () {
            $(this).focus();
        });
    };

    var tag = document.getElementById("tag");
    addTagsBtn.on('click', function() {
        var tagCount = tag.childElementCount;
        console.log(tagCount);
        if(tagCount >= 5){
            addTagsBtn.attr('disabled','disabled');
        } else {
            var value=$('.tag-name').eq(tagCount - 1).html();
            console.log(value);
            if(value !== "" && value !== null){
                appendPanel();
            }
        }
    });

    $('.tag').on('click','.removeTag',function () {
        $(this).parent().remove();
        var tagCount = tag.childElementCount;
        console.log(tagCount);
        if(tagCount < 5){
            addTagsBtn.removeAttr('disabled');
        }
    });


    // 显示文章作者
    var articleType = $('#select-type');

    // 当元素失去焦点时发生 blur 事件
    articleType.blur(function () {
        if(articleType.val() === "转载"){
            $('#originalAuthorHide').show();
            $('.articleUrlHide').show();
        } else if (articleType.val() === "原创"){
            $('#originalAuthorHide').hide();
            $('.articleUrlHide').hide();
        }
    });

    // 发表博客
    var surePublishBtn = $('.surePublishBtn');
    var articleCategories = $('#select-categories');
    var originalAuthor = $('#originalAuthor');
    var articleUrl = $('#articleUrl');
    surePublishBtn.click(function () {
        var articleImage = $('#articleImage').attr("src");

        var tagNum = $('.tag').find('.tag-name').length;
        var articleTagsValue = [];
        for(var j = 0; j < tagNum; j++){
            articleTagsValue[j] = $('.tag-name').eq(j).html();
        }
        console.log("发布文章的标签：" + articleTagsValue);

        var articleTypeValue = articleType.val();
        var articleCategoriesValue = articleCategories.val();
        var originalAuthorValue = originalAuthor.val();
        var articleUrlValue = articleUrl.val();

        if (articleImage === null || articleImage === "" || typeof(articleImage) === "undefined"){
            $('.notice-box-image').show();
        } else if(articleTagsValue.length === 0 || articleTagsValue[tagNum - 1] === ""){
            $('.notice-box-tags').show();
        } else if (articleTypeValue === "choose"){
            $('.notice-box-type').show();
        } else if (articleCategoriesValue === "choose"){
            $('.notice-box-categories').show();
        } else if (articleType.val() === "转载" && originalAuthorValue === ""){
            $('.notice-box-originalAuthor').show();
        } else if (articleType.val() === "转载" && articleUrlValue === ""){
            $('.notice-box-url').show();
        } else {
            $.ajax({
                type: "POST",
                url: "/publishArticle",
                traditional: true,// 传数组
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                dataType: "json",
                data:{
                    id : $('.surePublishBtn').attr("id"),
                    articleTitle : articleTitle.val(),
                    articleContent : articleContent.val(),
                    articleTagsValue : articleTagsValue,
                    articleType : articleTypeValue,
                    articleCategoryName : articleCategoriesValue,
                    originalAuthor : originalAuthorValue,
                    articleUrl : articleUrlValue,
                    imageUrl: $('#articleImage').attr("src"),
                    articleHtmlContent : testEditor.getHTML()
                },
                success:function (data) {
                    if(data['status'] === 101){
                        window.removeEventListener('beforeunload',fnClose);
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    } else if(data['status'] === 213) {
                        alert("文章首页图片还没上传呀，快去上传吧")
                    } else if(data['status'] === 205) {
                        alert("你还没有发布文章的权限哦")
                    } else if(data['status'] === 206) {
                        alert("服务器异常")
                    } else {
                        $('#my-alert').modal('close');
                        window.removeEventListener('beforeunload',fnClose);
                        publishSuccessPutIn(data['data']);
                    }
                },
                error:function () {
                    alert("发表博客异常")
                }
            });
        }

        // 定时关闭错误提示框
        var closeNoticeBox = setTimeout(function () {
            noticeBox.hide();
        },3000);
    });
});

function uploadPic(e) {
    var dom =$("input[id^='articlePic']")[0];
    var reader = new FileReader();
    reader.onload = (function (file) {
        return function (e) {
            //alert(this.result);
            $.ajax({
                type:'POST',
                url:'/uploadArticleImageUrl',
                dataType:'text',
                data:{
                    // base64
                    articleImageUrl:this.result
                },
                success:function (data) {
                    $("img[id='articleImage']").attr("src", data);
                },
                error:function () {
                    alert("上传文章首页图片失败了")
                }
            });
        };
    })(e.target.files[0]);
    reader.readAsDataURL(e.target.files[0]);
}
