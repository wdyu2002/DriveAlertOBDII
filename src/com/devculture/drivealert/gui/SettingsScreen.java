package com.devculture.drivealert.gui;

import com.devculture.drivealert.connectivity.obd.OBDAgent;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;

public class SettingsScreen extends MenuScreen {
	// options
	public static final int SettingsScreenOptionsEnableDisable = 0;
	public static final int SettingsScreenOptionsAlertPreferences = 1;
	public static final int SettingsScreenOptionsOverridingPreferences = 2;
	// dyu: TODO - (hold) add these 2 options back if ADP ever responds
	// public static final int SettingsScreenOptionsAllowedPhoneNumbers = 3;
	// public static final int SettingsScreenOptionsAutoReplyMessage = 4;
	public static final int SettingsScreenOptionsChangeTheme = 5;
	public static final int SettingsScreenOptionsChangePassword = 6;
	
	// TODO - add back sync settings
	// public static final int SettingsScreenOptionsSyncSettings = 7;
	
	//#if DEBUG_MODE
	public static final int SettingsScreenOptionsLogSettings = 7;
	//#endif
	private final UIMenuItem masterOnOffMenuItem;
	
	public SettingsScreen() {
		super(TEXT_SETTINGS);
		
		masterOnOffMenuItem = addMenuItem(SettingsScreenOptionsEnableDisable, TEXT_SETTING_TURN_ONOFF, TEXT_SETTING_TURN_ONOFF_DESC, UIMenuItemAccessoryTypeCheckbox);
		addMenuItem(SettingsScreenOptionsAlertPreferences, TEXT_SETTING_ALERT_PREFS, TEXT_SETTING_ALERT_PREFS_DESC, UIMenuItemAccessoryTypeNavigate);
		addMenuItem(SettingsScreenOptionsOverridingPreferences, TEXT_SETTING_LOCK_PREFS, TEXT_SETTING_LOCK_PREFS_DESC, UIMenuItemAccessoryTypeNavigate);
		// addMenuItem(SettingsScreenOptionsAllowedPhoneNumbers, TEXT_SETTING_PHONE, TEXT_SETTING_PHONE_DESC, UIMenuItemAccessoryTypeNavigate);
		// addMenuItem(SettingsScreenOptionsAutoReplyMessage, TEXT_SETTING_AUTOREPLY, TEXT_SETTING_AUTOREPLY_DESC, UIMenuItemAccessoryTypeNavigate);
		addMenuItem(SettingsScreenOptionsChangeTheme, TEXT_SETTING_CHNG_THEME, TEXT_SETTING_CHNG_THEME_DESC, UIMenuItemAccessoryTypeNavigate);
		addMenuItem(SettingsScreenOptionsChangePassword, TEXT_SETTING_CHNG_PWD, TEXT_SETTING_CHNG_PWD_DESC, UIMenuItemAccessoryTypeNavigate);
		
		// TODO - add back sync settings
		// addMenuItem(SettingsScreenOptionsSyncSettings, TEXT_SETTING_SYNC_SETTINGS, TEXT_SETTING_SYNC_SETTINGS_DESC, UIMenuItemAccessoryTypeNone);
		
		//#if DEBUG_MODE
		addMenuItem(SettingsScreenOptionsLogSettings, TEXT_LOG_PREFERENCES, TEXT_LOG_PREFERENCES_DESC, UIMenuItemAccessoryTypeNavigate);
		//#endif
		
		// toggle option based on settings
		masterOnOffMenuItem.setSelected(Settings.getMasterOnOff());
	}
	
	public void onShow() {
		masterOnOffMenuItem.setSelected(Settings.getMasterOnOff());
	}
	
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		switch(menuItem.getMenuId()) {
			case SettingsScreenOptionsEnableDisable:
				menuItem.setSelected(!menuItem.isSelected());
				boolean isOn = menuItem.isSelected();
				Settings.setMasterOnOff(isOn);
				if(isOn) {
					// turn on, ApplicationLogic will handle the rest
					// pushState(APPSTATE_ACTIVATION_SCREEN);
				} else {
					// turn off
					OBDAgent.disconnect();
				}
				break;
			case SettingsScreenOptionsAlertPreferences:
				pushState(APPSTATE_SETTINGS_ALERT_PREFERENCES_SCREEN);
				break;
			case SettingsScreenOptionsOverridingPreferences:
				pushState(APPSTATE_SETTINGS_OVERRIDING_PREFERENCES_SCREEN);
				break;
				/*
			case SettingsScreenOptionsAllowedPhoneNumbers:
				pushState(APPSTATE_SETTINGS_ALLOWED_PHONE_NUMBERS_SCREEN);
				break;
			case SettingsScreenOptionsAutoReplyMessage:
				pushState(APPSTATE_SETTINGS_AUTO_REPLY_SCREEN);
				break;
				*/
			case SettingsScreenOptionsChangeTheme:
				pushState(APPSTATE_SETTINGS_CHANGE_THEME_SCREEN);
				break;
			case SettingsScreenOptionsChangePassword:
				pushState(APPSTATE_SETTINGS_CHANGE_PASSWORD_SCREEN);
				break;
				
				// TODO - add back sync settings
				/*
			case SettingsScreenOptionsSyncSettings:
				pushState(APPSTATE_SETTINGS_SYNC_SCREEN);
				break;
				*/
				
			//#if DEBUG_MODE
			case SettingsScreenOptionsLogSettings:
				pushState(APPSTATE_SETTINGS_LOG_SCREEN);
				break;
			//#endif
		}
	}
}
