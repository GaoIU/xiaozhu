layui.use(['form', 'layer'], function() {
	form = layui.form;
	layer = parent.layer === undefined ? layui.layer : top.layer;

	form.verify({
		oldPwd: function(value, item) {
			var msg;
			$.ajax({
				url: '/checkPwd',
				type: 'POST',
				data: {
					"password": value
				},
				dataType: 'JSON',
				async: false,
				success: function(data) {
					if(data.code == 200) {
						if(!data.data) {
							msg = "旧密码错误，请重新输入！";
						}
					}
				}
			});
			return msg;
		},
		newPwd: function(value, item) {
			if(value.length < 6 || value.length > 16) {
				return "密码长度只能在6-16位之间";
			}
		},
		confirmPwd: function(value, item) {
			if(!new RegExp($("#oldPwd").val()).test(value)) {
				return "两次输入密码不一致，请重新输入！";
			}
		}
	});
	
	form.on('submit(changePwd)', function(data) {
		layer.load();
		$.ajax({
			url: '/resetPwd',
			type: 'POST',
			data: data.field,
			dataType: 'JSON',
			async: true,
			success: function(res) {
				layer.closeAll('loading');
				if(res.status == 200) {
					layer.msg(res.msg, {
						icon: 6,
						time: 1500
					});
					setTimeout(function() {
						window.parent.location.href = "/logout";
					}, 1500);
				} else {
					layer.msg(res.msg, {
						icon: 5,
						anim: 6
					});
				}
			},
			error: function() {
				layer.closeAll('loading');
				layer.msg('操作失败', {
					icon: 5,
					anim: 6
				});
			}
		});
		
		$(".pwd").val('');
		return false;
	});
});