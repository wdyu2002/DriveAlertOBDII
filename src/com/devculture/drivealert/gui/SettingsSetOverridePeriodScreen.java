package com.devculture.drivealert.gui;

import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;

public class SettingsSetOverridePeriodScreen extends MenuScreen {
	private static final int SettingsSetOverridePeriod1min = 0;
	private static final int SettingsSetOverridePeriod5min = 1;
	private static final int SettingsSetOverridePeriod10min = 2;
	private static final int SettingsSetOverridePeriod30min = 3;
	private static final int SettingsSetOverridePeriod60min = 4;
	private static final int SettingsSetOverridePeriod120min = 5;
	
	public SettingsSetOverridePeriodScreen() {
		super(TEXT_OVERRIDE_PERIOD_TITLE);
		
		UIMenuItem opt1 = addMenuItem(SettingsSetOverridePeriod1min, TEXT_1MIN, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt2 = addMenuItem(SettingsSetOverridePeriod5min, TEXT_5MIN, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt3 = addMenuItem(SettingsSetOverridePeriod10min, TEXT_10MIN, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt4 = addMenuItem(SettingsSetOverridePeriod30min, TEXT_30MIN, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt5 = addMenuItem(SettingsSetOverridePeriod60min, TEXT_60MIN, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt6 = addMenuItem(SettingsSetOverridePeriod120min, TEXT_120MIN, "", UIMenuItemAccessoryTypeRadio);
		
		switch(Settings.getOverridePeriod()) {
		case 1:
			opt1.setSelected(true);
			break;
		case 5:
			opt2.setSelected(true);
			break;
		case 10:
			opt3.setSelected(true);
			break;
		case 30:
			opt4.setSelected(true);
			break;
		case 60:
			opt5.setSelected(true);
			break;
		case 120:
			opt6.setSelected(true);
			break;
		default:
			// handle unexpected result
			Settings.setOverridePeriod(5); // default is 5 minutes
			opt2.setSelected(true);
			break;
		}
	}
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		deselectAll();
		menuItem.setSelected(true);

		switch(menuItem.getMenuId()) {
		case SettingsSetOverridePeriod1min:
			Settings.setOverridePeriod(1);
			break;
		case SettingsSetOverridePeriod5min:
			Settings.setOverridePeriod(5);
			break;
		case SettingsSetOverridePeriod10min:
			Settings.setOverridePeriod(10);
			break;
		case SettingsSetOverridePeriod30min:
			Settings.setOverridePeriod(30);
			break;
		case SettingsSetOverridePeriod60min:
			Settings.setOverridePeriod(60);
			break;
		case SettingsSetOverridePeriod120min:
			Settings.setOverridePeriod(120);
			break;
		}
		
		// reset the lock timer ONLY IF it's currently active
		Application.getLogic().resetNextLockTimeIfActive();
	}
	
}
