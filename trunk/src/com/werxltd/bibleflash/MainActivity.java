package com.werxltd.bibleflash;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnGestureListener {
	private static final String TAG = "MainActivity";

	private Preferences preferences;

	private TextView loading_text;

	final Handler mHandler = new Handler();

	private GestureDetector gestureScanner;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Preferences.D)
			Log.d(TAG, "onCreate()");

		if (savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (Preferences.D)
			Log.d(TAG, "onStart()");

		setContentView(R.layout.loading);
		loading_text = (TextView) findViewById(R.id.loading_text);

		gestureScanner = new GestureDetector(this);

		if (!sdCardMounted()) {
			if (Preferences.D)
				Log.d(TAG, "SDCard not mounted, aborting.");
			loading_text
					.setText("SDCard is unmounted, please restart this app or tap here when SDCard is avaliable..");
			return;
		}

		TextView loading_text_tv = (TextView) findViewById(R.id.greek);

		Typeface face = Typeface.createFromAsset(getAssets(),
				"fonts/GalSILB201.ttf");

		loading_text_tv.setTypeface(face);

		loading_text.setText("Loading preferences..");
		loadPreferences();
		
		if (Preferences.D)
			Log.d(TAG, "unzipping lessons.db");
		
		Utils.startUnzipAsset("lessons.zip", Preferences.SWORD_PATH,
				Preferences.LESSONS_DB, getAssets());
		
		File f = new File(Preferences.SWORD_PATH + Preferences.LEARNED_DB);
		if (!f.exists()) {
			if (Preferences.D)
				Log.d(TAG, "learned.db not found, creating...");
			File pf = new File(f.getParent());
			pf.mkdirs();
			
			DataBaseHelper.resetLearnedDB(new DataBaseHelper(this));
		}
		
		if (preferences.getCurrent_lessonset() == null) {
			setupLessonSetView();
		} else if (preferences.getCurrent_lesson() == null) {
			setupLessonView();
		} else {
			setupCardView();
		}
	}

	private boolean sdCardMounted() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	private void loadPreferences() {
		if (Preferences.D)
			Log.d(TAG, "loadPreferences()");

		preferences = Preferences.getPreferences(getSharedPreferences(
				Preferences.PREFS_NAME, 0));
	}

	private void savePreferences() {
		if (Preferences.D)
			Log.d(TAG, "savePreferences()");
		if (sdCardMounted() && preferences != null) {
			Preferences.savePreferences(getSharedPreferences(
					Preferences.PREFS_NAME, 0), preferences);
		}
	}

	private void setupLessonSetView() {
		if (Preferences.D)
			Log.d(TAG, "setupLessonSetView()");
		loading_text.setText("Displaying lesson set chooser..");
		Intent i = new Intent(MainActivity.this, LessonSetActivity.class);
		startActivity(i);
	}

	private void setupLessonView() {
		if (Preferences.D)
			Log.d(TAG, "setupLessonView()");
		loading_text.setText("Displaying lesson chooser..");
		Intent i = new Intent(MainActivity.this, LessonActivity.class);
		startActivity(i);
	}

	private void setupCardView() {
		if (Preferences.D)
			Log.d(TAG, "setupCardView()");
		loading_text.setText("Displaying card..");
		Intent i = new Intent(MainActivity.this, CardActivity.class);
		startActivity(i);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (Preferences.D)
			Log.d(TAG, "onSaveInstanceState()");

		savePreferences();

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (Preferences.D)
			Log.d(TAG, "onRestoreInstanceState()");

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		savePreferences();
		super.onPause();
	}

	@Override
	public void onStop() {
		preferences.setVersionNum(Preferences.getVersionCode(getPackageManager()));
		savePreferences();
		super.onStop();
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onLongPress(MotionEvent arg0) {

	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onShowPress(MotionEvent arg0) {

	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		onStart();
		return false;
	}
}