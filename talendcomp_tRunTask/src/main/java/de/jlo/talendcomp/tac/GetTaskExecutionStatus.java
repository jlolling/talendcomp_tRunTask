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

public class GetTaskExecutionStatus extends TACAction {
	
	public static final String ACTION_NAME = "getTaskExecutionStatus";
	private String errorStatus;
	private String execBasicStatus;
	private String execDetailedStatus;
	private Integer jobExitCode;
	private Integer returnCode;
	private String execRequestId;
	public static String OK = "OK";
	
	public GetTaskExecutionStatus(TACConnection connection) {
		super(connection);
	}

	@Override
	public String getAction() {
		return ACTION_NAME;
	}

	@Override
	public void execute() throws Exception {
		if (execRequestId == null || execRequestId.isEmpty()) {
			throw new IllegalStateException("execRequestId not set!");
		}
		addParam("execRequestId", execRequestId);
		String result = executeRequest();
		if (result != null && result.trim().startsWith(ERROR)) {
			// error by processing the request
			throw new Exception("Getting task status failed: " + result);
		} else {
			String returnCodeStr = Util.extractByRegexGroup(result, "\"returnCode\":([0-9]*)", 1, false);
			if (returnCodeStr != null && returnCodeStr.isEmpty() == false) {
				returnCode = Integer.parseInt(returnCodeStr);
			}
			errorStatus = Util.extractByRegexGroup(result, "\"errorStatus\":\"([A-Z_a-z0-9]*)\"", 1, false);
			if (errorStatus != null) {
				errorStatus = errorStatus.trim();
			}
			jobExitCode = null;
			execBasicStatus = Util.extractByRegexGroup(result, "\"execBasicStatus\":\"([A-Z_a-z0-9]*)\"", 1, false);
			execDetailedStatus = Util.extractByRegexGroup(result, "\"execDetailedStatus\":\"([A-Z_a-z0-9]*)\"", 1, false);
			String exitCode = Util.extractByRegexGroup(result, "\"jobExitCode\":([0-9]*)", 1, false);
			if (exitCode != null && exitCode.isEmpty() == false) {
				jobExitCode = Integer.parseInt(exitCode);
			}
		}
	}
	
	public boolean isRunning() {
		// running or requesting stop (it is running yet)
		return RUNNING.equals(execBasicStatus);
	}
	
	public boolean hasErrors() {
		return NO_ERROR.equals(errorStatus) == false;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public String getExecBasicStatus() {
		return execBasicStatus;
	}
	
	public String getExecDetailedStatus() {
		return execDetailedStatus;
	}

	public Integer getJobExitCode() {
		return jobExitCode;
	}
	
	public Integer getReturnCode() {
		return returnCode;
	}

	public boolean isPreparing() {
		if (execDetailedStatus != null) {
			return (execDetailedStatus.contains(GENERATE_PATTERN) || execDetailedStatus.contains(DEPLOY_PATTERN) || execDetailedStatus.contains(SENDING_SCRIPT_PATTERN)) && hasErrors() == false;
		} else {
			return false;
		}
	}

	public void setExecRequestId(String execRequestId) {
		if (execRequestId != null && execRequestId.isEmpty() == false) {
			this.execRequestId = execRequestId;
		} else {
			throw new IllegalArgumentException("execRequestId cannot be null or empty.");
		}
	}

}