/*function LoginUser(ns) {
	this.getUsername = function() {
		return window[(ns ? ns : '') + '_username'];
	}
	this.allow = function(permissionName) {
		var r = window[(ns ? ns : '') + '_check_perm_result']
		var ret = r.indexOf(permissionName) != -1 || r.indexOf('ALL') != -1;
		if (!ret) {// check ALL perm
			var paths = permissionName.split('.');
			var perm = paths[paths.length - 1];
			for (var i = 1; i < paths.length; i++) {
				paths[i] = paths[i - 1] + '.' + paths[i]
			}
			for (var i = 0; i < paths.length; i++) {
				if (r.indexOf(paths[i] + '.ALL') != -1) {
					return true;
				}
			}
			if (paths.length > 1) {
				for (var i = 0; i < paths.length - 1; i++) {
					if (r.indexOf(paths[i] + '.' + perm) != -1) {
						return true;
					}
				}
			}
		}
		return ret
	}
	this.filterAllowed = function(objlist, authNamePath) {
		authNamePath = authNamePath || 'authName'
		var hierarchy = authNamePath.indexOf('.') != -1;
		for (var i = objlist.length - 1; i >= 0; i--) {
			var authName;
			if (hierarchy) {
				authName = eval('(objlist[i].' + authNamePath + ')')
			} else {
				authName = objlist[i][authNamePath]
			}
			if (authName && !this.allow(authName)) {
				objlist.splice(i, 1);
			}
		}
		return objlist
	}
}

/** class for retrieving progress status of import/export *//*
function ProgressTimer(cfg) {
	var self = this;
	this.progressId = cfg.progressId;
	if (!this.progressId) {
		throw new Error('progressId is required!');
	}
	if (!cfg.initData || cfg.initData.active == null) {
		throw new Error('initData.active is required!');
	}
	this.active = cfg.initData.active;
	var progressText = cfg.progressText == 'percent' ? function(c, t) {
		return (t <= 0 ? 0 : parseInt(c * 100 / t)) + '%'
	} : function(c, t) {
		return c + "/" + t
	};
	var cb_finish = cfg.finish;
	var cb_inactive = cfg.inactive;
	var cb_error = cfg.error;
	var cb_poll = cfg.poll;
	var cb_scope = cfg.scope || this;

	cfg.boxConfig = cfg.boxConfig || {};
	this.updateButtonVisibility = function(progress) {
		if (!this.msgbox)
			return
		// this.msgbox.getDialog().getFooterToolbar().hide()
		Ext.each(this.msgbox.getDialog().buttons, function(btn) {
					if (progress && btn.text != '取消' && progress.finished)
						btn.enable()
					else if (progress && btn.text == '取消' && !progress.finished
							&& progress.cancelable)
						btn.enable()
					else
						btn.disable()
				})
		// this.msgbox.getDialog().getFooterToolbar().show()
		// this.msgbox.getDialog().hide()
		// this.msgbox.getDialog().show()
	}
	this.createMsgbox = function() {
		var m = Ext.Msg.show(Ext.apply({
			title : '导入',
			progress : true,
			progressText : '',
			buttons : {
				ok : '确定',
				cancel : '取消'
			},
			width : 300,
			fn : function(result) {
				if (result == 'cancel') {
					Ext.Ajax.request({
						url : './progress-timer!cancelProgress.action?progressId='
								+ self.progressId,
						success : function() {
						},
						failure : function() {
						}
					})
					m.getDialog().show()
				}
			}
		}, cfg.boxConfig));
		if (!window.__before_show_bind) {
			window.__before_show_bind = true;
			m.getDialog().on('beforeshow', function() {
						Ext.each(m.getDialog().buttons, function(btn) {
									btn.enable()
								})
					})
		}
		return m
	};
	this.msgbox = this.createMsgbox();
	this.updateButtonVisibility()
	var _requestImportProgress = function() {
		Ext.Ajax.request({
					url : './progress-timer!getProgress.action?progressId='
							+ self.progressId,
					success : function(resp) {
						if (!this.msgbox)
							return;
						var progress = Ext.util.JSON.decode(resp.responseText);
						var value = progress.totalCount
								? (progress.currentCount / progress.totalCount)
								: 0
						var text = progressText(progress.currentCount,
								progress.totalCount)
						this.msgbox.updateProgress(value, text,
								progress.message);
						if (progress.finished) {
							this.msgbox.updateProgress(1, progressText(
											progress.totalCount,
											progress.totalCount),
									progress.message);
							clearTimeout(this.importTimer);
							if (typeof cb_finish == 'function') {
								cb_finish.apply(cb_scope, [progress, resp])
							}
							this.updateButtonVisibility(progress);
						} else {
							if (typeof cb_poll == 'function')
								cb_poll.apply(cb_scope, [progress, resp])
							this.beginSchedule();
							this.updateButtonVisibility(progress)
						}
					},
					failure : function(resp) {
						if (!this.msgbox)
							return;
						if (typeof cb_error == 'function')
							cb_error.call(cb_scope, resp);
						else
							clearTimeout(this.importTimer);
						this.updateButtonVisibility(progress)
					},
					scope : self
				});
	}
	this.pause = function() {
		if (this.importTimer)
			clearTimeout(this.importTimer);
		if (this.msgbox) {
			this.msgbox.hide();
			delete this.msgbox
		}
	}
	this.restart = function(delay) {
		this.msgbox = this.createMsgbox();
		if (delay)
			_requestImportProgress.defer(delay, this)
		else
			this.beginSchedule();
	}
	this.beginSchedule = function() {
		if (this.importTimer)
			clearTimeout(this.importTimer);
		this.importTimer = window.setTimeout(function() {
					_requestImportProgress();
				}, 1000);
	}

	this.start = function() {
		if (!this.active)
			if (typeof cb_inactive == 'function')
				cb_inactive.call(cb_scope)
			else
				Ext.Msg.alert('提示', '当前有其他互斥操作正在进行，请稍后再试！');
		else {
			this.msgbox.updateProgress(0, progressText(0, 0), '');
			this.beginSchedule();
		}
		return this.active;
	}
}
ProgressTimer.createTimer = function(config, cb) {
	if (!(config instanceof Array)) {
		config = [config]
	}
	function poll() {
		if (!config.length)
			return
		var c = config.shift();
		if (c.initData) {
			if (cb) {
				var tm = new ProgressTimer(c);
				if (typeof cb == 'function')
					cb(tm)
				else
					tm.start()
			}
			return
		}
		Ext.Ajax.request({
					url : './progress-timer!getProgress.action?progressId='
							+ c.progressId,
					success : function(resp) {
						var progress = Ext.util.JSON.decode(resp.responseText);
						if (!progress.finished && cb) {
							c.initData = progress;
							var tm = new ProgressTimer(c);
							if (typeof cb == 'function') {
								cb(tm)
							} else {
								tm.start();
							}
						} else
							poll();
					}
				});
	}
	poll();
}
// EXT PART
Ext.Ajax.on('beforerequest', function(conn, options) {
			if (options.maskEl) {
				options.maskEl.mask('加载中...')
			}
		});
var __hide_loading_mask_1 = function(conn, response, options) {
	if (options.maskEl) {
		options.maskEl.unmask()
	}
}
function getTopWindow() {
	var topWin = window;
	try {
		while (topWin.parent && topWin.parent != topWin
				&& topWin.parent.location.href.indexOf('wrapper') == -1) {
			topWin = topWin.parent
		}
	} catch (e) {
	}
	return topWin;
}
var __error_alert_1 = function(conn, response, options) {
	__hide_loading_mask_1(conn, response, options)
	if (response.status == 403) {
		if (options.failure)
			delete options.failure
		var ec = response.getResponseHeader('error_code');
		if (ec == '1'){// logged in but target resource restricted
			Ext.Msg.alert('错误', '请求的资源未授权给当前登陆用户,请联系管理员');
		}else{
			Ext.Msg.alert('错误', '您还没有登陆，或者登陆已经超时。正在为您重定向到登陆页面...');
			getTopWindow().location = response.responseText
		}
	} else if (options && !options.failure)
		Ext.Msg.alert('错误', '服务器发生错误:'
						+ (response.responseText
								? response.responseText
								: '<br>'));
}
Ext.Ajax.on('requestcomplete', __hide_loading_mask_1);
Ext.Ajax.on('requestexception', __error_alert_1);
Ext.data.Connection.prototype.timeout = 300000
Ext.Window.prototype.constrainHeader = true
Ext.grid.GridView.prototype.sortAscText = '升序'
Ext.grid.GridView.prototype.sortDescText = '降序'
Ext.grid.GridView.prototype.columnsText = '列'*/
function LoginUser(ns) {
	this.getUsername = function() {
		return window[(ns ? ns : '') + '_username'];
	}
	this.allow = function(permissionName) {
		var r = window[(ns ? ns : '') + '_check_perm_result']
		var ret = r.indexOf(permissionName) != -1 || r.indexOf('ALL') != -1;
		if (!ret) {// check ALL perm
			var paths = permissionName.split('.');
			var perm = paths[paths.length - 1];
			for (var i = 1; i < paths.length; i++) {
				paths[i] = paths[i - 1] + '.' + paths[i]
			}
			for (var i = 0; i < paths.length; i++) {
				if (r.indexOf(paths[i] + '.ALL') != -1) {
					return true;
				}
			}
			if (paths.length > 1) {
				for (var i = 0; i < paths.length - 1; i++) {
					if (r.indexOf(paths[i] + '.' + perm) != -1) {
						return true;
					}
				}
			}
		}
		return ret
	}
	this.find = function(permissionNamePattern) {
		var r = window[(ns ? ns : '') + '_check_perm_result']
		var ret = []
		for (var i = 0; i < r.length; i++) {
			if (r[i].match(permissionNamePattern))
				ret.push(r[i]);
		}
		return ret
	}
	this.filterAllowed = function(objlist, authNamePath, authNamePatternPath) {
		authNamePath = authNamePath || 'authName'
		authNamePatternPath = authNamePatternPath || 'authNamePattern'
		var hierarchy = authNamePath.indexOf('.') != -1;
		var hierarchyP = authNamePatternPath.indexOf('.') != -1;
		for (var i = objlist.length - 1; i >= 0; i--) {
			var authName;
			var removeMe = false;
			if (hierarchy) {
				authName = eval('(objlist[i].' + authNamePath + ')')
			} else {
				authName = objlist[i][authNamePath]
			}
			if (authName) {
				if (!this.allow(authName))
					removeMe = true
			} else {
				var authNamePattern;
				if (hierarchyP) {
					authNamePattern = eval('(objlist[i].' + authNamePatternPath + ')')
				} else {
					authNamePattern = objlist[i][authNamePatternPath]
				}
				if (authNamePattern && !this.find(authNamePattern).length)
					removeMe = true
			}
			if (removeMe) {
				objlist.splice(i, 1);
			}
		}
		return objlist
	}
}

/** class for retrieving progress status of import/export */
function ProgressTimer(cfg) {
	var self = this;
	this.progressURL = cfg.progressURL;
	this.progressId = cfg.progressId;
	this.progress = cfg.progress;
	if (!this.progressId) {
		throw new Error('progressId is required!');
	}
	if (!cfg.initData || cfg.initData.active == null) {
		throw new Error('initData.active is required!');
	}
	this.active = cfg.initData.active;
	this.initData = cfg.initData;
	var progressText = cfg.progressText == 'percent' ? function(c, t) {
		return (t <= 0 ? 0 : parseInt(c * 100 / t)) + '%'
	} : function(c, t) {
		return c + "/" + t
	};
	var cb_finish = cfg.finish;
	var cb_inactive = cfg.inactive;
	var cb_error = cfg.error;
	var cb_poll = cfg.poll;
	var cb_scope = cfg.scope || this;

	cfg.boxConfig = cfg.boxConfig || {};
	this.updateButtonVisibility = function(progress) {
		if (!this.msgbox)
			return
		// this.msgbox.getDialog().getFooterToolbar().hide()
		Ext.each(this.msgbox.getDialog().buttons, function(btn) {
					if (progress && btn.text != '取消' && progress.finished)
						btn.enable()
					else if (progress && btn.text == '取消' && !progress.finished
							&& progress.cancelable) {
						if (cfg.isCancelDisabled) {
							btn.disable();
						} else {
							btn.enable();
						}
					} else
						btn.disable()
				})
		// this.msgbox.getDialog().getFooterToolbar().show()
		// this.msgbox.getDialog().hide()
		// this.msgbox.getDialog().show()
	}
	this.createMsgbox = function(config) {
		config = Ext.apply(config || {}, cfg.boxConfig)
		var m = Ext.Msg.show(Ext.apply({
					title : '导入',
					progress : typeof(self.progress) == 'undefined' ? true : self.progress,
					progressText : '',
					buttons : {
						ok : '确定',
						cancel : '取消'
					},
					width : 300,
					fn : function(result) {
						if (result == 'cancel') {
							Ext.Ajax.request({
										url : (self.progressURL||'../progress-timer!cancelProgress.action')+'?progressId=' + self.progressId,
										success : function() {
										},
										failure : function() {
										}
									})
							m.getDialog().show()
						}
					}
				}, config));
		if (!window.__before_show_bind) {
			window.__before_show_bind = true;
			m.getDialog().on('beforeshow', function() {
						Ext.each(m.getDialog().buttons, function(btn) {
									btn.enable()
								})
					})
		}
		return m
	};
	this.msgbox = this.createMsgbox();
	this.updateButtonVisibility()
	var _requestImportProgress = function() {
		Ext.Ajax.request({
					url : (self.progressURL||'../progress-timer!getProgress.action')+'?progressId=' + self.progressId,
					success : function(resp) {
						if (!this.msgbox)
							return;
						var progress = Ext.util.JSON.decode(resp.responseText);
						var value = progress.totalCount ? (progress.currentCount / progress.totalCount) : 0
						var text = progressText(progress.currentCount, progress.totalCount)
						this.msgbox.updateProgress(value, text, progress.message);
						if (progress.finished) {
							if (progress.multilineMsg) {
								this.createMsgbox({
											width : 600
										})
								this.msgbox.updateProgress(1, progressText(progress.totalCount, progress.totalCount), progress.message);
								addMsgBoxDetail(progress.multilineMsg)
							}else{
								this.msgbox.updateProgress(1, progressText(progress.totalCount, progress.totalCount), progress.message);
							}
							clearTimeout(this.importTimer);
							if (typeof cb_finish == 'function') {
								cb_finish.apply(cb_scope, [progress, resp])
							}
							this.updateButtonVisibility(progress);
						} else {
							if (typeof cb_poll == 'function')
								cb_poll.apply(cb_scope, [progress, resp])
							this.beginSchedule();
							this.updateButtonVisibility(progress)
						}
					},
					failure : function(resp) {
						if (!this.msgbox)
							return;
						if (typeof cb_error == 'function')
							cb_error.call(cb_scope, resp);
						else
							clearTimeout(this.importTimer);
						this.updateButtonVisibility(progress)
					},
					scope : self
				});
	}
	this.pause = function() {
		if (this.importTimer)
			clearTimeout(this.importTimer);
		if (this.msgbox) {
			this.msgbox.hide();
			delete this.msgbox
		}
	}
	this.restart = function(delay) {
		this.msgbox = this.createMsgbox();
		if (delay)
			_requestImportProgress.defer(delay, this)
		else
			this.beginSchedule();
	}
	this.beginSchedule = function() {
		if (this.importTimer)
			clearTimeout(this.importTimer);
		this.importTimer = window.setTimeout(function() {
					_requestImportProgress();
				}, 1000);
	}

	this.start = function() {
		if (!this.active)
			if (typeof cb_inactive == 'function')
				cb_inactive.call(cb_scope)
			else
				Ext.Msg.alert('提示', this.initData&&this.initData.message?this.initData.message:'当前有其他互斥操作正在进行，请稍后再试！');
		else {
			this.msgbox.updateProgress(0, progressText(0, 0), '');
			this.beginSchedule();
		}
		return this.active;
	}
}
ProgressTimer.createTimer = function(config, cb) {
	if (!(config instanceof Array)) {
		config = [config]
	}
	function poll() {
		if (!config.length)
			return
		var c = config.shift();
		if (c.initData) {
			if (cb) {
				var tm = new ProgressTimer(c);
				if (typeof cb == 'function')
					cb(tm)
				else
					tm.start()
			}
			return
		}
		Ext.Ajax.request({
					url : (c.progressURL||'../progress-timer!getProgress.action')+'?progressId=' + c.progressId,
					success : function(resp) {
						var progress = Ext.util.JSON.decode(resp.responseText);
						if (!progress)
							return;
						if (!progress.finished && cb) {
							c.initData = progress;
							var tm = new ProgressTimer(c);
							if (typeof cb == 'function') {
								cb(tm)
							} else {
								tm.start();
							}
						} else
							poll();
					}
				});
	}
	poll();
}
// EXT PART
Ext.Ajax.on('beforerequest', function(conn, options) {
			if (options.maskEl) {
				options.maskEl.mask('加载中...')
			}
		});
var __hide_loading_mask_1 = function(conn, response, options) {
	if (options.maskEl) {
		options.maskEl.unmask()
	}
}
function getTopWindow() {
	var topWin = window;
	try {
		while (topWin.parent && topWin.parent != topWin && topWin.parent.location.href.indexOf('wrapper') == -1) {
			topWin = topWin.parent
		}
	} catch (e) {
	}
	return topWin;
}
var __error_alert_1 = function(conn, response, options) {
	__hide_loading_mask_1(conn, response, options)
	if (response.status == 403) {
		if (options.failure)
			delete options.failure
		var ec = response.getResponseHeader('error_code');
		if (ec == '1') {// logged in but target resource restricted
			Ext.Msg.alert('错误', '请求的资源未授权给当前登陆用户,请联系管理员');
		} else {
			var redirect_url = response.getResponseHeader('redirect_url');
			if (redirect_url){
				Ext.Msg.alert('错误', '您还没有登陆，或者登陆已经超时。正在为您重定向到登陆页面...',function(){
					getTopWindow().location = redirect_url
				});
			}else{
				Ext.Msg.alert('错误', '您还没有登陆，或者登陆已经超时。请重新登陆！');
			}
		}
	} else if (options && !options.failure)
		Ext.Msg.alert('错误', '服务器发生错误:' + (response.responseText ? response.responseText : '<br>'));
}
Ext.Ajax.on('requestcomplete', __hide_loading_mask_1);
Ext.Ajax.on('requestexception', __error_alert_1);
Ext.data.Connection.prototype.timeout = 300000
Ext.Window.prototype.constrainHeader = true
Ext.grid.GridView.prototype.sortAscText = '升序'
Ext.grid.GridView.prototype.sortDescText = '降序'
Ext.grid.GridView.prototype.columnsText = '列'
Ext.override(Ext.Element, {
			// fix IE7,8 :dom.style.height cannot accept 'NaNpx'
			addUnits : function(size) {
				if (size === "" || size == "auto" || size === undefined || isNaN(size)) {
					size = size || '';
				} else if (!isNaN(size) || !unitPattern.test(size)) {
					size = size + (this.defaultUnit || 'px');
				}
				return size;
			}
		})
Function.prototype.dg = Function.prototype.createDelegate;
(function() {
	if (window.ext_rowexpander_bodystyle) {
		function applyStyle(dom, style) {
			if (dom && dom.tagName) {
				for (var key in style) {
					dom.style[key] = style[key]
				}
			}
			if (dom.childNodes)
				for (var i = 0; i < dom.childNodes.length; i++) {
					applyStyle(dom.childNodes[i], style)
				}
		}
		var _beforeExpand = Ext.grid.RowExpander.prototype.beforeExpand
		Ext.override(Ext.grid.RowExpander, {
					beforeExpand : function(r, body, row) {
						var ret = _beforeExpand.apply(this, arguments)
						applyStyle(body, Ext.decode(ext_rowexpander_bodystyle))
						return ret
					}
				})
	}
})();
Ext.DomObserver = Ext.extend(Object, {
    constructor: function(config) {
        this.listeners = config.listeners ? config.listeners : config;
    },

    // Component passes itself into plugin's init method
    init: function(c) {
        var p, l = this.listeners;
        for (p in l) {
            if (Ext.isFunction(l[p])) {
                l[p] = this.createHandler(l[p], c);
            } else {
                l[p].fn = this.createHandler(l[p].fn, c);
            }
        }

        // Add the listeners to the Element immediately following the render
		// call
        c.render = c.render.createSequence(function() {
            var e = c.getEl();
            if (e) {
                e.on(l);
            }
        });
    },

    createHandler: function(fn, c) {
        return function(e) {
            fn.call(this, e, c);
        };
    }
});
var DetailMsgBoxFormat = '<textarea style="overflow:auto;height:100px;width:100%;border:1px dotted grey;margin-top:5px;background:transparent">{0}</textarea>'
function addMsgBoxDetail(detail) {
	if (!window.addMsgBoxDetail_onhide) {
		window.addMsgBoxDetail_onhide = true;
		Ext.Msg.getDialog().on('hide', function() {
					var n = Ext.Msg.getDialog().getEl().query('.msgboxDetail')[0];
					if (n)
						n.parentNode.removeChild(n)
				})
	}
	if (detail instanceof Array) {
		detail = detail.join('\r\n')
	}
	detail = String.format(DetailMsgBoxFormat, detail)
	Ext.DomHelper.append(Ext.Msg.getDialog().getEl().query('.ext-mb-content')[0], {
				tag : 'div',
				'class' : 'msgboxDetail',
				html : detail
			})
}

function getxhr() {
		var _xhr = false;
		try {
			_xhr = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				_xhr = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e2) {
				_xhr = false;
			}
		}
		if (!_xhr && window.XMLHttpRequest)
			_xhr = new XMLHttpRequest();
		return _xhr;
}
function http_get(url){
	var xhr = getxhr();
	if (url.indexOf('?') == -1)
		url += '?'
	url+='&_ts='+new Date().getTime()
	xhr.open('GET', url, false);
	xhr.send();
	return xhr.responseText;
}
function getItem(itemid, c) {
	return c.find('itemId', itemid)[0]
}
var __raw_ext_encode = Ext.encode
Ext.encode = function(data,dateformat){
	var _raw_ed = Ext.util.JSON.encodeDate;
	try{
		if (dateformat){
			Ext.util.JSON.encodeDate = function(d) {
			    return d.format('"'+dateformat+'"');
			};
		}
		return __raw_ext_encode.call(this,data)
	}finally{
		if (dateformat) Ext.util.JSON.encodeDate  = _raw_ed
	}
}
Ext.onReady(function(){
	var _set_method=Ext.data.Record.prototype._set_method||Ext.data.Record.prototype.set 
	Ext.override(Ext.data.Record, {
		set : function(key, value) {
			if (!key) {
				return;
			}
			if (value && /.*<[^>]*on(error|load).*/.test(value)) {
				Ext.Msg.alert('警告', '您填写的内容含有非法字符，请检查。');
				return false;
			}
			return _set_method.apply(this, arguments)
		}
	});
	Ext.Ajax.on('beforerequest', function(conn, opts) {
		if (opts && opts.params) {
			try {
				var value = Ext.encode(opts.params);
				if (value && /.*<[^>]*on(error|load).*/.test(value)) {
					if (opts.failure)
						opts.failure.call(this, {responseText : '您填写的内容含有非法字符，请检查。'}, opts);
					return false;
				}
			}catch(e){}
		}
	});
	window.AUTH = {
		authorize : function(u, perm, cb, panel) {
			if (u && !u.allow0) {
				u.allow0 = function(_perm) {
					if (!u.allowPerms0)
						u.allowPerms0 = [];
					var idx = u.allowPerms0.indexOf(_perm);
					if (idx == -1)
						return u.allow(_perm);
					return true;
				};
				u.addPerm0 = function(_perm) {
					if (!u.allowPerms0)
						u.allowPerms0 = [];
					u.allowPerms0.push(_perm);
				};
			}
			if (u && u.allow0(perm)) {
				cb.call(panel);
				return;
			}
			if (!panel.authWin) {
				var pwd =  new Ext.form.TextField({
						name : 'password',
						fieldLabel : '密码',
						inputType: 'password'
					});
				var permField =  new Ext.form.Hidden({
						name : 'perm',
						ref : 'permField'
					});
				var authForm = new Ext.form.FormPanel({
					frame : false,
					border : false,
					labelWidth : 70,
					autoHeight : true,
					waitMsgTarget : true,
					bodyStyle : 'padding : 3px 10px; background-color:#d3e1f1;',
					defaults : {
						anchor : '96%',
						allowBlank : false
					},
					items : [ new Ext.form.TextField({
						name : 'username',
						fieldLabel : '用户名'
					}), pwd, permField],
					tbar : [ {
						text : '授权',
						iconCls : 'icon-ok',
						handler : function() {
							var btn = this;
							btn.disable();
							var valid = authForm.getForm().isValid();
							if (valid) {
								authForm.getForm().submit({
									url : 'auth!authorize.action',
									waitMsg : '等待授权结果...',
									params : {
										password0 : CryptoJS.SHA1(pwd.getValue())
									},
									success : function(form, a) {
										var ret = Ext.decode(a.response.responseText);
										var authorized = ret.data;
										if (authorized) {
											u.addPerm0(authForm.permField.getValue());
											authForm.cb.call(panel);
											panel.authWin.hide();
											panel.getEl().unmask();
										}
										else
											Dashboard.setAlert('授权失败，请检查授权用户是否有该权限。');
										btn.enable();
									},
									failure : function(form, act) {
										Ext.Msg.alert('错误', act.result.message);
										btn.enable();
										panel.getEl().unmask();
									}
								});
							} else {
								Dashboard.setAlert('请正确填写授权者的用户名密码。');
								btn.enable();
							}
						}
					}]
				});
				panel.authWin = new Ext.Window(Ext.apply({
					width : 300,
					title : '需要授权',
					items : [ authForm ]
				}, {
					defaults : {
						border : false
					},
					modal : true,
					plain : true,
					shim : true,
					closable : true,
					closeAction : 'hide',
					collapsible : true,
					resizable : false,
					draggable : true,
					animCollapse : true,
					constrainHeader : true,
					shadow : false,
					listeners : {
						'beforehide' : function(p) {
							p.form.getForm().reset();
							panel.getEl().unmask();
						}
					}
				}));
				panel.authWin.form = authForm;
			}
			panel.authWin.form.permField.setValue(perm);
			panel.authWin.form.cb = cb;
			panel.authWin.show();
			panel.getEl().mask();
		}
	};
});

if (!Ext.isDefined(Ext.webKitVersion)) {
    Ext.webKitVersion = Ext.isWebKit ? parseFloat(/AppleWebKit\/([\d.]+)/.exec(navigator.userAgent)[1], 10) : NaN;
}
/*
 * Box-sizing was changed beginning with Chrome v19. For background information,
 * see: http://code.google.com/p/chromium/issues/detail?id=124816
 * https://bugs.webkit.org/show_bug.cgi?id=78412
 * https://bugs.webkit.org/show_bug.cgi?id=87536
 * http://www.sencha.com/forum/showthread.php?198124-Grids-are-rendered-differently-in-upcoming-versions-of-Google-Chrome&p=824367
 * 
 */
if (Ext.isWebKit && Ext.webKitVersion >= 535.2) { // probably not the exact
													// version, but the issues
													// started appearing in
													// chromium 19
    Ext.override(Ext.grid.ColumnModel, {
        getTotalWidth: function (includeHidden) {
            if (!this.totalWidth) {
                var boxsizeadj = 2;
                this.totalWidth = 0;
                for (var i = 0, len = this.config.length; i < len; i++) {
                    if (includeHidden || !this.isHidden(i)) {
                        this.totalWidth += (this.getColumnWidth(i) + boxsizeadj);
                    }
                }
            }
            return this.totalWidth;
        }
    });


    Ext.onReady(function() {
        Ext.get(document.body).addClass('ext-chrome-fixes');
        Ext.util.CSS.createStyleSheet('@media screen and (-webkit-min-device-pixel-ratio:0) {.x-grid3-cell{box-sizing: border-box !important;}}', 'chrome-fixes-box-sizing');
    });
}