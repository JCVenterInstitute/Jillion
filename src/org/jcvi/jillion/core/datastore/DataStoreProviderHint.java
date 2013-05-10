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

import java.util.Map;

/**
 * {@code DataStoreProviderHint}
 * describes implementation hints to 
 * help datastore factories return
 * instances of datastore implementations
 * for the desired scenario. 
 * <strong>NOTE:</strong>
 * These hints are only guidelines;
 * the factories might
 * ignore any and all hints.  It is also
 * possible that  the factory could
 * return the same implementation
 * for multiple hint values.
 * @author dkatzel
 *
 */
public enum DataStoreProviderHint{
	/**
	 * Use a {@link DataStore} implementation
	 * that is optimized for speedily accessing
	 * records out of order 
	 * using {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * at the cost
	 * of possibly taking up more memory.
	 * For example, an implementation might 
	 * use a {@link Map} as a backing store which will
	 * put all records in memory to allow for very fast lookups.
	 * This is a very useful implementation if all the data
	 * fits into memory and the client code that uses
	 * this implementation will repeatedly require
	 * random access to the data.
	 * Therefore, this kind of implementation should not be used
	 * for {@link DataStore}s that contain many records
	 * or for {@link DataStore}s whose records take up lots of memory.
	 */
	RANDOM_ACCESS_OPTIMIZE_SPEED,
	/**
	 * Use a {@link DataStore} implementation
	 * that requires randomly accessing records
	 * using {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * but has been optimized to take up as little
	 * memory as possible. The implementation
	 * chosen will probably take more time to access records
	 * than {@link #RANDOM_ACCESS_OPTIMIZE_SPEED} but the {@link DataStore}
	 * will take up less total memory.
	 * <p/> 
	 * For example, if the input to this {@link DataStore} was 
	 * some kind of file containing record data, then perhaps
	 * and implementation will only store file offsets
	 * to each record.
	 * This allows large files to provide random 
	 * access without taking up much memory.  The down side is 
	 * lots of I/O must be performed to re-open up the file and seek to the correct file offset
	 * and re-parse the data each time 
	 * a call to {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * is called.
	 * Another limitation to such an implementation is
	 * the input of the data must exist and not
	 * get altered during the entire lifetime of this object.
	 */
	RANDOM_ACCESS_OPTIMIZE_MEMORY,
	/**
	 * Choose this option if the only methods
	 * in the {@link DataStore} that will be called
	 * are {@link DataStore#iterator()} and/or {@link DataStore#idIterator()}.
	 * The {@link DataStore} implementation returned
	 * will be optimized for iterating over records
	 * using {@link DataStore#iterator()} and {@link DataStore#idIterator()}
	 * possibly at the expense of poor performance of 
	 * randomly accessing records
	 * with {@link DataStore#get(String)} or {@link DataStore#contains(String)}.
	 * Such an implementation is ideal 
	 * for use cases
	 * where the contents of the datastore
	 * will only be iterated over in a single pass.
	 * For example, iterating over each record only once 
	 * using {@link DataStore#iterator()} inorder to perform
	 * a processing task.  This implementation choice
	 * is also an option for datastores
	 * that contain so many records that storing them in memory would
	 * cause out of memory errors or if the number of records exceeds
	 * {@link Integer#MAX_VALUE}. 
	 * <p/>
	 * Since calls to
	 * {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * may be so expensive,
	 * it is recommended that instances of that use 
	 * this hint
	 * are wrapped by a cached datastore using
	 * {@link DataStoreUtil#createNewCachedDataStore(Class, DataStore, int)}
	 * if random access will be used.
	 * 
	 * @see DataStoreUtil#createNewCachedDataStore(Class, DataStore, int) }
	 */
	ITERATION_ONLY
	;
}
