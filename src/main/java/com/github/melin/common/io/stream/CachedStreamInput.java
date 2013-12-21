/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io.stream;

import java.io.IOException;
import java.lang.ref.SoftReference;

import ch.qos.logback.core.rolling.helper.Compressor;

/**
 * Create on @2013-12-20 @下午5:11:36 
 * @author bsli@ustcinfo.com
 */
public class CachedStreamInput {

    static class Entry {
        final HandlesStreamInput handles;

        Entry(HandlesStreamInput handles) {
            this.handles = handles;
        }
    }

    private static final ThreadLocal<SoftReference<Entry>> cache = new ThreadLocal<SoftReference<Entry>>();

    static Entry instance() {
        SoftReference<Entry> ref = cache.get();
        Entry entry = ref == null ? null : ref.get();
        if (entry == null) {
            HandlesStreamInput handles = new HandlesStreamInput();
            entry = new Entry(handles);
            cache.set(new SoftReference<Entry>(entry));
        }
        return entry;
    }

    public static void clear() {
        cache.remove();
    }

    public static HandlesStreamInput cachedHandles(StreamInput in) {
        HandlesStreamInput handles = instance().handles;
        handles.reset(in);
        return handles;
    }
}