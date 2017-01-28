package com.devculture.drivealert.gui.themes;

import javax.microedition.lcdui.Graphics;
import com.devculture.drivealert.Globals;

public class Theme implements Globals {
	
	/****************
	 * BASIC COLORS
	 ****************/
	
	public static final int COLOR_CLEAR		= 0x0;
	public static final int COLOR_WHITE 	= 0xffffffff;
	public static final int COLOR_BLACK 	= 0xff101010;
	public static final int COLOR_RED		= 0xffcc0000;
	public static final int COLOR_GRAY40	= 0xff606060;
	public static final int COLOR_GRAY45 	= 0xff777777;
	public static final int COLOR_GRAY60	= 0xff999999;
	public static final int COLOR_GRAY80 	= 0xffcccccc;
	
	/****************
	 * THEME COLORS
	 ****************/
	
	public static final int THEME_BITFLAG								= 0x74000000;
	public static final int THEME_SCREEN_BG_COLOR						= THEME_BITFLAG | 10;
	public static final int THEME_MENU_HR_COLOR 						= THEME_BITFLAG | 11;
	
	public static final int THEME_TITLE_BG_COLOR 						= THEME_BITFLAG | 20;
	public static final int THEME_TITLE_TEXT_COLOR 						= THEME_BITFLAG | 21;
	public static final int THEME_TITLE_HR_COLOR 						= THEME_BITFLAG | 22;

	public static final int THEME_MENUBAR_BG_COLOR 						= THEME_BITFLAG | 30;
	public static final int THEME_MENUBAR_TEXT_COLOR 					= THEME_BITFLAG | 31;
	public static final int THEME_MENUBAR_HR_COLOR 						= THEME_BITFLAG | 32;
	
	public static final int THEME_LABEL_BG_COLOR						= THEME_BITFLAG | 40;
	public static final int THEME_LABEL_TEXT_COLOR 						= THEME_BITFLAG | 41;
	
	public static final int THEME_MENUITEM_NORMAL_BG_COLOR				= THEME_BITFLAG | 50;
	public static final int THEME_MENUITEM_NORMAL_TEXT_COLOR 			= THEME_BITFLAG | 51;
	public static final int THEME_MENUITEM_HIGHLIGHTED_BG_COLOR 		= THEME_BITFLAG | 52;
	public static final int THEME_MENUITEM_HIGHLIGHTED_TEXT_COLOR 		= THEME_BITFLAG | 53;
	
	public static final int THEME_BUTTON_BORDER_TOPLEFT_COLOR			= THEME_BITFLAG | 60;
	public static final int THEME_BUTTON_BORDER_BOTRIGHT_COLOR			= THEME_BITFLAG | 61;
	public static final int THEME_BUTTON_NORMAL_BG_COLOR				= THEME_BITFLAG | 62;
	public static final int THEME_BUTTON_NORMAL_TEXT_COLOR				= THEME_BITFLAG | 63;
	public static final int THEME_BUTTON_HIGHLIGHTED_BG_COLOR 			= THEME_BITFLAG | 64;
	public static final int THEME_BUTTON_HIGHLIGHTED_TEXT_COLOR 		= THEME_BITFLAG | 65;
	public static final int THEME_BUTTON_SELECTED_TEXT_COLOR 			= THEME_BITFLAG | 66;
	public static final int THEME_BUTTON_SELECTED_BG_COLOR 				= THEME_BITFLAG | 67;
	public static final int THEME_BUTTON_DISABLED_TEXT_COLOR 			= THEME_BITFLAG | 68;
	public static final int THEME_BUTTON_DISABLED_BG_COLOR 				= THEME_BITFLAG | 69;
	
	/****************
	 * THEMES
	 ****************/
	
	public static final int THEME_BLUE = 0;
	public static final int THEME_YELLOW = 1;
	public static final int THEME_PURPLE = 2;
	public static final int THEME_GREEN = 3;
	public static final int THEME_GRAY = 4;
	public static final int THEME_BLACK = 5;
	public static final int THEME_COUNT = 6;
	
	private static final int DARK_BG_COLOR_INDEX = 0;
	private static final int NORMAL_BG_COLOR_INDEX = 1;
	private static final int LIGHT_BG_COLOR_INDEX = 2;
	private static final int DARK_TEXT_COLOR_INDEX = 3;
	private static final int NORMAL_TEXT_COLOR_INDEX = 4;
	private static final int LIGHT_TEXT_COLOR_INDEX = 5;

	// default theme
	public static final int THEME_DEFAULT = THEME_BLUE;
	private static int mTheme = THEME_DEFAULT;

	// Come here to add new themes - 1/2
	private static int[][] mPresetThemeColors = new int[/* theme */][/* color indices */] {
		{ 0xff004165, 0xff3892ab, 0xffaed5e1, 0xffffffff, 0xff000000, 0xff000000 }, // blue
		{ 0xffa17700, 0xffeaab00, 0xfff8de6e, 0xffffffff, 0xff000000, 0xff000000 }, // yellow
		{ 0xff56364d, 0xff8f6678, 0xffb594a1, 0xffffffff, 0xffffffff, 0xff000000 }, // purple
		{ 0xff618e02, 0xff9eab05, 0xffdceea4, 0xffffffff, 0xff000000, 0xff000000 }, // green
		{ 0xff5b676f, 0xff818a8f, 0xffc3c8c8, 0xffffffff, 0xff000000, 0xff000000 }, // gray
		{ 0xff121212, 0xff777777, 0xffcccccc, 0xffffffff, 0xff000000, 0xff000000 }, // black
	};
	
	// Come here to add new themes - 2/2
	private static String[] mPresetThemeColorsString = new String[] {
		TEXT_THEME_BLUE,
		TEXT_THEME_YELLOW,
		TEXT_THEME_PURPLE,
		TEXT_THEME_GREEN,
		TEXT_THEME_GRAY,
		TEXT_THEME_BLACK,
	};
	
	private Theme() {
		
	}
	
	public static String getThemeName(int theme) {
		return mPresetThemeColorsString[theme];
	}
	
	public static void setTheme(int theme) {
		mTheme = theme >= THEME_COUNT || theme < 0 ? THEME_BLUE : theme;
	}
	
	public static boolean isOpaqueColor(int rgbOrThemeColor) {
		int argb = ((rgbOrThemeColor & 0xff000000) == THEME_BITFLAG) ? getRGBFromTheme(rgbOrThemeColor) : rgbOrThemeColor;
		return ((argb>>24) & 0xff) != 0;
	}
	
	public static void setColor(Graphics g, int rgbOrThemeColor) {
		int argb = ((rgbOrThemeColor & 0xff000000) == THEME_BITFLAG) ? getRGBFromTheme(rgbOrThemeColor) : rgbOrThemeColor;
		g.setColor(argb);
	}
	
	private static int getRGBFromTheme(int themeColor) {
		if((themeColor & 0xff000000) != THEME_BITFLAG) {
			throw new IllegalArgumentException("Bad color input.");
		}
		
		switch(themeColor) {
		case THEME_SCREEN_BG_COLOR:
			return COLOR_BLACK;
		case THEME_MENU_HR_COLOR:
			return COLOR_BLACK;
		case THEME_TITLE_BG_COLOR:
			return mPresetThemeColors[mTheme][DARK_BG_COLOR_INDEX];
		case THEME_TITLE_TEXT_COLOR:
			return mPresetThemeColors[mTheme][DARK_TEXT_COLOR_INDEX];
		case THEME_TITLE_HR_COLOR:
			return COLOR_BLACK;
		case THEME_MENUBAR_BG_COLOR:
			return mPresetThemeColors[mTheme][DARK_BG_COLOR_INDEX];
		case THEME_MENUBAR_TEXT_COLOR:
			return mPresetThemeColors[mTheme][DARK_TEXT_COLOR_INDEX];
		case THEME_MENUBAR_HR_COLOR:
			return COLOR_BLACK;
		case THEME_LABEL_BG_COLOR:
			return COLOR_CLEAR;
		case THEME_LABEL_TEXT_COLOR:
			return COLOR_BLACK;
		case THEME_MENUITEM_NORMAL_BG_COLOR:
			return mPresetThemeColors[mTheme][LIGHT_BG_COLOR_INDEX];
		case THEME_MENUITEM_NORMAL_TEXT_COLOR:
			return mPresetThemeColors[mTheme][LIGHT_TEXT_COLOR_INDEX];
		case THEME_MENUITEM_HIGHLIGHTED_BG_COLOR:
			return mPresetThemeColors[mTheme][NORMAL_BG_COLOR_INDEX];
		case THEME_MENUITEM_HIGHLIGHTED_TEXT_COLOR:
			return mPresetThemeColors[mTheme][NORMAL_TEXT_COLOR_INDEX];
		case THEME_BUTTON_BORDER_TOPLEFT_COLOR:
			return COLOR_GRAY80;
		case THEME_BUTTON_BORDER_BOTRIGHT_COLOR:
			return COLOR_GRAY45;
		case THEME_BUTTON_NORMAL_BG_COLOR:
			return mPresetThemeColors[mTheme][LIGHT_BG_COLOR_INDEX];
		case THEME_BUTTON_NORMAL_TEXT_COLOR:
			return mPresetThemeColors[mTheme][LIGHT_TEXT_COLOR_INDEX];
		case THEME_BUTTON_HIGHLIGHTED_BG_COLOR:
			return mPresetThemeColors[mTheme][NORMAL_BG_COLOR_INDEX];
		case THEME_BUTTON_HIGHLIGHTED_TEXT_COLOR:
			return mPresetThemeColors[mTheme][NORMAL_TEXT_COLOR_INDEX];
		case THEME_BUTTON_SELECTED_TEXT_COLOR:
			return mPresetThemeColors[mTheme][NORMAL_BG_COLOR_INDEX];
		case THEME_BUTTON_SELECTED_BG_COLOR:
			return mPresetThemeColors[mTheme][NORMAL_TEXT_COLOR_INDEX];
		case THEME_BUTTON_DISABLED_TEXT_COLOR:
			return COLOR_GRAY60;
		case THEME_BUTTON_DISABLED_BG_COLOR:
			return COLOR_GRAY80;
		}
		
		return COLOR_CLEAR;
	}
}
