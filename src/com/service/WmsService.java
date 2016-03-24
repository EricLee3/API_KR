package com.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.service.command.connection.DataBaseManager;
import com.service.command.log.Logger;
import com.service.command.query.ExecQuery;


public class WmsService {
	
	private static WmsService instance = new WmsService();

	public static WmsService getInstance() {
		return instance;
	}

	private WmsService() {
		
	}
	
	@SuppressWarnings("unchecked")
	public String getApiRecvData(String dbmode, String api_div, String arrValue) throws SQLException, Exception {
		Connection 			conn	= null;
		CallableStatement	cstmt   = null;

		
		String result = "";
		String brand_cd = "";
		
		
		
		String[] arrParam = arrValue.split("\\|");
		String procName = "";
		String procStr = "";
		
		try {
			conn = DataBaseManager.getConnection(dbmode);
			
			if (api_div.equals("fact")) {
				//procName = "IF_RECV_FACT";
				procName = "IF_RECV_APITEST_FACT";
			} else if (api_div.equals("delivery")) {
				//procName = "IF_RECV_DELIVERY";
				procName = "IF_RECV_APITEST_DELIVERY";
			} else if (api_div.equals("item")) {
				//procName = "IF_RECV_ITEM";
				procName = "IF_RECV_APITEST_ITEM";
			} else if (api_div.equals("inorder")) {
				//procName = "IF_RECV_INORDER_PLAN";
				procName = "IF_RECV_APITEST_INORDER_PLAN";
			} else if (api_div.equals("outorder")) {
				//procName = "IF_RECV_OUTORDER_PLAN";
				procName = "IF_RECV_APITEST_OUTORDER_PLAN";
			} else if (api_div.equals("etcorder")) {
				//procName = "IF_RECV_ETCORDER_PLAN";
				procName = "IF_RECV_APITEST_ETCORDER_PLAN";
			}
			
			//프로시저 호출 문자열 만들기..
			procStr = "call "+ procName +" (?,?";
			for (int i = 0; i < arrParam.length; i++) {
				procStr = procStr +",?";
			}
			procStr = procStr +")";
			
			cstmt = conn.prepareCall("{"+ procStr +"}");
				
			cstmt.registerOutParameter(1, Types.VARCHAR);
			cstmt.registerOutParameter(2, Types.VARCHAR);

			//브랜드코드 (BRAND_CD 구하기)
			brand_cd = arrParam[1];
			
			int j = 3;
			for (int i = 0; i < arrParam.length; i++) {
				cstmt.setString(j, arrParam[i]);
				j ++;
			}

			cstmt.executeUpdate();
            conn.commit();
            
            
            String errcode = cstmt.getString(1);
            String errmsg  = cstmt.getString(2);
			
            if (errcode.equals("F")) {	
            	result = "###Error###:"+ errmsg;
            	setErrorRecvLog(dbmode, api_div, "send", brand_cd, errmsg);
            } else {
            	result = "SUCCESS";
            }
			
			
		} catch (Exception e) {
			conn.rollback();
			result = "###Error###:"+procName+" Error sql:\n"+e.toString();
			setErrorRecvLog(dbmode, api_div, "send", brand_cd, e.toString());
			
			/*
			try {
				conn.rollback();
			} catch (SQLException se) {
				
			}
			*/
		} finally {
			DataBaseManager.close(conn, dbmode);
			if (cstmt != null) try{ cstmt.close(); cstmt = null; } catch (Exception _ex) {}finally{cstmt = null;}
			if (conn != null) try{ conn.close(); conn = null; }catch(Exception e){}finally{conn = null;}
		}
		
		return result;
	}
	
	
	public String getCheckingResult(String dbmode, String api_div, String brand_cd, String chkStr1, String chkStr2, String chkStr3, String chkStr4) throws SQLException, Exception {
		String methodName="com.service.WmsService.getCheckingResult()";
		Logger.debug(methodName);
		
		Connection 			conn		= null;
		ResultSet			rs			= null;
		StringBuffer 		sqlBuffer	= new StringBuffer(500);       //  주 쿼리문
		PreparedStatement	pstmt		= null;
		
		String result = "";
		String error_Str = "";
		
		try {
			conn = DataBaseManager.getConnection(dbmode);
			
			if (api_div.equals("fact")) {
				sqlBuffer.append("SELECT * FROM CMFACT WHERE BRAND_CD='"+ brand_cd +"' AND FACT_CD='"+ chkStr1 +"'");
				error_Str = "FAIL! 공급처코드 중복";
				
			} else if (api_div.equals("delivery")) {
				sqlBuffer.append("SELECT * FROM CMDELIVERY WHERE BRAND_CD='"+ brand_cd +"' AND DELIVERY_CD='"+ chkStr1 +"'");
				error_Str = "FAIL! 배송처코드 중복";
				
			} else if (api_div.equals("item")) {
				sqlBuffer.append("SELECT * FROM CMITEM WHERE BRAND_CD='"+ brand_cd +"' AND ITEM_CD='"+ chkStr1 +"'");
				error_Str = "FAIL! 상품코드 중복";
				
			} else if (api_div.equals("inorder")) {
				sqlBuffer.append("SELECT * FROM LI010NM 								\n");
				sqlBuffer.append("WHERE CENTER_CD IN (									\n");
				sqlBuffer.append("						SELECT DISTINCT IF_OWNER_CD		\n");
				sqlBuffer.append("                      FROM EDIINTERFACE				\n");
				sqlBuffer.append(" 						WHERE IF_GRP='CENTER'			\n");
				sqlBuffer.append("						AND IF_CUST_CD='"+ chkStr1 +"'	\n");
				sqlBuffer.append("					)									\n");
				sqlBuffer.append("AND BRAND_CD='"+ brand_cd +"'							\n");
				sqlBuffer.append("AND BRAND_DATE='"+ chkStr2 +"'						\n");
				sqlBuffer.append("AND BRAND_NO='"+ chkStr3 +"'							\n");
				error_Str = "FAIL! 입고/반출 전표일자,전표번호 중복";
				
			} else if (api_div.equals("outorder")) {
				sqlBuffer.append("SELECT * FROM LO010ND 								\n");
				sqlBuffer.append("WHERE CENTER_CD IN (									\n");
				sqlBuffer.append("						SELECT DISTINCT IF_OWNER_CD		\n");
				sqlBuffer.append("                      FROM EDIINTERFACE				\n");
				sqlBuffer.append(" 						WHERE IF_GRP='CENTER'			\n");
				sqlBuffer.append("						AND IF_CUST_CD='"+ chkStr1 +"'	\n");
				sqlBuffer.append("					)									\n");
				sqlBuffer.append("AND BRAND_CD='"+ brand_cd +"'							\n");
				sqlBuffer.append("AND BRAND_DATE='"+ chkStr2 +"'						\n");
				sqlBuffer.append("AND BRAND_NO='"+ chkStr3 +"'							\n");
				error_Str = "FAIL! 출고/반입 전표일자,전표번호 중복";
				
			} else if (api_div.equals("etcorder")) {
				sqlBuffer.append("SELECT * FROM LC010NM 								\n");
				sqlBuffer.append("WHERE CENTER_CD IN (									\n");
				sqlBuffer.append("						SELECT DISTINCT IF_OWNER_CD		\n");
				sqlBuffer.append("                      FROM EDIINTERFACE				\n");
				sqlBuffer.append(" 						WHERE IF_GRP='CENTER'			\n");
				sqlBuffer.append("						AND IF_CUST_CD='"+ chkStr1 +"'	\n");
				sqlBuffer.append("					)									\n");
				sqlBuffer.append("AND BRAND_CD='"+ brand_cd +"'							\n");
				sqlBuffer.append("AND BRAND_DATE='"+ chkStr2 +"'						\n");
				sqlBuffer.append("AND BRAND_NO='"+ chkStr3 +"'							\n");
				error_Str = "FAIL! 기타입출고 전표일자,전표번호 중복";
				
			}
			
			//Logger.debug(sqlBuffer.toString());
			
			pstmt = conn.prepareStatement(sqlBuffer.toString());
			rs = pstmt.executeQuery();
			
			if (rs.next())
			{
				result = error_Str;
			}
		
		} catch (Exception e) {
			result = "###Error###: Error sql:\n"+e.toString();
			setErrorRecvLog(dbmode, api_div, "send", brand_cd, e.toString());
		} finally {
			DataBaseManager.close(conn, dbmode);
			if (rs !=null) try{ rs.close(); rs = null; }catch(Exception e){}finally{rs = null;}
			if (pstmt != null) try{ pstmt.close(); pstmt = null; }catch(Exception e){}finally{pstmt = null;}
			if (conn != null) try{ conn.close(); conn = null; }catch(Exception e){}finally{conn = null;}
		}
		
		return result;
	}
	
	
	public List<Object> getConfirmData(String dbmode, String api_div, String brand_cd, String whcd, String ord_date_s, String ord_date_e, String brand_no) throws SQLException, Exception {
		String methodName="com.service.WmsService.getConfirmData()";
		Logger.debug(methodName);
		
		Connection 			conn		= null;
		StringBuffer 		sqlBuffer	= new StringBuffer(2000);       //  주 쿼리문
		
		ArrayList<Object> params = null;
		ResultSetHandler rsh = null;
		List<Object> result = null;
		
		try {
			conn = DataBaseManager.getConnection(dbmode);
			
			if (api_div.equals("inorder")) {			//입고/반출
				sqlBuffer.append("SELECT  L1.CENTER_CD																												\n");
				sqlBuffer.append("        ,L1.BRAND_CD																												\n");
				sqlBuffer.append("        ,TO_CHAR(L1.ORDER_DATE,'YYYY-MM-DD') AS ORDER_DATE																		\n");
				sqlBuffer.append("        ,L1.INOUT_CD																												\n");
				sqlBuffer.append("        ,C1.CODE_NM AS INOUT_CD_NM																								\n");
				sqlBuffer.append("        ,TO_CHAR(MAX(NVL(L1.INBOUND_DATE,'')),'YYYY-MM-DD') AS INOUTBOUND_DATE													\n");
				sqlBuffer.append("        ,MAX(L1.INBOUND_STATE) AS INOUTBOUND_STATE																				\n");
				sqlBuffer.append("        ,TO_CHAR(NVL(L1.BRAND_DATE,''),'YYYY-MM-DD') AS BRAND_DATE																\n");
				sqlBuffer.append("        ,L1.BRAND_NO																												\n");
				sqlBuffer.append("        ,MAX(NVL(L1.CONFIRM_YN,'')) AS CONFIRM_YN																					\n");
				sqlBuffer.append("        ,TO_CHAR(MAX(NVL(L1.CONFIRM_DATETIME,'')),'YYYY-MM-DD') AS CONFIRM_DATETIME												\n");
				sqlBuffer.append("FROM    (																															\n");
				sqlBuffer.append("        SELECT  M1.CENTER_CD																										\n");
				sqlBuffer.append("                ,M1.BRAND_CD																										\n");
				sqlBuffer.append("                ,M1.ORDER_DATE																									\n");
				sqlBuffer.append("                ,M1.ORDER_NO																										\n");
				sqlBuffer.append("                ,M1.INOUT_CD																										\n");
				sqlBuffer.append("                ,TO_DATE('') AS INBOUND_DATE																						\n");
				sqlBuffer.append("                ,'' AS INBOUND_NO																									\n");
				sqlBuffer.append("                ,M1.INBOUND_STATE																									\n");
				sqlBuffer.append("                ,M1.BRAND_DATE																									\n");
				sqlBuffer.append("                ,M1.BRAND_NO																										\n");
				sqlBuffer.append("                ,'' AS CONFIRM_YN																									\n");
				sqlBuffer.append("                ,TO_DATE('') AS CONFIRM_DATETIME																					\n");
				sqlBuffer.append("        FROM    LI010NM         M1																								\n");
				sqlBuffer.append("                JOIN    LI010ND M2  ON  M2.CENTER_CD=M1.CENTER_CD																	\n");
				sqlBuffer.append("                                    AND M2.BRAND_CD=M1.BRAND_CD																	\n");
				sqlBuffer.append("                                    AND M2.ORDER_DATE=M1.ORDER_DATE																\n");
				sqlBuffer.append("                                    AND M2.ORDER_NO=M1.ORDER_NO																	\n");
				sqlBuffer.append("        WHERE   M1.CENTER_CD=(SELECT DISTINCT IF_OWNER_CD FROM EDIINTERFACE WHERE IF_GRP='CENTER' AND IF_CUST_CD='"+ whcd +"')	\n");
				sqlBuffer.append("        AND     M1.BRAND_CD='"+ brand_cd +"'																						\n");
				sqlBuffer.append("        AND     M1.ORDER_DATE BETWEEN '"+ ord_date_s +"' AND '"+ ord_date_e +"'													\n");
				if (!brand_no.equals("")) {
					sqlBuffer.append("        AND     M1.BRAND_NO='"+ brand_no +"'																					\n");
				}
				sqlBuffer.append("        UNION ALL																													\n");
				sqlBuffer.append("        SELECT  M1.CENTER_CD																										\n");
				sqlBuffer.append("                ,M1.BRAND_CD																										\n");
				sqlBuffer.append("                ,M1.ORDER_DATE																									\n");
				sqlBuffer.append("                ,M1.ORDER_NO																										\n");
				sqlBuffer.append("                ,M1.INOUT_CD																										\n");
				sqlBuffer.append("                ,M1.INBOUND_DATE																									\n");
				sqlBuffer.append("                ,M1.INBOUND_NO																									\n");
				sqlBuffer.append("                ,M1.INBOUND_STATE																									\n");
				sqlBuffer.append("                ,M1.BRAND_DATE																									\n");
				sqlBuffer.append("                ,M1.BRAND_NO																										\n");
				sqlBuffer.append("                ,M3.CONFIRM_YN																									\n");
				sqlBuffer.append("                ,M3.CONFIRM_DATETIME																								\n");
				sqlBuffer.append("        FROM    LI020NM         M1																								\n");
				sqlBuffer.append("                JOIN    LI020ND M2  ON  M2.CENTER_CD=M1.CENTER_CD																	\n");
				sqlBuffer.append("                                    AND M2.BRAND_CD=M1.BRAND_CD																	\n");
				sqlBuffer.append("                                    AND M2.INBOUND_DATE=M1.INBOUND_DATE															\n");
				sqlBuffer.append("                                    AND M2.INBOUND_NO=M1.INBOUND_NO																\n");
				sqlBuffer.append("                JOIN    LI030NM M3  ON  M3.CENTER_CD=M1.CENTER_CD																	\n");
				sqlBuffer.append("                                    AND M3.BRAND_CD=M1.BRAND_CD																	\n");
				sqlBuffer.append("                                    AND M3.INBOUND_DATE=M1.INBOUND_DATE															\n");
				sqlBuffer.append("                                    AND M3.INBOUND_NO=M1.INBOUND_NO                                    							\n");
				sqlBuffer.append("        WHERE   M1.CENTER_CD=(SELECT DISTINCT IF_OWNER_CD FROM EDIINTERFACE WHERE IF_GRP='CENTER' AND IF_CUST_CD='"+ whcd +"')	\n");
				sqlBuffer.append("        AND     M1.BRAND_CD='"+ brand_cd +"'																						\n");
				sqlBuffer.append("        AND     M1.ORDER_DATE BETWEEN '"+ ord_date_s +"' AND '"+ ord_date_e +"'													\n");
				if (!brand_no.equals("")) {
					sqlBuffer.append("        AND     M1.BRAND_NO='"+ brand_no +"'																					\n");
				}
				sqlBuffer.append("        ) L1																														\n");
				sqlBuffer.append("        JOIN VCMCODE    C1  ON  C1.CODE_GRP='LDIV03'																				\n");
				sqlBuffer.append("                            AND C1.SUB_CD IN ('E1','E2')																			\n");
				sqlBuffer.append("                            AND C1.CODE_CD=L1.INOUT_CD																			\n");
				sqlBuffer.append("GROUP BY L1.CENTER_CD																												\n");
				sqlBuffer.append("        ,L1.BRAND_CD																												\n");
				sqlBuffer.append("        ,L1.ORDER_DATE																											\n");
				sqlBuffer.append("        ,L1.INOUT_CD																												\n");
				sqlBuffer.append("        ,L1.BRAND_DATE																											\n");
				sqlBuffer.append("        ,L1.BRAND_NO																												\n");
				sqlBuffer.append("        ,C1.CODE_NM																												\n");
				
			} else if (api_div.equals("outorder")) {	//출고/반입
				sqlBuffer.append("SELECT  L1.CENTER_CD																												\n");
				sqlBuffer.append("        ,L1.BRAND_CD																												\n");
				sqlBuffer.append("        ,TO_CHAR(L1.ORDER_DATE,'YYYY-MM-DD') AS ORDER_DATE																		\n");
				sqlBuffer.append("        ,L1.INOUT_CD																												\n");
				sqlBuffer.append("        ,C1.CODE_NM AS INOUT_CD_NM  																								\n");
				sqlBuffer.append("        ,TO_CHAR(MAX(NVL(L1.OUTBOUND_DATE,'')),'YYYY-MM-DD') AS INOUTBOUND_DATE													\n");
				sqlBuffer.append("        ,MAX(L1.OUTBOUND_STATE) AS INOUTBOUND_STATE																					\n");
				sqlBuffer.append("        ,TO_CHAR(NVL(L1.BRAND_DATE,''),'YYYY-MM-DD') AS BRAND_DATE																\n");
				sqlBuffer.append("        ,L1.BRAND_NO																												\n");
				sqlBuffer.append("        ,MAX(NVL(L1.CONFIRM_YN,'')) AS CONFIRM_YN																					\n");
				sqlBuffer.append("        ,TO_CHAR(MAX(NVL(L1.CONFIRM_DATETIME,'')),'YYYY-MM-DD') AS CONFIRM_DATETIME												\n");
				sqlBuffer.append("FROM    (																															\n");
				sqlBuffer.append("        SELECT  M1.CENTER_CD																										\n");
				sqlBuffer.append("                ,M1.BRAND_CD																										\n");
				sqlBuffer.append("                ,M1.ORDER_DATE																									\n");
				sqlBuffer.append("                ,M1.ORDER_NO																										\n");
				sqlBuffer.append("                ,M1.INOUT_CD																										\n");
				sqlBuffer.append("                ,TO_DATE('') AS OUTBOUND_DATE																						\n");
				sqlBuffer.append("                ,'' AS OUTBOUND_NO																								\n");
				sqlBuffer.append("                ,M1.OUTBOUND_STATE																								\n");
				sqlBuffer.append("                ,M2.BRAND_DATE																									\n");
				sqlBuffer.append("                ,M2.BRAND_NO																										\n");
				sqlBuffer.append("                ,'' AS CONFIRM_YN																									\n");
				sqlBuffer.append("                ,TO_DATE('') AS CONFIRM_DATETIME																					\n");
				sqlBuffer.append("        FROM    LO010NM         M1																								\n");
				sqlBuffer.append("                JOIN    LO010ND M2  ON  M2.CENTER_CD=M1.CENTER_CD																	\n");
				sqlBuffer.append("                                    AND M2.BRAND_CD=M1.BRAND_CD																	\n");
				sqlBuffer.append("                                    AND M2.ORDER_DATE=M1.ORDER_DATE																\n");
				sqlBuffer.append("                                    AND M2.ORDER_NO=M1.ORDER_NO																	\n");
				sqlBuffer.append("        WHERE   M1.CENTER_CD=(SELECT DISTINCT IF_OWNER_CD FROM EDIINTERFACE WHERE IF_GRP='CENTER' AND IF_CUST_CD='"+ whcd +"')	\n");
				sqlBuffer.append("        AND     M1.BRAND_CD='"+ brand_cd +"'																						\n");
				sqlBuffer.append("        AND     M2.ORDER_DATE BETWEEN '"+ ord_date_s +"' AND '"+ ord_date_e +"'													\n");
				if (!brand_no.equals("")) {
					sqlBuffer.append("        AND     M2.BRAND_NO='"+ brand_no +"'																					\n");
				}
				sqlBuffer.append("        UNION ALL																													\n");
				sqlBuffer.append("        SELECT  M1.CENTER_CD																										\n");
				sqlBuffer.append("                ,M1.BRAND_CD																										\n");
				sqlBuffer.append("                ,M1.ORDER_DATE																									\n");
				sqlBuffer.append("                ,M1.ORDER_NO																										\n");
				sqlBuffer.append("                ,M1.INOUT_CD																										\n");
				sqlBuffer.append("                ,M1.OUTBOUND_DATE																									\n");
				sqlBuffer.append("                ,M1.OUTBOUND_NO																									\n");
				sqlBuffer.append("                ,M1.OUTBOUND_STATE																								\n");
				sqlBuffer.append("                ,M2.BRAND_DATE																									\n");
				sqlBuffer.append("                ,M2.BRAND_NO																										\n");
				sqlBuffer.append("                ,M3.CONFIRM_YN																									\n");
				sqlBuffer.append("                ,M3.CONFIRM_DATETIME																								\n");
				sqlBuffer.append("        FROM    LO020NM         M1																								\n");
				sqlBuffer.append("                JOIN    LO020ND M2  ON  M2.CENTER_CD=M1.CENTER_CD																	\n");
				sqlBuffer.append("                                    AND M2.BRAND_CD=M1.BRAND_CD																	\n");
				sqlBuffer.append("                                    AND M2.OUTBOUND_DATE=M1.OUTBOUND_DATE															\n");
				sqlBuffer.append("                                    AND M2.OUTBOUND_NO=M1.OUTBOUND_NO																\n");
				sqlBuffer.append("                JOIN    LO030NM M3  ON  M3.CENTER_CD=M1.CENTER_CD																	\n");
				sqlBuffer.append("                                    AND M3.BRAND_CD=M1.BRAND_CD																	\n");
				sqlBuffer.append("                                    AND M3.OUTBOUND_DATE=M1.OUTBOUND_DATE															\n");
				sqlBuffer.append("                                    AND M3.OUTBOUND_NO=M1.OUTBOUND_NO                                    							\n");
				sqlBuffer.append("                                    AND M3.LINE_NO=M2.LINE_NO                                                   					\n");
				sqlBuffer.append("        WHERE   M1.CENTER_CD=(SELECT DISTINCT IF_OWNER_CD FROM EDIINTERFACE WHERE IF_GRP='CENTER' AND IF_CUST_CD='"+ whcd +"')	\n");
				sqlBuffer.append("        AND     M1.BRAND_CD='"+ brand_cd +"'																						\n");
				sqlBuffer.append("        AND     M1.ORDER_DATE BETWEEN '"+ ord_date_s +"' AND '"+ ord_date_e +"'													\n");
				if (!brand_no.equals("")) {
					sqlBuffer.append("        AND     M2.BRAND_NO='"+ brand_no +"'																					\n");
				}
				sqlBuffer.append("        ) L1																														\n");
				sqlBuffer.append("        JOIN VCMCODE    C1  ON  C1.CODE_GRP='LDIV03'																				\n");
				sqlBuffer.append("                            AND C1.SUB_CD IN ('D1','D2')																			\n");
				sqlBuffer.append("                            AND C1.CODE_CD=L1.INOUT_CD																			\n");
				sqlBuffer.append("GROUP BY L1.CENTER_CD																												\n");
				sqlBuffer.append("        ,L1.BRAND_CD																												\n");
				sqlBuffer.append("        ,L1.ORDER_DATE																											\n");
				sqlBuffer.append("        ,L1.INOUT_CD																												\n");
				sqlBuffer.append("        ,L1.BRAND_DATE																											\n");
				sqlBuffer.append("        ,L1.BRAND_NO   																											\n");
				sqlBuffer.append("        ,C1.CODE_NM 																												\n");
				
				
			} else if (api_div.equals("etcorder")) {	//기타입출고
				sqlBuffer.append("SELECT  M1.CENTER_CD																												\n");
				sqlBuffer.append("        ,M1.BRAND_CD																												\n");
				sqlBuffer.append("        ,TO_CHAR(M1.ETC_DATE,'YYYY-MM-DD') AS ORDER_DATE																			\n");
				sqlBuffer.append("        ,M1.INOUT_CD																												\n");
				sqlBuffer.append("        ,C1.CODE_NM AS INOUT_CD_NM  																								\n");
				sqlBuffer.append("        ,'' AS INOUTBOUND_DATE																									\n");
				sqlBuffer.append("        ,'' AS INOUTBOUND_STATE																									\n");
				sqlBuffer.append("        ,'' AS BRAND_DATE																											\n");
				sqlBuffer.append("        ,'' AS BRAND_NO																											\n");
				sqlBuffer.append("        ,NVL(M1.CONFIRM_YN,'') AS CONFIRM_YN																						\n");
				sqlBuffer.append("        ,TO_CHAR(NVL(M1.CONFIRM_DATETIME,''),'YYYY-MM-DD') AS CONFIRM_DATETIME													\n");
				sqlBuffer.append("FROM    LC010NM         M1																										\n");
				sqlBuffer.append("        JOIN LC010ND    M2  ON  M2.CENTER_CD=M1.CENTER_CD																			\n");
				sqlBuffer.append("                            AND M2.BRAND_CD=M1.BRAND_CD																			\n");
				sqlBuffer.append("                            AND M2.ETC_DATE=M1.ETC_DATE																			\n");
				sqlBuffer.append("                            AND M2.ETC_NO=M1.ETC_NO																				\n");
				sqlBuffer.append("        JOIN VCMCODE    C1  ON  C1.CODE_GRP='LDIV03'																				\n");
				sqlBuffer.append("                            AND C1.SUB_CD IN ('D9','E9')																			\n");
				sqlBuffer.append("                            AND C1.CODE_CD=M1.INOUT_CD																			\n");
				sqlBuffer.append("WHERE   M1.CENTER_CD=(SELECT DISTINCT IF_OWNER_CD FROM EDIINTERFACE WHERE IF_GRP='CENTER' AND IF_CUST_CD='"+ whcd +"')			\n");
				sqlBuffer.append("AND     M1.BRAND_CD='"+ brand_cd +"'																								\n");
				sqlBuffer.append("AND     M1.ETC_DATE BETWEEN '"+ ord_date_s +"' AND '"+ ord_date_e +"'																\n");
				if (!brand_no.equals("")) {
					sqlBuffer.append("AND     M1.BRAND_NO='"+ brand_no +"'																							\n");
				}
				sqlBuffer.append("GROUP BY M1.CENTER_CD																												\n");
				sqlBuffer.append("        ,M1.BRAND_CD																												\n");
				sqlBuffer.append("        ,M1.ETC_DATE																												\n");
				sqlBuffer.append("        ,M1.INOUT_CD																												\n");
				sqlBuffer.append("        ,M1.CONFIRM_YN																											\n");
				sqlBuffer.append("        ,M1.CONFIRM_DATETIME																										\n");
				sqlBuffer.append("        ,C1.CODE_NM																												\n");
			}
			
			//Logger.debug(sqlBuffer.toString());
			
			rsh = new MapListHandler();
			params = new ArrayList<Object>();
			
			result = (List<Object>)ExecQuery.query(conn, sqlBuffer.toString(), params, rsh);
			
		} catch (Exception e) {
			setErrorRecvLog(dbmode, api_div, "confirm", brand_cd, e.toString());
			//Logger.error(e);
			//throw e;
		} finally {
			DataBaseManager.close(conn, dbmode);
			if (conn != null) try{ conn.close(); conn = null; }catch(Exception e){}finally{conn = null;}
			
		}
		
		return result;
	}
	
	
	public String getApiRecvRequest(String dbmode, String request_div, String last_yn, String brand_cd, String whcd, String brand_date, String brand_no, String cancel_div) throws SQLException, Exception {
		String methodName="com.service.WmsService.getApiRecvCancel()";
		Logger.debug(methodName);
		
		Connection 			conn 		= null;
		CallableStatement	cstmt		= null;
		PreparedStatement 	pstmt 		= null;
		ResultSet         	rs    		= null;
		StringBuffer 		sqlBuffer	= new StringBuffer(500);       //  주 쿼리문
		
		String result = "";
		String center_cd = "";
		String api_div = "";
		
		try {
			conn = DataBaseManager.getConnection(dbmode);
			
			if (request_div.equals("cancel")) {			//수신취소요청
				if (cancel_div.equals("1")) {
					api_div = "inorder";
				} else if (cancel_div.equals("2")) {
					api_div = "outorder";
				} else if (cancel_div.equals("3")) {
					api_div = "etcorder";
				}
			} else if (request_div.equals("finish")) {	//입고예정 종결 처리 요청
				api_div = "inorder";
			}
				
			
			sqlBuffer.append("SELECT DISTINCT IF_OWNER_CD AS CENTER_CD FROM EDIINTERFACE WHERE IF_GRP='CENTER' AND IF_CUST_CD='"+ whcd +"' ");
			pstmt = conn.prepareStatement(sqlBuffer.toString());
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				center_cd = rs.getString("CENTER_CD");
			}
			rs.close();
			
			if (center_cd.equals("")) {
				result = "FAIL! 물류센터 코드 누락";
			} else {
				if (request_div.equals("cancel")) {			//수신취소요청
					cstmt = conn.prepareCall("{call IF_RECV_CANCEL (?,?,?,?,?,?,?,?)}");
				} else if (request_div.equals("finish")) {	//입고예정 종결 처리 요청
					cstmt = conn.prepareCall("{call IF_RECV_INORDER_FINISH (?,?,?,?,?,?,?)}");
				}
				
				cstmt.registerOutParameter(1, Types.VARCHAR);
				cstmt.registerOutParameter(2, Types.VARCHAR);
				cstmt.setString(3, last_yn);
				cstmt.setString(4, brand_cd);
				cstmt.setString(5, center_cd);
				cstmt.setString(6, brand_date);
				cstmt.setString(7, brand_no);
				
				if (request_div.equals("cancel")) {			//수신취소요청
					cstmt.setString(8, cancel_div);
				} 
				
				cstmt.executeUpdate();
	            conn.commit();
	            
	            
	            String errcode = cstmt.getString(1);
	            String errmsg  = cstmt.getString(2);
				
	            if (errcode.equals("F")) {	//프로시저 오류일때..
	            	result = "###Error###:"+ errmsg;
	            	setErrorRecvLog(dbmode, api_div, request_div, brand_cd, errmsg);
	            } else {
	            	result = "SUCCESS";
	            }
			}
				
	         
			
		} catch (Exception e) {
			conn.rollback();
			result = "###Error###: Error sql:\n"+e.toString();
			setErrorRecvLog(dbmode, api_div, request_div, brand_cd, e.toString());
		} finally {
			DataBaseManager.close(conn, dbmode);
			if (rs != null) try{ rs.close(); rs = null; }catch(Exception e){}finally{rs=null;}   
			if (pstmt != null) try{ pstmt.close(); pstmt = null; }catch(Exception e){}finally{pstmt=null;}   
			if (cstmt != null) try{ cstmt.close(); cstmt = null; } catch (Exception _ex) {}finally{cstmt = null;}
			if (conn != null) try{ conn.close(); conn = null; }catch(Exception e){}finally{conn = null;}
			
		}
		
		return result;
	}
	
	
	/**
	 * 에러 난 경우 에러를 DB에 INSERT
	 */
	public void setErrorRecvLog(String dbmode, String api_div, String link_div, String brand_cd, String error_msg)  throws Exception {
		String methodName="com.service.WmsService.setErrorRecvLog()";
		Logger.debug(methodName);
		
		Connection 			conn 		= null;
		String 				sqlBuffer	= null;       //  주 쿼리문
		PreparedStatement 	pstmt    	= null;
		
		try {
			conn = DataBaseManager.getConnection(dbmode);
			
			//Logger.debug("brand_cd="+brand_cd);
			//Logger.debug("api_div="+api_div);
			//Logger.debug("error_msg="+error_msg);
			
			sqlBuffer = "INSERT INTO EDIAPILINKERROR_LOG (BRAND_CD,API_DIV,LINK_DIV,ERROR_MSG,REG_DATE,REG_DATETIME) \n" +
						"VALUES (?,?,?,?,TO_CHAR(SYSDATE,'YYYY-MM-DD'),TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS'))";
			pstmt = conn.prepareStatement(sqlBuffer.toString());
			pstmt.setString(1,brand_cd);
            pstmt.setString(2,api_div);
            pstmt.setString(3,link_div);
            pstmt.setString(4,error_msg);
            pstmt.executeUpdate();
            
            
		} catch (Exception e) {
			
			try {
				conn.rollback();
			} catch (SQLException se) {
			}
			Logger.error(e);
			throw e; 
			
		} finally {
			DataBaseManager.close(conn, dbmode);
			if (pstmt != null) try{ pstmt.close(); pstmt = null;  } catch (Exception _ex) {}finally{pstmt=null;}  
			if(conn != null) conn.close();
		}
	}
	
	
}
