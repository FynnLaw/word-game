<%@ page contentType="text/html; charset=UTF-8" language="java" errorPage="" %>
<%   
	String isLogin = (String)session.getAttribute("isLogin");
	String pathName = (String)request.getParameter("pathName");
	String ErrorMsg = "您尚未登录！！";
	String urlsessionnoactive = request.getContextPath() + "/admin/login.jsp?pathName="+pathName;
	if(isLogin == null || "".equals(isLogin)){
%>   
<script language="javascript">
	var urlsessionnoactive="<%=urlsessionnoactive%>";
	//alert("<%=ErrorMsg%>");
	top.location.href=urlsessionnoactive;
</script>
<%
}
%>