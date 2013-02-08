package org.jcvi.jillion.assembly.asm;

import java.util.List;

import org.jcvi.jillion.assembly.asm.AsmVisitor2.AsmVisitorCallback;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;

public interface AsmXtigVisitor {
	/**
     * Visit one read layout onto the the current unitig or contig
     * depending on if the visitor is currently visiting
     * a unitig or a contig.
     * @param readType the type of the read, usually 'R' for
     * random read.  This is the same type as from the frg file.
     * @param externalReadId the read id.
     * @param readRange the {@link DirectedRange} which has the gapped range on the unitig or contig that this read
     * aligns to and the {@link Direction} of the read on this unitig or contig.
     * @param gapOffsets the gap offsets of this read onto the frg sequence.
     */
    void visitReadLayout(char readType, String externalReadId, 
            DirectedRange readRange, List<Integer> gapOffsets);
    /**
     * Visiting this contig/unitig has been halted
     * by a call to {@link AsmVisitorCallback#stopParsing()}.
     */
	void visitIncompleteEnd();
	/**
	 * The current contig/unitig  has been completely visited.
	 */
	void visitEnd();
}
