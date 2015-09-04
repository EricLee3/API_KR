package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import com.service.JSONRecvService;

public class JSONRecvServlet extends HttpServlet {
	JSONObject jObj;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
		
		String fork = "";
		if(request.getParameter("fork") != null) {
			fork = request.getParameter("fork"); 
		}
		 
		if(fork.length()>0) {
			JSONRecvService jRecvService = new JSONRecvService();
			jRecvService.recv(fork);
		} else {
			//error
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
		doPost(request, response);
	}
}
