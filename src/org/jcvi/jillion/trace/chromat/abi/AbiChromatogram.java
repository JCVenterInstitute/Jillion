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
package org.jcvi.jillion.trace.chromat.abi;

import org.jcvi.jillion.trace.chromat.Chromatogram;

/**
 * @author dkatzel
 *
 *
 */
public interface AbiChromatogram extends Chromatogram{

    /**
     * Get the original, unedited {@link Chromatogram}.  
     * @return the originalChromatogram
     */
    Chromatogram getOriginalChromatogram();

}
