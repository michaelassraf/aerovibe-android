package com.lbis.server.requests;

public class SinceRequest {

	private String callType;
	private long since;

	public SinceRequest(String callType, long since) {
		this.callType = callType;
		this.since = since;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public long getSince() {
		return since;
	}

	public void setSince(long since) {
		this.since = since;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(" Get all" + callType).append(" since " + since).toString();
	}
}
