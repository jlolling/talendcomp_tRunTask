package de.cimt.talendcomp.tac;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public abstract class TACAction {
	
	private HashMap<String, String> params = new HashMap<String, String>();
	private boolean debug = false;
	private String response = null;
	private String json;
	private TACConnection connection;
	
	public TACAction(TACConnection connection) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		this.connection = connection;
	}
	
	/**
	 * action to be done by the request
	 * @return name of action
	 */
	public abstract String getAction();
	
	public abstract void execute() throws Exception;
	
	public void addParam(String key, String value) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("key cannot be null or empty");
		}
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("value cannot be null or empty");
		}
		params.put(key, value);
	}
	
	public void clearParams() {
		params.clear();
	}
	
	private String buildJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionName\":\"");
		sb.append(getAction());
		sb.append("\",\"authUser\":\"");
		sb.append(connection.getUser());
		sb.append("\",\"authPass\":\"");
		sb.append(connection.getPassword());
		sb.append("\"");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(",\"");
			sb.append(entry.getKey());
			String value = entry.getValue();
			if (value.startsWith("{") && value.endsWith("}")) {
				// add a JSON object as value, avoid enclosures here!
				sb.append("\":");
				sb.append(value);
			} else {
				sb.append("\":\"");
				sb.append(value);
				sb.append("\"");
			}
		}
		sb.append("}");
		json = sb.toString();
		return json;
	}

	private String buildRequestUri() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(connection.getUrl());
		sb.append("?");
		String json = buildJson();
		if (debug) {
			System.out.println("Request:" + json);
		}
		sb.append(Base64.encodeBase64String(json.getBytes("UTF-8")));
		return sb.toString();
	}
	
	protected String executeRequest() throws Exception {
		String uri = buildRequestUri();
		response = connection.execute(uri);
		if (debug) {
			System.out.println("Response:" + response);
		}
		String errorMessage = Util.extractByRegexGroup(response, "\\{\"error\":\"([a-zA-Z0-9\\s.;_]*)", 1);
		if (errorMessage != null && errorMessage.isEmpty() == false) {
			throw new Exception("Server error:" + errorMessage);
		}
		return response;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getResponse() {
		return response;
	}

	public String getJson() {
		return json;
	}
	
	public String getTimeAsString() {
		return Util.getCurrentTimeAsString();
	}
	
}
