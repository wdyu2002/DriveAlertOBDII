package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;

public class OBDSearchTransaction extends OBDTransaction {
	public OBDSearchTransaction() {
		super("01 0D");
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		// probably starts with SEARCHING...
		return null;
	}
}
