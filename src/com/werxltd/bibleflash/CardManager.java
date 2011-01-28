package com.werxltd.bibleflash;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.util.Log;

import com.werxltd.bibleflash.events.BF_EventListener;
import com.werxltd.bibleflash.events.BF_Message;
import com.werxltd.bibleflash.events.BF_MessageEvent;

public class CardManager {
	private static final String TAG = "CardManager";
	
	private DataBaseHelper lessonDBH;
	private DataBaseHelper learnedDBH;
	
	protected Card card;
	protected Lesson lesson;

	private HashMap<String, String> learnedCards;

	private SQLiteDatabase lessonsetdb;
	private SQLiteDatabase learneddb;
	
	private int card_num_total = 0;

	private Preferences preferences;

	private AssetManager assets;
	
	private SharedPreferences sharedpreferences;
	
	private List<BF_EventListener> listeners = new ArrayList<BF_EventListener>();

	private Context context;
	
	protected void loadPreferences() {
		preferences = Preferences.getPreferences(sharedpreferences);
	}
	
	protected void savePreferences() {
		Preferences.savePreferences(sharedpreferences, preferences);
	}
	
	protected int numLearnedCards() {
		if(learnedCards == null) return 0;
		return learnedCards.size();
	}
	
	protected boolean hasLesson() {
		return lesson != null;
	}
	
	private void delLearned(String token) {
		String chunks[] = token.split(":");
		String cardfront = chunks[chunks.length - 1];
		if (Preferences.D)
			Log.d(TAG, "delLearned token:" + token + " cardfront:" + cardfront);
		learnedCards.remove(cardfront);
		
		fireMessageEvent(generateMessage(BF_Message.LEARNEDUPDATED));
	}

	private void addLearned(String token) {
		String chunks[] = token.split(":");
		String cardfront = chunks[chunks.length - 1];
		if (Preferences.D)
			Log.d(TAG, "addLearned token:" + token + " cardfront:" + cardfront);
		learnedCards.put(cardfront, token);
		fireMessageEvent(generateMessage(BF_Message.LEARNEDUPDATED));
	}
	
	protected void randomCard() {
		Random generator = new Random();
		preferences.setLessonOffset(generator.nextInt(card_num_total));
		loadCard();
		
		
		if(card == null || (preferences.hideKnownCards() && isKnown() && learnedCards.size() < card_num_total)) {
			randomCard();
		}
		
		fireMessageEvent(generateMessage(BF_Message.CARDCHANGED));
	}
	
	protected void setCardNotLearned() {
		if(card == null || card.getFront() == null || learneddb == null || !learneddb.isOpen()) return;
		
		String token = generateCardToken();
		learneddb.execSQL("DELETE FROM \"learned\" WHERE key = (?)",
				new String[] { token });
		delLearned(token);
		fireMessageEvent(generateMessage(BF_Message.LEARNEDUPDATED));
	}

	protected void setCardLearned() {
		if(card == null || card.getFront() == null || learneddb == null || !learneddb.isOpen()) return;
		
		String token = generateCardToken();
		learneddb.execSQL("INSERT INTO \"learned\" VALUES (NULL, (?))",
				new String[] { token });
		addLearned(token);
		fireMessageEvent(generateMessage(BF_Message.LEARNEDUPDATED));
	}
	
	protected String generateCardToken() {
		if(card == null || card.getFront() == null) return null;
		String token = preferences.getCurrent_lessonset() + ":"
				+ preferences.getCurrent_lesson() + ":"
				+ card.getFront();
		if (Preferences.D)
			Log.d(TAG, "generateCardToken: " + token);
		return token;
	}
	
	protected boolean isKnown() {
		if(learnedCards == null || card == null) return false;
		return learnedCards.containsKey(card.getFront());
	}
	
	protected void prevCard() {
		if (preferences.getLessonOffset() > 0) {
			preferences.setLessonOffset(preferences.getLessonOffset()-1);
		} else {
			preferences.setLessonOffset(card_num_total-1);
		}
		
		loadCard();
		
		if(card == null || (preferences.hideKnownCards() && isKnown() && learnedCards.size() < card_num_total)) {
			prevCard();
		}
		
		fireMessageEvent(generateMessage(BF_Message.CARDCHANGED));
	}
	
	protected void nextCard() {
		if (preferences.getLessonOffset() + 1 < card_num_total) {
			preferences.setLessonOffset(preferences.getLessonOffset()+1);
		} else {
			preferences.setLessonOffset(0);
		}
		
		loadCard();
		
		if(card == null || (preferences.hideKnownCards() && isKnown() && learnedCards.size() < card_num_total)) {
			nextCard();
		}
		
		fireMessageEvent(generateMessage(BF_Message.CARDCHANGED));
	}
	
	protected void loadLesson() {
		if (Preferences.D)
			Log.d(TAG, "loadLesson()");
		lesson = new Lesson();

		Cursor c = lessonsetdb
				.rawQuery(
						"SELECT lesson._id,lesson.name,lesson.font, COUNT(card._id) FROM lesson,card WHERE lesson.name = (?) AND lesson._id = card.lesson_id LIMIT 1",
						new String[] { preferences.getCurrent_lesson() });
		if (c != null && c.moveToFirst()) {
			if (Preferences.D)
				Log.d(TAG, "load active lesson: " + c.getString(1));
			lesson.setId(c.getInt(0));
			lesson.setName(c.getString(1));
			lesson.setFont(c.getString(2));

			setCardNumTotal(c.getInt(3));
			c.close();
		}
	}
	
	/*
	 * This function generates a SQL string designed to be used in a WHERE
	 * clause when selecting new cards from this lessonset
	 */
	protected void loadLearned() {
		try {
			if (Preferences.D)
				Log.d(TAG, "loadLearned()");
			learnedCards = new HashMap<String, String>();
	
			Cursor c = learneddb.rawQuery(
					"SELECT key FROM learned WHERE key LIKE (?)",
					new String[] { preferences.getCurrent_lessonset() + ":"
							+ preferences.getCurrent_lesson() + "%" });
			if (c.moveToFirst()) {
				do {
					String token = c.getString(0);
					addLearned(token);
				} while (c.moveToNext());
			}
			
			c.close();
		} catch (SQLiteException se) {
			/*
			 * Learned database must be bad, wipe it out and start over.
			 */
			DataBaseHelper.resetLearnedDB(new DataBaseHelper(context));
		}
	}
	
	private String generateLearnedSQL() {
		String learnedSQL = " 1 = 1 ";

		if (learnedCards.isEmpty()) {
			return learnedSQL;
		}

		Set<String> learnedCardsKeySet = learnedCards.keySet();
		Iterator<String> learnedCardsIterator = learnedCardsKeySet.iterator();
		while (learnedCardsIterator.hasNext()) {
			String learnedkey = (String) learnedCardsIterator.next();
			learnedSQL += "AND card.front != \"" + learnedkey + "\" ";
		}

		if (Preferences.D)
			Log.d(TAG, "generateLearnedSQL: "+learnedSQL);
		
		return learnedSQL;
	}
	
	protected void setupDatabases() {
		learnedDBH = new DataBaseHelper(context);
		learneddb = learnedDBH.openWritableDataBase(Preferences.SWORD_PATH
				+ Preferences.LEARNED_DB);

		File f = new File(Preferences.SWORD_PATH
				+ preferences.getCurrent_lessonset());
		if (!f.exists()) {
			Utils.startUnzipAsset("lessons.zip", Preferences.SWORD_PATH,
					preferences.getCurrent_lessonset() + ".db", getAssets());
		}

		lessonDBH = new DataBaseHelper(context);
		lessonsetdb = lessonDBH.openDataBase(Preferences.SWORD_PATH
				+ preferences.getCurrent_lessonset() + ".db");
	}
	
	protected String getLessonName() {
		if(lesson == null || lesson.getName() == null) return null;
		return lesson.getName();
	}
	
	protected void loadCard() {
		if (Preferences.D)
			Log.d(TAG, "loadCard()");

		if(lessonsetdb == null || !lessonsetdb.isOpen()) {
			setupDatabases();
		}
		
		if(lesson == null || lesson.getName() == null) return;
		
		card = new Card();

		String querystr = "SELECT card._id, card.front, card.back FROM card "
				+ "INNER JOIN lesson ON (lesson._id = card.lesson_id) "
				+ "WHERE lesson.name = (?) " + "AND " + generateLearnedSQL()
				+ " " + "ORDER BY card._id LIMIT " + (preferences.getLessonOffset()) + ",1";

		if (Preferences.D)
			Log.d(TAG, "querystr:"+querystr);
		
		Cursor c = lessonsetdb.rawQuery(querystr, new String[] { lesson
				.getName() });
		if (c.moveToFirst()) {
			if (Preferences.D)
				Log.d(TAG, "lessonset: " + c.getString(1));
			card.setId(c.getInt(0));
			card.setFront(c.getString(1));
			card.setBack(c.getString(2));
		} else {
			card = null;
		}
		
		c.close();
	}

	public synchronized void addMessageListener( BF_EventListener l ) {
        listeners.add(l);
    }
    
    public synchronized void removeMessageListener( BF_EventListener l ) {
        listeners.remove(l);
    }
	
	private BF_Message generateMessage(int code) {
		BF_Message msg = new BF_Message(code);
		return msg;
	}
	
	private synchronized void fireMessageEvent(BF_Message m) {
    	if (Preferences.D)
			Log.d(TAG, "Firing message with code: "+m.getCode());
		
    	BF_MessageEvent me = new BF_MessageEvent(this, m);
    	Iterator<BF_EventListener> ilisteners = listeners.iterator();
        while( ilisteners.hasNext() ) {
            ilisteners.next().messageReceived(me);
        }
    }
	
	public void setCardNumOffset(int card_num_offset) {
		preferences.setLessonOffset(card_num_offset);
	}

	public int getCardNumOffset() {
		return preferences.getLessonOffset();
	}

	public void setCardNumTotal(int card_num_total) {
		this.card_num_total = card_num_total;
	}

	public int getCardNumTotal() {
		return card_num_total;
	}
	
	public void closeDB() {
		if(lessonsetdb != null)	lessonsetdb.close();
		if(learneddb != null) learneddb.close();
	}
	
	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public void setAssets(AssetManager assets) {
		this.assets = assets;
	}

	public AssetManager getAssets() {
		return assets;
	}

	public Typeface getTypeFace() {
		if (Preferences.D)
			Log.d(TAG, "getTypeFace: " + "fonts" + File.separator
					+ lesson.getFont());
		
		if(lesson.getFont() != null) {
			try {
				Typeface face = Typeface.createFromAsset(getAssets(), "fonts"	+ File.separator + lesson.getFont() + ".ttf");
				
				return face;
			} catch(Exception e) {
				return null;
			}
		}
		return null;
	}

	public Context getContext() {
		return context;
	}
	
	public void setContext(Object context) {
		this.context = (Context)context;
	}

	public SharedPreferences getSharedpreferences() {
		return sharedpreferences;
	}

	public void setSharedpreferences(SharedPreferences sharedpreferences) {
		this.sharedpreferences = sharedpreferences;
	}
}
