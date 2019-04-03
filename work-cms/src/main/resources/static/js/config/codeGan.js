layui.use(['form', 'layer'], function() {
	form = layui.form;
	layer = layui.layer;
	
	$('#list_search_toggle').on('click', function() {
		var search = $('#list_search');
		if(search.hasClass('hidden')) {
			search.removeClass('hidden');
			$('#list_search_toggle').text('关闭搜索');
		} else {
			search.addClass('hidden');
			$('#list_search_toggle').text('展开搜索');
		}
	});
	
	form.on("submit(create)", function(data) {
		var index = top.layer.load();
		var tableNames = new Array();
		$("#tableNames").find("input").each(function() {
			if($(this).next().hasClass('layui-form-checked')) {
				tableNames.push($(this).val());
			}
		});
		data.field.tableNames = tableNames;
		
		$.ajax({
			url: '/sysConfig/codeGan',
			type: 'POST',
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
						location.reload();
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