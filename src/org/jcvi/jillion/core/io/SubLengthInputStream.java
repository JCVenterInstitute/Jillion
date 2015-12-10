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

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
/**
 * Wraps another {@link InputStream}
 * but only reads up to the specified number of bytes.
 * This will make it appear as if the InputStream only 
 * has that number of bytes.
 * 
 * @author dkatzel
 * 
 * @since 5.1
 *
 */
class SubLengthInputStream extends InputStream{

	

	private final InputStream in;
	
	private long bytesLeftToRead;
	
	/**
	 * Create a new instance that wraps the given inputStream and reads
	 * up to the specified number of bytes.
	 * @param in the InputStream to wrap; can no be null.
	 * 
	 * @param lengthToRead the number of bytes to read; if <0, then
	 * it will act as if the stream is empty.
	 * 
	 * @throws NullPointerException if in is null.
	 */
	public SubLengthInputStream(InputStream in, long lengthToRead) {
		Objects.requireNonNull(in);
		this.in = in;
		this.bytesLeftToRead = lengthToRead;
	}

	@Override
	public int read() throws IOException {
		if(bytesLeftToRead >0){
			bytesLeftToRead--;
			return in.read();
		}
		return -1;
	}

	

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(bytesLeftToRead <=0){
			return -1;
		}
		int bytesRead = in.read(b, off, (int) Math.min(bytesLeftToRead, len));
		if(bytesRead != -1){
			bytesLeftToRead -= bytesRead;
		}
		return bytesRead;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
	
	
}
