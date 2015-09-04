<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="com.service.CubeService" %>
<%@ page import="com.service.command.util.*" %>
<%@ page import="java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>WIZWID & CUBE API 연동</title>
</head>
<body>
<%
	CubeService cs	= CubeService.getInstance();
	 
	/**
	[command] 
	APIService-OrderRetrieve : 발주조회
	  
	//http://220.117.243.112/CSW/handler/wizwid/kr/APIService-OrderRetrieve?VENDOR_ID=138497&STA_DT=20120222000000&END_DT=20120222235959
	//http://api.isehq.com:8080/edi_wizwid.jsp?dbmode=iseccube&command=OrderRetrieve&inuser=sunyilv --&vendor_id=138497&sta_dt=20120222000000&end_dt=20120222235959
	//http://127.0.0.1:8888/edi_wizwid.jsp?dbmode=iseccube&command=OrderRetrieve&inuser=SY
			  
	APIService-OrderConfirm : 발주확인
	  
	//http://220.117.243.112/CSW/handler/wizwid/kr/APIService-OrderConfirm?VENDOR_ID=138497&SHIP_ID=045426149
	//http://api.isehq.com:8080/edi_wizwid.jsp?dbmode=iseccube&command=OrderConfirm&inuser=sunyilv -- &vendor_id=138497&&ship_id=045426149
	//http://127.0.0.1:8888/edi_wizwid.jsp?dbmode=iseccube&command=OrderConfirm&inuser=SY
	//http://wms.isecommerce.co.kr/edi_wizwid.jsp?dbmode=slvcube&command=OrderRetrieve&inuser=sunyilv
	*/
	
	String command = StringUtil.nullTo(request.getParameter("command"),"N/C");
	String vendor_id= StringUtil.nullTo(request.getParameter("vendor_id"),"");
	String sta_dt = StringUtil.nullTo(request.getParameter("sta_dt"),"");
	String end_dt = StringUtil.nullTo(request.getParameter("end_dt"),"");
	String ship_id = StringUtil.nullTo(request.getParameter("ship_id"),"");
	String dbmode = StringUtil.nullTo(request.getParameter("dbmode"),"");
	String inuser = StringUtil.nullTo(request.getParameter("inuser"),"SYSTEM");
	
	String transCD = "10";	//큐브 구분코드 (10:wizwid, 20:wck, 30:mangoKR)
	
	String Connip = "220.117.243.112";//test
	//String Connip = "220.117.243.71"; //LIVE
	//String Connip = "api.wizwid.com";
	
	
	
	if(dbmode.equals("") || dbmode == null){
		//cs.setRecvLog(dbmode, "N/C","N/C", vendor_id,
		//		sta_dt, end_dt, "500", "DB명이 올바르지않습니다.");
	}else{
		if(command.equals("OrderRetrieve")){
			// 발주조회
			cs.getOrderRecvData(dbmode, inuser, command,Connip, transCD);  
		}else if(command.equals("OrderConfirm")){ 
			// 발주확인
			cs.getOrderSendData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("DeliveryInsert")){
			// 배송정보등록
			cs.getOrderSendData(dbmode, inuser, command,Connip, transCD); 
		}else if(command.equals("SoldOutCancel")){
			//제휴사출고지시취소처리
			cs.getOrderSendData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("OrderCancelRetrieve")){
			//취소정보조회
			cs.getOrderRecvData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("OrderReturnRetrieve")){
			//반품정보조회
			cs.getOrderRecvData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("OrderReturnConfirm")){
			//반품정보확인
			cs.getOrderSendData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("ReturnPickUpInsert")){
			//반품수거등록
			cs.getOrderSendData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("OrderReturnCancelRetrieve")){
			//반품취소정보조회 
			cs.getOrderRecvData(dbmode, inuser, command,Connip, transCD);
		}else if(command.equals("ReturnRefuse")){
			//반품 취소 처리
			cs.getOrderSendData(dbmode, inuser, command,Connip, transCD);
		}else{  
			cs.setRecvLog(dbmode, inuser, command,command, vendor_id,
					sta_dt, end_dt, "500", "It's wrong cammand!", transCD);
		}
	}
	
%>
</body>
</html>