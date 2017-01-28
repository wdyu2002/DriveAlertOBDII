package com.devculture.drivealert;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class Application implements Globals {
	private static MIDlet mMIDlet = null;
	private static Display mDisplay = null;
	private static ApplicationLogic mLogic = null;
	
	public static void setMIDlet(MIDlet midlet) {
		mMIDlet = midlet;
	}
	
	public static MIDlet getMIDlet() {
		return mMIDlet;
	}
	
	public static void setDisplay(Display display) {
		mDisplay = display;
	}
	
	public static void setDisplayable(Displayable displayable) {
		mDisplay.setCurrent(displayable);
	}
	
	public static void setLogic(ApplicationLogic logic) {
		mLogic = logic;
	}
	
	public static ApplicationLogic getLogic() {
		return mLogic;
	}
	
	public static Alert popupErrorMessage(String errorMessage) {
		Alert alert = new Alert(TEXT_ERROR, errorMessage, null, AlertType.ERROR);
		mDisplay.setCurrent(alert);
		return alert;
	}
}
