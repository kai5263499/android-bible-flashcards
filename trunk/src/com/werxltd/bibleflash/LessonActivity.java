package com.werxltd.bibleflash;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LessonActivity extends Activity {
	private static final String TAG = "LessonActivity";

	private ArrayList<Lesson> listOfLesson;
	private SQLiteDatabase lessonsetdb;

	private DataBaseHelper myDbHelper;

	private Preferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (Preferences.D)
			Log.d(TAG, "setupView()");

		loadPreferences();

		if (preferences.getCurrent_lessonset() == null) {
			Intent i = new Intent(LessonActivity.this, LessonSetActivity.class);
			startActivity(i);
			return;
		}

		setContentView(R.layout.lesson_list);

		buildList();

		ListView lesson_list = (ListView) findViewById(R.id.lesson_list);
		LessonAdapter adapter = new LessonAdapter(this, listOfLesson);

		lesson_list.setAdapter(adapter);
		OnItemClickListener l = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int arg2,
					long arg3) {
				Lesson lesson = (Lesson) av.getItemAtPosition(arg2);
				Log.v(TAG, "lesson name:" + lesson.getName());

				preferences.setCurrent_lesson(lesson.getName());

				savePreferences();

				Intent i = new Intent(LessonActivity.this, CardActivity.class);
				startActivity(i);

				return;
			}
		};
		lesson_list.setOnItemClickListener(l);
	}

	private void buildList() {
		try {
			listOfLesson = new ArrayList<Lesson>();

			myDbHelper = new DataBaseHelper(this);

			lessonsetdb = myDbHelper.openDataBase(Preferences.SWORD_PATH
					+ preferences.getCurrent_lessonset() + ".db");

			Cursor c = lessonsetdb.rawQuery(
					"SELECT _id,name,font FROM lesson ORDER BY name ASC", null);
			if (c.moveToFirst()) {
				do {
					if (Preferences.D)
						Log.d(TAG, "lessonset: " + c.getString(1));
					Lesson lesson = new Lesson();
					lesson.setId(c.getInt(0));
					lesson.setName(c.getString(1));
					lesson.setFont(c.getString(2));
					listOfLesson.add(lesson);
				} while (c.moveToNext());
			}
			c.close();
			lessonsetdb.close();
		} catch (SQLiteException se) {
			Intent i = new Intent(LessonActivity.this, LessonSetActivity.class);
			startActivity(i);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (Preferences.D)
			Log.d(TAG, "onSaveInstanceState()");
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (Preferences.D)
			Log.d(TAG, "onRestoreInstanceState()");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (Preferences.D)
				Log.d(TAG, "onKeyDown:" + keyCode);
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				Intent i = new Intent(LessonActivity.this,
						LessonSetActivity.class);
				startActivity(i);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
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
		if (preferences != null) {
			Preferences.savePreferences(getSharedPreferences(
					Preferences.PREFS_NAME, 0), preferences);
		}
	}

	@Override
	protected void onDestroy() {
		listOfLesson.clear();
		super.onDestroy();
	}
}
