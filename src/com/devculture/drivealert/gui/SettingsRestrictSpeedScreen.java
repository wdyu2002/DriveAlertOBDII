package com.devculture.drivealert.gui;

import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;

public class SettingsRestrictSpeedScreen extends MenuScreen {
	private static final int SettingsRestrictionSpeed5mph = 0;
	private static final int SettingsRestrictionSpeed7mph = 1;
	private static final int SettingsRestrictionSpeed10mph = 2;
	private static final int SettingsRestrictionSpeed15mph = 3;
	
	public SettingsRestrictSpeedScreen() {
		super(TEXT_RESTRICT_SPEED_TITLE);
		
		UIMenuItem opt1 = addMenuItem(SettingsRestrictionSpeed5mph, TEXT_5MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt2 = addMenuItem(SettingsRestrictionSpeed7mph, TEXT_7MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt3 = addMenuItem(SettingsRestrictionSpeed10mph, TEXT_10MPH, "", UIMenuItemAccessoryTypeRadio);
		UIMenuItem opt4 = addMenuItem(SettingsRestrictionSpeed15mph, TEXT_15MPH, "", UIMenuItemAccessoryTypeRadio);
		
		switch(Settings.getMinRestrictionSpeed()) {
		case 5:
			opt1.setSelected(true);
			break;
		case 7:
			opt2.setSelected(true);
			break;
		case 10:
			opt3.setSelected(true);
			break;
		case 15:
			opt4.setSelected(true);
			break;
		default:
			// handle unexpected result
			Settings.setMinRestrictionSpeed(5);
			opt1.setSelected(true);
			break;
		}
	}
	
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		deselectAll();
		menuItem.setSelected(true);

		switch(menuItem.getMenuId()) {
		case SettingsRestrictionSpeed5mph:
			Settings.setMinRestrictionSpeed(5);
			break;
		case SettingsRestrictionSpeed7mph:
			Settings.setMinRestrictionSpeed(7);
			break;
		case SettingsRestrictionSpeed10mph:
			Settings.setMinRestrictionSpeed(10);
			break;
		case SettingsRestrictionSpeed15mph:
			Settings.setMinRestrictionSpeed(15);
			break;
		}
	}
}
