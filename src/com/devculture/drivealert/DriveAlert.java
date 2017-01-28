package com.devculture.drivealert;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import com.devculture.drivealert.connectivity.sms.SMS;
import com.devculture.drivealert.connectivity.sms.SMSEventListener;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.notification.NotificationEventListener;
import com.devculture.drivealert.utils.Log;
import com.devculture.drivealert.utils.SprintDevice;
import com.sprintpcs.util.System;
import com.sprintpcs.util.SystemEventListener;

public class DriveAlert extends MIDlet implements Globals, SystemEventListener, SMSEventListener, NotificationEventListener {

	/*
	
	TODO - ADD FEATURES
	- add text to remind the user to close the clamshell (on the lock screen)
	- add PUSH settings
	- add GPS as backup for not having bluetooth
	- when SMS starts failing, restart the application
	
	*/
	
	public DriveAlert() {
		Application.setMIDlet(this);
		Application.setLogic(new ApplicationLogic(ApplicationLogic.APPSTATE_CLIENT_STARTPAGE));
		
		// begin rendering thread
		new Thread(Application.getLogic()).start();
		
		// init device capabilities vars
		SprintDevice.init();
		
		// load user settings
		Settings.load();
		
		// send a fake SMS message to request user permission (doesnt work)
		SMS.sendSMS("1234567890", "");
		// SMS.sendSMS("3109992390", "Demo application is running.");
		
		// register listeners
		Notification.registerNotification(NOTIFICATION_ENTER_BACKGROUND, this);
		Notification.registerNotification(NOTIFICATION_BRING_TO_FOREGROUND, this);
		Notification.registerNotification(NOTIFICATION_EXIT, this);
		System.addSystemListener(this);
		SMS.addSMSListener(this);
	}

	protected void destroyApp(boolean exit) throws MIDletStateChangeException {
		Log.logSys("-- destroyApp --");
		
		// save user settings
		Settings.save();
		
		// remove listeners
		Notification.removeNotification(NOTIFICATION_ENTER_BACKGROUND, this);
		Notification.removeNotification(NOTIFICATION_BRING_TO_FOREGROUND, this);
		Notification.removeNotification(NOTIFICATION_EXIT, this);
		// System.addSystemListener(null);
		SMS.removeSMSListener();
	}

	protected void pauseApp() {
		Log.logSys("-- pauseApp --");

		Application.getLogic().pause();
	}

	protected void startApp() throws MIDletStateChangeException {
		Log.logSys("-- startApp --");

		// TODO - remove expiration check
		if(APP_EXPIRES_DATE < java.lang.System.currentTimeMillis())
		{
			throw new RuntimeException("The application has expired.");
		}
		else
		{
			Application.setDisplay(Display.getDisplay(this));
			Application.getLogic().start();
		}
	}

	/**
	 * Taken from the Sprint Developer API JavaDocs.
	 * 
	 * (x) 	denotes working system events for the DuraXT.
	 * 
	 * 
	 * (x) 	sprint.device.network - returns "EVDO"
	 * 		sprint.device.headset - returns "IN" or "OUT"
	 * 		sprint.device.battery - returns "WARNING" for very low, "LOW", "HALF", "FULL"
	 * 		sprint.device.formfactor - returns "OPEN" or "CLOSED"
	 * (x) 	sprint.device.interrupt.endkey - returns "END_KEY" 		
	 * (x) 	sprint.device.interrupt.incomingcall - returns "INCOMING_CALL" 		
	 * 	   	sprint.device.interrupt.voicemail - returns "VOICEMAIL" 		
	 * 	   	sprint.device.interrupt.sms - returns "SMS_MSG" 		
	 * 	   	sprint.device.interrupt.ptt - returns "PTT_CALL" 
	 */
	public void systemEvent(String property, String value) {
		String abbreviation = property;
		
		try {
			final int index = property.lastIndexOf('.') + 1;
			abbreviation = (index >= 0 && index < property.length()) ? property.substring(index) : property;
			
			if(property.equals("sprint.device.formfactor")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_CLAMSHELL, value);
			} else if(property.equals("sprint.application.focus")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_FOCUS, value);
			} else if(property.equals("sprint.device.interrupt.endkey")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_ENDKEY, value);
			} else if(property.equals("sprint.device.interrupt.incomingcall")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_CALL_INTERRUPT, value);
			} else if(property.equals("sprint.device.interrupt.voicemail")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_VMAIL_INTERRUPT, value);
			} else if(property.equals("sprint.device.interrupt.sms")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_SMS_INTERRUPT, value);
			} else if(property.equals("sprint.device.interrupt.ptt")) {
				Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_PTT_INTERRUPT, value);
			} else if(property.startsWith("sprint.device.bluetooth")) {
				// ignore
				return;
			} else {
				// print unknown property
				throw new Exception();
			}
			
			// print known property
			Log.logSys("SysEvt - (O) " + abbreviation + " = " + value);
		} catch(Exception ex) {
			Log.logSys("SysEvt - (?) " + abbreviation + " = " + value);
		}
	}

	public void smsEvent(int event, Object param) {
		if(event == SMS_RECEIVED) {
			Notification.postNotification(NOTIFICATION_SYSTEM_EVENT_SMS_INTERRUPT, param);
		}
	}

	public boolean onNotificationReceived(int event, Object param) {
		try {
			switch(event) {
			case NOTIFICATION_BRING_TO_FOREGROUND:
				Log.logSys("-- Request bring to foreground --");
				// only bring to foreground if we're currently paused
				ApplicationLogic logic = Application.getLogic();
				if(logic.isPaused()) {
					resumeRequest();
					repaintHackRequest();
				}
				break;
			case NOTIFICATION_ENTER_BACKGROUND:
				Log.logSys("-- Request send to background --");
				platformRequest("device://idle_screen");
				break;
			case NOTIFICATION_EXIT:
				Log.logSys("-- Request exit application --");
				destroyApp(true);
				notifyDestroyed();
				break;
			}
		} catch(Exception ex) {
			Log.log("DriveAlert Notification failed (" + event + ")");
		}
		return true;
	}
	
	// dyu: uber-hack
	// unfortunately setCurrent doesnt actually repaint
	// instead, we'll set a loop timer to repaint for 2 seconds
	private void repaintHackRequest() {
		new Thread() {
			public void run() {
				long startTime = java.lang.System.currentTimeMillis();
				while(java.lang.System.currentTimeMillis() - startTime < 2000) {
					try {
						Thread.sleep(100 /* max: 10fps */);
					} catch(Exception ex) {
						// ignored
					}
					Application.getLogic().repaint();
				}
			}
		}.start();
	}
}
