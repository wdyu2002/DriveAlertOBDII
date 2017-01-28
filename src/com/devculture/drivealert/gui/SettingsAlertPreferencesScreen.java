package com.devculture.drivealert.gui;

import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;

public class SettingsAlertPreferencesScreen extends MenuScreen {
	private static final int SettingsAlertPreferencesOptionsBluetoothNotification = 0;
	private static final int SettingsAlertPreferencesOptionsSetAlertEnabled = 1;
	private static final int SettingsAlertPreferencesOptionsSetAlertNumber = 2;
	private static final int SettingsAlertPreferencesOptionsSpeedAlertBySMS = 3;
	private static final int SettingsAlertPreferencesOptionsSpeedAlertTimeFrame = 4;
	
	public SettingsAlertPreferencesScreen() {
		super(TEXT_SETTING_ALERT_PREFS);
		
		UIMenuItem opt1 = addMenuItem(SettingsAlertPreferencesOptionsBluetoothNotification, TEXT_PREFS_BLUETOOTH, TEXT_PREFS_BLUETOOTH_DESC, UIMenuItemAccessoryTypeCheckbox);
		UIMenuItem opt2 = addMenuItem(SettingsAlertPreferencesOptionsSetAlertEnabled, TEXT_ALERT_PREFS_ENABLED, TEXT_ALERT_PREFS_ENABLED_DESC, UIMenuItemAccessoryTypeCheckbox);
		addMenuItem(SettingsAlertPreferencesOptionsSetAlertNumber, TEXT_PREFS_ALERT_NUMBERS, TEXT_PREFS_ALERT_NUMBERS_DESC, UIMenuItemAccessoryTypeNavigate);
		addMenuItem(SettingsAlertPreferencesOptionsSpeedAlertBySMS, TEXT_SETTING_SPEED_ALERT, TEXT_SETTING_SPEED_ALERT_DESC, UIMenuItemAccessoryTypeNavigate);
		addMenuItem(SettingsAlertPreferencesOptionsSpeedAlertTimeFrame, TEXT_SETTING_SPEED_TIME, TEXT_SETTING_SPEED_TIME_DESC, UIMenuItemAccessoryTypeNavigate);
		
		opt1.setSelected(Settings.getBluetoothStatusNotifyOnOff());
		opt2.setSelected(Settings.getEnableDirectAlert());
	}
	
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		switch(menuItem.getMenuId()) {
			case SettingsAlertPreferencesOptionsBluetoothNotification:
				menuItem.setSelected(!menuItem.isSelected());
				Settings.setBluetoothStatusNotifyOnOff(menuItem.isSelected());
				break;
			case SettingsAlertPreferencesOptionsSetAlertEnabled:
				menuItem.setSelected(!menuItem.isSelected());
				Settings.setEnableDirectAlert(menuItem.isSelected());
				break;
			case SettingsAlertPreferencesOptionsSetAlertNumber:
				pushState(APPSTATE_SETTINGS_SET_OVERRIDE_ALERT_NUMBER_SCREEN);
				break;
			case SettingsAlertPreferencesOptionsSpeedAlertBySMS:
				pushState(APPSTATE_SETTINGS_SPEED_ALERT_BY_SMS_SCREEN);
				break;
			case SettingsAlertPreferencesOptionsSpeedAlertTimeFrame:
				pushState(APPSTATE_SETTINGS_SPEED_ALERT_TIME_FRAME_SCREEN);
				break;
		}		
	}
}
