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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;


import org.jcvi.jillion.core.Range;

public interface CasMatch {

    boolean matchReported();
    boolean readHasMutlipleMatches();
    boolean hasMultipleAlignments();
    boolean readIsPartOfAPair();
    /**
     * Get the {@link CasAlignment} the assembler
     * has chosen to use for this read.
     * If no match reported then
     * this will return {@code null}.
     * @return a {@link CasAlignment} if a match
     * is reported; or {@code null}.
     */
    CasAlignment getChosenAlignment();
    long getNumberOfMatches();
    long getNumberOfReportedAlignments();
    int getScore();
    /**
     * Get the Range of the read used
     * in the CLC mapping.
     * Sometimes, the input read has been processed 
     * before CLC mapped the data. 
     * @return a {@link Range} representing the good region
     * of the full length read that was included in the mapping assembly;
     * or {@code null} if no trim range exists or is known.
     */
    Range getTrimRange();
    
}
