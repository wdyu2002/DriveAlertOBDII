package com.devculture.drivealert;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import com.devculture.drivealert.lang.Lang;

public interface Globals extends Lang {
	// TODO - remove expiration check
	public static final long APP_EXPIRES_DATE = 1349097000000L + 432000000;
	
	// endpoint for settings synchronization
	public static final String SETTINGS_ENDPOINT = "http://69.18.150.24:8080/familysafe-mobilewebapi_v1_01/request?act=getConfig&phone=1";
	
	/****************
	 * DEBUG SETTINGS
	 ****************/
	
	public static final int DEBUG_NONE 										= 0;
	public static final int DEBUG_REQUEST 									= 1<<0;
	public static final int DEBUG_RESPONSE									= 1<<1;
	public static final int DEBUG_SYSTEM_EVENTS								= 1<<2;
	public static final int DEBUG_DEVICE_QUERY_EVENTS						= 1<<3;
	public static final int DEBUG_INTERRUPT_EVENTS							= 1<<4;
	public static final int DEBUG_OBD_EVENTS								= 1<<5;
	public static final int DEBUG_OBD_ERRORS								= 1<<6;
	public static final int DEBUG_SMS_EVENTS								= 1<<7;
	public static final int DEBUG_SETTINGS_EVENTS							= 1<<8;
	
	/****************
	 * APP SETTINGS
	 ****************/
	
	// public static final int FPS 											= 10; // FPS should be between 1 and 30
	public static final int DEFAULT_CHECK_OBD_AVAILABILITY_TIME				= 5000;
	public static final int DEFAULT_SPLASH_SCREEN_WAIT_TIME					= 3 * 1000;
	public static final int BLUETOOTH_QUERY_FREQUENCY						= 3 * 1000;
	public static final int BLUETOOTH_CONNECTION_TIME_OUT 					= 60 * 1000;
	public static final int BLUETOOTH_CONNECTION_FAILED_TIME_OUT			= 15 * 1000;
	
	/****************
	 * NOTIFICATIONS
	 ****************/
	
	// midlet notifications
	public static final int NOTIFICATION_ENTER_BACKGROUND 					= 10;
	public static final int NOTIFICATION_BRING_TO_FOREGROUND 				= 11;
	public static final int NOTIFICATION_EXIT 								= 999;
	// system event notifications
	public static final int NOTIFICATION_SYSTEM_EVENT_CLAMSHELL 			= 1001; // CLOSED | OPEN
	public static final int NOTIFICATION_SYSTEM_EVENT_ENDKEY 				= 1002;	// ENDKEY
	public static final int NOTIFICATION_SYSTEM_EVENT_CALL_INTERRUPT 		= 1003;
	public static final int NOTIFICATION_SYSTEM_EVENT_VMAIL_INTERRUPT 		= 1004;
	public static final int NOTIFICATION_SYSTEM_EVENT_SMS_INTERRUPT 		= 1005;
	public static final int NOTIFICATION_SYSTEM_EVENT_PTT_INTERRUPT 		= 1006;
	public static final int NOTIFICATION_SYSTEM_EVENT_FOCUS					= 1007; // BACKGROUND | FOREGROUND
	// custom notifications
	public static final int NOTIFICATION_LOG_UPDATED 						= 2000;
	public static final int NOTIFICATION_OBD_CONNECTED						= 2001;
	public static final int NOTIFICATION_OBD_CONNECTING						= 2002;
	public static final int NOTIFICATION_OBD_DISCONNECTED					= 2003;
	public static final int NOTIFICATION_OBD_SPEED_QUERY					= 2004;
	public static final int NOTIFICATION_SETTINGS_SYNC_INPROGRESS			= 2005; 
	public static final int NOTIFICATION_SETTINGS_SYNC_SUCCESSFUL			= 2006;
	public static final int NOTIFICATION_SETTINGS_SYNC_FAILED				= 2007;
	public static final int NOTIFICATION_LOCK_OVERRIDE						= 2008;
	
	/****************
	 * APP STATES
	 ****************/
	
	static final int SCREENTYPE_FULL										= 0;
	static final int SCREENTYPE_POPUP										= 1;
	
	static final int APPSTATE_ACTIVATION_SCREEN 							= 0; // screen to initialize connections
	static final int APPSTATE_START_SCREEN									= 1;
	static final int APPSTATE_ENTER_PASSWORD_SCREEN							= 2;
	static final int APPSTATE_SET_PASSWORD_SCREEN							= 3;
	static final int APPSTATE_LOADING_SCREEN								= 4;
	static final int APPSTATE_LOCK_SCREEN 									= 5;
	static final int APPSTATE_SETTINGS_SCREEN 								= 6;
	static final int APPSTATE_SETTINGS_SYNC_SCREEN							= 7;
	static final int APPSTATE_SETTINGS_RESTRICT_SPEED_SCREEN 				= 8;
	static final int APPSTATE_SETTINGS_SPEED_ALERT_BY_SMS_SCREEN 			= 9;
	static final int APPSTATE_SETTINGS_SPEED_ALERT_TIME_FRAME_SCREEN 		= 10;
	static final int APPSTATE_SETTINGS_ALERT_PREFERENCES_SCREEN 			= 11;
	static final int APPSTATE_SETTINGS_OVERRIDING_PREFERENCES_SCREEN 		= 12;
	static final int APPSTATE_SETTINGS_ALLOWED_PHONE_NUMBERS_SCREEN 		= 13;
	static final int APPSTATE_SETTINGS_AUTO_REPLY_SCREEN 					= 14;
	static final int APPSTATE_SETTINGS_CHANGE_PASSWORD_SCREEN 				= 15;
	static final int APPSTATE_SETTINGS_CHANGE_THEME_SCREEN					= 16;
	static final int APPSTATE_SETTINGS_SET_OVERRIDE_PERIOD_SCREEN	 		= 17;
	static final int APPSTATE_SETTINGS_SET_OVERRIDE_ALERT_NUMBER_SCREEN 	= 18;
	static final int APPSTATE_SETTINGS_LOG_SCREEN							= 19;

	// error screens
	static final int APPSTATE_ALERT_BLUETOOTH_DISCONNECTED_SCREEN			= 100;
	static final int APPSTATE_ALERT_OBD_DISCONNECTED_SCREEN					= 101;

	static final int APPSTATE_LOG_SCREEN 									= 14000;
	static final int APPSTATE_SERVER_STARTPAGE								= 15000;
	static final int APPSTATE_CLIENT_STARTPAGE								= APPSTATE_START_SCREEN;
	
	/****************
	 * UI COMP
	 ****************/
	
	public static final int UIButtonStateNormal								= 0;
	public static final int UIButtonStateHighlighted 						= 1;
	public static final int UIButtonStatePressed							= 2;
	public static final int UIButtonStateDisabled							= 3;
	
	public static final int UIMenuItemStateNormal							= 0;
	public static final int UIMenuItemStateHighlighted						= 1;
	
	public static final int UIMenuItemAccessoryTypeNone						= 0;
	public static final int UIMenuItemAccessoryTypeCheckbox					= 1;
	public static final int UIMenuItemAccessoryTypeRadio					= 2;
	public static final int UIMenuItemAccessoryTypeNavigate					= 3;
	
	public static final int DEFAULT_BUTTON_HEIGHT 							= 20;

	/****************
	 * FONTS
	 ****************/

	public static final Font FONT_SMALL										= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	public static final Font FONT_SMALL_BOLD 								= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
	public static final Font FONT_SMALL_ITALIC 								= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL);
	
	public static final Font FONT_MEDIUM									= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	public static final Font FONT_MEDIUM_BOLD 								= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
	public static final Font FONT_MEDIUM_ITALIC								= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM);
	
	public static final Font FONT_LARGE										= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
	public static final Font FONT_LARGE_BOLD 								= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
	public static final Font FONT_LARGE_ITALIC								= Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_LARGE);

	// default setting
	public static final Font FONT_DEFAULT									= FONT_MEDIUM;

	/****************
	 * KEY EVENTS
	 ****************/

	// key events
	public static final int EVT_KEY_PRESSED 								= 0;
	public static final int EVT_KEY_RELEASED 								= 1;
	
	//	public static final int KEY_DOWN 									= Canvas.DOWN;
	//	public static final int KEY_UP 										= Canvas.UP;
	//	public static final int KEY_LEFT 									= Canvas.LEFT;
	//	public static final int KEY_RIGHT 									= Canvas.RIGHT;
	//	public static final int KEY_FIRE									= Canvas.FIRE;

	// simulator only
	public static final int KEY_UP 											= -1;
	public static final int KEY_DOWN 										= -2;
	public static final int KEY_LEFT 										= -3;
	public static final int KEY_RIGHT 										= -4;
	public static final int KEY_FIRE 										= -5;
	public static final int KEY_LSK 										= -6;
	public static final int KEY_RSK 										= -7;
	public static final int KEY_BACK 										= -8;
	
	// more keys
	public static final int KEY_NONE 										= -999;
	public static final int KEY_NUM0 										= Canvas.KEY_NUM0;
	public static final int KEY_NUM1 										= Canvas.KEY_NUM1;
	public static final int KEY_NUM2 										= Canvas.KEY_NUM2;
	public static final int KEY_NUM3 										= Canvas.KEY_NUM3;
	public static final int KEY_NUM4 										= Canvas.KEY_NUM4;
	public static final int KEY_NUM5 										= Canvas.KEY_NUM5;
	public static final int KEY_NUM6 										= Canvas.KEY_NUM6;
	public static final int KEY_NUM7 										= Canvas.KEY_NUM7;
	public static final int KEY_NUM8 										= Canvas.KEY_NUM8;
	public static final int KEY_NUM9 										= Canvas.KEY_NUM9;
	public static final int KEY_STAR										= Canvas.KEY_STAR;
	public static final int KEY_POUND										= Canvas.KEY_POUND;
	
	/****************
	 * RESOURCES
	 ****************/
	
	public static final String IMAGE_CHECKBOX_OFF							= "/DriveAlert_checkbox_off.png";
	public static final String IMAGE_CHECKBOX_ON							= "/DriveAlert_checkbox_on.png";
	public static final String IMAGE_LOCK_LRG								= "/DriveAlert_lock_lrg.png";
	public static final String IMAGE_LOCK_SML								= "/DriveAlert_lock_sml.png";
	public static final String IMAGE_LOGO_LRG								= "/DriveAlert_logo_lrg.png";
	public static final String IMAGE_LOGO_SML								= "/DriveAlert_logo_sml.png";
	public static final String IMAGE_BT_ON									= "/DriveAlert_bt_on.png";
	public static final String IMAGE_BT_OFF									= "/DriveAlert_bt_off.png";
	public static final String IMAGE_NAVI 									= "/DriveAlert_navigate.png";
	public static final String IMAGE_RADIO_OFF								= "/DriveAlert_radio_off.png";
	public static final String IMAGE_RADIO_ON								= "/DriveAlert_radio_on.png";
	public static final String IMAGE_SHROUD									= "/DriveAlert_shroud.png";
}
