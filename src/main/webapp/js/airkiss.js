wx.config({
	beta : true, // 开启内测接口调用，注入wx.invoke方法
	debug : false, // 开启调试模式
	appId : 'wx30dfc8e35ceace1a', // 第三方app唯一标识
	timestamp : _ts, // 生成签名的时间戳
	nonceStr : 'A1B2C3D4E5F6G7H8', // 生成签名的随机串
	signature : _sign,// 签名
	jsApiList : [ 'configWXDeviceWiFi' ]
});

wx.ready(function() {
	wx.invoke('configWXDeviceWiFi', {}, function(res) {
		if (res) {
			if (res.err_msg == 'configWXDeviceWiFi:ok') {
				alert('配置成功')
			} else if (res.err_msg == 'configWXDeviceWiFi:fail') {
				alert('配置失败');
			} else if (res.err_msg == 'configWXDeviceWiFi:cancel') {
				alert('用户取消');
			}
			wx.closeWindow();
		}
	});
});

wx.error(function(res) {
	for ( var key in res) {
		alert('err:' + key + '=' + res[key]);
	}
});