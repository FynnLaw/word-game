    Ext.QuickTips.init();
    var store1;
    var sm1;
    var column1;
    var grid;
    var ticketIsUse;
   	Ext.onReady(function(){
   		//Ext.Msg.alert('提示', '开始时间不能晚于结束时间');
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
   		var onetbar;
   		var twotbar;
   		var threebar;
   		
        store = new Ext.data.Store({
			url : qrUrl+"getDeviceInfo.action",
			reader : new Ext.data.JsonReader({
				root : 'result',
				totalProperty : 'totalCount',
				id : 'querydate',
				fields : [
		            {name:  'id'},
					{name:  'wxDeviceId'},
					{name : 'qrTicket'},
					{name : 'deviceId'},
					{name : 'model'},
					{name : 'version'},
					{name : 'status'},
					{name : 'autoUpdate'}
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:100}});
		
		var sm = new Ext.grid.CheckboxSelectionModel();
		sm1 = new Ext.grid.CheckboxSelectionModel();
		
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
            	{header:'id',dataIndex:'id',hidden:true},
            	sm,
            	{header:"设备ID",align:'center',dataIndex:"deviceId",sortable:true} ,
            	{header:"设备二维码ticket",align:'center',dataIndex:"qrTicket",sortable:true,width:200},
	            {header:"微信设备ID",align:'center',dataIndex:"wxDeviceId",sortable:true,width:150,hidden:true}, 
	            {header:"扫地机型号",align:'center',dataIndex:"model",sortable:true,width:50} ,
	            {header:"自动更新",align:'center',dataIndex:"autoUpdate",sortable:true,width:50,renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
                    if(value==0){
                        return '否';
                      } else if(value==1){
                        return '是';
                      }else{
                    	  return ;
                      }
	            }} ,
	            {header:"版本号",align:'center',dataIndex:"version",sortable:true,width:50} ,
	            {header:"状态",align:'center',dataIndex:"status",sortable:true,width:50,renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
	            	console.log(record.data.deviceId);
                    if(value=='0'){
                    	if(record.data.deviceId != null  &&  record.data.deviceId != ""){
                    		return '离线';
                    	}else{
                    		return '未绑定';
                    	}
                    } else if(value=='1') {
                        return '在线';
                    }else{
                    	return '未绑定';
                    }
	            }},{header:"操作",align:'center',dataIndex:"id",width:50,
		            renderer: function (value, meta, record) {
					var formatStr = "<input id = 'bt_"+record.get('deviceId')+"' onclick=\"showWin('"+record.get('wxDeviceId')+"','"+record.get('deviceId')+"');\" type='button' value='显示绑定关系' width ='30px'/>"; 
					var resultStr = String.format(formatStr);
					return "<div>" + resultStr + "</div>";
		        } .createDelegate(this)
	            }
            ] 
        ); 
        var typeCode = [
                	    ['0','全部'],
            			['1','x6'],
            			['2','v9']
        ];
            			
		var combobox = new Ext.form.ComboBox({
			width:150,
			//fieldLabel: '扫地机型号',
			id:'model',
			name:'model',
			store: new Ext.data.SimpleStore({
			fields: ['value', 'text'],
				data : typeCode
			}),
			valueField:'value',
			displayField:'text',
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
			});
		combobox.setValue(0);
		
		//在线状态
		var stateCode = [
			                ['2','全部'],
			                ['1','在线'],
			                ['0','离线']
			                ];
			
		var comboboxState = new Ext.form.ComboBox({
			width:150,
			fieldLabel: '在线状态',
			id:'onLineStatus',
			name:'onLineStatus',
			forceSelection: true,
			store: new Ext.data.SimpleStore({
				fields: ['value', 'text'],
				data : stateCode
			}),
			valueField:'value',
			displayField:'text',
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
		});
		comboboxState.setValue(2);
		
		//微信二维码是否已使用
		var ticketCode = [
		                 ['2','全部'],
		                 ['1','是'],
		                 ['0','否']
		                 ];
		
	    ticketIsUse = new Ext.form.ComboBox({
			width:150,
			fieldLabel: '微信二维码是否已使用',
			id:'_ticketIsUse',
			name:'_ticketIsUse',
			forceSelection: true,
			store: new Ext.data.SimpleStore({
				fields: ['value', 'text'],
				data : ticketCode
			}),
			valueField:'value',
			displayField:'text',
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
		});
		ticketIsUse.setValue(2);
		
		
		//版本号
		var moduleStore = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: qrUrl+"getVersion.action" //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
	        }),
	        reader: new Ext.data.JsonReader({
        	root : 'products',
	        fields:['value','name']
	        })
	    });
		moduleStore.load();
		var comb = new Ext.form.ComboBox({
			id:'fmversion',
			name:'fmversion',
			width:150,
			fieldLabel: '版本号',
			forceSelection: true,
			store:moduleStore,
			valueField:'value',
			displayField:'name',
			typeAhead: true,
			mode: 'local',
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
		});
		comb.setValue("全部");
		
		//推送版本号
		var moduleStore1 = new Ext.data.Store({
			proxy: new Ext.data.HttpProxy({
				url: qrUrl+"getVersionTs.action" //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
			}),
			reader: new Ext.data.JsonReader({
				root : 'products',
				fields:['value','name']
			})
		});
		moduleStore1.load();
		var comb1 = new Ext.form.ComboBox({
			id:'vvisionTs',
			name:'vvisionTs',
			width:150,
			fieldLabel: '请选择固件版本',
			name:'vvisionTs',
			forceSelection:false,
			store:moduleStore1,
			valueField:'value',
			displayField:'name',
			typeAhead: true,
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:true,
			editable:false
		});
		moduleStore.load();
		moduleStore1.load();
		
		onetbar = new Ext.Toolbar({  
            items :[/*'微信设备id：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;', {
				  		  id : 'device_id_w',
				  		  xtype : 'textfield',
				  		  width : 200,
				  	},*/'设备ID',{
				  		  id : 'device_id',
				  		  xtype : 'textfield',
				  		  width : 200
				  	},'扫地机型号&nbsp;',combobox,
				  	'在线状态',comboboxState,
				  	'版本号',comb,
				  	'微信二维码是否已使用',ticketIsUse
				  	]

        }); 
		var initDate = new Date();
    	initDate.setDate(1);
    	initDate.setHours(0, 0, 0, 0);
    	
    	var endTimeField = new ClearableDateTimeField({
    		id:'createDateEnd',
    		name:'createDateEnd',
    		editable : false,
    		width : 160
    	});
    	
    	var beginTimeField = new ClearableDateTimeField({
    		id:'createDateStart',
    		name:'createDateStart',
    		editable : false,
    		//value : initDate,
    		width : 160
    		//fieldLabel : '创建时间',
    	}); 
    	this.beginTimeField = beginTimeField;
    	
    	this.endTimeField = endTimeField;
    	beginTimeField.on('change', function(o, v) {
    	});
    	beginTimeField.fireEvent('change', beginTimeField, initDate);
    	endTimeField.on('change', function(o, v) {
    	});
    	
		twotbar = new Ext.Toolbar({
			items :['设备二维码ticket',{
				  		  id : 'qrticket',
				  		  xtype : 'textfield',
				  		  width : 200
				  	},'创建时间：',
				  	beginTimeField,'至',
				  	endTimeField, {
						text : '查询',
						handler : function() {
							reloadData();
						}
					},{
						text : '重置',
						handler : function() {
//							Ext.getCmp('device_id_w').setValue("");
							Ext.getCmp('qrticket').setValue("");
							Ext.getCmp('device_id').setValue("");
							Ext.getCmp('createDateStart').setValue("");
							Ext.getCmp('createDateEnd').setValue("");
							combobox.setValue(0);
							comboboxState.setValue(2);
							comb.setValue("全部");
						}
					},{
						text : '批量生成二维码',
						handler : function() {
							 Ext.MessageBox.prompt("提示","请输入生成二维码个数：",function(bu,txt){    
//							    Ext.MessageBox.alert("Result","你点击的是"+bu+"按钮,<br> 输入的内容为："+txt);
								 if(bu == 'ok'){
									 var reg = /^\d{1,3}$/;     
									 if(txt.match(reg) == null){  
				    					Ext.Msg.alert("提示", "最多只能填写3位数字");
				    					return;
									 }
									 generatQr(txt);
								 }
							},this,20); 
						}
					},{
						text : '导入',
						handler : function() {
							openUpload("xls-xlsx");
						}
					},{
						text : '导出',
						handler : function() {
							exportData();
						}
					},{
						text : '修改',
						handler : function() {
							showEditWin();
						}
					},{
						text : '删除',
						handler : function() {
//							exportData();
							deleteDevice();
						}
					}
			       ]
		});
        var menuFile = new Ext.menu.Menu({  
            //设置菜单四条边都有阴影  
            shadow : 'frame',  
            width:'93',
            //添加菜单项  
            items:[  
                {  
                    text:'自动',  
                    handler:function () {setAutoUpdate(1);}
                },  
                {  
                    text:'非自动',  
                    handler:function () {setAutoUpdate(0);}
                }
            ]  
        });
        function setAutoUpdate(val){
			var gcm = grid.getSelectionModel();
			var dids = '';
			var rows = gcm.getSelections();
		    if (rows.length > 0) {
				for ( var i = 0; i < rows.length; i++) {
					var row = rows[i];
					dids = dids + row.data.deviceId + ','; // 拼装ID串
				}
			} else {
				Ext.Msg.alert('提示', '请勾选要操作的记录');
				return;
			}
			Ext.Ajax.request( {
				  url : path + "/deviceqr!setAutoUpdate.action",
				  method : 'post',
				  params : {
					  dids:dids,
					  val:val
				  },
				  success : function(response, options) {
				  var o = Ext.util.JSON.decode(response.responseText);
				   if(o.i_type && "success"== o.i_type){
					   //Ext.Msg.alert('提示', '设置成功');
					   reFreshStatus('设置成功，是否刷新设备在线状态?');
					   
				   }else{
				   	   Ext.Msg.alert('提示', '设置失败'); 
				   }
				  },
				  failure : function() {
				  }
	 		});
     }
        function deleteDevice(){
        	var gcm = grid.getSelectionModel();
        	var dids = '';
        	var deviceWids = '';
        	var rows = gcm.getSelections();
        	if (rows.length > 0) {
        		for ( var i = 0; i < rows.length; i++) {
        			var row = rows[i];
        			dids = dids +row.data.deviceId + ","; // 拼装ID串
        			deviceWids = deviceWids + row.data.wxDeviceId + ","; // 拼装ID串
        		}
        	} else {
        		Ext.Msg.alert('提示', '请勾选要操作的记录');
        		return;
        	}
        	Ext.Msg.confirm('提示', "请确认是否删除数据",function (button,text){if(button == 'yes'){
	        	Ext.Ajax.request( {
	        		url : path + "/deviceqr!deleteDevice.action",
	        		method : 'post',
	        		params : {
	        			dids:dids,
	        			deviceWids:deviceWids
	        		},
	        		success : function(response, options) {
	        			var o = Ext.util.JSON.decode(response.responseText);
	        			if(o.i_type && "success"== o.i_type){
	        				//Ext.Msg.alert('提示', '设置成功');
	        				reFreshStatus('操作成功，是否刷新设备在线状态?');
	        			}else{
	        				Ext.Msg.alert('提示', o.i_msg); 
	        			}
	        		},
	        		failure : function() {
	        		}
	        	});
        	}
    		});
        }
		threebar = new Ext.Toolbar({
			items:['请选择固件版本&nbsp;',comb1,{
				text : '推送升级',
				handler : function() {
					var selectVal = comb1.getRawValue();
					if(!selectVal){
						Ext.Msg.alert('提示', '请选择固件版本');
						return;
					}
					var gcm = grid.getSelectionModel();
					var dids = '';
					var rows = gcm.getSelections();
				    if (rows.length > 0) {
						for ( var i = 0; i < rows.length; i++) {
							var row = rows[i];
							dids = dids + row.data.deviceId + ','; // 拼装ID串
						}
					} else {
						Ext.Msg.alert('提示', '请勾选要操作的记录');
						return;
					}
//				    alert(dids);
				    Ext.Ajax.request( {
						  url : path + "/deviceqr!PushData.action",
						  method : 'post',
						  params : {
							  dids:dids,
							  version:selectVal
						  },
						  success : function(response, options) {
						  var o = Ext.util.JSON.decode(response.responseText);
						   if(o.i_type && "success"== o.i_type){
							   Ext.Msg.alert('提示', '推送成功');
						   }else{
						   	   Ext.Msg.alert('提示', '推送失败：'+o.i_msg); 
						   }
						  },
						  failure : function() {
						  	
						  }
			 		});
				}
			},{
				text : '刷新设备在线状态',
				handler : function () {reFreshStatus('刷新设备在线状态?');}
			},{
				text : '设置非自动更新',
				menu: menuFile
			},{
				text : '强制解绑设备',
				handler : function () {
					var gcm = grid.getSelectionModel();
					var dids = '';
					var rows = gcm.getSelections();
				    if (rows.length > 0) {
						for ( var i = 0; i < rows.length; i++) {
							var row = rows[i];
							dids = dids +row.data.wxDeviceId +","; // 拼装ID串
						}
					} else {
						Ext.Msg.alert('提示', '请勾选要操作的记录');
						return;
					}
				    Ext.Ajax.request({
						  url : path + "/deviceqr!forceunbindall.action",
						  method : 'post',
						  params : {
							  deviceWIds:dids
						  },
						  success : function(response, options) {
						  var o = Ext.util.JSON.decode(response.responseText);
						   	   Ext.Msg.alert('提示', o.i_msg); 
						  },
						  failure : function() {
						  	
						  }
			 		});
				}
			}
			      ]
		});
        grid = new Ext.grid.EditorGridPanel({ 
			region:'center',
			border:false,
			height:'100%',
			sm:sm,
			viewConfig: {
	            forceFit: true, //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
	            emptyText: '系统中还没有任务'
	        },
            cm:column, 
            store:store, 
            loadMask:true, 
//            frame:true, 
            autoScroll:true, 
            tbar:[],
            listeners : {   
                'render' : function(){   
                 onetbar.render(this.tbar); //add one tbar   
                 twotbar.render(this.tbar); //add two tbar   
                 threebar.render(this.tbar); //add three tbar   
            }}, 
            bbar:new Ext.PagingToolbar({
					store : store,
					displayInfo : true,
					pageSize : 100,
					prependButtons : true,
					beforePageText : '第',
					afterPageText : '页，共{0}页',
					displayMsg : '第{0}到{1}条记录，共{2}条',
					emptyMsg : "没有记录"
				})
        });
  	var mainPanel = new Ext.Panel({
  		region:"center",
		layout:'border',
		border:false,
		items:[grid]
	});
	
   var viewport=new Ext.Viewport({
       //enableTabScroll:true,
       layout:"border",
       border:false,
       items:[
           mainPanel
   	   ]
   });
   
   
   
   store1 = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : path + "/deviceqr!qrBind.action"
		}),
		border:false,
		reader : new Ext.data.JsonReader({
			root : 'products',
			totalProperty : 'totalCount',
			fields : [
				{name:  'id'},
				{name:  'userId'},
				{name : 'deviceIdW'},
				{name : 'characterType'},
				{name : 'createTime'},
				{name : 'nickName'}
			]
		}),
		remoteSort : true
	});
	store1.load({params:{start:0,limit:100}});
	var datas = [  
	             ["one","one"],  
	             ["two","two"],  
	             ["three","three"]
	         ];
   column1=new Ext.grid.ColumnModel( 
       [ 
       	new Ext.grid.RowNumberer(),
       	 	sm1,
           {header:"用户ID",dataIndex:"userId",sortable:true}, 
           {header:"微信设备ID",dataIndex:"deviceIdW",sortable:true,hidden:true},
           {header:"性格",dataIndex:"characterType",sortable:true
        	/*editor:new Ext.grid.GridEditor(new Ext.form.ComboBox({
           	store:new Ext.data.SimpleStore({                  
           		fields:["value","text"],                  
           		data:datas              
           	}),             
           displayField:"text",              
           valueField:"value",              
           mode:"local",              
           triggerAction:"all",             
           emptyText:"请选择"                        
           }))*/ 
           },
           {header:"绑定时间",dataIndex:"createTime",sortable:true},
           {header:"昵称",dataIndex:"nickName",width:180,sortable:true}
//           {header:"operate",dataIndex:"id",
//	            renderer: function (value, meta, record) {
//	            			var formatStr = "<input id = 'bt_"+record.get('id')+"' onclick=\"deleteRow('"+record.get('userId')+"','"+record.get('deviceIdW')+"','"+record.get('id')+"');\" type='button' value='删除' width ='30px'/>"; 
//           				var resultStr = String.format(formatStr);
//           				return "<div>" + resultStr + "</div>";
//       				  } .createDelegate(this)
//	            } 
       ] 
   ); 
   
   
   });
   	function showWin(deviceIdW,deviceId){
   		var _grid = new Ext.grid.EditorGridPanel({ 
   	       //height:500,
   			viewConfig: {
   	           forceFit: true, //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
   	           emptyText: '系统中还没有任务'
   	       },
//   	       border:false,
//   		   autoWidth:true,
//   		   width:500, 
   		   layout:'fit' , 
   		   sm:sm1,
   	       cm:column1, 
   	       store:store1, 
   	       autoExpandColumn:0, 
   	       loadMask:true, 
//   	       frame:true, 
   	       autoScroll:true, 
   	       tbar:[{  
   	          text:"设备解绑",  
   	          handler:function(){
   	        	var _gcm = _grid.getSelectionModel();
   	  			var _dids = '';
   	  			var _rows = _gcm.getSelections();
   	  		    if (_rows.length > 0) {
   	  				for ( var i = 0; i < _rows.length; i++) {
   	  					var row = _rows[i];
   	  					_dids = _dids + row.data.userId + ','; // 拼装ID串
   	  				}
   	  			} else {
   	  				Ext.Msg.alert('提示', '请勾选要操作的记录');
   	  				return;
   	  			}  
   	        	  
   	       	  Ext.Ajax.request( {
   					  url : path + "/deviceqr!forceunbind.action",
   					  method : 'post',
   					  params : {
   						  userIds : _dids,
   						  deviceId:deviceId
   					  },
   					  success : function(response, options) {
   					       var o = Ext.util.JSON.decode(response.responseText);
	   						Ext.Msg.alert("提示",o.i_msg,function(){  
	   							ds1Reload(deviceIdW);
					        });  
   					  },
   					  failure : function() {
   					  	
   					  }
   		 		});
   	         }
   	       }]
   	   });
   		if(!newWin){
   			    newWin = new Ext.Window({
   				//el : 'branchDiv',
		    	border:false,
   				title : '绑定关系',
   				layout : 'fit',
   				closable : true,
   				width : 850,
   				height : 500,
   				//bodyStyle : 'padding:5px;',
   				closeAction : 'hide',
   				plain : true,
   				modal : true,
   				resizable : false,
   				items : [_grid]
   			});
   		}
   		newWin.show();
   		ds1Reload(deviceIdW);
   }
   	function showEditWin(deviceIdW,deviceId){
//   		var sm2 = new Ext.grid.CheckboxSelectionModel();
   		var rowSelectionModel = grid.getSelectionModel();
   		var selArr = grid.getSelectionModel().getSelections();
   		if(selArr.length == 0){
   			Ext.Msg.alert("提示","请先勾选需要修改的记录！");
			return;
   		}
   		var datas = [];
   		for(var i=0;i<selArr.length;i++){
   			datas.push(selArr[i].data);
   		}
   		var EditnewWin;
   	    var column2 =new Ext.grid.ColumnModel( 
             [ 
             	new Ext.grid.RowNumberer(),
             	{header:'id',dataIndex:'id',hidden:true},
//             	sm2,
             	{header:"设备ID",align:'center',dataIndex:"deviceId",sortable:true, editor: new Ext.form.TextField({
             		   allowBlank: false,
             		   maxLength:32
             	 })} ,
             	{header:"设备二维码ticket",align:'center',dataIndex:"qrTicket",sortable:true,width:200},
 	            {header:"微信设备ID",align:'center',dataIndex:"wxDeviceId",sortable:true,width:150,hidden:true}, 
 	            {header:"扫地机型号",align:'center',dataIndex:"model",sortable:true,width:50,editor: new Ext.form.TextField({
          		   allowBlank: false,
          		   maxLength:6
            	 })} ,
 	            {header:"自动更新",align:'center',dataIndex:"autoUpdate",sortable:true,width:50,hidden:true,renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
                     if(value==0){
                         return '否';
                       } else if(value==1){
                         return '是';
                       }else{
                     	  return ;
                       }
 	            }} ,
 	            {header:"版本号",align:'center',dataIndex:"version",sortable:true,width:50,hidden:true} ,
 	            {header:"状态",align:'center',dataIndex:"status",sortable:true,width:50,hidden:true,renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){    
                     if(value=='0'){
                         return '离线';
                       } else if(value=='1') {
                         return '在线';
                       }else{
                     	return;
                       }
 	            }},{header:"操作",align:'center',dataIndex:"id",width:50,hidden:true,
 		            renderer: function (value, meta, record) {
 					var formatStr = "<input id = 'bt_"+record.get('deviceId')+"' onclick=\"showWin('"+record.get('wxDeviceId')+"','"+record.get('deviceId')+"');\" type='button' value='显示绑定关系' width ='30px'/>"; 
 					var resultStr = String.format(formatStr);
 					return "<div>" + resultStr + "</div>";
 		        } .createDelegate(this)
 	            }
             ] 
         ); 
        var store2 = new  Ext.data.ArrayStore({
        	data:datas,
        	fields: [
	            {name:  'id',mapping:"id"},
				{name:  'wxDeviceId',mapping:"wxDeviceId"},
				{name : 'qrTicket',mapping:"qrTicket"},
				{name : 'deviceId',mapping:"deviceId"},
				{name : 'model',mapping:"model"},
				{name : 'version',mapping:"version"},
				{name : 'status',mapping:"status"},
				{name : 'autoUpdate',mapping:"autoUpdate"}
        	]
		});
//        store2.loadData(selArr);
//        store2.load({params:{start:0,limit:100}});
		
   		var _grid = new Ext.grid.EditorGridPanel({
   			viewConfig: {
   				forceFit: true, //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
   				emptyText: '系统中还没有任务'
   			},
   			layout:'fit' , 
//   			sm:sm2,
   			cm:column2, 
   			store:store2, 
   			autoExpandColumn:0, 
   			loadMask:true, 
   			autoScroll:true, 
   			tbar:[{  
   				text:"确认修改",  
   				handler:function(){
   					var recs=store2.getModifiedRecords();
   					if(recs.length==0){
   						Ext.Msg.alert("提示","尚未修改数据！");
   						return;
   					}
   					var tempUpdate=[];
   					for(var i=0;i<recs.length;i++){  
   	                   tempUpdate.push(recs[i].data);  
   					}
   					var deviceInfo=Ext.util.JSON.encode(tempUpdate); 
   					Ext.Ajax.request( {
   						url : path + "/deviceqr!updateDevice.action",
   						method : 'post',
   						params : {
   							deviceInfo : deviceInfo
   						},
   						success : function(response, options) {
   							var o = Ext.util.JSON.decode(response.responseText);
   							Ext.Msg.alert("提示",o.i_msg,function(){  
//   								ds1Reload(deviceIdW);
//   								reFreshStatus('是否刷新设备在线状态?');
//   								store2.load({params:{start:0,limit:100}});
   								//EditnewWin.close();
   							});  
   						},
   						failure : function() {
   							Ext.Msg.alert("提示","操作失败");
   						}
   					});
   				}
   			}]
   		});
   		
   		if(!EditnewWin){
   			EditnewWin = new Ext.Window({
   				//el : 'branchDiv',
   				border:false,
//   				title : '绑定关系',
   				layout : 'fit',
   				closable : true,
   				width : 850,
   				height : 500,
   				//bodyStyle : 'padding:5px;',
   				closeAction : 'hide',
   				plain : true,
   				modal : true,
   				resizable : false,
   				items : [_grid]
   			});
   		}
   		EditnewWin.show();
//   		ds1Reload(deviceIdW);
   	}
  	function ds1Reload(deviceIdW){
   		store1.baseParams['deviceIdW'] = deviceIdW;
   		store1.reload({
   			params: {start:0,limit:100},
   			callback: function(records, options, success){
   				if(!success){
   					Ext.Msg.alert('info', '查询失败');
   				}else{
   					
   				}
   			},
   			scope: store1
   		});
   	} 
   	

   
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
	   
//		var device_id_w = document.getElementById('device_id_w').value ;
		var device_id = document.getElementById('device_id').value ;
		var model = document.getElementById('model').value ;
		var createDateStart = document.getElementById('createDateStart').value ;
		var createDateEnd = document.getElementById('createDateEnd').value ;
		
		var version = document.getElementById('fmversion').value ;
		var onLineStatus = document.getElementById('onLineStatus').value ;
		var qrticket = document.getElementById('qrticket').value ;
		var qrticketisUse = ticketIsUse.getValue();
		
		if("全部" == model){
			model = "";
		}
		if("全部" == version){
			version = "";
		}
		if("全部" == onLineStatus){
			onLineStatus = "";
		}else if("在线" == onLineStatus){
			onLineStatus = "1";
		}else{
			onLineStatus = "0";
		}
		
//		store.baseParams['wxDeviceId'] = device_id_w;
		store.baseParams['qrTicket'] = qrticket;
		store.baseParams['deviceId'] = device_id;
		store.baseParams['model'] = model;
		store.baseParams['createDateStart'] = createDateStart;
		store.baseParams['createDateEnd'] = createDateEnd;
		store.baseParams['version'] = version;
		store.baseParams['status'] = onLineStatus;
		store.baseParams['qrticketisUse'] = qrticketisUse;
		store.reload({
			params: {start:0,limit:100},
			callback: function(records, options, success){
//				console.log(records);
			},
			scope: store
		});
	}
   
   function exportData(){
		var beginTime = this.beginTimeField.getValue();
		var endTime = this.endTimeField.getValue();
		if (!beginTime && !endTime) {
			var _bt = new Date();
			_bt.setDate(1);
			_bt.setHours(0, 0, 0, 0);
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
	   
		var device_id = document.getElementById('device_id').value ;
		var model = document.getElementById('model').value ;
		var createDateStart = document.getElementById('createDateStart').value ;
		var createDateEnd = document.getElementById('createDateEnd').value ;
		var version = document.getElementById('fmversion').value ;
		var onLineStatus = document.getElementById('onLineStatus').value ;
		var qrticket = document.getElementById('qrticket').value ;
		var qrticketisUse = ticketIsUse.getValue();
		if("全部" == model){
			model = "";
		}
		if("全部" == version){
			version = "";
		}
		if("全部" == onLineStatus){
			onLineStatus = "";
		}else if("在线" == onLineStatus){
			onLineStatus = "1";
		}else{
			onLineStatus = "0";
		}
		Ext.Ajax.request({    
           url:path + "/deviceqr!exportData.action",    
           method:'post',
           params : {
           	qrTicket:qrticket,
           	deviceId:device_id,
           	model:model,
           	createDateStart:createDateStart,
           	createDateEnd:createDateEnd,
           	version:version,
           	status:onLineStatus,
           	qrticketisUse:qrticketisUse
			},
           waitMsg:'数据加载中，请稍后....',    
           success:function(response,opts){    
               var obj=Ext.decode(response.responseText);    
               if(obj.i_type == "success") {
                   window.open(path+"/uploadFile/"+obj.i_file_name,"_blank"); 
               } else {
               	Ext.Msg.alert("提示", obj.i_msg);
               }  
           },    
           failure:function(response,opts){    
               //var obj=Ext.decode(response.responseText);    
           }
	    });  
   }
   function reFreshStatus(tip){
		Ext.Msg.confirm('提示', tip,function (button,text){if(button == 'yes'){
			Ext.Ajax.request( {
				  url : path + "/deviceqr!refreshStatus.action",
				  method : 'post',
				  params : {
				  },
				  success : function(response, options) {
				  var o = Ext.util.JSON.decode(response.responseText);
				   if(o.i_type && "success"== o.i_type){
//					   Ext.Msg.alert('提示', '刷新成功'); 
					   Ext.Msg.alert("提示",'刷新成功！',function(){
						   reloadData();
					   });
					   
				   }else{
//				   	   Ext.Msg.alert('提示', o.i_msg); 
//				   	   reloadData();
					   Ext.Msg.alert("提示",o.i_msg,function(){
						   reloadData();
					   });
				   }
				  },
				  failure : function() {
				  	
				  }
	 		});
		}
		});
	}
   function generatQr(num){
		Ext.Ajax.request( {
		  url : path + "/deviceqr!generatQr.action",
		  method : 'post',
		  params : {
			  num:num,
		  },
		  success : function(response, options) {
		  var o = Ext.util.JSON.decode(response.responseText);
		  Ext.Msg.alert("提示",o.i_msg,function(){  
			  reloadData();
	      });  
		  },
		  failure : function() {
		  }
		});
	}
        
   
   

	
	
	
