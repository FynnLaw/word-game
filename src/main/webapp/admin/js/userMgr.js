    Ext.QuickTips.init();
    var _cob;
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
   		
   		var qrUrl = path + "/user!";
   		var order;
        store = new Ext.data.Store({
			url : qrUrl+"listUser.action",
			reader : new Ext.data.JsonReader({
				root : 'products',
				totalProperty : 'totalCount',
				id : 'querydate',
				fields : [
					{name:  'userName'},
					{name : 'userRole'},
					{name : 'userRoleId'},
					{name : 'id'},
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:20}});
        
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
            	{header:"用户名",align:'center',dataIndex:"userName",sortable:true}, 
	            {header:"角色类别",align:'center',dataIndex:"userRole",sortable:true},
	            {header:"操作",align:'center',dataIndex:"id",width:50,
	            renderer: function (value, meta, record) {
					            			var formatStr = "<input id = 'bt_edit_" + record.get('id')
							+ "' onclick=\"showEditUser('" + record.get('id') + "','"
							+ record.get('userName') + "','"
							+ record.get('userRole') + "','"
							+ record.get('userRoleId')
							+ "');\" type='button' value='编辑' width ='15px'/>&nbsp;&nbsp;"; 

										     var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteUser('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>";
										            			
            				var resultStr = String.format(formatStr);
            				return "<div>" + resultStr+deleteBtn + "</div>";
        				  } .createDelegate(this)
	            } 
            ] 
        ); 
//        
//   	 	var _codata = [
//   	            ['2','全部'],
//   				['0','管理员'],
//   				['1','普通用户'],
//   			];
//   	    	
//    	_cob = new Ext.form.ComboBox({
//    		id:'shUserRole',
//    		forceSelection: true,
//    		listWidth: 150,
//    		width:150,
//    		store: new Ext.data.SimpleStore({
//    		fields: ['value', 'text'],
//    		data : _codata
//    		}),
//    		valueField:'value',
//    		displayField:'text',
//    		typeAhead: true,
//    		mode: 'local',
//    		triggerAction: 'all',
//    		selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
//    		allowBlank:false,
//    		editable:false
//    	});
//    	_cob.setValue('2');
    	//------------------------
		//用户角色
		var moduleStore = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: path + "/user!getRole.action?all="+1 //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
	        }),
	        reader: new Ext.data.JsonReader({
        	root : 'products',
	        fields:['value','text']
	        })
	    });
		
		_cob = new Ext.form.ComboBox({
			id:'shUserRole',
			width:150,
			forceSelection: true,
			store:moduleStore,
			valueField:'value',
			displayField:'text',
			typeAhead: true,
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
		});
		_cob.setValue("全部");
    	//------------------------
    	
    	
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['用户名：', {
		  		  id : 'shUserName',
		  		  xtype : 'textfield',
		  		  width : 115,
		  	},'用户角色：', _cob, {
				text : '查询',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				handler : function() {
					Ext.getCmp('shUserName').setValue("");
					Ext.getCmp('shUserRole').setValue("全部");
				}
			},{
				text : '添加新用户',
				handler : function() {
					showEditUser();
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
		var userName = document.getElementById('shUserName').value ;
		var shUserRole = _cob.getValue();
		store.baseParams['userName'] = userName;
		store.baseParams['userRole'] = shUserRole;
		store.reload({
			params: {start:0,limit:20},
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
	
	function deleteUser(id){
		Ext.Msg.confirm('删除数据', '确认?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request({
				  url : path + "/user!deleteUser.action",
				  method : 'post',
				  params : {
					  userId:id
				  },
				  success : function(response, options) {
				   var o = Ext.util.JSON.decode(response.responseText);
				   if(o.i_type && "success"== o.i_type){
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
    function showEditUser(_userId,_userName,_userRoleName,_userRole){
    	var isHidden = true;
    	var pwdval = "******";
    	if(typeof(_userId) == "undefined" || _userId  == ""){
    		isHidden = false;
    		pwdval ="";
    	}
    	//用户角色
		var _moduleStore = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: path + "/user!getRole.action?all="+0 //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
	        }),
	        reader: new Ext.data.JsonReader({
        	root : 'products',
	        fields:['value','text']
	        })
	    });
		_moduleStore.load(); 
		var co = new Ext.form.ComboBox({
			id:'couserRole',
			hiddenName:"couserRole", //提交到后台的input的name   
			width:180,
			forceSelection: true,
			store:_moduleStore,
			mode: 'local',
			valueField:'value',
			displayField:'text',
			typeAhead: true,
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
		});
		_moduleStore.on('load', function() { //数据加载完成后设置下拉框值  
	        if (_userRole)  
	        	co.setValue(_userRole);  
	    }); 
    	var _fileForm =  new Ext.form.FormPanel({
            frame: true,
            autoHeight: true,
            labelWidth: 80,
            labelAlign: "right",
            bodyStyle:"text-align:left",
            border : false,
            items: [
               {xtype:"textfield", width:180,id: "eUserName",name: "eUserName", fieldLabel: "用户名",value:_userName},
               {xtype:"textfield", width:180,id: "enewpwd",name: "enewpwd", fieldLabel: "用户密码",value:pwdval},
               co
            ],
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
    				var name = Ext.getCmp('eUserName').getValue();
    				var pwd = Ext.getCmp('enewpwd').getValue();
    				var role = co.getValue();
    				if(typeof(name) == "undefined" || name  == ""){
    					Ext.Msg.alert('提示', '请填写用户名');
    					return;
    				}
    				if(typeof(pwd) == "undefined" || pwd  == ""){
    					Ext.Msg.alert('提示', '请填写用户密码');
    					return;
    				}
    				if(typeof(role) == "undefined" || role  == ""){
    					Ext.Msg.alert('提示', '请选择用户角色');
    					return;
    				}
    				console.log(co);
    				Ext.Ajax.request({
    					  url : path + "/user!editUser.action",
    					  method : 'post',
    					  params : {
    						  userId:_userId,
    						  userName:name,
    						  userPwd:pwd,
    						  userRole:role
    					  },
    					  success : function(response, options) {
    					   var o = Ext.util.JSON.decode(response.responseText);
    					   if(o.i_type && "success"== o.i_type){
    						   Ext.Msg.alert("success",'保存成功！',function(){  
   								newWin.close();
   								reloadData();
    						   });
    					   }else{
    					   	   Ext.Msg.alert('提示', o.i_msg); 
    					   }
    					  },
    					  failure : function() {
    						  Ext.Msg.alert('提示', '删除失败'); 
    					  }
    		 		});
//    				_fileForm.submit({  
//    			        url : path + "/user!editUser.action",
//    			        baseParams : {  
//    			        	userId : _userId
//    			        }, 
//    			        waitMsg : '正在提交数据',  
//    			        waitTitle : '提示',  
//    			        method : "POST",  
//    			        success : function(form, action) {  
//    			            // 得到数据  
//    			            var result = Ext.util.JSON.decode(action.response.responseText);// 就可以取出来。如果是数组，那么很简单  
//    			            if(o.i_type && "success"== o.i_type){
//     						   Ext.Msg.alert("success",'保存成功！',function(){  
//    								newWin.close();
//    								reloadData();
//	     					   });
//	     					}else{
//	     					   Ext.Msg.alert('提示', o.i_msg); 
//	     					}
//    			        },  
//    			        failure : function(form, action) {  
//    			            Ext.Msg.alert('提示', "操作失败");  
//    			        }  
//    			    });  
    			}
    		}]
    	});
    	
    	newWin = new Ext.Window({
    		width : 520,
    		height:200,
    		title : '用户编辑',
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
    
