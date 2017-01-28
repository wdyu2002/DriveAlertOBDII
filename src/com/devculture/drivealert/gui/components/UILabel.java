package com.devculture.drivealert.gui.components;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.devculture.drivealert.gui.themes.Theme;
import com.devculture.drivealert.utils.Rect;

public class UILabel extends UIComponent {
	public static final int DEFAULT_STRING_BUFFERSIZE = 1024;
	private String mText;
	private int mLength;
	private final int mMaxWidth;
	private int mLineHeight;
	private int mAscent;
	private int mDescent;
	private int mColor;
	private int mBgColor;
	private Font mFont;
	private final Vector mSubstrings = new Vector();
	
	public UILabel(String text) {
		if(text == null) {
			text = TEXT_EMPTY;
		}
		
		mText = text;
		mLength = text.length();
		mMaxWidth = -1;
		mBgColor = Theme.THEME_LABEL_BG_COLOR;
		mColor = Theme.THEME_LABEL_TEXT_COLOR;
		setFont(FONT_DEFAULT);
	}
	
	public UILabel(String text, int maxWidth) {
		if(text == null) {
			text = TEXT_EMPTY;
		}
		
		mText = text;
		mLength = text.length();
		mMaxWidth = maxWidth;
		mBgColor = Theme.THEME_LABEL_BG_COLOR;
		mColor = Theme.THEME_LABEL_TEXT_COLOR;
		setFont(FONT_DEFAULT);
	}
	
	public String getText() {
		return mText;
	}
	
	public void setText(String text) {
		if(text == null) {
			text = TEXT_EMPTY;
		}
		
		mText = text;
		mLength = text.length();
		relayout();
	}
	
	private int getNumCharsThatFitWithinMaxWidth(char[] chars, int startPos, int maxWidth) {
		int width = 0;
		int index = startPos;
		
		if(maxWidth == -1) {
			return chars.length-startPos;
		}
		
		if(maxWidth == 0) {
			return 0;
		}
		
		while(index < chars.length && width < maxWidth) {
			width += mFont.charWidth(chars[index]);
			index++;
		}
		
		return index-startPos;
	}
	
	private void relayout() {
		int fits = 0;
		int startPos = 0;
		int endPos = -1;
		int width = 0;
		int height = 0;
		int ascent = 0;
		int descent = 0;
		char[] chars = mText.toCharArray();
		
		// grab font metrics
		width = mMaxWidth < 0 ? mFont.stringWidth(mText) : mMaxWidth;
		height = mFont.getHeight();
		ascent = mFont.getBaselinePosition();
		descent = height-ascent;

		// recalculate substrings
		mSubstrings.removeAllElements();

		next:
		while(endPos < mLength-1)
		{
			startPos = endPos + 1;
			
			// find # of chars that fit
			fits = getNumCharsThatFitWithinMaxWidth(chars, startPos, width);
			endPos = startPos+fits-1;

			// nothing fits or all fits, just throw start->end into substring
			if(fits == 0 || endPos == mLength-1) {
				mSubstrings.addElement(new int[] {startPos, mLength-1});
				endPos = mLength-1;
				break;
			}

			// search to the previous space character
			for(int i=endPos; i>=startPos; --i) {
				if(chars[i] == ' ') {
					mSubstrings.addElement(new int[] {startPos, i});
					endPos = i;
					continue next;
				}
			}

			// search to the next space character
			for(int i=endPos; i<mLength; i++) {
				if(chars[i] == ' ') {
					mSubstrings.addElement(new int[] {startPos, i});
					endPos = i;
					continue next;
				}
			}

			// no spaces found in the remaining string, throw in the entire substring
			mSubstrings.addElement(new int[] {startPos, mLength-1});
			endPos = mLength-1;
		}

		mWidth = width;
		mLineHeight = height;
		mHeight = mSubstrings.size() * mLineHeight;
		mAscent = ascent;
		mDescent = descent;
	}

	public void setTextColor(int color) {
		mColor = color;
	}
	
	public void setBgColor(int bgcolor) {
		mBgColor = bgcolor;
	}

	public void setFont(Font font) {
		mFont = font;
		relayout();
	}
	
	public Font getFont() {
		return mFont;
	}
	
	public int getAscent() {
		return mAscent;
	}
	
	public int getDescent() {
		return mDescent;
	}
	
	public void paint(Graphics g, int x, int y, int alignment) {
		Font oldFont = g.getFont();
		Rect rect = Rect.getRectFromAlignment(new Rect(x, y, mWidth, mHeight), alignment);
		
		if(mText != null) {
			// skip painting the background if clear color
			if(Theme.isOpaqueColor(mBgColor)) {
				Theme.setColor(g, mBgColor);
				g.fillRect(rect.x, rect.y, rect.w, rect.h);
			}
			
			Theme.setColor(g, mColor);
			g.setFont(mFont);

			// render lines of text
			for(int i=0; i<mSubstrings.size(); i++) {
				int[] pos = (int[])mSubstrings.elementAt(i);
				g.drawSubstring(mText, pos[0], pos[1]-pos[0]+1, rect.x, rect.y+i*mLineHeight, Graphics.TOP | Graphics.LEFT);
			}

			g.setFont(oldFont);
		}
	}
}
