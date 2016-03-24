<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.net.URLEncoder" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>WMS API 연동</title>
</head>
<body>
<form method="post" name="frm" action="wms_apilink.jsp">
<!--  공급처 테스트
<ul>
	<li><input type="text" name="api_div" value="fact"></li>	
	<li><input type="text" name="last_yn" value="Y"></li>
	<li><input type="text" name="brand_cd" value="1501"></li>
	<li><input type="text" name="item_cd" value="00200"></li>
	<li><input type="text" name="vdgu" value="2"></li>
	<li><input type="text" name="vdsnm" value="<%=URLEncoder.encode("아이에스이커머스") %>"></li>
	<li><input type="text" name="vdnm" value="<%=URLEncoder.encode("아이에스이커머스") %>"></li>
	<li><input type="text" name="busno" value=""></li>
	<li><input type="text" name="jmno" value=""></li>
	<li><input type="text" name="boss" value="<%=URLEncoder.encode("황재익") %>"></li>
	<li><input type="text" name="busut" value=""></li>
	<li><input type="text" name="busuj" value=""></li>
	<li><input type="text" name="email" value=""></li>
	<li><input type="text" name="zip1" value="135"></li>
	<li><input type="text" name="zip2" value="090"></li>
	<li><input type="text" name="addr1" value="<%=URLEncoder.encode("서울 강남구 삼성동") %>"></li>
	<li><input type="text" name="addr2" value=""></li>
	<li><input type="text" name="telno" value=""></li>
	<li><input type="text" name="faxno" value=""></li>
	<li><input type="text" name="cd1" value=""></li>
	<li><input type="text" name="cd2" value=""></li>
</ul>
공급처 테스트 -->

<!-- 배송처 테스트 -->
<ul>
	<li><input type="text" name="api_div" value="delivery"></li>	
	<li><input type="text" name="last_yn" value="Y"></li>
	<li><input type="text" name="brand_cd" value="1501"></li>
	<li><input type="text" name="item_cd" value="FRS05"></li>
	<li><input type="text" name="vdnm" value="<%=URLEncoder.encode("샘앤컴퍼니") %>"></li>
	<li><input type="text" name="busno" value="2110696409"></li>
	<li><input type="text" name="boss" value="<%=URLEncoder.encode("김성운") %>"></li>
	<li><input type="text" name="busut" value="<%=URLEncoder.encode("도소매") %>"></li>
	<li><input type="text" name="busuj" value="<%=URLEncoder.encode("의류") %>"></li>
	<li><input type="text" name="jmno" value="Y"></li>
	<li><input type="text" name="zip1" value="135"></li>
	<li><input type="text" name="zip2" value="889"></li>
	<li><input type="text" name="addr1" value="<%=URLEncoder.encode("서울 강남구 신사동") %>"></li>
	<li><input type="text" name="addr2" value="<%=URLEncoder.encode("545-14 조일빌딩 4층") %>"></li>
	<li><input type="text" name="telno" value=""></li>
	<li><input type="text" name="faxno" value=""></li>
	<li><input type="text" name="cd11" value="<%=URLEncoder.encode("조원진") %>"></li>
	<li><input type="text" name="cd12" value="010-5358-6472"></li>
	<li><input type="text" name="email" value=""></li>
	<li><input type="text" name="cd6" value=""></li>
	<li><input type="text" name="cd7" value=""></li>
	<li><input type="text" name="cd8" value=""></li>
	<li><input type="text" name="cd9" value=""></li>
	<li><input type="text" name="cd2" value=""></li>
</ul>
<!-- 배송처 테스트 -->


<input type="button" value="확인" onclick="document.frm.submit();">
</form>
</body>
</html>
