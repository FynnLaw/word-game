<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<script type="text/javascript">
			var path = '<%=path%>';
			var store;
			var wordGamePanel;
			var columnBreakList;
			var breakListGridPanel;
			var addButton;
			var listPanel;
			var formPanel;
			var northPanel;
			var wordGameTreePanel = null;
			var wordGamePanelWestPanel;
			var wordGamePanelWest;
			var formPanelEast;
			var wordGamePanelEast;
			var viewport;
		</script>
		<script type="text/javascript" src="../ext3/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="../ext3/ext-all.js"></script>
		<script type="text/javascript" src="../ext3/locale/ext-lang-zh_CN.js"></script>
		<script type="text/javascript" src="../admin/js/wordGame.js"></script>
		<script type="text/javascript" src="../ext3/ux/FileUploadField.js"></script>
		<script type="text/javascript" src="../admin/js/commons.js"></script>
		<script type="text/jav ascript" src="../ext3/ux/DateTimeField1.js"></script>
		
		<link rel="stylesheet" type="text/css" href="../ext3/resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../ext3/ux/css/ux-all.css" />
	</head>
	<jsp:include page="permission.jsp" >
		<jsp:param name="pathName" value="index" />
	</jsp:include>
</html>