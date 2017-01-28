package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIInputController;
import com.devculture.drivealert.gui.components.UIInputController.UITextControlListener;

public final class SettingsChangePasswordScreen extends ApplicationScreen implements UITextControlListener {
	private final UIInputController mInputController;
	
	public SettingsChangePasswordScreen() {
		mInputController = new UIInputController(TEXT_PROMPT_CREATE_PASSWORD);
		mInputController.setListener(this);
		mInputController.addTextField(TEXT_PASSWORD, "", TextField.PASSWORD);
		mInputController.addTextField(TEXT_PASSWORD_CONFIRM, "", TextField.PASSWORD);
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
			// NOTE: p1 & p2 should never be null
			String p1 = mInputController.getTextFromTextField(0);
			String p2 = mInputController.getTextFromTextField(1);
			
			/*
			if(p1 == null || p1.length() == 0) {
				// pop up error message
				Application.popupErrorMessage(TEXT_ERROR_PASSWORD_EMPTY);
			}
			 */
			
			if(p1 != null && !p1.equals(p2)) {
				// pop up error message
				Application.popupErrorMessage(TEXT_ERROR_PASSWORD_MISMATCH);
			} else {
				// store password entry into rms, even if entry is empty
				Settings.setPassword(p1);

				// exit this screen
				popState();
			}
		}
	}
}




