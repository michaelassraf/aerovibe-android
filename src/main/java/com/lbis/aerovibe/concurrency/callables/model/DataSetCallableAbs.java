package com.lbis.aerovibe.concurrency.callables.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import android.content.Context;

import com.lbis.database.executors.ExecuteMethods;
import com.lbis.database.model.KeyObjectIfc;
import com.lbis.database.model.ValueObjectIfc;

public abstract class DataSetCallableAbs<CURRENTHANDLEDOBJECT extends ValueObjectIfc<CURRENTHANDLEDOBJECT> & KeyObjectIfc & Serializable, DATABASEIMPL extends ExecuteMethods<CURRENTHANDLEDOBJECT>> implements Callable<Boolean> {

	protected DATABASEIMPL databaseImpl;
	protected Context context;
	protected Logger log;
	protected LinkedList<CURRENTHANDLEDOBJECT> listOfObjects;

	public DataSetCallableAbs(Context context, Logger log) {
		this.context = context;
		this.log = log;
	}

	public boolean deleteFromCache() {
		try {
			databaseImpl.dropAll(context);
		} catch (Throwable th) {
			log.error("Failed to delete " + listOfObjects.getClass().getName() + " objects from cache", th);
			return false;
		}
		return true;
	}

	public boolean insertToCache() {
		try {
			databaseImpl.putAll(context, listOfObjects);
		} catch (Throwable th) {
			log.error("Failed to insert  " + listOfObjects.getClass().getName() + " objects to cach", th);
			return false;
		}
		return true;
	}

}
