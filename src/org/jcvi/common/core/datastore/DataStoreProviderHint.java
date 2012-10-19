package org.jcvi.common.core.datastore;

import java.util.Map;

/**
 * {@code DataStoreProviderHint}
 * describes implementation hints to 
 * help datastore factories return
 * instances of datastore implementations
 * for the desired scenario. The 
 * factory might not always honor these
 * hints if the factory does not know how 
 * to implement that specific hint or
 * if the specified hint can not be used
 * for the given input data. 
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
	OPTIMIZE_RANDOM_ACCESS_SPEED,
	/**
	 * Use a {@link DataStore} implementation
	 * that requires randomly accessing records
	 * using {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * but has been optimized to take up as little
	 * memory as possible but might take more time to access records
	 * than {@link #OPTIMIZE_RANDOM_ACCESS_SPEED}.
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
	 * another limitation to such an implementation is
	 * the input of the data must exist and not
	 * get altered during the entire lifetime of this object.
	 */
	OPTIMIZE_RANDOM_ACCESS_MEMORY,
	/**
	 * 
	 * Use a {@link DataStore} implementation that
	 * has been optimized for iterating over records
	 * using {@link DataStore#iterator()} and {@link DataStore#idIterator()}
	 * at the expense of poor performance of 
	 * randomly accessing records
	 * with {@link DataStore#get(String)} or {@link DataStore#contains(String)}.
	 * Such an implementation is ideal for use cases
	 * where the contents of the input
	 * will only be read once in a single pass.
	 * For example, iterating over each record only once 
	 * using {@link DataStore#iterator()}. 
	 * <p/>
	 * Since calls to
	 * {@link DataStore#get(String)} or {@link DataStore#contains(String)}
	 * are so expensive,
	 * it is recommended that instances of that use 
	 * this hint
	 * are wrapped by a {@link CachedDataStore}
	 * if random access will be used.
	 */
	OPTIMIZE_ONE_PASS_ITERATION
	;
}
