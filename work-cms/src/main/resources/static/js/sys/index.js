var Tab;
layui.use(['element', 'tab'], function() {
	var element = layui.element;

	Tab = layui.tab({
		elem: '.layui-tab',
		maxSetting: {
			max: 20,
			tipMsg: '最多只能开启20个'
		},
		contextMenu: true,
		autoRefresh: true
	});
	
	$('#gloMenu').on('click', '.navT', function() {
		var parent = $(this).closest('li');
		if(parent.find('.navC').find('li').length) {
			if(parent.hasClass('open')) {
				parent.find('.fa-angle-down').addClass('fa-angle-right');
				parent.find('.fa-angle-down').removeClass('fa-angle-down');
				parent.find('li').removeClass('open');
				parent.find('.navC').stop(true).slideUp(300, function() {
					parent.removeClass('open')
				});
			} else {
				$(this).find('.fa-angle-right').addClass('fa-angle-down');
				$(this).find('.fa-angle-right').removeClass('fa-angle-right');
				parent.siblings().find('.fa-angle-down').addClass('fa-angle-right');
				parent.siblings().find('.fa-angle-down').removeClass('fa-angle-down');
				var openLi = parent.siblings();
				openLi.find('li').removeClass('open');
				openLi.removeClass('open').find('.navC').stop(true).slideUp(300);
				parent.addClass('open').find('.navC').eq(0).stop(true).slideDown(300);
			}

		}
	});
	
	$('.tooltip').hover(function() {
        var text = $(this).attr('data-tip-text');
        var type = $(this).attr('data-tip-type') ? $(this).attr('data-tip-type') : 2;
        var bg = $(this).attr('data-tip-bg') ? $(this).attr('data-tip-bg') : '#393D49';
        if (text) {
            layer.tips(text, $(this), {
                tips: [type, bg],
                time: 0
            });
        }
    },
    function() {
        layer.close(layer.tips());
    });
	
	$('#logout').on('click', function(){
		var anim = Math.floor(Math.random() * 6 + 1);
		layer.confirm('最后问您一遍，您确认要离开？', {
			anim: anim,
			btn: ['去意已决', '朕再看看'],
			yes: function(index) {
				layer.close(index);
				location.href = "/logout";
			}
		});
	});
	
	$('#gloMenu').on('click', 'a', function() {
		var href = $(this).attr('data-href');
		var title = $(this).attr('data-title') || $(this).attr('title');
		if(!title) title = $(this).text();
		var icon = $(this).attr('data-icon') || $(this).find('i.fa').attr('data-icon');

		$('#gloMenu').find('a.current').removeClass('current');
		$(this).addClass('current');

		Tab.tabAdd({
			title: title,
			href: href,
			icon: icon
		});
		return false;
	});

	$('#gloTop').find('.menuBar').click(function() {
		if($('#gloBox').hasClass('menu_close')) {
			$('#gloBox').removeClass('menu_close');
			$('.navT').find('span').each(function() {
				if($(this).hasClass('tooltip')) {
					$(this).css('display', 'none');
				} else {
					$(this).css('display', 'block');
				}
			});
			$('#gloLeft').find('ul').find('li').find('.navC').css('padding', '7px 8px');
		} else {
			$('#gloBox').addClass('menu_close');
			$('.navT').find('span').each(function() {
				if($(this).hasClass('tooltip')) {
					$(this).css('display', 'block');
				} else {
					$(this).css('display', 'none');
				}
			});
			$('#gloLeft').find('ul').find('li').find('.navC').css('padding', '0');
		}
	});

	$('#left_bar').find('.site-tree-mobile').click(function() {
		if($('#gloBox').hasClass('menu_close')) {
			$('#_icon').html('&#xe603;');
			$('#gloBox').removeClass('menu_close');
		} else {
			$('#_icon').html('&#xe602;');
			$('#gloBox').addClass('menu_close');
		}
	});

	$(window).resize(function() {
		initSize();
	});

	function initSize() {
		var screenWidth = $(window).width(); // 窗口宽度
		if(screenWidth < 992) {
			$('#gloBox').addClass('menu_close');
		}
	}

	$('.skin-down').hover(function() {
		$(this).find('.skin-show').stop(true, true).slideDown(300);
	}, function() {
		$(this).find('.skin-show').stop(true, true).slideUp(300);
	});

	// resize
	$(window).resize(function() {
		winWidth = $(window).width();
		heiHeght = $(window).height();
		$('#gloRght').height(heiHeght - 51);
		$('#gloLeft,#gloSLeft').css('height', (heiHeght - 51) + 'px');
		$('.layui-tab-content').height(heiHeght - 51 - 40);

	}).trigger('resize');

	//Tab
	$(window).resize(function() {
		if(typeof(Tab) != 'undefined') Tab.resize();
	});

	$('.tab-prev').unbind('click').bind('click', function() {
		var left = $('.layui-tab-title').position().left;
		left = left + 117 < 0 ? left + 117 : 0;
		$('.layui-tab-title').stop(true).animate({
			left: left
		}, 500);
	});

	$('.tab-next').unbind('click').bind('click', function() {
		var left = $('.layui-tab-title').position().left;
		var boxWid = $('.layui-tab-title').width();
		var liWid = 0;
		$('.layui-tab-title').children('span').remove().end().find('li').each(function() {
			liWid += $(this).outerWidth();
		});
		left = left - 117 > -(liWid - boxWid) ? left - 117 : -(liWid - boxWid);
		if(left > 0) left = 0;
		$('.layui-tab-title').stop(true).animate({
			left: left
		}, 500);
	});

	function full_screen() {
		var docElm = document.documentElement;
		// W3C
		if(docElm.requestFullscreen) {
			docElm.requestFullscreen();
		}
		//FireFox
		else if(docElm.mozRequestFullScreen) {
			docElm.mozRequestFullScreen();
		}
		// Chrome等
		else if(docElm.webkitRequestFullScreen) {
			docElm.webkitRequestFullScreen();
		}
		// IE11
		else if(docElm.msRequestFullscreen) {
			docElm.msRequestFullscreen();
		}
	}
});

function change_skin() {
	var skin = $(this).attr('data-skin');
	var color = $(this).css('background-color');
	$('.tooltip').attr('data-tip-bg', color);
	//$('.current').css('background-color', color);
	$('#gloBox').removeClass().addClass('layui-fluid').addClass(skin);
	if(!$('#gloBox').hasClass('menu_close')) {
		$('.navT').find('span').each(function() {
			if($(this).hasClass('tooltip')) {
				$(this).css('display', 'none');
			} else {
				$(this).css('display', 'block');
			}
		});
	}
}

var gloMenu = new Vue({
	el: '#gloMenu',
	data: {
		html: ''
	},
	created: function() {
		$.post('/index', function(res) {
			var json = res.data;
			var menu = getMenu(json, "");
			gloMenu.html = menu;
		});
	}
});

function getMenu(data, menu) {
	$.each(data, function(index, obj) {
		menu += "<li class='layui-nav-item '>";
		menu += "<div class='navT'>";
		
		var url = "javascript:;";
		if(obj.url != null) {
			url = obj.url;
		}
		if(obj.childList.length) {
			menu += "<span style='display: block;'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite><i data-icon='" + obj.icon + "' class='fa fa-angle-right animated' style='float: right; margin-top: 11px;'></i></span>";
			menu += "<span style='display: none;' class='tooltip' data-tip-text='" + obj.name + "' data-tip-bg='#66AFE2' data-title='" + obj.name + "' data-icon='" + obj.icon + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite></span>";
		} else {
			menu += "<a data-href='" + url + "' style='display: block;' kit-target data-id='" + obj.id + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite></a>";
			menu += "<a data-href='" + url + "' style='display: none;' class='tooltip' data-tip-text='" + obj.name + "' data-tip-bg='#66AFE2' data-title='" + obj.name + "' data-icon='" + obj.icon + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite></a>";
		}
		
		menu += "</div>";
		menu += "<div class='navC'>";
		menu += "<ul class='list'>";
		menu = menuChild(obj.childList, menu);
		menu += "</ul>";
		menu += "</div>";
		menu += "</li>";
	});
	
	return menu;
}

function menuChild(childList, menu) {
	if(!childList.length) {
		return menu;
	}
	
	$.each(childList, function(index, obj) {
		if(!obj.childList.length) {
			menu += "<li class='b'>";
			var url = "javascript:;";
			if(obj.url != null) {
				url = obj.url;
			}
			menu += "<a data-href='" + url + "' data-url='" + url + "' data-icon='" + obj.icon + "' data-title='" + obj.name + "' kit-target data-id='" + obj.id + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated'></i><cite>" + obj.name + "</cite></a>";
			menu += "</li>";
			menu += "<li class='s'>";
			menu += "<a data-href='" + url + "' class='tooltip' data-tip-text='" + obj.name + "' data-tip-bg='#66AFE2' data-title='" + obj.name + "' data-icon='" + obj.icon + "'><i class='" + obj.icon + "' style='margin-left: 0; margin-right: 0;'></i></a>";
			menu += "</li>";
		} else {
			menu += "<li class='layui-nav-item '>";
			menu += "<div class='navT'>";
			
			var url = "javascript:;";
			if(obj.url != null) {
				url = obj.url;
			}
			if(obj.childList.length) {
				menu += "<span style='display: block;'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite><i data-icon='" + obj.icon + "' class='fa fa-angle-right animated' style='float: right; margin-top: 11px;'></i></span>";
				menu += "<span style='display: none;' class='tooltip' data-tip-text='" + obj.name + "' data-tip-bg='#66AFE2' data-title='" + obj.name + "' data-icon='" + obj.icon + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite></span>";
			} else {
				menu += "<a data-href='" + url + "' style='display: block;' kit-target data-id='" + obj.id + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite></a>";
				menu += "<a data-href='" + url + "' style='display: none;' class='tooltip' data-tip-text='" + obj.name + "' data-tip-bg='#66AFE2' data-title='" + obj.name + "' data-icon='" + obj.icon + "'><i data-icon='" + obj.icon + "' class='" + obj.icon + " animated' style='color: rgb({rand(50,200)},{rand(50,200)},{rand(50,200)});'></i><cite>" + obj.name + "</cite></a>";
			}
			
			menu += "</div>";
			menu += "<div class='navC'>";
			menu += "<ul class='list'>";
			menu = menuChild(obj.childList, menu);
			menu += "</ul>";
			menu += "</div>";
			menu += "</li>";
		}
	});
	
	return menu;
}