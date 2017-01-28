package com.devculture.drivealert.gui.components;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import com.devculture.drivealert.Application;
import com.devculture.drivealert.Globals;

public class UIInputController implements Globals, CommandListener {
	
	public interface UITextControlListener {
		public static final int TEXTCTRL_DONE = 0;
		// public static final int TEXTCTRL_CANCEL = 1;
		public void onTextControlEvent(int event, UIInputController control);
	}
	
	public static final int DEFAULT_TEXTCONTROL_MAXSIZE = 255;
	private final String mTitle;
	private final Form mEntryForm;
	private UITextControlListener mListener;
	
	private Vector mTextFields = new Vector();
	
	public UIInputController(String title) {
		mTitle = title == null ? "" : title;
		mEntryForm = new Form(mTitle);
		mEntryForm.addCommand(new Command(TEXT_DONE, Command.OK, 1));
		mEntryForm.setCommandListener(this);
	}
	
	public int addTextField(String title, String text, int type) {
		TextField textField = new TextField(title, text, DEFAULT_TEXTCONTROL_MAXSIZE, type);
		mTextFields.addElement(textField);
		mEntryForm.append(textField);
		return mTextFields.size();
	}
	
	public String getTextFromTextField(int index) {
		TextField textField = (TextField)mTextFields.elementAt(index);
		String value = textField.getString();
		return value == null ? "" : value;
	}
	
	public boolean isShown() {
		return mEntryForm.isShown();
	}
	
	public void showForm() {
		Application.setDisplayable(mEntryForm);
	}
	
	public void hideForm() {
		
	}
	
	public void commandAction(Command command, Displayable display) {
		if(TEXT_DONE.equals(command.getLabel())) {
			if(mListener != null) {
				mListener.onTextControlEvent(UITextControlListener.TEXTCTRL_DONE, this);
			}
		/*
		} else if(TEXT_CANCEL.equals(command.getLabel())) {
			if(mListener != null) {
				mListener.onTextControlEvent(UITextControlListener.TEXTCTRL_CANCEL, this);
			}
		*/
		}
	}
	
	public void setListener(UITextControlListener listener) {
		mListener = listener;
	}
}
