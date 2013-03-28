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
package org.jcvi.jillion.trace.sff;

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code SffWriter} is an inteface for
 * writing sff encoded files.
 * @author dkatzel
 *
 */
public interface SffWriter extends Closeable{

	/**
	 * Write the given Flowgram.
	 * @param flowgram the flowgram to write
	 * can not be null.
	 * @throws IOException if there is a problem writing this 
	 * flowgram to the writer.
	 */
	void write(SffFlowgram flowgram) throws IOException;
	/**
	 * Write out an encoded Flowgram using the data
	 * contained in the given {@link SffReadHeader} and {@link SffReadData}.
	 * @param header the {@link SffReadHeader} to write
	 * can not be null.
	 * @param data the {@link SffReadData} to write
	 * can not be null.
	 * @throws IOException if there is a problem writing this 
	 * flowgram to the writer.
	 */
	void write(SffReadHeader header, SffReadData data) throws IOException;
	/**
	 * Complete writing out the sff file and close the file.
	 * <strong>Users must always close the writer to guarantee that all the 
	 * sff data has been written. </strong>
	 * {@link SffWriter} implementations may delay writing out all the data
	 * to the output until the writer is closed. (For example, 
	 * certain header information such as the number of reads in the file will not be known
	 * until all the records have been written).
	 */
	@Override
	void close() throws IOException;
}
