layui.use(['form', 'layer', 'upload'], function() {
	form = layui.form;
	layer = parent.layer === undefined ? layui.layer : top.layer;
	upload = layui.upload;
	
	var type;
	if($('.id').val()) {
		type = 'PUT';
	} else {
		type = 'POST';
	}
	var uploadListIns = upload.render({
		elem: '.userFaceBtn',
		url: '/model',
		method: type,
		auto: false,
		bindAction: '#create',
		accept: 'file',
		exts: 'xlsx|xls',
		field: 'file',
		size: 1024,
		headers: {
			'contentType': 'multipart/form-data'
		},
		choose: function(obj) {
			var trs = $('#demoList').find('tr');
			if (trs.length) {
				layer.msg('只能上传一个文件', {icon: 2});
			} else {
				var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
				//读取本地文件
			    obj.preview(function(index, file, result) {
			    	var tr = $(['<tr id="upload-'+ index +'">', '<td>'+ file.name +'</td>',
			    		'<td>'+ (file.size/1014).toFixed(1) +'kb</td>', '<td>等待上传</td>',
			    		'<td>', '<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>',
			    		'</td>', '</tr>'].join(''));
			    	
			    	//单个重传
			    	tr.find('.demo-reload').on('click', function() {
				        obj.upload(index, file);
				    });
			    	
			    	//删除
			        tr.find('.demo-delete').on('click', function(){
			          delete files[index]; //删除对应的文件
			          tr.remove();
			          uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
			        });
			        
			        $('#demoList').append(tr);
			    });
			}
		},
		before: function(obj) {
			this.data = $('#operateConfig').serializeObject();
			layer.load();
		},
		done: function(res, index, upload) {
			layer.closeAll('loading');
			layer.msg(res.msg, {icon: 6});
			setTimeout(function() {
				layer.closeAll("iframe");
				parent.location.reload();
			}, 2000);
		},
		error: function(index, upload) {
			layer.closeAll('loading');
			layer.msg('上传失败', {icon: 5});
		}
	});
});