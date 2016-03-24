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
	
	String transCD = "40";	//큐브 구분코드 (10:wizwid, 20:wck, 30:mangoKR, 40:connectME)
	
	String resultMessage = null;
	String sendDomain = ""; 	
	String cmID = "cube";
	String cmPassKey = "3f53309d78fede6f9f11974ed19a99eeda40fc9f8ab24ee6249d025bceafbcef";
	
	//sendDomain = "http://api.connectme.co.kr";		//리얼
	sendDomain = "http://devapi.connectme.co.kr";	//테스트
	
	
	if (dbmode.equals("") || dbmode == null) {
		resultMessage = "DB명이 올바르지않습니다.";
	} else {
		/*	커넥트미(CM) API 연동 작업 부분 주석 처리.. 2014-03-06
		if (command.equals("ProductData")) {
			-- 상품 등록,수정
			resultMessage = jsonDAO.CM_Api_Item(dbmode,command,joinID,transCD,sendDomain,cmID,cmPassKey);	
		} else if (command.equals("OrderRetrieve") || command.equals("OrderReturnRetrieve") || command.equals("OrderCancelRetrieve") || command.equals("OrderReturnCancelRetrieve")) {
			-- 주문정보(주문조회), 반품정보(반품조회), 
			-- 주문취소정보(주문취소조회), 반품취소정보(반품취소조회)
			resultMessage = jsonDAO.CM_Api_RecvData(dbmode,command,joinID,transCD,sendDomain,cmID,cmPassKey);	
		} else if (command.equals("SoldOutCancel") || command.equals("ReturnRefuse")) {
			-- 품절취소, 반품거부
			resultMessage = jsonDAO.CM_Api_Refuse(dbmode,command,joinID,transCD,sendDomain,cmID,cmPassKey);	
		} else if (command.equals("DeliveryInsert") || command.equals("ReturnPickUpInsert")) {
			-- 배송정보등록, 반품수거등록(반품 완료 처리)
			resultMessage = jsonDAO.CM_Api_SendData(dbmode,command,joinID,transCD,sendDomain,cmID,cmPassKey);
		} else if (command.equals("ItemStock")) {
			-- 상품 재고연동
			resultMessage = jsonDAO.CM_Api_ItemStock(dbmode,command,joinID,transCD,sendDomain,cmID,cmPassKey);		
		} else {
			resultMessage = "MODE FAIL!!";
		}
		커넥트미(CM) API 연동 작업 부분 주석 처리.. 2014-03-06 */
		
		resultMessage = "MODE FAIL!!";
	}
	
%>
<%=resultMessage%>