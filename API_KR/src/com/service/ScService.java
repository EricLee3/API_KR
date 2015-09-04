package com.service;

/*
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList;

import com.service.command.connection.DataBaseManager;
import com.service.command.util.CommonUtil;
import com.service.command.util.StringUtil;
import com.service.dao.ServiceDAO;
import com.service.entity.ServiceDataInfo;
import com.service.entity.ServiceLogInfo;
import com.service.entity.ServiceShoplinkerInfo;
import com.service.CubeApiCreateJSON;

import com.service.command.log.Logger;
import com.sun.java_cup.internal.production;
*/


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.StringWriter;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.service.command.connection.DataBaseManager;
import com.service.command.log.Logger;


public class ScService {
	

	private static ScService instance = new ScService();

	public static ScService getInstance() {
		return instance;
	}

	private ScService() {
		
	}
	
	/*
	public void writeXml(Document doc) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		
		DOMSource source = new DOMSource(doc);
		
		transformer.transform(source, sr);
		FileWriter fw = new FileWriter("/export/home/api/xmlForder/ProductData.xml");
		fw.write(sw.toString());
		fw.flush();
		fw.close();
	}
	*/
	
	public String getItemSendData(String dbmode, String command, String transCD, String sendDomain) throws SQLException, Exception {
		String methodName ="com.service.ScService.getItemSendData()";
		Logger.debug(methodName);
		
		Connection 			conn		= null;
		
		PreparedStatement	pstmt		= null;
		
		ResultSet			rs			= null;
		
		StringBuffer   		sqlBuffer  	= new StringBuffer(1000);	//주 쿼리문
		
		String 	sendMessage 	= null;
		
		
		try {
			conn = DataBaseManager.getConnection(dbmode);
			
			
			sqlBuffer.append("SELECT  MAX(A.TRAN_DATE)      AS TRAN_DATE						\n");
			sqlBuffer.append("        , MAX(A.TRAN_SEQ)     AS TRAN_SEQ                     	\n");
			sqlBuffer.append("        , MAX(A.SEQ)          AS SEQ                          	\n");
			sqlBuffer.append("        , MAX(A.STORAGE_ID)   AS STORAGE_ID                   	\n");
			sqlBuffer.append("        , A.PRODINC                                           	\n");
			sqlBuffer.append("        , MAX(A.PNAME)        AS PNAME                        	\n");
			sqlBuffer.append("        , MAX(A.WEIGHT)       AS WEIGHT                       	\n");
			sqlBuffer.append("        , MAX(A.WEIGHT_UNIT)  AS WEIGHT_UNIT                  	\n");
			sqlBuffer.append("        , MAX(A.ASSORT_GB)    AS ASSORT_GB                    	\n");
			sqlBuffer.append("        , MAX(A.NONSALE_YN)   AS NONSALE_YN                   	\n");
			sqlBuffer.append("        , MAX(A.SHORTAGE_YN)  AS SHORTAGE_YN                  	\n");
			sqlBuffer.append("        , MAX(A.RES_UNIT)     AS RES_UNIT                     	\n");
			sqlBuffer.append("        , MAX(A.TAX_GB)       AS TAX_GB                       	\n");
			sqlBuffer.append("        , MAX(A.PURL)         AS PURL                         	\n");
			sqlBuffer.append("        , MAX(A.STORY)        AS STORY                        	\n");
			sqlBuffer.append("        , MAX(A.BRAND_ID)     AS BRAND_ID                     	\n");
			sqlBuffer.append("        , MAX(A.CATEGORY_ID)  AS CATEGORY_ID                  	\n");
			sqlBuffer.append("        , MAX(A.NATION)       AS NATION                       	\n");
			sqlBuffer.append("        , MAX(A.LOCAL_PRICE)  AS LOCAL_PRICE                  	\n");
			sqlBuffer.append("        , MAX(A.LOCAL_SALE)   AS LOCAL_SALE            			\n");
			sqlBuffer.append("        , MAX(A.DELI_PRICE)   AS DELI_PRICE                   	\n");
			sqlBuffer.append("        , MAX(A.ESTI_PRICE)   AS ESTI_PRICE                   	\n");
			sqlBuffer.append("        , MAX(A.MARGIN_GB)    AS MARGIN_GB                    	\n");
			sqlBuffer.append("        , MAX(A.SALE_PRICE)   AS SALE_PRICE                   	\n");
			sqlBuffer.append("        , MAX(A.USER_ID)      AS USER_ID                      	\n");
			sqlBuffer.append("        , A.VENDOR_ID                                         	\n");
			sqlBuffer.append("        , MAX(A.CARD_FEE)     AS CARD_FEE                     	\n");
			sqlBuffer.append("        , MAX(C.COCD)         AS SUPPLY_ID                       	\n");
			sqlBuffer.append("FROM    TBP050_TRANSFER A,                                    	\n");
			sqlBuffer.append("        (                                                     	\n");
			sqlBuffer.append("            SELECT  BAR_CODE                                  	\n");
			sqlBuffer.append("                    , VENDOR_ID                               	\n");
			sqlBuffer.append("                    , MAX(TRAN_DATE) AS TRAN_DATE             	\n");
			sqlBuffer.append("                    , MAX(TRAN_SEQ)  AS TRAN_SEQ              	\n");
			sqlBuffer.append("            FROM    TBP050_TRANSFER                           	\n");
			//sqlBuffer.append("            WHERE   STATUS IN ('00', '99')                    	\n");
			sqlBuffer.append("            GROUP BY BAR_CODE, VENDOR_ID                      	\n");
			sqlBuffer.append("        )   B,                                                	\n");
			sqlBuffer.append("        (                                                     	\n");
			sqlBuffer.append("            SELECT  REFCD AS VENDOR_ID                        	\n");
			sqlBuffer.append("                    , CD1   AS SHOP_ID                        	\n");
			sqlBuffer.append("                    , RETC  AS COCD                           	\n");
			sqlBuffer.append("            FROM    TBB150                                    	\n");
			sqlBuffer.append("            WHERE   REFTP = 'ZY'                              	\n");
			sqlBuffer.append("            AND     REFCD <> '0000'                           	\n");
			sqlBuffer.append("            AND     CD4   = '"+ transCD +"'                   	\n");
			sqlBuffer.append("        ) C                                                   	\n");
			sqlBuffer.append("WHERE  A.TRAN_DATE = B.TRAN_DATE                              	\n");
			sqlBuffer.append("AND    A.TRAN_SEQ  = B.TRAN_SEQ                               	\n");
			sqlBuffer.append("AND    A.BAR_CODE  = B.BAR_CODE                               	\n");
			sqlBuffer.append("AND    A.VENDOR_ID = C.VENDOR_ID                              	\n");
			sqlBuffer.append("AND    A.SHOP_ID   = C.SHOP_ID                                	\n");
			sqlBuffer.append("GROUP BY A.VENDOR_ID, A.PRODINC                               	\n");
			sqlBuffer.append("ORDER BY A.VENDOR_ID, A.PRODINC                               	\n");
	
			pstmt = conn.prepareStatement(sqlBuffer.toString());
		
			
			rs = pstmt.executeQuery();
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("iteminfo");
			rootElement.setAttribute("id", "1");			
			doc.appendChild(rootElement);
			
			
			
			
			int i = 0;
			while(rs.next())
			{
				Element item = doc.createElement("item");
				item.setAttribute("id", Integer.toString(i));
				rootElement.appendChild(item);
				
				Element prodInc = doc.createElement("prodInc");
				prodInc.appendChild(doc.createTextNode(rs.getString("PRODINC")));
				item.appendChild(prodInc);
				
				Element itemNm = doc.createElement("itemNm");
				CDATASection cData = doc.createCDATASection(rs.getString("PNAME"));
				itemNm.appendChild(cData);
				item.appendChild(itemNm);
				
				i++;
			}
			
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult sr = new StreamResult(sw);
			
			DOMSource source = new DOMSource(doc);
			
			transformer.transform(source, sr);
			
			Logger.debug("xmlStr="+sw.toString());
			
			sendMessage = "SUCCESS!!";
			
			
			
			
			
		} catch(SQLException e) {
			Logger.debug("###Error###:"+ methodName +" Error sql:"+ e.toString());
			sendMessage	= "Error : "+ e.toString();
		} catch(Exception e) {
			Logger.debug("###Error###:"+ methodName +" Error :"+ e.toString());
			sendMessage	= "Error : "+ e.toString();
		} finally {

			
			try 
		    {
				if( rs !=null ) try{ rs.close(); rs = null; }catch(Exception e){}finally{rs = null;}
				
				if( pstmt != null ) try{ pstmt.close(); pstmt = null; }catch(Exception e){}finally{pstmt = null;}
				
				DataBaseManager.close(conn, dbmode);
				if( conn!= null ) try{conn.close(); conn = null; }catch(Exception e){}finally{conn = null;}
				
		    } 
		    catch (Exception e) 
		    {
		    	Logger.debug("###Error###:"+ methodName +" Error :"+ e.toString());
		    }
		}
		
		
		
		
		
		return sendMessage;
	}
	
	
}
