package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;

public class OBDEchoOffTransaction extends OBDTransaction {
	public OBDEchoOffTransaction() {
		super("AT E0");
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		return null;
	}
}
