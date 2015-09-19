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
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;
/**
 * <code>SectionDecoderException</code> is a subclass
 * of {@link ScfDecoderException} that will be thrown
 * if there is a problem parsing an SCF {@link Section}.
 * @author dkatzel
 *
 *
 */
public class SectionDecoderException extends ScfDecoderException {

    /**
     *
     */
    private static final long serialVersionUID = 7865788143804921065L;

  
    public SectionDecoderException(String message, Throwable cause) {
        super(message, cause);
    }
 
    public SectionDecoderException(String message) {
        super(message);
    }

}
