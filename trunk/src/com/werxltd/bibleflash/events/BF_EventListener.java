package com.werxltd.bibleflash.events;

import java.util.EventListener;

public interface BF_EventListener extends EventListener {
	public void messageReceived( BF_MessageEvent me );
}
