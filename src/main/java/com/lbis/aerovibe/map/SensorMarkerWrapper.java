package com.lbis.aerovibe.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.lbis.aerovibe.model.Sensor;

public class SensorMarkerWrapper extends Sensor implements ClusterItem {

	public SensorMarkerWrapper(Sensor sensor) {
		this.sensorExternalId = sensor.getSensorExternalId();
		this.sensorDataProvidor = sensor.getSensorDataProvidor();
		this.sensorCity = sensor.getSensorCity();
		this.sensorCountry = sensor.getSensorCountry();
		this.sensorAddressImage = sensor.getSensorAddressImage();
		this.sensorLatitude = sensor.getSensorLatitude();
		this.sensorLongitude = sensor.getSensorLongitude();
		this.sensorDescription = sensor.getSensorDescription();
		this.sensorDetails = sensor.getSensorDetails();
		this.sensorId = sensor.getSensorId();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LatLng getPosition() {
		return new LatLng(getSensorLatitude(), getSensorLongitude());
	}

}
