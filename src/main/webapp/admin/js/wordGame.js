    Ext.QuickTips.init();
    var _cob;
   	Ext.onReady(function(){
   		
	var qrUrl = path + "/wordGame!";
	var order;
    store = new Ext.data.Store({
		url : qrUrl+"getWordGameList.action",
		reader : new Ext.data.JsonReader({
			fields : [
				{name:  'name'},
				{name : 'id'},
			]
		}),
		remoteSort : true
	});
    
	store.load({params:{start:0,limit:20}});
    console.log(store);
    var columnBreakList=new Ext.grid.ColumnModel(
        [
        	new Ext.grid.RowNumberer(),
        	{header:"游戏列表",align:'center',dataIndex:"name",sortable:true}, 
            {header:"操作",align:'center',dataIndex:"id",
            renderer: function (value, meta, record) {

            			var formatStr = "<input id = 'bt_edit_" + record.get('id')
							+ "' onclick=\"editWordGame('" + record.get('id')
							+ "');\" type='button' value='编辑' width ='15px'/>&nbsp;&nbsp;"; 

            			var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteWordGame('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>&nbsp;&nbsp;";
									            			
        				var resultStr = String.format(formatStr);
        				return "<div>" + resultStr+deleteBtn + "</div>";
    				  }.createDelegate(this)
            }
        ] 
    ); 
    
    var breakListGridPanel = new Ext.grid.EditorGridPanel({ 
		region:'center',
		viewConfig: {
            forceFit: true // 让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
        },
        cm:columnBreakList, 
        store:store
    });
    
    var addButton = new Ext.Button({
    	region:'south',
    	text : "新增文字游戏",
    	height:40,
    	iconCls : "addButton",　　
    	id:"addButton", 
    	handler : function() {
    		addWordGame();
    	}
    });
    
    var listPanel = new Ext.Panel({
  		region:'west',
  		title:'文字闯关列表',  
		layout:'border',
		border:false,
		width:320,
		items:[breakListGridPanel,
		       addButton]
	});
    
    var formPanel =  new Ext.form.FormPanel({
    	border:false,
        items: [
           {xtype:"field", width:180,id: "oldpwd", fieldLabel: "名称", inputType: "input"},
           {xtype:"displayfield", width:180,id: "oldpwdes2t", fieldLabel: "ID(KEY)"},
           {xtype:"tbtext",id: "oldpwdesdt", text : "预览",style:"font-size:medium;font-weight:bold;text-align: center;"}
        ]
     });
    
    var northPanel = new Ext.Panel({
  		region:'north',
  		width:'100%',
        border:false,
		items:[formPanel]
	});
        
  	var wordGameTreePanel = new Ext.tree.TreePanel({
  		 region:'center',
         border:false,
         loader: new Ext.tree.TreeLoader({
			        url: path+'/user!getTree.action',
			        requestMethod: 'GET'
		    		}),
		    // 构造根节点
			root: new Ext.tree.AsyncTreeNode({
			    id: 'root',
			    text: '数据管理',
				expanded: true
			}),
			buttons: [{text: '保存' ,width:70,height:20,handler:function (){
				
			}},
			          {text: '清空全部' ,width:70,height:20,handler:function (){
				          gridForm.getForm().submit({
				        	  url : 'bookinput.do?actionsign=input', 
				        	  method : 'post'
				        	  });
				          }
			}],
			buttonAlign : 'center'
		});
  	
  	var wordGamePanelWestPanel = new Ext.Panel({
        border:false,
  		style: 'margin:20px',
		items:[northPanel,
		       wordGameTreePanel]
	});
  	
  	var wordGamePanelWest = new Ext.Panel({
  		region:'west',
        border:false,
        width :'30%',
        heigth:'100%',
		items:[wordGamePanelWestPanel]
	});
  	
  	 var formPanelEast =  new Ext.form.FormPanel({
     	 border:false,
         items: [
                 {xtype:"tbtext", width:360,id: "1", text: "层级内容编辑",style:"font-size:medium;font-weight:bold;text-align: center;"},
                 {xtype:"displayfield", width:180,id: "2", fieldLabel: "层级编号"},
                 {xtype:"textarea", width:180,id: "3", fieldLabel: "文字内容", inputType: "text"},
	             {xtype:"combo", 
	              width:180,
	              id: "combo0", 
	              fieldLabel: "选项0",  
	              store:new Ext.data.SimpleStore({
	                 fields:['value','text'],
	                 data:[
	                 ['value1','text1'],
	                 ['value2','text2'],
	                ]
	              }),
	              displayField:'text',
	              valueField:'value',
	              mode:'local',
	              emptyText:'请选择',
	              readOnly:false},
	              {xtype:"combo", 
	            	  width:180,
	            	  id: "combo1", 
	            	  fieldLabel: "选项1",  
	            	  store:new Ext.data.SimpleStore({
	            		  fields:['value','text'],
	            		  data:[
	            		        ['value1','text1'],
	            		        ['value2','text2'],
	            		        ]
	            	  }),
	            	  displayField:'text',
	            	  valueField:'value',
	            	  mode:'local',
	            	  emptyText:'请选择',
	            	  readOnly:false},
            	  {xtype:"combo", 
            		  width:180,
            		  id: "combo2", 
            		  fieldLabel: "选项2",  
            		  store:new Ext.data.SimpleStore({
            			  fields:['value','text'],
            			  data:[
            			        ['value1','text1'],
            			        ['value2','text2'],
            			        ]
            		  }),
            		  displayField:'text',
            		  valueField:'value',
            		  mode:'local',
            		  emptyText:'请选择',
            		  readOnly:false},
        		  {xtype:"combo", 
        			  width:180,
        			  id: "combo3", 
        			  fieldLabel: "选项3",  
        			  store:new Ext.data.SimpleStore({
        				  fields:['value','text'],
        				  data:[
        				        ['value1','text1'],
        				        ['value2','text2'],
        				        ]
        			  }),
        			  displayField:'text',
        			  valueField:'value',
        			  mode:'local',
        			  emptyText:'请选择',
        			  readOnly:false},
    			  {xtype:"combo", 
    				  width:180,
    				  id: "combo4", 
    				  fieldLabel: "选项4",  
    				  store:new Ext.data.SimpleStore({
    					  fields:['value','text'],
    					  data:[
    					        ['value1','text1'],
    					        ['value2','text2'],
    					        ]
    				  }),
    				  displayField:'text',
    				  valueField:'value',
    				  mode:'local',
    				  emptyText:'请选择',
    				  readOnly:false},
			  {xtype:"textarea", width:180,id: "3dd", fieldLabel: "结束提示语", inputType: "text"}
			  
         ],
         style: 'padding:20px',
         buttons:[{xtype:"button",id: "3dd22",text : '保存',listeners:{
			  click:function(){
				  console.log(1);
			  }
		  	}
		  },
		  {xtype:"button",id: "3dd2112",text : '取消',listeners:{
			  click:function(){
				  console.log(1);
			  }
		  	}
		  }],
		  buttonAlign : 'center'
      });
  	
  	var wordGamePanelEast = new Ext.Panel({
  		region:'center',
        items: [formPanelEast]
	});
  	wordGamePanelEast.hide();
  	
  	var wordGamePanel = new Ext.Panel({
  		region:'center',
  		title:'文字闯关编辑器',  
		layout:'border',
		border:false,
		items:[wordGamePanelWest,
		       wordGamePanelEast]
	});
  	
  	//默认不显示
    wordGamePanel.hide();
  	
   var viewport=new Ext.Viewport({
       enableTabScroll:true,
       layout:'border',
       defaultType: 'Viewport',
       items:[
           listPanel,
           wordGamePanel
   	   ]
   });
   });
   
   function reloadData(){
	   console.log(wordGamePanel);
		store.reload({
			params: {start:0,limit:20},
			callback: function(records, options, success){
// console.log(records);
			},
			scope: store
		});
	}
	
	
	function saveInfo(oldName,newName,_id){
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
					   // alert(o.i_type);
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
	
    function addWordGame(){
    	var _fileForm =  new Ext.form.FormPanel({
            frame: true,
            autoHeight: true,
            labelWidth: 80,
            labelAlign: "right",
            bodyStyle:"text-align:left",
            border : false,
            items: [
               {xtype:"textfield", width:180,id: "eRoleName", fieldLabel: "游戏名称"},
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
    				console.log(name);
    				if(typeof(name) == "undefined" || name  == ""){
    					Ext.Msg.alert('提示', '请填写游戏名称');
    					return;
    				}
    				Ext.Ajax.request({
    					  url : path + "/wordGame!addWordGame.action",
    					  method : 'post',
    					  params : {
    						  name:name,
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
    		title : '新增文字游戏',
    		defaults : {// 表示该窗口中所有子元素的特性
    			border : false
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
    
	function deleteWordGame(id){
		Ext.Msg.confirm('删除游戏', '确定删除该游戏吗?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request({
				  url : path + "/wordGame!deleteWordGame.action",
				  method : 'post',
				  params : {
					  id:id
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
	
	function editWordGame(id){
		console.log(id);
		wordGamePanel.show();
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
// url: '../admin/js/tree.txt',
    				requestMethod: 'GET'
    			}),
    			root: new Ext.tree.AsyncTreeNode({
    				    id: 'root',
    				    text: '数据管理',
    					expanded: true
    				})
    	 });
    	// 判断是否有子结点被选中
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
// height:110,
    		autoHeight : true,
    		title : '权限编辑',
    		defaults : {// 表示该窗口中所有子元素的特性
// border : false
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
    
