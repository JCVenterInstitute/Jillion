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

import java.math.BigInteger;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code SFFCommonHeader}
 * is the header in an sff file
 * that contains information
 * used by all of the flowgrams in the sff file.
 * @author dkatzel
 *
 *
 */
public interface SffCommonHeader {
    /**
     * The Index offset is the offset of
     * the optional index of reads at the 
     * end of the sff file.
     * If the sff file does not have
     * an index, then the offset
     * should be set to 0.  The sff file
     * specification does not specify 
     * how the index is to be implemented
     * so sff creators can design their own
     * systems.
     * @return the offset of the optional index block
     * as an unsigned long (BigInteger)
     */
    BigInteger getIndexOffset();
    /**
     * The Index length is the length of the optional
     * index block.  If the index is not included
     * in the file, then it should be set to 0.
     * @return the length of the optional
     * index block as a positive number.
     */
    long getIndexLength();
    /**
     * The number of reads in this sff file.
     * @return the total number of reads in this file.
     */
    long getNumberOfReads();
    /**
     * The number of flows for each of the reads in the file.
     * @return a positive number.
     */
    int getNumberOfFlowsPerRead();
    /**
     * The {@link NucleotideSequence} used for each flow of each read.
     * the length of this sequence should be equal 
     * to {@link #getNumberOfFlowsPerRead()}.
     * @return a non-null {@link NucleotideSequence}.
     */
    NucleotideSequence getFlowSequence();
    /**
     * The nucleotide bases of the key sequence used for these
     * reads.
     * @return a non-null {@link NucleotideSequence}, usually 4 bases long.
     */
    NucleotideSequence getKeySequence();

}
