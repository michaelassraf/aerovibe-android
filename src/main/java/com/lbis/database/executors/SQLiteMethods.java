package com.lbis.database.executors;

import java.util.Locale;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lbis.aerovibe.enums.DBEnums.BASIC_COMMANDS;
import com.lbis.aerovibe.utils.AerovibeUtils;

public class SQLiteMethods extends SQLiteOpenHelper {

	final Logger log = Logger.getLogger(getClass().getSimpleName());
	private static String DATABASE_NAME = "aerovibe.db";
	private static final int DATABASE_VERSION = 1;

	public SQLiteMethods(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void basicExcution(BASIC_COMMANDS command, SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		try {
			for (String s : AerovibeUtils.getInstance().getTablesNames()) {
				db.execSQL(new StringBuilder().append(command.getBasicCommandValue()).append(s.toString()).append(command.getBottomCommandValue()).toString());
			}
			sb.append("All ").append(command.getCommandValueDescription().toLowerCase(Locale.US)).append(" commands excuted successfully !").append(" Tables created are ").append(AerovibeUtils.getInstance().getTablesNames().toString());
		} catch (Exception e) {
			sb.append("Couldn't ").append(command.getCommandValueDescription().toLowerCase(Locale.US)).append(" tables due - ").append(e.getMessage());
		}

		log.info(sb.toString());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		basicExcution(BASIC_COMMANDS.BUILD, db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		basicExcution(BASIC_COMMANDS.DESTROY, db);
		basicExcution(BASIC_COMMANDS.BUILD, db);
	}

}
