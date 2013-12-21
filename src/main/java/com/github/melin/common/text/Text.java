/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.text;

import java.io.Serializable;

import com.github.melin.common.bytes.BytesReference;

/**
 * Text represents a (usually) long text data. We use this abstraction instead of {@link String}
 * so we can represent it in a more optimized manner in memory as well as serializing it over the
 * network as well as converting it to json format.
 */
public interface Text extends Comparable<Text>, Serializable {

    /**
     * Are bytes available without the need to be converted into bytes when calling {@link #bytes()}.
     */
    boolean hasBytes();

    /**
     * The UTF8 bytes representing the the text, might be converted on the fly, see {@link #hasBytes()}
     */
    BytesReference bytes();

    /**
     * Is there a {@link String} representation of the text. If not, then it {@link #hasBytes()}.
     */
    boolean hasString();

    /**
     * Returns the string representation of the text, might be converted to a string on the fly.
     */
    String string();

    /**
     * Returns the string representation of the text, might be converted to a string on the fly.
     */
    String toString();
}