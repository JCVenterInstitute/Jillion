package org.jcvi.experimental.primerDesign;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 1:14:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimerDesignerRequestInitializationException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 7070358332474822137L;

    public PrimerDesignerRequestInitializationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerRequestInitializationException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerRequestInitializationException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerRequestInitializationException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
