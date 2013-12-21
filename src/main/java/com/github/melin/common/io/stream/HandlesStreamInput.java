/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.IOException;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.github.melin.common.text.Text;

/**
 * Create on @2013-12-18 @下午6:11:13 
 * @author bsli@ustcinfo.com
 */
public class HandlesStreamInput extends AdapterStreamInput {

    private final IntObjectOpenHashMap<String> handles = new IntObjectOpenHashMap<String>();
    private final IntObjectOpenHashMap<Text> handlesText = new IntObjectOpenHashMap<Text>();

    HandlesStreamInput() {
        super();
    }

    public HandlesStreamInput(StreamInput in) {
        super(in);
    }

    @Override
    public String readSharedString() throws IOException {
        byte b = in.readByte();
        if (b == 0) {
            // full string with handle
            int handle = in.readVInt();
            String s = in.readString();
            handles.put(handle, s);
            return s;
        } else if (b == 1) {
            return handles.get(in.readVInt());
        } else {
            throw new IOException("Expected handle header, got [" + b + "]");
        }
    }

    @Override
    public String readString() throws IOException {
        return in.readString();
    }

    @Override
    public Text readSharedText() throws IOException {
        byte b = in.readByte();
        if (b == 0) {
            int handle = in.readVInt();
            Text s = in.readText();
            handlesText.put(handle, s);
            return s;
        } else if (b == 1) {
            return handlesText.get(in.readVInt());
        } else if (b == 2) {
            return in.readText();
        } else {
            throw new IOException("Expected handle header, got [" + b + "]");
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        cleanHandles();
    }

    public void reset(StreamInput in) {
        super.reset(in);
        cleanHandles();
    }

    public void cleanHandles() {
        handles.clear();
        handlesText.clear();
    }
}