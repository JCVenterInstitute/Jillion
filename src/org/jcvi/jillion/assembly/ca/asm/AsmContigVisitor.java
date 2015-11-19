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
package org.jcvi.jillion.assembly.ca.asm;

import java.util.List;
import java.util.SortedSet;

import org.jcvi.jillion.assembly.ca.asm.AsmVisitor.UnitigLayoutType;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code AsmContigVisitor} is a visitor interface
 * to visit a single contig in an ASM file.
 * 
 * @author dkatzel
 * @see AsmVisitor#visitContig(org.jcvi.jillion.assembly.ca.asm.AsmVisitor.AsmVisitorCallback, String, long, boolean, NucleotideSequence, org.jcvi.jillion.core.qual.QualitySequence, long, long, long)
 * 
 */
public interface AsmContigVisitor{
	/**
	 * {@code VariantRecord} is an alternative sequence
	 * for a specific region in a contig.
	 * @author dkatzel
	 * @see AsmContigVisitor#visitVariance(Range, long, long, long, long, SortedSet)
	 */
	interface VariantRecord extends Comparable<VariantRecord>{
        /**
         * The internal read ids that contribute to this variant.
         * @return a list of IIDs 
         */
        List<Long> getContributingReadIIDs();
        /**
         * The weight of this variant, the greater
         * the number the more major this variant is.
         * @return the weight.
         */
        int getWeight();
        /**
         * The {@link NucleotideSequence}
         * of this variant.
         * @return the variant sequence.
         */
        NucleotideSequence getVariantSequence();
    }
	
	/**
     * Visit one read layout onto the the current contig.
     * @param readType the type of the read, usually 'R' for
     * random read.  This is the same type as from the frg file.
     * 
     * @param externalReadId the read id.
     * @param readRange the {@link DirectedRange} which has the gapped 
     * range on the contig that this read
     * aligns to and the {@link org.jcvi.jillion.core.Direction} of the read on this contig.
     * @param gapOffsets the gap offsets of this read onto the frg sequence.
     */
    void visitReadLayout(char readType, String externalReadId, 
            DirectedRange readRange, List<Integer> gapOffsets);
    /**
     * Visiting this contig has been halted
     * by a call to {@link org.jcvi.jillion.assembly.ca.asm.AsmVisitor.AsmVisitorCallback#haltParsing()}.
     */
	void halted();
	/**
	 * The current contig  has been completely visited.
	 */
	void visitEnd();	
	
    /**
     * A Variance message indicates alternative sequence(s) for small
     * regions of the contig consensus.  A variant whose sequence
     * length is 1 is commonly known as a SNP.
     * @param range the location of this variant on the contig consensus sequence.
     * @param numberOfSpanningReads the number of reads that give 
     * coverage at the variant positions. Some spanning reads may not have contributed to the consensus
     * due to poor quality or lack of confirmation in the other reads.
     * @param anchorSize Ancor size used to detect variants, Currently, Celera Assembler
     * defaults to 11.
     * @param internalAccessionId the internal accession id of this variant.
     * @param accessionForPhasedVariant the accession for a different variant
     * that is phased with this one.  If set to a negative number, then
     * this variant has not related to another variant? Linking all the variants that are phased
     * with one another can be used to dephase mixed (or diploid?) sequence.
     * @param variantRecords a list of {@link VariantRecord} objects
     * that explain all the details of each part of the SNP.
     */
    void visitVariance(Range range, long numberOfSpanningReads,
            long anchorSize,long internalAccessionId, long accessionForPhasedVariant,
            SortedSet<VariantRecord> variantRecords);
	
    /**
     * Visit one unitig layout onto the the current contig.
     * @param type the {@link UnitigLayoutType} that explains
     * why the unitig is layed out here.
     * @param unitigExternalId the external id of this unitig.
     * @param unitigRange the gapped {@link DirectedRange} on the contig that this unitig
     * aligns to in the {@link org.jcvi.jillion.core.Direction} of the unitig on the contig.
     * If direction is {@link org.jcvi.jillion.core.Direction#REVERSE}, then the contig uses the 
     * reverse complement of the unitig's consensus sequence.
     * @param gapOffsets the gap offsets of this layed out unitig onto the unitig consensus sequence
     * (after reverse complementing?).
     */
    void visitUnitigLayout(UnitigLayoutType type, String unitigExternalId, 
            DirectedRange unitigRange, List<Long> gapOffsets);
}
