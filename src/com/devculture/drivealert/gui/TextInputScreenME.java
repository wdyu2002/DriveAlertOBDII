package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;
import com.devculture.drivealert.gui.components.UIInputController;
import com.devculture.drivealert.gui.components.UIInputController.UITextControlListener;

public abstract class TextInputScreenME extends ApplicationScreen implements UITextControlListener {
	private final UIInputController mInputController;
	
	public TextInputScreenME() {
		final String pageTitle = getTitle();
		final String inputTitle = getDescription();
		final String inputText = getTextInputValueFromDatabase();
		mInputController = new UIInputController(pageTitle);
		mInputController.setListener(this);
		mInputController.addTextField(inputTitle, inputText, TextField.ANY);
	}
	
	protected abstract String getTitle();
	
	protected abstract String getDescription();
	
	protected abstract String getTextInputValueFromDatabase();
	
	protected abstract boolean storeInputIntoDatabase(String text);

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
			// store entry into rms
			if(storeInputIntoDatabase(control.getTextFromTextField(0))) {
				// exit this screen
				popState();
			}
		}
	}
}
