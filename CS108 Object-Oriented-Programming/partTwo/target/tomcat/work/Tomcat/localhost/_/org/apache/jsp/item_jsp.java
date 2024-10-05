/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.47
 * Generated at: 2023-06-14 21:32:49 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.List;
import Objects.*;

public final class item_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


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

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");

    // Change this to your path
    String path = "store-images/";

    List<Product> data = (List<Product>) request.getServletContext().getAttribute("ProductList");
    Product product = null;
    String id = request.getParameter("id");

    for (Product p : data) {
        if (p.getProductId().equals(id)) {
            product = p;
            break;
        }
    }

    String imgPath = path + product.getImageFile();

      out.write("\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("    <meta charset=\"UTF-8\">\n");
      out.write("    <title>");
      out.print( product.getName() );
      out.write("</title>\n");
      out.write("    <style>\n");
      out.write("        h1 {\n");
      out.write("            font-size: 24px;\n");
      out.write("            color: #333;\n");
      out.write("        }\n");
      out.write("        img {\n");
      out.write("            max-width: 300px;\n");
      out.write("            height: auto;\n");
      out.write("        }\n");
      out.write("        p {\n");
      out.write("            font-size: 18px;\n");
      out.write("        }\n");
      out.write("        .price {\n");
      out.write("            font-weight: bold;\n");
      out.write("            color: #f00;\n");
      out.write("        }\n");
      out.write("        .add-to-cart {\n");
      out.write("            background-color: #007bff;\n");
      out.write("            color: #fff;\n");
      out.write("            padding: 10px 20px;\n");
      out.write("            border: none;\n");
      out.write("            cursor: pointer;\n");
      out.write("            font-size: 16px;\n");
      out.write("            border-radius: 4px;\n");
      out.write("            transition: background-color 0.3s ease;\n");
      out.write("        }\n");
      out.write("        .add-to-cart:hover {\n");
      out.write("            background-color: #0056b3;\n");
      out.write("        }\n");
      out.write("    </style>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("<form action=\"./CartManager\" method=\"post\">\n");
      out.write("    <input name=\"id\" type=\"hidden\" value=\"");
      out.print( product.getProductId() );
      out.write("\">\n");
      out.write("    <h1>");
      out.print( product.getName() );
      out.write("</h1>\n");
      out.write("    <img src=\"");
      out.print( imgPath );
      out.write("\" alt=\"Product Image\">\n");
      out.write("    <p class=\"price\">$");
      out.print( product.getPrice() );
      out.write("</p>\n");
      out.write("    <input type=\"submit\" class=\"add-to-cart\" value=\"Add to Cart\">\n");
      out.write("</form>\n");
      out.write("</body>\n");
      out.write("</html>\n");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
