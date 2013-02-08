package org.jcvi.jillion.assembly.asm;

import org.jcvi.jillion.assembly.asm.AsmVisitor2.LinkOrientation;

public interface AsmScaffoldVisitor {

	 /**
     * A contig pair message defines a pair of contigs that belong to a scaffold.
     * A scaffold with three contigs {1,2,3} would be represented by two pair message
     * (1,2) and (2,3).  Note that the first contig of a pair can have a reverse orientation
     * to preserve its orientation in the previous contig pair message.
     * @param externalContigId1 the external id of one of the contigs in this pair.
     * @param externalContigId2 the external id of the other contig in this pair.
     * @param meanDistance the the predicted number of bases in the gap between contigs.  A negative
     * distance indicates that the contigs overlap (according to mate pairs)
     * but their consensus sequences do not align.
     * @param stddev standard deviation of the distance distribution of the mates that span the contigs.
     * @param orientation the relative {@link LinkOrientation} of the two contigs.
     */
    void visitContigPair(String externalContigId1,String externalContigId2, 
            float meanDistance, float stddev, LinkOrientation orientation);
    
	void visitIncompleteEnd();
	void visitEnd();
}
