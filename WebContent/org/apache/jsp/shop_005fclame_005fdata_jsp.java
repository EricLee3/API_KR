package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class shop_005fclame_005fdata_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<?xml version=\"1.0\" encoding=\"EUC-KR\"?>\r\n");
      out.write("<ClameInfo>\r\n");
      out.write("\t<Clame>\r\n");
      out.write("\t<shoplinker_order_id>12345</shoplinker_order_id>\r\n");
      out.write("\t<mall_order_id>67891</mall_order_id>\r\n");
      out.write("\t<mall_name>cjmall</mall_name>\r\n");
      out.write("\t<order_product_id>452154235</order_product_id>\r\n");
      out.write("\t<shoplinker_product_id>1215411245</shoplinker_product_id>\r\n");
      out.write("\t<product_name>Å×½ºÆ®</product_name>\r\n");
      out.write("\t<quantity>5</quantity>\r\n");
      out.write("\t<order_price>40000</order_price>\r\n");
      out.write("\t<sku>red-M</sku>\r\n");
      out.write("\t<clame_status>100</clame_status>\r\n");
      out.write("\t<clame_memo>¿¡·¯¿¡·¯</clame_memo>\r\n");
      out.write("\t<clame_date>20120824</clame_date>\r\n");
      out.write("\t</Clame>\r\n");
      out.write("</ClameInfo>\r\n");
      out.write("\r\n");
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
