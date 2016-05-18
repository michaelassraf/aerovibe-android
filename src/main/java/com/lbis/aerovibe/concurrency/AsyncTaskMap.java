package com.lbis.aerovibe.concurrency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.AsyncTask;

public class AsyncTaskMap {

	HashMap<String, AsyncTask<?, ?, ?>> mapInstance = new HashMap<String, AsyncTask<?, ?, ?>>();
	static AsyncTaskMap instance;

	public AsyncTaskMap() {
	}

	public static AsyncTaskMap getAsyncHashMap() {
		if (instance == null)
			instance = new AsyncTaskMap();
		return instance;
	}

	public void cancelAll() {
		Iterator<Map.Entry<String, AsyncTask<?, ?, ?>>> entries = mapInstance.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, AsyncTask<?, ?, ?>> entry = entries.next();
			entry.getValue().cancel(true);
		}
	}

	public void add(AsyncTask<?, ?, ?> entry) {
		mapInstance.put(entry.toString(), entry);
	}
}
