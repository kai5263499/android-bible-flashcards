package com.werxltd.bibleflash;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DataBaseHelper";

	@SuppressWarnings("unused")
	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, null, null, 1);
		this.myContext = context;
	}

	public SQLiteDatabase createDataBase(String db_name) throws SQLException {
		if (Preferences.D)
			Log.d(TAG, "createDataBase("+db_name+")");
		return SQLiteDatabase.openOrCreateDatabase(db_name, null);
	}
	
	public SQLiteDatabase openDataBase(String db_name) throws SQLException {
		if (Preferences.D)
			Log.d(TAG, "openDataBase("+db_name+")");
		return SQLiteDatabase.openDatabase(db_name, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	public SQLiteDatabase openWritableDataBase(String db_name) throws SQLException {
		if (Preferences.D)
			Log.d(TAG, "openWritableDataBase("+db_name+")");
		return SQLiteDatabase.openDatabase(db_name, null,
				SQLiteDatabase.OPEN_READWRITE);
	}
	
	public static void resetLearnedDB(DataBaseHelper dbh) {
		SQLiteDatabase learneddb  = dbh.createDataBase(Preferences.SWORD_PATH+Preferences.LEARNED_DB);
		learneddb.execSQL("DROP TABLE IF EXISTS \"learned\"");
		learneddb.execSQL("CREATE TABLE \"learned\" (_id INTEGER PRIMARY KEY, key TEXT)");
		learneddb.execSQL("DROP TABLE IF EXISTS \"android_metadata\"");
		learneddb.execSQL("CREATE TABLE \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US')");
		learneddb.execSQL("INSERT INTO \"android_metadata\" VALUES ('en_US')");
		learneddb.close();
	}
	
	@Override
	public synchronized void close() {
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}