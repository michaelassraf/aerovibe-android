package com.lbis.aerovibe.management;

import java.util.Locale;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lbis.aerovibe.enums.DBEnums.MANAGEMENT_BASIC_COMMANDS;

public class SQLiteManagementMethods extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "mazeltov-management.db";
	private static final int DATABASE_VERSION = 1;
	final Logger log = Logger.getLogger(getClass().getSimpleName());

	public SQLiteManagementMethods(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void basicExcution(MANAGEMENT_BASIC_COMMANDS command, SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		try {
			db.execSQL(command.getCommandValue());
			sb.append("All ").append(command.getCommandDescription().toLowerCase(Locale.US)).append(" commands excuted successfully !").append(" Management DB created ");
		} catch (Exception e) {
			sb.append("Couldn't ").append(command.getCommandDescription().toLowerCase(Locale.US)).append(" table due - ").append(e.getMessage());
		}
		log.info(sb.toString());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		basicExcution(MANAGEMENT_BASIC_COMMANDS.BUILD, db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		basicExcution(MANAGEMENT_BASIC_COMMANDS.DESTROY, db);
		basicExcution(MANAGEMENT_BASIC_COMMANDS.BUILD, db);
	}
}
