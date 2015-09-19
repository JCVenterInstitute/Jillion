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
package org.jcvi.jillion.trace.chromat.scf;

import java.io.IOException;
/**
 * <code>SCFParserException</code> is a subclass of
 * {@link IOException} which is used if an SCF
 * file fails to parse.
 * @author dkatzel
 *
 *
 */
public class ScfDecoderException extends IOException {

    /**
     *
     */
    private static final long serialVersionUID = -8636660736340790019L;

    /**
     * @param message
     * @param cause
     */
    public ScfDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ScfDecoderException(String message) {
        super(message);
    }

}
