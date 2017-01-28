package com.devculture.drivealert.gui;

import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;

public class SettingsSpeedAlertTimeFrameScreen extends TextInputScreenME {

	protected String getTitle() {
		return TEXT_SETTING_SPEED_TIME;
	}

	protected String getDescription() {
		return TEXT_SETTING_SPEED_TIME_DESC;
	}

	protected String getTextInputValueFromDatabase() {
		return Integer.toString(Settings.getAlertSpeedTimeFrame());
	}

	protected boolean storeInputIntoDatabase(String text) {
		try {
			// validate this is a valid number
			int seconds = Integer.parseInt(text);
			if(seconds > 0) {
				Settings.setAlertSpeedTimeFrame(seconds);
				return true;
			}
		} catch(Exception ex) {
			// ignored
		}
		Application.popupErrorMessage(TEXT_ERROR_TIME_FRAME_INVALID);
		return false;
	}

}
