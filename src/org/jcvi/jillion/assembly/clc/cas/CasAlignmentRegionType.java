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
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

public enum CasAlignmentRegionType {
    /**
     * The read and reference have the same
     * number of bases, most of which match,
     * but there may be mismatches
     * but not any gaps.
     */
    MATCH_MISMATCH,
    /**
     * The read has extra bases
     * vs the reference.  (The reference must
     * add gaps to compensate).
     */
    INSERT,
    /**
     * The read has less bases
     * vs the reference.  (The read must
     * add gaps to compensate).
     */
    DELETION,
    /**
     * There was a phase change,
     * only valid for SOLID reads.
     */
    PHASE_CHANGE
}
