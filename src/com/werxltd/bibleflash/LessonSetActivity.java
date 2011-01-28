package com.werxltd.bibleflash;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LessonSetActivity extends Activity {
	private static final String TAG = "LessonSetActivity";

	private ArrayList<LessonSet> listOfLessonset;
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
		if (Preferences.D) Log.d(TAG, "onStart()");
		setContentView(R.layout.lessonset_list);
		
		loadPreferences();
		
		if(listOfLessonset == null) buildList();
		
		ListView lessonset_list = (ListView) findViewById(R.id.lessonset_list);
		LessonSetAdapter adapter = new LessonSetAdapter(this, listOfLessonset);
		lessonset_list.setAdapter(adapter);
		OnItemClickListener l = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int arg2,
					long arg3) {
				LessonSet lessonset = (LessonSet) av.getItemAtPosition(arg2);
				Log.v(TAG, "lessonset name:" + lessonset.getName());
				preferences.setCurrent_lessonset(lessonset.getName());
				savePreferences();
				
				if (Preferences.D) Log.d(TAG, "unzipping lessonset "+lessonset.getName()+".db");
				Utils.startUnzipAsset("lessons.zip", Preferences.SWORD_PATH, lessonset.getName()+".db", getAssets());
				
				Intent i = new Intent(LessonSetActivity.this, LessonActivity.class);
				startActivity(i);

				return;
			}
		};
		lessonset_list.setOnItemClickListener(l);
	}

	private void buildList() {
		try {
			myDbHelper = new DataBaseHelper(this);
			
			lessonsetdb = myDbHelper.openDataBase(Preferences.SWORD_PATH+Preferences.LESSONS_DB);
			listOfLessonset = new ArrayList<LessonSet>();
			
			Cursor c = lessonsetdb.rawQuery("SELECT _id,name "
					+ " FROM lesson_set ORDER BY name ASC", null);
			if (c.moveToFirst()) {
				do {
					if (Preferences.D)
						Log.d(TAG, "lessonset: " + c.getString(1));
					LessonSet lessonset = new LessonSet();
					lessonset.setId(c.getInt(0));
					lessonset.setName(c.getString(1));
					listOfLessonset.add(lessonset);
				} while (c.moveToNext());
			}
			c.close();
		} catch (SQLException sqle) {
			Intent i = new Intent(LessonSetActivity.this, MainActivity.class);
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
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	if (Preferences.D)
				Log.d(TAG, "onKeyDown:"+keyCode);
			if(keyCode == KeyEvent.KEYCODE_BACK) {
				Intent i = new Intent(LessonSetActivity.this, CardActivity.class);
				startActivity(i);
			}
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	private void loadPreferences() {
		if (Preferences.D)
			Log.d(TAG, "loadPreferences()");
		
		preferences = Preferences.getPreferences(getSharedPreferences(Preferences.PREFS_NAME, 0));
	}

	private void savePreferences() {
		if (Preferences.D)
			Log.d(TAG, "savePreferences()");
		if(preferences != null) {
			Preferences.savePreferences(getSharedPreferences(Preferences.PREFS_NAME, 0), preferences);
		}
	}
	
	@Override
	protected void onDestroy() {
		lessonsetdb.close();
		super.onDestroy();
	}
}
