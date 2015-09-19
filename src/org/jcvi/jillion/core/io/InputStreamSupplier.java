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
package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import org.jcvi.jillion.internal.core.io.MagicNumberInputStream;

/**
 * A Supplier function that can create multiple
 * new {@link InputStream}s from the same source file.
 * Different implementations may be able to handle
 * different file encodings or compressions.
 * 
 * @author dkatzel
 * 
 * @since 5.0
 *
 */
@FunctionalInterface
public interface InputStreamSupplier {
    /**
     * Create a new {@link InputStream} that starts
     * at the beginning of the file.
     * 
     * @return a new {@link InputStream}; should
     *          never be null but might not have any bytes to read.
     *          
     * @throws IOException if there is a problem creating the {@link InputStream}.
     */
    InputStream get() throws IOException;
    /**
     * Create a new {@link InputStream} that starts 
     * at the specified byte start offset.
     * 
     * @param startOffset the number of <strong>uncompressed</strong>
     *          bytes to skip over before returning the InputStream.
     * @return a new {@link InputStream}; should
     *          never be null but might not have any bytes to read.
     *          
     * @throws IOException if there is a problem creating the {@link InputStream}
     *                   or skipping over the desired number of bytes.
     *          
     * @implNote the default implementation creates a new {@link InputStream}
     *                 via {@link #get()} and then skips over {@code startOffset}
     *                 number of bytes. Implementations should override this method
     *                 if they are able to more efficiently start in the middle of an {@link InputStream}.
     */
    default InputStream get(long startOffset) throws IOException{
        InputStream in = get();
        IOUtil.blockingSkip(in, startOffset);
        return in;
    }
    
    /**
     * Create a new {@link InputStreamSupplier} for the given {@link File}
     * and try to correctly automatically decompress it.
     * 
     * The first few bytes of the given file are parsed to see if 
     * it is one of the few compressed file formats that have
     * built-in JDK InputStream implementations.
     * 
     * Currently the only supported formats are:
     * <ul>
     * <li>uncompressed</li>
     * <li>zip - single entry only</li>
     * <li>gzip</li>
     * </ul>
     * 
     * If the file is not one of these types, then it is assumed
     * to be uncompressed and an {@link InputStreamSupplier}
     * implementation based on {@link FileInputStream}
     * will be returned.
     * 
     * File encoding is determined by the actual contents
     * of the file.  The file name is not examined at all
     * so input files may use any file name extension conventions without
     * worrying about this method misinterpreting.
     * 
     * 
     * @param f the {@link File} object to create an {@link InputStreamSupplier} for;
     * can not be null, must exist,must be readable and should continue to exist for the lifetime
     * of this {@link InputStreamSupplier}.
     * 
     * @return a new {@link InputStreamSupplier}; will never be null.
     * @throws IOException if there is a problem reading this file.
     * @throws NullPointerException if f is null.
     */
    public static InputStreamSupplier forFile(File f) throws IOException{
       IOUtil.verifyIsReadable(f);
       
       byte[] magicNumber;
       try(MagicNumberInputStream magicNumInputStream = new MagicNumberInputStream(f)){
           magicNumber= magicNumInputStream.peekMagicNumber();
       }
       
       if (magicNumber[0] == (byte)0x50 && magicNumber[1] == (byte)0x4B && magicNumber[2] == (byte)0x03 && magicNumber[3]== (byte) 0x04){
           //zipped
           return ()-> {
               ZipInputStream in =new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
               //assume first record is the entry we care about?
               in.getNextEntry();
               return in;
           };
       }
       if( magicNumber[0] == (byte) 0x1F && magicNumber[1] == (byte)0x8B){
           //gzip
           return ()-> new GZIPInputStream(new BufferedInputStream(new FileInputStream(f)));
       }
       
        return new RawFileInputStreamSupplier(f);
    }
}
