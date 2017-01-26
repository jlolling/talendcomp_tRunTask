
import de.cimt.talendcomp.tac.GetTaskExecutionStatus;
import de.cimt.talendcomp.tac.GetTaskIdByName;
import de.cimt.talendcomp.tac.GetTaskStatus;
import de.cimt.talendcomp.tac.RunTask;
import de.cimt.talendcomp.tac.TACConnection;

public class Check {
	
	private static String url = "http://on-0337-jll.local:8080/tac_560";
//	private static String url = "http://on-0337-jll.local:8080/org.talend.administrator.542";
	private static String user = "jan.lolling@cimt-ag.de";
	private static String passwd = "lolli";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public void testRunTaskASynchron(int die_code) throws Exception {
		TACConnection conn = new TACConnection(url);
		conn.setUser(user);
		conn.setPassword(passwd);
		GetTaskIdByName gtn = new GetTaskIdByName(conn);
		gtn.setDebug(true);
		String taskId = null;
		if ((die_code % 3) == 0) {
			taskId = gtn.getTaskId("test_dummy3");
		} else if ((die_code % 2) == 0) {
			taskId = gtn.getTaskId("test_dummy2");
		} else {
			taskId = gtn.getTaskId("test_dummy1");
		}
		RunTask rt = new RunTask(conn);
		rt.setDebug(true);
		rt.setSynchronous(false);
		rt.addContextParam("date_var", new java.util.Date());
		rt.addContextParam("string_var", "\\\\unc äöü");
		rt.addContextParam("integer_var", die_code + 99);
		rt.addContextParam("die_code", die_code % 5);
		try {
			rt.setTaskId(taskId);
			rt.execute();
			System.out.println("Request send:" + rt.getExecRequestId());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		int waitTimeUntilRunning = 1000;
		GetTaskExecutionStatus status = new GetTaskExecutionStatus(conn);
		status.setDebug(true);
		status.setExecRequestId(rt.getExecRequestId());
		try {
			status.execute();
			while (status.isRunning()) {
				Thread.sleep(waitTimeUntilRunning);
				status.execute();
			}
			System.out.println("execBasicStatus:" + status.getExecBasicStatus());
			System.out.println("execDetailedStatus:" + status.getExecDetailedStatus());
			System.out.println("returnCode:" + status.getReturnCode());
			System.out.println("jobExitCode:" + status.getJobExitCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}

	public void testRunTaskSynchron() {
		TACConnection conn = new TACConnection(url);
		conn.setUser(user);
		conn.setPassword(passwd);
		GetTaskIdByName gtn = new GetTaskIdByName(conn);
		gtn.setDebug(true);
		gtn.setTaskName("test_dummy1");
		String taskId = gtn.getTaskId();
		RunTask rt = new RunTask(conn);
		rt.setDebug(true);
		rt.setSynchronous(true);
		rt.addContextParam("die_code", 99);
		try {
			rt.setTaskId(taskId);
			rt.execute();
			System.out.println("exeRequestId=" + rt.getExecRequestId());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println(rt.getResponse());
		GetTaskExecutionStatus status = new GetTaskExecutionStatus(conn);
		status.setDebug(true);
		status.setExecRequestId(rt.getExecRequestId());
		try {
			status.execute();
			System.out.println("execBasicStatus:" + status.getExecBasicStatus());
			System.out.println("errorStatus:" + status.getErrorStatus());
			System.out.println("returnCode:" + status.getReturnCode());
			System.out.println("ready.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetTaskIdByName() {
		TACConnection conn = new TACConnection(url);
		conn.setUser(user);
		conn.setPassword(passwd);
		GetTaskIdByName rt = new GetTaskIdByName(conn);
		rt.setDebug(true);
		rt.setTaskName("test_dummy1");
		try {
			rt.execute();
			System.out.println(rt.getTaskId());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}
	
	public void testGetTaskStatus() {
		TACConnection conn = new TACConnection(url);
		conn.setUser(user);
		conn.setPassword(passwd);
		GetTaskStatus rt = new GetTaskStatus(conn);
		rt.setDebug(true);
		rt.setTaskId("1");
		try {
			rt.execute();
			System.out.println(rt.getErrorStatus());
			System.out.println(rt.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}

	public static void main(String[] args) {
//		System.out.println(Util.extractByRegexGroup("Response:{\"error\":\"Incorrect password\",\"returnCode\":3}", "Response:\\{\"error\":\"([a-zA-Z0-9\\s.;_]*)", 1));
		Check t = new Check();
		try {
//			t.testGetTaskIdByName();
//			t.testRunTaskSynchron();
			for (int i = 0; i < 10000000; i++) {
				System.out.println("###############################################");
				t.testRunTaskASynchron(i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
