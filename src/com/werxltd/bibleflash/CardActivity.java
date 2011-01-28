package com.werxltd.bibleflash;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.werxltd.bibleflash.events.BF_EventListener;
import com.werxltd.bibleflash.events.BF_Message;
import com.werxltd.bibleflash.events.BF_MessageEvent;

public class CardActivity extends Activity implements OnGestureListener, BF_EventListener {
	private static final String TAG = "CardActivity";

	private TextView learnedcount_tv;
	private TextView learned_tv;
	private TextView word_tv;
	private TextView lessonset_name_tv;
	private TextView lesson_name_tv;
	private TextView cardcount;
	private ImageButton shufflecardbutton;
	private ImageButton checkcardbutton;
	private ImageButton cancelcardbutton;

	private GestureDetector gestureScanner;

	private boolean is_front = true;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private static final String LEARNED = "Learned";
	private static final String NOTLEARNED = "Not learned";

	private CardManager cm;
	
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
			Log.d(TAG, "onStart()");

		setContentView(R.layout.card);

		gestureScanner = new GestureDetector(this);


		cm = new CardManager();
		cm.setSharedpreferences(getSharedPreferences(Preferences.PREFS_NAME, 0));
		cm.loadPreferences();
		cm.setAssets(getAssets());
		cm.setContext(this);
		cm.addMessageListener(this);
		cm.setCardNumOffset(cm.getPreferences().getLessonOffset());
		
		if (cm.getPreferences() == null || cm.getPreferences().getCurrent_lessonset() == null
				|| cm.getPreferences().getCurrent_lesson() == null) {
			Intent i;

			if (cm.getPreferences() != null
					&& cm.getPreferences().getCurrent_lessonset() != null) {
				i = new Intent(CardActivity.this, LessonActivity.class);
			} else {
				i = new Intent(CardActivity.this, LessonSetActivity.class);
			}

			startActivity(i);
			return;
		}

		File f = new File(Preferences.SWORD_PATH + Preferences.LEARNED_DB);
		if (!f.exists()) {
			Intent i = new Intent(CardActivity.this, MainActivity.class);
			startActivity(i);
			return;
		}
		
		word_tv = (TextView) findViewById(R.id.word);
		word_tv.setTextSize(cm.getPreferences().getTextSize());

		learnedcount_tv = (TextView) findViewById(R.id.learnedcount);
		learned_tv = (TextView) findViewById(R.id.learned);

		lesson_name_tv = (TextView) findViewById(R.id.lesson_name);
		lesson_name_tv.setText(cm.getPreferences().getCurrent_lesson());

		lessonset_name_tv = (TextView) findViewById(R.id.lessonset_name);
		lessonset_name_tv.setText(cm.getPreferences().getCurrent_lessonset());

		cardcount = (TextView) findViewById(R.id.cardcount);

		shufflecardbutton = (ImageButton) findViewById(R.id.ShuffleCardBtn);
		checkcardbutton = (ImageButton) findViewById(R.id.CheckCardBtn);
		cancelcardbutton = (ImageButton) findViewById(R.id.CancelCardBtn);

		shufflecardbutton.setOnClickListener(shufflecardbuttonListener);
		checkcardbutton.setOnClickListener(checkcardbuttonListener);
		cancelcardbutton.setOnClickListener(cancelcardbuttonListener);
		
		cm.setupDatabases();

		cm.loadLesson();
		
		if (!cm.hasLesson() || cm.getLessonName() == null) {
			if (Preferences.D)
				Log.d(TAG, "hasLesson() false, running LessonActivity");
			Intent i = new Intent(CardActivity.this, LessonActivity.class);
			startActivity(i);
			return;
		}

		cm.loadLearned();

		updateLearnedButtons();
		updateLearnedText();
		updateCardCount();

		if(cm.getCardNumOffset() >= 0) {
			cm.loadCard();
	
			if (is_front)
				showCardFront();
			else
				showCardBack();
			
			updateLearnedButtons();
		} else {
			cm.nextCard();
		}
		
		if(cm.getTypeFace() != null) {
			word_tv.setTypeface(cm.getTypeFace());
		} else {
			Toast.makeText(getApplicationContext(), "Failed loading typeface: "+cm.lesson.getFont()+".ttf", Toast.LENGTH_SHORT).show();
		}
	}

	private OnClickListener shufflecardbuttonListener = new OnClickListener() {
		public void onClick(View v) {
			cm.randomCard();
		}
	};

	private OnClickListener checkcardbuttonListener = new OnClickListener() {
		public void onClick(View v) {
			cm.setCardLearned();
		}
	};

	private OnClickListener cancelcardbuttonListener = new OnClickListener() {
		public void onClick(View v) {
			cm.setCardNotLearned();
		}
	};


	private void showCardFront() {
		if (Preferences.D)
			Log.d(TAG, "showCardFront(): " + cm.card.getFront());

		if(cm.card == null) return;
		
		word_tv.setTextSize(cm.getPreferences().getTextSize());

		String frontText = cm.card.getFront();
		word_tv.setText(frontText);
	}

	private void showCardBack() {
		if (Preferences.D)
			Log.d(TAG, "showCardBack(): " + cm.card.getBack());
		if (Preferences.D)
			Log.d(TAG, "card_num_offset: " + cm.getCardNumOffset());
		
		if(cm.card == null) return;

		String backText = cm.card.getBack();
		word_tv.setText(backText);
	}

	private void flipCard() {
		if (is_front)
			showCardBack();
		else
			showCardFront();
		is_front = !is_front;
	}

	private void updateLearnedButtons() {
		if (cm.isKnown()) {
			learned_tv.setText(LEARNED);
			cancelcardbutton.setClickable(true);
			cancelcardbutton.setImageDrawable(getResources().getDrawable(
					R.drawable.cancel));
			checkcardbutton.setClickable(false);
			checkcardbutton.setImageDrawable(getResources().getDrawable(
					R.drawable.check_grey));
		} else {
			learned_tv.setText(NOTLEARNED);
			cancelcardbutton.setClickable(false);
			cancelcardbutton.setImageDrawable(getResources().getDrawable(
					R.drawable.cancel_grey));
			checkcardbutton.setClickable(true);
			checkcardbutton.setImageDrawable(getResources().getDrawable(
					R.drawable.check));
		}
	}

	private void updateCardCount() {
		cardcount.setText("Card "+(cm.getCardNumOffset()+1)+"/"+cm.getCardNumTotal());
	}
	
	private void updateLearnedCount() {
		learnedcount_tv.setText(cm.numLearnedCards() + " learned");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (Preferences.D)
			Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

		Intent i = null;

		switch (item.getItemId()) {
		case R.id.lessonsetOpt:
			if (Preferences.D)
				Log.d(TAG, "lessonsetOpt");
			i = new Intent(CardActivity.this, LessonSetActivity.class);
			break;
		case R.id.lessonOpt:
			if (Preferences.D)
				Log.d(TAG, "lessonOpt");
			i = new Intent(CardActivity.this, LessonActivity.class);
			break;
		case R.id.prefsOpt:
			if (Preferences.D)
				Log.d(TAG, "prefsOpt");
			i = new Intent(CardActivity.this, PreferencesActivity.class);
			break;
		default:
			return false;
		}

		startActivity(i);

		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (Preferences.D)
			Log.d(TAG, "onSaveInstanceState()");

		if (Preferences.D)
			Log.d(TAG, "card_num_offset: " + cm.getPreferences().getLessonOffset());

		savedInstanceState.putBoolean("is_front", is_front);
		
		cm.closeDB();
		
		cm.savePreferences();
		
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (Preferences.D)
			Log.d(TAG, "onRestoreInstanceState()");

		if (Preferences.D)
			Log.d(TAG, "savedInstanceState.getInt(card_num_offset): " + savedInstanceState.getInt("card_num_offset"));
		
		is_front = savedInstanceState.getBoolean("is_front");

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	public boolean onDown(MotionEvent me) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (Preferences.D)
			Log.d(TAG, "onFling flipcard()");

		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				cm.nextCard();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				cm.prevCard();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void onLongPress(MotionEvent me) {

	}

	public boolean onScroll(MotionEvent me1, MotionEvent me2, float arg2,
			float arg3) {
		return false;
	}

	public void onShowPress(MotionEvent me) {

	}

	public boolean onSingleTapUp(MotionEvent me) {
		if (Preferences.D)
			Log.d(TAG, "onFling onSingleTapUp()");
		flipCard();
		return false;
	}

	@Override
	protected void onDestroy() {
		cm.closeDB();
		
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (Preferences.D)
				Log.d(TAG, "onKeyDown:" + keyCode);
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				Intent i = new Intent(CardActivity.this, LessonActivity.class);
				startActivity(i);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void updateLearnedText() {
		if(cm.isKnown()) {
			learned_tv.setText(LEARNED);
		} else {
			learned_tv.setText(NOTLEARNED);
		}
	}
	
	public void messageReceived(BF_MessageEvent me) {
		switch(me.message().getCode()) {
			case BF_Message.CARDCHANGED:
				showCardFront();
				updateLearnedButtons();
				updateLearnedText();
				updateLearnedCount();
				updateCardCount();
			break;
			case BF_Message.LEARNEDUPDATED:
				updateLearnedButtons();
				updateLearnedText();
				updateLearnedCount();
				updateCardCount();
			break;
			case BF_Message.CARDCOUNTUPDATED:
				cardcount.setText("Card " + (cm.getCardNumOffset() + 1) + "/"
						+ cm.getCardNumTotal());
				updateCardCount();
				updateLearnedCount();
			break;
		}
	}
}
