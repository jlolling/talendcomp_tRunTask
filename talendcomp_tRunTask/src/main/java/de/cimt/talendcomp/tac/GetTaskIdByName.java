package de.cimt.talendcomp.tac;

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
				taskId = Util.extractByRegexGroup(result, "\"task ID[:\\s]*\":([0-9]*)", 1);
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