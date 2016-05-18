package com.lbis.server.requests;

public class SearchRequest {

	private String searchQuery;
	private String searchType;

	public SearchRequest(String searchQuery, String searchType) {
		super();
		this.searchQuery = searchQuery;
		this.searchType = searchType;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("==Search Request==").append(" Get all" + searchType).append(" with " + searchQuery).toString();
	}
}
