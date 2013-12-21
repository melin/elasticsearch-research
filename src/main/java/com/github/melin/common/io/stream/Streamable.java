/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.IOException;

/**
 * Create on @2013-12-18 @下午3:41:01 
 * @author bsli@ustcinfo.com
 */
public interface Streamable {
	
	void readFrom(StreamInput in) throws IOException;

    void writeTo(StreamOutput out) throws IOException;
}
