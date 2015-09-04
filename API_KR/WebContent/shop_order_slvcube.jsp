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
	int call_seq = 0;
	
	
	try {
		conn = DataBaseManager.getConnection(dbmode);
		spList = dao.getShopRecvOrderList(conn);
		
		xml_out.append("<Shoplinker>");
		
		if(spList != null && spList.size() > 0){ 
			for(int i = 0 ; i<spList.size();i++){
				map =  (HashMap<String, String>) spList.get(i);
				call_seq = i+1;
				xml_out.append("<messageHeader>");
				xml_out.append("<sendID>"+call_seq+"</sendID>");
				xml_out.append("<senddate>"+CommonUtil.getCurrentDate() +"</senddate>");
				xml_out.append("</messageHeader>");
				xml_out.append("<OrderInfo>");
				xml_out.append("<Order>");
				xml_out.append("<customer_id>"+StringUtil.checkNull(map.get("cd1")) +"</customer_id>");
				xml_out.append("<shoplinker_id><![CDATA["+StringUtil.checkNull(map.get("cd2")) +"]]></shoplinker_id>");
				xml_out.append("<st_date>"+StringUtil.substring(StringUtil.checkNull(map.get("sta_dt")),0,8)+"</st_date>");
				xml_out.append("<ed_date>"+StringUtil.substring(StringUtil.checkNull(map.get("end_dt")),0,8)+"</ed_date>");
				xml_out.append("<mall_order_code>"+((map.get("cd3") != null)? StringUtil.checkNull(map.get("cd3")):"")+"</mall_order_code>");
				xml_out.append("<mall_user_id><![CDATA["+((map.get("cd4") != null)? StringUtil.checkNull(map.get("cd4")):"" )+"]]></mall_user_id>");
				xml_out.append("</Order>");
				xml_out.append("</OrderInfo>");
		}
	}
	
	xml_out.append("</Shoplinker>");
	Logger.debug(xml_out.toString());
}catch (Exception e) {
	//svc.setErrorRecvLog(dbmode, CommonUtil.getCurrentDate(), String.valueOf(call_seq), e.toString());
} finally {
	DataBaseManager.close(conn, dbmode);
	if(conn != null) conn.close();
}

%>
<%=xml_out.toString()%>