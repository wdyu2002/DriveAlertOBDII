package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIInputController;
import com.devculture.drivealert.gui.components.UIInputController.UITextControlListener;
import com.devculture.drivealert.utils.Utils;

public final class SettingsAllowedPhoneNumbersScreen extends ApplicationScreen implements UITextControlListener { 
	public static final int MAX_NUMBER_OF_PHONE_NUMBERS = 5;
	private final UIInputController mInputController;
	
	public SettingsAllowedPhoneNumbersScreen() {
		mInputController = new UIInputController(TEXT_SETTING_PHONE);
		mInputController.setListener(this);
		
		try {
			for(int i=0; i<MAX_NUMBER_OF_PHONE_NUMBERS; i++) {
				mInputController.addTextField(TEXT_PHONE_ALLOWED_NUMBER + (i+1), Settings.getAllowedPhoneNumber(i), TextField.ANY);
			}
		} catch(Exception ex) {
			// ignored
		}
	}
	
	protected void onShow() {
		
	}

	protected void onHide() {

	}
	
	protected void onPaint(Graphics g) {
		if(!mInputController.isShown()) {
			mInputController.showForm();
		}
	}

	protected boolean onHandleEvent(int event, int param) {
		// ignore
		return false;
	}
	
	public void onTextControlEvent(int event, UIInputController control) {
		if(event == UITextControlListener.TEXTCTRL_DONE) {
			for(int i=0; i<MAX_NUMBER_OF_PHONE_NUMBERS; i++) {
				String pnum = mInputController.getTextFromTextField(i);
				if(!Utils.isValidPhoneNumber(pnum) && !Utils.isPhoneNumberNotSet(pnum)) {
					// pop up error message for bad format
					Application.popupErrorMessage(TEXT_ERROR_PHONE_NUMBER_INVALID + (i+1));
					return;
				} else {
					// successful entry, store it for now
					Settings.setAllowedPhoneNumber(i, pnum);
				}
			}
			
			// exit this screen
			popState();
		}
	}
}
