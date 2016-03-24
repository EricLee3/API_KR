package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.sql.Connection;
import java.sql.SQLException;
import com.service.command.connection.DataBaseManager;
import com.service.dao.ServiceDAO;
import com.service.CubeService;
import com.service.command.util.*;
import com.service.command.log.Logger;
import java.util.*;

public final class shop_005fdelivery_005fiseccube_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html; charset=euc-kr");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<?xml version=\"1.0\" encoding=\"euc-kr\" ?>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");

	Connection conn  = null;
	Map<String, String> map = null;
	List spList = null;
	String dbmode = "";

      out.write("\r\n");
      out.write("<Shoplinker>\r\n");

CubeService svc = CubeService.getInstance();
ServiceDAO dao = ServiceDAO.getInstance();
dbmode = "iseccube";
try {
	conn = DataBaseManager.getConnection(dbmode);
	spList = dao.getShopSendDeliveryList(conn);
	
	if(spList.size() > 0){ 
		for(int i = 0 ; i<spList.size();i++){
			map =  (HashMap<String, String>) spList.get(i);

      out.write("\r\n");
      out.write("\t<MessageHeader> \r\n");
      out.write("\t\t<send_id>");
      out.print(i+1 );
      out.write("</send_id>\r\n");
      out.write("\t\t<send_date>");
      out.print(CommonUtil.getCurrentDate() );
      out.write("</send_date>\r\n");
      out.write("\t\t<customer_id>");
      out.print(StringUtil.checkNull(map.get("cd1")) );
      out.write("</customer_id>\r\n");
      out.write("\t</MessageHeader>\r\n");
      out.write("\t<OrderInfo>\r\n");
      out.write("\t\t<Delivery>\r\n");
      out.write("\t\t\t<order_id>");
      out.print(StringUtil.checkNull(map.get("tempno")) );
      out.write("</order_id>\r\n");
      out.write("\t\t\t<delivery_name>");
      out.print(StringUtil.checkNull(map.get("refnm")) );
      out.write("</delivery_name>\r\n");
      out.write("\t\t\t<delivery_invoice>");
      out.print((map.get("expnm") != null)?StringUtil.checkNull( map.get("expnm")):"" );
      out.write("</delivery_invoice>\r\n");
      out.write("\t\t</Delivery>\r\n");
      out.write("\t</OrderInfo>\r\n");

		}
	}else {
		//svc.setSendLog(dbmode, "SYSTEM", "ShopSendDelivery", "송장전송", "N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "100", "SHOPLINKER 연동할 대상 정보가 없습니다.","00");
	}
	
	spList = null;
	dbmode = null;

	DataBaseManager.close(conn,dbmode);
	if(conn != null) conn.close();
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

      out.write("\r\n");
      out.write("</Shoplinker>");
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
