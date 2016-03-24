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
	
	Map<String, String> map = null;
	List spList = null;
	String dbMode = "wmsapi";
	StringBuffer xml_out = new StringBuffer();
	
	
	String result = "";
	String api_div = StringUtil.nullTo(request.getParameter("api_div"),"");			//전송구분
	String brand_cd = StringUtil.nullTo(request.getParameter("brand_cd"),"");		//브랜드코드
	String whcd = StringUtil.nullTo(request.getParameter("whcd"),"");				//물류센터코드
	String ord_date_s = StringUtil.nullTo(request.getParameter("ord_date_s"),"");	//예정일자 조회 시작일
	String ord_date_e = StringUtil.nullTo(request.getParameter("ord_date_e"),"");	//예정일자 조회 끝일
	String brand_no = StringUtil.nullTo(request.getParameter("brand_no"),"");		//전표번호
	
	String inoutbound_state = "";
	
	if (api_div.equals("") || brand_cd.equals("") || whcd.equals("") || ord_date_s.equals("") || ord_date_e.equals("")) {
		result = "FAIL! 필수 파라미터 누락";
	} else {
		
		try {
			
			spList = ws.getConfirmData(dbMode, api_div, brand_cd, whcd, ord_date_s, ord_date_e, brand_no);

			if (spList != null && spList.size() > 0) { 
				for (int i = 0 ; i<spList.size();i++) {
					map =  (HashMap<String, String>) spList.get(i);
					if (api_div.equals("inorder") || api_div.equals("outorder")) {
						if (api_div.equals("inorder")) {
							if (map.get("INOUTBOUND_STATE").equals("10")) {
								inoutbound_state = "입고예정";
							} else if (map.get("INOUTBOUND_STATE").equals("20")) {
								inoutbound_state = "입고등록";
							} else if (map.get("INOUTBOUND_STATE").equals("30")) {
								inoutbound_state = "입고지시";
							} else if (map.get("INOUTBOUND_STATE").equals("40")) {
								inoutbound_state = "입고검수";
							} else if (map.get("INOUTBOUND_STATE").equals("50")) {
								inoutbound_state = "입고적치";
							} else if (map.get("INOUTBOUND_STATE").equals("60")) {
								inoutbound_state = "입고확정";
							}
							
						} else if (api_div.equals("outorder")) {
							if (map.get("INOUTBOUND_STATE").equals("10")) {
								inoutbound_state = "출고예정";
							} else if (map.get("INOUTBOUND_STATE").equals("20")) {
								inoutbound_state = "출고등록";
							} else if (map.get("INOUTBOUND_STATE").equals("21")) {
								inoutbound_state = "출고보류";
							} else if (map.get("INOUTBOUND_STATE").equals("30")) {
								inoutbound_state = "배차생성";
							} else if (map.get("INOUTBOUND_STATE").equals("40")) {
								inoutbound_state = "출고지시";
							} else if (map.get("INOUTBOUND_STATE").equals("50")) {
								inoutbound_state = "출고확정";
							} else if (map.get("INOUTBOUND_STATE").equals("60")) {
								inoutbound_state = "배송완료";
							}
						}
					} 
					
					
					xml_out.append("<ResultMessage>");
					xml_out.append("	<center_cd>"+map.get("CENTER_CD")+"</center_cd>");
					xml_out.append("	<brand_cd>"+map.get("BRAND_CD")+"</brand_cd>");
					xml_out.append("	<order_date>"+map.get("ORDER_DATE")+"</order_date>");
					xml_out.append("	<brand_no>"+StringUtil.nullTo(map.get("BRAND_NO"),"")+"</brand_no>");
					xml_out.append("	<inoutbound_date>"+StringUtil.nullTo(map.get("INOUTBOUND_DATE"),"")+"</inoutbound_date>");
					xml_out.append("	<inoutbound_code>"+StringUtil.nullTo(map.get("INOUTBOUND_STATE"),"")+"</inoutbound_code>");
					xml_out.append("	<inoutbound_state>"+inoutbound_state+"</inoutbound_state>");
					xml_out.append("	<confirm_yn>"+StringUtil.nullTo(map.get("CONFIRM_YN"),"")+"</confirm_yn>");
					xml_out.append("	<confirm_date>"+StringUtil.nullTo(map.get("CONFIRM_DATETIME"),"")+"</confirm_date>");
					xml_out.append("</ResultMessage>");
					
				}
			} else {
				result = "FAIL! 대상 정보 없음";
			}
		
			//Logger.debug(xml_out.toString());
			
			
		} catch (Exception e) {
			result = "###Error###:"+e.toString();
			ws.setErrorRecvLog(dbMode, api_div, "confirm", brand_cd, e.toString());
		} finally {
			
		}
	}

%>
<WmsApiLinker>
	<% if (result.equals("")) { %>
	<%=xml_out.toString()%>
	<ResultCode>01</ResultCode>
	<% } else { %>
	<ResultMessage><%=result%></ResultMessage>
	<ResultCode>99</ResultCode>
	<% } %>
</WmsApiLinker>
