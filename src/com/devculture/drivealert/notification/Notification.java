package com.devculture.drivealert.notification;

import java.util.Hashtable;
import java.util.Vector;

public class Notification {
	public static Hashtable mNotifications = new Hashtable();
	
	public static void registerNotification(int event, NotificationEventListener listener) {
		Object key = new Integer(event);
		Object tmp = mNotifications.get(key);
		if(tmp != null) {
			Vector list = (Vector)tmp;
			if(!list.contains(listener)) {
				list.addElement(listener);
			}
			// dup ignored
		} else {
			Vector list = new Vector();
			list.addElement(listener);
			mNotifications.put(key, list);
		}
	}
	
	public static void removeNotification(int event, NotificationEventListener listener) {
		Object key = new Integer(event);
		Object tmp = mNotifications.get(key);
		if(tmp != null) {
			Vector list = (Vector)tmp;
			list.removeElement(listener);
		}
	}
	
	public static void postNotification(int event) {
		postNotification(event, null);
	}
	
	public static void postNotification(int event, Object param) {
		Object tmp = mNotifications.get(new Integer(event));
		if(tmp != null) {
			Vector list = (Vector)tmp;
			Object[] listeners = new Object[list.size()];
			list.copyInto(listeners);
			for(int i=0; i<listeners.length; i++) {
				((NotificationEventListener)listeners[i]).onNotificationReceived(event, param);
			}
		}
	}
	
}
