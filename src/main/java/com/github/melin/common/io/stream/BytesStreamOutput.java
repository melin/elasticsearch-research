/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.github.melin.common.io.stream;

import java.io.IOException;

import com.github.melin.common.ArrayUtil;
import com.github.melin.common.bytes.BytesArray;
import com.github.melin.common.bytes.BytesReference;
import com.github.melin.common.io.BytesStream;

/**
 * Create on @2013-12-18 @下午4:22:38
 * 
 * @author bsli@ustcinfo.com
 */
public class BytesStreamOutput extends StreamOutput implements BytesStream {

	public static final int DEFAULT_SIZE = 2 * 1024;

	public static final int OVERSIZE_LIMIT = 256 * 1024;

	/**
	 * The buffer where data is stored.
	 */
	protected byte buf[];

	/**
	 * The number of valid bytes in the buffer.
	 */
	protected int count;

	public BytesStreamOutput() {
		this(DEFAULT_SIZE);
	}

	public BytesStreamOutput(int size) {
		this.buf = new byte[size];
	}

	@Override
	public boolean seekPositionSupported() {
		return true;
	}

	@Override
	public long position() throws IOException {
		return count;
	}

	@Override
	public void seek(long position) throws IOException {
		if (position > Integer.MAX_VALUE) {
			throw new UnsupportedOperationException();
		}
		count = (int) position;
	}

	@Override
	public void writeByte(byte b) throws IOException {
		int newcount = count + 1;
		if (newcount > buf.length) {
			buf = grow(newcount);
		}
		buf[count] = b;
		count = newcount;
	}

	public void skip(int length) {
		int newcount = count + length;
		if (newcount > buf.length) {
			buf = grow(newcount);
		}
		count = newcount;
	}

	@Override
	public void writeBytes(byte[] b, int offset, int length) throws IOException {
		if (length == 0) {
			return;
		}
		int newcount = count + length;
		if (newcount > buf.length) {
			buf = grow(newcount);
		}
		System.arraycopy(b, offset, buf, count, length);
		count = newcount;
	}

	private byte[] grow(int newCount) {
		// try and grow faster while we are small...
		if (newCount < OVERSIZE_LIMIT) {
			newCount = Math.max(buf.length << 1, newCount);
		}
		return ArrayUtil.grow(buf, newCount);
	}

	public void seek(int seekTo) {
		count = seekTo;
	}

	public void reset() {
		count = 0;
	}

	public int bufferSize() {
		return buf.length;
	}

	@Override
	public void flush() throws IOException {
		// nothing to do there
	}

	@Override
	public void close() throws IOException {
		// nothing to do here
	}

	@Override
	public BytesReference bytes() {
		return new BytesArray(buf, 0, count);
	}

	/**
	 * Returns the current size of the buffer.
	 * 
	 * @return the value of the <code>count</code> field, which is the number of
	 *         valid bytes in this output stream.
	 * @see java.io.ByteArrayOutputStream#count
	 */
	public int size() {
		return count;
	}
}
