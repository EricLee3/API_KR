package com.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import com.service.PantosConfirmService;
import com.service.PantosInspectService;

public class PantosSendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
		
		request.setCharacterEncoding("UTF-8");
		String fork = request.getParameter("fork");
		String resp = "";
		if(fork.equals("wms")) {
			// 판토스 검품 api 호출 (proc0002, proc0003)
			PantosInspectService ptnInspect = new PantosInspectService();
			resp = ptnInspect.send(request, response);
			
		} else if(fork.equals("wmsConfirm")) {
			PantosConfirmService ptnConfirm = new PantosConfirmService();
			resp = ptnConfirm.send(request, response);
		} else if(fork.equals("ics")) {
			PantosInspectService ptnInspect = new PantosInspectService();
			resp = ptnInspect.send(request, response);
			
			if(!resp.contains("error:")) {
			
				JSONObject jProc0004 = JSONObject.fromObject(resp);
				PantosConfirmService ptnConfirm = new PantosConfirmService();
				resp = ptnConfirm.send(jProc0004);
			}
						
		} else if(fork.equals("icsConfirm")) {
			PantosConfirmService ptnConfirm = new PantosConfirmService();
			resp = ptnConfirm.send(request, response);
		} else {
			
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-www-form-urlencoded");
		response.getWriter().print(resp);
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
		doPost(request, response);
	}
}
