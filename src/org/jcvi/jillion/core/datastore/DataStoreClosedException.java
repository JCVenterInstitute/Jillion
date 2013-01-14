package org.jcvi.jillion.core.datastore;
/**
 * {@code DataStoreClosedException} is a {@link RuntimeException}
 * that is thrown if an operation that requires the {@link DataStore}
 * to be open is called on a closed {@link DataStore}.
 * @author dkatzel
 *
 */
public final class DataStoreClosedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4092998958360325365L;
	/**
	 * Create a new instance of DataStoreClosedException
	 * with the given error message.
	 * @param message the error message.
	 */
	public DataStoreClosedException(String message) {
		super(message);
	}

}