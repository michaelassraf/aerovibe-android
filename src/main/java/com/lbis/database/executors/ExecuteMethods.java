package com.lbis.database.executors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.database.Cursor;

import com.lbis.aerovibe.gson.GsonSerializer;
import com.lbis.database.model.KeyObjectIfc;
import com.lbis.database.model.ValueObjectIfc;

public abstract class ExecuteMethods<HANDLEDOBJECT extends ValueObjectIfc<HANDLEDOBJECT> & KeyObjectIfc> {
	final protected Logger log = Logger.getLogger(getClass().getSimpleName());

	public LinkedList<HANDLEDOBJECT> getAll(HANDLEDOBJECT object, Context context) {

		log.info("Starting to fetch objects from " + getTableName());

		Cursor objects = SQLiteClient.getConnetion(context).rawQuery("SELECT * FROM " + getTableName() + " ORDER BY k DESC", null);

		LinkedList<HANDLEDOBJECT> map = createLinkedListFromCursor(objects, new LinkedList<HANDLEDOBJECT>());

		objects.close();

		if (map == null) {
			log.info("No cache data for " + getTableName() + "!!! Baasa !!");
			return map;
		}

		log.info("Successfully fetched " + map.size() + " objects from " + getTableName() + "!!!");

		return map;
	}

	public HashMap<String, HANDLEDOBJECT> getAllAsMap(HANDLEDOBJECT object, Context context) {

		log.info("Starting to fetch objects from " + getTableName());

		Cursor objects = SQLiteClient.getConnetion(context).rawQuery("SELECT * FROM " + getTableName() + " ORDER BY k DESC", null);

		HashMap<String, HANDLEDOBJECT> map = createHashMapFromCursor(objects, new HashMap<String, HANDLEDOBJECT>());

		objects.close();

		if (map == null) {
			log.info("No cache data for " + getTableName() + "!!! Baasa !!");
			return map;
		}

		log.info("Successfully fetched " + map.size() + " objects from " + getTableName() + "!!!");

		return map;
	}

	public HANDLEDOBJECT get(String id, Context context) {

		Cursor cursor = SQLiteClient.getConnetion(context).rawQuery("SELECT * FROM " + getTableName() + " WHERE k='" + id + "'", null);
		log.info("looking for " + getTableName() + " with id " + id + "in local cache");
		if (!cursor.moveToNext()) {
			log.info(getTableName() + " with id " + id + " is not exist in local cache.");
			cursor.close();
			return null;
		}

		HANDLEDOBJECT fetchedObject = GsonSerializer.getInstance().fromJson(cursor.getString(1), getHandledObjectType());

		cursor.close();

		log.info("Successfully fetched " + fetchedObject.toString());
		return fetchedObject;
	}

	public HANDLEDOBJECT getFirstObjectIfExists(Context context) {
		log.info("Looking for any match in " + getTableName());
		Cursor cursor = SQLiteClient.getConnetion(context).rawQuery("SELECT * FROM " + getTableName(), null);

		if (!cursor.moveToNext()) {
			log.info(getTableName() + " is empty");
			cursor.close();
			return null;
		}

		HANDLEDOBJECT fetchedObject = GsonSerializer.getInstance().fromJson(cursor.getString(1), getHandledObjectType());

		cursor.close();

		log.info("Successfully fetched " + fetchedObject.toString());
		return fetchedObject;
	}

	public HANDLEDOBJECT get(HANDLEDOBJECT object, Context context) {
		return this.get(object.getSQLObjectKey(), context);
	}

	protected LinkedList<HANDLEDOBJECT> createLinkedListFromCursor(Cursor objects, LinkedList<HANDLEDOBJECT> map) {

		objects.moveToFirst();
		if (objects.isAfterLast()) {
			objects.close();
			return null;
		}

		do {

			map.add(GsonSerializer.getInstance().fromJson(objects.getString(1), getHandledObjectType()));

		} while (objects.moveToNext());

		objects.close();

		return map;
	}

	protected HashMap<String, HANDLEDOBJECT> createHashMapFromCursor(Cursor objects, HashMap<String, HANDLEDOBJECT> map) {

		objects.moveToFirst();
		if (objects.isAfterLast()) {
			objects.close();
			return null;
		}
		do {
			HANDLEDOBJECT object = GsonSerializer.getInstance().fromJson(objects.getString(1), getHandledObjectType());
			map.put(object.getObjectKey(), object);
		} while (objects.moveToNext());
		objects.close();
		return map;
	}

	public void putAll(Context context, LinkedList<HANDLEDOBJECT> objects) {

		if (objects == null || objects.size() < 1 || objects.get(0) == null) {
			return;
		}

		log.info("Starting to insert objects to " + getTableName());

		List<String> insertCommands = new ArrayList<String>();
		int t = 0;

		while (t < objects.size()) {
			int k = 0;
			StringBuilder sb = new StringBuilder();

			sb.append("INSERT OR REPLACE INTO ").append(getTableName()).append("('k', 'v') ");

			for (t = t + 0; t < objects.size() && k < 450; t++) {
				k++;
				if (t % 450 != 0)
					sb.append("UNION ");
				sb.append("SELECT '" + objects.get(t).getSQLObjectKey()).append("' AS k, ").append("'" + GsonSerializer.getInstance().toJson(objects.get(t), getHandledObjectType()) + "'").append(" AS v ");
			}

			insertCommands.add(sb.toString());
		}

		for (String insertCommand : insertCommands)
			SQLiteClient.getConnetion(context).execSQL(insertCommand);

		log.info("Successfully inserted " + t + " objects to " + getTableName() + "!!!");
	}

	public void put(Context context, HANDLEDOBJECT object) {
		if (object == null)
			return;
		log.info("Inserting object to " + getTableName());
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT OR REPLACE INTO ").append(getTableName()).append("('k', 'v') ");
		sb.append("VALUES ");
		sb.append("('" + object.getSQLObjectKey() + "',").append("'" + GsonSerializer.getInstance().toJson(object, getHandledObjectType()) + "')");
		log.info(sb.toString());
		SQLiteClient.getConnetion(context).execSQL(sb.toString());
		log.info("Successfully inserted " + object.toString() + " object to " + getTableName() + "!!!");
	}

	public void drop(Context context, HANDLEDOBJECT object) {
		if (object == null)
			return;
		// check if the object exists
		HANDLEDOBJECT dbCheck = get(object.getSQLObjectKey(), context);
		if (dbCheck == null) {
			log.info("No match for object with id " + object.getSQLObjectKey() + " found is table " + getTableName());
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(getTableName()).append(" WHERE k=").append(object.getObjectKey());
		SQLiteClient.getConnetion(context).execSQL(sb.toString());
		log.info("Successfully deleted " + object.toString() + " object from " + getTableName() + "!!!");
	}

	public void dropAll(Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(getTableName());
		SQLiteClient.getConnetion(context).execSQL(sb.toString());
		log.info("Successfully deleted everything from " + getTableName() + "!!!");
	}

	public String getTableName() {
		return getHandledObjectType().getSimpleName() + "s";
	}

	public abstract Class<HANDLEDOBJECT> getHandledObjectType();
}
