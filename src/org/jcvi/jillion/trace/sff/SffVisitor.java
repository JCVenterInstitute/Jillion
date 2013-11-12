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

/**
 * {@code SffVisitor} is a visitor
 * interface to visit components of a single
 * sff encoded file.
 * 
 * @author dkatzel
 *
 */
public interface SffVisitor {
	/**
	 * Visit the {@link SffCommonHeader} of the given
	 * sff file which explains the metadata common to all
	 * reads in the sff file.
	 * @param callback  a {@link SffVisitorCallback} that can be used
     * to communicate with the parser object. 
	 * @param header the {@link SffCommonHeader} instance;
	 * will never be null.
	 */
	void visitHeader(SffVisitorCallback callback, SffCommonHeader header);
	/**
	 * Visit the a single read encoded in this sff file.
	 * @param callback  a {@link SffVisitorCallback} that can be used
     * to communicate with the parser object. 
	 * @param readHeader an instance of {@link SffReadHeader}
	 * describing this read. 
	 * @return an instance of {@link SffFileReadVisitor}
	 * that will be used to visit this read's data;
	 * if {@code null} is returned, then this read's data will be skipped.
	 */
	SffFileReadVisitor visitRead(SffVisitorCallback callback, SffReadHeader readHeader);
	/**
	 * The end of the sff file has been reached.
	 */
	void end();
}
