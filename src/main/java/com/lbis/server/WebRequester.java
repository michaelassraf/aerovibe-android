package com.lbis.server;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import android.content.Context;
import android.util.Xml.Encoding;

import com.lbis.aerovibe.enums.HTTPHeaders;
import com.lbis.aerovibe.enums.URLEnums;
import com.lbis.aerovibe.enums.URLEnums.URLs;
import com.lbis.aerovibe.gson.GsonSerializer;
import com.lbis.aerovibe.management.ExecuteManagementMethods;
import com.lbis.aerovibe.model.Token;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.database.executors.model.UserLocationDbExecutors;
import com.lbis.server.actions.UserActions;
import com.lbis.server.requests.SearchRequest;
import com.lbis.server.requests.SinceRequest;

public abstract class WebRequester<CLASSTYPE> {

	Logger log = Logger.getLogger(getClass().getSimpleName());

	private static String userAgent = null;

	public static String getUserAgent() {
		if (userAgent == null)
			userAgent = System.getProperty("http.agent");
		return userAgent;
	}

	private HttpClient getClientFromFactory() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setUserAgent(getUserAgent());
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		builder.setDefaultRequestConfig(requestBuilder.build());
		return builder.build();
	}

	public StringEntity makeJsonRequest(CLASSTYPE object) throws UnsupportedEncodingException {
		StringEntity requestEntity = new StringEntity(GsonSerializer.getInstance().toJson(object).toString(), Encoding.UTF_8.toString());
		requestEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
//		log.debug("Json " + GsonSerializer.getInstance().toJson(object).toString());
		return requestEntity;
	}

	public String makeStringJsonRequest(CLASSTYPE object) throws UnsupportedEncodingException {
		return GsonSerializer.getInstance().toJson(object).toString();
	}

	public StringEntity makeSinceJsonRequest(SinceRequest object) throws UnsupportedEncodingException {
		StringEntity requestEntity = new StringEntity(GsonSerializer.getInstance().toJson(object).toString(), Encoding.UTF_8.toString());
		requestEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
//		log.debug("Json " + GsonSerializer.getInstance().toJson(object).toString());
		return requestEntity;
	}

	public StringEntity makeSearchJsonRequest(SearchRequest object) throws UnsupportedEncodingException {
		StringEntity requestEntity = new StringEntity(GsonSerializer.getInstance().toJson(object).toString(), Encoding.UTF_8.toString());
		requestEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
//		log.debug("Json " + GsonSerializer.getInstance().toJson(object).toString());
		return requestEntity;
	}

	public LinkedList<CLASSTYPE> getAllObjectsSince(long since, Context context, String prefix) throws UnsupportedEncodingException, Exception {
		LinkedList<CLASSTYPE> serialized = createObjectsFromJson(invokeHTTPPostRequest(getAllObjectsSinceUrl(), makeSinceJsonRequest(new SinceRequest(getClassType().getSimpleName() + prefix, since)), getClientFromFactory(), context));
		return serialized;
	}

	public LinkedList<CLASSTYPE> getObjectsWithGet(Context context) throws UnsupportedEncodingException, Exception {
		LinkedList<CLASSTYPE> serialized = createObjectsFromJson(invokeHTTPGetRequest(getObjectsForObjectUrl(), getClientFromFactory(), context));
		return serialized;
	}

	public LinkedList<CLASSTYPE> getObjectsForObject(CLASSTYPE object, Context context, String prefix) throws UnsupportedEncodingException, Exception {
		LinkedList<CLASSTYPE> serialized = createObjectsFromJson(invokeHTTPPostRequest(getObjectsForObjectUrl(), makeJsonRequest(object), getClientFromFactory(), context));
		return serialized;
	}

	public LinkedList<CLASSTYPE> getObjectsForObject(CLASSTYPE object, Context context) throws UnsupportedEncodingException, Exception {
		return getObjectsForObject(object, context, "");
	}

	public LinkedList<CLASSTYPE> getAllObjectsSince(long since, Context context) throws Exception {
		return this.getAllObjectsSince(since, context, getPullSincePrefix());
	}

	public LinkedList<CLASSTYPE> getAllObjectsInit(Context context) throws UnsupportedEncodingException, Exception {
		return this.getAllObjectsSince(0, context, getInitializeSincePrefix());
	}

	public LinkedList<CLASSTYPE> searchForObjects(String searchPhrase, Context context) throws UnsupportedEncodingException, Exception {
		LinkedList<CLASSTYPE> serialized = createObjectsFromJson(invokeHTTPPostRequest(getSearchObjectsUrl(), makeSearchJsonRequest(new SearchRequest(searchPhrase, getClassType().getSimpleName())), getClientFromFactory(), context));
		return serialized;
	}

	public CLASSTYPE createObjectFromJson(String json) {
//		log.debug("Json " + json);
		return GsonSerializer.getInstance().fromJson(json, getClassType());
	}

	public LinkedList<CLASSTYPE> createObjectsFromJson(String json) {
//		log.debug("Json " + json);
		return GsonSerializer.getInstance().fromJson(json, getMultipleClassType());
	}

	public String invokeHTTPGetRequest(URLEnums.URLs url, final HttpClient connection, Context context) {
		String verbalResponse = null;
		int numTries = 5;
		int currentTries = 0;
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<HttpResponse> future;
		while (true) {
			try {
				final HttpGet httpGet = new HttpGet(url.getValue());
				setRequestHeaders(httpGet, context);
				future = service.submit(new Callable<HttpResponse>() {

					@Override
					public HttpResponse call() throws Exception {
						return connection.execute(httpGet);
					}
				});

				verbalResponse = handleResponse(future.get(5, TimeUnit.SECONDS), context, currentTries);
				if (verbalResponse == null && numTries > 0)
					throw new Throwable("Http reposnse is empty");
				break;
			} catch (Throwable th) {
				log.debug("Couldn't get response from " + url.getValue() + " trying for the " + currentTries + " time",th);
				if (++currentTries == numTries) {
					verbalResponse = th.getMessage();
					log.error("Wow I tried lot of times (" + currentTries + ") HTTP request failed due - ", th);
					break;
				}
			}
		}
		return verbalResponse;
	}

	private void setRequestHeaders(HttpRequestBase clientBase, Context context) {
		clientBase.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
		clientBase.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		clientBase.addHeader(HTTPHeaders.TokenHeader.gethTTPHeaderValue(), new ExecuteManagementMethods().getTokenAndUserId(context) != null ? new ExecuteManagementMethods().getTokenAndUserId(context).getTokenValue() : "");
		clientBase.addHeader(HTTPHeaders.UserHeader.gethTTPHeaderValue(), new ExecuteManagementMethods().getTokenAndUserId(context) != null ? new ExecuteManagementMethods().getTokenAndUserId(context).getTokenUserId() : "");
		clientBase.setHeader(HttpHeaders.ACCEPT_ENCODING, HTTPHeaders.GZIPHeader.gethTTPHeaderValue());
	}

	private String handleResponse(HttpResponse response, Context context, int numTries) throws Throwable {
		String verbalResponse = null;
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK && numTries > 0) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();
			verbalResponse = out.toString();
			throw new Throwable(verbalResponse);
		}

		String tokenValueExtracted = response.getFirstHeader(HTTPHeaders.TokenHeader.gethTTPHeaderValue()) != null ? response.getFirstHeader(HTTPHeaders.TokenHeader.gethTTPHeaderValue()).getValue() : null;
		String userIdValueExtracted = response.getFirstHeader(HTTPHeaders.UserHeader.gethTTPHeaderValue()) != null ? response.getFirstHeader(HTTPHeaders.UserHeader.gethTTPHeaderValue()).getValue() : null;

		Header contentLength = response.getFirstHeader(HttpHeaders.CONTENT_LENGTH);

		if (contentLength != null && contentLength.getValue() != null && contentLength.getName() != null) {
//			log.debug("Response " + contentLength.getName() + " is " + contentLength.getValue());
		}

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			new UserActions().logIn(new UserDbExecutors().getFirstObjectIfExists(context), context);
		}

		if (tokenValueExtracted != null && userIdValueExtracted != null)
			new ExecuteManagementMethods().setTokenAndUserId(context, new Token(tokenValueExtracted, userIdValueExtracted));

		Double latitudeValueExtracted = null;
		Double longitudeValueExtracted = null;
		try {
			latitudeValueExtracted = response.getFirstHeader(HTTPHeaders.UserLatitude.gethTTPHeaderValue()) != null ? Double.parseDouble(response.getFirstHeader(HTTPHeaders.UserLatitude.gethTTPHeaderValue()).getValue()) : null;
			longitudeValueExtracted = response.getFirstHeader(HTTPHeaders.UserLongitude.gethTTPHeaderValue()) != null ? Double.parseDouble(response.getFirstHeader(HTTPHeaders.UserLongitude.gethTTPHeaderValue()).getValue()) : null;
		} catch (Throwable th) {
			log.error("Cannot parse value" + response.getFirstHeader(HTTPHeaders.UserLatitude.gethTTPHeaderValue()).getValue() + " or " + response.getFirstHeader(HTTPHeaders.UserLongitude.gethTTPHeaderValue()).getValue(), th);
		}

		if (latitudeValueExtracted != null && longitudeValueExtracted != null)
			new UserLocationDbExecutors().put(context, new UserLocation(latitudeValueExtracted, longitudeValueExtracted, new ExecuteManagementMethods().getTokenAndUserId(context).getTokenUserId(), null));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		out.close();
		verbalResponse = out.toString();
//		log.debug("HTTP " + verbalResponse);
		return out.toString();
	}

	public String invokeHTTPPostRequest(URLEnums.URLs url, StringEntity requestEntity, final HttpClient connection, Context context) {
		String verbalResponse;
		int numTries = 5;
		int currentTries = 0;
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<HttpResponse> future;
		while (true) {
			try {
				final HttpPost postMethod = new HttpPost(url.getValue());
				setRequestHeaders(postMethod, context);
				postMethod.setEntity(requestEntity);
				future = service.submit(new Callable<HttpResponse>() {

					@Override
					public HttpResponse call() throws Exception {
						return connection.execute(postMethod);
					}
				});

				verbalResponse = handleResponse(future.get(5, TimeUnit.SECONDS), context, currentTries);
				if (verbalResponse == null && numTries > 0)
					throw new Throwable("Http reposnse is empty");
				break;
			} catch (Throwable th) {
				log.error("Couldn't get response from " + url.getValue() + " trying for the " + currentTries + " time");
				if (++currentTries == numTries) {
					verbalResponse = th.getMessage();
					log.error("Wow I tried lot of times (" + currentTries + ") HTTP request failed due - ", th);
					break;
				}
			}
		}
		return verbalResponse;
	}

	public CLASSTYPE createNewRequest(CLASSTYPE object, Context context) throws UnsupportedEncodingException, Exception {
		return getDeserializedObject(object, context, createNewRequestUrl());
	}

	public CLASSTYPE getObjectForObject(CLASSTYPE object, Context context) throws UnsupportedEncodingException, Exception {
		return getDeserializedObject(object, context, getObjectForObjectUrl());
	}

	public CLASSTYPE getObjectForObject(CLASSTYPE object, Context context, URLs url) throws UnsupportedEncodingException, Exception {
		return getDeserializedObject(object, context, url);
	}

	private CLASSTYPE getDeserializedObject(CLASSTYPE object, Context context, URLEnums.URLs url) throws UnsupportedEncodingException, Exception {
		CLASSTYPE serialized = createObjectFromJson(invokeHTTPPostRequest(url, makeJsonRequest(object), getClientFromFactory(), context));
		return serialized;
	}

	public abstract Class<CLASSTYPE> getClassType();

	public abstract String getClassName();

	public abstract URLEnums.URLs createNewRequestUrl();

	public abstract URLEnums.URLs getObjectForObjectUrl();

	public abstract URLEnums.URLs getObjectsForObjectUrl();

	public abstract URLEnums.URLs getAllObjectsSinceUrl();

	public abstract URLEnums.URLs getSearchObjectsUrl();

	public abstract String getInitializeSincePrefix();

	public abstract String getPullSincePrefix();

	protected abstract Type getMultipleClassType();
}