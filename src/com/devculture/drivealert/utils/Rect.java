package com.devculture.drivealert.utils;

import javax.microedition.lcdui.Graphics;

public class Rect {
	public int x;
	public int y;
	public int w;
	public int h;
	
	public Rect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public static Rect getRectFromAlignment(Rect rect, int alignment) {
		if((alignment & Graphics.LEFT) != 0) {
			// left, do nothing
		} else if((alignment & Graphics.RIGHT) != 0) {
			rect.x -= rect.w;
		} else if((alignment & Graphics.HCENTER) != 0) {
			rect.x -= rect.w >> 1;
		}
		if((alignment & Graphics.TOP) != 0) {
			// top, do nothing
		} else if((alignment & Graphics.VCENTER) != 0) {
			rect.y -= rect.h >> 1;
		} else if((alignment & Graphics.BOTTOM) != 0) {
			rect.y -= rect.h;
		} else if((alignment & Graphics.BASELINE) != 0) {
			rect.y -= rect.h;
		}
		return rect;
	}

	public static int getHorizontalAlignment(int alignment) {
		if((alignment & Graphics.LEFT) != 0) {
			return Graphics.LEFT;
		} else if((alignment & Graphics.RIGHT) != 0) {
			return Graphics.RIGHT;
		} else if((alignment & Graphics.HCENTER) != 0) {
			return Graphics.HCENTER;
		}
		return Graphics.LEFT;
	}
	
	public static int getVerticalAlignment(int alignment) {
		if((alignment & Graphics.TOP) != 0) {
			return Graphics.TOP;
		} else if((alignment & Graphics.VCENTER) != 0) {
			return Graphics.VCENTER;
		} else if((alignment & Graphics.BOTTOM) != 0) {
			return Graphics.BOTTOM;
		} else if((alignment & Graphics.BASELINE) != 0) {
			return Graphics.BASELINE; 
		}
		return Graphics.TOP;
	}
}

