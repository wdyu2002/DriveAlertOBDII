package com.devculture.drivealert.connectivity.obd.transactions;

import java.io.InputStream;
import java.io.OutputStream;
import com.devculture.drivealert.Globals;
import com.devculture.drivealert.connectivity.obd.exceptions.OBDTransactionParseException;
import com.devculture.drivealert.utils.Log;

public abstract class OBDTransaction implements Globals {
	private final String mCommand;
	private final OBDTransactionListener mListener;
	
	protected OBDTransaction(String command) {
		this(command, null);
	}
	
	protected OBDTransaction(String command, OBDTransactionListener listener) {
		mCommand = command + "\r";
		mListener = listener;
	}
	
	public String getCommand() {
		return mCommand;
	}
	
	public void execute(InputStream is, OutputStream os) {
		try {
			Log.logReq(">> " + mCommand.substring(0, mCommand.length()-1));
			
			// send command
			os.write(mCommand.getBytes());
			os.flush();
			// golden wait duration so queries don't time-out
			Thread.sleep(200);
			
			// receive response - read until '>' arrives
			char read = 0;
			StringBuffer buffer = new StringBuffer();
			while((read = (char)is.read()) != '>') {
				buffer.append(read);
			}
		
			// full response
			String response = buffer.toString();
			Log.logResp("    " + response);
			
			// parse received content
			notifySuccess(parseReceivedData(response));
		} catch(OBDTransactionParseException ex) {
			Log.logOBDError("Error in parse", ex);
			notifyFailure(OBDTransactionListener.OBD_TRANSACTION_ERROR_PARSE);
		} catch(Exception ex) {
			Log.logOBDError("Error in read", ex);
			notifyFailure(OBDTransactionListener.OBD_TRANSACTION_ERROR_READ);
		}
	}
	
	protected abstract Object parseReceivedData(String response) throws OBDTransactionParseException;
	
	public void notifySuccess(Object result) {
		if(mListener != null) {
			mListener.onOBDTransactionSuccessful(this, result);
		}
	}
	
	public void notifyFailure(int reason) {
		if(mListener != null) {
			mListener.onOBDTransactionFailed(this, reason);
		}
	}
}
