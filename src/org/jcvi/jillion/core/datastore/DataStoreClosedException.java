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
package org.jcvi.jillion.core.datastore;
/**
 * {@code DataStoreClosedException} is a {@link IllegalStateException}
 * that is thrown if an operation that requires the {@link DataStore}
 * to be open is called on a closed {@link DataStore}.
 * @author dkatzel
 *
 */
public final class DataStoreClosedException extends IllegalStateException {


	/**
	 * auto-generated serialVersion id
	 */
	private static final long serialVersionUID = -4221044131514655440L;

	/**
	 * Create a new instance of DataStoreClosedException
	 * with the given error message.
	 * @param message the error message.
	 */
	public DataStoreClosedException(String message) {
		super(message);
	}

}
