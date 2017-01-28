package com.devculture.drivealert.gui;

import java.io.IOException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIButton;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.notification.Notification;
import com.devculture.drivealert.notification.NotificationEventListener;

public class SyncScreen extends ApplicationScreen implements NotificationEventListener {
	private final static int SEPARATION_SMALL = 10;
	private final static int SEPARATION_LARGE = 15;
	
	public final static int BUTTON_RETRY = 0;
	public final static int BUTTON_CANCEL = 1;
	private int mSelectedItem = BUTTON_CANCEL; // default selected item
	
	private Image mShroudImage; 
	private Image mLogoImage;
	private String mString = TEXT_EMPTY;
	private Font mFont = FONT_MEDIUM_ITALIC;
	private UIButton mRetryButton;
	private UIButton mCancelButton;
	private int mBoxHeight = 0;
	private int mBoxTop = 0;
	private int mLogoTop = 0;
	private int mTextTop = 0;
	private int mRetryButtonTop = 0;
	private int mCancelButtonTop = 0;
	
	public SyncScreen() {
		try {
			mShroudImage = Image.createImage(IMAGE_SHROUD);
			mLogoImage = Image.createImage(IMAGE_LOGO_SML);
			mRetryButton = new UIButton(TEXT_RETRY, (int)(getDisplayWidth()*0.7f), DEFAULT_BUTTON_HEIGHT);
			mCancelButton = new UIButton(TEXT_CANCEL, (int)(getDisplayWidth()*0.7f), DEFAULT_BUTTON_HEIGHT);
			
			// initialize selected item
			setSelectedItem(mSelectedItem);
			
			// start to connect
			Settings.sync();
		} catch (IOException e) {
			// ignored
		}
	}
	
	public int getScreenType() {
		return SCREENTYPE_POPUP;
	}
	
	private void layout() {
		// calculate offsets & overall height of the elements
		mBoxHeight = SEPARATION_LARGE + mLogoImage.getHeight() + SEPARATION_LARGE + mFont.getHeight() + SEPARATION_LARGE + mRetryButton.getHeight() + SEPARATION_SMALL + mCancelButton.getHeight() + SEPARATION_LARGE;
		mBoxTop = ((getDisplayHeight() - mBoxHeight)>>1) + SEPARATION_SMALL /* additional offset */;
		mLogoTop = mBoxTop + SEPARATION_LARGE;
		mTextTop = mLogoTop + mLogoImage.getHeight() + SEPARATION_LARGE;
		mRetryButtonTop = mTextTop + mFont.getHeight() + SEPARATION_LARGE;
		mCancelButtonTop = mRetryButtonTop + mRetryButton.getHeight() + SEPARATION_SMALL;
	}
	
	protected void onShow() {
		Notification.registerNotification(NOTIFICATION_SETTINGS_SYNC_FAILED, this);
		Notification.registerNotification(NOTIFICATION_SETTINGS_SYNC_INPROGRESS, this);
		Notification.registerNotification(NOTIFICATION_SETTINGS_SYNC_SUCCESSFUL, this);
		
		// layout screen elements
		layout();
	}

	protected void onHide() {
		Notification.removeNotification(NOTIFICATION_SETTINGS_SYNC_FAILED, this);
		Notification.removeNotification(NOTIFICATION_SETTINGS_SYNC_INPROGRESS, this);
		Notification.removeNotification(NOTIFICATION_SETTINGS_SYNC_SUCCESSFUL, this);
	}

	public boolean onNotificationReceived(int event, Object param) {
		switch(event) {
		case NOTIFICATION_SETTINGS_SYNC_SUCCESSFUL:
			mString = TEXT_SYNC_SUCCESSFUL;
			onSyncSuccess();
			break;
		case NOTIFICATION_SETTINGS_SYNC_INPROGRESS:
			mString = TEXT_SYNC_INPROGRESS;
			setRetryDisabled();
			break;
		case NOTIFICATION_SETTINGS_SYNC_FAILED:
			mString = TEXT_SYNC_FAILED;
			onSyncError();
			break;
		}
		
		repaint();
		return true;
	}

	private void setRetryDisabled() {
		mSelectedItem = BUTTON_CANCEL;
		mRetryButton.setState(UIButtonStateDisabled);
		mCancelButton.setState(UIButtonStateHighlighted);
	}
	
	private void setSelectedItem(int item) {
		mSelectedItem = item;
		mRetryButton.setState(mSelectedItem == BUTTON_RETRY ? UIButtonStateHighlighted : UIButtonStateNormal);
		mCancelButton.setState(mSelectedItem == BUTTON_CANCEL ? UIButtonStateHighlighted : UIButtonStateNormal);
	}

	private void onSyncSuccess() {
		popState();
	}

	private void onSyncError() {
		setSelectedItem(mSelectedItem);
	}
	
	private void onUserRetry() {
		Settings.sync();
	}

	private void onUserCancelled() {
		// hide the screen, sync will continue anyway
		popState();
	}
	
	protected void onPaint(Graphics g) {
		final int screenWidth = getDisplayWidth();
		final int screenHeight = getDisplayHeight();
		final int centerX = screenWidth>>1;
		int boxBorderThickness = 1;
		
		// draw bg shroud
		for(int x=0; x<screenWidth; x+=30) {
			for(int y=0; y<screenHeight; y+=30) {
				g.drawImage(mShroudImage, x, y, Graphics.TOP | Graphics.LEFT);
			}
		}
		
		// fill outside (border)
		Theme.setColor(g, Theme.COLOR_WHITE);
		g.fillRoundRect(20-boxBorderThickness, mBoxTop-boxBorderThickness, screenWidth-40+boxBorderThickness*2, mBoxHeight+boxBorderThickness*2, 10, 10);

		// fill inside
		boxBorderThickness = 0;
		Theme.setColor(g, Theme.COLOR_BLACK);
		g.fillRoundRect(20-boxBorderThickness, mBoxTop-boxBorderThickness, screenWidth-40+boxBorderThickness*2, mBoxHeight+boxBorderThickness*2, 10, 10);
		
		// draw inner content
		g.setFont(mFont);
		Theme.setColor(g, Theme.COLOR_WHITE);
		g.drawImage(mLogoImage, centerX, mLogoTop, Graphics.TOP | Graphics.HCENTER);
		g.drawString(mString, centerX, mTextTop, Graphics.TOP | Graphics.HCENTER);
		mRetryButton.paint(g, centerX, mRetryButtonTop, Graphics.TOP | Graphics.HCENTER);
		mCancelButton.paint(g, centerX, mCancelButtonTop, Graphics.TOP | Graphics.HCENTER);
	}

	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		if(event == EVT_KEY_PRESSED) {
			if(param == KEY_UP && mSelectedItem == BUTTON_CANCEL && mRetryButton.getState() != UIButtonStateDisabled) {
				setSelectedItem(BUTTON_RETRY);
				handled = true;
			} else if(param == KEY_DOWN && mSelectedItem == BUTTON_RETRY) {
				setSelectedItem(BUTTON_CANCEL);
				handled = true;
			} else if(param == KEY_FIRE && mSelectedItem == BUTTON_CANCEL) {
				onUserCancelled();
				handled = true;
			} else if(param == KEY_FIRE && mSelectedItem == BUTTON_RETRY && mRetryButton.getState() != UIButtonStateDisabled) {
				onUserRetry();
				handled = true;
			}
		}
		return handled;
	}

}
