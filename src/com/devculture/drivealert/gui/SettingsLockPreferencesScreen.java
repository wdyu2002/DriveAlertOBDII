package com.devculture.drivealert.gui;

import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;

public class SettingsLockPreferencesScreen extends MenuScreen {
	private static final int SettingsLockPreferencesOptionsLockScreenSpeed = 0;
	private static final int SettingsLockPreferencesOptionsEnableOverriding = 1;
	private static final int SettingsLockPreferencesOptionsSetOverridePeriod = 2;

	public SettingsLockPreferencesScreen() {
		super(TEXT_SETTING_LOCK_PREFS);
		
		addMenuItem(SettingsLockPreferencesOptionsLockScreenSpeed, TEXT_SETTING_RESTRICT_SPEED, TEXT_SETTING_RESTRICT_SPEED_DESC, UIMenuItemAccessoryTypeNavigate);
		UIMenuItem opt2 = addMenuItem(SettingsLockPreferencesOptionsEnableOverriding, TEXT_PREFS_OVERRIDE, TEXT_PREFS_OVERRIDE_DESC, UIMenuItemAccessoryTypeCheckbox);
		addMenuItem(SettingsLockPreferencesOptionsSetOverridePeriod, TEXT_PREFS_OVERRIDE_PERIOD, TEXT_PREFS_OVERRIDE_PERIOD_DESC, UIMenuItemAccessoryTypeNavigate);
		
		
		opt2.setSelected(Settings.getEnableOverride());
	}
	
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		switch(menuItem.getMenuId()) {
			case SettingsLockPreferencesOptionsLockScreenSpeed:
				pushState(APPSTATE_SETTINGS_RESTRICT_SPEED_SCREEN);
				break;
			case SettingsLockPreferencesOptionsEnableOverriding:
				menuItem.setSelected(!menuItem.isSelected());
				Settings.setEnableOverride(menuItem.isSelected());
				break;
			case SettingsLockPreferencesOptionsSetOverridePeriod:
				pushState(APPSTATE_SETTINGS_SET_OVERRIDE_PERIOD_SCREEN);
				break;
				/*
			case SettingsOverridePreferencesOptionsSetAlertNumber:
				pushState(APPSTATE_SUBSETTINGS_SET_OVERRIDE_ALERT_NUMBER_SCREEN);
				break;
				*/
		}		
	}
}
