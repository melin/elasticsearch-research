/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.IOException;

import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.github.melin.common.text.Text;

/**
 * Create on @2013-12-18 @下午6:09:42 
 * @author bsli@ustcinfo.com
 */
public class HandlesStreamOutput extends AdapterStreamOutput {

    private final ObjectIntOpenHashMap<String> handles = new ObjectIntOpenHashMap<String>();
    private final ObjectIntOpenHashMap<Text> handlesText = new ObjectIntOpenHashMap<Text>();

    public HandlesStreamOutput(StreamOutput out) {
        super(out);
    }

    @Override
    public void writeSharedString(String str) throws IOException {
        if (handles.containsKey(str)) {
            out.writeByte((byte) 1);
            out.writeVInt(handles.lget());
        } else {
            int handle = handles.size();
            handles.put(str, handle);
            out.writeByte((byte) 0);
            out.writeVInt(handle);
            out.writeString(str);
        }
    }

    @Override
    public void writeString(String s) throws IOException {
        out.writeString(s);
    }

    @Override
    public void writeSharedText(Text text) throws IOException {
        if (handlesText.containsKey(text)) {
            out.writeByte((byte) 1);
            out.writeVInt(handlesText.lget());
        } else {
            int handle = handlesText.size();
            handlesText.put(text, handle);
            out.writeByte((byte) 0);
            out.writeVInt(handle);
            out.writeText(text);
        }
    }

    @Override
    public void reset() throws IOException {
        clear();
        if (out != null) {
            out.reset();
        }
    }

    public void clear() {
        handles.clear();
        handlesText.clear();
    }
}