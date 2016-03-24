<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="com.service.ScService" %>
<%@ page import="com.service.command.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.httpclient.methods.GetMethod" %>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod" %>
<%@ page import="org.apache.commons.httpclient.HttpClient" %>
<%@ page import="org.apache.commons.httpclient.HttpException" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SC & CUBE API 연동</title>
</head>
<body>
<%
	ScService cs	= ScService.getInstance();
	 
	String dbmode	= StringUtil.nullTo(request.getParameter("dbmode"),"");
	String command	= StringUtil.nullTo(request.getParameter("command"),"");
	String xmlData	= StringUtil.nullTo(request.getParameter("data"),"");
	
	
	String transCD = "40";	//큐브 구분코드 (10:wizwid, 20:wck, 30:mangoKR)
	String resultMessage = null;
	String sendDomain = ""; 	

	//sendDomain = "";		//리얼
	sendDomain = "";	//테스트
	
	
	if (dbmode.equals("") || dbmode == null) {
		out.print("DB명이 올바르지않습니다.");
	} else {
		if (command.equals("ProductData")) {
			resultMessage = cs.getItemSendData(dbmode,command,transCD,sendDomain);  	
		} else {
			//cs.setRecvLog(dbmode, inuser, command, command, "N/A", CommonUtil.getCurrentDate(), CommonUtil.getCurrentDate(), "500", "It's wrong cammand!", transCD);
		}
	}
	
%>
<%=resultMessage%>
</body>
</html>