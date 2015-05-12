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
	private boolean closedInputOutput = false;
	
	public AsyncOutputStreamThread(InputStream input,OutputStream output) {
		this.input = input;
		this.output = output;
	}
	
	public AsyncOutputStreamThread(InputStream input,OutputStream output,boolean closedInputOutput) {
		this.input = input;
		this.output = output;
		this.closedInputOutput = closedInputOutput;
	}
	
	public void run() {
		try {
			try {
				IOUtils.copy(input, output);
			} catch (IOException e) {
				logger.error("io copy error",e);
			}
		}finally {
			if(closedInputOutput) {
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}
		}
	}

}
