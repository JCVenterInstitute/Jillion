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
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.trace.chromat.Chromatogram;
/**
 * {@code ZtrChromatogram} is a ZTR
 * specific implementation {@link Chromatogram}
 * that has an extra field for the clip points.
 * @author dkatzel
 *
 */
public interface ZtrChromatogram extends Chromatogram{
    /**
     * Gets the ZTR Specific clip points as a {@link Range}.
     * @return a clip, may be null or empty.
     */
    Range getClip();
}
