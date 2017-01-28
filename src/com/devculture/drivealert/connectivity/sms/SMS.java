package com.devculture.drivealert.connectivity.sms;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import com.devculture.drivealert.Application;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.utils.Log;
import com.devculture.drivealert.utils.SprintDevice;
import com.devculture.drivealert.utils.Utils;

public class SMS implements Globals {
	private static SMSEventListener mListener = null;
	private static SMSListenerThread mListenerThread = null;
	
	/**
	 * Sends SMS in a separate thread, so that it doesn't block the calling
	 * thread.
	 * 
	 * @param phoneNumber 	ie. "13109992390"
	 * @param message		ie. "Hello world"
	 */
	public static void sendSMS(final String phoneNumber, final String message) {
		// do nothing if invalid param is found
		if(phoneNumber == null || !Utils.isValidPhoneNumber(phoneNumber)) {
			return;
		}

		// do nothing if invalid param is found
		if(message == null || message.length() == 0) {
			return;
		}
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				MessageConnection conn = null;
				String smsPort = Application.getMIDlet().getAppProperty("SMS-Port");
				String address = new StringBuffer("sms://").append(phoneNumber).append(":").append(smsPort).toString();
				try {
					Log.logSMS("SMS - sent message to " + address);
					
					// don't need to actually send the text on the simulator
					if(SprintDevice.isSimulator()) {
						return;
					}
					
					conn = (MessageConnection) Connector.open(address);
					TextMessage textMessage = (TextMessage)conn.newMessage(MessageConnection.TEXT_MESSAGE);
					textMessage.setAddress(address);
					textMessage.setPayloadText(message);
					conn.send(textMessage);
					Log.logSMS("SMS - send success");
				} catch(IOException ex) {
					Log.logSMS("SMS - send failure");
				} finally {
					try {
						conn.close();
					} catch(IOException ex) {
						// ignore
					}
					conn = null;
				}
			}
		}, 0);
	}
	
	public static void addSMSListener(SMSEventListener listener) {
		mListener = listener;
		if (mListenerThread == null || mListenerThread.isStopped()) {
			mListenerThread = new SMSListenerThread();
			mListenerThread.start();
		}
	}
	
	public static void removeSMSListener() {
		mListener = null;
		if (mListenerThread != null) {
			mListenerThread.stop();
			mListenerThread = null;
		}
	}
	
	private static class SMSListenerThread extends Thread implements Globals, MessageListener {
		private boolean mStopped = false;
		private MessageConnection mSMSListenerConnection = null;
		
		public SMSListenerThread() {
			String smsPort = Application.getMIDlet().getAppProperty("SMS-Port");
			String smsConnection = "sms://:" + smsPort;
            try {
            	Log.logSMS("SMS - init began");
            	mSMSListenerConnection = (MessageConnection)Connector.open(smsConnection);
            	mSMSListenerConnection.setMessageListener(this);
            } catch (Exception ex) {
            	Log.logSMS("SMS - init failed");
            	notifyListener(SMSEventListener.SMS_INIT_FAILURE, null);
            	mStopped = true;
            }
		}
		
		public void run() {
			while(!mStopped && mSMSListenerConnection != null) {
				try {
					Log.logSMS("SMS - begin listener thread");
					Message message = mSMSListenerConnection.receive();
					notifyListener(SMSEventListener.SMS_RECEIVED, message.toString());
					
					/*
					if(message != null) {
						// use public notification system
		                if(message instanceof TextMessage) {
		                	// notifyListener(message.getAddress() + ": " + ((TextMessage)message).getPayloadText());
		                } else {
		                    // byte[] data = ((BinaryMessage)message).getPayloadData();
		                	// notifyListener(message.getAddress() + ": Binary Data");
		                }
		            }
		            */
				} catch (InterruptedIOException ex) {
					Log.logSMS("SMS - listener interrupted");
					notifyListener(SMSEventListener.SMS_RECEIVE_FAILURE, null);
					mStopped = true;
					break;
				} catch (Exception ex) {
					Log.logSMS("SMS - listener exception (" + ex.getClass().getName() + ")");
					notifyListener(SMSEventListener.SMS_RECEIVE_FAILURE, null);
					mStopped = true;
					break;
				}
			}
			
			mStopped = true;
			Log.logSMS("SMS - listener ended");
			
			try {
				mSMSListenerConnection.close();
				mSMSListenerConnection = null;
			} catch(Exception ex) {
				// ignored
			}
			
			// try again
			SMS.addSMSListener(mListener);
		}
		
		public boolean isStopped() {
			return mStopped;
		}
		
		public void stop() {
			try {
				mStopped = true;
				mSMSListenerConnection.close();
			} catch(Exception ex) {
				Log.logSMS("SMS - failed to close");
				notifyListener(SMSEventListener.SMS_CLOSE_FAILURE, null);
			} finally {
				mSMSListenerConnection = null;
			}
		}
		
		public void notifyIncomingMessage(MessageConnection message) {
			notifyListener(SMSEventListener.SMS_RECEIVED, message.toString());
		}

		private void notifyListener(int event, String data) {
			if(mListener != null) {
				mListener.smsEvent(event, data);
			}
		}
	}
}
