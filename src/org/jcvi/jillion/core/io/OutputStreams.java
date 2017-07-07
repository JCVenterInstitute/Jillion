/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Helper method to create {@link OutputStreams}
 * of various formats.  Most of these returned outputStreams
 * are buffered.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
public final class OutputStreams {

    private static final int _8K = 8192;
    
    private OutputStreams(){
        //can't instantiate
    }
    /**
     * Create a GZIPOutputStream for the given file
     * using a buffersize of 8K.  This is larger than the normal
     * default buffersize of GZIPOutputStream which is only 512 bytes!
     * So most of the time this larger buffer should provide better write performance.
     * 
     * @param out the file to write.
     * @return a new GZIPOutputStream; will never be null.
     * @throws IOException if there is a problem creating the outputstream.
     * 
     * @see #gzip(File, int)
     */
    public static GZIPOutputStream gzip(File out) throws IOException{
        return gzip(out, _8K);
    }
    /**
     * Create a GZIPOutputStream for the given file
     * using the given buffersize.
     * 
     * @param out the file to write.
     * @param bufferSize the size of the buffer to use.
     * 
     * @return a new GZIPOutputStream; will never be null.
     * @throws IOException if there is a problem creating the outputstream.
     * @throws IllegalArgumentException if bufferSize is less than 1.
     */
    public static GZIPOutputStream gzip(File out, int bufferSize) throws IOException{
        return new GZIPOutputStream(new FileOutputStream(out), bufferSize);
    }
    
    /**
     * Create a ZipOutputStream for the given file
     * using the given buffersize.
     * 
     * @param out the file to write.
     * @param bufferSize the size of the buffer to use.
     * 
     * @return a new ZipOutputStream; will never be null.
     * @throws IOException if there is a problem creating the outputstream.
     * @throws IllegalArgumentException if bufferSize is less than 1.
     */
    public static ZipOutputStream zip(File out, int bufferSize) throws IOException{
        return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(out), bufferSize));
    }
    /**
     * Create a ZipOutputStream for the given file
     * using the default buffersize.
     * 
     * @param out the file to write.
     * 
     * @return a new ZipOutputStream; will never be null.
     * @throws IOException if there is a problem creating the outputstream.
     */
    public static ZipOutputStream zip(File out) throws IOException{
        return new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(out)));
    }
    /**
     * Create a new FileOutputStream.  This is the same
     * as {@code new FileOutputStream(out);}.  
     * 
     * @param out the file to write to.
     * @return a new OutputStream; Warning: the returned outputstream is not buffered.
     * 
     * @throws IOException if there is a problem creating the outputStream.
     */
    public static OutputStream raw(File out) throws IOException{
        return new FileOutputStream(out);
    }
    /**
     * Create a new BufferedOutputStream for the given file
     * using the default buffersize.
     * 
     * @param out the file to write.
     * 
     * @return a new BufferedOutputStream; will never be null.
     * @throws IOException if there is a problem creating the outputstream.
     */
    public static OutputStream buffered(File out) throws IOException{
        return new BufferedOutputStream(new FileOutputStream(out));
    }
    /**
     * Create a new BufferedOutputStream for the given file
     * using the given buffersize.
     * 
     * @param out the file to write.
     * @param bufferSize the size of the buffer to use.
     * 
     * @return a new BufferedOutputStream; will never be null.
     * @throws IOException if there is a problem creating the outputstream.
     * @throws IllegalArgumentException if bufferSize is less than 1.
     */
    public static OutputStream buffered(File out, int bufferSize) throws IOException{
        return new BufferedOutputStream(new FileOutputStream(out), bufferSize);
    }
    
}
