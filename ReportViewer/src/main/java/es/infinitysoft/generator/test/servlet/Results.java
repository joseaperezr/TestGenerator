//To save as "<CATALINA_HOME>\webapps\helloservlet\WEB-INF\src\mypkg\HelloWorldExample.java"
package es.infinitysoft.generator.test.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Results extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// Set the response message's MIME type.
		response.setContentType("text/html;charset=UTF-8");
		// Allocate a output writer to write the response message into the
		// network socket.
		PrintWriter out = response.getWriter();

		// Use a ResourceBundle for localized string in
		// "LocalStrings_xx.properties" for i18n.
		// The request.getLocale() sets the locale based on the
		// "Accept-Language" request header.
		//ResourceBundle rb = ResourceBundle.getBundle("LocalStrings",request.getLocale());
		// To test other locales.
		// ResourceBundle rb = ResourceBundle.getBundle("LocalStrings", new
		// Locale("fr"));

		// Write the response message, in an HTML document.
		try {
			
			
			String uri = request.getRequestURI();
			
			String folderName = request.getParameter("folderName");
			
			out.println("<!DOCTYPE html>"); // HTML 5
			out.println("<html><head>");
			out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
			//String title = rb.getString("helloworld.title");
			out.println("<title>" + "Resultados" + "</title></head>");
			out.println("<body>");
			out.println("<h1>" + "Resultados" + "</h1>"); // Prints "Hello, world!"
			// Set a hyperlink image to refresh this page
			/*out.println("<a href='" + request.getRequestURI()
					+ "'><img src='images/return.gif'></a>");*/
			out.println("<iframe src=\"file:///C:/ouput_soap/test-suite/target/site/GoogleSearchTestResults.html#Test_Cases\" width=\"100%\" style=\"border:0px; height: 100em\">");
			out.println("</iframe></body></html>");
		} finally {
			out.close(); // Always close the output writer
		}
	}
}