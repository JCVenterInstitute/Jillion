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
package org.jcvi.jillion.trace.chromat;

import java.util.Map;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code ChromatogramFileVisitor} is a {@link FileVisitor}
 * used for visiting chromatogram files.
 * @author dkatzel
 *
 *
 */
public interface ChromatogramFileVisitor{
    /**
     * The end of the chromatogram has been 
     * visited.  There will be no more visitXXX methods
     * called for this chromatogram.
     */
    void visitEnd();
	/**
     * Visit the basecalls in the chromatogram file
     * being visited.
     * @param basecalls the basecalls as a {@link NucleotideSequence},
     * although unlikely, it is possible there are 
     * gaps.
     */
    void visitBasecalls(NucleotideSequence basecalls);

    /**
     * Visit the raw peak values of the
     * chromatogram file being visited.
     * @param peaks the raw peaks as shorts,
     * may be null.
     */
    void visitPeaks(short[] peaks);

    
    /**
     * Visit any comments associated with 
     * this chromatogram. 
     * @param comments the comments associated
     * with this chromatogram file stored
     * as key-value pairs.
     */
    void visitComments(Map<String,String> comments);
   
    /**
     * Visit the raw positions of the A channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitAPositions(short[] positions);

    /**
     * Visit the raw positions of the C channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitCPositions(short[] positions);
    /**
     * Visit the raw positions of the G channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitGPositions(short[] positions);
    /**
     * Visit the raw positions of the T channel of the
     * chromatogram file being visited.
     * @param positions the raw positions as shorts,
     * may be null.
     */
    void visitTPositions(short[] positions);

    /**
     * Visit the raw confidence (quality) of the A channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitAConfidence(byte[] confidence);

    /**
     * Visit the raw confidence (quality) of the C channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitCConfidence(byte[] confidence);

    /**
     * Visit the raw confidence (quality) of the G channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitGConfidence(byte[] confidence);

    /**
     * Visit the raw confidence (quality) of the T channel of the
     * chromatogram file being visited.
     * @param confidence the raw confidence as bytes,
     * may be null.
     */
    void visitTConfidence(byte[] confidence);

}
