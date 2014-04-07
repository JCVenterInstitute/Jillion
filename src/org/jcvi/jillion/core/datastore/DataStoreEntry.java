/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.datastore;
/**
 * A {@link DataStore} entry (key-value pair).  
 * This class is similar to Map's {@link java.util.Map.Entry}
 * except it is read only.
 *
 * @see DataStore#entryIterator()
 */
public final class DataStoreEntry<V> {

	private final String key;
	private final V value;
	/**
	 * Create a new {@link DataStoreEntry} instance;
	 * @param key the key; may not be null.
	 * @param value the value; may not be null.
	 * @throws NullPointerException if either parameter is null.
	 */
	public DataStoreEntry(String key, V value){
		if(key ==null){
			throw new NullPointerException("key can not be null");
		}
		if(value ==null){
			throw new NullPointerException("value can not be null");
		}
		this.key = key;
		this.value =value;
	}
	/**
	 * Get the key of this entry.
	 * This should be the Id of the value
	 * in the datastore.
	 * @return a String; will never be null.
	 */
	public String getKey(){
		return key;
	}
	/**
	 * Get the value of this entry.
	 * This should be equal (although may not be 
	 * the same reference) as the value returned by
	 * {@code datastore.get( entry.getKey() ); }
	 * @return an object of the DataStore's type;
	 * will never be null.
	 */
	public V getValue(){
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key.hashCode();
		result = prime * result + value.hashCode();
		return result;
	}
	/**
	 * {@link DataStoreEntry}s are only 
	 * equal if they have equal keys and values.
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DataStoreEntry)) {
			return false;
		}
		DataStoreEntry other = (DataStoreEntry) obj;
		if (!key.equals(other.key)) {
			return false;
		}
		if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
	
	
	
}
