/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

public class IdReaderException extends Exception {

   
    /**
     * 
     */
    private static final long serialVersionUID = -5128060014728471695L;

    public IdReaderException(String message) {
        super(message);
    }

    public IdReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
