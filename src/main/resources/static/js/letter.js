$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	//在这里写逻辑，服务端处理完数据再提示成功
	var toNmae = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toNmae, "content":content},
		function (data) {
			data = $.parseJSON(data);
			if (data.code == 0) {
				$("#hintBody").text("发送成功");
			} else {
				$("#hintBody").text(data.msg);
			}
			//下面是把页面刷新一下，也就是复制原本的：发送成功，2s后小时
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();//重载当前页面
			}, 2000);
		}

	);

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}