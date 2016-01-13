package com.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import com.service.command.log.Logger;


public class PantosTokenMgr {	
	public static void main(String[] args) {
		Logger.debug("************************************** START **************************************");
		System.out.println("Start!");
		
		String startPantosMgr ="2015-12-19 04:00:00";
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar cal = Calendar.getInstance();
		Timer t1 = new Timer(false);
		try{
		if("init".equals(args[0])) {
			Date startExpressDt = transFormat.parse(startPantosMgr);
//			cal.set(2015,3,27,21,30,0);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 30);
//			cal.set(Calendar.SECOND, 0);
			t1.schedule(new TaskPantosToken(), startExpressDt, 86400000);
		} else if("exec".equals(args[0])) {
			t1.schedule(new TaskPantosToken(), 1000);
		}else {
			Logger.debug("[ERROR] init failed!");
		}
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("End!");
		Logger.debug("************************************** PANTOS INSPECT SERVICE END **************************************");
	}
}
