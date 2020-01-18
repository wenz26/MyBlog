

$(function () {
    $('.superAdminList .superAdminClick').click(function () {
        var flag = $(this).attr('class').substring(16);
        $('#statistics,#articleManagement,#commentMessage,#articleCategories,#articleTags,#userManagement,#userLoginMore,' +
            '#userOperating,#returnCodeHelpPage,#databaseManagement,#interfaceManagement,#userFeedback').css("display","none");
        $("#" + flag).css("display","block");
    });
})
