package com.devculture.drivealert.connectivity.sms;

public interface SMSEventListener {
	public static final int SMS_RECEIVED = 0;
	public static final int SMS_INIT_FAILURE = 1;
	public static final int SMS_RECEIVE_FAILURE = 2;
	public static final int SMS_CLOSE_FAILURE = 3;
	
	public abstract void smsEvent(int event, Object param);
}
