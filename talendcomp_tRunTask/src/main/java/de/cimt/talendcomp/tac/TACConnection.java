package de.cimt.talendcomp.tac;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class TACConnection {
	
	private HttpClient client = null;
	private String url;
	private String user;
	private String password;
	private static final String PATH = "metaServlet";

	public TACConnection(String url) {
		client = new DefaultHttpClient();
		setUrl(url);
	}

	public String getUrl() {
		return url;
	}

	private void setUrl(String url) {
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("url cannot be null or empty");
		}
		if (url.contains("metaServlet") == false) {
			this.url = url + "/" + PATH;
		} else {
			this.url = url;
		}
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

	public String execute(String uri) throws Exception {
		HttpGet get = new HttpGet(uri);
		ResponseHandler<String> handler = new BasicResponseHandler();
		return client.execute(get, handler);
	}
	
	public void close() {
		client.getConnectionManager().shutdown();
	}
	
}
