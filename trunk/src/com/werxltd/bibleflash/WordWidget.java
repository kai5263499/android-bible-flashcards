package com.werxltd.bibleflash;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WordWidget extends AppWidgetProvider {
	private static final String TAG = "WordWidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// To prevent any ANR timeouts, we perform the update in a service
		if (Preferences.D)
			Log.d(TAG, "onUpdate()");
		context.startService(new Intent(context, CardService.class));
	}

}
