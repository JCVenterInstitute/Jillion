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
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;

/**
 * <code>SCFHeaderParserException</code> is a subclass of
 * {@link ScfDecoderException} which is used if an SCF
 * Header fails to parse.
 * @author dkatzel
 *
 *
 */
public class SCFHeaderDecoderException extends ScfDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = 3355691805761753118L;

    
    public SCFHeaderDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

   
    public SCFHeaderDecoderException(String message) {
        super(message);
    }

}
