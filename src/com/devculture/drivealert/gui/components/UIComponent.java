package com.devculture.drivealert.gui.components;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.Globals;

public abstract class UIComponent implements Globals {
	protected int mWidth;
	protected int mHeight;

	public abstract void paint(Graphics g, int x, int y, int alignment);
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getHeight() {
		return mHeight;
	}
}