/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;

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
public final class MagicNumberInputStream extends InputStream{

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
    /**
     * Convenience constructor which defaults to a magic number
     * size of 4 bytes. 
     * @param file the File to read as an InputStream.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     * @see #MagicNumberInputStream(InputStream, int)
     */
    public MagicNumberInputStream(File file) throws IOException{
        this(new BufferedInputStream(new FileInputStream(file)),4);
    }
    /**
     * Convenience constructor which defaults to a magic number
     * size of 4 bytes. 
     * @param file the File to read as an InputStream.
     * @param sizeOfMagicNumber the number of bytes the magic number
     * should be.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     * 
     * @see #MagicNumberInputStream(InputStream, int)
     * 
     * @since 6.0
     */
    public MagicNumberInputStream(File file, int sizeOfMagicNumber) throws IOException{
        this(new BufferedInputStream(new FileInputStream(file)),sizeOfMagicNumber);
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
        magicNumber =IOUtil.toByteArray(in, sizeOfMagicNumber);
        this.in = in;
    }
    /**
     * Gets the entire magic number without advancing the
     * {@link InputStream}.  If the stream has already read past
     * the magic number, it is cached for later retrieval.
     * @return the magic number of this inputStream as a byte array.
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
