/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.github.melin.common.io;

import com.google.common.base.Charsets;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static com.github.melin.common.io.Streams.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for {@link org.elasticsearch.common.io.Streams}.
 */
public class StreamsTests {

    @Test
    public void testCopyFromInputStream() throws IOException {
        byte[] content = "content".getBytes(Charsets.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(content);
        ByteArrayOutputStream out = new ByteArrayOutputStream(content.length);
        long count = copy(in, out);

        assertThat(count, equalTo((long) content.length));
        assertThat(Arrays.equals(content, out.toByteArray()), equalTo(true));
    }

    @Test
    public void testCopyFromByteArray() throws IOException {
        byte[] content = "content".getBytes(Charsets.UTF_8);
        ByteArrayOutputStream out = new ByteArrayOutputStream(content.length);
        copy(content, out);
        assertThat(Arrays.equals(content, out.toByteArray()), equalTo(true));
    }

    @Test
    public void testCopyToByteArray() throws IOException {
        byte[] content = "content".getBytes(Charsets.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(content);
        byte[] result = copyToByteArray(in);
        assertThat(Arrays.equals(content, result), equalTo(true));
    }

    @Test
    public void testCopyFromReader() throws IOException {
        String content = "content";
        StringReader in = new StringReader(content);
        StringWriter out = new StringWriter();
        int count = copy(in, out);
        assertThat(content.length(), equalTo(count));
        assertThat(out.toString(), equalTo(content));
    }

    @Test
    public void testCopyFromString() throws IOException {
        String content = "content";
        StringWriter out = new StringWriter();
        copy(content, out);
        assertThat(out.toString(), equalTo(content));
    }

    @Test
    public void testCopyToString() throws IOException {
        String content = "content";
        StringReader in = new StringReader(content);
        String result = copyToString(in);
        assertThat(result, equalTo(content));
    }

}