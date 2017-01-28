package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIInputController;
import com.devculture.drivealert.gui.components.UIInputController.UITextControlListener;

public final class PasswordScreen extends ApplicationScreen implements UITextControlListener, CommandListener {
	private final UIInputController mInputController;
	
	public PasswordScreen() {
		mInputController = new UIInputController(TEXT_ENTER_PASSWORD);
		mInputController.setListener(this);
		mInputController.addTextField(TEXT_PASSWORD, "", TextField.PASSWORD);
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
			String p1 = mInputController.getTextFromTextField(0);
			String p2 = Settings.getPassword();
			
			if(!p1.equals(p2)) {
				// pop up error message
				Application.popupErrorMessage(TEXT_ERROR_PASSWORD_MISMATCH).setCommandListener(this);
			} else {
				// hide input controller
				onHide();
				
				// exit this screen (pop this screen)
				popState();
				
				// enter settings screen
				pushState(APPSTATE_SETTINGS_SCREEN);
			}
		}
	}

	public void commandAction(Command command, Displayable display) {
		if(display instanceof Alert) {
			// remove alert
			// Alert a = (Alert)display;
			// a.setCommandListener(null);
			// a.setTimeout(0);
			
			// exit this screen (pop this screen)
			popState();
		}
	}
	
}




