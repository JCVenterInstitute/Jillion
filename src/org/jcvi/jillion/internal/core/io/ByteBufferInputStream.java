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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * {@code ByteBufferInputStream} is an {@link InputStream}
 * that delegates reads to a {@link ByteBuffer}.
 * @author dkatzel
 *
 */
public final class ByteBufferInputStream extends InputStream{

	private final ByteBuffer buffer;
	
	public ByteBufferInputStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public synchronized int read() throws IOException {
		if(!buffer.hasRemaining()){
			return -1;
		}
		//need to return byte as unsigned
		return IOUtil.toUnsignedByte(buffer.get());

	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if(!buffer.hasRemaining()){
			return -1;
		}
		int bytesRead = Math.min(len, buffer.remaining());
		buffer.get(b,off,bytesRead);
		return bytesRead;
	}

}
