package com.lbis.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class WebReciever {

	static WebReciever instance = null;
	List<DefaultHttpClient> httpClients = null;
	Object synchronizer = new Object();
	AtomicInteger counter;
	static int conNo = 20;
	static HttpParams httpParamas;

	public static WebReciever getInstance() {
		if (instance == null)
			instance = new WebReciever();

		return instance;
	}

	public void createPool() {
		synchronized (synchronizer) {
			if (httpClients == null)
				synchronized (synchronizer) {
					if (httpClients == null) {
						httpClients = new ArrayList<DefaultHttpClient>();
						counter = new AtomicInteger(0);
						httpParamas = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(httpParamas, 30000);
						HttpConnectionParams.setSoTimeout(httpParamas, 30000);
						while (counter.getAndIncrement() < conNo)
							httpClients.add(new DefaultHttpClient(httpParamas));
					}
				}
		}
	}

	public DefaultHttpClient getConnection() {
		if (httpClients == null)
			createPool();

		synchronized (synchronizer) {
			if (counter.get() >= conNo)
				synchronized (synchronizer) {
					if (counter.get() >= conNo)
						counter.set(0);
				}
		}
		return httpClients.get(counter.getAndIncrement());
	}
}
