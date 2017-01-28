package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;

public class OBDLineFeedOffTransaction extends OBDTransaction {
	public OBDLineFeedOffTransaction() {
		super("AT L0");
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		return null;
	}
}
