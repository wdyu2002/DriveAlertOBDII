package com.devculture.drivealert.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.rms.RecordStore;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.lang.Lang;
import com.devculture.drivealert.utils.Log;
import com.devculture.drivealert.utils.Utils;

public class Settings implements Globals, Lang {

	/**
	 * The recordstore name
	 * 
	 * Future versions of the application could use the version string
	 * to migrate old into new data
	 */
	private static String RECORD_NAME = "Settings_v1";
	private static Settings mInstance = null;

	/**
	 * Singleton implementation
	 */
	private static Settings getInstance() {
		if(mInstance == null) {
			mInstance = new Settings();
		}
		return mInstance;
	}
	
	/**
	 * Actual settings container
	 */
	private JSONObject mHeadObject = null;

	private Settings() {

	}
	
	/*
	private JSONObject getHead() {
		return mHeadObject;
	}
	*/
	
	private JSONObject getDriveAlertSettings() throws Exception {
		return mHeadObject.getJSONObject("DRIVE_ALERT_SETTINGS");
	}
	
	private void loadSettings() {
		try {
			// attempt to load data from rms
			byte[] data = getRecordData();
			
			if(data == null) {
				InputStream is = Settings.class.getResourceAsStream("/DriveAlert_DefaultSettings.dat");
				data = getData(is);
				is.close();
			}
			
			// data should not be null
			if(data == null) {
				throw new IOException();
			}

			// parse local stored data
			onDataReceived(data);
		} catch(Exception ex) {
			Application.popupErrorMessage("Error loading settings from local file");
		}
	}
	
	private void saveSettings() {
		if(mHeadObject == null) {
			// nothing to save
			deleteRecord();
		} else {
			// save to record
			String tmp = mHeadObject.toString();
			saveRecord(tmp.getBytes());
		}
	}
	
	private void syncSettings() {
		// TODO - add back sync settings
		if(true) {
			return;
		}
		
		/*
		Thread asyncConnThread = new Thread() {
			public void run() {
				try {
					// begin sync
					Notification.postNotification(NOTIFICATION_SETTINGS_SYNC_INPROGRESS);
					String phoneNumber = SprintDevice.getPhoneNumber();
					final HttpConnection connection = (HttpConnection)Connector.open(SETTINGS_ENDPOINT + phoneNumber);
					
					Timer timer = new Timer();
					TimerTask timeout = new TimerTask() {
						public void run() {
							if(connection != null) {
								try {
									connection.close();
								} catch (IOException e) {
									// ignored
								}
							}
						}
					};
					
					// begin timer
					timer.schedule(timeout, 10000); // 10sec time-out
					
					// make the connection
					int rescode = connection.getResponseCode();
					byte[] data;
					InputStream is;
					
					// stop timer
					timeout.cancel();
					timer.cancel();
					
					// on response
					if(rescode == 200) {
						// success, load data received
						is = connection.openInputStream();
						data = getData(is);
						onDataReceived(data);
						connection.close();
					} else {
						// failure, error handling
						throw new IOException();
					}
					
					// sync was successful
					Notification.postNotification(NOTIFICATION_SETTINGS_SYNC_SUCCESSFUL);
				} catch(Exception ex) {
					ex.printStackTrace();
					// sync totally failed
					Notification.postNotification(NOTIFICATION_SETTINGS_SYNC_FAILED);
				}
			}
		};
		
		// begin thread
		asyncConnThread.start();
		
		*/
	}
	
	private byte[] getData(InputStream is) throws IOException {
		int read = -1;
		byte[] data = new byte[1024];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		if(is != null) {
			while((read = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, read);
			}
		}
		return buffer.toByteArray();
	}
	
	private void onDataReceived(byte[] data) throws JSONException {
		JSONObject tmp = new JSONObject(new String(data));
		
		// check for data validity, throws exception if DRIVE_ALERT_SETTINGS is not found 
		if(tmp.getJSONObject("DRIVE_ALERT_SETTINGS") != null) {
			mHeadObject = tmp;
			
			// hack: set themes on settings loaded
			Theme.setTheme(getThemeColor());
			Log.logSettings("Settings - loaded successfully");
		}
	}
	
	/*******************
	 * RMS methods
	 *******************/
	
	private byte[] getRecordData() {
		byte[] data = null;
		try {
			RecordStore rs = RecordStore.openRecordStore(RECORD_NAME, true);
			if(rs.getNumRecords() != 0) {
				data = rs.getRecord(1);
			}
			rs.closeRecordStore();
		} catch(Exception ex) {
			Log.logSettings("Settings - RMS read failure");
		}
		return data;
	}
	
	private boolean deleteRecord() {
		boolean result = false;
		try {
			RecordStore.deleteRecordStore(RECORD_NAME);
			result = true;
		} catch(Exception ex) {
			Log.logSettings("Settings - RMS delete failure");
		}
		return result;
	}
	
	private boolean saveRecord(byte[] data) {
		boolean result = false;
		try {
			RecordStore rs = RecordStore.openRecordStore(RECORD_NAME, true, RecordStore.AUTHMODE_ANY, true);
			if(rs.getNumRecords() != 0) {
				// record exists, overwrite
				rs.setRecord(1, data, 0, data.length);
			} else {
				// record doesn't exist, save new
				rs.addRecord(data, 0, data.length);
			}
			rs.closeRecordStore();
			result = true;
		} catch(Exception ex) {
			Log.logSettings("Settings - RMS save failure");
		}
		return result;
	}
	
	/*******************
	 * Interface
	 *******************/
	
	public static void load() {
		getInstance().loadSettings();
	}
	
	public static void save() {
		getInstance().saveSettings();
	}
	
	public static void sync() {
		getInstance().syncSettings();
	}
	
	/*******************
	 * Settings ENUM
	 *******************/
	
	private static final int ALERT_SPEED 					= 0;
	private static final int ALERT_SPEED_TIME_FRAME 		= 1;
	private static final int ALLOWED_PHONE1  				= 2;
	private static final int ALLOWED_PHONE2  				= 3;
	private static final int ALLOWED_PHONE3  				= 4;
	private static final int ALLOWED_PHONE4  				= 5;
	private static final int ALLOWED_PHONE5  				= 6;
	private static final int AUTO_REPLY_TEXT 				= 7;
	private static final int DIRECT_ALERT_PHONE 			= 8;
	// private static final int ENABLE_AUTO_REPLY 			= 9;
	private static final int ENABLE_DIRECT_ALERT 			= 10;
	private static final int ENABLE_OVERRIDE 				= 11;
	// private static final int GPS_SEND_STEP 				= 12;
	private static final int GPS_STATUS_NOTIFY 				= 13;
	private static final int MIN_RESTRICTION_SPEED 			= 14;
	private static final int OVERRIDE_PERIOD 				= 15;
	// private static final int SPEED_DETECTION_SOURCE 		= 16;
	// private static final int STARTED_STATE 				= 17;
	
	// private variables (not part of the endpoint)
	private static final int PRIV_MASTER_ONOFF 				= 100;
	private static final int PRIV_PASSWORD 					= 101;
	private static final int PRIV_BLUETOOTH_STATUS_NOTIFY	= 102;
	private static final int PRIV_THEME_COLOR				= 103;

	private static void set(int setting, Object object) {
		try {
			Settings settings = Settings.getInstance();
			
			switch(setting) {
			case ALLOWED_PHONE1:
			case ALLOWED_PHONE2:
			case ALLOWED_PHONE3:
			case ALLOWED_PHONE4:
			case ALLOWED_PHONE5:
				settings.getDriveAlertSettings().put("ALLOWED_PHONE" + (setting-ALLOWED_PHONE1+1), (String)object);
				break;
			case MIN_RESTRICTION_SPEED:
				settings.getDriveAlertSettings().put("MIN_RESTRICTION_SPEED", ((Integer)object).intValue());
				break;
			case ALERT_SPEED:
				settings.getDriveAlertSettings().put("ALERT_SPEED", ((Integer)object).intValue());
				break;
			case ALERT_SPEED_TIME_FRAME:
				settings.getDriveAlertSettings().put("ALERT_SPEED_TIME_FRAME", ((Integer)object).intValue());
				break;
			case AUTO_REPLY_TEXT:
				settings.getDriveAlertSettings().put("AUTO_REPLY_TEXT", (String)object);
				break;
			case GPS_STATUS_NOTIFY:
				settings.getDriveAlertSettings().put("GPS_STATUS_NOTIFY", ((Boolean)object).booleanValue());
				break;
			case ENABLE_OVERRIDE:
				settings.getDriveAlertSettings().put("ENABLE_OVERRIDE", ((Boolean)object).booleanValue());
				break;
			case DIRECT_ALERT_PHONE:
				settings.getDriveAlertSettings().put("DIRECT_ALERT_PHONE", (String)object);
				break;
			case ENABLE_DIRECT_ALERT:
				settings.getDriveAlertSettings().put("ENABLE_DIRECT_ALERT", ((Boolean)object).booleanValue());
				break;
			case OVERRIDE_PERIOD:
				settings.getDriveAlertSettings().put("OVERRIDE_PERIOD", ((Integer)object).intValue());
				break;
			case PRIV_MASTER_ONOFF:
				settings.getDriveAlertSettings().put("PRIV_MASTER_ONOFF", ((Boolean)object).booleanValue());
				break;
			case PRIV_PASSWORD:
				settings.getDriveAlertSettings().put("PRIV_PASSWORD", (String)object);
				break;
			case PRIV_BLUETOOTH_STATUS_NOTIFY:
				settings.getDriveAlertSettings().put("PRIV_BLUETOOTH_STATUS_NOTIFY", ((Boolean)object).booleanValue());
				break;
			case PRIV_THEME_COLOR:
				settings.getDriveAlertSettings().put("PRIV_THEME_COLOR", ((Integer)object).intValue());
				break;
			}
		} catch(Exception ex) {
			Log.logSettings("Settings: Failed to set setting (" + setting + ")");
		}
	}
	
	private static Object get(int setting) throws Exception {
		Object result = null;

		try {
			Settings settings = Settings.getInstance();
			
			switch(setting) {
			case ALLOWED_PHONE1:
			case ALLOWED_PHONE2:
			case ALLOWED_PHONE3:
			case ALLOWED_PHONE4:
			case ALLOWED_PHONE5:
				result = settings.getDriveAlertSettings().getString("ALLOWED_PHONE" + (setting-ALLOWED_PHONE1+1));
				break;
			case MIN_RESTRICTION_SPEED:
				result = new Integer(settings.getDriveAlertSettings().getInt("MIN_RESTRICTION_SPEED"));
				break;
			case ALERT_SPEED:
				result = new Integer(settings.getDriveAlertSettings().getInt("ALERT_SPEED"));
				break;
			case ALERT_SPEED_TIME_FRAME:
				result = new Integer(settings.getDriveAlertSettings().getInt("ALERT_SPEED_TIME_FRAME"));
				break;
			case AUTO_REPLY_TEXT:
				result = settings.getDriveAlertSettings().getString("AUTO_REPLY_TEXT");
				break;
			case GPS_STATUS_NOTIFY:
				result = new Boolean(settings.getDriveAlertSettings().getBoolean("GPS_STATUS_NOTIFY"));
				break;
			case ENABLE_OVERRIDE:
				result = new Boolean(settings.getDriveAlertSettings().getBoolean("ENABLE_OVERRIDE"));
				break;
			case DIRECT_ALERT_PHONE:
				result = settings.getDriveAlertSettings().getString("DIRECT_ALERT_PHONE");
				break;
			case ENABLE_DIRECT_ALERT:
				result = new Boolean(settings.getDriveAlertSettings().getBoolean("ENABLE_DIRECT_ALERT"));
				break;
			case OVERRIDE_PERIOD:
				result = new Integer(settings.getDriveAlertSettings().getInt("OVERRIDE_PERIOD"));
				break;
			case PRIV_MASTER_ONOFF:
				if(!settings.getDriveAlertSettings().has("PRIV_MASTER_ONOFF")) {
					settings.getDriveAlertSettings().put("PRIV_MASTER_ONOFF", true /* default value is 'on' */);
				}
				result = new Boolean(settings.getDriveAlertSettings().getBoolean("PRIV_MASTER_ONOFF"));
				break;
			case PRIV_PASSWORD:
				if(!settings.getDriveAlertSettings().has("PRIV_PASSWORD")) {
					settings.getDriveAlertSettings().put("PRIV_PASSWORD", "");
				}
				result = settings.getDriveAlertSettings().getString("PRIV_PASSWORD");
				break;
			case PRIV_BLUETOOTH_STATUS_NOTIFY:
				if(!settings.getDriveAlertSettings().has("PRIV_BLUETOOTH_STATUS_NOTIFY")) {
					settings.getDriveAlertSettings().put("PRIV_BLUETOOTH_STATUS_NOTIFY", true);
				}
				result = new Boolean(settings.getDriveAlertSettings().getBoolean("PRIV_BLUETOOTH_STATUS_NOTIFY"));
				break;
			case PRIV_THEME_COLOR:
				if(!settings.getDriveAlertSettings().has("PRIV_THEME_COLOR")) {
					settings.getDriveAlertSettings().put("PRIV_THEME_COLOR", Theme.THEME_DEFAULT);
				}
				result = new Integer(settings.getDriveAlertSettings().getInt("PRIV_THEME_COLOR"));
				break;
			}
		} catch(Exception ex) {
			Log.logSettings("Settings: Failed to retrieve setting (" + setting + ")");
			throw ex;
		}
		
		return result;
	}

	/********************************
	 * Settings Interfacing Methods
	 ********************************/

	public static void setThemeColor(int theme) {
		Settings.set(PRIV_THEME_COLOR, new Integer(theme));
		Theme.setTheme(theme);
	}
	
	public static int getThemeColor() {
		int theme = Theme.THEME_DEFAULT;
		try {
			theme = ((Integer)Settings.get(PRIV_THEME_COLOR)).intValue();
		} catch(Exception ex) {
			// ignored
		}
		return theme;
	}
	
	public static void setBluetoothStatusNotifyOnOff(boolean on) {
		Settings.set(PRIV_BLUETOOTH_STATUS_NOTIFY, new Boolean(on));
	}
	
	public static boolean getBluetoothStatusNotifyOnOff() {
		boolean bluetoothNotifyOnOff = true;
		try {
			bluetoothNotifyOnOff = ((Boolean)Settings.get(PRIV_BLUETOOTH_STATUS_NOTIFY)).booleanValue();
		} catch(Exception ex) {
			// ignored
		}
		return bluetoothNotifyOnOff;
	}
	
	public static void setPassword(String password) {
		Settings.set(PRIV_PASSWORD, password);
	}
	
	public static String getPassword() {
		String password = ""; // default password
		try {
			String pwd = (String)Settings.get(PRIV_PASSWORD);
			if(pwd.length() > 0) {
				password = pwd;
			}
		} catch(Exception ex) {
			// ignored
		}
		return password;
	}
	
	public static void setMasterOnOff(boolean on) {
		Settings.set(PRIV_MASTER_ONOFF, new Boolean(on));
	}
	
	public static boolean getMasterOnOff() {
		boolean masterOnOff = true;
		try {
			masterOnOff = ((Boolean)Settings.get(PRIV_MASTER_ONOFF)).booleanValue();
		} catch(Exception ex) {
			// ignored
		}
		return masterOnOff;
	}
	
	public static void setOverridePeriod(int minutes) {
		Settings.set(OVERRIDE_PERIOD, new Integer(minutes));
	}
	
	public static int getOverridePeriod() {
		int minutes = 5;
		try {
			minutes = ((Integer)Settings.get(OVERRIDE_PERIOD)).intValue();
		} catch(Exception ex) {
			// ignored
		}
		return minutes;
	}

	public static void setEnableDirectAlert(boolean on) {
		Settings.set(ENABLE_DIRECT_ALERT, new Boolean(on));
	}
	
	public static boolean getEnableDirectAlert() {
		boolean enableDirectAlert = true;
		try {
			enableDirectAlert = ((Boolean)Settings.get(ENABLE_DIRECT_ALERT)).booleanValue();
		} catch(Exception ex) {
			// ignored
		}
		return enableDirectAlert;
	}
	
	public static void setDirectAlertPhoneNumber(String phoneNumber) {
		Settings.set(DIRECT_ALERT_PHONE, phoneNumber);
	}
	
	public static String getDirectAlertPhoneNumber() {
		String phoneNumber = TEXT_PHONE_NOT_SET;
		try {
			String pnum = (String)Settings.get(DIRECT_ALERT_PHONE);
			if(Utils.isValidPhoneNumber(pnum)) {
				phoneNumber = pnum;
			}
		} catch(Exception ex) {
			// ignored
		}
		return phoneNumber;
	}
	
	public static void setEnableOverride(boolean on) {
		Settings.set(ENABLE_OVERRIDE, new Boolean(on));
	}
	
	public static boolean getEnableOverride() {
		boolean enableOverride = true;
		try {
			enableOverride = ((Boolean)Settings.get(ENABLE_OVERRIDE)).booleanValue();
		} catch(Exception ex) {
			// ignored
		}
		return enableOverride;
	}
	
	public static void setGPSStatusNotify(boolean on) {
		Settings.set(GPS_STATUS_NOTIFY, new Boolean(on));
	}
	
	public static boolean getGpsStatusNotify() {
		boolean gpsStatusNotify = true;
		try {
			gpsStatusNotify = ((Boolean)Settings.get(GPS_STATUS_NOTIFY)).booleanValue();
		} catch(Exception ex) {
			// ignored
		}
		return gpsStatusNotify;
	}
	
	public static void setAutoReplyText(String text) {
		Settings.set(AUTO_REPLY_TEXT, text);
	}
	
	public static String getAutoReplyText() {
		String text = TEXT_AUTO_MESSAGE;
		try {
			String txt = (String)Settings.get(AUTO_REPLY_TEXT);
			if(txt.length() > 0) {
				text = txt;
			}
		} catch(Exception ex) {
			// ignored
		}
		return text;
	}
	
	public static void setAlertSpeedTimeFrame(int seconds) {
		Settings.set(ALERT_SPEED_TIME_FRAME, new Integer(seconds));
	}
	
	public static int getAlertSpeedTimeFrame() {
		int alertSpeedTimeFrame = 120;
		try {
			alertSpeedTimeFrame = ((Integer)Settings.get(ALERT_SPEED_TIME_FRAME)).intValue();
		} catch(Exception ex) {
			// ignored
		}
		return alertSpeedTimeFrame;
	}
	
	public static void setAlertSpeed(int speed) {
		Settings.set(ALERT_SPEED, new Integer(speed));
	}
	
	public static int getAlertSpeed() {
		int alertSpeed = 60;
		try {
			alertSpeed = ((Integer)Settings.get(ALERT_SPEED)).intValue();
		} catch(Exception ex) {
			// ignored
		}
		return alertSpeed;
	}
	
	public static void setMinRestrictionSpeed(int speed) {
		Settings.set(MIN_RESTRICTION_SPEED, new Integer(speed));
	}
	
	public static int getMinRestrictionSpeed() {
		int minRestrictionSpeed = 5;
		try {
			minRestrictionSpeed = ((Integer)Settings.get(MIN_RESTRICTION_SPEED)).intValue();
		} catch(Exception ex) {
			// ignored
		}
		return minRestrictionSpeed;
	}
	
	public static void setAllowedPhoneNumber(int phoneNumberEntry, String phoneNumber) {
		Settings.set(ALLOWED_PHONE1 + phoneNumberEntry, phoneNumber);
	}
	
	public static String getAllowedPhoneNumber(int phoneNumberEntry) {
		String phoneNumber = TEXT_PHONE_NOT_SET;
		try {
			String pnum = (String)Settings.get(ALLOWED_PHONE1 + phoneNumberEntry);
			if(Utils.isValidPhoneNumber(pnum)) {
				phoneNumber = pnum;
			}
		} catch(Exception ex) {
			// ignored
		}
		return phoneNumber;
	}
}
