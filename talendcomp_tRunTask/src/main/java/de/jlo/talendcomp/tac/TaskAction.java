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

public abstract class TaskAction extends TACAction {

	public static final String PARAM_TASKID = "taskId";
	protected String taskId;

	public TaskAction(TACConnection connection) {
		super(connection);
	}

	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		if (taskId == null || taskId.isEmpty()) {
			throw new IllegalArgumentException("taskId cannot be null or empty");
		}
		this.taskId = taskId;
	}

}
