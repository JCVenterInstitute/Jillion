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
package org.jcvi.jillion.trace.chromat.abi.tag.rate;

/**
 * @author dkatzel
 *
 *
 */
public interface ScanRate {
    /**
     * The time when the scan rate changed.
     * @return the time as an int.
     */
    int getTime();
    /**
     * The Scan period in milliseconds.
     * @return the scan period in milliseconds.
     */
    int getScanPeriod();
    /**
     * The scan line of the first line 
     * of a new rate.
     * @return the scan line as an int.
     */
    int getFirstScanLine();
}
