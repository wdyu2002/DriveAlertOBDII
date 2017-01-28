package com.devculture.drivealert.server;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.connectivity.obd.OBDAgent;
import com.devculture.drivealert.gui.LogScreen;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.notification.NotificationEventListener;
import com.devculture.drivealert.utils.Log;

public class OBDServerListScreen extends LogScreen implements NotificationEventListener {
	
	private final static String BROADCAST = "Broadcast";
	private final static String DISCONNECT = "Disconnect";
	
	public OBDServerListScreen(String title) {
		super(title);
		setLSK(BROADCAST);
	}
	
	protected void onShow() {
		super.onShow();
		Notification.registerNotification(NOTIFICATION_OBD_CONNECTING, this);
		Notification.registerNotification(NOTIFICATION_OBD_CONNECTED, this);
		Notification.registerNotification(NOTIFICATION_OBD_DISCONNECTED, this);
	}
	
	protected void onHide() {
		super.onHide();
		Notification.removeNotification(NOTIFICATION_OBD_CONNECTING, this);
		Notification.removeNotification(NOTIFICATION_OBD_CONNECTED, this);
		Notification.removeNotification(NOTIFICATION_OBD_DISCONNECTED, this);
	}
	
	public boolean onNotificationReceived(int event, Object param) {
		// first let super attempt t handle the notification
		if(!super.onNotificationReceived(event, param)) {
			switch(event) {
			case NOTIFICATION_OBD_CONNECTED:
			case NOTIFICATION_OBD_CONNECTING:
			case NOTIFICATION_OBD_DISCONNECTED:
				String status = OBDAgent.getStatusString();
				if(status != null && status.length() > 0) {
					Log.log(status);
				}
				setLSK(event == NOTIFICATION_OBD_DISCONNECTED ? BROADCAST : DISCONNECT);				
				break;
			}
		}
		return true;
	}
	
	protected void onPaint(Graphics g) {
		super.onPaint(g);
	}
	
	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		
		// let LogScreen handle key event first
		if(super.onHandleEvent(event, param)) {
			return true;
		}
		
		// if LogScreen did not handle the key event
		if(event == EVT_KEY_PRESSED) {
			switch(param) {
			case KEY_LSK:
				if(BROADCAST.equals(getLSK())) {
					OBDAgent.disconnect();
					OBDAgent.broadcast();
				} else if(DISCONNECT.equals(getLSK())) {
					OBDAgent.disconnect();
				}
				handled = true;
				break;
			case KEY_NUM0:
				Log.log("Speed = 0mph");
				OBDAgent.THE_CAR_SPEED = "00";
				handled = true;
				break;
			case KEY_NUM1:
				Log.log("Speed = 10mph");
				OBDAgent.THE_CAR_SPEED = "10"; // 16kmh
				handled = true;
				break;
			case KEY_NUM2:
				Log.log("Speed = 20mph");
				OBDAgent.THE_CAR_SPEED = "20"; // 32kmh
				handled = true;
				break;
			case KEY_NUM3:
				Log.log("Speed = 30mph");
				OBDAgent.THE_CAR_SPEED = "30"; // 48kmh
				handled = true;
				break;
			case KEY_NUM4:
				Log.log("Speed = 40mph");
				OBDAgent.THE_CAR_SPEED = "40"; // 64kmh
				handled = true;
				break;
			case KEY_NUM5:
				Log.log("Speed = 50mph");
				OBDAgent.THE_CAR_SPEED = "50"; // 80kmh
				handled = true;
				break;
			case KEY_NUM6:
				Log.log("Speed = 60mph");
				OBDAgent.THE_CAR_SPEED = "61"; // 97kmh
				handled = true;
				break;
			case KEY_NUM7:
				Log.log("Speed = 70mph");
				OBDAgent.THE_CAR_SPEED = "71"; // 113kmh
				handled = true;
				break;
			case KEY_NUM8:
				Log.log("Speed = 80mph");
				OBDAgent.THE_CAR_SPEED = "81"; // 129kmh
				handled = true;
				break;
			case KEY_NUM9:
				Log.log("Send ERR91");
				OBDAgent.THE_CAR_SPEED = "ERR91"; // "5a"; // 145kmh
				handled = true;
				break;
			}
		}
		return handled;
	}
}
