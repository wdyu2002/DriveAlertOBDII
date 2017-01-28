package com.devculture.drivealert.gui;

import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.utils.Utils;

public class SettingsSetOverrideAlertNumberScreen extends TextInputScreenME {

	protected String getTitle() {
		return TEXT_PREFS_ALERT_NUMBERS;
	}

	protected String getDescription() {
		return TEXT_PREFS_ALERT_NUMBERS_DESC;
	}

	protected String getTextInputValueFromDatabase() {
		return Settings.getDirectAlertPhoneNumber();
	}

	protected boolean storeInputIntoDatabase(String pnum) {
		if(!Utils.isValidPhoneNumber(pnum) && !Utils.isPhoneNumberNotSet(pnum)) {
			// pop up error message for bad format
			Application.popupErrorMessage(TEXT_ERROR_PHONE_NUMBER_INVALID);
			return false;
		} else {
			// successful entry, store it for now
			Settings.setDirectAlertPhoneNumber(pnum);
			return true;
		}
	}

}
