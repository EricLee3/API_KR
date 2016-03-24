<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "javax.servlet.http.HttpServletRequest" %>
<%@ page import = "com.service.CubeService" %>
<%@ page import = "com.service.command.util.*" %>
<%@ page import = "java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ShopLinker & CUBE API 연동</title>
</head>
<body>
<%
	CubeService cs	= CubeService.getInstance();
	 
	String command = StringUtil.nullTo(request.getParameter("command"),"N/C");
	String dbmode = StringUtil.nullTo(request.getParameter("dbmode"),""); 
	String inuser = StringUtil.nullTo(request.getParameter("inuser"),"SYSTEM");
	
	String transCD = "";	//큐브 구분코드 (10:wizwid, 20:wck, 30:mangoKR)
	
	 /**
		ShopRecvOrder : 주문수집
		ShopRecvClame : 클레임수집
		ShopSendDelivery : 송장전송
	 */
	 
	if(dbmode.equals("") || dbmode == null){
		out.print(" <script type='text/javascript'>alert('db모드를 정상적으로 입력해주세요.');</script>");
	}else{
		if(command.equals("ShopRecvOrder")){ 
			// 주문수집 
			cs.getShopOrderRecvData(dbmode, inuser, command);  
		}else if(command.equals("ShopSendDelivery")){ 
			// 송장전송  
			cs.getShopOrderSendData(dbmode, inuser, command);
		}else if(command.equals("ShopRecvClame")){
			// 클레임수집
			cs.getShopOrderRecvData(dbmode, inuser, command); 
		}else{  
			cs.setRecvLog(dbmode, inuser, command,command, "","", "", "500", "It's wrong cammand!", transCD);
		}
	}
%>
</body>
</html>