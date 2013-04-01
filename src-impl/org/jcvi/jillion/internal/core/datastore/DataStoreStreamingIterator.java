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
package org.jcvi.jillion.internal.core.datastore;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@linkplain DataStoreStreamingIterator} is a {@link StreamingIterator}
 * implementation for a {@link DataStore}.
 * The values {@link #hasNext()} and {@link #next()}
 * will throw a {@link DataStoreClosedException}
 * if the given {@link DataStore} is closed.
 * @author dkatzel
 *
 * @param <T> the Type of element returned by each call to {@link Iterator#next()}
 */
public final class DataStoreStreamingIterator<T> implements StreamingIterator<T>{

	private final DataStore<?>	parentDataStore;
	private final StreamingIterator<T> delegate;
	/**
	 * Create a new instance of {@code DataStoreStreamingIterator}.
	 * @param parentDataStore the DataStore to use to check 
	 * to see if it is closed.
	 * @param delegate the {@link StreamingIterator} to iterate over.
	 * @return a new instance, never null.
	 */
	public static <T> DataStoreStreamingIterator<T> create(DataStore<?> parentDataStore,
			StreamingIterator<T> delegate){
		return new DataStoreStreamingIterator<T>(parentDataStore, delegate);
	}
	/**
	 * Create a new instance of {@code DataStoreStreamingIterator}.
	 * @param parentDataStore the DataStore to use to check 
	 * to see if it is closed.
	 * @param delegate the {@link Iterator} to iterate over.
	 * @return a new instance, never null.
	 */
	public static <T> DataStoreStreamingIterator<T> create(DataStore<?> parentDataStore,
			Iterator<T> delegate){
		return new DataStoreStreamingIterator<T>(parentDataStore, IteratorUtil.createStreamingIterator(delegate));
	}
	private DataStoreStreamingIterator(DataStore<?> parentDataStore,
			StreamingIterator<T> delegate) {
		this.parentDataStore = parentDataStore;
		this.delegate = delegate;
	}
	/**
	 * @throws DataStoreClosedException if 
	 * the datastore has been closed
	 * but this iterator still has elements
	 * left to iterate.
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		boolean delegateHasNext = delegate.hasNext();
		if(parentDataStore.isClosed() && delegateHasNext){
			IOUtil.closeAndIgnoreErrors(this);
			throw new DataStoreClosedException("datastore is closed");
		}
		return delegateHasNext;
	}

	@Override
	public void close() throws IOException {
		delegate.close();
		
	}

	@Override
	public T next() {
		//delegate to hasNext()
		//to do datastore closed checking
		hasNext();
		//if we get here then we're ok
		return delegate.next();
	}

	@Override
	public void remove() {
		delegate.remove();
		
	}
	
	
}
