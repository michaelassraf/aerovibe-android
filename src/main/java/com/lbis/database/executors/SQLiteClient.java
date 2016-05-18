package com.lbis.database.executors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteClient {

	private static SQLiteDatabase connection;

	public static synchronized SQLiteDatabase getConnetion(Context context) {
		if (connection == null) {
			connection = new SQLiteMethods(context).getWritableDatabase();
		}
		return connection;
	}
}