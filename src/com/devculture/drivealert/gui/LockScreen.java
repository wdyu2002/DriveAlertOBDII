package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.data.Settings;
import com.devculture.drivealert.gui.components.UIImage;
import com.devculture.drivealert.gui.components.UILabel;
import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.notification.Notification;

public class LockScreen extends ApplicationScreen {
	private final UIImage mLogoImage;
	private final UIImage mLockImage;
	private final UILabel mOverrideInstructionsLabel;
	private final static int OVERRIDE_KEY_LENGTH  = 5;
	private int mLogoTop = 20;
	private int mOverrideInstructionsLabelTop = 0;
	private int mLockImageCenter = 0;
	private final int[] mOverrideKey = {KEY_NUM5, KEY_NUM5, KEY_NUM5, KEY_NUM5, KEY_NUM5};
	private final int[] mOverrideEntry = {KEY_NONE, KEY_NONE, KEY_NONE, KEY_NONE, KEY_NONE};
	private long mEntryBeginTime = 0;
	
	public LockScreen() {
		super();
		mLogoImage = new UIImage(IMAGE_LOGO_LRG);
		mLockImage = new UIImage(IMAGE_LOCK_LRG);
		mOverrideInstructionsLabel = new UILabel(TEXT_OVERRIDE_INSTRUCTIONS);
		mOverrideInstructionsLabel.setFont(FONT_SMALL);
		mOverrideInstructionsLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
	}

	private void layout() {
		int logoBottom = (mLogoTop + mLogoImage.getHeight());
		mOverrideInstructionsLabelTop = getHeight() - mOverrideInstructionsLabel.getHeight() - 5;
		mLockImageCenter = ((mOverrideInstructionsLabelTop - logoBottom) >> 1) + logoBottom - 5;
	}
	
	protected void onShow() {
		// layout screen elements
		layout();
	}

	protected void onHide() {
		
	}

	protected void onPaint(Graphics g) {
		final int centerX = getWidth() / 2;
		mLogoImage.paint(g, centerX, mLogoTop, Graphics.TOP | Graphics.HCENTER);
		mLockImage.paint(g, centerX, mLockImageCenter, Graphics.VCENTER | Graphics.HCENTER);
		// if enable lock-override, print the instructions
		if(Settings.getEnableOverride()) {
			mOverrideInstructionsLabel.paint(g, centerX, mOverrideInstructionsLabelTop, Graphics.TOP | Graphics.HCENTER);
		}
	}

	protected boolean onHandleEvent(int event, int param) {
		// if don't enable lock-override, ignore keypresses
		if(!Settings.getEnableOverride()) {
			return true;
		}
		
		if(event == EVT_KEY_PRESSED) {
			long currTime = System.currentTimeMillis();
			if(currTime - mEntryBeginTime > 5000) {
				mOverrideEntry[OVERRIDE_KEY_LENGTH-1] = KEY_NONE;
				mEntryBeginTime = currTime;
			}
			
			// assume match (unless proven otherwise)
			boolean match = true;
			
			// compare with offset 1
			for(int i=0; i<OVERRIDE_KEY_LENGTH-1; i++) {
				if(mOverrideKey[i] != mOverrideEntry[i+1]) {
					match = false;
				}
				// shift while comparing
				mOverrideEntry[i] = mOverrideEntry[i+1];
			}

			// compare last entry with key-entered
			if(mOverrideKey[OVERRIDE_KEY_LENGTH-1] != param) {
				match = false;
			}
			mOverrideEntry[OVERRIDE_KEY_LENGTH-1] = param;
			
			// unlocked !
			if(match) {
				Application.getLogic().setNextLockTime();
				Notification.postNotification(NOTIFICATION_LOCK_OVERRIDE);
				Notification.postNotification(NOTIFICATION_OBD_SPEED_QUERY, new Integer(0));
			}
		}
		
		return true;
	}
}
