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
package org.jcvi.jillion.internal.trace.chromat.abi.tag.rate;

/**
 * @author dkatzel
 *
 *
 */
public final class ScanRateUtils {
    
    private static final float ONE_THOUSAND = 1000F;
    
    private ScanRateUtils(){
    	//can not instantiate
    }
    /**
     * Get the Sampling Rate (Hz) that is displayed in the
     * Seq Analysis annotation report.
     * @param scanRate the scan rate to compute; can not be null.
     * @return the sampling rate for the given scan rate.
     * @throws NullPointerException if scanRate is null.
     */
    public static float getSamplingRateFor(ScanRate scanRate){
        return ONE_THOUSAND/scanRate.getScanPeriod();
    }
}
