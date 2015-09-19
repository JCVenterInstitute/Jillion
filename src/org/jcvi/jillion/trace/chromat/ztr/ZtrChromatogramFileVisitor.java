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
package org.jcvi.jillion.trace.chromat.ztr;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;

/**
 * {@code ZTRChromatogramFileVisitor} is a {@link ChromatogramFileVisitor}
 * that has additional visitXXX methods for ZTR specific fields.
 * @author dkatzel
 *
 *
 */
public interface ZtrChromatogramFileVisitor extends ChromatogramFileVisitor{

  
    /**
     * Visit the clip points of the ZTR chromatogram.
     * @param clipRange the clip points which describe
     * the valid range of the data (may be null or empty).
     */
    void visitClipRange(Range clipRange);


}
