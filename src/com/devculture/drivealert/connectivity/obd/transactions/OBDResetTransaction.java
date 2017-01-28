package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;

public class OBDResetTransaction extends OBDTransaction {
	public OBDResetTransaction() {
		super("AT Z");
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		return null;
	}
}
