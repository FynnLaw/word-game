    Ext.QuickTips.init();
   	Ext.onReady(function(){
   		Ext.override(Ext.menu.DateMenu, {  
   		    render : function() {  
   		        Ext.menu.DateMenu.superclass.render.call(this);  
   		        if (Ext.isGecko || Ext.isSafari || Ext.isChrome) {  
   		            this.picker.el.dom.childNodes[0].style.width = '178px';  
   		            this.picker.el.dom.style.width = '178px';  
   		        }  
   		    }  
   		}); 
   		
   		var qrUrl = path + "/deviceqr!";
   		var order;
        store = new Ext.data.Store({
			url : qrUrl+"list.action",
			reader : new Ext.data.JsonReader({
				root : 'products',
				totalProperty : 'totalCount',
				id : 'querydate',
				fields : [
					{name:  'originalRomName'},
					{name : 'version'},
					{name : 'type'},
					{name : 'createTime'},
					{name : 'modifyTime'},
					{name : 'id'},
					{name : 'comment'}
					
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:20}});
        
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
            	{header:"文件名称",align:'center',dataIndex:"originalRomName",sortable:true}, 
	            {header:"版本号",align:'center',dataIndex:"version",sortable:true}, 
	            {header:"类别",align:'center',dataIndex:"type",sortable:true,renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){    
                    if(value=='0'){
                        return '可选更新';
                      } else {
                        return '强制更新';
                       }
	            }},
	            {header:"创建时间",align:'center',dataIndex:"createTime",sortable:true},
	            {header:"修改时间",align:'center',dataIndex:"modifyTime",sortable:true},
	            {header:"操作",align:'center',dataIndex:"id",width:50,
	            renderer: function (value, meta, record) {
					            			var formatStr = "<input id = 'bt_edit_" + record.get('id')
							+ "' onclick=\"showEditRom('" + record.get('id') + "','"
							+ record.get('originalRomName') + "','"
							+ record.get('version')+"','"
							+ record.get('type')+"','"
							+ record.get('comment')
							+ "');\" type='button' value='编辑' width ='15px'/>&nbsp;&nbsp;"; 

										     var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteRom('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>";
										            			
            				var resultStr = String.format(formatStr);
            				return "<div>" + resultStr+deleteBtn + "</div>";
        				  } .createDelegate(this)
	            } 
            ] 
        ); 
        
        var initDate = new Date();
    	initDate.setDate(1);
    	initDate.setHours(0, 0, 0, 0);
    	
    	var endTimeField = new ClearableDateTimeField({
    		id:"createDateEnd",
    		editable : false,
    		width : 160
    	});
    	
    	var beginTimeField = new ClearableDateTimeField({
    		id:"createDateStart",
    		editable : false,
//    		value : initDate,
    		width : 160,
    		//fieldLabel : '创建时间',
    	}); 
    	this.beginTimeField = beginTimeField;
    	
    	this.endTimeField = endTimeField;
    	beginTimeField.on('change', function(o, v) {
    		// if (v) {
    		// endTimeField.setMinValue(v);
    		// var max = new Date();
    		// max.setFullYear(v.getFullYear());
    		// max.setMonth(v.getMonth() + 1);
    		// max.setDate(0);
    		// endTimeField.setMaxValue(max);
    		// }
    	});
    	beginTimeField.fireEvent('change', beginTimeField, initDate);
    	endTimeField.on('change', function(o, v) {
    		// beginTimeField.setMaxValue(v);
    		// var min = new Date();
    		// min.setFullYear(v.getFullYear());
    		// min.setMonth(v.getMonth());
    		// min.setDate(1);
    		// beginTimeField.setMinValue(min);
    	});
    	
    	
    	var endTimeField1 = new ClearableDateTimeField({
    		id:"editDateEnd",
    		editable : false,
    		width : 160
    	});
    	
    	var beginTimeField1 = new ClearableDateTimeField({
    		id:"editDateStart",
    		editable : false,
//    		value : initDate,
    		width : 160,
    	}); 
    	this.beginTimeField1 = beginTimeField1;
    	
    	this.endTimeField1 = endTimeField1;
    	beginTimeField1.on('change', function(o, v) {
    	});
    	beginTimeField1.fireEvent('change', beginTimeField, initDate);
    	endTimeField1.on('change', function(o, v) {
    	});
       
        
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['版本号：', {
		  		  id : 'searchItemText',
		  		  emptyText : "请输入版本号",
		  		  xtype : 'textfield',
		  		  width : 115,
		  		  listeners : {
		  			  specialkey : function(f, e) {
		  				  if (e.getKey() == e.ENTER) {
		  					  self.searchItem(f);
		  				  }
		  			  }
		  		  }
		  	},'&nbsp;&nbsp;创建时间：',
		  	beginTimeField,'至',
		  	endTimeField,'&nbsp;&nbsp;编辑时间：',
		  	beginTimeField1,'至',endTimeField1, {
				text : '查询',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				handler : function() {
					Ext.getCmp('searchItemText').setValue("");
					Ext.getCmp('createDateStart').setValue("");
					Ext.getCmp('createDateEnd').setValue("");
					Ext.getCmp('editDateStart').setValue("");
					Ext.getCmp('editDateEnd').setValue("");
				}
			},{
				text : '添加新版本',
				handler : function() {
//					showEditRom('123','asdkahs','3','4','5');
					showEditRom();
				}
			}]

        });  
        var grid = new Ext.grid.EditorGridPanel({ 
			region:'center',
			border:false,
//			autoHeight:true,
			viewConfig: {
	            forceFit: true, //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
	            emptyText: '系统中还没有任务'
	        },
            cm:column, 
            store:store, 
            autoExpandColumn:0, 
            loadMask:true, 
            frame:true, 
            autoScroll:true, 
            tbar:tbar,
            bbar:new Ext.PagingToolbar({
					store : store,
					displayInfo : true,
					pageSize : 20,
					prependButtons : true,
					beforePageText : '第',
					afterPageText : '页，共{0}页',
					displayMsg : '第{0}到{1}条记录，共{2}条',
					emptyMsg : "没有记录"
				}),
        });
        
	
      
  	var mainPanel = new Ext.Panel({
  		region:"center",
		layout:'border',
		border:false,
		items:[grid],
	});
	
   var viewport=new Ext.Viewport({
       //enableTabScroll:true,
       layout:"border",
       items:[
           mainPanel
   	   ]
   });
   });
   
   function reloadData(){
		var beginTime = this.beginTimeField.getValue();
		var endTime = this.endTimeField.getValue();
		if (!beginTime && !endTime) {
			var _bt = new Date();
			_bt.setDate(1);
			_bt.setHours(0, 0, 0, 0);
//			this.beginTimeField.setValue(_bt);
			beginTime = _bt;
		}
		if (beginTime && endTime) {
			if (beginTime.getTime() >= endTime.getTime()) {
				Ext.Msg.alert('提示', '创建开始时间不能晚于结束时间');
				return false;
			}
		}
		var beginTimeStr = '', endTimeStr = '';
		if (beginTime)
			beginTimeStr = beginTime.format('Y-m-d H:i:s');
		if (endTime)
			endTimeStr = endTime.format('Y-m-d H:i:s');
		
		var beginTime1 = this.beginTimeField1.getValue();
		var endTime1 = this.endTimeField1.getValue();
		if (!beginTime1 && !endTime1) {
			var _bt1 = new Date();
			_bt1.setDate(1);
			_bt1.setHours(0, 0, 0, 0);
//			this.beginTimeField1.setValue(_bt);
			beginTime1 = _bt1;
		}
		if (beginTime1 && endTime1) {
			if (beginTime1.getTime() >= endTime1.getTime()) {
				Ext.Msg.alert('提示', '编辑开始时间不能晚于结束时间');
				return false;
			}
		}
		var beginTimeStr1 = '', endTimeStr1 = '';
		if (beginTime1)
			beginTimeStr1 = beginTime1.format('Y-m-d H:i:s');
		if (endTime1)
			endTimeStr1 = endTime1.format('Y-m-d H:i:s');
	   
	   
	   
//		var version = searchPanel.form.getValues().robotVision;
		var version = document.getElementById('searchItemText').value ;
		var createDateStart = document.getElementById('createDateStart').value ;
		var createDateEnd = document.getElementById('createDateEnd').value ;
		
		
		var editDateStart = document.getElementById('editDateStart').value ;
		var editDateEnd = document.getElementById('editDateEnd').value ;
		if(version == "请输入版本号"){
			version == "";
		}
		store.baseParams['version'] = version;
		store.baseParams['createDateStart'] = createDateStart;
		store.baseParams['createDateEnd'] = createDateEnd;
		store.baseParams['editDateStart'] = editDateStart;
		store.baseParams['editDateEnd'] = editDateEnd;
		store.reload({
			params: {start:0,limit:10},
			callback: function(records, options, success){
//				console.log(records);
			},
			scope: store
		});
	}
	
	
	function saveInfo(oldName,newName,_id){
		//console.log(_id);
		if(oldName != newName){
			Ext.Msg.confirm('保存数据', '确认?',function (button,text){if(button == 'yes'){
				Ext.Ajax.request( {
					  url : path + "/deviceqr!updateNickName.action",
					  method : 'post',
					  params : {
					   newName : newName,
					   did : _id
					  },
					  success : function(response, options) {
					   var o = Ext.util.JSON.decode(response.responseText);
					   //alert(o.i_type);
					   if(o.i_type && "success"== o.i_type){
					   	
					   }else{
					   	   Ext.Msg.alert('提示', '保存失败'); 
					   }
					  },
					  failure : function() {
					  	
					  }
		 		});
			}});
		}
	}
	
	function deleteRom(id){
		Ext.Msg.confirm('删除数据', '确认?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request( {
				  url : path + "/deviceqr!deleteRomById.action",
				  method : 'post',
				  params : {
					  id:id
				  },
				  success : function(response, options) {
				   var o = Ext.util.JSON.decode(response.responseText);
				   //alert(o.i_type);
				   if(o.i_type && "success"== o.i_type){
//					   Ext.Msg.alert('提示', '删除成功'); 
					   reloadData();
				   }else{
				   	   Ext.Msg.alert('提示', o.i_msg); 
				   }
				  },
				  failure : function() {
					  Ext.Msg.alert('提示', '删除失败'); 
				  }
	 		});
		}});
		
	}
    function showEditRom(_romId,_romOriginalName,_romVersion,_romType,_romComment){
    	 var _data = [
			['0','可选更新'],
			['1','强制更新'],
		];
    	
    	var co = new Ext.form.ComboBox({
    		fieldLabel: '分类',
    		name:'romType',
    		forceSelection: true,
    		listWidth: 150,
    		width:150,
    		store: new Ext.data.SimpleStore({
    		fields: ['value', 'text'],
    		data : _data
    		}),
    		valueField:'value',
    		displayField:'text',
    		typeAhead: true,
    		mode: 'local',
    		triggerAction: 'all',
    		selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
    		allowBlank:false,
    		editable:false
    	})
    	if(typeof(_romType) != "undefined" && _romType  != ""){
    		co.setValue(_romType);
    	}
    	var winHeight='';
    	var isHid =null;
    	var imisHid =null;
    	if(typeof(_romId) == "undefined" || _romId  == ""){
    		winHeight = 400;
    		isHid = false;
    		imisHid = true;
    	}else{
    		winHeight = 400;
    		isHid = true;
    		imisHid = false;
    	}
    	var uxfile = new Ext.ux.form.FileUploadField({
    		width: 200,
    		hidden:imisHid,
			hideLabel:imisHid,
			id : 'showFileName',
			name : 'uploadFile',
			fieldLabel : '文件名'
    	});
    	uxfile.setValue(_romOriginalName);
    	
    	var _fileForm = new Ext.FormPanel({
    		layout : "fit",
    		frame : true,
    		border : false,
    		autoHeight : true,
    		waitMsgTarget : true,
    		defaults : {
    			bodyStyle : 'padding:10px'
    		},
    		margins : '0 0 0 0',
    		labelAlign : "left",
    		labelWidth : 60,
    		fileUpload : true,
    		items : [{  
                xtype:'hidden',  
                fieldLabel: 'id',  
                name: 'id',  
                value: _romId 
            },{
    			xtype : 'fieldset',
    			title : '选择文件',
    			autoHeight : true,
    			//hidden:isHid,
    			items : [{
    				id : 'sampleUploadFileId',
    				name : 'uploadFile',
    				xtype : "textfield",
    				fieldLabel : '文件',
    				inputType : 'file',
    				anchor : '96%',
					hidden:isHid,
					hideLabel:isHid 
    			},uxfile]
    		}, {
    			xtype : 'fieldset',
    			title : '设置参数',
    			autoHeight : true,
    			items : [{
    				width:150,
    				xtype : 'textfield',
    				id:'romVersion',
    				name : 'romVersion',
    				fieldLabel : '版本号',
    				regex : /^\d{8}$/, //正则表达式在/...../之间
    				regexText:"版本号只能填写8位数字，如20150101", //正则表达式错误提示  
    				value:_romVersion,
    			},co,{
    				id:'_romCommentEdit',
    				width:400,
    				height:100,
    				xtype : 'textarea',
    				name : 'romComment',
    				anchor: "96.7%",
    				value:typeof(_romComment) == "undefined"?"":_romComment.replace(/<br>/ig, "\n"),
    				fieldLabel : '更新说明',
    			}]
    		}]
    	});
    	
    	var _importPanel = new Ext.Panel({
    		layout : "fit",
    		layoutConfig : {
    			animate : true
    		},
    		items : [_fileForm],
    		buttons : [{
    			id : "btn_import_wordclass",
    			text : "保存",
    			handler : function() {
    				var vers = document.getElementById('romVersion').value ;
    		        var reg = /^\d{8}$/;     
    				if(vers.match(reg) == null){  
    					Ext.Msg.alert("error", "版本号只能填写8位数字，如20150101");
    					return;
    				}
    				if(typeof(_romId) == "undefined" || _romId  == ""){
	    				if (!Ext.getCmp("sampleUploadFileId").getValue()) {
	    					Ext.Msg.alert("error", "请选择你要上传的文件");
	    					return;
	    				} else {
	    					this.disable();
	    					var btn = this;
	    					// 开始上传
	    					var sampleForm = _fileForm.getForm();
	    					sampleForm.submit({
	    						url : path +'/upload!uploadFile.action?uploadType=1',
	    						success : function(form, action) {
	    							btn.enable();
	    							var data = Ext.decode(action.response.responseText);
	    							if(data.i_type == "success"){
	    								Ext.Msg.alert("success",'上传成功！',function(){  
		    								newWin.close();
		    								reloadData();
	    								});
	    							}else{
	    								Ext.Msg.alert("Error",data.i_msg,function(){  
	    						            //关闭后执行  
	    									newWin.close();
		    								reloadData();
	    						        });  
	    								
	    							}
	    						},
	    						failure : function(form, action) {
	    							Ext.Msg.alert("Error",'上传失败！',function(){  
		    							newWin.close();
		    							reloadData();
	    							});
	    						}
	    					});
	    				}
    				}else{
						var sampleForm = _fileForm.getForm();
    					this.disable();
    					var btn = this;
						sampleForm.submit({
							params : {}	,
							url : path +'/deviceqr!updateRom.action?id='+_romId,
							success : function(form, action) {
							   btn.enable();
	    	            	   var data = Ext.decode(action.response.responseText);
	    	            	   if(data.i_type == "success"){
	    	            		   Ext.Msg.alert("success",'保存成功！',function(){  
		    	            		   newWin.close();
		    	            		   reloadData();
	    	            		   });
	    	            	   }else{
	    	            		   Ext.Msg.alert("Error",data.i_msg,function(){
		    	            		   newWin.close();
		    	            		   reloadData();
	    	            		   });
	    	            	   }
							},
							failure : function(form, action) {
								Ext.Msg.alert("Error",'保存失败！',function(){
									newWin.close();
									reloadData();
								});
							}
						});
    				}
    			}
    		}]
    	});
    	
    	newWin = new Ext.Window({
    		width : 520,
    		title : '版本编辑',
    		height : winHeight,
    		defaults : {// 表示该窗口中所有子元素的特性
    			border : false
    			// 表示所有子元素都不要边框
    		},
    		plain : true,// 方角 默认
    		modal : true,
    		shim : true,
    		collapsible : true,// 折叠
    		closable : true, // 关闭
    		closeAction: 'close',
    		resizable : false,// 改变大小
    		draggable : true,// 拖动
    		minimizable : false,// 最小化
    		maximizable : false,// 最大化
    		animCollapse : true,
    		constrainHeader : true,
    		autoHeight : false,
    		items : [_importPanel]
    	});
		newWin.show();
    }
    
