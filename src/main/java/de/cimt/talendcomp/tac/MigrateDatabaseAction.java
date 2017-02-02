package de.cimt.talendcomp.tac;

import org.apache.log4j.Logger;

public class MigrateDatabaseAction extends TACAction {
	
	private static Logger logger = Logger.getLogger(MigrateDatabaseAction.class);
	private String sourceUrl = null;
	private String sourceUser = null;
	private String sourcePasswd = null;
	private String targetUrl = null;
	private String targetUser = null;
	private String targetPasswd = null;
	private String mode = "synchronous";
	private String dbConfigPassword = null;

	public MigrateDatabaseAction(TACConnection connection) {
		super(connection);
	}

	@Override
	public String getAction() {
		return "migrateDatabase";
	}

	@Override
	public void execute() throws Exception {
		if (dbConfigPassword == null || dbConfigPassword.trim().isEmpty()) {
			throw new Exception("dbConfigPassword not set!");
		}
		addParam("dbConfigPassword", dbConfigPassword);
		addParam("mode", mode);
		if (sourceUrl == null || sourceUrl.trim().isEmpty()) {
			throw new Exception("sourceUrl not set!");
		}
		addParam("sourceUrl", sourceUrl);
		if (sourceUser == null || sourceUser.trim().isEmpty()) {
			throw new Exception("sourceUser not set!");
		}
		addParam("sourceUser", sourceUser);
		if (sourcePasswd == null || sourcePasswd.trim().isEmpty()) {
			throw new Exception("sourcePasswd not set!");
		}
		addParam("sourcePasswd", sourcePasswd);
		if (targetUrl == null || targetUrl.trim().isEmpty()) {
			throw new Exception("targetUrl not set!");
		}
		addParam("targetUrl", targetUrl);
		if (targetUser == null || targetUser.trim().isEmpty()) {
			throw new Exception("targetUser not set!");
		}
		addParam("targetUser", targetUser);
		if (targetPasswd == null || targetPasswd.trim().isEmpty()) {
			throw new Exception("targetPasswd not set!");
		}
		addParam("targetPasswd", targetPasswd);
		String result = null;
		try {
			result = executeRequest();
		} catch (Exception e) {
			logger.error("Execute migrateDatabase failed: " + e.getMessage(), e);
			throw e;
		}
		if (result != null && result.trim().startsWith(ERROR)) {
			logger.error("Execute migrateDatabase failed: " + result);
		} else {
			logger.info("Response: " + result);
			System.out.println("Finished migrating database. Please check the result!");
		}
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}

	public String getSourcePasswd() {
		return sourcePasswd;
	}

	public void setSourcePasswd(String sourcePasswd) {
		this.sourcePasswd = sourcePasswd;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public String getTargetPasswd() {
		return targetPasswd;
	}

	public void setTargetPasswd(String targetPasswd) {
		this.targetPasswd = targetPasswd;
	}

	public String getDbConfigPassword() {
		return dbConfigPassword;
	}

	public void setDbConfigPassword(String dbConfigPassword) {
		this.dbConfigPassword = dbConfigPassword;
	}


}
