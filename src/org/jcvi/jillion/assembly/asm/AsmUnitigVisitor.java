package org.jcvi.jillion.assembly.asm;

import java.util.List;

import org.jcvi.jillion.assembly.asm.AsmVisitor.AsmVisitorCallback;
import org.jcvi.jillion.assembly.asm.AsmVisitor.UnitigStatus;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code AsmUnitigVisitor} is a visitor interface
 * to visit a single unitig in an ASM file.
 * 
 * @author dkatzel
 * @see AsmVisitor#visitUnitig(AsmVisitorCallback, String, long, float, float, UnitigStatus, NucleotideSequence, QualitySequence, long)
 * 
 */
public interface AsmUnitigVisitor{
	/**
     * Visit one read layout onto the the current unitig.
     * This method will be called once for each read in this unitig 
     * or until {@link AsmVisitorCallback#haltParsing()} is called.
     * 
     * @param readType the type of the read, usually 'R' for
     * random read.  This is the same type as from the frg file.
     * @param externalReadId the read id.
     * @param readRange the {@link DirectedRange} which has the gapped range on the unitig 
     * that this read
     * aligns to and the {@link Direction} of the read on this unitig.
     * @param gapOffsets the gap offsets of this read onto the frg sequence.
     */
    void visitReadLayout(char readType, String externalReadId, 
            DirectedRange readRange, List<Integer> gapOffsets);
    /**
     * Visiting this unitig has been halted
     * by a call to {@link AsmVisitorCallback#haltParsing()}.
     */
	void halted();
	/**
	 * The current unitig  has been completely visited.
	 */
	void visitEnd();
}
