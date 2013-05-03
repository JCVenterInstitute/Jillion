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
package org.jcvi.jillion.trace.chromat.scf;
/**
 * PrivateData is a wrapper around a byte array
 * for additional optional data in an scf file.
 * The scf file specification puts no limitations
 * on what this data can contain and is implementation 
 * specific.  It is up to different scf writer implementations
 * to decide what data to put here (if any) and how to encode it. 
 * 
 * @author dkatzel
 *
 */
public interface PrivateData {

	/**
	 * @return the data
	 */
	byte[] getBytes();

	/**
	 * {@inheritDoc}
	 */
	int hashCode();

	/**
	 * Two PrivateData instances are equal
	 * if they both contain the same data.
	 */
	boolean equals(Object obj);

}
