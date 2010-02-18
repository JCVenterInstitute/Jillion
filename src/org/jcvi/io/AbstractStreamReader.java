/*
 * Created on Oct 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.IOException;
import java.io.InputStream;
/**
 * {@code AbstractStreamReader} handles the boiler
 * plate code for reading data from an {@link InputStream}.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractStreamReader {

    private final int bufferSize;
    private final boolean closeStream;

    public AbstractStreamReader(){
        this(2048, true);
    }
    /**
     * @param bufferSize
     */
    public AbstractStreamReader(int bufferSize,boolean closeStream) {
        this.bufferSize = bufferSize;
        this.closeStream = closeStream;
    }
    
    public void read(InputStream in) throws IOException{
        int bytesRead;
        byte[] byteArray = new byte[bufferSize];
        try{
            while((bytesRead = in.read(byteArray)) >-1){
                handleBytes(byteArray,0, bytesRead);
            }
           
        }
        finally{
            if(closeStream){
                IOUtil.closeAndIgnoreErrors(in);
            }
        }
    }
    /**
     * Handle the given bytes in the byte array were
     *  just read from the inputStream.
     * @param bytes
     * @param offset
     * @param numberOfBytesRead
     * @throws IOException
     */
    protected abstract void handleBytes(byte[] bytes, int offset, int numberOfBytesRead) throws IOException;
}
