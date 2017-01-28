package com.devculture.drivealert.gui;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.gui.components.UIMenuItem;

public abstract class MenuScreen extends ApplicationScreen {
	private final UIMenuItem[] mMenuItems;
	private int mMenuItemsCount;
	private int mCurrentMenuItem = 0;
	private int mScreenOffset = 0;
	
	public MenuScreen(String title) {
		setTitle(title);
		mMenuItems = new UIMenuItem[30];
		mCurrentMenuItem = 0;
		mScreenOffset = 0;
	}

	protected UIMenuItem addMenuItem(int menuId, String title, String description, int accessoryType) {
		UIMenuItem result = new UIMenuItem(menuId, title, description, getWidth(), accessoryType);
		mMenuItems[mMenuItemsCount++] = result;
		return result;
	}

	protected void deselectAll() {
		for(int i=0; i<mMenuItemsCount; i++) {
			mMenuItems[i].setSelected(false);
		}
	}
	
	protected void onShow() {
		
	}

	protected void onHide() {
		
	}

	protected void onPaint(Graphics g) {
		int y = mScreenOffset;
		for(int i=0; i<mMenuItemsCount; i++) {
			mMenuItems[i].setState((i == mCurrentMenuItem) ? UIMenuItemStateHighlighted : UIMenuItemStateNormal);
			mMenuItems[i].paint(g, 0, y, Graphics.TOP | Graphics.LEFT);
			y += mMenuItems[i].getHeight();
		}
	}

	protected abstract void onMenuItemPressed(UIMenuItem menuItem);
	
	protected boolean onHandleEvent(int event, int param) {
		boolean handled = false;
		if(event == EVT_KEY_PRESSED) {
			switch(param) {
			case KEY_BACK:
				popState();
				handled = true;
				break;
			
			case KEY_UP:
				if(mCurrentMenuItem > 0) {
					mCurrentMenuItem--;
					
					// recalculate screenOffset to display menu item on screen
					int dy = 0;
					for(int i=0; i<mCurrentMenuItem; i++) {
						dy += mMenuItems[i].getHeight();
					}
					if(mScreenOffset < -dy) {
						mScreenOffset = - dy;
					}
				}
				break;
				
			case KEY_DOWN:
				if(mCurrentMenuItem < mMenuItemsCount-1) {
					mCurrentMenuItem++;

					// recalculate screenOffset to display menu item on screen
					int dy = 0;
					int screenHeight = getHeight();
					for(int i=0; i<=mCurrentMenuItem; i++) {
						dy += mMenuItems[i].getHeight();
					}
					if(screenHeight < dy + mScreenOffset) {
						mScreenOffset = screenHeight - dy;
					}
				}
				break;
				
			case KEY_FIRE:
				onMenuItemPressed(mMenuItems[mCurrentMenuItem]);
				break;
			}
		}
		return handled;
	}
}