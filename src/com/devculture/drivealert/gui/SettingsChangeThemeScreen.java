package com.devculture.drivealert.gui;

import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIMenuItem;
import com.devculture.drivealert.gui.themes.Theme;

public final class SettingsChangeThemeScreen extends MenuScreen {
	public SettingsChangeThemeScreen() {
		super(TEXT_SETTING_CHNG_THEME);
		
		for(int i=0; i<Theme.THEME_COUNT; i++) {
			addMenuItem(i, Theme.getThemeName(i), TEXT_EMPTY, UIMenuItemAccessoryTypeNone);
		}
	}
	
	public void onShow() {
		
	}
	
	protected void onMenuItemPressed(UIMenuItem menuItem) {
		int theme = menuItem.getMenuId();
		Settings.setThemeColor(theme);
	}
}




