//*****************************************上传的公共js***************************************************************//   
/**  
 
 * 约定:types为调用时传来的参数.形式为jsp-gig-png  
 *      uploadid为上传后要填充路径的控件id  
 *      上传的属性均为upload  
 * 功能:页面调用openUpload("","");方法即可  
 */  
//...允许上传的后缀名   

var types = "";   
//...上传后填充控件的id   
var uploadid = "";  
var winUpload;
function openUpload(type,id){   
    types = type;   
    uploadid = id;
    if(!winUpload){
    	winUpload = new Ext.Window({   
    	    title: '资源上传',   
    	    width: 400,   
    	    height:200,   
    	    minWidth: 300,   
    	    minHeight: 100,   
    	    layout: 'fit',   
    	    closeAction : 'hide',
    	    plain : true,
			modal : true,
    	    bodyStyle:'padding:5px;',   
    	    buttonAlign:'center',   
    	    items: formUpload,   
    	    buttons: [{   
    	      text: '上 传',   
    	      handler: function() {   
    	        if(formUpload.form.isValid()){   
    	          Ext.MessageBox.show({   
    	               title: 'Please wait',   
    	               msg: 'Uploading...',   
    	               progressText: '',   
    	               width:300,   
    	               progress:true,   
    	               closable:false,   
    	               animEl: 'loding'  
    	          });   
    	          formUpload.getForm().submit({       
	    	          url:path +'/upload!uploadFile.action?uploadType=0',   
	    	          success: function(form, action){
    	            	  var data = Ext.decode(action.response.responseText);
    	            	  if(data.i_type == "success"){
    	            	   	       Ext.Ajax.request({    
						                url:path + "/deviceqr!importData.action",    
						                method:'post',
						                params : {
										   filePath : data.filePath
										},
						                waitMsg:'数据加载中，请稍后....',    
						                success:function(response,opts){    
						                    var obj=Ext.decode(response.responseText);    
						                    if(obj.i_type == "success") {//如果你处理的JSON串中true不是字符串，就obj.success == true  
						                         //你后台返回success 为 false时执行的代码  
						                          winUpload.hide();
						                          Ext.Msg.confirm('tip', 'EXCEL总共有'+obj.totalSize+'条，成功导入'+obj.successSize+'条，是否下载查看详情！',function (button,text){if(button == 'yes'){
						                        	  //window.location.href = obj.fileName;//这样就可以弹出下载对话框了
						                        	  window.open(path+"/uploadFile/"+data.fileName,"_blank"); 
						                          }});
						                    } else {  
						                         //你后台返回success 为 false时执行的代码  
						                    }  
						                },    
						                failure:function(response,opts){    
						                    var obj=Ext.decode(response.responseText);    
						                }
    							    });  
    	            	   }else{
    	            	   	   Ext.Msg.alert('Error', action.response.responseText.msg);
    	            	   }
    	            },       
    	            failure: function(form, action){       
    	              //... action生成的json{msg:上传失败},页面就可以用action.result.msg得到非常之灵活   
    	              Ext.Msg.alert('Error','上传失败！');      
    	            }   
    	          })              
    	        }   
    	       }   
    	    },{   
    	      text: '取 消',   
    	      handler:function(){winUpload.hide();}   
    	    }]   
    	  });
    }
    winUpload.show();   
  }   
var formUpload = new Ext.form.FormPanel({   
    baseCls: 'x-plain',   
    labelWidth: 80,   
    fileUpload:true,   
    defaultType: 'textfield',   
    items: [{   
      xtype: 'textfield',   
      fieldLabel: '文 件',   
      name: 'uploadFile',   
      inputType: 'file',   
      allowBlank: false,   
      blankText: '请上传文件',   
      anchor: '90%'  // anchor width by percentage   
    }]   
  });   
  
  
  
