/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Assert;
import org.junit.Test;

import com.github.melin.common.Constants;

/**
 * Create on @2013-12-18 @下午4:42:10 
 * @author bsli@ustcinfo.com
 */
public class BytesStreamsTests {

    @Test
    public void testSimpleStreams() throws Exception {
        Assert.assertTrue(Constants.JRE_IS_64BIT);
        
        //写入
        BytesStreamOutput out = new BytesStreamOutput();
        out.writeBoolean(false);
        out.writeByte((byte) 1);
        out.writeShort((short) -1);
        out.writeInt(-1);
        out.writeVInt(2);
        out.writeLong(-3);
        out.writeVLong(4);
        out.writeFloat(1.1f);
        out.writeDouble(2.2);
        int[] intArray = {1, 2, 3};
        out.writeGenericValue(intArray);
        long[] longArray = {1, 2, 3};
        out.writeGenericValue(longArray);
        float[] floatArray = {1.1f, 2.2f, 3.3f};
        out.writeGenericValue(floatArray);
        double[] doubleArray = {1.1, 2.2, 3.3};
        out.writeGenericValue(doubleArray);
        out.writeString("hello");
        out.writeString("goodbye");
        
        //读取
        BytesStreamInput in = new BytesStreamInput(out.bytes().toBytes(), false);
        assertThat(in.readBoolean(), equalTo(false));
        assertThat(in.readByte(), equalTo((byte) 1));
        assertThat(in.readShort(), equalTo((short) -1));
        assertThat(in.readInt(), equalTo(-1));
        assertThat(in.readVInt(), equalTo(2));
        assertThat(in.readLong(), equalTo((long) -3));
        assertThat(in.readVLong(), equalTo((long) 4));
        assertThat((double) in.readFloat(), closeTo(1.1, 0.0001));
        assertThat(in.readDouble(), closeTo(2.2, 0.0001));
        assertThat(in.readGenericValue(), equalTo((Object)intArray));
        assertThat(in.readGenericValue(), equalTo((Object)longArray));
        assertThat(in.readGenericValue(), equalTo((Object)floatArray));
        assertThat(in.readGenericValue(), equalTo((Object)doubleArray));
        assertThat(in.readString(), equalTo("hello"));
        assertThat(in.readString(), equalTo("goodbye"));
    }

    @Test
    public void testGrowLogic() throws Exception {
    	Assert.assertTrue(Constants.JRE_IS_64BIT);
    	
        BytesStreamOutput out = new BytesStreamOutput();
        out.writeBytes(new byte[BytesStreamOutput.DEFAULT_SIZE - 5]);
        assertThat(out.bufferSize(), equalTo(2048)); // remains the default
        out.writeBytes(new byte[1 * 1024]);
        assertThat(out.bufferSize(), equalTo(4608));
        out.writeBytes(new byte[32 * 1024]);
        assertThat(out.bufferSize(), equalTo(40320));
        out.writeBytes(new byte[32 * 1024]);
        assertThat(out.bufferSize(), equalTo(90720));
    }
}