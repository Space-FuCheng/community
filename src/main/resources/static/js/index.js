$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	//发送异步请求POST
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function (data) {
			data = $.parseJSON(data);
			//提示框种显示返回消息，利用id获取
			$("#hintBody").text(data.msg);//并修改其内容
			//显示提示框，两秒后自动隐藏
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//成功了刷新页面
				if (data.code == 0) {
					window.location.reload();
				}
			}, 2000);


		}
	)


}