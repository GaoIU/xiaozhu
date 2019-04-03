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
			title: '添加公司',
			anim: anim,
			type: 2,
			area: ['100%', '100%'],
			content: '/organization/gotoInfo',
			success: function(index, layero) {
				setTimeout(function() {
					layer.tips('点击此处返回公司列表', '.layui-layer-setwin .layui-layer-close', {
						tips: 3
					});
				}, 500);
			}
		});
	});
});

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
			var URL = "/organization?current=" + pageShow.current + "&size=" + pageShow.size + "&" + $('#searchForm').serialize();
			$.get(URL, function(res) {
				var page = res.data;
				pageShow.setPage(page);
				queryList.items = page.records;
			});
		},
		edit(id) {
			var anim = Math.floor(Math.random() * 6 + 1);
			layer.open({
				title: '修改用户',
				anim: anim,
				type: 2,
				area: ['100%', '100%'],
				content: '/organization/gotoInfo?id=' + id,
				success: function(index, layero) {
					setTimeout(function() {
						layer.tips('点击此处返回公司列表', '.layui-layer-setwin .layui-layer-close', {
							tips: 3
						});
					}, 500);
				}
			});
		}
	}
});