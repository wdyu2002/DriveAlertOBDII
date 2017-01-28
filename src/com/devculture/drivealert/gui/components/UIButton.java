package com.devculture.drivealert.gui.components;

import javax.microedition.lcdui.Graphics;

import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.utils.Rect;

public class UIButton extends UIComponent {
	private int mCurrState;
	private UILabel mTextLabel;
	private UIImage[] mBgImage = new UIImage[4];
	private int[] mTextColor = new int[4];
	
	public UIButton(String text, int width, int height) {
		mCurrState = UIButtonStateNormal;
		mTextLabel = new UILabel(text);
		mBgImage[UIButtonStateNormal] = null;
		mBgImage[UIButtonStateHighlighted] = null;
		mBgImage[UIButtonStatePressed] = null;
		mBgImage[UIButtonStateDisabled] = null;
		mTextColor[UIButtonStateNormal] = Theme.THEME_BUTTON_NORMAL_TEXT_COLOR;
		mTextColor[UIButtonStateHighlighted] = Theme.THEME_BUTTON_HIGHLIGHTED_TEXT_COLOR;
		mTextColor[UIButtonStatePressed] = Theme.THEME_BUTTON_SELECTED_TEXT_COLOR;
		mTextColor[UIButtonStateDisabled] = Theme.THEME_BUTTON_DISABLED_TEXT_COLOR;
		
		mWidth = width;
		mHeight = height;
	}
	
	public void setState(int state) {
		mCurrState = state;
	}
	
	public int getState() {
		return mCurrState;
	}
	
	public void setBgImageForState(int state, String path) {
		mBgImage[state] = new UIImage(path);
	}
	
	public void setText(String text) {
		mTextLabel.setText(text);
	}
	
	public void setTextColorForState(int state, int color) {
		mTextColor[state] = color;
	}
	
	public void paint(Graphics g, int x, int y, int alignment) {
		int tmp = UIButtonStateNormal;
		int backgroundColor;
	
		if(mBgImage[mCurrState] != null) {
			tmp = mCurrState;
		}

		if(mBgImage[tmp] != null) {
			mBgImage[tmp].paint(g, x, y, alignment);
		}

		// calculate drawing rect
		Rect rect = Rect.getRectFromAlignment(new Rect(x, y, getWidth(), getHeight()), alignment);
		Rect border = new Rect(rect.x-2, rect.y-2, rect.w+4, rect.h+4);
		
		switch(mCurrState) {
		case UIButtonStateHighlighted:
			backgroundColor = Theme.THEME_BUTTON_HIGHLIGHTED_BG_COLOR;
			break;
		case UIButtonStatePressed:
			backgroundColor = Theme.THEME_BUTTON_SELECTED_BG_COLOR;
			break;
		case UIButtonStateDisabled:
			backgroundColor = Theme.THEME_BUTTON_DISABLED_BG_COLOR;
			break;
		default:
		case UIButtonStateNormal:
			backgroundColor = Theme.THEME_BUTTON_NORMAL_BG_COLOR;
			break;
		}

		// draw frame
		Theme.setColor(g, backgroundColor);
		g.fillRect(rect.x, rect.y, rect.w, rect.h);
		
		// draw raised frame (top-left)
		Theme.setColor(g, Theme.THEME_BUTTON_BORDER_TOPLEFT_COLOR);
		g.fillRect(border.x, border.y, border.w, 2);
		g.fillRect(border.x, border.y, 2, border.h);
		
		// draw raised frame (bot-right)
		Theme.setColor(g, Theme.THEME_BUTTON_BORDER_BOTRIGHT_COLOR);
		g.fillRect(border.x + border.w - 2, border.y, 2, border.h);
		g.fillRect(border.x, border.y + border.h - 2, border.w, 2);
	
		// set text color based on curr state
		mTextLabel.setTextColor(mTextColor[mCurrState]);
		mTextLabel.paint(g, rect.x + rect.w/2, rect.y + rect.h/2, Graphics.HCENTER | Graphics.VCENTER);
	}
}