package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;

public class OBDTimeOutTransaction extends OBDTransaction {
	public OBDTimeOutTransaction(int msec) {
		super("AT ST " + Integer.toHexString(0xFF & (msec/4)));
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		return null;
	}
}
