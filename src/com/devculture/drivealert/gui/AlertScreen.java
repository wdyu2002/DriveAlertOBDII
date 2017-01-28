package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.devculture.drivealert.gui.components.UILabel;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.notification.Notification;

public class AlertScreen extends ApplicationScreen {
	private final static int SEPARATION_SMALL = 10;
	private final static int SEPARATION_LARGE = 15;
	
	private final int mAlertState;
	private final UILabel mLabel;
	private final Image mShroudImage;
	private final Image mErrorImage;
	private int mBoxHeight = 0;
	private int mBoxTop = 0;
	private int mLogoTop = 0;
	private int mTextTop = 0;
	
	public AlertScreen(int alertState) {
		mAlertState = alertState;
		mLabel = getLabel();
		mLabel.setTextColor(Theme.COLOR_RED);
		mLabel.setFont(FONT_SMALL);
		mShroudImage = getImage(IMAGE_SHROUD);
		mErrorImage = getImage(IMAGE_LOGO_SML);
	}
	
	private UILabel getLabel() {
		String text = TEXT_EMPTY;
		
		switch(mAlertState) {
		// dyu: ADD ALERT STATES HERE
		case APPSTATE_ALERT_OBD_DISCONNECTED_SCREEN:
			text = TEXT_ALERT_OBD_DISCONNECTED;
			break;
		case APPSTATE_ALERT_BLUETOOTH_DISCONNECTED_SCREEN:
			text = TEXT_ALERT_BLUETOOTH_DISCONNECTED;
			break;
		}
		
		return new UILabel(text, getDisplayWidth() - 30 * 2);
	}
	
	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		if(event == EVT_KEY_PRESSED) {
			switch(mAlertState) {
			// dyu: ADD ALERT STATES HERE
			case APPSTATE_ALERT_OBD_DISCONNECTED_SCREEN:
				// on disconnect, pop all and go to the start screen
				popAllStates();
				handled = true;
				break;
			case APPSTATE_ALERT_BLUETOOTH_DISCONNECTED_SCREEN:
				// app force entering background so that user can readjust bluetooth settings
				Notification.postNotification(NOTIFICATION_ENTER_BACKGROUND);
				popAllStates();
				handled = true;
				break;
			}
		}
		return handled;
	}
	
	public int getScreenType() {
		return SCREENTYPE_POPUP;
	}
	
	private void layout() {
		// calculate offsets & overall height of the elements
		mBoxHeight = SEPARATION_LARGE + mErrorImage.getHeight() + SEPARATION_LARGE + mLabel.getHeight() + SEPARATION_LARGE;
		mBoxTop = ((getDisplayHeight() - mBoxHeight)>>1) + SEPARATION_SMALL /* additional offset */;
		mLogoTop = mBoxTop + SEPARATION_LARGE;
		mTextTop = mLogoTop + mErrorImage.getHeight() + SEPARATION_LARGE;
	}
	
	protected void onShow() {
		// layout screen elements
		layout();
	}

	protected void onHide() {
		
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
		g.drawImage(mErrorImage, centerX, mLogoTop, Graphics.TOP | Graphics.HCENTER);
		mLabel.paint(g, centerX, mTextTop, Graphics.TOP | Graphics.HCENTER);
	}
}
