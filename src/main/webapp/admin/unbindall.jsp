<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>unbindall</title>
<link rel="stylesheet" type="text/css" href="./css/login.css">
<script src="../js/jquery.js"></script>
<script src="../js/jquery.ui.draggable.js" type="text/javascript"></script>
<script src="../js/jquery.alerts.js" type="text/javascript"></script>

<link href="../css/jquery.alerts.css" rel="stylesheet" type="text/css" media="screen">
</head>

<body>
	<jsp:include page="permission.jsp" >
		<jsp:param name="pathName" value="unbindall" />
	</jsp:include>
<!--"content"-->
<form class="example-form fl" role="form" style="width:100%;">
<div class="input-content pt20">
  <div class="input-group">
    <label>设备ID：</label>
    <input id="deviceId" type="text" class="form-control" value="" style="margin-left:0;">
      <button type="button" class="btn btn-primary loginbtn1" onclick="unbindAll()" style="width:80px;" onfocus = this.blur() />
      	解绑	
      </button>
  </div>
  <p>注：该功能会解除设备号相关硬件下所有设备的用户绑定关系</p>
</div>
</form>
<script type="text/javascript">
	var path = '<%=path%>';
	function unbindAll(){
		var deviceId = $("#deviceId").val();
		if(deviceId == null || deviceId == ""){
			jAlert("请输入设备ID", '提示');
			return;
		}
		jConfirm("确认解绑设备：\n" + deviceId , "提示" , function(r){
			if(r){
				$.post(path + "/deviceqr!forceunbindall.action",{"deviceId":deviceId},function(result){
					jAlert(result, '提示');
					$("#deviceId").val("");
				});
			}
		})
	}
</script>
</body>
</html>
