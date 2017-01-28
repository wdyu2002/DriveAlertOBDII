package com.devculture.drivealert.connectivity;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class Connectivity {
	private static Timer mCallBlockTimer = null;
	private static CallBlockerTimerTask mCallBlockerTask = null;
	
	public static void startBlockingCalls() {
		stopBlockingCalls();
		
		mCallBlockTimer = new Timer();
		mCallBlockerTask = new CallBlockerTimerTask();
		mCallBlockTimer.schedule(mCallBlockerTask, 0);
	}
	
	public static void stopBlockingCalls() {
		if(mCallBlockTimer != null) {
			mCallBlockTimer.cancel();
			mCallBlockTimer = null;
		}

		if(mCallBlockerTask != null) {
			mCallBlockerTask.stop();
			mCallBlockerTask = null;
		}
	}
	
	private static class CallBlockerTimerTask extends TimerTask {
		private Thread mThread = null;
		// private boolean mIsStopped = false;
		
		public void run() {
			InputStream istr = null;
			HttpConnection conn = null;

			try {
				// store thread for interrupt
				mThread = Thread.currentThread();

				// init connection
				conn = (HttpConnection)Connector.open("http://www.google.com", Connector.READ, true);
				conn.setRequestMethod(HttpConnection.GET);
				istr = conn.openInputStream();
				
				// read & rest
				istr.read();
				Thread.sleep(5000);
				
				// while(!mIsStopped && istr.read() != -1) {
				//	try { Thread.sleep(5000); } catch(InterruptedException ex) { }
				// }
			} catch(Exception ex) {
				// ignore
			} finally {
				try { istr.close(); } catch(Exception ex) { }
				try { conn.close(); } catch(Exception ex) { }
				
				// schedule follow-up call blocker
				if(mCallBlockTimer != null) {
					mCallBlockerTask = new CallBlockerTimerTask();
					mCallBlockTimer.schedule(mCallBlockerTask, 0);
				}
			}
			// mIsStopped = true;
		}
		
		public void stop() {
			// mIsStopped = true;
			if(mThread != null) {
				mThread.interrupt();
			}
			// cancel();
		}
	}
}
