layui.use(['form', 'layer'], function() {
	form = layui.form;
	layer = parent.layer === undefined ? layui.layer : top.layer;
	
	form.verify({
		userName: function(value, item) {
			if(value.length > 8) {
				return "用户账号长度不能大于8位";
			}
			
			var msg;
			$.ajax({
				url: '/checkUserName',
				type: 'POST',
				data: {
					"userName": value,
					"sysUserId": $('.id').val()
				},
				dataType: 'JSON',
				async: false,
				success: function(res) {
					if(res.status == 200) {
						if(res.data) {
							msg = "该用户账号已被使用";
						}
					}
				}
			});
			return msg;
		},
		nickName: function(value, item) {
			if(value.length > 8) {
				return "用户昵称长度不能大于8位";
			}
			
			var msg;
			$.ajax({
				url: '/checkNickName',
				type: 'POST',
				data: {
					"nickName": value,
					"sysUserId": $('.id').val()
				},
				dataType: 'JSON',
				async: false,
				success: function(res) {
					if(res.status == 200) {
						if(res.data) {
							msg = "该用户昵称已被使用";
						}
					}
				}
			});
			return msg;
		},
		mobile: function(value, item) {
			if(!new RegExp('0?(13|14|15|17|18|19)[0-9]{9}').test(value)) {
				return "手机号码不合法";
			}
			
			var msg;
			$.ajax({
				url: '/checkMobile',
				type: 'POST',
				data: {
					"mobile": value,
					"sysUserId": $('.id').val()
				},
				dataType: 'JSON',
				async: false,
				success: function(res) {
					if(res.status == 200) {
						if(res.data) {
							msg = "该手机号已存在";
						}
					}
				}
			});
			return msg;
		}
	});
	
	form.on("submit(create)", function(data) {
		var sysRoleIds = "";
		$.each($("input[name='sysRoleIds']:checked"), function(index, obj) {
			if(index != 0) {
				sysRoleIds += ",";
			}
			sysRoleIds += $(obj).val();
		});
		
		if(!sysRoleIds.length) {
			layer.msg('请选择角色范围', {
				icon: 5,
				anim: 6
			});
			return false;
		}
		
		var index = top.layer.load();
		
		data.field['sysRoleIds'] = sysRoleIds;
		var type;
		if(data.field.id) {
			type = 'PUT';
		} else {
			type = 'POST';
		}
		
		$.ajax({
			url: '/sysUser',
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