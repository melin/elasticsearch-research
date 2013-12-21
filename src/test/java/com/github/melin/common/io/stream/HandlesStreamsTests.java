/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Create on @2013-12-18 @下午5:48:18 
 * @author bsli@ustcinfo.com
 */
public class HandlesStreamsTests {

    @Test
    public void testSharedStringHandles() throws Exception {
        String test1 = "test1";
        String test2 = "test2";
        String test3 = "test3";
        String test4 = "test4";
        String test5 = "test5";
        String test6 = "test6";

        BytesStreamOutput bout = new BytesStreamOutput();
        HandlesStreamOutput out = new HandlesStreamOutput(bout);
        out.writeString(test1);
        out.writeString(test1);
        out.writeString(test2);
        out.writeString(test3);
        out.writeSharedString(test4);
        out.writeSharedString(test4);
        out.writeSharedString(test5);
        out.writeSharedString(test6);

        BytesStreamInput bin = new BytesStreamInput(bout.bytes());
        HandlesStreamInput in = new HandlesStreamInput(bin);
        String s1 = in.readString();
        String s2 = in.readString();
        String s3 = in.readString();
        String s4 = in.readString();
        String s5 = in.readSharedString();
        String s6 = in.readSharedString();
        String s7 = in.readSharedString();
        String s8 = in.readSharedString();

        assertThat(s1, equalTo(test1));
        assertThat(s2, equalTo(test1));
        assertThat(s3, equalTo(test2));
        assertThat(s4, equalTo(test3));
        assertThat(s5, equalTo(test4));
        assertThat(s6, equalTo(test4));
        assertThat(s7, equalTo(test5));
        assertThat(s8, equalTo(test6));

        assertThat(s1, not(sameInstance(s2)));
        assertThat(s5, sameInstance(s6));
    }
}