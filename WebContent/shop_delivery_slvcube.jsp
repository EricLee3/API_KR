<?xml version="1.0" encoding="euc-kr" ?>
<%@ page language="java" contentType="text/html; charset=euc-kr" pageEncoding="euc-kr"%>
<%@ page import = "java.sql.Connection, java.sql.SQLException" %>
<%@ page import = "com.service.command.connection.DataBaseManager" %>
<%@ page import = "com.service.dao.ServiceDAO,com.service.CubeService" %>
<%@ page import = "com.service.command.util.*, com.service.command.log.Logger" %>
<%@ page import = "java.util.*" %>
<%
	Connection conn  = null;
	Map<String, String> map = null;
	List spList = null;
	String dbmode = "";
	StringBuffer xml_out = new StringBuffer();
	
	CubeService svc = CubeService.getInstance();
	ServiceDAO dao = ServiceDAO.getInstance();
	dbmode = "slvcube";
	
	String[] order = null;
	order = StringUtil.getSplit(StringUtil.nullTo(request.getParameter("order_id"),""),"|");
	String order_id = "";
	String send_no = "";	
	String expnm = "";
	
	if(order != null){ 
		order_id = StringUtil.nullTo(order[0],"");
		send_no = StringUtil.nullTo(order[1],"");
		expnm = StringUtil.nullTo(order[2],"");
	}
	
	try {
		conn = DataBaseManager.getConnection(dbmode);
		spList = dao.getShopSendDeliveryData(conn, order_id, expnm);

		xml_out.append("<Shoplinker>");
		
		
		if(spList != null && spList.size() > 0){ 
			for(int i = 0 ; i<spList.size();i++){
				map =  (HashMap<String, String>) spList.get(i);
				
				xml_out.append("<MessageHeader>");
				xml_out.append("<send_id>"+send_no+"</send_id>");
				xml_out.append("<send_date>"+CommonUtil.getCurrentDate()+"</send_date>");
				xml_out.append("<customer_id>"+StringUtil.checkNull(map.get("cd1"))+"</customer_id>");
				xml_out.append("</MessageHeader>");
				xml_out.append("<OrderInfo>");
				xml_out.append("<Delivery>");
				xml_out.append("<order_id>"+StringUtil.checkNull(map.get("tempno")) +"</order_id>");
				xml_out.append("<delivery_name>"+StringUtil.checkNull(map.get("refnm"))+"</delivery_name>");
				xml_out.append("<delivery_invoice>"+((map.get("expnm") != null)?StringUtil.checkNull( map.get("expnm")):"")+"</delivery_invoice>");
				xml_out.append("</Delivery>");
				xml_out.append("</OrderInfo>");
				
		}
	}else {
		//svc.setSendLog(dbmode, "SYSTEM", "ShopSendDelivery", "송장전송", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "100", "SHOPLINKER 연동할 대상 정보가 없습니다.","00");
	}
	
	xml_out.append("</Shoplinker>");
	Logger.debug(xml_out.toString());
}catch (Exception e) {
	try {
		conn.rollback();
	} catch (SQLException se) {
	}
	Logger.error(e);
	throw e;
} finally {
	DataBaseManager.close(conn,dbmode);
	if(conn != null) conn.close();
}
%>
<%=xml_out.toString()%>