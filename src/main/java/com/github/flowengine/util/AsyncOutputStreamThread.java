package com.github.flowengine.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsyncOutputStreamThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(AsyncOutputStreamThread.class);
	private InputStream input;
	private OutputStream output;
	
	public AsyncOutputStreamThread(InputStream inputStream,OutputStream output) {
		this.input = input;
		this.output = output;
	}
	
	public void run() {
		try {
			try {
				IOUtils.copy(input, output);
			} catch (IOException e) {
				logger.error("io copy error",e);
			}
		}finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}

}
