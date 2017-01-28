package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.connectivity.obd.OBDAgent;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIButton;
import com.devculture.drivealert.gui.components.UIImage;
import com.devculture.drivealert.gui.components.UILabel;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.notification.NotificationEventListener;

public class StartScreen extends ApplicationScreen implements NotificationEventListener {
	public static final int BUTTON_SETTINGS = 0;
	
	private final UIImage mLogoImage;
	private final UIImage mBtOnImage;
	private final UIImage mBtOffImage;
	private final UIButton mSettingsButton;
	private final UILabel mUrlLabel;
	private final UILabel mCopyrightLabel;
	
	// y-offsets
	private int mLogoTop = 20;
	private int mBTImageCenter = 0;
	private int mSettingsButtonTop = 0;
	private int mUrlLabelTop = 0;
	private int mCopyrightLabelTop = 0;
	
	//#if DEBUG_MODE
	private final UILabel mDebugLabel;
	private int mDebugLabelTop = 0;
	//#endif
	
	public StartScreen() {
		mLogoImage = new UIImage(IMAGE_LOGO_LRG);
		mBtOnImage = new UIImage(IMAGE_BT_ON);
		mBtOffImage = new UIImage(IMAGE_BT_OFF);
		
		mUrlLabel = new UILabel(TEXT_URL);
		mUrlLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
		mUrlLabel.setFont(FONT_SMALL);
		
		mCopyrightLabel = new UILabel(TEXT_COPYRIGHT);
		mCopyrightLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
		mCopyrightLabel.setFont(FONT_SMALL);
		
		mSettingsButton = new UIButton(TEXT_SETTINGS, (int)(getWidth() * 0.7f), DEFAULT_BUTTON_HEIGHT);
		mSettingsButton.setState(UIButtonStateHighlighted); // always selected
		
		//#if DEBUG_MODE
		mDebugLabel = new UILabel(TEXT_DEBUG_BUILD);
		mDebugLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
		mDebugLabel.setFont(FONT_SMALL_ITALIC);
		//#endif
	}
	
	private void layout() {
		mCopyrightLabelTop = getHeight() - mCopyrightLabel.getHeight() - 5;
		mUrlLabelTop = mCopyrightLabelTop - mUrlLabel.getHeight();
		mSettingsButtonTop = mUrlLabelTop - mSettingsButton.getHeight() - 20;
		int logoBottom = (mLogoTop + mLogoImage.getHeight());
		mBTImageCenter = ((mSettingsButtonTop - logoBottom) >> 1) + logoBottom - 5;

		//#if DEBUG_MODE
		mDebugLabelTop = mSettingsButtonTop - mDebugLabel.getHeight() - 10;
		//#endif
	}
	
	protected void onShow() {
		Notification.registerNotification(NOTIFICATION_OBD_CONNECTING, this);
		Notification.registerNotification(NOTIFICATION_OBD_CONNECTED, this);
		Notification.registerNotification(NOTIFICATION_OBD_DISCONNECTED, this);
		
		// layout screen elements
		layout();
	}

	protected void onHide() {
		Notification.removeNotification(NOTIFICATION_OBD_CONNECTING, this);
		Notification.removeNotification(NOTIFICATION_OBD_CONNECTED, this);
		Notification.removeNotification(NOTIFICATION_OBD_DISCONNECTED, this);
	}
	
	public boolean onNotificationReceived(int event, Object param) {
		repaint();
		return true;
	}

	protected void onPaint(Graphics g) {
		final int centerX = getWidth() / 2;
		UIImage btImage = Settings.getMasterOnOff() && OBDAgent.isConnected() ? mBtOnImage : mBtOffImage;
		mLogoImage.paint(g, centerX, 20, Graphics.TOP | Graphics.HCENTER);
		mSettingsButton.paint(g, centerX, mSettingsButtonTop, Graphics.TOP | Graphics.HCENTER);
		mUrlLabel.paint(g, centerX, mUrlLabelTop, Graphics.TOP | Graphics.HCENTER);
		mCopyrightLabel.paint(g, centerX, mCopyrightLabelTop, Graphics.TOP | Graphics.HCENTER);
		btImage.paint(g, centerX, mBTImageCenter, Graphics.HCENTER | Graphics.VCENTER);
		
		//#if DEBUG_MODE
		if(mDebugLabel != null) {
			mDebugLabel.paint(g, centerX, mDebugLabelTop, Graphics.TOP | Graphics.HCENTER);
		}
		//#endif
	}

	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		if(event == EVT_KEY_PRESSED) {
			if(param == KEY_FIRE) {
				String password = Settings.getPassword();
				if(password == null || password.length() == 0) {
					// ok to skip password screen
					pushState(APPSTATE_SETTINGS_SCREEN);
				} else {
					// goto password entry
					pushState(APPSTATE_ENTER_PASSWORD_SCREEN);
				}
				handled = true;
			}
		}
		return handled;
	}
}