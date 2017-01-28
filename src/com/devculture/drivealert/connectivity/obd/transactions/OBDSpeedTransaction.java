package com.devculture.drivealert.connectivity.obd.transactions;

import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;
import com.devculture.drivealert.utils.Utils;

public class OBDSpeedTransaction extends OBDTransaction {
	public OBDSpeedTransaction(OBDTransactionListener listener) {
		super("01 0D", listener);
	}

	protected Object parseReceivedData(String response) throws OBDTransactionParseException {
		try {
			int index = response.indexOf("41 0D ");
			int speedInKmh = Utils.parseByte(response.charAt(index+6), response.charAt(index+7));
			return new Integer(speedInKmh);
		} catch(Exception ex) {
			throw new OBDTransactionParseException();
		}
	}
}
