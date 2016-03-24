<?xml version="1.0" encoding="utf-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "javax.servlet.http.HttpServletRequest" %>
<%@ page import = "com.service.WmsService" %>
<%@ page import = "com.service.command.util.*" %>
<%@ page import = "java.net.URLDecoder" %>
<%@ page import = "java.util.*" %>
<%@ page import="org.apache.commons.httpclient.methods.GetMethod" %>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod" %>
<%@ page import="org.apache.commons.httpclient.HttpClient" %>
<%@ page import="org.apache.commons.httpclient.HttpException" %>
<%
	WmsService ws = WmsService.getInstance();
	
	String dbMode = "wmsapi";
	
	String result = "";
	
	String request_div = StringUtil.nullTo(request.getParameter("request_div"),"");		//요청구분 (수신취소:cancel , 입고예정종결처리:finish)
	String last_yn = StringUtil.nullTo(request.getParameter("last_yn"),"");				//마지막 레코드 여부
	String brand_cd = StringUtil.nullTo(request.getParameter("brand_cd"),"");			//브랜드코드
	String whcd = StringUtil.nullTo(request.getParameter("whcd"),"");					//물류센터코드
	String brand_date = StringUtil.nullTo(request.getParameter("brand_date"),"");		//전표일자
	String brand_no = StringUtil.nullTo(request.getParameter("brand_no"),"");			//전표번호
	String cancel_div = StringUtil.nullTo(request.getParameter("cancel_div"),"");		//취소구분 (1:입고/반출, 2:출고/반입, 3:기타입출고)
	
	
	if (request_div.equals("") || brand_cd.equals("") || whcd.equals("") || brand_date.equals("") || brand_no.equals("")) {
		result = "FAIL! 필수 파라미터 누락";
	} else {
		if (request_div.equals("cancel")) {
			if (cancel_div.equals("")) {
				result = "FAIL! 취소 필수 파라미터 누락";
			}
		} 
		
		if (result.equals("")) {
			result = ws.getApiRecvRequest(dbMode, request_div, last_yn, brand_cd, whcd, brand_date, brand_no, cancel_div);
		}
	}

%>
<WmsApiLinker>
	<%
		if(result.contains("SUCCESS")) {
	%>
		<ResultCode>01</ResultCode>
	<%			
		} else {
	%>
		<ResultCode>99</ResultCode>
	<%
		}
	%>
	<ResultMessage><%=result%></ResultMessage>
</WmsApiLinker>
