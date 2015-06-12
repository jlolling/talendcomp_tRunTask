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

public class GetTaskIdByName extends TaskAction {
	
	public static final String ACTION_NAME = "getTaskIdByName";
	public static final String PARAM_LABEL = "label";
	public static final String PARAM_TASKNAME = "taskName";
	private String taskName = null;
	private static Map<String, String> taskNameMap = new HashMap<String, String>();
	
	public GetTaskIdByName(TACConnection connection) {
		super(connection);
	}

	public String getTaskId(String taskName) throws Exception {
		String taskId = taskNameMap.get(taskName);
		if (taskId == null) {
			setTaskName(taskName);
			execute();
			taskId = getTaskId();
			if (taskId == null) {
				throw new Exception("There is no task with the label:" + taskName);
			}
			taskNameMap.put(taskName, taskId);
		}
		return taskId;
	}

	@Override
	public String getAction() {
		return ACTION_NAME;
	}

	@Override
	public void execute() throws Exception {
		addParam(PARAM_LABEL, taskName);
		addParam(PARAM_TASKNAME, taskName);
		String result = executeRequest();
		if (result != null && result.isEmpty() == false) {
			if (result.trim().startsWith(ERROR)) {
				// error by processing the request
				throw new Exception("action failed: " + result);
			} else {
				taskId = Util.extractByRegexGroup(result, "\"task[\\s]{0,1}ID[:\\s]*\":([0-9]*)", 1, false);
			}
		} else {
			throw new Exception("Request returns no result!");
		}
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		if (taskName == null || taskName.isEmpty()) {
			throw new IllegalArgumentException("taskName cannot be null or empty");
		}
		this.taskName = taskName;
	}

}