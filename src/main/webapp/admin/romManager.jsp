<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
	<head>
		<title>固件管理</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<script type="text/javascript">
			var path = '<%=path%>';
			var searchPanel;
			var maincontent;
			var store;
			var _grid;
			var _editForm;
			//var newWin;
		</script>
		<script type="text/javascript" src="../ext3/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="../ext3/ext-all.js"></script>
		<script type="text/javascript" src="../ext3/locale/ext-lang-zh_CN.js"></script>
		<script type="text/javascript" src="../admin/js/romManager.js"></script>
		<script type="text/javascript" src="../ext3/ux/FileUploadField.js"></script>
		<script type="text/javascript" src="../admin/js/commons.js"></script>
		<link rel="stylesheet" type="text/css" href="../ext3/resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../ext3/ux/css/ux-all.css" />
		
		<script type="text/javascript" src="../ext3/ux/DateTimeField1.js"></script>
		<style type="text/css">
    	</style>
	</head>
	<jsp:include page="permission.jsp" >
		<jsp:param name="pathName" value="index" />
	</jsp:include>
	<body scroll="no" id="romManager">
	</body>
</html>