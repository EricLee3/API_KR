package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.http.HttpServletRequest;
import com.service.CubeService;
import com.service.command.util.*;
import java.util.*;

public final class edi_005fshoplinker_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\r\n");
      out.write("<title>ShopLinker&CUBE API 연동</title>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");

	CubeService cs	= CubeService.getInstance();
	 
	String command = StringUtil.nullTo(request.getParameter("command"),"N/C");
	String dbmode = StringUtil.nullTo(request.getParameter("dbmode"),""); 
	String inuser = StringUtil.nullTo(request.getParameter("inuser"),"SYSTEM");
	
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
			cs.setRecvLog(dbmode, inuser, command,command, "","", "", "500", "It's wrong cammand!");
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
