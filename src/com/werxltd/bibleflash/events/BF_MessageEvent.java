package com.werxltd.bibleflash.events;

import java.util.EventObject;

public class BF_MessageEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1522676910933572512L;
	private BF_Message _message;

	public BF_MessageEvent(Object source, BF_Message message) {
		super(source);
		_message = message;
	}

	public BF_Message message() {
		return _message;
	}

}
