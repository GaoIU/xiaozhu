layui.use(['form', 'layer', 'laydate'], function() {
	form = layui.form;
	laydate = layui.laydate;
	layer = parent.layer === undefined ? layui.layer : top.layer;
	
	var anim = Math.floor(Math.random() * 6 + 1);
	var color;
	switch(anim) {
		case 1:
			color = '#5EABE1';
			break;
		case 2:
			color = '#00A65A';
			break;
		case 3:
			color = '#FA6086';
			break;
		case 4:
			color = '#FF9A1E';
			break;
		case 5:
			color = '#FA2A00';
			break;
		case 6:
			color = 'molv';
			break;
	}
	
	$('.datepicker').each(function() {
		laydate.render({
			elem: this,
			type: 'date',
			trigger: 'click',
			min: 0,
			theme: color
		});
	});

	$('.datetimepicker').each(function() {
		laydate.render({
			elem: this,
			type: 'datetime',
			trigger: 'click',
			min: 0,
			theme: color
		});
	});
	
	form.verify({
		name: function(value, item) {
			if(value.length > 64) {
				return "门店名称长度不能大于64位";
			}
			
			var msg;
			$.ajax({
				url: '/organization/checkName',
				type: 'POST',
				data: {
					"name": value,
					"id": $('.id').val()
				},
				dataType: 'JSON',
				async: false,
				success: function(res) {
					if(res.status == 200) {
						if(res.data) {
							msg = "该门店名称已被使用";
						}
					}
				}
			});
			return msg;
		}
	});
	
	form.on("submit(create)", function(data) {
		var index = top.layer.load();
		
		var type;
		if(data.field.id) {
			type = 'PUT';
		} else {
			type = 'POST';
		}
		
		$.ajax({
			url: '/organization',
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