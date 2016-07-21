<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String pathName = request.getParameter("pathName")==null?"index":request.getParameter("pathName");

session.removeAttribute("isLogin");//判断是否登陆
session.removeAttribute("userName");			
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>login</title>
<link rel="stylesheet" type="text/css" href="../css/jquery.alerts.css">
<link rel="stylesheet" type="text/css" href="../css/login.css">
<script src="../js/jquery.js"></script>
<script src="../js/jquery.ui.draggable.js" type="text/javascript"></script>
<script src="../js/jquery.alerts.js" type="text/javascript"></script>
</head>

<body>
<!--"content"-->
<div class="alert-content1 tc lh22 fs14 cl7">
  <form action="" method="get" class="login fr">
    <font class="loginhint dsp bfb fs14 cl8 tl" style="display:none;">你还没有输入账号！</font>
    <input id="username" type="text" class="homeinput01 mb10" placeholder="您的账号" autocomplete="off">
    <input id="userpwd" type="password" class="homeinput02 mb10" placeholder="登录密码" autocomplete="off">
    <p class="bfb tr pb5 dsp pt5">
      <button type="button" id="loginBtn" class="btn btn-primary loginbtn" style="width:100%;font-size:20px;"  onclick="login();" onfocus = this.blur() />
    	  登  录
      </button>
    </p>
  </form>
</div>
<script type="text/javascript">
	var path = '<%=path%>';
	function login(){
		var username = $("#username").val();
		var userpwd = $("#userpwd").val();
		var pathName = '<%=pathName%>';
		if(username == null || username == ""){
			jAlert("请输入用户名", '提示');
		}else if(userpwd == null || userpwd == ""){
			jAlert("请输入密码", '提示');
		}else{
			$.post(path+"/login!login.action",{"userName":username,"userPwd":userpwd},function(data){
				if("success" == data.i_type){
					//跳转
					window.location.href = pathName+".jsp";
				}else{
					jAlert(data.i_msg, '提示');
				}
			},"json");
		}
	}
	
	document.onkeyup = function (event) {
           var e = event || window.event;
           var keyCode = e.keyCode || e.which;
           if(keyCode == 13){
           	login();
           }
    }
</script>
</body>
</html>
