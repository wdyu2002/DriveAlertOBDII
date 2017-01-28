package com.devculture.drivealert.connectivity.obd;

import java.util.Timer;
import java.util.TimerTask;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.connectivity.obd.transactions.OBDSpeedTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDTransactionListener;
import com.devculture.drivealert.notification.Notification;

public class OBDQueryService implements Globals, OBDTransactionListener {
	private Timer mQueryServiceTimer = null;
	private static OBDQueryService mInstance = null;
	private static long mLastSuccessfulResponseTime = 0;
	
	private OBDQueryService() {
		
	}
	
	private static OBDQueryService getInstance() {
		if(mInstance == null) {
			mInstance = new OBDQueryService();
		}
		return mInstance;
	}
	
	public static void start() {
		getInstance().startService();
	}
	
	public static void stop() {
		getInstance().stopService();
	}
	
	private void startService() {
		// stop & clean existing
		stopService();
		mLastSuccessfulResponseTime = System.currentTimeMillis();
		mQueryServiceTimer = new Timer();
		mQueryServiceTimer.schedule(new TimerTask() {
			public void run() {
				OBDAgent.query(new OBDSpeedTransaction(OBDQueryService.getInstance()));
			}
		}, 0, BLUETOOTH_QUERY_FREQUENCY);
	}
	
	private void stopService() {
		if(mQueryServiceTimer != null) {
			mQueryServiceTimer.cancel();
			mQueryServiceTimer = null;
		}
	}

	public void onOBDTransactionSuccessful(OBDTransaction transaction, Object result) {
		if(transaction instanceof OBDSpeedTransaction) {
			Notification.postNotification(NOTIFICATION_OBD_SPEED_QUERY, result /* OBDSpeedTransaction parser returns Integer */);
		}
		mLastSuccessfulResponseTime = System.currentTimeMillis();
	}

	public void onOBDTransactionFailed(OBDTransaction transaction, int reason) {
		// check for 15 second threshold since the last good response
		if(System.currentTimeMillis() - mLastSuccessfulResponseTime > BLUETOOTH_CONNECTION_FAILED_TIME_OUT) {
			// notify agent connection is considered failed/severed
			OBDAgent.connFailed();
			// stop this service in case the listener doesnt
			stopService();
		}
	}
}
