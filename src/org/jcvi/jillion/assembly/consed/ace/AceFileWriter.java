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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code AceFileWriter} handles writing out
 * assembly data in ace format.
 * @author dkatzel
 *
 */
public interface AceFileWriter extends Closeable{
	/**
	 * Writes the given {@link AceContig}.
	 * @param contig the contig to write;
	 * can not be null.
	 * @throws IOException if there is a problem
	 * writing out the contig.
	 * @throws NullPointerException if the contig is null.
	 */
	void write(AceContig contig) throws IOException;
	/**
	 * Writes the given {@link ReadAceTag}.
	 * @param tag the tag to write;
	 * can not be null.
	 * @throws IOException if there is a problem
	 * writing out the tag.
	 * @throws NullPointerException if the tag is null.
	 */
	void write(ReadAceTag tag) throws IOException;
	/**
	 * Writes the given {@link ConsensusAceTag}.
	 * @param tag the tag to write;
	 * can not be null.
	 * @throws IOException if there is a problem
	 * writing out the tag.
	 * @throws NullPointerException if the tag is null.
	 */
	void write(ConsensusAceTag tag) throws IOException;
	/**
	 * Writes the given {@link WholeAssemblyAceTag}.
	 * @param tag the tag to write;
	 * can not be null.
	 * @throws IOException if there is a problem
	 * writing out the tag.
	 * @throws NullPointerException if the tag is null.
	 */
	void write(WholeAssemblyAceTag tag) throws IOException;
	/**
	 * Finish writing the ace formatted data.
	 * This must always be called since
	 * the ace file format has certain fields
	 * that can only be correctly set once all the 
	 * data has been processed. 
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	void close() throws IOException;
	
}
