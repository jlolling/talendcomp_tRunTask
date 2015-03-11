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
