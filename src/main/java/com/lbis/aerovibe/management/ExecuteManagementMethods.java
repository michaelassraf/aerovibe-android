package com.lbis.aerovibe.management;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.Cursor;

import com.lbis.aerovibe.model.Token;

public class ExecuteManagementMethods {

	final Logger log = Logger.getLogger(getClass().getSimpleName());
	static Token token = null;

	public Token getTokenAndUserId(Context context) {
		if (token == null) {
			Cursor curs = SQLiteManagementClient.getConnetion(context).rawQuery("SELECT * FROM Management", null);
			curs.moveToFirst();
			if (curs != null && curs.getCount() > 0) {
				token = new Token(curs.getString(1), curs.getString(0));
				log.info("Token extracted is " + token);
			}
			curs.close();
		}
		return token;
	}

	public void setTokenAndUserId(Context context, Token newToken) {

		if (newToken == null || newToken.getTokenUserId() == null || newToken.getTokenValue() == null) {
			log.info("Token recieved is bad, will exit");
			return;
		}

		if (token != null && newToken.getTokenValue().equals(token.getTokenValue())) {
			log.info("No need to replace token");
			return;
		}

		if (newToken != null) {
			StringBuilder stringBuilder = new StringBuilder().append("INSERT OR REPLACE INTO Management (userId, tokenValue) VALUES(").append("'" + newToken.getTokenUserId() + "',").append("'" + newToken.getTokenValue() + "')");
			SQLiteManagementClient.getConnetion(context).execSQL(stringBuilder.toString());
			log.info("New token successfully imported !");
			token = null;
			return;
		}
	}

}
