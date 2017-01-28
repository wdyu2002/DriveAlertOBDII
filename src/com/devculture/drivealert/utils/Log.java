package com.devculture.drivealert.utils;

import java.util.Vector;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.gui.components.UILabel;
import com.devculture.drivealert.notification.Notification;

public class Log implements Globals {
	private final static int MAX_LOG_MESSAGES = 75;
	private static Log mInstance = null;
	private final Vector mLabels = new Vector();
	
	private static int mDebug 												= DEBUG_NONE;
	
	public static void addDebugFlags(int flags) {
		mDebug |= flags;
	}
	
	public static void removeDebugFlags(int flags) {
		mDebug &= ~flags;
	}
	
	public static boolean isFlagsOn(int flag) {
		return (mDebug & flag) == flag;
	}
	
	public static Log getInstance() {
		if(mInstance == null) {
			mInstance = new Log();
		}
		return mInstance;
	}
	
	public static void logSettings(String str) {
		if((mDebug & DEBUG_SETTINGS_EVENTS) != 0) {
			log(str);
		}
	}
	
	public static void logSMS(String str) {
		if((mDebug & DEBUG_SMS_EVENTS) != 0) {
			log(str);
		}
	}
	
	public static void logOBDError(String str, Exception ex) {
		if((mDebug & DEBUG_OBD_ERRORS) != 0) {
			log(str);
			log(ex);
		}
	}
	
	public static void logOBDEvent(String str) {
		if((mDebug & DEBUG_OBD_EVENTS) != 0) {
			log(str);
		}
	}
	
	public static void logInterrupt(String str) {
		if((mDebug & DEBUG_INTERRUPT_EVENTS) != 0) {
			log(str);
		}
	}
	
	public static void logDeviceQuery(String str) {
		if((mDebug & DEBUG_DEVICE_QUERY_EVENTS) != 0) {
			log(str);
		}
	}
	
	public static void logSys(String str) {
		if((mDebug & DEBUG_SYSTEM_EVENTS) != 0) {
			log(str);
		}
	}
	
	public static void logReq(String str) {
		if((mDebug & DEBUG_REQUEST) != 0) {
			log(str);
		}
	}

	public static void logResp(String str) {
		if((mDebug & DEBUG_RESPONSE) != 0) {
			log(str);
		}
	}
	
	public static void log(String str) {
		getInstance()._log(str);
	}
	
	public static void log(Exception ex) {
		getInstance()._log(ex.getClass().toString());
		getInstance()._log(ex.getMessage());
	}
	
	private void _log(String msg) {
		// validity check
		if(msg == null) {
			return;
		}
		
		// print
		System.out.println(msg);
		// store
		add(new UILabel(msg, 240 /* hardcoded screen width */));
	}
	
	public int count() {
		int size = 0;
		synchronized(mLabels) {
			size = mLabels.size();
		}
		return size;
	}

	public UILabel[] getLabelsArrayCopy() {
		synchronized(mLabels) {
			UILabel[] data = new UILabel[mLabels.size()];
			mLabels.copyInto(data);
			return data;
		}
	}
	
	public void clear() {
		synchronized(mLabels) {
			mLabels.removeAllElements();
		}
		Notification.postNotification(NOTIFICATION_LOG_UPDATED, getLabelsArrayCopy());
	}
	
	protected void add(Object data) {
		synchronized(mLabels) {
			if(mLabels.size() == MAX_LOG_MESSAGES) {
				mLabels.removeElementAt(0);
			}
			mLabels.addElement(data);
		}
		Notification.postNotification(NOTIFICATION_LOG_UPDATED, getLabelsArrayCopy());
	}
}
