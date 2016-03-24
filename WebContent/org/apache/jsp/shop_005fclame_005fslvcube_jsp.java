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

public final class shop_005fclame_005fslvcube_jsp extends org.apache.jasper.runtime.HttpJspBase
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
dbmode = "slvcube";
int call_seq =0;
try {
	conn = DataBaseManager.getConnection(dbmode);
	spList = dao.getShopRecvClameList(conn);
	
	if(spList.size() > 0){ 
		for(int i = 0 ; i<spList.size();i++){
			map =  (HashMap<String, String>) spList.get(i);
			call_seq = i+1;

      out.write("\r\n");
      out.write("\t<MessageHeader>\r\n");
      out.write("\t\t<send_id>");
      out.print(call_seq );
      out.write("</send_id>\r\n");
      out.write("\t\t<send_date>");
      out.print(CommonUtil.getCurrentDate() );
      out.write("</send_date>\r\n");
      out.write("\t\t<customer_id>");
      out.print(StringUtil.checkNull((String)map.get("cd1")) );
      out.write("</customer_id>\r\n");
      out.write("\t</MessageHeader>\r\n");
      out.write("\t<ClameInfo>\r\n");
      out.write("\t\t\t<shoplinker_id>");
      out.print(StringUtil.checkNull(map.get("cd2")) );
      out.write("</shoplinker_id>\r\n");
      out.write("\t\t\t<st_date>");
      out.print(StringUtil.substring(StringUtil.checkNull((String)map.get("sta_dt")),0,8) );
      out.write("</st_date>\r\n");
      out.write("\t\t\t<ed_date>");
      out.print(StringUtil.substring(StringUtil.checkNull((String)map.get("end_dt")),0,8) );
      out.write("</ed_date>\r\n");
      out.write("\t</ClameInfo>\r\n");
      out.write("\r\n");

		}
	}
	
	spList = null;
	dbmode = null;

	DataBaseManager.close(conn,dbmode);
	if(conn != null) conn.close();
}catch (Exception e) {
	//dao.setErrorRecvLog(dbmode, CommonUtil.getCurrentDate(), call_seq, e.toString());
} finally {
	DataBaseManager.close(conn, dbmode);
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
