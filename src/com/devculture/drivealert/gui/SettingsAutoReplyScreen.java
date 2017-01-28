package com.devculture.drivealert.gui;

import com.devculture.drivealert.data.Settings;

public class SettingsAutoReplyScreen extends TextInputScreenME {

	protected String getTitle() {
		return TEXT_AUTOREPLY_TITLE;
	}

	protected String getDescription() {
		return TEXT_AUTOREPLY_TITLE;
	}

	protected String getTextInputValueFromDatabase() {
		return Settings.getAutoReplyText();
	}

	protected boolean storeInputIntoDatabase(String submitText) {
		Settings.setAutoReplyText(submitText);
		return true;
	}

}
