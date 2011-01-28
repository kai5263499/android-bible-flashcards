package com.werxltd.bibleflash;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.werxltd.bibleflash.events.BF_EventListener;
import com.werxltd.bibleflash.events.BF_Message;
import com.werxltd.bibleflash.events.BF_MessageEvent;

public class CardService extends Service implements BF_EventListener {
	private static final String TAG = "CardService";

	RemoteViews updateViews;

	static final int MSG_NEXT_CARD = 1;
	static final int MSG_PREV_CARD = 2;
	static final int MSG_RANDOM_CARD = 3;
	static final int MSG_CARD_LEARNED = 4;
	static final int MSG_CARD_UNLEARNED = 5;
	static final int MSG_CARD_FLIP = 6;
	
	CardManager cm;
	
	@Override
	public void onStart(Intent intent, int startId) {
		if (Preferences.D)
			Log.d(TAG, "onStart()");

		cm = new CardManager();
		
		// Build the widget update for today
		RemoteViews updateViews = buildUpdate(this);

		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(this, WordWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, updateViews);

		updateCard();
	}

	public RemoteViews buildUpdate(Context context) {
		if (Preferences.D)
			Log.d(TAG, "buildUpdate()");
		// Resources res = context.getResources();
		updateViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_word);
		updateViews.setTextViewText(R.id.word_title, "Loading...");

		updateCard();

		Intent i = new Intent(context, CardActivity.class);
		// Intent defineIntent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse(definePage));
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				0 /* no requestCode */, i, 0 /* no flags */);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

		return updateViews;
	}

	private void updateCard() {
		if (Preferences.D)
			Log.d(TAG, "updateCard()");
		
		cm
				.setSharedpreferences(getSharedPreferences(
						Preferences.PREFS_NAME, 0));
		cm.loadPreferences();
		cm.setAssets(getAssets());
		cm.setContext(this);
		cm.addMessageListener(this);

		cm.setupDatabases();

		cm.loadLesson();

		if (cm.hasLesson() && cm.getLessonName() != null
				&& cm.getCardNumTotal() > 0) {
			cm.loadLearned();

			cm.getPreferences().setLessonOffset(cm.getCardNumOffset());
			cm.savePreferences();

			updateViews.setTextViewText(R.id.word_title, cm.card.getFront());

			cm.savePreferences();
		}
		cm.closeDB();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't need to bind to this service
		return null;
	}

	public void messageReceived(BF_MessageEvent me) {
		switch (me.message().getCode()) {
		case BF_Message.CARDCHANGED:
			updateViews.setTextViewText(R.id.word_title, "Loading...");
			break;
		}
	}

}
