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
package de.cimt.talendcomp.tac;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.jlo.talendcomp.json.JsonDocument;

public class RunTask extends TaskAction {
	
	public static final String ACTION_NAME = "runTask";
	public static final String PARAM_MODE = "mode";
	public static final String PARAM_CONTEXT = "context"; 
	private String errorStatus;
	private boolean synchronous = true;
	private Map<String, Object> contextParams = new HashMap<String, Object>();
	private String status;
	private Integer returnCode;
	private SimpleDateFormat date_param_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String execRequestId;
	private boolean needRestartWithCheckTaskStatus = false;

	public RunTask(TACConnection connection) {
		super(connection);
	}

	@Override
	public String getAction() {
		return ACTION_NAME;
	}

	@Override
	public void execute() throws Exception {
		needRestartWithCheckTaskStatus = false;
		addParam(PARAM_TASKID, taskId);
		addParam(PARAM_MODE, synchronous ? "synchronous" : "asynchronous");
		if (contextParams.isEmpty() == false) {
			addParam(PARAM_CONTEXT, buildContextParamJson());
		}
		String result = null;
		try {
			result = executeRequest();
		} catch (Exception e) {
			String message = e.getMessage();
			if (message != null) {
				if (message.contains("still processing")) {
					needRestartWithCheckTaskStatus = true;
					info("Task #" + taskId + " is already running - what a surprise - we have to check the task status again.");
				}
			}
		}
		if (result != null && result.trim().startsWith(ERROR)) {
			// error by processing the request
			throw new Exception("Starting task failed: " + result);
		} else {
			execRequestId = Util.extractByRegexGroup(result, "\"execRequestId\":\"([A-Z_a-z0-9]*)\"", 1, false);
			if (synchronous) {
				errorStatus = Util.extractByRegexGroup(result, "\"errorStatus\":\"([A-Z_a-z0-9]*)\"", 1, false);
				if (errorStatus != null) {
					errorStatus = errorStatus.trim();
				}
				status = Util.extractByRegexGroup(result, "\"status\":\"([A-Z_a-z0-9]*)\"", 1, false);
				String returnCodeStr = Util.extractByRegexGroup(result, "\"returnCode\":([0-9]*)", 1, false);
				if (returnCodeStr != null && returnCodeStr.isEmpty() == false) {
					returnCode = Integer.parseInt(returnCodeStr);
				}
			}
		}
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}
	
	public void addContextParam(String key, Object value) {
		if (key != null && key.isEmpty() == false && value != null) {
			contextParams.put(key, value);
		}
	}
	
	private String buildContextParamJson() {
		JsonDocument doc = new JsonDocument(false);
		ObjectNode context = (ObjectNode) doc.getRootNode();
		for (Map.Entry<String, Object> entry : contextParams.entrySet()) {
			context.put(entry.getKey(), convertValue(entry.getValue()));
		}
		return doc.toString();
	}
		
	private String convertValue(Object value) {
		if (value != null) {
			if (value instanceof Date) {
				return date_param_format.format((Date) value);
			} else if (value instanceof String) {
				return (String) value;
			} else {
				return value.toString();
			}
		} else {
			return null;
		}
	}

	public String getStatus() {
		return status;
	}
	
	public Integer getReturnCode() {
		return returnCode;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public String getExecRequestId() {
		return execRequestId;
	}
	
	public boolean useRequestHandling() {
		return execRequestId != null;
	}

	public boolean needRestartWithCheckTaskStatus() {
		return needRestartWithCheckTaskStatus;
	}

}