package com.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.TimerTask;

import com.service.command.log.Logger;
import com.service.command.util.PropManager;

public class TaskPantosToken extends TimerTask {
	public void run() {
		Logger.debug("*************************** PANTOS TOKEN INIT START ***************************");
		System.out.println("*************************** PANTOS TOKEN INIT START ***************************");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		PropManager propMng = PropManager.getInstances();
		Properties propPath = propMng.getProp("pantosSetup");
		String path = propPath.getProperty("pantos.path");
		
		propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("pantosToken");
		
		OutputStream os = null;
		
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT DISTINCT FTP_USERID FROM CMEXPRESSCONST WHERE EXPRESS_CD = '82'");
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
//			conn= DriverManager.getConnection("jdbc:oracle:thin:@220.117.243.55:1521:WMS","WMS_USER", "WMS_USER");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@220.117.243.54:1522:WMS","WMS_USER","WMS_PWD");
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			os = new FileOutputStream(new File(path+"pantosToken.properties"));
			while(rs.next()) {
				String ifCd = rs.getString("FTP_USERID");
				String preToken = prop.getProperty("pantos."+ifCd+".current_token");
				if(preToken == null) {
					preToken = "";
				}
				System.out.println(ifCd);
				prop.setProperty("pantos."+ifCd+".current_token", "");
				prop.setProperty("pantos."+ifCd+".previous_token", preToken);
			}
			prop.store(os, "");
			os.flush();
			
		} catch(Exception e) {
			e.printStackTrace();
			Logger.error(e);
		} finally {
			try {
				os.close();
				if(rs!=null) {
					rs.close();
				}
				if(pstmt!=null) {
					pstmt.close();
				}
				if(conn!=null) {
					conn.close();
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}
		System.out.println("*************************** PANTOS TOKEN INIT END ***************************");
		Logger.debug("*************************** PANTOS TOKEN INIT END ***************************");
	}
}
