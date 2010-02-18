/*
 * Created on Oct 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;


import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamReader extends AbstractStreamReader{
    private final OutputStream outputStream ;
    
    /**
     * 
     */
    public OutputStreamReader(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * @param bufferSize
     * @param closeStream
     */
    public OutputStreamReader(OutputStream outputStream, int bufferSize, boolean closeStream) {
        super(bufferSize, closeStream);
        this.outputStream = outputStream;
    }

    @Override
    protected void handleBytes(byte[] bytes, int offset, int numberOfBytesRead) throws IOException {
        outputStream.write(bytes,offset, numberOfBytesRead);        
    }

    
    
}
