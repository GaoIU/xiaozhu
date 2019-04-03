layui.use([ 'form', 'layer', 'upload' ], function() {
	form = layui.form;
	layer = parent.layer === undefined ? layui.layer : top.layer;
	upload = layui.upload;
	
	upload.render({
		elem: '.userFaceBtn',
		url: '/upload',
		method: 'POST',
		data: {
			"sysUserId": $('.id').val()
		},
		accept: 'images',
		acceptMime: 'image/*',
		field: 'avatar',
		size: 5120,
		before: function() {
			layer.load();
		},
		done: function(res, index, upload) {
			layer.closeAll('loading');
			layer.msg('上传成功', {icon: 6});
			$('#userFace').attr('src', res.data);
			$('#userAvatar', parent.document).attr('src', res.data);
		},
		error: function(index, upload) {
			layer.closeAll('loading');
			layer.msg('上传失败', {icon: 5});
		}
	});
	
	form.verify({
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
					if(res.code == 200) {
						if(res.data) {
							msg = "该昵称已被使用";
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
					if(res.code == 200) {
						if(res.data) {
							msg = "该手机号已存在";
						}
					}
				}
			});
			return msg;
		}
	});
	
	form.on('submit(changeUser)', function(data) {
		delete data.field.avatar;
		layer.load();
		$.ajax({
			url: '/sysUserInfo',
			type: 'PUT',
			data: JSON.stringify(data.field),
			dataType: 'JSON',
			contentType: 'application/json;charset=UTF-8',
			async: true,
			success: function(res) {
				layer.closeAll('loading');
				if(res.status == 200) {
					layer.msg(res.msg, {
						icon: 6,
						time: 1500
					});
					setTimeout(function() {
						$('#userNickName', parent.document).attr('src', data.field.nickName);
						location.reload();
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
		
		return false;
	});
});