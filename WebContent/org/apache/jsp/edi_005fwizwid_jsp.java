package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.http.HttpServletRequest;
import com.service.CubeService;
import com.service.command.util.*;
import java.util.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;

public final class edi_005fwizwid_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n");
      out.write("<title>WIZWID&CUBE API 연동</title>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");

	CubeService cs	= CubeService.getInstance();
	 
	/**
	 [command] 
	  APIService-OrderRetrieve : 발주조회
	  
	  //http://220.117.243.112/CSW/handler/wizwid/kr/APIService-OrderRetrieve?VENDOR_ID=138497&STA_DT=20120222000000&END_DT=20120222235959
	  //http://wms.itlinetest.com:8080/edi_wizwid.jsp?dbmode=iseccube&command=OrderRetrieve&inuser=sunyilv --&vendor_id=138497&sta_dt=20120222000000&end_dt=20120222235959
	  //http://127.0.0.1:8888/edi_wizwid.jsp?dbmode=iseccube&command=OrderRetrieve&inuser=SY
			  
	  APIService-OrderConfirm : 발주확인
	  
	  //http://220.117.243.112/CSW/handler/wizwid/kr/APIService-OrderConfirm?VENDOR_ID=138497&SHIP_ID=045426149
	  //http://wms.itlinetest.com:8080/edi_wizwid.jsp?dbmode=iseccube&command=OrderConfirm&inuser=sunyilv -- &vendor_id=138497&&ship_id=045426149
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
	//String Connip = "220.117.243.112";//test  위즈위드
	//String Connip = "api.wizwid.com";위즈위드
	String Connip = ""; //LIVE위즈위드 
	String wizwid_connip = "http://220.117.243.71"; //LIVE
	String wizwid_connip2 = "http://220.117.243.72"; //LIVE
	int statusCode = 0;

	try {
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(wizwid_connip);
		
		statusCode = client.executeMethod(get);
		
		
		if(statusCode == 200){
			Connip = "220.117.243.71";
		}else{
			Connip = "220.117.243.72";
		}
	 }catch(NullPointerException e){
		 Connip = "220.117.243.72";
	 }catch(Exception ex){
		 Connip = "220.117.243.72";
	 }
	 
	
	if(dbmode.equals("") || dbmode == null){
		//cs.setRecvLog(dbmode, "N/C","N/C", vendor_id,
		//		sta_dt, end_dt, "500", "DB명이 올바르지않습니다.");
	}else{
		if(command.equals("OrderRetrieve")){
			// 발주조회
			cs.getOrderRecvData(dbmode, inuser, command,Connip);  
		}else if(command.equals("OrderConfirm")){ 
			// 발주확인
			cs.getOrderSendData(dbmode, inuser, command,Connip);
		}else if(command.equals("DeliveryInsert")){
			// 배송정보등록
			cs.getOrderSendData(dbmode, inuser, command,Connip); 
		}else if(command.equals("SoldOutCancel")){
			//제휴사출고지시취소처리
			cs.getOrderSendData(dbmode, inuser, command,Connip);
		}else if(command.equals("OrderCancelRetrieve")){
			//취소정보조회
			cs.getOrderRecvData(dbmode, inuser, command,Connip);
		}else if(command.equals("OrderReturnRetrieve")){
			//반품정보조회
			cs.getOrderRecvData(dbmode, inuser, command,Connip);
		}else if(command.equals("OrderReturnConfirm")){
			//반품정보확인
			cs.getOrderSendData(dbmode, inuser, command,Connip);
		}else if(command.equals("ReturnPickUpInsert")){
			//반품수거등록
			cs.getOrderSendData(dbmode, inuser, command,Connip);
		}else if(command.equals("OrderReturnCancelRetrieve")){
			//반품취소정보조회 
			cs.getOrderRecvData(dbmode, inuser, command,Connip);
		}else if(command.equals("ReturnRefuse")){
			//반품 취소 처리
			cs.getOrderSendData(dbmode, inuser, command,Connip);
		}else{  
			cs.setRecvLog(dbmode, inuser, command,command, vendor_id,
					sta_dt, end_dt, "500", "It's wrong cammand!");
		}
	}
	

      out.write("\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
