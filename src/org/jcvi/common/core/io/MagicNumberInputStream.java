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

package org.jcvi.common.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * {@code MagicNumberInputStream} is an
 * {@link InputStream} implementation
 * that reads ahead and saves the magic number
 * so clients can determine a course of
 * action by peeking at the magic number
 * before they have to read any bytes 
 * from the inputStream. 
 * 
 * @author dkatzel
 *
 *
 */
public class MagicNumberInputStream extends InputStream{

    private final InputStream in;
    private final byte[] magicNumber;
    private int numberOfBytesRead=0;
    /**
     * Convenience constructor which defaults to a magic number
     * size of 4 bytes.  This is the same as
     * {@link #MagicNumberInputStream(InputStream, int) new MagicNumberInputStream(in,4)}.
     * @param in the InputStream to wrap.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     * @see #MagicNumberInputStream(InputStream, int)
     */
    public MagicNumberInputStream(InputStream in) throws IOException{
        this(in,4);
    }
    
    public MagicNumberInputStream(File file) throws IOException{
        this(new FileInputStream(file),4);
    }
    /**
     * Wraps the given {@link InputStream} and reads the 
     * first {@code sizeOfMagicNumber} bytes as the magic number. 
     * Will block until the entire magic number has been read.
     * @param in the InputStream to wrap.
     * @param sizeOfMagicNumber the number of bytes the magic number
     * should be.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     */
    public MagicNumberInputStream(InputStream in,int sizeOfMagicNumber) throws IOException {
        magicNumber =IOUtil.readByteArray(in, sizeOfMagicNumber);
        this.in = in;
    }
    /**
     * Gets the entire magic number without advancing the
     * {@link InputStream}.
     * @return
     */
    public byte[] peekMagicNumber(){
        return Arrays.copyOf(magicNumber, magicNumber.length);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized int read() throws IOException {
        if(numberOfBytesRead< magicNumber.length){            
            return magicNumber[numberOfBytesRead++];
        }
        return in.read();
    }

}
