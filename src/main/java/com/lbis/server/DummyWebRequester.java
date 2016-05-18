package com.lbis.server;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public class DummyWebRequester {
	Logger log = Logger.getLogger(getClass().getSimpleName());

	public String invokeHTTPGetRequest(String url) {
		HttpClient connection = HttpClientBuilder.create().setUserAgent(WebRequester.getUserAgent()).build();
		String verbalResponse = null;
		int numTries = 5;
		int currentTries = 0;
		while (true) {
			try {
				HttpGet httpGet = new HttpGet(url);

				HttpResponse response = connection.execute(httpGet);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK && numTries > 0) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					verbalResponse = out.toString();
					verbalResponse = "Server is down";
					throw new Exception("Http reposnse is empty");
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				verbalResponse = out.toString();
				log.info("HTTP " + verbalResponse);
				log.info("URL is " + url);
				if (verbalResponse == null && numTries > 0)
					throw new Exception("Http reposnse is empty");
				break;
			} catch (Exception e) {
				log.info("Couldn't get response from " + url + " trying for the " + currentTries + " time");
				if (++currentTries == numTries) {
					verbalResponse = e.getMessage();
					log.error("Wow I tried lot of times (" + currentTries + ") HTTP request failed due - ", e);
					break;
				}
			}
		}
		return verbalResponse;
	}

}
