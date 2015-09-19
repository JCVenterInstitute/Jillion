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
