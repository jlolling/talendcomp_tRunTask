package de.jlo.talendcomp.tac;

public class GetTaskStatus extends TaskAction {
	
	public static final String ACTION_NAME = "getTaskStatus";
	private String errorStatus;
	private String status;
	private Integer returnCode;

	public GetTaskStatus(TACConnection connection) {
		super(connection);
	}

	@Override
	public String getAction() {
		return ACTION_NAME;
	}

	@Override
	public void execute() throws Exception {
		addParam(PARAM_TASKID, taskId);
		String result = executeRequest();
		if (result != null && result.trim().startsWith(ERROR)) {
			// error by processing the request
			throw new Exception("Getting task status failed: " + result);
		} else {
			errorStatus = Util.extractByRegexGroup(result, "\"errorStatus\":\"([A-Z_a-z0-9]*)\"", 1, false);
			if (errorStatus != null) {
				errorStatus = errorStatus.trim();
			}
			status = Util.extractByRegexGroup(result, "\"status\":\"([A-Z_a-z0-9]*)\"", 1, false);
			if (NO_ERROR.equals(errorStatus) == false) {
				returnCode = 4; // default return code in case of error
			} else {
				returnCode = null;
			}
		}
	}
	
	public boolean isRunning() {
		// running or requesting stop (it is running yet)
		return REQUESTING_RUN.equals(status) || RUNNING.equals(status) || REQUESTING_STOP.equals(status);
	}
	
	public boolean isReadyToRun() {
		return READY_TO_RUN.equals(status);
	}

	public boolean hasErrors() {
		return NO_ERROR.equals(errorStatus) == false;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public String getStatus() {
		return status;
	}
	
	public Integer getReturnCode() {
		return returnCode;
	}
	
	public boolean isPreparing() {
		if (status != null) {
			return (status.contains(GENERATE_PATTERN) || status.contains(DEPLOY_PATTERN) || status.contains(SENDING_SCRIPT_PATTERN)) && hasErrors() == false;
		} else {
			return false;
		}
	}
	
	public boolean isInReadyState() {
		return status.contains(READY);
	}

}