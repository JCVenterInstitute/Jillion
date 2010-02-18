/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
