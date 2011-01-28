package com.werxltd.bibleflash;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Preferences {
	protected static final boolean D = true;
	protected static final String PREFS_NAME = "bibleflash";

	protected static final String SWORD_PATH = "/sdcard/sword/lessons/";
	protected static final String LESSONS_DB = "lessons.db";
	protected static final String LEARNED_DB = "learned.db";

	private String current_lessonset;
	private String current_lesson_name;

	private boolean quizzingEnabled;
	private int quizzingInterval;

	private boolean hideKnownCards;

	private int textSize;
	private int lessonOffset = 0;

	private int versionNum = 0;

	public static void savePreferences(SharedPreferences settings,
			Preferences preferences) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("current_lessonset", preferences
				.getCurrent_lessonset());
		editor.putString("current_lesson", preferences.getCurrent_lesson());
		editor.putBoolean("quizzingEnabled", preferences.isQuizzingEnabled());
		editor.putBoolean("hideKnownCards", preferences.hideKnownCards());
		editor.putInt("textSize", preferences.getTextSize());
		editor.putInt("versionNum", preferences.getVersionNum());
		editor.putInt("card_num_offset", preferences.getLessonOffset());
		editor.commit();
	}

	public static Preferences getPreferences(SharedPreferences settings) {
		Preferences preferences = new Preferences();

		preferences.setCurrent_lessonset(settings.getString(
				"current_lessonset", null));
		preferences.setCurrent_lesson(settings
				.getString("current_lesson", null));
		preferences.setTextSize(settings.getInt("textSize", 30));
		preferences.setVersionNum(settings.getInt("versionNum", 0));
		preferences.setLessonOffset(settings.getInt("card_num_offset", -1));
		preferences.setQuizzingEnabled(settings.getBoolean("quizzingEnabled",
				false));
		preferences.setHideKnownCards(settings.getBoolean("hideKnownCards",
				false));

		return preferences;
	}

	public static int getVersionCode(PackageManager pm) {
		PackageInfo pInfo = null;
		try {
			pInfo = pm.getPackageInfo("com.beanie.test",
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return 0;
		}

		return pInfo.versionCode;
	}

	public String getCurrent_lessonset() {
		return current_lessonset;
	}

	public void setCurrent_lessonset(String currentLessonset) {
		current_lessonset = currentLessonset;
	}

	public String getCurrent_lesson() {
		return current_lesson_name;
	}

	public void setCurrent_lesson(String currentLesson) {
		current_lesson_name = currentLesson;
	}

	public boolean isQuizzingEnabled() {
		return quizzingEnabled;
	}

	public void setQuizzingEnabled(boolean quizzingEnabled) {
		this.quizzingEnabled = quizzingEnabled;
	}

	public int getQuizzingInterval() {
		return quizzingInterval;
	}

	public void setQuizzingInterval(int quizzingInterval) {
		this.quizzingInterval = quizzingInterval;
	}

	public boolean hideKnownCards() {
		return hideKnownCards;
	}

	public void setHideKnownCards(boolean hideKnownCards) {
		this.hideKnownCards = hideKnownCards;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}

	public int getVersionNum() {
		return versionNum;
	}

	public void setLessonOffset(int lessonOffset) {
		this.lessonOffset = lessonOffset;
	}

	public int getLessonOffset() {
		return lessonOffset;
	}
}
