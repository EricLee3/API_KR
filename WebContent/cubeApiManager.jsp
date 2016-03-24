<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="com.service.CubeApiCreateJSON" %>
<%@ page import="com.service.command.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.httpclient.methods.GetMethod" %>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod" %>
<%@ page import="org.apache.commons.httpclient.HttpClient" %>
<%@ page import="org.apache.commons.httpclient.HttpException" %>
<%
	CubeApiCreateJSON jsonDAO	= CubeApiCreateJSON.getInstance();

	String dbmode	= StringUtil.nullTo(request.getParameter("dbmode"),"");
	String command	= StringUtil.nullTo(request.getParameter("command"),"");
	String joinID	= StringUtil.nullTo(request.getParameter("joinID"),"");
	String jsonData	= StringUtil.nullTo(request.getParameter("data"),"");
	
	String transCD = "30";	//큐브 구분코드 (10:wizwid, 20:wck, 30:mangoKR)
	
	String resultMessage = null;
	String sendDomain = ""; 	

	
	
	//sendDomain = "http://kr.mangoplus.com";		//리얼
	sendDomain = "http://kr-test.mangoplus.com";	//테스트
	
	
	if (dbmode.equals("") || dbmode == null) {
		resultMessage = "DB명이 올바르지않습니다.";
	} else {
		if (command.equals("ProductData")) {
			//상품 등록,수정
			resultMessage = jsonDAO.api_Auto_Item(dbmode,command,joinID,transCD,sendDomain);	
		} else if (command.equals("OrderRetrieve") || command.equals("OrderReturnRetrieve") || command.equals("OrderCancelRetrieve") || command.equals("OrderReturnCancelRetrieve")) {
			//주문정보, 반품정보, 취소정보, 반품취소정보 요청
			resultMessage = jsonDAO.api_Auto_PO(dbmode,command,joinID,transCD,sendDomain);	
		} else if (command.equals("SoldOutCancel") || command.equals("ReturnRefuse")) {
			//품절취소, 반품거부
			resultMessage = jsonDAO.api_Auto_PO_Refuse(dbmode,command,joinID,transCD,sendDomain);	
		} else if (command.equals("ItemStock")) {
			//상품 재고연동
			resultMessage = jsonDAO.api_Auto_ItemStock(dbmode,command,joinID,transCD,sendDomain);	
		} else {
			resultMessage = "MODE FAIL!!";
		}
	}
	
%>
<%=resultMessage%>