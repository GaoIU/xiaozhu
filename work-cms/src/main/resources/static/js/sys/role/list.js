layui.use(['form', 'element', 'layer', 'laydate'], function() {
	layForm = layui.form;
	laydate = layui.laydate;
	layer = layui.layer;
	
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
			max: 0,
			theme: color
		});
	});

	$('.datetimepicker').each(function() {
		laydate.render({
			elem: this,
			type: 'datetime',
			trigger: 'click',
			max: 0,
			theme: color
		});
	});
	
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
	
	$('#goPage').keyup(function() {
		var max_page = parseInt($('#goPage').attr('max'));
		var target_page = $('#goPage').val();
		if(!/\d{1,}/.test(target_page)) {
			target_page = 1;
		} else {
			target_page = parseInt(target_page);
		}
		if(target_page > max_page) target_page = max_page;
		$('#goPage').val(target_page);
		pageShow.gotoPage(target_page, pageShow.size);
	});
	
	$("table.list").tableresize({
		resizeTable: false
	});
	
	$('.create').on('click', function() {
		layer.open({
			title: '添加后台角色',
			anim: anim,
			type: 2,
			area: ['100%', '100%'],
			content: '/sysRole/gotoInfo',
			success: function(index, layero) {
				setTimeout(function() {
					layer.tips('点击此处返回后台角色列表', '.layui-layer-setwin .layui-layer-close', {
						tips: 3
					});
				}, 500);
			}
		});
	});
	
	$('.delete').on('click', function() {
		var ids = $('input[name="chooseIds"]:checked');
		if (ids != null && ids.length) {
			var anim = Math.floor(Math.random() * 6 + 1);
			layer.confirm('该操作不可逆，是否确认执行？', {
				icon: 3,
				anim: anim,
				title: '提示'
			}, function(index) {
				layer.close(index);
				var id = "";
				$.each(ids, function(index, obj) {
					if(index > 0) {
						id += ",";
					}
					id += $(obj).val();
				});
				dels(id);
			});
		} else {
			layer.msg('您还没有选择要操作的数据', {
				icon: 0,
				anim: 6
			});
		}
	});
});

function dels(id) {
	layer.load();
	$.ajax({
		url: '/sysRole?id=' + id,
		type: 'DELETE',
		dataType: 'JSON',
		async: true,
		success: function(res) {
			layer.closeAll();
			top.layer.msg(res.msg, {
				icon: 6,
				time: 1500
			});
			setTimeout(function() {
				if(res.status == 200) {
					$('.all-choose').removeClass('layui-form-checked');
					$.each($('.one-choose'), function(index, obj) {
						var inp = $(obj).prev();
						$(obj).removeClass('layui-form-checked');
						$(inp).attr('checked', false);
					});
					queryList.find();
				}
			}, 2000);
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
}

var pageShow = new Vue({
	el: '#pageShow',
	data: {
		current: '1',
		size: '15',
		total: '0',
		pagenumber: '1',
		pagesizes: [{
			name: '10 条/页',
			value: '10'
		}, {
			name: '15 条/页',
			value: '15'
		}, {
			name: '20 条/页',
			value: '20'
		}, {
			name: '25 条/页',
			value: '25'
		}]
	},
	created: function() {
		this.size = this.pagesizes[1].value;
	},
	methods: {
		pageRefresh() {
			queryList.find();
		},
		gotoPage(current, size) {
			this.size = size;
			this.current = current;
			queryList.find();
		},
		setPage(page) {
			this.current = page.current;
			this.size = page.size;
			this.total = page.total;
			if(page.total <= page.size) {
				this.pagenumber = 1;
			} else if((page.total % page.size) == 0) {
				this.pagenumber = page.total / page.size;
			} else {
				this.pagenumber = Math.floor(page.total / page.size) + 1;
			}
		}
	}
});

var listSearch = new Vue({
	el: '#list_search',
	methods: {
		search() {
			queryList.find();
		}
	}
});

var queryList = new Vue({
	el: '#queryList',
	data: {
		items: []
	},
	created: function() {
		this.find();
	},
	methods: {
		find() {
			var URL = "/sysRole?current=" + pageShow.current + "&size=" + pageShow.size + "&" + $('#searchForm').serialize();
			$.get(URL, function(res) {
				var page = res.data;
				pageShow.setPage(page);
				queryList.items = page.records;
			});
		},
		oneChoose(e) {
			var divbox = e.currentTarget;
			var checkbox = $(divbox).prev();
	    	if($(divbox).hasClass('layui-form-checked')) {
	    		$(divbox).removeClass('layui-form-checked');
	    		$(checkbox).attr('checked', false);
	    		$('.all-choose').removeClass('layui-form-checked');
	    	} else {
	    		$(divbox).addClass('layui-form-checked');
	    		$(checkbox).attr('checked', true);
	    		var ischecked = true;
	    		$.each($('.one-choose'), function(index, obj) {
	    			if(!$(obj).hasClass('layui-form-checked') && !$(obj).hasClass('layui-checkbox-disbaled')) {
	    				ischecked = false;
	    			}
	    		});
	    		if(ischecked) {
	    			$('.all-choose').addClass('layui-form-checked');
	    		} else {
	    			$('.all-choose').removeClass('layui-form-checked');
	    		}
	    	}
		},
		allChoose(e) {
			var divbox = e.currentTarget;
			var ischecked = $(divbox).hasClass('layui-form-checked');
			
			if(ischecked) {
				$(divbox).removeClass('layui-form-checked');
			} else {
				$(divbox).addClass('layui-form-checked');
			}
			$.each($('.one-choose'), function(index, obj) {
				var inp = $(obj).prev();
				if(ischecked) {
					$(obj).removeClass('layui-form-checked');
					$(inp).attr('checked', false);
				} else {
					if(!$(obj).hasClass('layui-checkbox-disbaled')) {
						$(obj).addClass('layui-form-checked');
						$(inp).attr('checked', true);
					}
				}
			});
		},
		edit(id, code) {
			if(code == 'ADMINISTRATOR') {
				layer.msg('超级管理员不可被修改', {
					icon: 5,
					anim: 6
				});
				
				return false;
			}
			
			var anim = Math.floor(Math.random() * 6 + 1);
			layer.open({
				title: '修改后台角色',
				anim: anim,
				type: 2,
				area: ['100%', '100%'],
				content: '/sysRole/gotoInfo?id=' + id,
				success: function(index, layero) {
					setTimeout(function() {
						layer.tips('点击此处返回后台角色列表', '.layui-layer-setwin .layui-layer-close', {
							tips: 3
						});
					}, 500);
				}
			});
		},
		usable(id, code, status) {
			if(code == 'ADMINISTRATOR') {
				layer.msg('超级管理员不可被禁用', {
					icon: 5,
					anim: 6
				});
				
				return false;
			}
			
			var anim = Math.floor(Math.random() * 6 + 1);
			var msg;
			if (status == 0) {
				msg = "是否确认启用？";
			} else {
				msg = "该操作可能使部分功能不可用，是否确认执行？";
			}
			layer.confirm(msg, {
				icon: 3,
				anim: anim,
				title: '提示'
			}, function(index) {
				layer.close(index);
				layer.load();
				var param = {"id": id, "status": status};
				$.ajax({
					url: '/sysRole/usable',
					type: 'PUT',
					data: JSON.stringify(param),
					dataType: 'JSON',
					contentType: 'application/json;charset=UTF-8',
					async: true,
					success: function(res) {
						layer.closeAll();
						top.layer.msg(res.msg, {
							icon: 6,
							time: 1500
						});
						setTimeout(function() {
							if(res.status == 200) {
								queryList.find();
							}
						}, 2000);
					},
					error: function() {
						layer.closeAll();
						layer.msg('操作失败', {
							icon: 5,
							anim: 6
						});
					}
				});
			});
		},
		del(id, code) {
			if(code == 'ADMINISTRATOR') {
				layer.msg('超级管理员不可被删除', {
					icon: 5,
					anim: 6
				});
				
				return false;
			}
			
			var anim = Math.floor(Math.random() * 6 + 1);
			layer.confirm('该操作不可逆，是否确认执行？', {
				icon: 3,
				anim: anim,
				title: '提示'
			}, function(index) {
				layer.close(index);
				dels(id);
			});
		}
	}
});