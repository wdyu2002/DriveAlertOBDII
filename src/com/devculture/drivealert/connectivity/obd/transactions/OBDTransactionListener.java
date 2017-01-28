package com.devculture.drivealert.connectivity.obd.transactions;

public interface OBDTransactionListener {
	// error reasons
	public static final int OBD_TRANSACTION_ERROR_READ 					= 0;
	public static final int OBD_TRANSACTION_ERROR_PARSE	 				= 1;
	public static final int OBD_TRANSACTION_ERROR_DEVICE_UNINITIALIZED	= 2;
	
	public void onOBDTransactionSuccessful(OBDTransaction transaction, Object result);
	public void onOBDTransactionFailed(OBDTransaction transaction, int reason);
}
