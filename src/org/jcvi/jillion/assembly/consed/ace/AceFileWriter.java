/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
