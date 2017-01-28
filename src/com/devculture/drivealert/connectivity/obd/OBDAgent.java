package com.devculture.drivealert.connectivity.obd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.connectivity.obd.transactions.OBDEchoOffTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDLineFeedOffTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDProtocolTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDResetTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDSearchTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDSpeedTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDTimeOutTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDTransaction;
import com.devculture.drivealert.connectivity.obd.transactions.OBDTransactionListener;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.utils.Log;
import com.devculture.drivealert.utils.SprintDevice;

public class OBDAgent implements Globals, DiscoveryListener, OBDTransactionListener {
	
	/**
	 * internal states (begins at 0)
	 */
	
	// private static final int OBD_STATE_UNINITIALIZED 			= 0;
	private static final int OBD_STATE_CONNECTING					= 1;
	private static final int OBD_STATE_DEVICE_FOUND					= 2;
	private static final int OBD_STATE_CONNECTED 					= 3;
	private static final int OBD_STATE_INITIALIZED 					= 4;
	private static final int OBD_STATE_USER_DISCONNECTED 			= 5;

	/**
	 * error states (begins at 301)
	 */

	public static final int OBD_ERRSTATE_CODES						= 300;
	public static final int OBD_ERRSTATE_TIMEOUT					= 301;
	public static final int OBD_ERRSTATE_NO_DEVICE_FOUND			= 302;
	public static final int OBD_ERRSTATE_DEVICE_SEARCH_ERROR		= 303;
	public static final int OBD_ERRSTATE_NO_SERVICE_FOUND			= 304;
	public static final int OBD_ERRSTATE_SERVICE_SEARCH_ERROR		= 305;
	public static final int OBD_ERRSTATE_INITIALIZE_ERROR			= 306;
	public static final int OBD_ERRSTATE_BLUETOOTH_OFF				= 307;
	public static final int OBD_ERRSTATE_CONNECTION_ERROR			= 308;
	public static final int OBD_ERRSTATE_DATA_ERROR					= 309; // server only state
	public static final int OBD_ERRSTATE_DISCOVERABLE_ERROR			= 310; // server only state
	
	/**
	 * bluetooth uuids
	 */
	
	public static final int UUID_SERIAL_PORT_SERVICE 				= 0x1101; // OBD-II
	public static final int UUID_HEADSET_SERVICE 					= 0x1108;
	public static final int UUID_AUDIO_SOURCE_SERVICE 				= 0x110A;
	public static final int UUID_AUDIO_SINK_SERVICE					= 0x110B;
	public static final int UUID_HEADSET_AUDIO_GATEWAY_SERVICE 		= 0x1112;
	public static final int UUID_HANDSFREE_AUDIO_GATEWAY_SERVICE 	= 0x111F;
	public static final int UUID_GENERIC_AUDIO_SERVICE 				= 0x1203;
	
	/**
	 * singleton implementation & interface
	 */
	
	private static OBDAgent mInstance = null;

	private static OBDAgent getInstance() {
		if(mInstance == null) {
			mInstance = new OBDAgent();
		}
		return mInstance;
	}
	
	public static void connect() {
		// fire off in another thread because waitForConnection is a blocking call
		new Thread() {
			public void run() {
				OBDAgent.getInstance().searchAndConnect();;
			}
		}.start();
	}
	
	public static void query(OBDTransaction transaction) {
		OBDAgent.getInstance().addQuery(transaction);
	}
	
	public static void disconnect() {
		OBDAgent.getInstance().stopConnection(OBD_STATE_USER_DISCONNECTED);
	}
	
	public static void connFailed() {
		OBDAgent.getInstance().stopConnection(OBD_ERRSTATE_CONNECTION_ERROR);
	}
	
	public static boolean isBluetoothOn() {
		return LocalDevice.isPowerOn();
	}
	
	public static boolean isConnecting() {
		switch(OBDAgent.getInstance().mState) {
		// case OBD_STATE_UNINITIALIZED:
		case OBD_STATE_CONNECTING:
		case OBD_STATE_DEVICE_FOUND:
		case OBD_STATE_CONNECTED:
			return true;
		}
		return false;
	}
	
	public static boolean isConnected() {
		return OBDAgent.getInstance().mState == OBD_STATE_INITIALIZED;
	}

	public static String getStatusString() {
		String status = "";
		
		switch(OBDAgent.getInstance().mState) {
			// connecting states
		case OBD_STATE_CONNECTING:
			status = TEXT_OBD_STATE_CONNECTING;
			break;
		case OBD_STATE_DEVICE_FOUND:
			status = TEXT_OBD_STATE_DEVICE_FOUND;
			break;
		case OBD_STATE_CONNECTED:
			status = TEXT_OBD_STATE_CONNECTED;
			break;
		case OBD_STATE_INITIALIZED:
			status = TEXT_OBD_STATE_INITIALIZED;
			break;
		case OBD_STATE_USER_DISCONNECTED:
			status = TEXT_OBD_STATE_USER_DISCONNECTED;
			break;
			
			// error states
		case OBD_ERRSTATE_TIMEOUT:
			status = TEXT_OBD_STATE_TIMEOUT;
			break;
		case OBD_ERRSTATE_NO_DEVICE_FOUND:
			status = TEXT_OBD_STATE_NO_DEVICE_FOUND;
			break;
		case OBD_ERRSTATE_DEVICE_SEARCH_ERROR:
			status = TEXT_OBD_STATE_DEVICE_SEARCH_ERROR;
			break;
		case OBD_ERRSTATE_NO_SERVICE_FOUND:
			status = TEXT_OBD_STATE_NO_SERVICE_FOUND;
			break;
		case OBD_ERRSTATE_SERVICE_SEARCH_ERROR:
			status = TEXT_OBD_STATE_SERVICE_SEARCH_ERROR;
			break;
		case OBD_ERRSTATE_INITIALIZE_ERROR:
			status = TEXT_OBD_STATE_INITIALIZE_ERROR;
			break;
		case OBD_ERRSTATE_BLUETOOTH_OFF:
			status = TEXT_OBD_STATE_BLUETOOTH_OFF;
			break;
		case OBD_ERRSTATE_CONNECTION_ERROR:
			status = TEXT_OBD_STATE_TIMEOUT;
			break;
		}
		
		return status;
	}
	
	/******************************************
	 * CLIENT IMPLEMENTATION
	 * 
	 ******************************************/

	private OBDConnectionManager mConnectionManager = null;
	private Vector mDevices = new Vector();
	private Vector mServices = new Vector();
	private Timer mTimer = null;
	private int mState = 0;
	
	private OBDAgent() {
		
	}
	
	private void initialize() {
		mDevices.removeAllElements();
		mServices.removeAllElements();
		
		if(mConnectionManager != null) {
			mConnectionManager.terminate();
		}
	}
	
	private void searchAndConnect() {
		try {
			// begin connecting
			mState = OBD_STATE_CONNECTING;
			Notification.postNotification(NOTIFICATION_OBD_CONNECTING);
			
			initialize();
			startTimer();
			
			LocalDevice ld = LocalDevice.getLocalDevice();
			if(!ld.getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this)) {
				throw new Exception();
			}
			
			Log.logOBDEvent("Begin device search");
		} catch (Exception ex) {
			Log.logOBDError("Cant start device search", ex);
			stopConnection(OBD_ERRSTATE_DEVICE_SEARCH_ERROR);
		}
	}
	
	private void stopConnection(int reason) {
		try {
			initialize();
			stopTimer();
			
			LocalDevice ld = LocalDevice.getLocalDevice();
			ld.getDiscoveryAgent().cancelInquiry(mInstance);
			ld.getDiscoveryAgent().cancelServiceSearch(0);
			
			// close possibly open server connection
			if(mConnection != null) {
				mConnection.close();
				mConnection = null;
			}
			
			/*

			// dyu: do not attempt to close this, or the server will fail to restart
			if(mNotifier != null) {
				mNotifier.close();
				mNotifier = null;
			}
			
			*/
			
			// print reason we decided to stop the connection
			Log.logOBDEvent("Connection stopped (" + reason + ")");
		} catch(Exception ex) {
			// ignore
		} finally {
			mState = reason;
			Notification.postNotification(NOTIFICATION_OBD_DISCONNECTED, new Integer(reason));
		}
	}
	
	/**
	 * Timer for time-outs
	 */
	
	private void startTimer() {
		// stop previous
		stopTimer();
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			public void run() {
				Log.logOBDEvent("Connection timed out");
				stopConnection(OBD_ERRSTATE_TIMEOUT);
			}
		}, BLUETOOTH_CONNECTION_TIME_OUT);
	}
	
	private void stopTimer() {
		if(mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	/**
	 * DiscoveryListener interface methods
	 */
	 
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass deviceClass) {
		int major = 0;
		int minor = 0;
		try {
			major = deviceClass.getMajorDeviceClass();
			minor = deviceClass.getMinorDeviceClass();
			
			// attempt to pass through name of the device
			Log.logOBDEvent("--------");
			Log.logOBDEvent(btDevice.getFriendlyName(false));
			Log.logOBDEvent(btDevice.getBluetoothAddress() + ", " + major + ", " + minor);
		} catch(Exception ex) {
			Log.logOBDError("ERR: Bad bluetooth device name", ex);
		}
		
		// dyu: apply OBD device filter here
		if(SprintDevice.isSimulator() || major == 7936 && minor == 0) {
			mDevices.addElement(new Object[] {btDevice, deviceClass});
		}
	}

	public void inquiryCompleted(int discoveryType) {
		switch(discoveryType) {
		case DiscoveryListener.INQUIRY_COMPLETED:
			// clean previous services
			mServices.removeAllElements();
			
			// connect to the first service
			try {
				if(mDevices.size() == 0) {
					Log.logOBDEvent("No devices");
					stopConnection(OBD_ERRSTATE_NO_DEVICE_FOUND);
					break;
				}
				
				int[] attrSet = null; // use default attributes
	            UUID[] uuids = new UUID[1];
	            uuids[0] = new UUID(UUID_SERIAL_PORT_SERVICE);
				Object[] device = (Object[])mDevices.elementAt(0);
				LocalDevice ld = LocalDevice.getLocalDevice();
				
				mState = OBD_STATE_DEVICE_FOUND;
				Notification.postNotification(NOTIFICATION_OBD_CONNECTING);
				
				Log.logOBDEvent("Begin service search");
				ld.getDiscoveryAgent().searchServices(attrSet, uuids, (RemoteDevice)device[0], this);
			} catch (Exception ex) {
				Log.logOBDError("Cant start service search", ex);
				stopConnection(OBD_ERRSTATE_DEVICE_SEARCH_ERROR);
			}
			break;
		case DiscoveryListener.INQUIRY_TERMINATED:
			// cancelled, ignore
			break;
		case DiscoveryListener.INQUIRY_ERROR:
			Log.logOBDEvent("Failed in device search");
			stopConnection(OBD_ERRSTATE_DEVICE_SEARCH_ERROR);
			break;
		}
	}

	public void servicesDiscovered(int transactionId, ServiceRecord[] records) {
		Object[] service = new Object[records.length + 1];
		service[0] = new Integer(transactionId);
		for(int i=0; i<records.length; i++) {
			service[i+1] = records[i];
		}
		mServices.addElement(service);
	}

	public void serviceSearchCompleted(int transactionId, int responseCode) {
		switch(responseCode) {
		case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
			StreamConnection connection = null;
			
			try {
				if(mServices.size() == 0) {
					Log.logOBDEvent("No services");
					stopConnection(OBD_ERRSTATE_NO_SERVICE_FOUND);
					break;
				}

				Log.logOBDEvent("Found " + mServices.size() + " services");
				Object[] service = (Object[])mServices.elementAt(0);
				String url = ((ServiceRecord)service[1]).getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
				Log.logOBDEvent(url);
				connection = (StreamConnection)Connector.open(url);
			} catch(Exception ex) {
				Log.logOBDError("Failed in service search", ex);
				stopConnection(OBD_ERRSTATE_SERVICE_SEARCH_ERROR);
			}
			
			try {
				// connected
				stopTimer();
				mState = OBD_STATE_CONNECTED;
				Notification.postNotification(NOTIFICATION_OBD_CONNECTING);
				
				// initialize obd device
				mConnectionManager = new OBDConnectionManager(connection);
				mConnectionManager.start();
				mConnectionManager.queueRequest(new OBDResetTransaction());
				mConnectionManager.queueRequest(new OBDEchoOffTransaction());
				mConnectionManager.queueRequest(new OBDEchoOffTransaction());
				mConnectionManager.queueRequest(new OBDLineFeedOffTransaction());
				mConnectionManager.queueRequest(new OBDTimeOutTransaction(300));
				mConnectionManager.queueRequest(new OBDProtocolTransaction());
				mConnectionManager.queueRequest(new OBDSearchTransaction());

				// test initialization
				mConnectionManager.queueRequest(new OBDSpeedTransaction(this));
			} catch(Exception ex) {
				Log.logOBDError("Failed to initialize device", ex);
				stopConnection(OBD_ERRSTATE_INITIALIZE_ERROR);
			}
			break;
		case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
			Log.logOBDEvent("Failed device not reachable");
			stopConnection(OBD_ERRSTATE_SERVICE_SEARCH_ERROR);
			break;
		case DiscoveryListener.SERVICE_SEARCH_ERROR:
			Log.logOBDEvent("Failed unknown error");
			stopConnection(OBD_ERRSTATE_SERVICE_SEARCH_ERROR);
			break;
		case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
			Log.logOBDEvent("Failed no records");
			stopConnection(OBD_ERRSTATE_SERVICE_SEARCH_ERROR);
			break;
		case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
			// cancelled, ignore
			break;
		}
	}
	
	private void addQuery(OBDTransaction transaction) {
		if(mState != OBD_STATE_INITIALIZED) {
			transaction.notifyFailure(OBDTransactionListener.OBD_TRANSACTION_ERROR_DEVICE_UNINITIALIZED);
		} else {
			mConnectionManager.queueRequest(transaction);
		}
	}
	
	public void onOBDTransactionSuccessful(OBDTransaction transaction, Object result) {
		if(transaction instanceof OBDSpeedTransaction) {
			// only on speed returned, success
			Log.logOBDEvent("Device initialized, is ready");
			mState = OBD_STATE_INITIALIZED;
			Notification.postNotification(NOTIFICATION_OBD_CONNECTED);
		}
	}

	public void onOBDTransactionFailed(OBDTransaction transaction, int reason) {
		// transaction failure can only occur during the initialization stage
		stopConnection(OBD_ERRSTATE_INITIALIZE_ERROR);
		
		/*
		if(transaction instanceof OBDSpeedTransaction) {
			switch(reason) {
			case OBD_TRANSACTION_ERROR_READ:
			case OBD_TRANSACTION_ERROR_PARSE:
			case OBD_TRANSACTION_ERROR_DEVICE_UNINITIALIZED:
				stopConnection(OBD_ERRSTATE_INITIALIZE_ERROR);
				break;
			}
		} else {
			stopConnection(OBD_ERRSTATE_INITIALIZE_ERROR);
		}
		*/
	}
	
	private class OBDConnectionManager extends Thread {
		private StreamConnection mConnection;
		private InputStream mIS;
		private OutputStream mOS;
		private boolean mIsAlive = true;
		private Vector mQueue = new Vector();
		private final static int MAX_QUEUE_SIZE = 100; // max 100 requests
		
		OBDConnectionManager(StreamConnection connection) throws IOException {
			mConnection = connection;
			mIS = connection.openInputStream();
			mOS = connection.openOutputStream();
		}
		
		synchronized boolean queueRequest(OBDTransaction transaction) {
			// ignore queue requests over 100
			if(mQueue.size() >= MAX_QUEUE_SIZE) {
				return false;
			}
			mQueue.addElement(transaction);
			notifyAll();
			return true;
		}
		
		private synchronized void waitForQueueRequest() {
			try {
				wait();
			} catch(InterruptedException ex) {
				
			}
		}
		
		public void run() {
			synchronized(this) {
				if(mQueue.size() == 0) {
					waitForQueueRequest();
				}
			}
			
			while(mIsAlive) {
				try  {
					Object first = null;
					int size = 0;
					synchronized(this) {
						size = mQueue.size();
						while(size == 0) {
							waitForQueueRequest();
							size = mQueue.size();
						}
						first = mQueue.elementAt(0);
						mQueue.removeElementAt(0);
					}
					((OBDTransaction)first).execute(mIS, mOS);
				} catch(Exception ex) {
					Log.logOBDError("ERR: Bad state in OBD Connection Manager", ex);
				}
			}
			mIsAlive = false;
		}
		
		public void terminate() {
			try {
				if(mIS != null) {
					mIS.close();
				}
			} catch(Exception ex) {
				
			}
			
			try {
				if(mOS != null) {
					mOS.close();
				}
			} catch(Exception ex) {
				
			}
			
			try {
				if(mConnection != null) {
					mConnection.close();
				}
			} catch(Exception ex) {
				
			}
			
			mIS = null;
			mOS = null;
			mConnection = null;
			mIsAlive = false;
		}
	}
	
	/******************************************
	 * MOCK SERVER IMPLEMENTATION
	 * 
	 ******************************************/

	private final UUID uuid = new UUID("F0E0D0C0B0A000908070605040302010", false);
	private final String mUrl = "btspp://localhost:" + uuid.toString() + ";name=Server;authorize=false";
	private StreamConnectionNotifier mNotifier = null;
	private StreamConnection mConnection = null;
	public static String THE_CAR_SPEED = "00";

	public static void broadcast() {
		// fire off in another thread because waitForConnection is a blocking call
		new Thread() {
			public void run() {
				OBDAgent.getInstance().waitForConnection();
			}
		}.start();
	}
	
	private void waitForConnection() {
		// local scope
		InputStream is = null;
		OutputStream os = null;

		try {
			/*
			// check if bluetooth device is even on
			if(!LocalDevice.isPowerOn()) {
				notifyStateChanged(OBDAgentListener.OBD_STATE_BLUETOOTH_OFF);
				return;
			}
			*/
			
			LocalDevice ld = LocalDevice.getLocalDevice();
			if (!ld.setDiscoverable(DiscoveryAgent.GIAC)) {
				stopConnection(OBD_ERRSTATE_DISCOVERABLE_ERROR);
                return;
			}
			
			// begin broadcast
			mState = OBD_STATE_CONNECTING;
			Notification.postNotification(NOTIFICATION_OBD_CONNECTING);
			
			if(mNotifier == null) {
				mNotifier = (StreamConnectionNotifier)Connector.open(mUrl);
			}
			
			// awaiting connection
			mConnection = mNotifier.acceptAndOpen();
			mState = OBD_STATE_CONNECTED;
			Notification.postNotification(NOTIFICATION_OBD_CONNECTING);
			
			// attempt to open connections (in & out)
			is = mConnection.openInputStream();
			os = mConnection.openOutputStream();
			char read = 0;
			StringBuffer buffer = new StringBuffer();
			
			while(mConnection != null) {
				try {
					buffer.setLength(0);
					while((read = (char)is.read()) != '\r') {
						buffer.append(read);
					}
					
					Log.logOBDEvent("<< " + buffer.toString());
					
					// pass the data through to the OBDSim
					
					// get the data back from the OBDSim
					
					// pass the data back to the client
					if(buffer.length() == 0) {
						os.write("NO DATA>".getBytes());
					} else if(buffer.toString().equals("AT Z")) {
						os.write("ELM327 v1.5>".getBytes());
					} else if(buffer.toString().equals("AT E0")) {
						os.write("OK>".getBytes());
					} else if(buffer.toString().equals("AT L0")) {
						os.write("OK>".getBytes());
					} else if(buffer.toString().startsWith("AT ST")) {
						os.write("OK>".getBytes());
					} else if(buffer.toString().startsWith("AT SP")) {
						os.write("OK>".getBytes());
					} else if(buffer.toString().equals("01 0D")) {
						if("ERR91".equals(THE_CAR_SPEED)) {
							// write ERR91
							os.write(("ERR91>").getBytes());
						} else {
							// write normal car speed
							os.write(("41 0D " + THE_CAR_SPEED + ">").getBytes());
						}
					} else {
						os.write("?>".getBytes());
					}
					os.flush();
				} catch(Exception ex) {
					Log.logOBDError("Broadcast error", ex);
					stopConnection(OBD_ERRSTATE_DATA_ERROR);
				}
			}
			stopConnection(OBD_STATE_USER_DISCONNECTED);
		} catch (Exception ex) {
			Log.logOBDError("Failed device search", ex);
			stopConnection(OBD_ERRSTATE_INITIALIZE_ERROR);
		} finally {
			try {
				if(is != null) {
					is.close();
				}
				
				if(os != null) {
					os.close();
				}
			} catch(IOException ex) {
				// ignore
			}
		}
		
	}
}

