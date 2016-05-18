package com.lbis.aerovibe.concurrency;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import android.content.Context;

import com.lbis.aerovibe.concurrency.callables.SensorMeasurementsCallable;
import com.lbis.aerovibe.concurrency.callables.SensorsCallable;
import com.lbis.aerovibe.concurrency.callables.UserMeasurementsCallable;

public class RefreshAllDBsRunnable implements Runnable {

	Logger log;
	Context context;

	public RefreshAllDBsRunnable(Context context, Logger log) {
		this.log = log;
		this.context = context;
	}

	ExecutorService service = Executors.newFixedThreadPool(3);

	public void run() {

		List<Future<Boolean>> listOfFutres = new LinkedList<Future<Boolean>>();

		final SensorMeasurementsCallable sensorMeasurementsCallable = new SensorMeasurementsCallable(context, log);
		final SensorsCallable sensorsCallable = new SensorsCallable(context, log);
		final UserMeasurementsCallable userMeasurementsCallable = new UserMeasurementsCallable(context, log);

		listOfFutres.add(service.submit(sensorMeasurementsCallable));
		listOfFutres.add(service.submit(sensorsCallable));
		listOfFutres.add(service.submit(userMeasurementsCallable));

		//Get everything from server
		for (Future<Boolean> future : listOfFutres) {
			try {
				if (!future.get())
					return;
			} catch (Throwable th) {
				log.error("Can't wait for web content", th);
			}
		}

		listOfFutres = new LinkedList<Future<Boolean>>();

		//Delete everything from cache
		listOfFutres.add(service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return sensorMeasurementsCallable.deleteFromCache();
			}
		}));

		listOfFutres.add(service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return sensorsCallable.deleteFromCache();
			}
		}));
		listOfFutres.add(service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return userMeasurementsCallable.deleteFromCache();
			}
		}));
		//Wait for everything to be deleted
		for (Future<Boolean> future : listOfFutres) {
			try {
				if (!future.get())
					return;
			} catch (Throwable th) {
				log.error("Can't wait for content deletion", th);
			}
		}
		
		listOfFutres = new LinkedList<Future<Boolean>>();

		//Insert everything to cache
		listOfFutres.add(service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return sensorMeasurementsCallable.insertToCache();
			}
		}));

		listOfFutres.add(service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return sensorsCallable.insertToCache();
			}
		}));
		listOfFutres.add(service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return userMeasurementsCallable.insertToCache();
			}
		}));
		
		//Wait for everything to be deleted
		for (Future<Boolean> future : listOfFutres) {
			try {
				if (!future.get())
					return;
			} catch (Throwable th) {
				log.error("Can't wait for content to be inserted", th);
			}
		}
	}
}
