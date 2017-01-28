package com.devculture.drivealert.server;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.ApplicationLogic;
import com.devculture.drivealert.utils.Log;

public class OBDServer extends MIDlet {

	public OBDServer() {
		Log.addDebugFlags(-1);
		Application.setMIDlet(this);
		Application.setLogic(new ApplicationLogic(ApplicationLogic.APPSTATE_SERVER_STARTPAGE));
	}

	protected void destroyApp(boolean exit) throws MIDletStateChangeException {
		
	}

	protected void pauseApp() {

	}

	protected void startApp() throws MIDletStateChangeException {
		Application.setDisplay(Display.getDisplay(this));
		Display.getDisplay(this).setCurrent(Application.getLogic());
		Application.getLogic().start();
	}

}
