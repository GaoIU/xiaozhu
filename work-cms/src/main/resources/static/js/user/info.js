layui.use(['form', 'layer'], function() {
	form = layui.form;
	layer = parent.layer === undefined ? layui.layer : top.layer;
	
	form.verify({
		username: function(value, item) {
			if(!new RegExp('0?(13|14|15|17|18|19)[0-9]{9}').test(value)) {
				return "用户账号不合法";
			}
			
			var msg;
			$.ajax({
				url: '/user/checkUsername',
				type: 'POST',
				data: {
					"username": value,
					"id": $('.id').val()
				},
				dataType: 'JSON',
				async: false,
				success: function(res) {
					if(res.status == 200) {
						if(res.data) {
							msg = "该账号已被使用";
						}
					}
				}
			});
			return msg;
		},
		email: function(value, item) {
			if (value.length) {
				if(!new RegExp('\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}').test(value)) {
					return "邮箱不合法";
				}
				
				var msg;
				$.ajax({
					url: '/user/checkEmail',
					type: 'POST',
					data: {
						"email": value,
						"id": $('.id').val()
					},
					dataType: 'JSON',
					async: false,
					success: function(res) {
						if(res.status == 200) {
							if(res.data) {
								msg = "该邮箱已被使用";
							}
						}
					}
				});
				return msg;
			}
		},
		orgId: function(value, item) {
			if (!value) {
				return "请选择所属公司";
			}
		}
	});
	
	form.on("submit(create)", function(data) {
		var index = top.layer.load();
		var dec = $('#dection').find('option:selected').text();
		data.field.dection = dec;
		var orgId = $('#dection').val();
		if (!orgId) {
			top.layer.msg('请选择所属公司', {
				icon: 5,
				anim: 6
			});
			return false;
		}
		data.field.orgId = orgId;
		
		var type;
		if(data.field.id) {
			type = 'PUT';
		} else {
			type = 'POST';
		}
		
		$.ajax({
			url: '/user',
			type: type,
			data: JSON.stringify(data.field),
			dataType: 'JSON',
			contentType: 'application/json;charset=UTF-8',
			async: true,
			success: function(res) {
				top.layer.close(index);
				if(res.status == 200) {
					top.layer.msg(res.msg, {
						icon: 6,
						time: 1500
					});
					setTimeout(function() {
						layer.closeAll("iframe");
						parent.location.reload();
					}, 2000);
				} else {
					top.layer.msg(res.msg, {
						icon: 5,
						anim: 6
					});
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				var msg = '操作失败';
				if(jqXHR.status == 405) {
					msg = '无权访问';
				}
				
				top.layer.close(index);
				layer.msg(msg, {
					icon: 5,
					anim: 6
				});
			}
		});
		
		return false;
	});
});