package de.cimt.talendcomp.tac;

public abstract class TaskAction extends TACAction {

	public static final String NO_ERROR = "NO_ERROR";
	public static final String ERROR = "ERROR";
	public static final String READY_TO_RUN = "READY_TO_RUN";
	public static final String RUNNING = "RUNNING";
	public static final String REQUESTING_RUN = "REQUESTING_RUN";
	public static final String REQUESTING_STOP = "REQUESTING_STOP";
	public static final String GENERATE_PATTERN = "GENERAT";
	public static final String DEPLOY_PATTERN = "DEPLOY";
	public static final String SENDING_SCRIPT_PATTERN = "SEND";
	public static final String PARAM_TASKID = "taskId";
	protected String taskId;

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
