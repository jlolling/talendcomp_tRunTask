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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	public RunTask(TACConnection connection) {
		super(connection);
	}

	@Override
	public String getAction() {
		return ACTION_NAME;
	}

	@Override
	public void execute() throws Exception {
		addParam(PARAM_TASKID, taskId);
		addParam(PARAM_MODE, synchronous ? "synchronous" : "asynchronous");
		if (contextParams.isEmpty() == false) {
			addParam(PARAM_CONTEXT, buildContextParamJson());
		}
		String result = executeRequest();
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
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean firstLoop = true;
		for (Map.Entry<String, Object> entry : contextParams.entrySet()) {
			if (firstLoop) {
				firstLoop = false;
			} else {
				sb.append(",");
			}
			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\":");
			sb.append(convertValue(entry.getValue()));
		}
		sb.append("}");
		return sb.toString();
	}
		
	private String convertValue(Object value) {
		if (value != null) {
			if (value instanceof Date) {
				return "\"" + date_param_format.format((Date) value) + "\"";
			} else if (value instanceof String) {
				String s = ((String) value).replace("\\", "\\\\");
				s = s.replace("\n", "\\n");
				s = s.replace("\r", "\\r");
				s = s.replace("\t", "\\t");
				return "\"" + s + "\"";
			} else {
				return "\"" + value.toString() + "\"";
			}
		} else {
			return "";
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

}