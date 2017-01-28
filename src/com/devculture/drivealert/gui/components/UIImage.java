package com.devculture.drivealert.gui.components;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.devculture.drivealert.utils.Log;

public class UIImage extends UIComponent {
	private Image mImage;
	
	public UIImage(String path) {
		try {
			mImage = Image.createImage(path);
			mWidth = mImage.getWidth();
			mHeight = mImage.getHeight();
		} catch (Exception ex) {
			Log.log("Failed to create image " + path);
		}
	}
	
	public void paint(Graphics g, int x, int y, int alignment) {
		g.drawImage(mImage, x, y, alignment);
	}
}
