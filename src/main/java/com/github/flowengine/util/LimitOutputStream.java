package com.github.flowengine.util;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ProxyOutputStream;


/**
 * 超过maxLength的数据将被忽略
 * @author badqiu
 *
 */
public class LimitOutputStream extends ProxyOutputStream  {
	private long len = 0;
	private long maxLength = -1;
	public LimitOutputStream(OutputStream proxy,long maxLength) {
		super(proxy);
		this.maxLength = maxLength;
	}
	
	@Override
	public void write(byte[] bts) throws IOException {
		if(len > maxLength) {
			return;
		}
		super.write(bts);
	}
	
	@Override
	public void write(byte[] bts, int st, int end) throws IOException {
		if(len > maxLength) {
			return;
		}
		super.write(bts, st, end);
	}
	
	@Override
	public void write(int idx) throws IOException {
		if(len > maxLength) {
			return;
		}
		super.write(idx);
	}
	
	@Override
	protected void beforeWrite(int n) throws IOException {
		super.beforeWrite(n);
		len +=n;
	}

}
