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
import java.util.Optional;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.streams.ThrowingSupplier;

import lombok.Builder;
import lombok.Data;

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
public interface InputStreamSupplier extends ThrowingSupplier<InputStream, IOException>{
	@Data
	@Builder
	public static class InputStreamReadOptions{
		private Long start;
		private Long length;
		private boolean nestedDecompress;
		
		public static class InputStreamReadOptionsBuilder{
			
			public InputStreamReadOptionsBuilder start(Long start) {
				this.start = start;
				
				return this;
			}
			public InputStreamReadOptionsBuilder start(long start) {
				this.start = start;
				
				return this;
			}
			
			public InputStreamReadOptionsBuilder length(Long length) {
				this.length = length;
				
				return this;
			}
			public InputStreamReadOptionsBuilder length(long length) {
				this.length = length;
				
				return this;
			}
		
			public InputStreamReadOptionsBuilder range(Range r) {
				start(r.getBegin());
				length(r.getLength());
				return this;
			}
		}
	}
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
     * at the beginning of the file.
     * 
     * @return a new {@link InputStream}; should
     *          never be null but might not have any bytes to read.
     *          
     * @throws IOException if there is a problem creating the {@link InputStream}.
     */
    default InputStream get(InputStreamReadOptions readOptions) throws IOException{
    	InputStream in = get(readOptions.nestedDecompress);
    	
    	if(readOptions.getStart() !=null && readOptions.getStart().longValue() >0L) {
    		IOUtil.blockingSkip(in, readOptions.getStart());
    	}
    	if(readOptions.getLength() !=null) {
    		return new SubLengthInputStream(in,readOptions.getLength());
    	}
    	return in;
    }
    /**
     * Create a new {@link InputStream} that starts at the beginning
     * of the file and returns the uncompressed bytes of perhaps nested compressed data.
     * For example, if the file was {@code tar.gz} then it would ungunzip and then untar
     * that result and send you the untarred resulting stream.
     * 
     * @param uncompressNestedStream
     * @return
     * @throws IOException
     */
    default InputStream get(boolean uncompressNestedStream) throws IOException{
    	InputStream in = get();
    	if(uncompressNestedStream) {
    		return InputStreamSupplierRegistery.getInstance().decodeInputStream(in);
    	}
    	return in;
    	
    }
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
        
        return get(InputStreamReadOptions.builder().start(startOffset).build());
    }
    
    /**
     * Create a new {@link InputStream} that starts 
     * at the specified byte start offset.
     * 
     * @param range the {@link Range} of <strong>uncompressed</strong>
     *          bytes to read in this file/
     *          .
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
    default InputStream get(Range range) throws IOException{
    	 return get(InputStreamReadOptions.builder().range(range).build());
    }
    /**
     * Can we reread this inputStream by
     * calling get() multiple times.
     * 
     * @return {@code true} if this is re-readable;
     * {@code false} otherwise.
     * 
     * @since 6.0
     * 
     * @implNote the default implementation does a check
     * to see if this supplier is backed by a file or not.
     */
    default boolean isReReadable() {
    	return getFile().isPresent();
    }
    
    /**
     * Get the {@link File} object
     * that is the source of this inputStream.
     * 
     * @return an {@link Optional} File that may be empty
     * if the file source is not known.
     * 
     * @since 5.2
     */
    default Optional<File> getFile(){
        return Optional.empty();
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
     * <li>xc</li>
     * <li>tar - single entry only</li>
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
    	return InputStreamSupplierRegistery.getInstance().createInputStreamSupplierFor(f);
    }
}
