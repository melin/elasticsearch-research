/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.bytes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.github.melin.common.BytesRef;
import com.github.melin.common.UnicodeUtil;
import com.github.melin.common.io.stream.BytesStreamInput;
import com.github.melin.common.io.stream.StreamInput;
import com.google.common.base.Charsets;

/**
 * Create on @2013-12-18 @下午4:03:29 
 * @author bsli@ustcinfo.com
 */
public class BytesArray implements BytesReference {

    public static final BytesArray EMPTY = new BytesArray(BytesRef.EMPTY_BYTES, 0, 0);

    private byte[] bytes;
    private int offset;
    private int length;

    public BytesArray(String bytes) {
        BytesRef bytesRef = new BytesRef();
        UnicodeUtil.UTF16toUTF8(bytes, 0, bytes.length(), bytesRef);
        this.bytes = bytesRef.bytes;
        this.offset = bytesRef.offset;
        this.length = bytesRef.length;
    }

    public BytesArray(BytesRef bytesRef) {
        this(bytesRef, false);
    }

    public BytesArray(BytesRef bytesRef, boolean deepCopy) {
        if (deepCopy) {
            BytesRef copy = BytesRef.deepCopyOf(bytesRef);
            bytes = copy.bytes;
            offset = copy.offset;
            length = copy.length;
        } else {
            bytes = bytesRef.bytes;
            offset = bytesRef.offset;
            length = bytesRef.length;
        }
    }

    public BytesArray(byte[] bytes) {
        this.bytes = bytes;
        this.offset = 0;
        this.length = bytes.length;
    }

    public BytesArray(byte[] bytes, int offset, int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public byte get(int index) {
        return bytes[offset + index];
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public BytesReference slice(int from, int length) {
        if (from < 0 || (from + length) > this.length) {
            throw new IllegalArgumentException("can't slice a buffer with length [" + this.length + "], with slice parameters from [" + from + "], length [" + length + "]");
        }
        return new BytesArray(bytes, offset + from, length);
    }

    @Override
    public StreamInput streamInput() {
        return new BytesStreamInput(bytes, offset, length, false);
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(bytes, offset, length);
    }

    @Override
    public byte[] toBytes() {
        if (offset == 0 && bytes.length == length) {
            return bytes;
        }
        return Arrays.copyOfRange(bytes, offset, offset + length);
    }

    @Override
    public BytesArray toBytesArray() {
        return this;
    }

    @Override
    public BytesArray copyBytesArray() {
        return new BytesArray(Arrays.copyOfRange(bytes, offset, offset + length));
    }

    @Override
    public ChannelBuffer toChannelBuffer() {
        return ChannelBuffers.wrappedBuffer(bytes, offset, length);
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        return bytes;
    }

    @Override
    public int arrayOffset() {
        return offset;
    }

    @Override
    public String toUtf8() {
        if (length == 0) {
            return "";
        }
        return new String(bytes, offset, length, Charsets.UTF_8);
    }

    @Override
    public BytesRef toBytesRef() {
        return new BytesRef(bytes, offset, length);
    }

    @Override
    public BytesRef copyBytesRef() {
        return new BytesRef(Arrays.copyOfRange(bytes, offset, offset + length));
    }

    @Override
    public int hashCode() {
        return Helper.bytesHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return Helper.bytesEqual(this, (BytesReference) obj);
    }
}