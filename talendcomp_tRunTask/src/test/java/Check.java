
import de.cimt.talendcomp.tac.GetTaskIdByName;
import de.cimt.talendcomp.tac.GetTaskStatus;
import de.cimt.talendcomp.tac.RunTask;
import de.cimt.talendcomp.tac.Util;

public class Check {
	
//	private static String url = "http://on-0337-jll.local:8080/tac_560";
	private static String url = "http://on-0337-jll.local:8080/org.talend.administrator.542";
	private static String user = "jan.lolling@cimt-ag.de";
	private static String passwd = "lolli";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public void testRunTaskASynchron() throws Exception {
		GetTaskIdByName gtn = new GetTaskIdByName();
		gtn.setDebug(true);
		gtn.setTacUrl(url);
		gtn.setUser(user);
		gtn.setPassword(passwd);
		String taskId = gtn.getTaskId("test_dummy1");
		RunTask rt = new RunTask();
		rt.setDebug(true);
		rt.setTacUrl(url);
		rt.setUser(user);
		rt.setPassword(passwd);
		rt.setSynchronous(false);
		rt.addContextParam("string_var", "\\\\unc");
		try {
			rt.setTaskId(taskId);
			rt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		int waitTimeUntilRunning = 200;
		GetTaskStatus status = new GetTaskStatus();
		status.setDebug(true);
		status.setTacUrl(rt.getTacUrl());
		status.setUser(rt.getUser());
		status.setPassword(rt.getPassword());
		status.setTaskId(rt.getTaskId());
		try {
			status.execute();
			while (status.isRunning() == false) {
				Thread.sleep(waitTimeUntilRunning);
				status.execute();
			}
			System.out.println("status:" + status.getStatus());
			System.out.println("running...");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		int waitUntilFinished = 1000;
		try {
			status.execute();
			System.out.println("wait until finished...");
			while (status.isRunning()) {
				Thread.sleep(waitUntilFinished);
				status.execute();
			}
			System.out.println("status:" + status.getStatus() + " errorStatus:" + status.getErrorStatus());
			System.out.println("ready.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testRunTaskSynchron() {
		GetTaskIdByName gtn = new GetTaskIdByName();
		gtn.setDebug(true);
		gtn.setTacUrl(url);
		gtn.setUser(user);
		gtn.setPassword(passwd);
		gtn.setTaskName("test_job");
		String taskId = gtn.getTaskId();
		RunTask rt = new RunTask();
		rt.setDebug(true);
		rt.setTacUrl(url);
		rt.setUser(user);
		rt.setPassword(passwd);
		rt.setSynchronous(true);
		rt.addContextParam("die_code", 99);
		try {
			rt.setTaskId(taskId);
			rt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println(rt.getResponse());
		GetTaskStatus status = new GetTaskStatus();
		status.setTacUrl(rt.getTacUrl());
		status.setUser(rt.getUser());
		status.setPassword(rt.getPassword());
		status.setTaskId(rt.getTaskId());
		try {
			status.execute();
			System.out.println("response:" + status.getResponse());
			System.out.println("status:" + status.getStatus() + " errorStatus:" + status.getErrorStatus());
			System.out.println("ready.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetTaskIdByName() {
		GetTaskIdByName rt = new GetTaskIdByName();
		rt.setDebug(true);
		rt.setTacUrl(url);
		rt.setUser(user);
		rt.setPassword(passwd);
		rt.setTaskName("test_dummy1");
		try {
			rt.execute();
			System.out.println(rt.getTaskId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetTaskStatus() {
		GetTaskStatus rt = new GetTaskStatus();
		rt.setDebug(true);
		rt.setTacUrl("http://on-0337-jll.local:8080/org.talend.administrator");
		rt.setUser("jan.lolling@cimt-ag.de");
		rt.setPassword("lolli");
		rt.setTaskId("1");
		try {
			rt.execute();
			System.out.println(rt.getErrorStatus());
			System.out.println(rt.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		System.out.println(Util.extractByRegexGroup("Response:{\"error\":\"Incorrect password\",\"returnCode\":3}", "Response:\\{\"error\":\"([a-zA-Z0-9\\s.;_]*)", 1));
		Check t = new Check();
		try {
			t.testGetTaskIdByName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
