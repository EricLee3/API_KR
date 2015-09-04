package com.main;

import java.util.Calendar;
import java.util.Timer;

import com.service.command.log.Logger;


public class PantosTokenMgr {	
	public static void main(String[] args) {
		Logger.debug("************************************** START **************************************");
		System.out.println("Start!");
		Calendar cal = Calendar.getInstance();
		Timer t1 = new Timer(false);
		if("init".equals(args[0])) {
//			cal.set(2015,3,27,21,30,0);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 30);
			cal.set(Calendar.SECOND, 0);
			
			t1.schedule(new TaskPantosToken(), cal.getTime(), 86400000);
		} else {
			Logger.debug("[ERROR] init failed!");
		}
		System.out.println("End!");
		Logger.debug("************************************** PANTOS INSPECT SERVICE END **************************************");
	}
}
