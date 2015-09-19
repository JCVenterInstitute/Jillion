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
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;

/**
 * {@code ScfChromatogramFileVisitor} is a {@link ChromatogramFileVisitor}
 * that has additional visitXXX methods for SCF specific fields.
 * @author dkatzel
 *
 *
 */
public interface ScfChromatogramFileVisitor extends ChromatogramFileVisitor{
    /**
     * Visit the private data section of an SCF chromatogram 
     * file.
     * @param privateData the private data contained in this
     * SCF chromatogram (may be null).
     */
    void visitPrivateData(byte[] privateData);
    /**
     * Visit the confidence data section of an SCF chromatogram 
     * file that describes how confident the basecaller was
     * that the given basecall is not a substituion.
     * @param confidence the substitution data contained in this
     * SCF chromatogram (may be null or empty).
     */
    void visitSubstitutionConfidence(byte[] confidence);
    /**
     * Visit the confidence data section of an SCF chromatogram 
     * file that describes how confident the basecaller was
     * that the given basecall is not an insertion.
     * @param confidence the insertion data contained in this
     * SCF chromatogram (may be null or empty).
     */
    void visitInsertionConfidence(byte[] confidence);
    /**
     * Visit the confidence data section of an SCF chromatogram 
     * file that describes how confident the basecaller was
     * that the given basecall is not a deletion.
     * @param confidence the deletion data contained in this
     * SCF chromatogram (may be null or empty).
     */
    void visitDeletionConfidence(byte[] confidence);
}
