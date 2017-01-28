package com.devculture.drivealert.notification;

public interface NotificationEventListener {
	public abstract boolean onNotificationReceived(int event, Object param);
}
