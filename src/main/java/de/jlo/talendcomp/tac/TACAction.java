/**
 * Copyright 2015 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jlo.talendcomp.tac;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class TACAction {
	
	private static Logger logger = Logger.getLogger(TACAction.class);
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
			debug("Request json:" + json);
		}
		sb.append(Base64.encodeBase64String(json.getBytes("UTF-8")));
		return sb.toString();
	}
	
	protected String executeRequest() throws Exception {
		String uri = buildRequestUri();
		currentRepeatCounter = 0;
		long currentRepeatWaitTime = repeatWaitTime;
		for ( ; currentRepeatCounter < maxAttempts; currentRepeatCounter++) {
			lastException = null;
			try {
				response = connection.execute(uri);
				if (debug) {
					debug("Response:" + response);
				}
				String errorMessage = Util.extractByRegexGroup(response, "\"error\":\"([a-z0-9\\-:#\\s.\\\\\\\"_,!'?]*)\",", 1, false);
				if (errorMessage != null && errorMessage.isEmpty() == false) {
					throw new Exception("Server error: " + errorMessage);
				}
				break; // if everything is fine
			} catch (Exception e) {
				String message = e.getMessage();
				if (message != null) {
					if (message.contains("still processing")) {
						error("Got a still processing error. Stop retrying requests, need a complete status recheck!", e);
					}
				}
				error("Request failed: " + e.getMessage(), e);
				lastException = e;
				if (debug) {
					debug(getAction() + " failed " + (currentRepeatCounter + 1) + " times with error:" + e.getMessage());
					debug("Shutdown http client...");
				}
				connection.close();
				if (currentRepeatCounter < (maxAttempts - 1)) {
					info("Wait " + currentRepeatWaitTime + " ms before retry. Current attempt: " + (currentRepeatCounter + 1) + ", max attempts: " + maxAttempts);
					Thread.sleep(currentRepeatWaitTime);
					currentRepeatWaitTime = currentRepeatWaitTime * 2;
				}
				if (debug) {
					debug("Initialise new http client...");
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
		if (logger != null) {
			if (debug) {
				logger.setLevel(Level.DEBUG);
			} else {
				logger.setLevel(Level.INFO);
			}
		}
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
	
	public void info(String message) {
		if (logger != null) {
			logger.info(message);
		} else {
			System.out.println("INFO: " + message);
		}
	}
	
	public void debug(String message) {
		if (logger != null) {
			logger.debug(message);
		} else {
			System.out.println("DEBUG: " + message);
		}
	}

	public void error(String message, Throwable t) {
		if (message == null) {
			message = t.getMessage();
		}
		if (logger != null) {
			logger.error(message, t);
		} else {
			System.err.println("ERROR: " + message);
			t.printStackTrace(System.err);
		}
	}

}
