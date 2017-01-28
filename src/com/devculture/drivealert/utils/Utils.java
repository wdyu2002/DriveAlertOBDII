package com.devculture.drivealert.utils;

import java.util.Random;

import com.devculture.drivealert.Globals;

public class Utils implements Globals {
	public static Random mRandomGenerator = new Random(System.currentTimeMillis());
	
	// convert 0x00 to a single byte value
	public static byte parseByte(char c1, char c2) throws IllegalArgumentException {
		if(!isValidHexChar(c1) || !isValidHexChar(c2)) {
			throw new IllegalArgumentException("Invalid hex character '" + c1 + c2 + "'");
		}
		return (byte)(getHexValue(c1)*16 + getHexValue(c2));
	}
	
	public static boolean isValidHexChar(char c) {
		return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'));
	}
	
	public static int getHexValue(char c) {
		if(c >= '0' && c <= '9') {
			return c-'0';
		} else if(c >= 'A' && c <= 'F') {
			return c-'A'+10;
		} else if(c >= 'a' && c <= 'f') {
			return c-'a'+10;
		}
		return 0;
	}

	public static char getRandomHex() {
		final int val = Math.abs(mRandomGenerator.nextInt(15));
		if(val >= 0 && val <= 9) {
			return (char)('0' + val);
		} else if(val >= 10 && val <= 15){
			return (char)('A' + (val-10));
		}
		return 'x';
	}
	
	public static String getRandomHexByte() {
		return new StringBuffer().append(getRandomHex()).append(getRandomHex()).toString();
	}
	
	public static boolean isValidNumeric(char c) {
		return c >= '0' && c <= '9';
	}
	
	public static boolean isPhoneNumberNotSet(String phoneNumber) {
		// not set is defined as (A) 0 length or (B) Not Set
		return phoneNumber != null && (phoneNumber.length() == 0 || phoneNumber.equalsIgnoreCase(TEXT_PHONE_NOT_SET));
	}
	
	public static boolean isValidPhoneNumber(String phoneNumber) {
		if(phoneNumber == null) {
			return false;
		}
		
		// else validate 10-digit phone number
		int length = phoneNumber.length();
		if(length == 10) {
			for(int i=0; i<length; i++) {
				if(!isValidNumeric(phoneNumber.charAt(i))) {
					// invalid character found
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
