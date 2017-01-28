package com.devculture.drivealert.connectivity.obd;

/**
 * All OBD protocols.
 */

public class OBDProtocol
{
	public final static OBDProtocol OBDP_AUTO = new OBDProtocol('0');					// auto
	public final static OBDProtocol OBDP_SAE_J1850_PWM = new OBDProtocol('1'); 			// 41.6 kbaud
	public final static OBDProtocol OBDP_SAE_J1850_VPW = new OBDProtocol('2');			// 10.4 kbaud
	public final static OBDProtocol OBDP_ISO_9141_2 = new OBDProtocol('3'); 			// 5 baud init
	public final static OBDProtocol OBDP_ISO_14230_4_KWP = new OBDProtocol('4');		// 5 baud init
	public final static OBDProtocol OBDP_ISO_14230_4_KWP_FAST = new OBDProtocol('5');	// fast init
	public final static OBDProtocol OBDP_ISO_15765_4_CAN = new OBDProtocol('6');		// 11 bit ID, 500 kbaud
	public final static OBDProtocol OBDP_ISO_15765_4_CAN_B = new OBDProtocol('7');		// 29 bit ID, 500 kbaud
	public final static OBDProtocol OBDP_ISO_15765_4_CAN_C = new OBDProtocol('8');		// 11 bit ID, 250 kbaud
	public final static OBDProtocol OBDP_ISO_15765_4_CAN_D = new OBDProtocol('9');		// 29 bit ID, 250 kbaud
	public final static OBDProtocol OBDP_SAE_J1939_CAN = new OBDProtocol('A'); 			// 29 bit ID, 250 kbaud (user adjustable)
	public final static OBDProtocol OBDP_USER1_CAN = new OBDProtocol('B');				// 11 bit ID (user adjustable), 125 kbaud (user adjustable)
	public final static OBDProtocol OBDP_USER2_CAN = new OBDProtocol('C');				// 11 bit ID (user adjustable), 50 kbaud (user adjustable)

	private final String mValue;
	
	private OBDProtocol(char value)
	{
		mValue = new StringBuffer().append(value).toString();
	}
	
	public String toString() {
		return mValue;
	}
}