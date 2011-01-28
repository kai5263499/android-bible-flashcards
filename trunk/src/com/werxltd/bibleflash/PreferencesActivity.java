package com.werxltd.bibleflash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PreferencesActivity extends Activity {
	private static final String TAG = "PreferencesActivity";

	private SQLiteDatabase learneddb;

	private DataBaseHelper learnedDBH;

	private int learned_count;

	private Preferences preferences;

	private SeekBar textSizeSb;
	private SeekBar quizIntervalSb;
	private CheckBox quizCbx;
	private CheckBox hideKnownCbx;
	private TextView textSize;

	private Button clearlearnedsbBtn;

	@Override
	public void onStart() {
		super.onStart();
		if (Preferences.D)
			Log.d(TAG, "onStart()");

		setContentView(R.layout.preferences);

		loadPreferences();

		setupDatabases();
		getLearnedCount();

		textSize = (TextView) findViewById(R.id.TextSizeTv);

		textSizeSb = (SeekBar) findViewById(R.id.TextSizeSb);
		textSizeSb.setOnSeekBarChangeListener(textSizeChangeListener);

		textSizeSb.setProgress(preferences.getTextSize());

		quizIntervalSb = (SeekBar) findViewById(R.id.QuizIntervalSb);
		quizIntervalSb.setOnSeekBarChangeListener(quizIntervalChangeListener);

		quizCbx = (CheckBox) findViewById(R.id.EnableNotificationCbx);
		quizCbx.setOnCheckedChangeListener(quizEnabledListener);

		hideKnownCbx = (CheckBox) findViewById(R.id.HideKnownCardsChkBx);
		hideKnownCbx.setOnCheckedChangeListener(hideKnownCardsListener);

		hideKnownCbx.setChecked(preferences.hideKnownCards());

		clearlearnedsbBtn = (Button) findViewById(R.id.ClearLearnedBtn);

		clearlearnedsbBtn.setOnClickListener(clearLearnedListener);

		updateLearnedButton();

		updateQuizIntervalText();
		updateTextSizeText();
	}

	private void updateLearnedButton() {
		if (learned_count > 0) {
			clearlearnedsbBtn.setEnabled(true);
			clearlearnedsbBtn.setText("Mark all (" + learned_count
					+ ") cards as unlearned.");
		} else {
			clearlearnedsbBtn.setEnabled(false);
			clearlearnedsbBtn.setText("No cards marked as learned.");
		}
	}

	private void getLearnedCount() {
		Cursor c = learneddb.query(false, "learned", new String[] { "key" },
				null, null, null, null, null, null);
		learned_count = c.getCount();
	}

	private void setupDatabases() {
		learnedDBH = new DataBaseHelper(this);
		learneddb = learnedDBH.openWritableDataBase(Preferences.SWORD_PATH
				+ Preferences.LEARNED_DB);
	}

	private void clearLearnedDB() {
		DataBaseHelper.resetLearnedDB(new DataBaseHelper(this));
		learned_count = 0;
	}

	private void showClearDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to clear all learned cards?")
				.setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								clearLearnedDB();
								updateLearnedButton();
								dialog.cancel();
							}
						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private OnClickListener clearLearnedListener = new OnClickListener() {
		public void onClick(View v) {
			showClearDialog();
		}
	};

	private OnSeekBarChangeListener textSizeChangeListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar sb, int arg1, boolean arg2) {
			preferences.setTextSize(arg1);
			savePreferences();
			updateTextSizeText();
		}

		public void onStartTrackingTouch(SeekBar sb) {
		}

		public void onStopTrackingTouch(SeekBar sb) {
		}
	};

	private OnSeekBarChangeListener quizIntervalChangeListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar sb, int arg1, boolean arg2) {

			preferences.setQuizzingInterval(arg1);
			savePreferences();
			updateQuizIntervalText();
		}

		public void onStartTrackingTouch(SeekBar sb) {
		}

		public void onStopTrackingTouch(SeekBar sb) {
		}
	};

	private void updateQuizIntervalText() {
		int interval = preferences.getQuizzingInterval();

		quizCbx.setText("Enable periodic quizzing every " + interval
				+ "seconds");
	}

	private OnCheckedChangeListener quizEnabledListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton cb, boolean arg1) {
			preferences.setQuizzingEnabled(arg1);
			savePreferences();
		}
	};

	protected void updateTextSizeText() {
		int size = preferences.getTextSize();
		textSize.setText("Card text size: " + size);
	}

	private OnCheckedChangeListener hideKnownCardsListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton cb, boolean arg1) {
			if (Preferences.D)
				Log.d(TAG, "hideKnownCardsListener: " + arg1);
			preferences.setHideKnownCards(arg1);
			savePreferences();
		}
	};

	private void loadPreferences() {
		if (Preferences.D)
			Log.d(TAG, "loadPreferences()");

		preferences = Preferences.getPreferences(getSharedPreferences(
				Preferences.PREFS_NAME, 0));
	}

	private void savePreferences() {
		if (Preferences.D)
			Log.d(TAG, "savePreferences()");
		Preferences.savePreferences(getSharedPreferences(
				Preferences.PREFS_NAME, 0), preferences);
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				Intent i = new Intent(PreferencesActivity.this,
						CardActivity.class);
				startActivity(i);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
