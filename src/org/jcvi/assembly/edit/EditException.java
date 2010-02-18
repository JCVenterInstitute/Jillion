/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit;

public class EditException extends Exception {

   

    /**
     * 
     */
    private static final long serialVersionUID = -104301694076354205L;


    public EditException(String message) {
        super(message);
    }
    

    public EditException(String message, Throwable cause) {
        super(message, cause);
    }

}
