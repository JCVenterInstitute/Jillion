package org.jcvi.experimental.primerDesign.results;

/**
 * User: aresnick
 * Date: Jul 27, 2010
 * Time: 2:35:13 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesignerResultParseException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -3599765954320254983L;

    public PrimerDesignerResultParseException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerResultParseException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerResultParseException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrimerDesignerResultParseException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
