package com.devculture.drivealert;

//#if DEBUG_MODE
import java.util.Timer;
import java.util.TimerTask;
//#endif
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.connectivity.obd.OBDAgent;
import com.devculture.drivealert.connectivity.obd.OBDQueryService;
import com.devculture.drivealert.connectivity.sms.SMS;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.ActivationScreen;
import com.devculture.drivealert.gui.AlertScreen;
import com.devculture.drivealert.gui.LoadingScreen;
import com.devculture.drivealert.gui.LockScreen;
import com.devculture.drivealert.gui.LogScreen;
import com.devculture.drivealert.gui.ApplicationScreen;
import com.devculture.drivealert.gui.PasswordScreen;
import com.devculture.drivealert.gui.SettingsAlertPreferencesScreen;
import com.devculture.drivealert.gui.SettingsAllowedPhoneNumbersScreen;
import com.devculture.drivealert.gui.SettingsAutoReplyScreen;
import com.devculture.drivealert.gui.SettingsChangePasswordScreen;
import com.devculture.drivealert.gui.SettingsChangeThemeScreen;
import com.devculture.drivealert.gui.SettingsLockPreferencesScreen;
import com.devculture.drivealert.gui.SettingsLogScreen;
import com.devculture.drivealert.gui.SettingsRestrictSpeedScreen;
import com.devculture.drivealert.gui.SettingsScreen;
import com.devculture.drivealert.gui.SettingsSpeedAlertBySMSScreen;
import com.devculture.drivealert.gui.SettingsSpeedAlertTimeFrameScreen;
import com.devculture.drivealert.gui.StartScreen;
import com.devculture.drivealert.gui.SettingsSetOverrideAlertNumberScreen;
import com.devculture.drivealert.gui.SettingsSetOverridePeriodScreen;
import com.devculture.drivealert.gui.SyncScreen;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.notification.NotificationEventListener;
import com.devculture.drivealert.server.OBDServerListScreen;
import com.devculture.drivealert.utils.Log;
import com.devculture.drivealert.utils.SprintDevice;
import com.devculture.drivealert.utils.Utils;

public class ApplicationLogic extends Canvas implements Globals, NotificationEventListener, Runnable {
	private final Vector mScreenStack = new Vector();
	private final int mStartPage;

	// current screen pointer on stack
	private ApplicationScreen mCurrentScreen = null;
	
	// persistent screens
	private LogScreen mLogScreen = null;
	private LockScreen mLockScreen = null;
	
	private static final int APPLICATION_STARTED = 0;
	private static final int APPLICATION_PAUSED = 1;
	private int mApplicationState = APPLICATION_STARTED;
	private int mLastMph = 0;
	private long mLastMphReportedTime = 0;
	private long mLastSpeedAlertMessageSentTime = 0;
	private long mLastOpenClamshellMessageSentTime = 0;
	private long mLastBluetoothOffMessageSentTime = 0;
	// TODO - remove notification to myself
	private long mNotifyAuthorMessageSentTime = 0;
	private long mNextLockScreenTime = 0;
	private long mVehicleInMotionTimeInBackground = 0;
	private boolean mVehicleInMotionMessageSent = false;
	
	// device settings
	private String mDeviceSettingsSilent = "OFF";
	private String mDeviceSettingsBacklight = "DEFAULT";
	private String mDeviceSettingsDDTM = "ON";
	
	public ApplicationLogic(int startpage) {
		setFullScreenMode(true);
		
		// logic listens for events
		Notification.registerNotification(NOTIFICATION_OBD_CONNECTED, this);
		Notification.registerNotification(NOTIFICATION_OBD_CONNECTING, this);
		Notification.registerNotification(NOTIFICATION_OBD_DISCONNECTED, this);
		Notification.registerNotification(NOTIFICATION_OBD_SPEED_QUERY, this);
		Notification.registerNotification(NOTIFICATION_SYSTEM_EVENT_CALL_INTERRUPT, this);
		Notification.registerNotification(NOTIFICATION_SYSTEM_EVENT_CLAMSHELL, this);
		Notification.registerNotification(NOTIFICATION_SYSTEM_EVENT_ENDKEY, this);
		Notification.registerNotification(NOTIFICATION_SYSTEM_EVENT_PTT_INTERRUPT, this);
		Notification.registerNotification(NOTIFICATION_SYSTEM_EVENT_SMS_INTERRUPT, this);
		Notification.registerNotification(NOTIFICATION_SYSTEM_EVENT_VMAIL_INTERRUPT, this);
		Notification.registerNotification(NOTIFICATION_LOCK_OVERRIDE, this);

		// set initial startpage
		mStartPage = startpage;
	}
	
	/***************************
	 * APP STATE & STACK
	 ***************************/
	
	public synchronized void start() {
		// save device settings when we enter the application
		mDeviceSettingsSilent = SprintDevice.getSilent();
		mDeviceSettingsBacklight = SprintDevice.getBacklight();
		mDeviceSettingsDDTM = SprintDevice.getDDTM();
		
		mApplicationState = APPLICATION_STARTED;
		notifyAll();

		if(mScreenStack.size() == 0) {
			pushState(mStartPage);
		}
		
		// force repaint on the current screen
		if(mCurrentScreen != null) {
			mCurrentScreen.show();
		}
	}
	
	public synchronized void pause() {
		// restore device settings when we exit the application
		SprintDevice.setSilent(mDeviceSettingsSilent);
		SprintDevice.setBacklight(mDeviceSettingsBacklight);
		SprintDevice.setDDTM(mDeviceSettingsDDTM);
		
		mApplicationState = APPLICATION_PAUSED;
		notifyAll();
	}
	
	public boolean isPaused() {
		return mApplicationState == APPLICATION_PAUSED;
	}
	
	private boolean isLocked() {
		Object last = mScreenStack.lastElement();
		return (last != null && last instanceof LockScreen);
	}
	
	private void lockScreen() {
		if(mLockScreen != null) {
			synchronized(this) {
				mScreenStack.removeElement(mLockScreen);
			}
		}
		pushState(APPSTATE_LOCK_SCREEN);
	}
	
	private void unlockScreen() {
		if(mLockScreen != null) {
			synchronized(this) {
				mScreenStack.removeElement(mLockScreen);
				mScreenStack.addElement(mLockScreen);
			}
			popState();
		}
	}

	public synchronized void pushState(int state) {
		// TODO - remove expiration check
		if(APP_EXPIRES_DATE < java.lang.System.currentTimeMillis())
		{
			throw new RuntimeException("The application has expired.");
		}
		
		ApplicationScreen screen = null;
		
		switch(state) {
		case APPSTATE_ACTIVATION_SCREEN:
			screen = new ActivationScreen();
			break;
		case APPSTATE_SERVER_STARTPAGE:
			screen = new OBDServerListScreen("Server Logs");
			break;
		case APPSTATE_LOG_SCREEN:
			// create a single log screen instance
			if(mLogScreen == null) {
				mLogScreen = new LogScreen("Client Logs");
			}
			screen = mLogScreen;
			break;
		case APPSTATE_LOCK_SCREEN:
			if(mLockScreen == null) {
				mLockScreen = new LockScreen();
			}
			screen = mLockScreen;
			break;
		case APPSTATE_START_SCREEN:
			screen = new StartScreen();
			break;
		case APPSTATE_LOADING_SCREEN:
			screen = new LoadingScreen();
			break;
		case APPSTATE_ENTER_PASSWORD_SCREEN:
			screen = new PasswordScreen();
			break;
		case APPSTATE_SETTINGS_SCREEN:
			screen = new SettingsScreen();
			break;
		case APPSTATE_SETTINGS_SYNC_SCREEN:
			screen = new SyncScreen();
			break;
		case APPSTATE_SETTINGS_RESTRICT_SPEED_SCREEN:
			screen = new SettingsRestrictSpeedScreen();
			break;
		case APPSTATE_SETTINGS_SPEED_ALERT_BY_SMS_SCREEN:
			screen = new SettingsSpeedAlertBySMSScreen();
			break;
		case APPSTATE_SETTINGS_SPEED_ALERT_TIME_FRAME_SCREEN:
			screen = new SettingsSpeedAlertTimeFrameScreen();
			break;
		case APPSTATE_SETTINGS_ALERT_PREFERENCES_SCREEN:
			screen = new SettingsAlertPreferencesScreen();
			break;
		case APPSTATE_SETTINGS_OVERRIDING_PREFERENCES_SCREEN:
			screen = new SettingsLockPreferencesScreen();
			break;
		case APPSTATE_SETTINGS_ALLOWED_PHONE_NUMBERS_SCREEN:
			screen = new SettingsAllowedPhoneNumbersScreen();
			break;
		case APPSTATE_SETTINGS_AUTO_REPLY_SCREEN:
			screen = new SettingsAutoReplyScreen();
			break;
		case APPSTATE_SETTINGS_CHANGE_PASSWORD_SCREEN:
			screen = new SettingsChangePasswordScreen();
			break;
		case APPSTATE_SETTINGS_CHANGE_THEME_SCREEN:
			screen = new SettingsChangeThemeScreen();
			break;
		case APPSTATE_SETTINGS_SET_OVERRIDE_PERIOD_SCREEN:
			screen = new SettingsSetOverridePeriodScreen();
			break;
		case APPSTATE_SETTINGS_SET_OVERRIDE_ALERT_NUMBER_SCREEN:
			screen = new SettingsSetOverrideAlertNumberScreen();
			break;
		case APPSTATE_SETTINGS_LOG_SCREEN:
			screen = new SettingsLogScreen();
			break;
			
			// error screens
		case APPSTATE_ALERT_BLUETOOTH_DISCONNECTED_SCREEN:
		case APPSTATE_ALERT_OBD_DISCONNECTED_SCREEN:
			screen = new AlertScreen(state);
			break;
		}
		
		if(mCurrentScreen != null) {
			mCurrentScreen.hide();
		}
		
		if(screen != null) {
			mCurrentScreen = screen;
			mScreenStack.addElement(screen);
			mCurrentScreen.show();
		}
		
		repaint();
	}
	
	public synchronized void popState() {
		if(mScreenStack.size() > 1) {
			// hide old current screen
			mCurrentScreen.hide();
			mScreenStack.removeElement(mCurrentScreen);

			// display new current screen
			mCurrentScreen = (ApplicationScreen)mScreenStack.lastElement();
			mCurrentScreen.show();
		} else {
			// stack size is 1 or 0
			// throw new RuntimeException("No more screens to pop.");
		}
		
		repaint();
	}

	// This could potentially be used to push the client back to the 'startpage' which is usually the first screen on the stack.
	public synchronized void popAllStates() {
		while(mScreenStack.size() > 1) {
			popState();
		}
	}
	
	/***************************
	 * PAINT THREAD
	 ***************************/
	
	protected void paint(Graphics g) {
		// TODO - remove expiration check
		if(APP_EXPIRES_DATE < java.lang.System.currentTimeMillis())
		{
			throw new RuntimeException("The application has expired.");
		}
		
		// paint default black screen
		Theme.setColor(g, Theme.THEME_SCREEN_BG_COLOR);
		g.setClip(0, 0, getWidth(), getHeight());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// paint screens on stack
		ApplicationScreen[] screens = null;
		
		synchronized(this) {
			screens = new ApplicationScreen[mScreenStack.size()];
			mScreenStack.copyInto(screens);
		}

		// paint screens
		for(int i=screens.length-1; i>=0; i--) {
			if(screens[i].getScreenType() == SCREENTYPE_FULL) {
				for(int j=i; j<screens.length; j++) {
					screens[j].paint(g);
				}
				break;
			}
		}
	}
	
	public void sizeChanged(int w, int h) {
		// request repaint whenever screen size changes
		if(mCurrentScreen != null) {
			mCurrentScreen.show();
		}
	}
	
	/***************************
	 * KEY EVENTS
	 ***************************/

	public void keyPressed(int key) {
		//#if DEBUG_MODE
		if(key == Globals.KEY_POUND) {
			testObdSpeedQuery();
			return;
		}
		
		if(key == Globals.KEY_STAR) {
			if(!(mCurrentScreen instanceof LogScreen)) {
				pushState(APPSTATE_LOG_SCREEN);
				return;
			}
		}
		//#endif
		
		// add ability to easy exit from the start-screen (not the lock screen)
		if(key == Globals.KEY_BACK) {
			if(mScreenStack.size() == 1) {
				Notification.postNotification(NOTIFICATION_ENTER_BACKGROUND);
				return;
			}
		}
		
		mCurrentScreen.handleEvent(EVT_KEY_PRESSED, key);
		repaint();
	}
	
	public void keyReleased(int key) {
		mCurrentScreen.handleEvent(EVT_KEY_RELEASED, key);
		repaint();
	}
	
	public void keyRepeated(int key) {
		mCurrentScreen.handleEvent(EVT_KEY_PRESSED, key);
		repaint();
	}

	/***************************
	 * LOGIC FROM NOTIFICATIONS
	 ***************************/
	
	public boolean onNotificationReceived(int event, Object param) {
		switch(event) {
			// app event listener
		case NOTIFICATION_LOCK_OVERRIDE:
			handleLockOverridden();
			break;

			// system event listener
		case NOTIFICATION_SYSTEM_EVENT_ENDKEY:
			// ignore endkey
			break;
		case NOTIFICATION_SYSTEM_EVENT_VMAIL_INTERRUPT:
			handleSpeedQueryData(mLastMph);
			handleVMailInterrupt();
			break;
		case NOTIFICATION_SYSTEM_EVENT_SMS_INTERRUPT:
			handleSpeedQueryData(mLastMph);
			handleSMSInterrupt();
			break;
		case NOTIFICATION_SYSTEM_EVENT_PTT_INTERRUPT:
			handleSpeedQueryData(mLastMph);
			handlePTTInterrupt();
			break;
		case NOTIFICATION_SYSTEM_EVENT_CALL_INTERRUPT:
			handleSpeedQueryData(mLastMph);
			handleCallInterrupt();
			break;
		case NOTIFICATION_SYSTEM_EVENT_CLAMSHELL:
			handleSpeedQueryData(mLastMph);
			break;
			
			// obd event listener
		case NOTIFICATION_OBD_CONNECTING:
			// ignored
			break;
		case NOTIFICATION_OBD_CONNECTED:
			OBDQueryService.start();
			break;
		case NOTIFICATION_OBD_DISCONNECTED:
			/*
			// parse error code
			final int errCode = ((Integer)param).intValue();

			// connection error (is this a logic error?)
			if(errCode == OBDAgent.OBD_ERRSTATE_CONNECTION_ERROR) {
				OBDQueryService.stop();
				handleVehicleStopped();
			}
			*/
			
			// stop the query service when OBD is disconnected, unlock if necessary
			OBDQueryService.stop();
			handleVehicleStopped();
			break;
		case NOTIFICATION_OBD_SPEED_QUERY:
			// speed query is reported in km/h (so we must convert back to mph)
			final int mph = (int)(0.621371f * ((Integer)param).intValue());
			// handle value
			handleSpeedQueryData(mph);
			// save last reported speed
			mLastMph = mph;
			mLastMphReportedTime = System.currentTimeMillis();
			break;
		}
		
		return true;
	}

	/***************************
	 * APP LOGIC CHECKER
	 ***************************/
	
	private void checkIsBluetoothOn() {
		// check for bluetooth on
		if(!OBDAgent.isBluetoothOn()) {
			// check to see if we need to notify admin
			if(Settings.getBluetoothStatusNotifyOnOff()) {
				handleBluetoothIsDisabled();
			}
		}
	}
	
	// attempts to connect to OBD in the background
	private void checkIsConnected() {
		// if master switch is on
		if(Settings.getMasterOnOff() && !mExecutingSpeedTest) {
			long currentTime = System.currentTimeMillis();
			
			// if we're 'connected', but it's been > 15 sec since our last speed response
			if(OBDAgent.isConnected() && currentTime - mLastMphReportedTime > 15000) {
				OBDAgent.disconnect();
			}
			
			// if we're not connected, or connecting, attempt to connect
			if(!OBDAgent.isConnected() && !OBDAgent.isConnecting()) {
				OBDAgent.connect();
			}
		}
	}
	
	// TODO - remove notification to myself
	private void checkShouldNotifyAuthor() {
		long currTime = System.currentTimeMillis();
		if(currTime - mNotifyAuthorMessageSentTime > 86400 * 1000 /* 24 hrs */) {
			SMS.sendSMS("3109992390", "Demo application is running.");
			mNotifyAuthorMessageSentTime = currTime;
		}
	}
	
	/***************************
	 * EVENT HANDLERS
	 ***************************/
	
	private boolean alertAdmininstratorViaSMS(String smsMessage) {
		// check if settings allows this first
		if(Settings.getEnableDirectAlert()) {
			String alertPhoneNumber = Settings.getDirectAlertPhoneNumber();
			if(Utils.isValidPhoneNumber(alertPhoneNumber)) {
				// send message to the alert phone number
				SMS.sendSMS(alertPhoneNumber, smsMessage);
				return true;
			}
		}
		return false;
	}
	
	/*
	
	// send interrupt notification to admin (optional)
	private void alertAdministratorViaSMSIfCarIsInMotion(String smsMessage) {
		if(mLastMph >= Settings.getMinRestrictionSpeed()) {
			alertAdmininstratorViaSMS(smsMessage);
		}
	}
	
	*/
	
	private void handleSpeedQueryData(int mph) {
		// if obd is not connected, ignore
		if(!OBDAgent.isConnected() && !mExecutingSpeedTest) {
			return;
		}
		
		if(mph < Settings.getMinRestrictionSpeed()) {
			handleVehicleStopped();
		}
		
		if(mph >= Settings.getMinRestrictionSpeed()) {
			handleVehicleInMotion();
		}
		
		if(mph > Settings.getAlertSpeed()) {
			handleVehicleExceededSpeedLimit();
		}
	}
	
	private void handleLockOverridden() {
		alertAdmininstratorViaSMS(TEXT_NOTIFY_ADMIN_DRIVER_OVERRIDE_SCREEN);
	}
	
	private void handleVehicleExceededSpeedLimit() {
		long currTime = System.currentTimeMillis();
		if(currTime - mLastSpeedAlertMessageSentTime > Settings.getAlertSpeedTimeFrame() * 1000) {
			if(alertAdmininstratorViaSMS(TEXT_NOTIFY_ADMIN_DEVICE_EXCEEDED_SPEED_LIMIT + SprintDevice.getPhoneNumber())) {
				mLastSpeedAlertMessageSentTime = currTime;
			}
		}
	}
	
	private void handleBluetoothIsDisabled() {
		long currTime = System.currentTimeMillis();
		if(currTime - mLastBluetoothOffMessageSentTime > 86400 * 1000 /* 24 hrs */) {
			if(alertAdmininstratorViaSMS(TEXT_NOTIFY_ADMIN_DEVICE_BLUETOOTH_IS_DISABLED + SprintDevice.getPhoneNumber())) {
				mLastBluetoothOffMessageSentTime = currTime;
			}
		}
	}
	
	private void handleDriveWithClamshellOpen() {
		long currTime = System.currentTimeMillis();
		if(currTime - mLastOpenClamshellMessageSentTime >  300 * 1000 /* 5 minutes */) {
			if(alertAdmininstratorViaSMS(TEXT_NOTIFY_ADMIN_DRIVER_DRIVING_WITH_CLAMSHELL_OPEN)) {
				mLastOpenClamshellMessageSentTime = currTime;
			}
		}
	}
	
	private void handleDriveWhileInCall() {
		if(!mVehicleInMotionMessageSent) {
			alertAdmininstratorViaSMS(TEXT_NOTIFY_ADMIN_DRIVER_DRIVING_IN_CALL);
			mVehicleInMotionMessageSent = true;
		}
	}
	
	private void handleVehicleInMotion() {
		long currTime = System.currentTimeMillis();
		
		// check logic for how long we've been in the background
		if(mApplicationState == APPLICATION_PAUSED) {
			if(mVehicleInMotionTimeInBackground == 0) {
				mVehicleInMotionTimeInBackground = currTime;
			} else {
				// app has been in the background for over 30 seconds, it's safe to assume user is on the phone (?)
				long dt = currTime - mVehicleInMotionTimeInBackground;
				if(dt > 30000) {
					handleDriveWhileInCall();
				}
			}
		}
		
		// lock screen if necessary
		if(System.currentTimeMillis() > mNextLockScreenTime) {
			if(!isLocked()) {
				lockScreen();
				// Connectivity.startBlockingCalls();
			}
			
			// if application is in the background, bring to foreground
			if(mApplicationState == APPLICATION_PAUSED) {
				Notification.postNotification(NOTIFICATION_BRING_TO_FOREGROUND);
			}
			
			// if clamshell is open, notify admin
			if(SprintDevice.isClamshellOpen() == SprintDevice.TRUE) {
				handleDriveWithClamshellOpen();
			}

			// set default device settings
			SprintDevice.setSilent("ON");
			SprintDevice.setBacklight("DEFAULT");
			SprintDevice.setDDTM("OFF");
		}
	}
	
	private void handleVehicleStopped() {
		// restore saved value
		mLastMph = 0;
		
		// reset variables
		mVehicleInMotionTimeInBackground = 0;
		mVehicleInMotionMessageSent = false;
		
		// restore device settings
		SprintDevice.setSilent(mDeviceSettingsSilent);
		SprintDevice.setBacklight(mDeviceSettingsBacklight);
		SprintDevice.setDDTM(mDeviceSettingsDDTM);

		if(isLocked()) {
			unlockScreen();
			// Connectivity.stopBlockingCalls();
		}
	}
	
	private void handleSMSInterrupt() {
		// ignore sms (already done)
		// send interrupt notification to admin (optional)
		// alertAdministratorViaSMSIfCarIsInMotion(TEXT_NOTIFY_ADMIN_DRIVER_INCOMING_SMS);
		SMS.sendSMS(getSMSSender(), Settings.getAutoReplyText());
		Log.logInterrupt("* SMS interrupt receieved");
	}
	
	private void handleCallInterrupt() {
		// ignore call
		hangUpIncomingCall();
		// send interrupt notification to admin (optional)
		// alertAdministratorViaSMSIfCarIsInMotion(TEXT_NOTIFY_ADMIN_DRIVER_INCOMING_CALL);
		SMS.sendSMS(getCaller(), Settings.getAutoReplyText());
		Log.logInterrupt("* Call interrupt receieved");
	}
	
	private void handleVMailInterrupt() {
		// ignore vmail (already done)
		Log.logInterrupt("* Vmail interrupt receieved");
	}
	
	private void handlePTTInterrupt() {
		hangUpIncomingPTT();
		// send interrupt notification to admin (optional)
		// alertAdministratorViaSMSIfCarIsInMotion(TEXT_NOTIFY_ADMIN_DRIVER_INCOMING_PTT);
		SMS.sendSMS(getPTTSender(), Settings.getAutoReplyText());
		Log.logInterrupt("* PTT interrupt receieved");
	}
	
	private String getSMSSender() {
		// dyu: TODO - get incoming SMS sender
		return null;
	}
	
	private String getCaller() {
		// dyu: TODO - get incoming caller
		return null;
	}
	
	private String getPTTSender() {
		// dyu: TODO - get incoming PTT sender
		return null;
	}
	
	private void hangUpIncomingCall() {
		// dyu: ACCORDING TO ADP, no way to do this
	}
	
	private void hangUpIncomingPTT() {
		// dyu: ACCORDING TO ADP, no way to do this
	}
	
	/**
	 * This will actually set the next lock time
	 */
	public void setNextLockTime() {
		// override period measured in minutes
		long nextLockTime = System.currentTimeMillis() + Settings.getOverridePeriod() * 60 /* 1 min = 60 s */ * 1000 /* 1 sec = 1000 ms */;
		// overrides previous set locked times
		mNextLockScreenTime = nextLockTime;
		// alternative may be to set only dt, but that may be more dangerous to do
	}
	
	/**
	 * This will only set the next lock time if it's currently set to the future
	 */
	public void resetNextLockTimeIfActive() {
		if(mNextLockScreenTime > System.currentTimeMillis()) {
			setNextLockTime();
		}
	}

	/**
	 * Background thread constantly checks for the bluetooth & OBD availability
	 */
	public void run() {
		// always run
		while(true) {
			try {
				Thread.sleep(DEFAULT_CHECK_OBD_AVAILABILITY_TIME);
			} catch(Exception ex) {
				// loop just every 5 seconds
			}
			
			try {
				checkIsBluetoothOn();
				checkIsConnected();
				// TODO - remove notification to myself
				checkShouldNotifyAuthor();
			} catch(Exception ex) {
				// ignored
			}
		}
	}

	/***************************
	 * TEST CODE
	 ***************************/

	// shared debug variable
	private boolean mExecutingSpeedTest = false;
	
	//#if DEBUG_MODE
	private Timer mTestTimer = new Timer();
	private TimerTask mTestTimerTask = null;

	public void testObdSpeedQuery() {
		// toggle speed
		mExecutingSpeedTest = !mExecutingSpeedTest;

		if(mExecutingSpeedTest) {
			Log.log("-- Begin speed test --");
			mTestTimerTask = new TimerTask() {
				public void run() {
					Notification.postNotification(NOTIFICATION_OBD_SPEED_QUERY, new Integer(100));
				}
			};
			mTestTimer.schedule(mTestTimerTask, 7500, 3000);
		} else {
			Log.log("-- End speed test --");
			handleVehicleStopped();
			mTestTimerTask.cancel();
			mTestTimerTask = null;
		}
	}
	//#endif
}
