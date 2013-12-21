/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.multicast;

import java.io.IOException;

import com.github.melin.common.io.stream.StreamInput;
import com.github.melin.common.io.stream.StreamOutput;
import com.github.melin.common.io.stream.Streamable;

/**
 * Create on @2013-12-20 @下午5:20:15 
 * @author bsli@ustcinfo.com
 */
public class PingResponse implements Streamable {

	private String msg;
	
	@Override
	public void readFrom(StreamInput in) throws IOException {
		msg = in.readString();
	}

	@Override
	public void writeTo(StreamOutput out) throws IOException {
		out.writeString(msg);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
