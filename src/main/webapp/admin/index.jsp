<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String userRole = (String)session.getAttribute("userRole");
boolean isNotAdmin = !"0".equals(userRole);
%>
<html>
	<head>
		<title>数据管理</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<script type="text/javascript">
			var path = '<%=path%>';
			var isNotAdmin = <%=isNotAdmin%>; 
		</script>
		<script type="text/javascript" src="../ext3/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="../ext3/ext-all.js"></script>
		<script type="text/javascript" src="../ext3/locale/ext-lang-zh_CN.js"></script>
		<script type="text/javascript" src="../admin/js/upLoad.js"></script>
		<script type="text/javascript" src="../admin/js/index.js"></script>
		<script type="text/javascript" src="../admin/js/commons.js"></script>
		<link rel="stylesheet" type="text/css" href="../ext3/resources/css/ext-all.css" />
		<style type="text/css">
			.font_b{
				color: blue;
				font-size: 16px;
				font-weight: bold;
			}
			#header {
			    background: #1E4176 url("images/head_bg.jpg") repeat-x scroll 0px 0px;
			    border: 0px none;
			    padding: 0px 3px;
			    margin: 0px;
			}
			#header .api-title {
			    background: transparent url("images/head_left_bg.jpg") no-repeat scroll left top;
			    color: #FFF;
			    height: 31px;
			    padding: 6px 0px 0px 100px;
			}
    	</style>
	</head>
	<jsp:include page="permission.jsp" >
		<jsp:param name="pathName" value="index" />
	</jsp:include>
	<body scroll="no" id="docs">
	</body>
</html>