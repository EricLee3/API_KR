package com.service;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
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
import com.service.command.util.StringUtil;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

public class PantosLabelService {
	private HttpClient httpClient;
	private JSONObject jObj; 
	private JSONArray jOriArr;
	private int sendCallCnt = 0;
	private String url;
	private String path;
	
	public String send(HttpServletRequest request, HttpServletResponse response) {
		Logger.debug("********************************* PANTOS LABEL SERVICE START *************************************");
		String soNo = request.getParameter("soNo");
		JSONObject respObj = new JSONObject();
		JSONObject preRespJSON = null;
		PropManager propMng = PropManager.getInstances();
		Properties propUrl = propMng.getProp("pantosSetup");
		url = propUrl.getProperty("pantos.url");
		
		Logger.debug("soNo : " + soNo);
		Logger.debug("url : " + url);
		
//		url = "http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y";
		
		propMng = PropManager.getInstances();
		Properties propPath = propMng.getProp("pantosSetup");
		path = propPath.getProperty("pantos.path");
		
		try {
			String rsValues = new String(createRSValues(soNo));
			rsValues = "msg=" + rsValues;
			Logger.debug("rsValues: "+rsValues);
//			respJSON = sendJSON("http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y", rsValues);
			JSONObject respJSON = sendJSON(url, rsValues);
			
			String tokenRS = respJSON.getJSONObject("header").getString("result");
			if(tokenRS.equals("fail")) {
				writeToken(respJSON.getJSONObject("header").getString("token"));
				rsValues = new String(createRSValues(soNo));
				rsValues = "msg=" + rsValues;
//				respJSON = sendJSON("http://totPrd.pantos.com/gsi/cm/extif/execExtIf.ext?comCd=isecommerce&encYn=Y", loValues);
				respJSON = sendJSON(url, rsValues);
			}
			Thread.sleep(6000);

			propMng = PropManager.getInstances();
			Properties prop = propMng.getProp("wmsConfirm");
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
		Logger.debug("********************************* PANTOS LABEL SERVICE END *************************************");
		return respObj.toString();
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
		
		byte[] respByte = KisaSeedUtils.dec(arrayOutputStream.toByteArray(), "utf-8", "sUxsPvbH7q66fRUGK87Hpg==");
//		String respStr = URLDecoder.decode(new String(respByte, 0, respByte.length), "UTF-8");
		String respStr = new String(respByte, 0, respByte.length);
		Logger.debug("sendJSON resp: " + respStr);
		
//		byte[] respByte = new String(arrayOutputStream.toByteArray(), "UTF-8").getBytes();
		JSONObject respJSON = JSONObject.fromObject(respStr);
//		String testStr = "{\"header\":{\"result\":\"success\",\"code\":\"0\",\"message\":\"The process is complete.\",\"token\":\"\",\"devonTargetRow\":1,\"NUMBER_OF_ROWS_OF_PAGE\":1,\"NUMBER_OF_PAGES_OF_INDEX\":1,\"currentPage\":1,\"totalPage\":2,\"totalRecords\":2,\"eachGetCount\":false,\"FIRST_ROW\":1,\"devonRowSize\":1},\"body\":[{\"rowTotCnt\":2,\"pageTotCnt\":2,\"pageNo\":1,\"innerPageTotCnt\":2,\"innerPageRownum\":1,\"choice\":\"F\",\"recvSeq\":13686720,\"coNo\":\"A1410120171211004511\",\"hblNo\":\"PEKR70474571\",\"mblNo\":\"PEKR70474571\",\"boxQty\":0,\"uploadDate\":\"20171211\",\"uploadTime\":\"1550\",\"ptalProgStatCd\":\"40\",\"ptalProgStatNm\":\"Confirm\",\"customerConfirm\":\"Y\",\"shppCd\":\"2207111\",\"customerName\":\"HQ\",\"cneeName\":\"ASOS\",\"cneeAddr\":\"ASOS, BARLBY ROAD, SELBY, YO8 5BL, UK\",\"cneeAddr1\":\"ASOS, BARLBY ROAD, SELBY, YO8 5BL, UK\",\"cneeAddr2\":\"\",\"cneeAddr3\":\"SELBY\",\"cneeAddr4\":\"\",\"cneeNatnCd\":\"GB\",\"itemCdOrg\":\"CLOTHES\",\"itemCd\":\"CLOTHES\",\"itemNm\":\"NIKE ADVANCED KNIT 15 TRACKSUIT SET IN RED 861766-608\",\"expressMode\":\"Pantos Express\",\"cneeNm1\":\"ASOS\",\"cneeNm2\":\"\",\"cneeZipcd\":\"YO85BL\",\"cneeTelNo\":\"+44(0)20 7756 1001\",\"cneeEmailAddr\":\".\",\"polCd\":\"KRICN\",\"podCd\":\"GBLON\",\"orgnNatnCd\":\"KR\",\"untprc\":48.52,\"curCd\":\"GBP\",\"itemQty\":1,\"hsCd\":\"6211490000\",\"homepageAddr\":\"\",\"wthLen\":10,\"vertLen\":10,\"hgt\":10,\"wgt\":2.1,\"wgtUnitCd\":\"1\",\"rmk\":\"\",\"etcNm1\":\"\",\"frgttermCd\":\"PP\",\"multiLabelNo\":\"\",\"labelImg\":\"SUkqANw6AAD//5WgJQFHyBKBaSfyci00hsy/Gko////////////////////////////5qBCV+SYchUSyyskZ39h2RZbDIv5FvwQeb/64XTXX09f7PR17PR446J8x/6t36t/oJv/u1+7/0//WzGXZsic83H48YQerZ4jQkGYjcXM0IIMvHyPxc9JzEgy+af9/VMJ14Qd74TQafoMIO9PQfjp3/1SjTi6rrXtFw641rTj661/usvC7aLvdou2GFvou3CcMLk7zYwwtF25O/eGE9fOxRaFVTdP9P66TqP02k/03TfX3/SI62tJpJ6qh93puH9Y/pNV6fuv30F9/dr6r0wf1QX6vra/r52NHs9evr69+LYP7s4E+/vv7X7CI6176uqv18N/4RG//r8h/vXwh7/33f784N/VTd/7r7//ow/X//z/6pWH+7S/f720vtfsQiOt9tJtdW791zOL3//6aX9r719FDj17W0rW0vVJhpWv1VhfbWq2Gl8f4giP77FMWxTFQ9jjYqH3bHD2Ku9iuG/4IEK12mRj4Ijzt6CDTTf8ER9va/a26+VCI6P5HRHRIRHRHRHRHR0RtEfI6iCI8XROiOiPkdHREfI632msMLYT7CYQaf8MK32vwwt/6TKHKtKLSamiBAkopKNLikkkEFyOsRERERHEREREREREcRH+KQtEdcILQiLSRHU44QUS66Ufx/r4JEeBFDpSOgSQiGR0cGR0ELI6iIaQSSSI+R0ZkpH/1+hoRHCCE44IE5h1EECHMOCBMIFERjKHCCEf4JfZxwgrOOcdRCCEQiOouUOIRHURkdDLHOPI6iJh0R1//QjQiGgghBAhzjhBYnHFzjnHCCiCKHBAhEIJkdBHHOOEEI/4X5Q5x8w5xwiOohBCCI9ERBAhKHURHETDiNiIgiP/j+IhBYiOccRCI6mHBAhBAhCI+ixzjoupxwQIShzjqcc44IEP/8ECFkfI+R8jpCeAQIZHURBAhBAhTEWKCCEQQIRYQiIIj//4ggQiI4RH2IIF0ccIIQQIQRHkcc44Ij8QQJI45xwiOpxzjnHCCF//lDhBCRjgiPwQIVGLEECEQghsRHCCFhCI4iIhEdf/QiaxFEdIQiOpx5HUTDlOIRHSLHOOER0gQIQgzjhEdShy7BAh//ZxwRH5xwghsIIcQgujDhBRlDhBDhBDxiER1EWhBEfBF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F2bvI8cjhnI5EfORyNGbi7PEYi7CDNmfIkGeM5EdmH/jTTaS0L002L2LiLCbF8Ni44v9vT0k9IJxabF6aaacRf+CRoaM7TUMJmjo0UaKLjouMtybuZ2i4hgrRcQzWEzR0ktOYenunzO5cUaGi4hhI0NGijO0aHLcm//QtJO1C1b8cXb9ummkm3uygDDuurY///+k3ti0+qV4pOldNN/7CeKHTpcHBql/6frXaptVX7wRH94Ij+8ER/98JrbW8Hjv6X+jwu9pN8NhtN7X7SG03UJpq7/77SvtK+0o0swK01fw//b/6JjvT21w22muv6ZHCJpJkCaaft/616161/omO00/w3/6X/hNbMPpJrM2Q201/7zUTbUJp6pLSSerp6unq6mn4TTT15m//b/+nm5W7bTy85+dNPv9LNyaVimknd/xscbHGx5ub9NNJPy8+bv9L/9N7XpP/7T7pe3aTTe02+6Ri//832kqXTTbtpeX29tL2//FsUm3DbS4pik0tD9Jik0oaaUOGh/vT09JsVHFppQ2KYpNimK9L/smPaad0m8NNNN8zhe2Gmm3abd5nC0kqenp2g1M4TJj2m3aaaaa9v4iIiIiIiIiI2IiIiIiIjYiIiIiIiNiIiIiIiIiP2mmvppp/qq+SYG0Fn//+P/////////////8lozNkOI0vhB6hf3WpWI6kdRnUvrY+EGEGEH+Xj9QmmE/0+XZ+Nxc0GfM1O5fNE8cvn+x0Gug0wg9AiPoJ0EG0E/4OPi3T+9un2/shPJx5O7BInD68Um8ft030/Tir39/Dfv3pP2w6th/Bv1/7rZEm2mRGQ/g///q2w2rYfyUtuq//rDDXDD/92/X6GdR9EMf20vXT20rB3sH/H/dPakh72k/394pA2K0ajN7fmJv7C9Zh7tfp02ldfDC/su3DC63W1dfiIjiIircNWGEoa//YpimK/pW001/3BhBhAwv4iIj8f/+SYG0Fn//+P+ACACAK0ZZDQM0kM4zbMwzTWzYZpSH//////////LLzbxiXWXZEvLL//////////////NgmInGSGQxTWHzYJ//////////////5sPItkGJiRZ//////////kpzMz7Mz////2iraKs+FW////8IjpJtJ0m/zNmtmbOBzbNGbf/17Fbsf////9W3Tt/////1dg/YP/////Vgw/Bh45sU2JItxM2aC4/4w3eG////6PjLp6zCf////T3srlf////XbStW0o5dl5l2XZOy7LucZtx/q2T8JkzcL/////EREf/////////8TMEjNAXEcf//////8shoGw2C5szbNh5c5sQkM2zZm3///////////////kpzQz7MzOhGefGbzQjGczwzNmmfI1mbENo+M3nM0zmcZ5y1ZFsvC7Lrkhmz80f7RVtFWfCraPhXuVxV0fCo1K4rirP7R8PoVbnPZVt6egj2ewRhf///+ER0k2k6TdcIjq7Wu0rtJ4RHVOk29K9pWlTr////9exW7G9eqq91qlte7sVut1dUrq/////Vt07b9Xd2/dXdPV07dPfdXVvdFcgU4HzQOZmbFGQSyLZeTb+rsH7BoavVUhsdV6vasHIt2NjY6w1////6sGH4MOVzq5Hblc6keerdwYbKOkfHVeECML////xhu8N3xzxq9543xqkG1ut3+r////6PjMJ6zCd3R8dPbv09aPjqyQnp7f79+XMu5xmxRzbNBTMGB/T3srleq0926r3bK5U91fauvul////9dtK1bSql6bWq6btV6VtKqWvZV7X////Vsn4TJ4V5Hau3kcEf7cMKrkfFHJ2RII3I5zy2Ewf///8RERZWzMozKXBf////yuViibZtm3ItkPWIl2QQU9JJL///iI///////ywyLZdyGzWHNghEM0F////////////yywBiQYmRa5mzgpt8uy0nkXIwRtGvOZdH/////K89u6q0F////6ert0v////fdOqRNxQOcFkEmXchhx8R/oHXVrSX///4Iwo7qrX///4spyub3bZH////9606TZNwUzYpIEJAc4zNmYGDYJ/6erWn///+RZdrb1bKNwT///9KqrV7///+1I7I3KOlbJuNCyJMusikCJFsmQXZYZY14iLKN0r///////6X///xlly5lhkPMnZZBBiZdZFSf///////////////yLQuZdZFAiKHNsq3////koM2IXRORjOf//8AEAErRkXRvMZpmecRhHETcpFNGZs1ikCFIYptlWykPpNu9dpZsQ2zbl2QXwuc2Gw2Gb+xVb3X///+3VP3X///9g5FhobH////Bhsqilc6GTsuy7ItZFsgxZYEWGWNeG1u9////kWT07v////vtVX///+2lVVWTcsZDDQbBIk5kWZOyHmRLIqT2TsiORwR////+IiI////////IoZZBdk5l1l2Q9ZcyKH/////////////xNbJAgmbGJD2P////////LIaBsE2KaBqmYIRIX//////////8uyGyyHoQMWRZl0LmXWRUn////////////Ibl3OCmxTQC5xmbNs1v//////////////zbzYfl2RQy5lzmw0HGP//////////////+IiSBRMwMGbk5lkEQo/////////lkNAzc2JLsuk2zbOPNuXhDpP///////////////8svM3JzmZyLOaCjj/////////////5sEzbNiEgOcZmzMGs4//////////////+RaCJFLkWyJZFSf///////////////LrIaKEWsuy5l3///////////////HyGKbFNA0RIsyw/////////////Hm3LsuxIZpETgf///////////EREREf/+SYG0Fn//+VyyK4Ff/////8AEAErRlcsiuBRXFj//////////////IVHUjqM6l/8IMIMIPKTNMzRtnM+z7JwhcIYGcZqR9n2UL+oTTCf6SbhBp/oNU01/3L5onjl89VVHtsqz3R7Z1yfR6HuqPbR7a5yM88ZozwzAhz+gRH0E6CDaCfIeq1+k3/6TYX/C7qEGEH3/726fb3QIj6BEfVJu/SbSS8UCI+k2k2gRH9ukbHPdHofn/rxSbx066/3H9JKiO+F/8L7SQQbS/f+r397S6/9pf8Q6Lsf/H3/bb/S/2w6th5oFS0uv19JJGvEP//bSSb0kiO/b/WyJNtMibWvXWv9f4tzX//9L2KYh6/1bYbVsPIeuu/ul/9N//7H+jXiv9YYa4Ye6EOIa+oS9JL///r0e2Lel9DOo+iGPTY/9L////39Jvn9t/sHewfaJGafa6CbXz3/tq2v//7f/e0nx93rXr9JtL66+m0v//o1Gb2/MTf9sdSEgfsVHsbH9/5H/6/6dNpXX+wl2Ekk0GFsJhfsMJNrpaSf63W1dcRccREREREemKimKjZEkI9W4asMJQ17rsMIMJqF/9imKYrdcRERH6VtNNbK0X/cGEGEDCxH+IiI/////////////////////////////////kmBtBZ///lcsiuBX//////////+SkyNr/hB3lOZ7K3miIzPR7IaOnkCM7RmmQ/8+RwnXb9XRP4TJPbS/0+jY7fvUbfRsb/X1E4ziI5nMvm2efTetaCI69em16v7BoOIRHVaaD0hovHmbRHjDI6PM+8juzb+82z2bZHVF5B6QmM3mDQZgzmR4wzhtqeeXzebf2R2e8FrSNFHv9w040LQabF6DeukHoXEP+tPTpNU/TqtB/De/fcId+rMJHtwSPaR7o9tGvSPe91R70j2poQYXV1KyGEj3iVAlQ5WXpHt3dI9/kE4veu4vurEL7e4hau93W/e9urXvuGK7dVwxtRC/e/yLpvrWq+vBaqosFf4tfqL6qv6rkd/f5GfgtVxf15R3fdypa7M+qRo33o0Vet6f67un7M+6Db061QbekaN3X+1s5715vs57fdwvupRwvW5SescpDuu12/sz1te7NO1hf3K3r7FMVUdMUxXaX2tadnO1bXs5xtWc/Tr+0tS/+r+Y96ta1s5/DCatq2mtiqqO2KYqNCqYppWKpirph7FMVGw2KWqjjjtimK+IiIiOGrtgiP7CI+0wRH5x1bW7VsIj/fhphEfDC2u7DCpgiP7CI+1/ERERGhEREREREaERxERERERERERH+VjU7AtfgAgAitGVyyKwKdgQpWorix/DCDC/4j/////////////////5KhDcZ5mHzsTZ1REeVNE6Ps655mtGmfM8J+G6VYQa+qhPCd4QcP8/20p5Aee61qs9tZ7e6Njn//btfSbC3hQqTapdLT03/3rIcfM3kdnjN5eMMjx7/QIj+YzbI60CI+gRH/QIj49/3f/bSwg04sJ6aZH0seF0khfC4X9a747X/YrnijQ5ozQ4lOJUf0h7SR7WOP64QVLB1f9LkKOnQQbptBBuE02R+tektv9tUuHeGR4/8f4//t9a9cdV/HWD7hh/+EgiPmznP//S7/S4RH+/2W9dulJFX+fTf///7dYS9u6/HQh94bv/pNj///1rpf6f+/5j9L/T+CBa/q/bFaC6bYr83pEPMf99f9JvbSYdew6X/+9hEf/bX0m0m0rX+tyHHGxXsV9eiEc/xEa/ijfsVFMVEL/T4aa9r7a/yBa/teGg0Gq+IiIiIiIiuIjHiIiIiI/8ML+CWP+v+P//////////JMDaCz///H////////+RYUgiIj/dnT/yuYuP9tf7R5njPs4iPEfMIjtBkdHDOIvHD/2g0049C+04hp/3R7yso8YKpohhUj3mhSs/13hjQ902t7dXDH/xcjt+///I2UU/8N1VK71T1Qb/pSqt3eq3a3Zn/1s5l/zf7a/r5f/xTFRsVVIUw6Yqqj/CaDCau5x121dhhfERERxERER+/1//H//////+ACACCtGWdUP/+QUziKGcEI6PZgQ2ZDROKcyGj5nEezjPinDJCJxTn9Hh2irP5WOpzlW1RyHoq3dBHs9nQVbVHIf0E26T9W6Ta1CI6SSbq6ekm1qER1/FVvt1sUER1a/Y26V3YoIjq1/pVT9LTY71e3S29NjvV/u/45FwHoi3fYOOuReB6It3/T/yvytQ+UhWkDDlf+VUHykK/639rYb7jw3a/Yb7j/Z9VX1TmifdHx59PVXuTp90fH91tlcu7W/Wn77t0lv1p/sq1Vr09NpdLpNpU7KvTaXS/2pHgXayJZMPIg6vZPGrCkTCZGRB1fxERERERERERERERH////////ILmiP55Ho+jbOR9GxnEbZ9GaORyP5gjyPr9rqgRhKgRjQIwlNNUCMaqgRhIEYXKPX8IjpVVLWtLWlrVV111X66CI6QRHSCI67QRHQv2giOkkER0LoIjpBEdVdWgiOtBEdIIjr9WPSj7jruOo6jj770kgY4/1fSr16X/r+PSuCML/1f376uvWv6CI6VXFld/x6Vf6C30tBfvvS//R8ehD/1/vX/+L//T5/9cILXrCC/XU5l1/9ffZULplQv69lQmVCfS/9XrtcjhfyPO00rX/ERERERERERERER/////////////////////////////////////gAgAgrRlnVD///////////////////////////////////////////////////////////////////8AEAECtGWdUP///////////////////////////////////////////////////////////////////wAQAQK0ZZ1Q/////////////////////////////////////////////+QUKShEsBDsVM0ZGM4ZxkDZDZhmGejDPxLGfiSI4zWyGzX/tfr96qq11Wnq/2uov0o3a29uorbf5u8vZa/3qu1td6/22eI9F2EDOGTlR0Ztl2RzPxHZsycjMph5mzER3wRHs8RyPSl2fd2u5mZ+Lvc+ZOy7PGEDLs8Sl3Zw7/WnFuEGE7emLjQYTCaXUdrqnexdVVWoQcXaoNOgg3pNi6Qb/vRodqmnVrdJpp93T/SNDfTu79073TQtNbRoadp/6SS5cQwUuM15D/SlxgpozXmvvUFr6XpMuNVXMO1lxlxmHazRlwpcQwVJbLjU0b/GL76TdjbFberdjYr37T4vek3d2lZHnSb3sjzq2laTfYuk3at1+gtfTyLr6+nkXiLUrrdbwr79VV1a09dWtPtU/QV+qdv9GYl/asOZOvqNhsPe6T9Twmkvd3Vu9rfbvHV2vZwRe41/NqTH19O3fv7baHW/0iY/3eqq6Y6eumPuqfpEx7vX/tBPf2oPSevNXB7vin0E7eq3d1fa33zEK7XtBOq3Nr/Vdf4IrHaf9gisYIrH18dcU/VVdP9dPd1/Vb9Wv71v20gn1M+0tzeE8J6L93/rppXdtXtpX3ub7TbS71TSvN/+r64OOKkHvcfFxUV9b1e7HVUnTx108WxUcHV7HTF1+2O/aa0m63aarvp2Okru3t2r7d22u2NK3/0yY7rcMINdaLdbCDQaVuv2THezDqqrrDCrrYTLdBhPTJjtmHVML+IiIiIiIuIiIiIiIiLiIiOIiIiIjiIiIiI/9Js7A9Uv4Lsf/3qv8Kwwl+IiI/JMDaCz//8AEAECtGWdUP//////JlHswZgi8Yi5FyMZ6LkXjBnsxGDMReMZ4zBF4xmCORjMRyMGYMxF48ZgzEXjEcMxGDMReORgzBF4xnjMRgzBmI4ReMZiMZ7MEXIwZjPReMGYjkYMwZi////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////+IiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiPABABCtGWdUP/////////////////////MoAgkAoPDJAhwNBIDQSCIQzMNBrMnBc8Gg6H+H6UPCDXX/97VXhP/1/emEr7XX1/f1emuvr+SpklCSkkakr9fS/29NKw2RhJNqS5KiQHX+/pVpeqS6QX++0lb1/XVL/XTSpUknVJbSRGP/v6VuvcmPWTH0RyVr/rtJUtektJKhDpBf/kG/tK9fS1S7Vhf9fuglX71vdD/0tjil9JVSW1/x6fXpdJclmv9dwl/1+m1/1JKCEH1XpdJYvX+tW4L9X1f/8FwrksDX6VaV1/ySi72G/0vS0//6CbpuuokIdRIROq//3bq965xpr/9p6ulS6wtV/9O7btv7/v/+3T1/Xa1/9p7tbSKc1KQ20l/8gXx3TexrrFf/9u32v3//tdXTT07Vf/Tbtu2ltWFf//p8ML8GF/xERERERERH///////////yyjingh4zNHkTozRgzzJ0cE/eE1VVTTX/K5yiDY6VaR8zw1JC/t9N4QRHQQRHX2lhBEdL/a0h4Swlx94Spf79RcXX7F6/vIkKvpKq1/r3/pXfX/lIX6pVWv/7M9L6Su/4AIAIrRllHR/NokZ/Lo0ROjiOR/OIzRmjvD+vaS/02v/ipCWKf9ESBWv4WGkvS2v+IiIiIiI////////////////JMDaCz///H//////////5NkmaEfBc1ESBnzJDLmSGRwXI4OQyPhgkF8rjTPs+zYQ8idE6sINwQYQf+7DBBqEH8IMINPVVoJqmnrqqab38ghnkaZLMg+e890e2aaqty+bOoJ80Tx3+z6nPRPmjAy8fwg76yZaTaTf4UKFpBOqCbQQb+9VdBNvT/nzjab/6TaSBEfQIj6BEfvbenp4Ij6OPgiPo493bp1r/T1UIjpRx/ShcLhdYqkxTff9+q6TZHxf6jZHjeXyOzmR2eZ5EezZ1ZmzzPmbzeYReTM9JL8cce7fB///3dcf+wfDqLYtBximummnXEO3VfS/phrXYK6XulqqVKv2R4zCtnijRR7wU96bRoo8Ue91NCYQ1X/9siTIOd7S3/fsg8DtkHEL4b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56vXa9fZz3+znrVq9nP61UccdxsVGxTFVGb2KqqQpapiqq1qo40KYpio7qmKYqOo6umGxVLVMUxVxpsV/u2CI+7BEfwwmmCI+jjpHHVu4YV3c467tq7b7tgiPvOOmmmCI/3bTCI+1bBEfvvavu2mER/YIj7bXxERGhERERxxGhERHERERERERGhEREREREREREREREREREREfq7q6/CSqElCX6+uviIiI///////870RK0SQU7Nc7NF8lHd2VHVkP+48rm9i/1raXX5AjQZgziI7OZHZvMIvHmR0aZHdm2fZ5nz7Ns4i8mbM8ZQ6PM+z2R4wzhlDOZHZxEdnER4wZzI7NojxHzjTNv6emhbF8Q0Li9NNBp+nEO000LQaDpNU04tC402Lj07Qf57hhI95oo0amhI9pnjSPbR4o9576SPbmhMJHvKyjQke6PeJUCVDlZR7bPGaMEj3RowVI9thI9/2K7dN03dXtpN2IWh3sYxC1b7cMYTe+9VwxEKk3Tft032IX3+yV/f/+q/Bbi2SOgWtfkc3xcW/yOn3//8FeL+29Oru9VXbvSNGtuUg0brpw3VetUG0aLur1Tu9UjRX+RPW11Vbu7pVuF3Kr8L7e1NO7lHKRuzTharrdqq3cL5TcX8mF+tra/6dr2ub7OZj+1rW1ML1s52c38x9ra2vra/avZz/Gw2KYpiqqmKYqo2KYqPjurYqNimKYpaqONimKpimKqo02KN/wwtpHHSOOru2ER9HHVsER9poMLYIj/bbQYTCI+013YYTBEfRx0jjq2kcdXbBEfbaf4iOOIiI1iIiIiIiIiIiIiIiIjjiI4iIiIj93KjV3d/qsaqq/8ML/+IiIiP//////+P//Js6pj+P/+ACACD/8AEAEAARAP4ABAABAAAAAgAAAAABAwABAAAAEQMAAAEBAwABAAAAIgYAAAIBAwABAAAAAQAAAAMBAwABAAAABAAAAAYBAwABAAAAAAAAABEBBAAUAAAArjsAABIBAwABAAAAAQAAABUBAwABAAAAAQAAABYBAwABAAAAUgAAABcBBAAUAAAA/jsAABoBBQABAAAATjwAABsBBQABAAAAVjwAABwBAwABAAAAAQAAAB0BAgAHAAAAXjwAACgBAwABAAAAAgAAACkBAwACAAAAAQABAAAAAAAIAAAAgQQAACsKAAB0DQAADxEAAPgSAADAFgAA6xgAAOEaAAAcGwAAVxsAAOYdAACGIwAAQCUAAO8nAACpKwAAoi0AADQxAACANgAA1joAAHkEAACqBQAASQMAAJsDAADpAQAAyAMAACsCAAD2AQAAOwAAADsAAACPAgAAoAUAALoBAACvAgAAugMAAPkBAACSAwAATAUAAFYEAAAFAAAAAAAAGQAAIAAAAAAZAAAgAFBhZ2UgMQA=\"}]}  ";
//		JSONObject respJSON = JSONObject.fromObject(testStr);
		
		return respJSON;
	}
	
	public void writeToken(String token) throws Exception {
		
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("pantosToken");
		String preToken = prop.getProperty("pantos.ISECOMMERCE2.current_token");
		
//		OutputStream os = new FileOutputStream(new File("./props/pantosToken.properties"));
		OutputStream os = new FileOutputStream(new File(path+"pantosToken.properties"));
		
		prop.setProperty("pantos.ISECOMMERCE2.previous_token", preToken);
		prop.setProperty("pantos.ISECOMMERCE2.current_token", token);
		prop.store(os, "");
		
		os.close();
	}
	
	public String readToken() {
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("pantosToken");
		if(prop.getProperty("pantos.ISECOMMERCE2.current_token") == null) {
			return null;
		} else {
			return prop.getProperty("pantos.ISECOMMERCE2.current_token");
		}
	}
		
	public byte[] createRSValues(String soNo) throws Exception {
		
		String token = readToken();
		PropManager propMng = PropManager.getInstances();
		Properties prop = propMng.getProp("wmsConfirm");
		
		JSONObject listReqObj = new JSONObject();
		JSONObject listReqHeader = new JSONObject();
		JSONObject listReqBody = new JSONObject();
		
		listReqHeader.put(prop.getProperty("prop.proc0005.header.id1").trim(), "isecommerce");
		if(token.length()>0) {
			listReqHeader.put(prop.getProperty("prop.proc0005.header.id2").trim(), "");
		} else {
			listReqHeader.put(prop.getProperty("prop.proc0005.header.id2").trim(), "isecommerceKey");
		}
		listReqHeader.put(prop.getProperty("prop.proc0005.header.id3").trim(), "proc0005");
		listReqHeader.put(prop.getProperty("prop.proc0005.header.id4").trim(), "ISECOMMERCE2");
		listReqHeader.put(prop.getProperty("prop.proc0005.header.id5").trim(), "UTF-8");
		listReqHeader.put(prop.getProperty("prop.proc0005.header.id6").trim(), token);
		
		listReqObj.put("header", listReqHeader);
		Logger.debug("header create!");
		
		int j=1;
		while(prop.getProperty("prop.proc0005.body.id"+j)!=null) {
			if(prop.getProperty("prop.proc0005.body.id"+j).equals("soNo")) {
				listReqBody.put(prop.getProperty("prop.proc0005.body.id"+j), soNo);
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
