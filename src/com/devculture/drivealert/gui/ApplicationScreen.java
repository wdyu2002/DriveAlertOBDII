package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.devculture.drivealert.Application;
import com.devculture.drivealert.ApplicationLogic;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.gui.components.UILabel;
import com.devculture.drivealert.gui.themes.Theme;

public abstract class ApplicationScreen implements Globals {
	private static final int TITLE_HPADDING = 10;
	private static final int TITLE_VPADDING = 30;
	private static final int MENUBAR_HPADDING = 3;
	private static final int MENUBAR_VPADDING = 10;

	private final ApplicationLogic mLogic;
	private final Font mFont = FONT_DEFAULT;
	private UILabel mTitleLabel;
	private UILabel mLskLabel;
	private UILabel mRskLabel;
	private int mTitleHeight = 0;
	private int mMenubarHeight = 0;
	
	protected ApplicationScreen() {
		mLogic = Application.getLogic();
	}
	
	public int getScreenType() {
		return SCREENTYPE_FULL;
	}
	
	public void show() {
		// always set displayable to main canvas first
		Application.setDisplayable(mLogic);
		
		// subclass onShow
		onShow();
	}
	
	public void hide() {
		onHide();
	}
	
	public void paint(Graphics g) {
		// render content
		g.translate(0, mTitleHeight);
		g.setClip(0, 0, getWidth(), getHeight());
		onPaint(g);
		g.translate(0, -mTitleHeight);
		g.setFont(mFont);
		
		// render titlebar
		if(mTitleHeight > 0) {
			// fill bg
			Theme.setColor(g, Theme.THEME_TITLE_BG_COLOR);
			g.setClip(0, 0, getWidth(), mTitleHeight);
			g.fillRect(0, 0, getWidth(), mTitleHeight);
			// draw separation
			Theme.setColor(g, Theme.THEME_TITLE_HR_COLOR);
			g.drawLine(0, mTitleHeight-1, getWidth(), mTitleHeight-1);
			// draw text label
			mTitleLabel.paint(g, (TITLE_HPADDING>>1), (TITLE_VPADDING>>1) + 6, Graphics.TOP | Graphics.LEFT);
		}
		
		// render menubar
		if(mMenubarHeight > 0) {
			// fill bg
			Theme.setColor(g, Theme.THEME_MENUBAR_BG_COLOR);
			g.setClip(0, getDisplayHeight()-mMenubarHeight, getDisplayWidth(), mMenubarHeight);
			g.fillRect(0, getDisplayHeight()-mMenubarHeight, getDisplayWidth(), mMenubarHeight);
			// draw separation
			Theme.setColor(g, Theme.THEME_MENUBAR_HR_COLOR);
			g.drawLine(0, getDisplayHeight()-mMenubarHeight, getWidth(), getDisplayHeight()-mMenubarHeight);
			// draw text label
			if(mLskLabel != null) mLskLabel.paint(g, MENUBAR_HPADDING, getDisplayHeight()-(MENUBAR_VPADDING>>1)+1, Graphics.BOTTOM | Graphics.LEFT);
			if(mRskLabel != null) mRskLabel.paint(g, getDisplayWidth()-MENUBAR_HPADDING, getDisplayHeight()-(MENUBAR_VPADDING>>1)+1, Graphics.BOTTOM | Graphics.RIGHT);
		}
	}
	
	public void handleEvent(int event, int param) {
		// subclass handle events
		onHandleEvent(event, param);
	}
	
	protected abstract void onShow();
	protected abstract void onHide();
	protected abstract void onPaint(Graphics g);
	
	protected void repaint() {
		mLogic.repaint();
	}
	
	protected abstract boolean onHandleEvent(int event, int param);

	protected int getWidth() {
		return mLogic.getWidth();
	}
	
	protected int getHeight() {
		return mLogic.getHeight() - mTitleHeight - mMenubarHeight;
	}
	
	protected int getDisplayWidth() {
		return mLogic.getWidth();
	}
	
	protected int getDisplayHeight() {
		return mLogic.getHeight();
	}
	
	protected void setLSK(String lsk) {
		if(lsk != null) {
			mLskLabel = new UILabel(lsk);
			mLskLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
			mLskLabel.setFont(FONT_MEDIUM_BOLD);
			mMenubarHeight = mLskLabel.getHeight() + MENUBAR_VPADDING;
		} else {
			mLskLabel = null;
			mMenubarHeight = mRskLabel == null ? 0 : mMenubarHeight;
		}
	}
	
	protected String getLSK() {
		if(mLskLabel != null) {
			return mLskLabel.getText();
		}
		return null;
	}
	
	protected void setRSK(String rsk) {
		if(rsk != null) {
			mRskLabel = new UILabel(rsk);
			mRskLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
			mRskLabel.setFont(FONT_MEDIUM_BOLD);
			mMenubarHeight = mRskLabel.getHeight() + MENUBAR_VPADDING;
		} else {
			mRskLabel = null;
			mMenubarHeight = mLskLabel == null ? 0 : mMenubarHeight;
		}
	}
	
	protected String getRSK() {
		if(mRskLabel != null) {
			return mRskLabel.getText();
		}
		return null;
	}

	protected void setTitle(String title) {
		if(title != null) {
			mTitleLabel = new UILabel(title, getWidth() - TITLE_HPADDING);
			mTitleLabel.setTextColor(Theme.THEME_TITLE_TEXT_COLOR);
			mTitleLabel.setFont(FONT_MEDIUM_BOLD);
			mTitleHeight = mTitleLabel.getHeight() + TITLE_VPADDING;
		} else {
			mTitleLabel = null;
			mTitleHeight = 0;
		}
	}
	
	protected void pushState(int state) {
		mLogic.pushState(state);
	}

	protected void popState() {
		mLogic.popState();
	}
	
	protected void popAllStates() {
		mLogic.popAllStates();
	}
	
	protected Image getImage(String imageName) {
		Image image = null;
		try {
			image = Image.createImage(imageName);
		} catch(Exception ex) {
			// return a red 10x10 image to denote failure
			image = Image.createImage(10, 10);
			image.getGraphics().setColor(0xff0000);
			image.getGraphics().fillRect(0, 0, 10, 10);
		}
		return image;
	}
}
