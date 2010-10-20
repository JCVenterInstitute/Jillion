package org.jcvi.primerDesign;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 1:14:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimerDesignerRequestJobCreationException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 2891788942063186123L;

    public PrimerDesignerRequestJobCreationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerRequestJobCreationException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerRequestJobCreationException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerRequestJobCreationException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}