<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	   <script type="text/javascript">
	   		var searchPanel;
			var maincontent;
			var store;
			//var _grid;
			var _editForm;
			var newWin;
		    var path = '<%=path%>';
	   </script>
		<script type="text/javascript" src="../ext3/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="../ext3/ext-all.js"></script>
		<script type="text/javascript" src="../ext3/locale/ext-lang-zh_CN.js"></script>
		<script type="text/javascript" src="../admin/js/dataOperate.js?m="+Math.random()></script>
		<script type="text/javascript" src="../admin/js/upLoad.js"></script>
		<script type="text/javascript" src="../admin/js/commons.js"></script>
		<link rel="stylesheet" type="text/css" href="../ext3/resources/css/ext-all.css" />
		<link rel="stylesheet" type="text/css" href="../ext3/ux/css/ux-all.css" />
		
		<script type="text/javascript" src="../ext3/ux/DateTimeField1.js"></script>
		<style type="text/css">
			
    	</style>
</head>
</html>
