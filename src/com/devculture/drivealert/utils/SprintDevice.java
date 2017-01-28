package com.devculture.drivealert.utils;

import com.sprintpcs.util.System;

public class SprintDevice {
	
	// device capabilities flag & bits
	private static byte DEVICE_CAPABILITIES = 0;
	private static byte SPRINT_API_10 = 1<<0;
	private static byte SPRINT_API_20 = 1<<1;
	private static byte SPRINT_API_21 = 1<<2;
	
	// query result
	public static final int INVALID = -1;
	public static final int FALSE = 0;
	public static final int TRUE = 1;
	
	// API 10 is pretty much useless
	public static boolean supportsSprintApi10() {
		return (getDeviceCapabilities() & SPRINT_API_10) != 0;
	}
	
	// API 20 is used for getSystemState
	public static boolean supportsSprintApi20() {
		return (getDeviceCapabilities() & SPRINT_API_20) != 0;
	}
	
	// API 21 is used for setSystemSetting
	public static boolean supportsSprintApi21() {
		return (getDeviceCapabilities() & SPRINT_API_21) != 0;
	}
	
	private static byte getDeviceCapabilities() {
		if(DEVICE_CAPABILITIES == 0) {
			/*
			// check sprint api 1.0
			try {
				System.setExitURI(null);
				DEVICE_CAPABILITIES |= 1 << 0;
				Log.logSys("Device - Sprint API 1.0");
			} catch(Exception ex) {
				Log.logSys("Device - No Sprint API 1.0");
			}
			*/

			// check sprint api 2.0
			try {
				System.getSystemState("sprint.device.network");
				DEVICE_CAPABILITIES |= 1 << 1;
				Log.logSys("Device - Sprint API 2.0");
			} catch(Exception ex) {
				Log.logSys("Device - No Sprint API 2.0");
			}
			
			// check sprint api 2.1
			try {
				System.setSystemSetting("sprint.device.setting.backlight.display", "ON");
				DEVICE_CAPABILITIES |= 1 << 2;
				Log.logSys("Device - Sprint API 2.1");
			} catch(Exception ex) {
				Log.logSys("Device - No Sprint API 2.1");
			}
		}
		return DEVICE_CAPABILITIES;
	}
	
	public static void init() {
		getDeviceCapabilities();
		// getDeviceProperties(FILTER_SETTABLES);
		getPhoneNumber();
		// getESN();
	}
	
	/*************************************************************************
	 * DEVICE PROPERTIES QUERY
	 * 
	 * getSystemState - (API 2.0+ only)
	 *
	 * sprint.device.formfactor -------------------	"OPEN"
	 * sprint.device.setting.silenceall 			"NO"
	 * sprint.device.setting.keytonevolume -------- "12"
	 * sprint.device.setting.netguard 				"OFF"
	 * sprint.device.setting.dataroamguard -------- "ON"
	 * sprint.device.setting.airplanemode			"OFF"
	 * sprint.device.setting.dataservice ---------- "ENABLED"
	 * sprint.device.setting.ddtm					"NOT_ACTIVE"
	 * sprint.device.setting.hpptt ---------------- "OFF"
	 * sprint.device.setting.bluetooth				"ON"
	 * sprint.device.setting.fmtransmitter -------- "UNKNOWN"
	 * sprint.device.setting.signalstrength			"4"
	 * sprint.device.setting.vibration ------------ "OFF"
	 * sprint.device.setting.backlight.display		"ON"
	 * sprint.device.setting.backlight.keypad ----- "OFF"
	 * sprint.device.setting.keytone				"ON"
	 * sprint.device.setting.pppteardown ---------- "UNKNOWN"
	 * sprint.device.setting.viewing				"PORTRAIT"
	 * sprint.device.setting.orientation ---------- "ORIENTATION_PORTRAIT"
	 * sprint.media.rebuff.duration					"UNKNOWN"
	 * sprint.media.init.duration ----------------- "7"
	 * sprint.media.fullscreenmode 					"ON"
	 * sprint.application.focus ------------------- "FOREGROUND"
	 * 
	 *************************************************************************/

	private static String getProperty(String title, String property) {
		String value = null;
		if(!SprintDevice.isSimulator() && supportsSprintApi20()) {
			try {
				value = System.getSystemState(property);
				Log.logDeviceQuery("Device - get " + title + " = " + value);
				return value;
			} catch(Exception ex) {
				// ignored, falls through
			}
		}
		Log.logDeviceQuery("Device - get " + title + " failed");
		return value;
	}
	
	public static int isApplicationInForeground() {
		String value = getProperty("foreground", "sprint.application.focus");
		if(value == null) {
			return INVALID;
		} else if(value.equals("ENABLED")) {
			return TRUE;
		} else {
			// also doubles as the default value
			return FALSE;
		}
	}
	
	public static int isClamshellOpen() {
		String value = getProperty("formfactor", "sprint.device.formfactor");
		if(value == null) {
			return INVALID;
		} else if(value.equals("CLOSED")) {
			return FALSE;
		} else {
			// also doubles as the default value
			return TRUE;
		}
	}
	
	public static int isSilent() {
		String value = getProperty("silent", "sprint.device.setting.silenceall");
		if(value == null) {
			return INVALID;
		} else if(value.equals("YES")) {
			return TRUE;
		} else {
			// also doubles as the default value
			return FALSE;
		}
	}
	
	public static int isDataServicesEnabled() {
		String value = getProperty("dataservice", "sprint.device.setting.dataservice");
		if(value == null) {
			return INVALID;
		} else if(value.equals("ENABLED")) {
			return TRUE;
		} else {
			// also doubles as the default value
			return FALSE;
		}
	}
	
	public static int isDDTMOn() {
		String value = getProperty("ddtm", "sprint.device.setting.ddtm");
		if(value == null) {
			return INVALID;
		} else if(value.equals("NOT_ACTIVE")) {
			return FALSE;
		} else {
			// also doubles as the default value
			return TRUE;
		}
	}
	
	public static int isPTTOn() {
		String value = getProperty("ptt", "sprint.device.setting.hpptt");
		if(value == null) {
			return INVALID;
		} else if(value.equals("ON")) {
			return TRUE;
		} else {
			// also doubles as the default value
			return FALSE;
		}
	}
	
	public static int isBluetoothOn() {
		String value = getProperty("bluetooth", "sprint.device.setting.bluetooth");
		if(value == null) {
			return INVALID;
		} else if(value.equals("OFF")) {
			return FALSE;
		} else {
			// also doubles as the default value
			return TRUE;
		}
	}
	
	/*************************************************************************
	 * DEVICE PROTECTED PROPERTIES QUERY
	 * 
	 * getProtectedProperty - (API 2.0+ only)
	 * 
	 * sprint.user.mdn - returns user's phone number
	 * sprint.user.esn - returns user's esn
	 * 
	 *************************************************************************/

	private static String getProtectedProperty(String title, String property) {
		String value = null;
		if(supportsSprintApi21()) {
			try {
				value = System.getProtectedProperty(property);
				Log.logDeviceQuery("Device - get " + title + " = " + value);
				return value;
			} catch(Exception ex) {
				// ignored, falls through
			}
		}
		Log.logDeviceQuery("Device - get " + title + " failed");
		return value;
	}

	public static boolean isSimulator() {
		return getDeviceName() == null;
	}
	
	public static String getDeviceName() {
		return java.lang.System.getProperty("sprint.device.model"); // E4277
	}
	
	public static String getPhoneNumber() {
		return getProtectedProperty("phone", "sprint.user.mdn");
	}
	
	public static String getESN() {
		return getProtectedProperty("esn", "sprint.user.esn");
	}
	
	private static final int FILTER_ALL = 0;
	private static final int FILTER_SETTABLES = 1;
	
	public static String[] getDeviceProperties(int filter) {
		String[] available = new String[0];
		
		try {
			Log.logSys(filter == FILTER_ALL ? "-- Device Props --" : "-- Settable Device Props --");
			available = System.getPropertiesList();
			
			for (int i = 0; i < available.length; i++) {
				try {
					switch(filter) {
					case FILTER_ALL:
						System.getSystemState(available[i]);
						Log.logSys("(O) " + available[i]);
						break;
					case FILTER_SETTABLES:
						System.setSystemSetting(available[i], System.getSystemState(available[i]));
						Log.logSys("(O) " + available[i]);
						break;
					}
				} catch(Exception ex) {
					Log.logSys("(X) " + available[i]);
				}
			}
		} catch(Exception ex) {
			Log.logSys("Failed to get properties");
		}
		return available;
	}
	
	/**************************
	 * DEVICE SET PROPERTIES
	 **************************/
	
	private static void setProperty(String title, String property, String value) {
		if(!SprintDevice.isSimulator() && supportsSprintApi21()) {
			try {
				System.setSystemSetting(property, value);
				Log.logDeviceQuery("Device - set " + title + " = " + value);
				return;
			} catch(Exception ex) {
				// ignored, falls through
			}
		}
		Log.logDeviceQuery("Device - set " + title + " = " + value + " failed");
	}
	
	public static void setSilent(String value) {
		// String value = on ? "ON" : "OFF";
		setProperty("silent", "sprint.device.setting.silenceall", value);
	}
	
	public static String getSilent() {
		String value = getProperty("silent", "sprint.device.setting.silenceall");
		return value == null ? "OFF" : value;
	}
	
	public static void setBacklight(String value) {
		// String value = on ? "ON" : "DEFAULT";
		setProperty("backlight", "sprint.device.setting.backlight.display", value);
	}
	
	public static String getBacklight() {
		String value = getProperty("backlight", "sprint.device.setting.backlight.display");
		return value == null ? "DEFAULT" : value;
	}
	
	public static void setDDTM(String value) {
		// String value = on ? "ON" : "OFF";
		setProperty("ddtm", "sprint.device.setting.ddtm", value);
	}
	
	public static String getDDTM() {
		String value = getProperty("ddtm", "sprint.device.setting.ddtm");
		return value == null ? "ON" : value;
	}
	
	// 	- setSystemSetting - (2.1)
	
	//	sprint.device.setting.silenceall - Value is "ON" or "OFF"
	//	sprint.device.setting.vibration - Value is "ON" or "OFF"
	//	sprint.device.setting.location - Value is "ON" or "OFF"
	//	sprint.device.setting.backlight.display - - Value is "ON", "DIM", "OFF", "DEFAULT"
	//	sprint.device.setting.backlight.keypad - Value is "ON" or "OFF"
	//	sprint.device.setting.keytone - Value is "ON" or "OFF"
	//	sprint.device.setting.pppteardown - Value is "YES"
	//	sprint.device.setting.ddtm - Value is "ON" or "OFF"

	// 	- supported on the DuraXT (*) denotes settable properties -
	
	//		sprint.device.network --------------------- "EVDO"
	//		sprint.device.headset 						"OUT"
	//		sprint.device.battery --------------------- "FULL"
	//		sprint.device.formfactor 					"OPEN"
	//	(*)	sprint.device.setting.silenceall ---------- "NO"
	//	(*)	sprint.device.setting.location 				"ON"
	//		sprint.device.setting.keytonevolume ------- "12"
	//		sprint.device.setting.netguard 				"OFF"
	//		sprint.device.setting.dataroamguard ------- "ON"
	//		sprint.device.setting.airplanemode			"OFF"
	//		sprint.device.setting.dataservice --------- "ENABLED"
	//		sprint.device.setting.ddtm					"NOT_ACTIVE"
	//		sprint.device.setting.hpptt --------------- "OFF"
	//		sprint.device.setting.bluetooth				"ON"
	//		sprint.device.setting.fmtransmitter ------- "UNKNOWN"
	//		sprint.device.setting.signalstrength		"4"
	//	(*)	sprint.device.setting.vibration ----------- "OFF"
	//	(*)	sprint.device.setting.backlight.display		"ON"
	//	(*)	sprint.device.setting.backlight.keypad ---- "OFF"
	//	(*)	sprint.device.setting.keytone				"ON"
	//		sprint.device.setting.pppteardown --------- "UNKNOWN"
	//	(*)	sprint.device.setting.viewing				"PORTRAIT"
	//	(*)	sprint.device.setting.orientation --------- "ORIENTATION_PORTRAIT"
	//		sprint.media.rebuff.duration				"UNKNOWN"
	//		sprint.media.init.duration ---------------- "7"
	//	(*)	sprint.media.fullscreenmode 				"ON"
	//		sprint.application.focus ------------------ "FOREGROUND"
}
