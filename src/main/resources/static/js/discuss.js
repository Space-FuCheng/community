function like(btn, entityType, entityId, entityUserId) {
    $.post(
        // TIP：注意这里path的"/like"要加/，这样才是链接
      CONTEXT_PATH + "/like",
        {"entityType":entityType, "entityId":entityId, "entityUserId":entityUserId},
        function (data) {
          data = $.parseJSON(data);
          if (data.code == 0) {
              //通过传进来的btn按钮得到其下级<b>标签和<i>标签
              $(btn).children("i").text(data.likeCount);
              $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
          } else {
              alert(data.msg);
          }
        }
    );
}