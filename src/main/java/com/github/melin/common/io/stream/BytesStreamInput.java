/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.EOFException;
import java.io.IOException;

import com.github.melin.common.BytesRef;
import com.github.melin.common.bytes.BytesArray;
import com.github.melin.common.bytes.BytesReference;

/**
 * Create on @2013-12-18 @下午4:14:27 
 * @author bsli@ustcinfo.com
 */
public class BytesStreamInput extends StreamInput {

    protected byte buf[];

    protected int pos;

    protected int count;

    private final boolean unsafe;

    public BytesStreamInput(BytesReference bytes) {
        if (!bytes.hasArray()) {
            bytes = bytes.toBytesArray();
        }
        this.buf = bytes.array();
        this.pos = bytes.arrayOffset();
        this.count = bytes.length();
        this.unsafe = false;
    }

    public BytesStreamInput(byte buf[], boolean unsafe) {
        this(buf, 0, buf.length, unsafe);
    }

    public BytesStreamInput(byte buf[], int offset, int length, boolean unsafe) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
        this.unsafe = unsafe;
    }

    @Override
    public BytesReference readBytesReference(int length) throws IOException {
        if (unsafe) {
            return super.readBytesReference(length);
        }
        BytesArray bytes = new BytesArray(buf, pos, length);
        pos += length;
        return bytes;
    }

    @Override
    public BytesRef readBytesRef(int length) throws IOException {
        if (unsafe) {
            return super.readBytesRef(length);
        }
        BytesRef bytes = new BytesRef(buf, pos, length);
        pos += length;
        return bytes;
    }

    @Override
    public long skip(long n) throws IOException {
        if (pos + n > count) {
            n = count - pos;
        }
        if (n < 0) {
            return 0;
        }
        pos += n;
        return n;
    }

    public int position() {
        return this.pos;
    }

    @Override
    public int read() throws IOException {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (pos >= count) {
            return -1;
        }
        if (pos + len > count) {
            len = count - pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    public byte[] underlyingBuffer() {
        return buf;
    }

    @Override
    public byte readByte() throws IOException {
        if (pos >= count) {
            throw new EOFException();
        }
        return buf[pos++];
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        if (len == 0) {
            return;
        }
        if (pos >= count) {
            throw new EOFException();
        }
        if (pos + len > count) {
            len = count - pos;
        }
        if (len <= 0) {
            throw new EOFException();
        }
        System.arraycopy(buf, pos, b, offset, len);
        pos += len;
    }

    @Override
    public void reset() throws IOException {
        pos = 0;
    }

    @Override
    public void close() throws IOException {
        // nothing to do here...
    }
}
