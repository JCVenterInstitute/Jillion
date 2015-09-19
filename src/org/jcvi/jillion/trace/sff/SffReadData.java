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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code SFFReadData} contains the raw
 * sequencing data from a given SFF read.
 * @author dkatzel
 *
 *
 */
public interface SffReadData {
    /**
     * The flowgram values contains the homopolymer
     * stretch estimates for each flow of the read.
     * @return an array containing homopolymer
     * estimates for each flow; never null.
     */
    short[] getFlowgramValues();
    /**
     * the flow index in the array
     * returned by {@link #getFlowgramValues()} for each base
     * in the called sequence.
     * @return
     */
    byte[] getFlowIndexPerBase();
    /**
     * The called basecalls as a {@link NucleotideSequence}.
     * @return a {@link NucleotideSequence}; never null.
     */
    NucleotideSequence getNucleotideSequence();
    /**
     * The quality scores for each base in the sequence
     * as a {@link QualitySequence}.
     * @return
     */
    QualitySequence getQualitySequence();

}
