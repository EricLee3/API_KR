<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="com.service.ScApiCreateREDIS" %>
<%@ page import="com.service.command.util.*" %>
<%@ page import="java.util.*" %>
<html>
<head>
<script language="javascript">
	function PopLocation(Type)
	{

		
		/*******테스트  시작 **********/
 		if (Type == "1")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=SendProductData", "", "");
 		}
 		if (Type == "2")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=RecvProductData", "", "");
 		}
 		if (Type == "3")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=SendItemStock", "", "");
 		}
 		if (Type == "4")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=RecvItemStock", "", "");
 		}
 		if (Type == "5")
 		{
 			//window.open("http://localhost:8080/API_KR/scApiManager.jsp?dbmode=iseccube&command=OrderProcess", "", "");
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=OrderProcess", "", "");
 		}
 		if (Type == "6")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=OrderReturnProcess", "", "");
 		}
 		if (Type == "7")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=DeliveryInsert", "", "");
 		}
 		if (Type == "8")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=SendProductDataRedMarker", "", "");
 		}
 		if (Type == "9")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=SendItemStockRedMarker", "", "");
 		}
 		if (Type == "15")
 		{
 			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=ReturnPickUpInsert", "", "");
 		}

		/*20150828  축가  => 20150904 주문 확정 정보 송신시 매장 거부 정보가 먼저 송신 되어야 함 해당 메 뉴 삭제*/
		/* if (Type == "16")
 		{
			//로컬;
 			window.open("http://localhost:8080/API_KR/scApiManager.jsp?dbmode=iseccube&command=StoreCancel", "", "");
 			//운영
 			//window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=iseccube&command=StoreCancel", "", "");
 			//개발
 			//window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=iseccube&command=StoreCancel", "", "");
 		} */

		/*******테스트 끝**********/
	

		/* if (Type == "dis1")
		{
			window.open("http://api.itlinetest.com:8080/scApiManager.jsp?dbmode=disysmrm&command=SendProductData", "", "");
		}
		if (Type == "dis2")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=RecvProductData", "", "");
		}
		if (Type == "dis3")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=SendItemStock", "", "");
		}
		if (Type == "dis4")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=RecvItemStock", "", "");
		}
		if (Type == "dis5")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=OrderProcess", "", "");
		}
		if (Type == "dis6")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=OrderReturnProcess", "", "");
		}
		if (Type == "dis7")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=DeliveryInsert", "", "");
		}
 		if (Type == "dis8")
 		{
 			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=SendProductDataRedMarker", "", "");
 		}
 		if (Type == "dis9")
 		{
 			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=SendItemStockRedMarker", "", "");
 		}
 		if (Type == "dis15")
 		{
 			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=disysmrm&command=ReturnPickUpInsert", "", "");
 		} */
		
		
		// REDCUBE 
		/* if (Type == "red1")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=SendProductDataRedMarker", "", "");
		}
		if (Type == "red2")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=SendItemStockRedMarker", "", "");
		}
		if (Type == "red3")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=RecvProductData", "", "");
		}		
		if (Type == "red4")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=RecvItemStock", "", "");
		}
		if (Type == "red5")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=OrderProcess", "", "");
		}
		if (Type == "red6")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=OrderReturnProcess", "", "");
		}
		if (Type == "red7")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=DeliveryInsert", "", "");
		}
		if (Type == "red8")
 		{
 			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=redcube&command=ReturnPickUpInsert", "", "");
 		}
		 */
		
		//rabcube
	/* 	if (Type == "RA1")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=SendProductDataRedMarker", "", "");
		}
		if (Type == "RA2")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=SendItemStockRedMarker", "", "");
		}
		if (Type == "RA3")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=RecvProductData", "", "");
		}		
		if (Type == "RA4")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=RecvItemStock", "", "");
		}
		if (Type == "RA5")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=OrderProcess", "", "");
		}
		if (Type == "RA6")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=OrderReturnProcess", "", "");
		}
		if (Type == "RA7")
		{
			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=DeliveryInsert", "", "");
		}
		if (Type == "RA8")
 		{
 			window.open("http://api.isehq.com:8080/scApiManager.jsp?dbmode=rabcube&command=ReturnPickUpInsert", "", "");
 		} 
 */
	}
</script>
</head>
</html>
<body>

<div>테스트서버</div>
<input type="button" value="상품연동송신" onclick="PopLocation('1')" style="cursor:pointer"> <BR>
<input type="button" value="상품연동결과수신" onclick="PopLocation('2')" style="cursor:pointer"><BR>
<input type="button" value="재고연동" onclick="PopLocation('3')" style="cursor:pointer"><BR>
<input type="button" value="재고연동결과수신" onclick="PopLocation('4')" style="cursor:pointer"><BR>
<input type="button" value="주문/주문취소" onclick="PopLocation('5')" style="cursor:pointer"><BR>
<input type="button" value="반품/반품취소" onclick="PopLocation('6')" style="cursor:pointer"><BR>
<input type="button" value="출고확정" onclick="PopLocation('7')" style="cursor:pointer"><BR>
<input type="button" value="반품확정" onclick="PopLocation('15')" style="cursor:pointer"><BR>
<!-- <input type="button" value="매장직출거부(RABCUBE)" onclick="PopLocation('16')" style="cursor:pointer"><BR> -->


<!--
<div><H1>REAL서버 : 임의로 테스트 금지</H1></div> 
<div>disysmrm</div>
<input type="button" value="상품연동송신" onclick="PopLocation('dis1')" style="cursor:pointer"> <BR>
<input type="button" value="상품연동결과수신" onclick="PopLocation('dis2')" style="cursor:pointer"><BR>
<input type="button" value="재고연동" onclick="PopLocation('dis3')" style="cursor:pointer"><BR>
<input type="button" value="재고연동결과수신" onclick="PopLocation('dis4')" style="cursor:pointer"><BR>
<input type="button" value="주문/주문취소" onclick="PopLocation('dis5')" style="cursor:pointer"><BR>
<input type="button" value="반품/반품취소" onclick="PopLocation('dis6')" style="cursor:pointer"><BR>
<input type="button" value="출고확정" onclick="PopLocation('dis7')" style="cursor:pointer"><BR>
<input type="button" value="반품확정" onclick="PopLocation('dis15')" style="cursor:pointer"><BR>
<div>RedCube</div>
<input type="button" value="상품연동송신(REDCUBE)" onclick="PopLocation('red1')" style="cursor:pointer"><BR>
<input type="button" value="재고연동(REDCUBE)" onclick="PopLocation('red2')" style="cursor:pointer"><BR>
<input type="button" value="상품연동결과수신(REDCUBE)" onclick="PopLocation('red3')" style="cursor:pointer"><BR>
<input type="button" value="재고연동결과수신(REDCUBE)" onclick="PopLocation('red4')" style="cursor:pointer"><BR>
<input type="button" value="주문/주문취소(REDCUBE)" onclick="PopLocation('red5')" style="cursor:pointer"><BR>
<input type="button" value="반품/반품취소(REDCUBE)" onclick="PopLocation('red6')" style="cursor:pointer"><BR>
<input type="button" value="출고확정(REDCUBE)" onclick="PopLocation('red7')" style="cursor:pointer"><BR>
<input type="button" value="반품확정(REDCUBE)" onclick="PopLocation('red8')" style="cursor:pointer"><BR>
<div>RABCube</div>
<input type="button" value="상품연동송신(RABCUBE)" onclick="PopLocation('RA1')" style="cursor:pointer"><BR>
<input type="button" value="재고연동(RABCUBE)" onclick="PopLocation('RA2')" style="cursor:pointer"><BR>
<input type="button" value="상품연동결과수신(RABCUBE)" onclick="PopLocation('RA3')" style="cursor:pointer"><BR>
<input type="button" value="재고연동결과수신(RABCUBE)" onclick="PopLocation('RA4')" style="cursor:pointer"><BR>
<input type="button" value="주문/주문취소(RABCUBE)" onclick="PopLocation('RA5')" style="cursor:pointer"><BR>
<input type="button" value="반품/반품취소(RABCUBE)" onclick="PopLocation('RA6')" style="cursor:pointer"><BR>
<input type="button" value="출고확정(RABCUBE)" onclick="PopLocation('RA7')" style="cursor:pointer"><BR>
<input type="button" value="반품확정(RABCUBE)" onclick="PopLocation('RA8')" style="cursor:pointer"><BR>
<br>
 -->
</br>
</body>