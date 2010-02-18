/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream{

    private final ByteBuffer buffer;
    
    /**
     * @param buffer
     */
    public ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public synchronized int read() throws IOException {        
        return buffer.get();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int actualLength = Math.min(len, buffer.remaining());
        buffer.get(b, off, actualLength);
        return actualLength;
    }
    

    
}
