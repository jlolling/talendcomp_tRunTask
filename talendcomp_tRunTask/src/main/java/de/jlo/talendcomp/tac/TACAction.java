package de.jlo.talendcomp.tac;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

public abstract class TACAction {
	
	public static final String NO_ERROR = "NO_ERROR";
	public static final String ERROR = "ERROR";
	public static final String READY_TO_RUN = "READY_TO_RUN";
	public static final String READY = "READY_";
	public static final String RUNNING = "RUNNING";
	public static final String REQUESTING_RUN = "REQUESTING_RUN";
	public static final String REQUESTING_STOP = "REQUESTING_STOP";
	public static final String ENDING_SCRIPT = "ENDING_SCRIPT";
	public static final String GENERATE_PATTERN = "GENERAT";
	public static final String DEPLOY_PATTERN = "DEPLOY";
	public static final String SENDING_SCRIPT_PATTERN = "SEND";
	private HashMap<String, String> params = new HashMap<String, String>();
	private boolean debug = false;
	private String response = null;
	private String json;
	private TACConnection connection;
	private int maxAttempts = 1;
	private int repeatWaitTime = 5000;
	private int currentRepeatCounter = 0;
	private Exception lastException = null;
	
	public TACAction(TACConnection connection) {
		if (connection == null) {
			throw new IllegalArgumentException("Connection cannot be null");
		}
		this.connection = connection;
		this.maxAttempts = connection.getMaxAttempts();
		this.repeatWaitTime = connection.getRepeatWaitTime();
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
		currentRepeatCounter = 0;
		for ( ; currentRepeatCounter < maxAttempts; currentRepeatCounter++) {
			lastException = null;
			try {
				response = connection.execute(uri);
				if (debug) {
					System.out.println("Response:" + response);
				}
				String errorMessage = Util.extractByRegexGroup(response, "\"error\":\"([a-z0-9\\-:#\\s.\\\\\\\"_,!'?]*)\",", 1, false);
				if (errorMessage != null && errorMessage.isEmpty() == false) {
					throw new Exception("Server error:" + errorMessage);
				}
				break; // if everything is fine
			} catch (Exception e) {
				lastException = e;
				if (debug) {
					System.err.println(getAction() + " failed " + (currentRepeatCounter + 1) + " times with error:" + e.getMessage());
					System.out.println("Shutdown http client...");
				}
				connection.close();
				if (currentRepeatCounter < (maxAttempts - 1)) {
					System.out.println("Wait " + repeatWaitTime + " ms before retry...");
					Thread.sleep(repeatWaitTime);
				}
				if (debug) {
					System.out.println("Initialise new http client...");
				}
				connection.init();
			}
		}
		if (lastException != null) {
			throw lastException;
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

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxRepeats(Integer repeatMax) {
		if (repeatMax != null && repeatMax >= 0) {
			this.maxAttempts = repeatMax + 1;
		} else {
			this.maxAttempts = 1;
		}
	}

	public void setRepeatWaitTime(Integer repeatWaitTime) {
		if (repeatWaitTime != null && repeatWaitTime > 0) {
			this.repeatWaitTime = repeatWaitTime;
		}
	}

	public int getCurrentRepeatCounter() {
		return currentRepeatCounter;
	}
	
}
