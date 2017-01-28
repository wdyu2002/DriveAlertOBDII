package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;

public class OBDProtocolTransaction extends OBDTransaction {
	public OBDProtocolTransaction() {
		super("AT SP 0");
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		return null;
	}
}
