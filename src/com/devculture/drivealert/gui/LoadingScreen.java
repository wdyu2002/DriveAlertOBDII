package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;

public class LoadingScreen extends ApplicationScreen {

	protected void onShow() {
		
	}

	protected void onHide() {
		
	}

	protected void onPaint(Graphics g) {
		
	}

	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		if(event == EVT_KEY_PRESSED) {
			switch(param) {
			case KEY_BACK:
				popState();
				handled = true;
				break;
			}
		}
		return handled;
	}
}
