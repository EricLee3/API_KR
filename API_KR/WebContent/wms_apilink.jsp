<?xml version="1.0" encoding="utf-8" ?>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@page import="java.io.StringReader"%>
<%@page import="javax.xml.parsers.DocumentBuilder"%>
<%@page import="org.xml.sax.InputSource"%>
<%@page import="java.io.Serializable"%>
<%@page import="org.w3c.dom.Node"%>
<%@page import="org.w3c.dom.NodeList"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import = "javax.servlet.http.HttpServletRequest" %>
<%@page import = "com.service.WmsService" %>
<%@page import = "com.service.command.util.*" %>
<%@page import = "java.net.URLDecoder" %>
<%@page import = "java.util.*" %>
<%@page import="org.apache.commons.httpclient.methods.GetMethod" %>
<%@page import="org.apache.commons.httpclient.methods.PostMethod" %>
<%@page import="org.apache.commons.httpclient.HttpClient" %>
<%@page import="org.apache.commons.httpclient.HttpException" %>
<%
	String result = "";
	boolean hasError = false;
  	String apiDiv = StringUtil.nullTo(request.getParameter("apiDiv"),"");	
	if(request.getParameter("xmlData")==null) {
		result = "FAIL! XMLData is null";
		hasError = true;
	}
	
	if(apiDiv.equals("")) {
		result = "FAIL! 필수 파라미터 누락";
		hasError = true;
	}
	String xmlData = URLDecoder.decode(request.getParameter("xmlData"),"UTF-8");
	
    if(!hasError) {	
		Document doc = null;
		NodeList root = null;
		NodeList nodeList = null;
		Element element = null;
		HashMap<String,String> list = new HashMap<String,String>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(xmlData.toString());
			InputSource is = new InputSource(sr);
			doc = db.parse(is);
			nodeList = doc.getElementsByTagName("order_list").item(0).getChildNodes();
			for(int i=0; i<nodeList.getLength(); i++) {
				element = (Element)nodeList.item(i);
				list.put(element.getTagName(),StringUtil.nullTo(element.getTextContent(),""));
			}	
			
			WmsService ws = WmsService.getInstance();
			String dbMode = "wmsapi";
			String arrValue = "";
			String brandCD = list.get("brand_cd");
			String chkStr1 = "";
			String chkStr2 = "";
			String chkStr3 = "";
			String chkStr4 = "";
			
			if ("fact".equals(apiDiv)) {		//입고/반출
				chkStr1 = list.get("item_cd");
			} else if("delivery".equals(apiDiv)){
				chkStr1 = list.get("item_cd");
			} else if("item".equals(apiDiv)) {
				chkStr1 = list.get("barcode");
			} else {
				chkStr1 = list.get("whcd");
				if("inorder".equals(apiDiv)) {
					chkStr2 = list.get("ireqdt");
					chkStr3 = list.get("ireqno");							
				} else if("outorder".equals(apiDiv)) {
					chkStr2 = list.get("redt");
					chkStr3 = list.get("brandno");
				} else if("etcorder".equals(apiDiv)) {
					chkStr2 = list.get("ireqdt");
					chkStr3 = list.get("ireqno");
				} else {}
// 				chkStr2 = chkStr2.substring(1,4) +"-"+ chkStr2.substring(5,2) +"-"+ chkStr2.substring(7,2);
			}
			
			result = ws.getCheckingResult(dbMode, apiDiv, brandCD, chkStr1, chkStr2, chkStr3, chkStr4);
			if (list.size() > 0 && "".equals(result)) {
				for(int i=0; i<nodeList.getLength(); i++) {
					element = (Element)nodeList.item(i);
					arrValue = arrValue + list.get(element.getTagName());					
					if (i < nodeList.getLength()-1) {
						arrValue = arrValue +"|";	
					}
				}				
				result = ws.getApiRecvData(dbMode, apiDiv, arrValue);
			}
		} catch(Exception e) {
			e.printStackTrace();
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