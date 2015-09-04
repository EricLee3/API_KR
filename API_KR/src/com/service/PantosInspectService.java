package com.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import com.service.command.log.Logger;
import com.service.command.util.KisaSeedUtils;
import com.service.command.util.PropManager;

public class PantosInspectService {
	private HttpClient httpClient;
	private JSONObject jObj; 
	private JSONArray jOriArr;
	private String url;
	private String path;
	
	public String send(HttpServletRequest request, HttpServletResponse response) {
		Logger.debug("************************************** PANTOS INSPECT SERVICE START **************************************");
		String fork = request.getParameter("fork");
		Logger.debug("******************************** "+fork+"***************************************************************");
		StringBuilder sbRespBody  = new StringBuilder();
		PropManager propMng = PropManager.getInstances();
		Properties propUrl = propMng.getProp("pantosSetup");
		url = propUrl.getProperty("pantos.url");
		//url = "http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y";
		
		propMng = PropManager.getInstances();
		Properties propPath = propMng.getProp("pantosSetup");
		path = propPath.getProperty("pantos.path");
		
		try {
			String loValues = new String(createLO(request,response));
			loValues = "msg=" + loValues;
			JSONObject respJSON = sendJSON(url, loValues);
			
			Logger.debug("PANTOS RESPONSE JSON: "+respJSON.toString());
			System.out.println("PANTOS RESPONSE JSON: "+respJSON.toString());
			
			String tokenRS = respJSON.getJSONObject("header").getString("result");
			if(tokenRS.equals("fail")) {
				writeToken(respJSON.getJSONObject("header").getString("token"));
				loValues = new String(createLO(request, response));
				loValues = "msg=" + loValues;
				respJSON = sendJSON(url, loValues);
			}
			
			String token = readToken();
			String rsValues = new String(createRSValues(fork, token));
			rsValues = "msg=" + rsValues;
			
			respJSON = sendJSON(url, rsValues);
			response.setContentType("application/x-www-form-urlencoded");
			
			JSONObject respBody = respJSON.getJSONArray("body").getJSONObject(0); 
			if(fork.equals("ics")) {				
				JSONObject jProc0004 = new JSONObject();
				JSONObject jProcHdr0004 = new JSONObject();
				JSONArray jProcArr0004 = new JSONArray();
				JSONObject jProcBd0004 = new JSONObject();
				
				
				jProcHdr0004.put("bizPtrId", 	jOriArr.getJSONObject(0).getString("col1"));
				jProcHdr0004.put("bizPtrPw", 	jOriArr.getJSONObject(0).getString("col2"));
				jProcHdr0004.put("callId", 		"proc0004");
				jProcHdr0004.put("ifCd", 		jOriArr.getJSONObject(0).getString("col4"));
				jProcHdr0004.put("encType", 	jOriArr.getJSONObject(0).getString("col5"));
				jProcHdr0004.put("token", 		token);
				jProc0004.put("header", jProcHdr0004);
				
				jProcBd0004.put("devonTargetRow", 1);
				jProcBd0004.put("NUMBER_OF_ROWS_OF_PAGE", 1);
				jProcBd0004.put("NUMBER_OF_PAGES_OF_INDEX", 1);
				jProcBd0004.put("currentPage", 1);
				jProcBd0004.put("totalPage", 1);
				jProcBd0004.put("totalRecords", 1);
				jProcBd0004.put("eachGetCount", true);
				jProcBd0004.put("FIRST_ROW", 1);
				jProcBd0004.put("devonRowSize", 1);
				jProcBd0004.put("soNo", respBody.getString("soNo"));
				jProc0004.put("body", jProcBd0004);
				jProcArr0004.add(jProcBd0004);
				
				jProc0004.put("body", jProcArr0004);
				sbRespBody.append(jProc0004.toString());
			} else {
				sbRespBody.append(respJSON.toString());
			}
		} catch (Exception e) {
			sbRespBody.append("error: 등록시 오류가 발생하였습니다. -> "+e.getMessage());
			System.out.println(e.getMessage());
			Logger.debug(e.getMessage());
			Logger.error(e);
		}	
		
		Logger.debug("************************************** PANTOS INSPECT SERVICE END **************************************");
		return sbRespBody.toString();
	}
	
	public JSONObject sendJSON(String url, String value) throws Exception {
		
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
		
		Logger.debug("url: "+ url);
		System.out.println("url: "+url);
		HttpPost request = new HttpPost(url);
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);

//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("msg", value));
		
		ByteArrayEntity params = new ByteArrayEntity(value.getBytes());
		request.addHeader("content-type", "application/x-www-form-urlencoded");
//		request.setEntity(new UrlEncodedFormEntity(params));
		
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		InputStream respIS = entity.getContent();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[1024];
		int line = -1;
		while((line=respIS.read(buffer))!= -1) {
			arrayOutputStream.write(buffer, 0, line);
		}
		arrayOutputStream.flush();
		arrayOutputStream.close();
		
		Logger.debug("DECODE PANTOS RESP JSON :" +new String(KisaSeedUtils.dec(arrayOutputStream.toByteArray(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==")));
		//Logger.debug("DECODE PANTOS RESP JSON :" +new String(arrayOutputStream.toByteArray()));
		//System.out.println("DECODE PANTOS RESP JSON :" +new String(arrayOutputStream.toByteArray()));
		
//		byte[] respByte = new String(arrayOutputStream.toByteArray(), "UTF-8").getBytes();
		byte[] respByte = KisaSeedUtils.dec(arrayOutputStream.toByteArray(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
		JSONObject respJSON = JSONObject.fromObject(new String(respByte, 0, respByte.length));
		
		return respJSON;
	}
	
	public void writeToken(String token) {
		Logger.debug("token write: "+token);
		System.out.println("token write: "+token);
		try {
			
			PropManager propMng = PropManager.getInstances();
			Properties prop = propMng.getProp("pantosToken");
			String preToken = prop.getProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token");
			
			OutputStream os = new FileOutputStream(new File(path+"pantosToken.properties"));			
			prop.setProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".previous_token", preToken);
			prop.setProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token", token);
			prop.store(os, "");
			os.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
			Logger.error(e);
			Logger.debug(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Logger.error(e);
			Logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String readToken() {
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("pantosToken");
		Logger.debug("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token");
		System.out.println("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token");
		if(prop.getProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token") == null) {
			return "";
		} else {
			return prop.getProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token");
		}
	}
		
	public byte[] createLO(HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		String token = "";
		String fork = request.getParameter("fork");
		if(fork == null) {
			return null;
		}
			
		jObj = new JSONObject();
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp(fork);
//		int keyIdx = 1;
//		String[] keyList = prop.getProperty("prop.key").split(",");
		
		String msg = request.getParameter("msg");
		JSONArray jArrParam = JSONArray.fromObject(JSONObject.fromObject(msg).getJSONArray("header"));
		JSONArray jArr = new JSONArray();
		JSONObject header = new JSONObject();
		
		jOriArr = jArrParam;
		token = readToken();
		
		Logger.debug(jArrParam.toString());
		System.out.println(jArrParam.toString());
		int j=1;
		while(prop.getProperty("prop.proc0002.header.id"+j)!=null) {
			if(prop.getProperty("prop.proc0002.header.id"+j).equals("bizPtrPw")) {
				if(token.length()>0) {
					header.put(prop.getProperty("prop.proc0002.header.id"+j).trim(),"");
				} else {
					header.put(prop.getProperty("prop.proc0002.header.id"+j).trim()
							,jArrParam.getJSONObject(0).getString("col"+j));
				}
			} else if(prop.getProperty("prop.proc0002.header.id"+j).equals("token")) {
				header.put(prop.getProperty("prop.proc0002.header.id"+j),token);
			} else {
				header.put(prop.getProperty("prop.proc0002.header.id"+j), jArrParam.getJSONObject(0).getString("col"+j));
			}
			j++;
		}
		jObj.put("header", header);
		Logger.debug("header create!");
		System.out.println("header create!");
		
		for(int i=0; i<jArrParam.size(); i++) {
			j=1;
			JSONObject body = new JSONObject();
			while(prop.getProperty("prop.proc0002.body.id"+j)!=null) {
				body.put(prop.getProperty("prop.proc0002.body.id"+j), jArrParam.getJSONObject(i).getString("col"+(j+header.size())));
				j++;
			}
			
			body.put("carrCd", "");
			body.put("expsSvcTypeCd", "");
			
			jArr.add(body);
		}
		jObj.put("body", jArr);
		Logger.debug("body create!");
		Logger.debug("#################### PANTOS SEND JSON:: "+jObj.toString());
		System.out.println("#################### PANTOS SEND JSON:: "+jObj.toString());
		return KisaSeedUtils.enc(jObj.toString(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
	}
	
	public byte[] createRSValues(String fork, String token) throws Exception {
		int i = 1;
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp(fork);
		JSONObject listReqObj = new JSONObject();
		JSONObject listReqHeader = new JSONObject();
		JSONObject listReqBody = new JSONObject();
		
		while(prop.getProperty("prop.proc0003.header.id"+i)!= null) {
			if(prop.getProperty("prop.proc0003.header.id"+i).equals("callId")) {
				listReqHeader.put(prop.getProperty("prop.proc0003.header.id"+i), "proc0003");
			} else {
				listReqHeader.put(prop.getProperty("prop.proc0003.header.id"+i), 
						jObj.getJSONObject("header").getString(prop.getProperty("prop.proc0003.header.id"+i)));
			}
			i++;
		}
		listReqObj.put("header",listReqHeader);
		
		Calendar cal = Calendar.getInstance();
		String toDate = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH)+1);
		if(month.length() < 2) {
			month = "0"+month;
		} 
		
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if(day.length() < 2) {
			day = "0"+day;
		}
		
		toDate = toDate + month + day;
		Logger.debug("toDate: "+toDate);
//		String toDate = jObj.getJSONArray("body").getJSONObject(0).getString("coNo").substring(6, 14);
		SimpleDateFormat smd = new SimpleDateFormat("yyyyMMdd");
		Date endDate = smd.parse(toDate);
		Date startDate = new Date();
		startDate.setTime(endDate.getTime() + (1000 * 60 * 60 * 24) * -1);
		Logger.debug("endDate: "+smd.format(endDate));
		Logger.debug("startDate: "+smd.format(startDate));
		
//		Calendar cal = Calendar.getInstance();
//		String startDate = String.valueOf(cal.get(Calendar.YEAR))+"0101";
//		String endDate = String.valueOf(cal.get(Calendar.YEAR))+"1231";
		
		i=1;
		while(prop.getProperty("prop.proc0003.body.id"+i)!= null) {
			if(prop.getProperty("prop.proc0003.body.id"+i).equals("fromDate")) {
				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), smd.format(startDate) );
//				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), startDate);
			} else if(prop.getProperty("prop.proc0003.body.id"+i).equals("toDate")) {
				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), smd.format(endDate) );
//				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), endDate);
			} else if(prop.getProperty("prop.proc0003.body.id"+i).equals("customerOrder")) {
				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), jObj.getJSONArray("body").getJSONObject(0).getString("coNo"));
			} else if(prop.getProperty("prop.proc0003.body.id"+i).equals("ifCd")) { 
				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), jOriArr.getJSONObject(0).getString("col4"));
			} else {
				listReqBody.put(prop.getProperty("prop.proc0003.body.id"+i), prop.getProperty("prop.proc0003.body.value"+i));
			}
			i++;
		}
		
		listReqObj.put("body",listReqBody);
		
		Logger.debug("PANTOS RS REQUEST :"+listReqObj.toString());
		System.out.println("PANTOS RS REQUEST :"+listReqObj.toString());
		return KisaSeedUtils.enc(listReqObj.toString(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
	}
}
