package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.gui.components.UILabel;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.notification.NotificationEventListener;
import com.devculture.drivealert.utils.Log;

public class LogScreen extends ApplicationScreen implements NotificationEventListener {
	private UILabel[] mLabels = null;
	private int mCurrentLabel = 0;
	private int mScreenOffset = 0;
	
	public LogScreen(String title) {
		setTitle(title);
		setRSK("Clear logs");
		mCurrentLabel = 0;
		mScreenOffset = 0;
	}
	
	private void clearLogs() {
		mLabels = null;
		mCurrentLabel = 0;
		mScreenOffset = 0;
		Log.getInstance().clear();
	}

	protected void onShow() {
		Notification.registerNotification(NOTIFICATION_LOG_UPDATED, this);
		mLabels = Log.getInstance().getLabelsArrayCopy();
	}

	protected void onHide() {
		Notification.removeNotification(NOTIFICATION_LOG_UPDATED, this);
	}

	protected void onPaint(Graphics g) {
		if(mLabels != null) {
			int y = mScreenOffset;
			final int labelsCount = mLabels.length;
			for(int i=0; i<labelsCount; i++) {
				mLabels[i].setBgColor((i == mCurrentLabel) ? Theme.THEME_MENUITEM_HIGHLIGHTED_BG_COLOR : Theme.THEME_MENUITEM_NORMAL_BG_COLOR);
				mLabels[i].setTextColor((i == mCurrentLabel) ? Theme.THEME_MENUITEM_HIGHLIGHTED_TEXT_COLOR : Theme.THEME_MENUITEM_NORMAL_TEXT_COLOR);
				mLabels[i].paint(g, 0, y, Graphics.TOP | Graphics.LEFT);
				y += mLabels[i].getHeight();
			}
		}
	}

	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		if(event == EVT_KEY_PRESSED) {
			switch(param) {
			case KEY_UP:
				if(mCurrentLabel > 0) {
					mCurrentLabel--;
					
					// recalculate screenOffset to display menu item on screen
					int dy = 0;
					for(int i=0; i<mCurrentLabel; i++) {
						dy += mLabels[i].getHeight();
					}
					if(mScreenOffset < -dy) {
						mScreenOffset = - dy;
					}
				}
				break;
				
			case KEY_DOWN:
				if(mCurrentLabel < mLabels.length-1) {
					mCurrentLabel++;

					// recalculate screenOffset to display menu item on screen
					int dy = 0;
					int screenHeight = getHeight();
					for(int i=0; i<=mCurrentLabel; i++) {
						dy += mLabels[i].getHeight();
					}
					if(screenHeight < dy + mScreenOffset) {
						mScreenOffset = screenHeight - dy;
					}
				}
				break;
				
			case KEY_RSK:
				clearLogs();
				handled = true;
				break;

			case KEY_BACK:
			case KEY_STAR:
				popState();
				handled = true;
				break;
			}
		}
		return handled;
	}

	public boolean onNotificationReceived(int event, Object param) {
		switch(event) {
		case NOTIFICATION_LOG_UPDATED:
			mLabels = (UILabel[])param;
			repaint();
			return true;
		}
		return false;
	}
}
