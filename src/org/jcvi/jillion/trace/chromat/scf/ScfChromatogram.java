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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.trace.chromat.Chromatogram;
/**
 * <code>ScfChromatogram</code> is a SCF specific implementation
 * of {@link Chromatogram}.  ScfChromatograms have additional
 * data such as Substitution, Insertion, and Deletion confidence at each
 * base call.  There is also the possibility of additional "private data".
 * @author dkatzel
 *
 *
 */
public interface ScfChromatogram extends Chromatogram{


    /**
     * {@link ScfChromatogram}s may have additional PrivateData.
     * @return the privateData; or <code>null</code> if there
     * is no {@link PrivateData}.
     */
    PrivateData getPrivateData();
    /**
     * This is a {@link QualitySequence} describing
     * the confidence level that each basecall is 
     * a substitution.  This sequence is optional
     * in the SCF spec.  If this data does 
     * not exist for the given chromatogram,
     * then this value will be null.
     * @return a {@link QualitySequence}; or
     * null if no data exists. 
     */
    QualitySequence getSubstitutionConfidence();
    /**
     * This is a {@link QualitySequence} describing
     * the confidence level that each basecall is 
     * an insertion.  This sequence is optional
     * in the SCF spec.  If this data does 
     * not exist for the given chromatogram,
     * then this value will be null.
     * @return a {@link QualitySequence}; or
     * null if no data exists. 
     */
    QualitySequence getInsertionConfidence();
    /**
     * This is a {@link QualitySequence} describing
     * the confidence level that each basecall is 
     * a deletion.  This sequence is optional
     * in the SCF spec.  If this data does 
     * not exist for the given chromatogram,
     * then this value will be null.
     * @return a {@link QualitySequence}; or
     * null if no data exists. 
     */
    QualitySequence getDeletionConfidence();
    /**
     * Two {@link ScfChromatogram}s are equal
     * if and only if the ids, NucleotideSequece, 
     * QualitySequence, PositionSequence,
     * ChannelGroup, Comments,
     * DeletionConfidence, InsertionConfidence, SubsitutionConfidence
     * and PrivateData are all equal.
     * @return {@code true} if equal; {@code false}
     * otherwise.
     */
    @Override
    boolean equals(Object obj);

}
