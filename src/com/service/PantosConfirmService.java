package com.service;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.media.jai.JAI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
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
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

public class PantosConfirmService {
	private HttpClient httpClient;
	private JSONObject jObj; 
	private JSONArray jOriArr;
	private int sendCallCnt = 0;
	private String url;
	private String path;
	
	public String send(HttpServletRequest request, HttpServletResponse response) {
		Logger.debug("********************************* PANTOS CONFIRM SERVICE START *************************************");
		String fork = request.getParameter("fork");
		JSONArray param = JSONArray.fromObject(JSONObject.fromObject(request.getParameter("msg")).getJSONArray("header"));
		JSONObject respObj = new JSONObject();
		JSONObject preRespJSON = null;
		PropManager propMng = PropManager.getInstances();
		Properties propUrl = propMng.getProp("pantosSetup");
		url = propUrl.getProperty("pantos.url");
//		url = "http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y";
		
		propMng = PropManager.getInstances();
		Properties propPath = propMng.getProp("pantosSetup");
		path = propPath.getProperty("pantos.path");
		
		try {
			String loValues = new String(createLO(request,response));
			loValues = "msg=" + loValues;
			JSONObject respJSON = sendJSON(url, loValues);
//			JSONObject respJSON = sendJSON("http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y", loValues);
			
			Logger.debug("doPost resp: "+respJSON.toString());
			String tokenRS = respJSON.getJSONObject("header").getString("result");
			if(tokenRS.equals("fail")) {
				writeToken(respJSON.getJSONObject("header").getString("token"));
				loValues = new String(createLO(request, response));
				loValues = "msg=" + loValues;
//				respJSON = sendJSON("http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y", loValues);
				respJSON = sendJSON(url, loValues);
			}
			Thread.sleep(6000);
			String rsValues = new String(createRSValues(fork,param.getJSONObject(0)));
			rsValues = "msg=" + rsValues;
			Logger.debug("rsValues: "+rsValues);
//			respJSON = sendJSON("http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y", rsValues);
			respJSON = sendJSON(url, rsValues);
			
			propMng = PropManager.getInstances();
			Properties prop = propMng.getProp("icsConfirm");
			String isSleep = prop.getProperty("thread.sleep");
			if(!isSleep.equals("off")) {
				String strLabelImg = respJSON.getJSONArray("body").getJSONObject(0).getString("labelImg");
				Logger.debug("strLabelImg: "+strLabelImg);
				while(strLabelImg.equals("VGhlcmUgYXJlIG5vIGltYWdlcyBpbiB0aGUgRURNUy4NCg==")
						||strLabelImg.length() <= "VGhlcmUgYXJlIG5vIGltYWdlcyBpbiB0aGUgRURNUy4NCg==".length()) {
					if(sendCallCnt > 10) {
						break;
					}
					Thread.sleep(6000); 
					sendCallCnt++;
					respJSON = sendJSON(url, rsValues);
					if(respJSON != null) {
						strLabelImg = respJSON.getJSONArray("body").getJSONObject(0).getString("labelImg");
						preRespJSON = respJSON;
						Logger.debug("rs while respJson: "+respJSON.toString());
					} else {
						strLabelImg = preRespJSON.getJSONArray("body").getJSONObject(0).getString("labelImg");
						Logger.debug("rs while respJson: "+preRespJSON.toString());
					}
				}
			}
			
			if(respJSON!=null) {
				respObj.put("msg", JSONObject.fromObject(respJSON.toString()));
			} else {
				respObj.put("msg", JSONObject.fromObject(preRespJSON.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.debug(e.getMessage());
		}	
		Logger.debug("********************************* PANTOS CONFIRM SERVICE END *************************************");
		return respObj.toString();
	}
	
	public String send(JSONObject jProc0004) {
		Logger.debug("********************************* PANTOS CONFIRM SERVICE START *************************************");
//		JSONObject respObj = null;
		JSONObject respJSON = null;
		JSONObject preRespJSON = null;
		String rtnString = "";
		try {
			Logger.debug("jProc0004 proc: "+jProc0004.toString());
			String loValues = "msg="+new String(KisaSeedUtils.enc(jProc0004.toString(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg=="));
//			respJSON = sendJSON("http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y", loValues);
			respJSON = sendJSON(url, loValues);
			Logger.debug("respJSON: "+respJSON);
			
			jProc0004.getJSONObject("header").remove("callId");
			JSONObject jProc0005 = new JSONObject();
			JSONObject jProcHdr0005 = new JSONObject();
			JSONObject jProcBd0005 = new JSONObject();
					
			jProcHdr0005.put("bizPtrId", 	jProc0004.getJSONObject("header").getString("bizPtrId"));
			jProcHdr0005.put("bizPtrPw", 	jProc0004.getJSONObject("header").getString("bizPtrPw"));
			jProcHdr0005.put("callId", 		"proc0005");
			jProcHdr0005.put("ifCd", 		jProc0004.getJSONObject("header").getString("ifCd"));
			jProcHdr0005.put("encType", 	jProc0004.getJSONObject("header").getString("encType"));
			jProcHdr0005.put("token", 		jProc0004.getJSONObject("header").getString("token"));
			jProc0005.put("header", jProcHdr0005);
			
			jProcBd0005.put("devonTargetRow", 1);
			jProcBd0005.put("NUMBER_OF_ROWS_OF_PAGE", 1);
			jProcBd0005.put("NUMBER_OF_PAGES_OF_INDEX", 1);
			jProcBd0005.put("currentPage", 1);
			jProcBd0005.put("totalPage", 1);
			jProcBd0005.put("totalRecords", 1);
			jProcBd0005.put("eachGetCount", true);
			jProcBd0005.put("FIRST_ROW", 1);
			jProcBd0005.put("devonRowSize", 1);
			jProcBd0005.put("soNo", jProc0004.getJSONArray("body").getJSONObject(0).getString("soNo"));
			jProc0005.put("body", jProcBd0005);
			
			jProc0005.put("body", jProcBd0005);
			Logger.debug(jProc0005.toString());
			
			Logger.debug("jProc0005: "+jProc0005.toString());
			
			String rsValues = "msg="+new String(KisaSeedUtils.enc(jProc0005.toString(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg=="));
			respJSON = sendJSON(url, rsValues);
			Logger.debug("confirm JSON: "+ respJSON.toString());
			
			PropManager propMng = PropManager.getInstances();
			Properties prop = propMng.getProp("icsConfirm");
			String isSleep = prop.getProperty("thread.sleep");
			
			if(!isSleep.equals("off")) {
				String strLabelImg = respJSON.getJSONArray("body").getJSONObject(0).getString("labelImg");
				Logger.debug("strLabelImg: "+strLabelImg);
				while(strLabelImg.equals("VGhlcmUgYXJlIG5vIGltYWdlcyBpbiB0aGUgRURNUy4NCg==")
						||strLabelImg.length() <= "VGhlcmUgYXJlIG5vIGltYWdlcyBpbiB0aGUgRURNUy4NCg==".length()) {
					if(sendCallCnt > 10) {
						break;
					}
					Thread.sleep(6000); 
					sendCallCnt++;
					respJSON = sendJSON(url, rsValues);
					if(respJSON != null) {
						strLabelImg = respJSON.getJSONArray("body").getJSONObject(0).getString("labelImg");
						preRespJSON = respJSON;
						Logger.debug("rs while respJson: "+respJSON.toString());
					} else {
						strLabelImg = preRespJSON.getJSONArray("body").getJSONObject(0).getString("labelImg");
						Logger.debug("rs while respJson: "+preRespJSON.toString());
					}
					
				}
			}
			
			if(respJSON==null) {
				rtnString = "송장이미지 연동을 하지 못하였습니다.";
			} else {
				String expressNo = "";
				String sourcefilename = "";
				String targetfilename = "";
				
				for(int i=0; i<respJSON.getJSONArray("body").size(); i++) {
					if(respJSON.getJSONArray("body").getJSONObject(0).getString("hblNo").length() > 0) {
						expressNo = respJSON.getJSONArray("body").getJSONObject(0).getString("hblNo");
					} else if(respJSON.getJSONArray("body").getJSONObject(0).getString("mblNo").length() > 0) {
						expressNo = respJSON.getJSONArray("body").getJSONObject(0).getString("mblNo");
					} else {
						Logger.error(new Exception("송장번호 널값 오류"));
					}
					
					sourcefilename = "/export/home/api/pantos_bl/"+expressNo+"_"+(i+1)+".tiff";
					targetfilename = "/export/home/api/pantos_bl/"+expressNo+"_"+(i+1)+".jpg";
					 
					FileOutputStream fos = new FileOutputStream(new File(sourcefilename));
					fos.write(Base64.decodeBase64(respJSON.getJSONArray("body").getJSONObject(0).getString("labelImg").getBytes()));
					fos.flush();
					fos.close();
					
					FileSeekableStream stream = null;
					stream = new FileSeekableStream(sourcefilename);
					ImageDecoder dec = ImageCodec.createImageDecoder("tiff", stream,null);
					RenderedImage image = dec.decodeAsRenderedImage(0);
					JAI.create("filestore",image ,targetfilename,"JPEG");
					
					if(i>0) {
						rtnString +=  ",";
					}
					rtnString += expressNo+"_"+(i+1)+".jpg";
				}
			}
			Logger.debug("rtnString: "+rtnString);
		} catch (Exception e) {
			Logger.error(e);
		}	
		Logger.debug("********************************* PANTOS CONFIRM SERVICE END *************************************");
		return rtnString;
	}
	
	public JSONObject sendJSON(String url, String value) throws Exception {
		
		if(sendCallCnt > 10 ) {
			return null;
		}
		
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
//		httpClient.getParams().setParameter("http.socket.timeout", new Integer(1000));
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
		
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
				
		Logger.debug("sendJSON resp:"+new String(KisaSeedUtils.dec(arrayOutputStream.toByteArray(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==")));
//		Logger.debug("sendJSON resp:"+new String(arrayOutputStream.toByteArray()));
		
//		byte[] respByte = new String(arrayOutputStream.toByteArray(), "UTF-8").getBytes();
		byte[] respByte = KisaSeedUtils.dec(arrayOutputStream.toByteArray(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
		JSONObject respJSON = JSONObject.fromObject(new String(respByte, 0, respByte.length));
		
		return respJSON;
	}
	
	public void writeToken(String token) throws Exception {
		
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("pantosToken");
		String preToken = prop.getProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token");
		
//		OutputStream os = new FileOutputStream(new File("./props/pantosToken.properties"));
		OutputStream os = new FileOutputStream(new File(path+"pantosToken.properties"));
		
		prop.setProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".previous_token", preToken);
		prop.setProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token", token);
		prop.store(os, "");
		
		os.close();
	}
	
	public String readToken() {
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("pantosToken");
		if(prop.getProperty("pantos."+jOriArr.getJSONObject(0).getString("col4")+".current_token") == null) {
			return null;
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
			
		Logger.debug(fork);
		jObj = new JSONObject();
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp(fork);
//		int keyIdx = 1;
//		String[] keyList = prop.getProperty("prop.key").split(",");
		
		
		String msg = request.getParameter("msg");
		Logger.debug("msg: "+msg);
		JSONArray jArrParam = JSONArray.fromObject(JSONObject.fromObject(msg).getJSONArray("header"));
		JSONArray jArr = new JSONArray();
		JSONObject header = new JSONObject();
		
		jOriArr = jArrParam;
		token = readToken();
		int j=1;
		while(prop.getProperty("prop.proc0004.header.id"+j)!=null) {
			if(prop.getProperty("prop.proc0004.header.id"+j).equals("bizPtrPw")) {
				if(token.length()>0) {
					header.put(prop.getProperty("prop.proc0004.header.id"+j).trim(),"");
				} else {
					header.put(prop.getProperty("prop.proc0004.header.id"+j).trim()
							,jArrParam.getJSONObject(0).getString("col"+j));
				}
			} else if(prop.getProperty("prop.proc0004.header.id"+j).equals("token")) {
				header.put(prop.getProperty("prop.proc0004.header.id"+j),token);
			} else {
				header.put(prop.getProperty("prop.proc0004.header.id"+j), jArrParam.getJSONObject(0).getString("col"+j));
			}
			j++;
		}
		jObj.put("header", header);
		Logger.debug("header create!");
				
		for(int i=0; i<jArrParam.size(); i++) {
			j=1;
			JSONObject body = new JSONObject();
			while(prop.getProperty("prop.proc0004.body.id"+j)!=null) {
				if(prop.getProperty("prop.proc0004.body.id"+j).equals("soNo")) {
					body.put(prop.getProperty("prop.proc0004.body.id"+j), jArrParam.getJSONObject(i).getString("col6"));
				} else {
					body.put(prop.getProperty("prop.proc0004.body.id"+j), prop.getProperty("prop.proc0004.body.value"+j));
				}
				j++;
			}
			jArr.add(body);
		}
		jObj.put("body", jArr);
		Logger.debug("body create!");
		Logger.debug("********************************* PANTOS REQUEST JSON : "+jObj.toString());
		
		return KisaSeedUtils.enc(jObj.toString(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
	}
	
	public byte[] createRSValues(String fork, JSONObject param) throws Exception {
		
		String token = readToken();
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp(fork);
		
		JSONObject listReqObj = new JSONObject();
		JSONObject listReqHeader = new JSONObject();
		JSONObject listReqBody = new JSONObject();
		
		int j=1;
		while(prop.getProperty("prop.proc0005.header.id"+j)!=null) {
			if(prop.getProperty("prop.proc0005.header.id"+j).equals("bizPtrPw")) {
				if(token.length()>0) {
					listReqHeader.put(prop.getProperty("prop.proc0005.header.id"+j).trim(),"");
				} else {
					listReqHeader.put(prop.getProperty("prop.proc0005.header.id"+j).trim()
							,param.getString("col"+j));
				}
			} else if(prop.getProperty("prop.proc0005.header.id"+j).equals("token")) {
				listReqHeader.put(prop.getProperty("prop.proc0005.header.id"+j),token);
			} else if(prop.getProperty("prop.proc0005.header.id"+j).equals("callId")) {
				listReqHeader.put(prop.getProperty("prop.proc0005.header.id"+j), "proc0005");
			} else {
				listReqHeader.put(prop.getProperty("prop.proc0005.header.id"+j), param.getString("col"+j));
			}
			j++;
		}
		listReqObj.put("header", listReqHeader);
		Logger.debug("header create!");
		
		j=1;
		while(prop.getProperty("prop.proc0005.body.id"+j)!=null) {
			if(prop.getProperty("prop.proc0005.body.id"+j).equals("soNo")) {
				listReqBody.put(prop.getProperty("prop.proc0005.body.id"+j), param.getString("col6"));
			} else {
				listReqBody.put(prop.getProperty("prop.proc0005.body.id"+j), prop.getProperty("prop.proc0005.body.value"+j));
			}
			j++;
		}
		
		listReqObj.put("body", listReqBody);
		Logger.debug("body create!");
		Logger.debug("#################### jobj:: "+listReqObj.toString());
		return KisaSeedUtils.enc(listReqObj.toString(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
	}
}
