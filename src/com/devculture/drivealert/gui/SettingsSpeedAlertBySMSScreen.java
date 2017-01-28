package com.devculture.drivealert.gui;

import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;

public class SettingsSpeedAlertBySMSScreen extends MenuScreen {
	private static final int SettingsAlertSpeed60mph = 0;
	private static final int SettingsAlertSpeed70mph = 1;
	private static final int SettingsAlertSpeed80mph = 2;
	private static final int SettingsAlertSpeed90mph = 3;
	private static final int SettingsAlertSpeed100mph = 4;
	private static final int SettingsAlertSpeedOff = 5;

	public SettingsSpeedAlertBySMSScreen() {
		super(TEXT_SPEED_ALERT_TITLE);
		
		UIMenuItem opt1 = addMenuItem(SettingsAlertSpeed60mph, TEXT_60MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt2 = addMenuItem(SettingsAlertSpeed70mph, TEXT_70MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt3 = addMenuItem(SettingsAlertSpeed80mph, TEXT_80MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt4 = addMenuItem(SettingsAlertSpeed90mph, TEXT_90MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt5 = addMenuItem(SettingsAlertSpeed100mph, TEXT_100MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt6 = addMenuItem(SettingsAlertSpeedOff, TEXT_OFF, "", UIMenuItemAccessoryTypeRadio);
		
		switch(Settings.getAlertSpeed()) {
		case 60:
			opt1.setSelected(true);
			break;
		case 70:
			opt2.setSelected(true);
			break;
		case 80:
			opt3.setSelected(true);
			break;
		case 90:
			opt4.setSelected(true);
			break;
		case 100:
			opt5.setSelected(true);
			break;
		case -1:
			opt6.setSelected(true);
			break;
		default:
			// handle unexpected result
			Settings.setAlertSpeed(60);
			opt1.setSelected(true);
			break;
		}
	}

	protected void onMenuItemPressed(UIMenuItem menuItem) {
		deselectAll();
		menuItem.setSelected(true);

		switch(menuItem.getMenuId()) {
		case SettingsAlertSpeed60mph:
			Settings.setAlertSpeed(60);
			break;
		case SettingsAlertSpeed70mph:
			Settings.setAlertSpeed(70);
			break;
		case SettingsAlertSpeed80mph:
			Settings.setAlertSpeed(80);
			break;
		case SettingsAlertSpeed90mph:
			Settings.setAlertSpeed(90);
			break;
		case SettingsAlertSpeed100mph:
			Settings.setAlertSpeed(100);
			break;
		case SettingsAlertSpeedOff:
			Settings.setAlertSpeed(1000000 /* 1 million, impossibly high value */);
			break;
		}
	}
}
