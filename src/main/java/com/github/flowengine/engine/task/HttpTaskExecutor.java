package com.github.flowengine.engine.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.flowengine.engine.TaskExecutor;
import com.github.flowengine.model.FlowContext;
import com.github.flowengine.model.FlowTask;

public class HttpTaskExecutor implements TaskExecutor {

	private static Logger logger = LoggerFactory.getLogger(HttpTaskExecutor.class);
	
	@Override
	public void exec(FlowTask task, FlowContext flowContext)
			throws Exception {
		String url = task.getScript();
		URL urlObject = new URL(url);
		HttpURLConnection conn = (HttpURLConnection)urlObject.openConnection();
		
		try {
			conn.connect();
			String response = getResponseBody(conn);
			int responseCode = conn.getResponseCode();
			if(responseCode == 200) {
				logger.info("success execute url:"+url+" responseBody:"+response);
			}else {
				throw new RuntimeException("http response code error,expected 200,but actual:"+responseCode+" url:"+url+", responseBody:"+response);
			}
		}finally {
			conn.disconnect();
		}
	}

	private String getResponseBody(HttpURLConnection conn) throws IOException {
		InputStream inputStream = conn.getInputStream();
		String response = null;
		try {
			response = IOUtils.toString(inputStream);
		}finally {
			IOUtils.closeQuietly(inputStream);
		}
		return response;
	}

}
