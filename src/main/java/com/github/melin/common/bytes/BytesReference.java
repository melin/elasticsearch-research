/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.bytes;

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.netty.buffer.ChannelBuffer;

import com.github.melin.common.BytesRef;
import com.github.melin.common.io.stream.StreamInput;

/**
 * Create on @2013-12-18 @下午4:02:47 
 * @author bsli@ustcinfo.com
 */
public interface BytesReference {

    public static class Helper {

        public static boolean bytesEqual(BytesReference a, BytesReference b) {
            if (a == b) {
                return true;
            }
            if (a.length() != b.length()) {
                return false;
            }
            if (!a.hasArray()) {
                a = a.toBytesArray();
            }
            if (!b.hasArray()) {
                b = b.toBytesArray();
            }
            int bUpTo = b.arrayOffset();
            final byte[] aArray = a.array();
            final byte[] bArray = b.array();
            final int end = a.arrayOffset() + a.length();
            for (int aUpTo = a.arrayOffset(); aUpTo < end; aUpTo++, bUpTo++) {
                if (aArray[aUpTo] != bArray[bUpTo]) {
                    return false;
                }
            }
            return true;
        }

        public static int bytesHashCode(BytesReference a) {
            if (!a.hasArray()) {
                a = a.toBytesArray();
            }
            int result = 0;
            final int end = a.arrayOffset() + a.length();
            for (int i = a.arrayOffset(); i < end; i++) {
                result = 31 * result + a.array()[i];
            }
            return result;
        }
    }

    /**
     * Returns the byte at the specified index. Need to be between 0 and length.
     */
    byte get(int index);

    /**
     * The length.
     */
    int length();

    /**
     * Slice the bytes from the <tt>from</tt> index up to <tt>length</tt>.
     */
    BytesReference slice(int from, int length);

    /**
     * A stream input of the bytes.
     */
    StreamInput streamInput();

    /**
     * Writes the bytes directly to the output stream.
     */
    void writeTo(OutputStream os) throws IOException;

    /**
     * Returns the bytes as a single byte array.
     */
    byte[] toBytes();

    /**
     * Returns the bytes as a byte array, possibly sharing the underlying byte buffer.
     */
    BytesArray toBytesArray();

    /**
     * Returns the bytes copied over as a byte array.
     */
    BytesArray copyBytesArray();

    /**
     * Returns the bytes as a channel buffer.
     */
    ChannelBuffer toChannelBuffer();

    /**
     * Is there an underlying byte array for this bytes reference.
     */
    boolean hasArray();

    /**
     * The underlying byte array (if exists).
     */
    byte[] array();

    /**
     * The offset into the underlying byte array.
     */
    int arrayOffset();

    /**
     * Converts to a string based on utf8.
     */
    String toUtf8();

    /**
     * Converts to Lucene BytesRef.
     */
    BytesRef toBytesRef();

    /**
     * Converts to a copied Lucene BytesRef.
     */
    BytesRef copyBytesRef();
}