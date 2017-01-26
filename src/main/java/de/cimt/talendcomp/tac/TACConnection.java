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
package de.cimt.talendcomp.tac;

import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TACConnection {
	
	private CloseableHttpClient client = null;
	private String url;
	private String user;
	private String password;
	private static final String PATH = "metaServlet";
	private int maxAttempts = 1;
	private int repeatWaitTime = 100;

	public TACConnection(String url) {
		setUrl(url);
		init();
	}
	
	public void init() {
        client = HttpClients.custom().build();
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
		try {
			client.close();
		} catch (IOException e) {}
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public int getRepeatWaitTime() {
		return repeatWaitTime;
	}

	public void setMaxRepeats(Integer repeatMax) {
		if (repeatMax != null && repeatMax >= 0) {
			this.maxAttempts = repeatMax + 1;
		} else {
			this.maxAttempts = 1;
		}
	}

	public void setRepeatWaitTime(Integer repeatWaitTime) {
		if (repeatWaitTime != null && repeatWaitTime > 0) {
			this.repeatWaitTime = repeatWaitTime;
		}
	}
	
}
