package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TigrContigReadVisitor {
	
	void visitBasecalls(NucleotideSequence gappedBasecalls);
	
	void visitEnd();
}
