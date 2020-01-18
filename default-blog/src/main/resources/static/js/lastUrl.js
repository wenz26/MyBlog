
$.ajax({
    type: 'HEAD', // 获取头信息，type=HEAD即可
    url: window.location.href,
    async: false,
    success: function (data, status, xhr) {
        // alert("getHead");
        var lastUrl = xhr.getResponseHeader("lastUrl");
        if (lastUrl != null) {
            window.location.replace(lastUrl);
        }
    }

});
