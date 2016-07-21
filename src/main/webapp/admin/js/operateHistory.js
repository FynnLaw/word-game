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
   		
   		var qrUrl = path + "/operate!";
   		var order;
        store = new Ext.data.Store({
			url : qrUrl+"list.action",
			reader : new Ext.data.JsonReader({
				root : 'products',
				totalProperty : 'totalCount',
				id : 'querydate',
				fields : [
					{name:  'operateUserId'},
					{name : 'opereteTypeId'},
					{name : 'createTime'},
					{name : 'operateUserIp'},
					{name : 'isSuccess'},
					{name : 'operateSummary'},
					{name : 'id'}
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:100}});
        
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
            	{header:"操作用户名",align:'center',dataIndex:"operateUserId",sortable:true}, 
            	{header:"操作类型",align:'center',dataIndex:"opereteTypeId",sortable:true}, 
            	{header:"操作时间",align:'center',dataIndex:"createTime",sortable:true}, 
	            {header:"操作结果",align:'center',dataIndex:"isSuccess",sortable:true,renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){    
                    if(value=='0'){
                        return '失败';
                      } else {
                        return '成功';
                      }
	            }},
	            {header:"操作用户IP",align:'center',dataIndex:"operateUserIp",sortable:true},
	            {header:"操作详情",align:'center',dataIndex:"id",renderer: function (value, meta, record) {
        			var formatStr = "<input id = 'bt_edit_" + record.get('id')
					+ "' onclick=\"Ext.Msg.alert('详情','" + (record.get('operateSummary')+'').replace(/\'/g,  '\\\'')
					+ "');\" type='button' value='查看' width ='15px'/>&nbsp;&nbsp;"; 
								            			
    				var resultStr = String.format(formatStr);
    				return "<div>" + resultStr + "</div>";
				  } .createDelegate(this)}
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
        
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['操作用户名：', {
		  		  id : 'operateUser',
		  		  xtype : 'textfield',
		  		  width : 115
		  	}, '&nbsp;&nbsp;创建时间：',
		  	beginTimeField,'至',
		  	endTimeField,{
				text : '查询',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				handler : function() {
					Ext.getCmp('operateUser').setValue("");
					Ext.getCmp('createDateStart').setValue("");
					Ext.getCmp('createDateEnd').setValue("");
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
		
		var userName = document.getElementById('operateUser').value ;
		store.baseParams['operateUser'] = userName;
		store.baseParams['createDateStart'] = beginTime;
		store.baseParams['createDateEnd'] = endTime;
		store.reload({
			params: {start:0,limit:100},
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
