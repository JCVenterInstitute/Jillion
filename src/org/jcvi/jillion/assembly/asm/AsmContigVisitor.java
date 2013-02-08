package org.jcvi.jillion.assembly.asm;

import java.util.List;
import java.util.SortedSet;

import org.jcvi.jillion.assembly.asm.AsmVisitor2.UnitigLayoutType;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface AsmContigVisitor extends AsmXtigVisitor{

	interface VariantRecord extends Comparable<VariantRecord>{
        /**
         * The internal read ids that contribute to this variant.
         * @return a list of IIDs 
         */
        List<Long> getContributingReadIIDs();
        /**
         * The weight of this variant, the greater
         * the number the more major this variant is.
         * @return
         */
        int getWeight();
        /**
         * The {@link NucleotideSequence}
         * of this variant.
         * @return
         */
        NucleotideSequence getVariantSequence();
    }
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
     * aligns to in the {@link Direction} of the unitig on the contig.
     * If direction is {@link Direction#REVERSE}, then the contig uses the 
     * reverse complement of the unitig's consensus sequence.
     * @param gapOffsets the gap offsets of this layed out unitig onto the unitig consensus sequence
     * (after reverse complementing?).
     */
    void visitUnitigLayout(UnitigLayoutType type, String unitigExternalId, 
            DirectedRange unitigRange, List<Long> gapOffsets);
}
