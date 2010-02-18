/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

public class DataStoreException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 537135759192307631L;

    /**
     * @param message
     * @param cause
     */
    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public DataStoreException(String message) {
        super(message);
    }

}
