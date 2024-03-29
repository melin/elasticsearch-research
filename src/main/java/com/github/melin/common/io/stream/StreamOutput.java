/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.ReadableInstant;

import com.github.melin.common.BytesRef;
import com.github.melin.common.bytes.BytesReference;
import com.github.melin.common.io.UTF8StreamWriter;
import com.github.melin.common.text.Text;

/**
 * Create on @2013-12-18 @下午4:19:53 
 * @author bsli@ustcinfo.com
 */
public abstract class StreamOutput extends OutputStream {

    private static ThreadLocal<SoftReference<UTF8StreamWriter>> utf8StreamWriter = new ThreadLocal<SoftReference<UTF8StreamWriter>>();

    public static UTF8StreamWriter utf8StreamWriter() {
        SoftReference<UTF8StreamWriter> ref = utf8StreamWriter.get();
        UTF8StreamWriter writer = (ref == null) ? null : ref.get();
        if (writer == null) {
            writer = new UTF8StreamWriter(1024 * 4);
            utf8StreamWriter.set(new SoftReference<UTF8StreamWriter>(writer));
        }
        writer.reset();
        return writer;
    }

    public boolean seekPositionSupported() {
        return false;
    }

    public long position() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void seek(long position) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Writes a single byte.
     */
    public abstract void writeByte(byte b) throws IOException;

    /**
     * Writes an array of bytes.
     *
     * @param b the bytes to write
     */
    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    /**
     * Writes an array of bytes.
     *
     * @param b      the bytes to write
     * @param length the number of bytes to write
     */
    public void writeBytes(byte[] b, int length) throws IOException {
        writeBytes(b, 0, length);
    }

    /**
     * Writes an array of bytes.
     *
     * @param b      the bytes to write
     * @param offset the offset in the byte array
     * @param length the number of bytes to write
     */
    public abstract void writeBytes(byte[] b, int offset, int length) throws IOException;

    /**
     * Writes the bytes reference, including a length header.
     */
    public void writeBytesReference(BytesReference bytes) throws IOException {
        if (bytes == null) {
            writeVInt(0);
            return;
        }
        writeVInt(bytes.length());
        bytes.writeTo(this);
    }

    public void writeBytesRef(BytesRef bytes) throws IOException {
        if (bytes == null) {
            writeVInt(0);
            return;
        }
        writeVInt(bytes.length);
        write(bytes.bytes, bytes.offset, bytes.length);
    }

    public final void writeShort(short v) throws IOException {
        writeByte((byte) (v >> 8));
        writeByte((byte) v);
    }

    /**
     * Writes an int as four bytes.
     */
    public void writeInt(int i) throws IOException {
        writeByte((byte) (i >> 24));
        writeByte((byte) (i >> 16));
        writeByte((byte) (i >> 8));
        writeByte((byte) i);
    }

    /**
     * Writes an int in a variable-length format.  Writes between one and
     * five bytes.  Smaller values take fewer bytes.  Negative numbers
     * will always use all 5 bytes and are therefore better serialized
     * using {@link #writeInt}
     */
    public void writeVInt(int i) throws IOException {
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) i);
    }

    /**
     * Writes a long as eight bytes.
     */
    public void writeLong(long i) throws IOException {
        writeInt((int) (i >> 32));
        writeInt((int) i);
    }

    /**
     * Writes an long in a variable-length format.  Writes between one and nine
     * bytes.  Smaller values take fewer bytes.  Negative numbers are not
     * supported.
     */
    public void writeVLong(long i) throws IOException {
        assert i >= 0;
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7f) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) i);
    }

    public void writeOptionalString(String str) throws IOException {
        if (str == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeString(str);
        }
    }

    public void writeOptionalSharedString(String str) throws IOException {
        if (str == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeSharedString(str);
        }
    }

    public void writeOptionalText(Text text) throws IOException {
        if (text == null) {
            writeInt(-1);
        } else {
            writeText(text);
        }
    }

    public void writeText(Text text) throws IOException {
        if (!text.hasBytes() && seekPositionSupported()) {
            long pos1 = position();
            // make room for the size
            seek(pos1 + 4);
            UTF8StreamWriter utf8StreamWriter = utf8StreamWriter();
            utf8StreamWriter.setOutput(this);
            utf8StreamWriter.write(text.string());
            utf8StreamWriter.close();
            long pos2 = position();
            seek(pos1);
            writeInt((int) (pos2 - pos1 - 4));
            seek(pos2);
        } else {
            BytesReference bytes = text.bytes();
            writeInt(bytes.length());
            bytes.writeTo(this);
        }
    }

    public void writeTextArray(Text[] array) throws IOException {
        writeVInt(array.length);
        for (Text t : array) {
            writeText(t);
        }
    }

    public void writeSharedText(Text text) throws IOException {
        writeText(text);
    }

    public void writeString(String str) throws IOException {
        int charCount = str.length();
        writeVInt(charCount);
        int c;
        for (int i = 0; i < charCount; i++) {
            c = str.charAt(i);
            if (c <= 0x007F) {
                writeByte((byte) c);
            } else if (c > 0x07FF) {
                writeByte((byte) (0xE0 | c >> 12 & 0x0F));
                writeByte((byte) (0x80 | c >> 6 & 0x3F));
                writeByte((byte) (0x80 | c >> 0 & 0x3F));
            } else {
                writeByte((byte) (0xC0 | c >> 6 & 0x1F));
                writeByte((byte) (0x80 | c >> 0 & 0x3F));
            }
        }
    }

    public void writeSharedString(String str) throws IOException {
        writeString(str);
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }


    private static byte ZERO = 0;
    private static byte ONE = 1;
    private static byte TWO = 2;

    /**
     * Writes a boolean.
     */
    public void writeBoolean(boolean b) throws IOException {
        writeByte(b ? ONE : ZERO);
    }

    public void writeOptionalBoolean(Boolean b) throws IOException {
        if (b == null) {
            writeByte(TWO);
        } else {
            writeByte(b ? ONE : ZERO);
        }
    }

    /**
     * Forces any buffered output to be written.
     */
    public abstract void flush() throws IOException;

    /**
     * Closes this stream to further operations.
     */
    public abstract void close() throws IOException;

    public abstract void reset() throws IOException;

    @Override
    public void write(int b) throws IOException {
        writeByte((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeBytes(b, off, len);
    }

    public void writeStringArray(String[] array) throws IOException {
        writeVInt(array.length);
        for (String s : array) {
            writeString(s);
        }
    }

    /**
     * Writes a string array, for nullable string, writes it as 0 (empty string).
     */
    public void writeStringArrayNullable(String[] array) throws IOException {
        if (array == null) {
            writeVInt(0);
        } else {
            writeVInt(array.length);
            for (String s : array) {
                writeString(s);
            }
        }
    }

    public void writeMap(Map<String, Object> map) throws IOException {
        writeGenericValue(map);
    }

    public void writeGenericValue(Object value) throws IOException {
        if (value == null) {
            writeByte((byte) -1);
            return;
        }
        Class type = value.getClass();
        if (type == String.class) {
            writeByte((byte) 0);
            writeString((String) value);
        } else if (type == Integer.class) {
            writeByte((byte) 1);
            writeInt((Integer) value);
        } else if (type == Long.class) {
            writeByte((byte) 2);
            writeLong((Long) value);
        } else if (type == Float.class) {
            writeByte((byte) 3);
            writeFloat((Float) value);
        } else if (type == Double.class) {
            writeByte((byte) 4);
            writeDouble((Double) value);
        } else if (type == Boolean.class) {
            writeByte((byte) 5);
            writeBoolean((Boolean) value);
        } else if (type == byte[].class) {
            writeByte((byte) 6);
            writeVInt(((byte[]) value).length);
            writeBytes(((byte[]) value));
        } else if (value instanceof List) {
            writeByte((byte) 7);
            List list = (List) value;
            writeVInt(list.size());
            for (Object o : list) {
                writeGenericValue(o);
            }
        } else if (value instanceof Object[]) {
            writeByte((byte) 8);
            Object[] list = (Object[]) value;
            writeVInt(list.length);
            for (Object o : list) {
                writeGenericValue(o);
            }
        } else if (value instanceof Map) {
            if (value instanceof LinkedHashMap) {
                writeByte((byte) 9);
            } else {
                writeByte((byte) 10);
            }
            Map<String, Object> map = (Map<String, Object>) value;
            writeVInt(map.size());
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                writeSharedString(entry.getKey());
                writeGenericValue(entry.getValue());
            }
        } else if (type == Byte.class) {
            writeByte((byte) 11);
            writeByte((Byte) value);
        } else if (type == Date.class) {
            writeByte((byte) 12);
            writeLong(((Date) value).getTime());
        } else if (value instanceof ReadableInstant) {
            writeByte((byte) 13);
            writeLong(((ReadableInstant) value).getMillis());
        } else if (value instanceof BytesReference) {
            writeByte((byte) 14);
            writeBytesReference((BytesReference) value);
        } else if (value instanceof Text) {
            writeByte((byte) 15);
            writeText((Text) value);
        } else if (type == Short.class) {
            writeByte((byte) 16);
            writeShort((Short) value);
        } else if (type == int[].class) {
            writeByte((byte) 17);
            writePrimitiveIntArray((int[]) value);
        } else if (type == long[].class) {
            writeByte((byte) 18);
            writePrimitiveLongArray((long[]) value);
        } else if (type == float[].class) {
            writeByte((byte) 19);
            writePrimitiveFloatArray((float[]) value);
        } else if (type == double[].class) {
            writeByte((byte) 20);
            writePrimitiveDoubleArray((double[]) value);
        } else {
            throw new IOException("Can't write type [" + type + "]");
        }
    }

    private void writePrimitiveIntArray(int[] value) throws IOException {
        writeVInt(value.length);
        for (int i=0; i<value.length; i++) {
            writeInt(value[i]);
        }
    }
    
    private void writePrimitiveLongArray(long[] value) throws IOException {
        writeVInt(value.length);
        for (int i=0; i<value.length; i++) {
            writeLong(value[i]);
        }
    }
    
    private void writePrimitiveFloatArray(float[] value) throws IOException {
        writeVInt(value.length);
        for (int i=0; i<value.length; i++) {
            writeFloat(value[i]);
        }
    }
    
    private void writePrimitiveDoubleArray(double[] value) throws IOException {
        writeVInt(value.length);
        for (int i=0; i<value.length; i++) {
            writeDouble(value[i]);
        }
    }

    /**
     * Serializes a potential null value.
     */
    public void writeOptionalStreamable(Streamable streamable) throws IOException {
        if (streamable != null) {
            writeBoolean(true);
            streamable.writeTo(this);
        } else {
            writeBoolean(false);
        }
    }
}