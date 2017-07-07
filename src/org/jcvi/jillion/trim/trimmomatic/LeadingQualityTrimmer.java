/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.trim.trimmomatic;

import org.jcvi.jillion.core.qual.PhredQuality;
/**
 * Trims off the bases from the leading (5') edge of a read
 * that have quailty values below the given threshold.
 * 
 * @author dkatzel
 *
 * @since 5.2
 * 
 * @see TrailingQualityTrimmer
 */
public class LeadingQualityTrimmer extends AbstractEdgeQualityTrimmer {
  
    /**
     * Trim off the bases that are below the given threshold.
     * @param threshold the quality value threshold to use. Can not be null.
     * 
     * @throws NullPointerException if threshold is null
     */
    public LeadingQualityTrimmer(PhredQuality threshold) {
        super(threshold, true);
    }

}
