package com.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import net.sf.json.JSONObject;

public class PantosRecvServlet extends JSONRecvServlet{
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
		try {
			String url = "http://192.168.10.154:9080/API/pantos";
			super.doPost(request, response);
			
			System.out.println(jObj.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {
		doPost(request, response);
	}
	
	public void sendJSON(String url, JSONObject jObj) 
		throws IOException {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		StringEntity params = null;
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
		
		params = new StringEntity(jObj.toString(), HTTP.UTF_8);
		request.addHeader("content-type", "application/x-www-form-urlencoded");
		request.setEntity(params);
		httpClient.execute(request);
	}
}
