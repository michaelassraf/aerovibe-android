package com.lbis.aerovibe.management;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteManagementClient {

	private static SQLiteDatabase connection;

	public static synchronized SQLiteDatabase getConnetion(Context context) {
		if (connection == null) {
			connection = new SQLiteManagementMethods(context).getWritableDatabase();
		}
		return connection;
	}
}
