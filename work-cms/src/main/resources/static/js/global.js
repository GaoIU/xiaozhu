$(document).ready(function() {
    $(document).on('click', '.new_tab',
    function(ev) {

        var title = $(this).attr('data-title') ? $(this).attr('data-title') : $(this).text();
        var href = $(this).attr('data-href');
        var icon = $(this).attr('data-icon') ? $(this).attr('data-icon') : $(this).find('i').attr('data-icon');

        if (parent && parent.Tab) {
            parent.Tab.tabAdd({
                title: title,
                href: href,
                icon: icon
            });
        } else {
            Tab.tabAdd({
                title: title,
                href: href,
                icon: icon
            });
        }
        if (ev && ev.preventDefault) ev.preventDefault();
        else window.event.returnValue = false;
        return false;
    }).on('click', '.javascript',
    function(ev) {
        var callback;

        if (callback = $(this).attr('rel')) {
            if (window[callback]) {
                window[callback].call(this);
            }
        }
        if (ev && ev.preventDefault) ev.preventDefault();
        else window.event.returnValue = false;
        return false;
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
});

function basePath() {
	return window.location.protocol + "//" + window.location.host;
}

$.fn.extend({
    serializeObject : function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if(this.name != "file"){//排除文件上传
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [ o[this.name] ];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            }
        });
        return o;
    }
});

Vue.filter('datetime', function (value, formatString) {
	if(!value) {
		return "";
	}
	
    formatString = formatString || 'YYYY-MM-DD HH:mm:ss';
    return moment(value).format(formatString);
});

Vue.filter('date', function (value, formatString) {
	if(!value) {
		return "";
	}
	
    formatString = formatString || 'YYYY-MM-DD';
    return moment(value).format(formatString);
});

Vue.filter('time', function (value, formatString) {
	if(!value) {
		return "";
	}
	
    formatString = formatString || 'HH:mm:ss';
    return moment(value).format(formatString);
});

Vue.filter('num', function(value) {
	if(!value) {
		return 0;
	}
	
	var intPart = parseInt(Number(value));
	var val = intPart.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,');
	if(value.toString().indexOf("\.") != -1) {
		var items = value.toString().split("\.");
		val = val.toString() + "." + items[1];
	}
	
	return val;
});