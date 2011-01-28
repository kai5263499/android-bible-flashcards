package com.werxltd.bibleflash.events;

public class BF_Message {
	/*
	 * The following are message types which also serve to indicate the type
	 * of information that is also being supplied in the data field.
	 */
	public static final int CARDCHANGED	     = 10;
	public static final int LEARNEDUPDATED   = 20;
	public static final int CARDCOUNTUPDATED = 30;
	
	private int code;
	
	public BF_Message(int code) {
    	this.code = code;
    }
    
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
