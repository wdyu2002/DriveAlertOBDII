package com.devculture.drivealert.gui;

import com.devculture.drivealert.gui.components.UIMenuItem;
import com.devculture.drivealert.utils.Log;

public class SettingsLogScreen extends MenuScreen {
	// options
	public static final int SettingsLogScreenOptionsRequestEnableDisable = DEBUG_REQUEST;
	public static final int SettingsLogScreenOptionsResponseEnableDisable = DEBUG_RESPONSE;
	public static final int SettingsLogScreenOptionsSystemEnableDisable = DEBUG_SYSTEM_EVENTS;
	public static final int SettingsLogScreenOptionsQueryEnableDisable = DEBUG_DEVICE_QUERY_EVENTS;
	public static final int SettingsLogScreenOptionsInterruptEnableDisable = DEBUG_INTERRUPT_EVENTS;
	public static final int SettingsLogScreenOptionsOBDEventsEnableDisable = DEBUG_OBD_EVENTS;
	public static final int SettingsLogScreenOptionsOBDErrorsEnableDisable = DEBUG_OBD_ERRORS;
	public static final int SettingsLogScreenOptionsSMSEnableDisable = DEBUG_SMS_EVENTS;
	public static final int SettingsLogScreenOptionsSettingsEnableDisable = DEBUG_SETTINGS_EVENTS;
	
	public SettingsLogScreen() {
		super(TEXT_LOG_PREFERENCES);
		
		int i = 0;
		UIMenuItem[] options = new UIMenuItem[100];
		
		// dyu: add more log menu items here
		options[i++] = addMenuItem(SettingsLogScreenOptionsRequestEnableDisable, TEXT_LOG_REQUEST, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsResponseEnableDisable, TEXT_LOG_RESPONSE, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsOBDEventsEnableDisable, TEXT_LOG_OBDEVENTS, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsOBDErrorsEnableDisable, TEXT_LOG_OBDERRORS, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsQueryEnableDisable, TEXT_LOG_DEVICE_QUERY, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsSystemEnableDisable, TEXT_LOG_SYSTEM, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsInterruptEnableDisable, TEXT_LOG_INTERRUPT, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsSMSEnableDisable, TEXT_LOG_SMS, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		options[i++] = addMenuItem(SettingsLogScreenOptionsSettingsEnableDisable, TEXT_LOG_SETTINGS, TEXT_EMPTY, UIMenuItemAccessoryTypeCheckbox);
		
		for(int j=0; j<i; j++) {
			options[j].setSelected(Log.isFlagsOn(options[j].getMenuId()));
		}
	}
	
	public void onShow() {
		
	}
	
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		// set selected
		menuItem.setSelected(!menuItem.isSelected());
		
		// retrieve debug flag
		int flag = menuItem.getMenuId();
		
		// add/remove flag from debug log
		if(menuItem.isSelected()) {
			Log.addDebugFlags(flag);
		} else {
			Log.removeDebugFlags(flag);
		}
	}
}
