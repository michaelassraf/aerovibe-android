package com.lbis.aerovibe.concurrency;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import android.content.Context;

import com.lbis.aerovibe.enums.PollutantsEnums;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.SensorMeasurementValue;
import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.database.executors.model.SensorDbExecutors;
import com.lbis.database.executors.model.SensorMeasurementDbExecutors;
import com.lbis.database.executors.model.UserMeasurementDbExecutors;

public class DataScheduleTaskRefresher {

	private static DataScheduleTaskRefresher instance;
	final Logger log = Logger.getLogger(DataScheduleTaskRefresher.class.getSimpleName());
	static Context context;

	volatile static HashMap<String, Sensor> sensors = new HashMap<String, Sensor>();
	volatile static HashMap<String, SensorMeasurement> sensorMeasurements = new HashMap<String, SensorMeasurement>();
	volatile static LinkedList<UserMeasurement> userMeasurements = new LinkedList<UserMeasurement>();
	volatile static HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>> userMeasurementsChunked = new HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>>();

	public static DataScheduleTaskRefresher getInstance(Context currentContext) {
		context = currentContext;
		if (instance == null) {
			instance = new DataScheduleTaskRefresher();
		}

		return instance;
	}

	public void refreshAllLists() {
		new RefreshAllDBsRunnable(context, log).run();
		ExecutorService service = Executors.newFixedThreadPool(3);
		List<Future<Boolean>> futures = new LinkedList<Future<Boolean>>();
		futures.add(service.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				setFreshSensors();
				return true;
			}
		}));

		futures.add(service.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				setFreshSensroMeasurements();
				return true;
			}
		}));
		futures.add(service.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				setFreshUserMeasurements();
				braekUserMeasurementsToHashMap();
				return true;
			}
		}));

		for (Future<Boolean> future : futures) {
			try {
				future.get();
			} catch (Throwable th) {
				log.error("Can't wait for update Threads", th);
			}
		}

	}

	public void setFreshSensors() {
		HashMap<String, Sensor> freshSensors = new SensorDbExecutors().getAllAsMap(new Sensor(), context);
		if (freshSensors != null && freshSensors.size() > 0) {
			sensors = freshSensors;
		}
	}

	public void setFreshSensroMeasurements() {
		LinkedList<SensorMeasurement> freshSensorMeasurements = new SensorMeasurementDbExecutors().getAll(new SensorMeasurement(), context);
		if (freshSensorMeasurements != null && freshSensorMeasurements.size() > 0) {
			for (SensorMeasurement sensorMeasurement : freshSensorMeasurements) {
				sensorMeasurements.put(sensorMeasurement.getSensorMeasurementSensorId(), sensorMeasurement);
			}
		}
	}

	public void setFreshUserMeasurements() {
		LinkedList<UserMeasurement> freshUserMeasurements = new UserMeasurementDbExecutors().getAll(new UserMeasurement(), context);
		if (freshUserMeasurements != null && freshUserMeasurements.size() > 0) {
			userMeasurements = freshUserMeasurements;
		}
	}

	public void braekUserMeasurementsToHashMap() {
		if (userMeasurements != null && userMeasurements.size() > 0) {
			HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>> freshUserMeasurementsChunked = new HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>>();
			for (UserMeasurement userMeasurement : userMeasurements) {
				SensorMeasurement sensorMeasurement = userMeasurement.getUserMeasurementSensorMeasurement();
				for (SensorMeasurementValue sensorMeasurementValue : sensorMeasurement.getSensorMeasurementValues()) {
					if (!freshUserMeasurementsChunked.containsKey(sensorMeasurementValue.getSensorMeasurementPollutantsEnums())) {
						freshUserMeasurementsChunked.put(sensorMeasurementValue.getSensorMeasurementPollutantsEnums(), new LinkedList<SensorMeasurementValue>());
					}
					freshUserMeasurementsChunked.get(sensorMeasurementValue.getSensorMeasurementPollutantsEnums()).add(sensorMeasurementValue);
				}
			}
			userMeasurementsChunked = freshUserMeasurementsChunked;
		}
	}

	public HashMap<String, Sensor> getFreshSensorsData() {
		if (context == null) {
			log.info("Won't return sensors, application is null.");
			return null;
		}
		return sensors;
	}

	public LinkedList<UserMeasurement> getFreshUserMeasurementsData() {
		if (context == null) {
			log.info("Won't return user measurements data, application is null.");
			return null;
		}
		return userMeasurements;
	}

	public HashMap<String, SensorMeasurement> getFreshSensorMeasurementData() {
		if (context == null) {
			log.info("Won't return sensor measurements, application is null.");
			return null;
		}
		return sensorMeasurements;
	}

	public HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>> getFreshUserMeasurementsChunkedData() {
		if (context == null) {
			log.info("Won't return user measurements chunked data, application is null.");
			return null;
		}
		return userMeasurementsChunked;
	}
}
