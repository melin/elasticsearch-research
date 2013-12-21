/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.text;

import com.github.melin.common.bytes.BytesArray;
import com.github.melin.common.bytes.BytesReference;
import com.google.common.base.Charsets;

/**
 * Both {@link String} and {@link BytesReference} representation of the text. Starts with one of those, and if
 * the other is requests, caches the other one in a local reference so no additional conversion will be needed.
 */
public class StringAndBytesText implements Text {

    public static final Text[] EMPTY_ARRAY = new Text[0];

    public static Text[] convertFromStringArray(String[] strings) {
        if (strings.length == 0) {
            return EMPTY_ARRAY;
        }
        Text[] texts = new Text[strings.length];
        for (int i = 0; i < strings.length; i++) {
            texts[i] = new StringAndBytesText(strings[i]);
        }
        return texts;
    }

    private BytesReference bytes;
    private String text;
    private int hash;

    public StringAndBytesText(BytesReference bytes) {
        this.bytes = bytes;
    }

    public StringAndBytesText(String text) {
        this.text = text;
    }

    @Override
    public boolean hasBytes() {
        return bytes != null;
    }

    @Override
    public BytesReference bytes() {
        if (bytes == null) {
            bytes = new BytesArray(text.getBytes(Charsets.UTF_8));
        }
        return bytes;
    }

    @Override
    public boolean hasString() {
        return text != null;
    }

    @Override
    public String string() {
        // TODO: we can optimize the conversion based on the bytes reference API similar to UnicodeUtil
        if (text == null) {
            if (!bytes.hasArray()) {
                bytes = bytes.toBytesArray();
            }
            text = new String(bytes.array(), bytes.arrayOffset(), bytes.length(), Charsets.UTF_8);
        }
        return text;
    }

    @Override
    public String toString() {
        return string();
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = bytes().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return bytes().equals(((Text) obj).bytes());
    }

    @Override
    public int compareTo(Text text) {
        return UTF8SortedAsUnicodeComparator.utf8SortedAsUnicodeSortOrder.compare(bytes(), text.bytes());
    }
}