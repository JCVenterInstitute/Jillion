package org.jcvi.jillion.assembly.tasm;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TasmContigReadVisitor {

	void visitBasecalls(NucleotideSequence gappedBasecalls);
	
	//we don't need to visit the asm_lend, asm_rend, seq_lend, seq_rend
	//since they can be computed using the start offset, valid range and gapped sequence
	
	void visitEnd();
}
