    Ext.QuickTips.init();
   	Ext.onReady(function(){
   		
   		var Btn1 = new Ext.Button({  
            text : '确定'  ,
            style: {
                marginLeft:'100px',//距左边宽度
                marginRight:'20px'//距右边宽度
            },
            handler:function(){
            	var oldpwd = document.getElementById('oldpwd').value ;
            	var newpwd = document.getElementById('newpwd').value ;
            	var cfnewpwd = document.getElementById('cfnewpwd').value ;
            	if(newpwd != cfnewpwd){
            		Ext.Msg.alert('提示消息框','您输入的密码与确认密码不一致');
            		return;
            	}
				Ext.Ajax.request( {
					  url : path + "/user!updatePwd.action",
					  method : 'post',
					  params : {
						  oldpwd : oldpwd,
						  newpwd : newpwd
					  },
					  success : function(response, options) {
					   var o = Ext.util.JSON.decode(response.responseText);
					   if(o.i_type && "success"== o.i_type){
						   Ext.Msg.alert('提示', "操作成功！"); 
						   formReset();
					   }else{
					   	   Ext.Msg.alert('提示', o.msg); 
					   }
					  },
					  failure : function() {
						  Ext.Msg.alert('提示', "操作失败！");
					  }
		 		});
            },
        });  
  
   		var Btn2 = new Ext.Button({  
            text : '重置' ,
            handler:function(){formReset();},
        });
   		
   	  var panle =  new Ext.form.FormPanel({
   		  region: "center",
          frame: true,
//          autoHeight: true,
          labelWidth: 80,
          labelAlign: "right",
          bodyStyle:"text-align:left",
          border : false,
          items: [
             {xtype:"textfield", width:180,id: "oldpwd", fieldLabel: "原密码", inputType: "password"},
             {xtype:"textfield", width:180,id: "newpwd", fieldLabel: "新密码", inputType: "password"},
             {xtype:"textfield", width:180,id: "cfnewpwd", fieldLabel: "确认新密码", inputType: "password",
             },
             {xtype:'panel',layout:'column',width: 400,items:[
                                                        Btn1,Btn2
                                                  ]}
          ],
       });
   	  
   	  new Ext.Viewport({
          layout: "border",
          defaults: {
              frame: true
          },
          items: [
                  	panle
                 ]
      });
   	});
   	
   	
   	function formReset(){
    	document.getElementById('oldpwd').value ="" ;
    	document.getElementById('newpwd').value ="";
    	document.getElementById('cfnewpwd').value ="";
   	}
   	
   	
    
