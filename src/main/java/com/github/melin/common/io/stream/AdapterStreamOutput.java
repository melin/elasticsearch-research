/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.IOException;

import com.github.melin.common.bytes.BytesReference;
import com.github.melin.common.text.Text;

/**
 * Create on @2013-12-18 @下午6:10:13 
 * @author bsli@ustcinfo.com
 */
public class AdapterStreamOutput extends StreamOutput {

    protected StreamOutput out;

    public AdapterStreamOutput(StreamOutput out) {
        this.out = out;
    }

    public void setOut(StreamOutput out) {
        this.out = out;
    }

    public StreamOutput wrappedOut() {
        return this.out;
    }

    @Override
    public boolean seekPositionSupported() {
        return out.seekPositionSupported();
    }

    @Override
    public long position() throws IOException {
        return out.position();
    }

    @Override
    public void seek(long position) throws IOException {
        out.seek(position);
    }

    @Override
    public void writeByte(byte b) throws IOException {
        out.writeByte(b);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        out.writeBytes(b, offset, length);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void reset() throws IOException {
        out.reset();
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        out.writeBytes(b);
    }

    @Override
    public void writeBytes(byte[] b, int length) throws IOException {
        out.writeBytes(b, length);
    }

    @Override
    public void writeBytesReference(BytesReference bytes) throws IOException {
        out.writeBytesReference(bytes);
    }

    @Override
    public void writeInt(int i) throws IOException {
        out.writeInt(i);
    }

    @Override
    public void writeVInt(int i) throws IOException {
        out.writeVInt(i);
    }

    @Override
    public void writeLong(long i) throws IOException {
        out.writeLong(i);
    }

    @Override
    public void writeVLong(long i) throws IOException {
        out.writeVLong(i);
    }

    @Override
    public void writeString(String str) throws IOException {
        out.writeString(str);
    }

    @Override
    public void writeSharedString(String str) throws IOException {
        out.writeSharedString(str);
    }

    @Override
    public void writeText(Text text) throws IOException {
        out.writeText(text);
    }

    @Override
    public void writeSharedText(Text text) throws IOException {
        out.writeSharedText(text);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        out.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        out.writeDouble(v);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        out.writeBoolean(b);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public String toString() {
        return out.toString();
    }
}