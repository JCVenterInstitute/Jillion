package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TigrContigReadVisitor {

	
	void visitValidRange(Range validRange);
	
	void visitBasecalls(NucleotideSequence gappedBasecalls);
	
	void visitIncompleteEnd();
	void visitEnd();
}
