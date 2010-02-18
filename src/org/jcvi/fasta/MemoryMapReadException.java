/*
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

public class MemoryMapReadException extends RuntimeException {
   

    /**
     * 
     */
    private static final long serialVersionUID = 2831540439813335523L;

    public MemoryMapReadException(String message, Throwable cause) {
        super(message, cause);
    }
    public MemoryMapReadException(String message) {
        super(message);
    }
}
