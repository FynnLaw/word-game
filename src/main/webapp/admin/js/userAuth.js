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
//   		var sm = new Ext.grid.CheckboxSelectionModel();
        store = new Ext.data.Store({
			url : qrUrl+"listRole.action",
			reader : new Ext.data.JsonReader({
				root : 'products',
				totalProperty : 'totalCount',
				id : 'querydate',
				fields : [
					{name:  'roleName'},
					{name : 'id'},
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:20}});
        
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
//            	sm,
            	{header:"角色名",align:'center',dataIndex:"roleName",sortable:true}, 
	            {header:"操作",align:'center',dataIndex:"id",
	            renderer: function (value, meta, record) {
					            			var formatStr = "<input id = 'bt_edit_" + record.get('id')
							+ "' onclick=\"showEditRole('" + record.get('id') + "','"
							+ record.get('roleName') 
							+ "');\" type='button' value='编辑' width ='15px'/>&nbsp;&nbsp;"; 

										     var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteRole('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>&nbsp;&nbsp;";
										     var authBtn = "<input id = 'bt_auth_" + record.get('id')
						     + "' onclick=\"showEditAuth('" + record.get('id')
						     + "');\" type='button' value='设置权限' width ='20px'/>";
										            			
            				var resultStr = String.format(formatStr);
            				return "<div>" + resultStr+deleteBtn +authBtn+ "</div>";
        				  } .createDelegate(this)
	            } 
            ] 
        ); 
        
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['角色名：', {
		  		  id : 'shRoleName',
		  		  xtype : 'textfield',
		  		  width : 115,
		  	}, {
				text : '查询',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				handler : function() {
					Ext.getCmp('shRoleName').setValue("");
				}
			},{
				text : '添加新角色',
				handler : function() {
					showEditRole();
				}
			},{
				text : '设置权限',
				handler : function() {
					showEditAuth();
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
//            sm:sm,
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
		var roleName = document.getElementById('shRoleName').value ;
		store.baseParams['roleName'] = roleName;
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
	
	function deleteRole(id){
		Ext.Msg.confirm('tip', '删除角色同时会删除相应的用户?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request({
				  url : path + "/user!deleteRole.action",
				  method : 'post',
				  params : {
					  roleId:id
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
    function showEditRole(_roleId,_userName,_userRole){
    	var isHidden = true;
    	if(typeof(_roleId) == "undefined" || _roleId  == ""){
    		isHidden = false;
    	}
    	var _fileForm =  new Ext.form.FormPanel({
            frame: true,
            autoHeight: true,
            labelWidth: 80,
            labelAlign: "right",
            bodyStyle:"text-align:left",
            border : false,
            items: [
               {xtype:"textfield", width:180,id: "eRoleName", fieldLabel: "角色名",value:_userName},
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
    				var name = Ext.getCmp('eRoleName').getValue();
    				if(typeof(name) == "undefined" || name  == ""){
    					Ext.Msg.alert('提示', '请填写角色名');
    					return;
    				}
    				Ext.Ajax.request({
    					  url : path + "/user!editRole.action",
    					  method : 'post',
    					  params : {
    						  roleId:_roleId,
    						  roleName:name,
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
    						  Ext.Msg.alert('提示', '操作失败'); 
    					  }
    		 		});
    				
    			}
    		}]
    	});
    	
    	newWin = new Ext.Window({
    		width : 520,
    		height:110,
    		title : '角色编辑',
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
    
    
    function showEditAuth(roleId){
    	  var authTree = new Ext.tree.TreePanel({
    	        animate : true,
    	        border:false,
    			title:"勾选角色可以操作的菜单",
    			collapsible:true,
    			frame:true,
    			enableDD:true,
    			enableDrag:true,
    			rootVisible:true,
    			autoScroll:true,
    			autoHeight:true,
    			width:150,
    			lines:true,
    			loader: new Ext.tree.TreeLoader({
    				url: path+'/user!getAuthTree.action?roleId='+roleId,
//    			        url: '../admin/js/tree.txt',
    				requestMethod: 'GET'
    			}),
    			root: new Ext.tree.AsyncTreeNode({
    				    id: 'root',
    				    text: '数据管理',
    					expanded: true
    				})
    	 });
    	//判断是否有子结点被选中
    	var childHasChecked = function(node){
			var childNodes = node.childNodes;
			if(childNodes || childNodes.length>0){
		    	for(var i=0;i<childNodes.length;i++){
		    		if(childNodes[i].getUI().checkbox.checked)
		    		return true;
		    	}
			}
			return false;
		}
    	// 级联选中父节点
    	var parentCheck = function(node ,checked){
	    	var checkbox = node.getUI().checkbox;
	    	if(typeof checkbox == 'undefined')
	    	return false;
	    	if(!(checked ^ checkbox.checked))
	    	return false;
	    	if(!checked && childHasChecked(node))
	    	return false;
	    	checkbox.checked = checked;
	    	node.attributes.checked = checked;
	    	node.getUI().checkbox.indeterminate = checked; // 半选中状态
	    	node.getOwnerTree().fireEvent('check', node, checked);
	    	var parentNode = node.parentNode;
	    	if( parentNode !== null){
	    		parentCheck(parentNode,checked);
	    	}
    	}
		authTree.on('checkchange', function(node, checked) {
			node.expand();
			node.attributes.checked = checked;
			var parentNode = node.parentNode;
			if(parentNode !== null){
				parentCheck(parentNode,checked);
			}
			node.eachChild(function(child) {
			    child.ui.toggleCheck(checked);
			    child.attributes.checked = checked;
			    child.fireEvent('checkchange', child, checked);
			});
		 }, authTree);
	     authTree.expandAll();
    	 var _importPanel = new Ext.Panel({
      		layout : "fit",
      		layoutConfig : {
      			animate : true
      		},
      		border:false,
      		items : [authTree],
      		buttons : [{
      			text : "保存",
      			handler : function() {
      				var parm = "";
      				var checkNode=new Array()
      				checkNode = authTree.getChecked();
      				if(checkNode != null){
      					for(var i=0;i<checkNode.length ;i++){
      						parm = parm + checkNode[i].id+','
      					}
      				}else{
      					Ext.Msg.alert('提示', "请勾选菜单！");
      					return;
      				}
      				Ext.Ajax.request({
  					  url : path + "/user!updateUserAuth.action",
  					  method : 'post',
  					  params : {
  						  roleId:roleId,
  						  ids:parm,
  					  },
  					  success : function(response, options) {
  					   var o = Ext.util.JSON.decode(response.responseText);
  					   if(o.i_type && "success"== o.i_type){
  						   Ext.Msg.alert("success",'保存成功！',function(){  
  							    authWin.close();
  						   });
  					   }else{
  					   	   Ext.Msg.alert('提示', o.i_msg); 
  					   }
  					  },
  					  failure : function() {
  						  Ext.Msg.alert('提示', '操作失败'); 
  					  }
      				});
      			}
      		}]
      	});
    	
    	var authWin = new Ext.Window({
    		width : 300,
//    		height:110,
    		autoHeight : true,
    		title : '权限编辑',
    		defaults : {// 表示该窗口中所有子元素的特性
//    			border : false
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
    		items : [_importPanel]
    	});
    	authWin.show();
    }
    
