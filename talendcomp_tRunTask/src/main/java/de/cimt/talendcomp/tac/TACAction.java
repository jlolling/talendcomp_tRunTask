package de.cimt.talendcomp.tac;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class TACAction {
	
	private String tacUrl;
	private static final String PATH = "metaServlet";
	private String user;
	private String password;
	private HashMap<String, String> params = new HashMap<String, String>();
	private boolean debug = false;
	private String response = null;
	private String json;
	
	public String getTacUrl() {
		return tacUrl;
	}
	
	public void setTacUrl(String tacUrl) {
		if (tacUrl == null || tacUrl.isEmpty()) {
			throw new IllegalArgumentException("tacUrl cannot be null or empty");
		}
		this.tacUrl = tacUrl;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		if (user == null || user.isEmpty()) {
			throw new IllegalArgumentException("user cannot be null or empty");
		}
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("password cannot be null or empty");
		}
		this.password = password;
	}
	
	/**
	 * action to be done by the request
	 * @return name of action
	 */
	public abstract String getAction();
	
	public abstract void execute() throws Exception;
	
	public void addParam(String key, String value) {
		if (key == null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("key cannot be null or empty");
		}
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("value cannot be null or empty");
		}
		params.put(key, value);
	}
	
	public void clearParams() {
		params.clear();
	}
	
	private String buildJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"actionName\":\"");
		sb.append(getAction());
		sb.append("\",\"authUser\":\"");
		sb.append(user);
		sb.append("\",\"authPass\":\"");
		sb.append(password);
		sb.append("\"");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(",\"");
			sb.append(entry.getKey());
			String value = entry.getValue();
			if (value.startsWith("{") && value.endsWith("}")) {
				// add a JSON object as value, avoid enclosures here!
				sb.append("\":");
				sb.append(value);
			} else {
				sb.append("\":\"");
				sb.append(value);
				sb.append("\"");
			}
		}
		sb.append("}");
		json = sb.toString();
		return json;
	}

	private String buildRequestUri() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(tacUrl);
		sb.append("/");
		sb.append(PATH);
		sb.append("?");
		String json = buildJson();
		if (debug) {
			System.out.println("Request:" + json);
		}
		sb.append(Base64.encodeBase64String(json.getBytes("UTF-8")));
		return sb.toString();
	}
	
	protected String executeRequest() throws Exception {
		HttpClient client = new DefaultHttpClient();
		try {
			String uri = buildRequestUri();
			HttpGet get = new HttpGet(uri);
			ResponseHandler<String> handler = new BasicResponseHandler();
			response = client.execute(get, handler);
			if (debug) {
				System.out.println("Response:" + response);
			}
			String errorMessage = Util.extractByRegexGroup(response, "\\{\"error\":\"([a-zA-Z0-9\\s.;_]*)", 1);
			if (errorMessage != null && errorMessage.isEmpty() == false) {
				throw new Exception("Server error:" + errorMessage);
			}
			return response;
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getResponse() {
		return response;
	}

	public String getJson() {
		return json;
	}
	
	public String getTimeAsString() {
		return Util.getCurrentTimeAsString();
	}
	
}
