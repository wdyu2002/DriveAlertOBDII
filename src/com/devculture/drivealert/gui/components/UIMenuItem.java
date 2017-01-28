package com.devculture.drivealert.gui.components;

import javax.microedition.lcdui.Graphics;

import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.utils.Rect;

public class UIMenuItem extends UIComponent {
	public static final int MINIMUM_HEIGHT = 30;
	public static final int PADDING_TOP = 3;
	public static final int PADDING_BOTTOM = 3;
	public static final int PADDING_LEFT = 5;
	public static final int PADDING_RIGHT = 5;

	private int mMenuId;
	private String mTitle;
	private String mDescription;
	private UILabel mTitleLabel;
	private UILabel mDescriptionLabel;
	// private int mAccessoryType;
	private int mCurrState;
	private boolean mSelected;
	private int[] mBgColor = new int[2];
	private int[] mTextColor = new int[2];
	private UIImage[] mAccessoryImage = new UIImage[2];
	
	public UIMenuItem(int menuId, String title, String description, int width, int accessoryType) {
		// int length = 0;
		mTitle = title;
		mDescription = description;
		mMenuId = menuId;
		mCurrState = UIMenuItemStateNormal;
		mSelected = false;
		
		// label max width sans padding
		final int labelMaxWidth = width - PADDING_LEFT - PADDING_RIGHT - ((accessoryType != UIMenuItemAccessoryTypeNone) ? 20 : 0);
		int mTitleLabelHeight = 0;
		int mDescriptionLabelHeight = 0;

		// set default fonts
		mTitleLabel = new UILabel(mTitle, labelMaxWidth);
		mTitleLabel.setFont(FONT_MEDIUM_BOLD);
		mTitleLabelHeight = mTitleLabel.getHeight();
		mDescriptionLabel = new UILabel(mDescription, labelMaxWidth);
		mDescriptionLabel.setFont(FONT_SMALL_ITALIC);
		mDescriptionLabelHeight = mDescriptionLabel.getHeight();
		
		// set colors	
		mBgColor[UIMenuItemStateNormal] = Theme.THEME_MENUITEM_NORMAL_BG_COLOR;
		mBgColor[UIMenuItemStateHighlighted] = Theme.THEME_MENUITEM_HIGHLIGHTED_BG_COLOR;
		mTextColor[UIMenuItemStateNormal] = Theme.THEME_MENUITEM_NORMAL_TEXT_COLOR;
		mTextColor[UIMenuItemStateHighlighted] = Theme.THEME_MENUITEM_HIGHLIGHTED_TEXT_COLOR;
		
		switch(accessoryType) {
		case UIMenuItemAccessoryTypeNone:
			mAccessoryImage[0] = null;
			mAccessoryImage[1] = null;
			break;
		case UIMenuItemAccessoryTypeCheckbox:
			mAccessoryImage[0] = new UIImage(IMAGE_CHECKBOX_OFF);
			mAccessoryImage[1] = new UIImage(IMAGE_CHECKBOX_ON);
			break;
		case UIMenuItemAccessoryTypeRadio:
			mAccessoryImage[0] = new UIImage(IMAGE_RADIO_OFF);
			mAccessoryImage[1] = new UIImage(IMAGE_RADIO_ON);
			break;
		case UIMenuItemAccessoryTypeNavigate:
			mAccessoryImage[0] = new UIImage(IMAGE_NAVI);
			mAccessoryImage[1] = new UIImage(IMAGE_NAVI);
			break;
		}
		
		mWidth = width;
		mHeight = Math.max(mTitleLabelHeight + mDescriptionLabelHeight + PADDING_TOP + PADDING_BOTTOM + 1 , MINIMUM_HEIGHT);
	}

	public void setState(int state) {
		mCurrState = state;
	}

	public void setBgColorForState(int state, int color) {
		mBgColor[state] = color;
	}

	public void setTextColorForState(int state, int color) {
		mTextColor[state] = color;
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public int getMenuId() {
		return mMenuId;
	}

	public void paint(Graphics g, int x, int y, int alignment) {
		Rect rect = Rect.getRectFromAlignment(new Rect(x, y, getWidth(), getHeight()), alignment);

		// got to paint the entire block wtih bg color
		Theme.setColor(g, mBgColor[mCurrState]);
		g.fillRect(rect.x, rect.y, rect.w, rect.h-1);

		// render title
		if(mTitleLabel != null) {
			mTitleLabel.setTextColor(mTextColor[mCurrState]);

			// draw title centered, if no description label is available
			if(mDescriptionLabel == null) {
				mTitleLabel.paint(g, rect.x + PADDING_LEFT, rect.y + getHeight()/2, Graphics.VCENTER | Graphics.LEFT);
			} else {
				mTitleLabel.paint(g, rect.x + PADDING_LEFT, rect.y + PADDING_TOP, Graphics.TOP | Graphics.LEFT);
			}
		}

		// render description
		if(mDescriptionLabel != null) {
			mDescriptionLabel.setTextColor(mTextColor[mCurrState]);
			mDescriptionLabel.paint(g, rect.x + PADDING_LEFT, rect.y + PADDING_TOP + mTitleLabel.getHeight(), Graphics.TOP | Graphics.LEFT);
		}

		// render accessory image
		UIImage accessoryImage = mAccessoryImage[(!mSelected ? 0 : 1)];
		if(accessoryImage != null) {
			accessoryImage.paint(g, rect.x + getWidth() - PADDING_RIGHT, rect.y + getHeight()/2, Graphics.VCENTER | Graphics.RIGHT);
		}

		// draw border
		Theme.setColor(g, Theme.THEME_MENU_HR_COLOR);
		g.drawLine(rect.x + PADDING_LEFT, rect.y + getHeight() - 1, getWidth() - PADDING_LEFT - PADDING_RIGHT, rect.y + getHeight() - 1);
	}
}
