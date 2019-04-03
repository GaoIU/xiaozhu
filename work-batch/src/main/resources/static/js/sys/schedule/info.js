layui.use(['form', 'layer'], function() {
	form = layui.form;
	layer = parent.layer === undefined ? layui.layer : top.layer;
	
	form.on("submit(create)", function(data) {
		var index = top.layer.load();
		
		var type;
		if(data.field.id) {
			type = 'PUT';
		} else {
			type = 'POST';
		}
		
		$.ajax({
			url: '/scheduleJob',
			type: type,
			data: JSON.stringify(data.field),
			dataType: 'JSON',
			contentType: 'application/json;charset=UTF-8',
			async: true,
			success: function(res) {
				top.layer.close(index);
				top.layer.msg(res.msg, {
					icon: 6,
					time: 1500
				});
				setTimeout(function() {
					if(res.status == 200) {
						layer.closeAll("iframe");
						parent.location.reload();
					}
				}, 1500);
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