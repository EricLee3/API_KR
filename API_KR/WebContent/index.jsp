<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import="org.apache.commons.httpclient.methods.GetMethod" %>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod" %>
<%@ page import="org.apache.commons.httpclient.HttpClient" %>
<%@ page import="org.apache.commons.httpclient.HttpException" %>
<%
	String wizwid_connip = "http://220.117.243.71"; //LIVE
	String wizwid_connip2 = "http://220.117.243.72"; //LIVE
	String connip = "";
	int statusCode = 0;

try {
	HttpClient client = new HttpClient();
	GetMethod get = new GetMethod(wizwid_connip);
	
	statusCode = client.executeMethod(get);
	
	
	if(statusCode == 200){
		connip = wizwid_connip;
	}else{
		connip = wizwid_connip2;
	}
 }catch(NullPointerException e){
	connip = wizwid_connip2;
 }catch(Exception ex){
	connip = wizwid_connip2;
 }
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
</head>
<body>
EDI 전송을 위한 서버
</body>
</html>