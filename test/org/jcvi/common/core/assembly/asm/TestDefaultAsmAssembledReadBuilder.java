package org.jcvi.common.core.assembly.asm;

import org.jcvi.common.core.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.common.core.assembly.asm.AsmAssembledRead;
import org.jcvi.common.core.assembly.asm.AsmAssembledReadBuilder;
import org.jcvi.common.core.assembly.asm.DefaultAsmPlacedRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class TestDefaultAsmAssembledReadBuilder extends AbstractTestAssembledReadBuilder<AsmAssembledRead>{
	@Override
	protected AsmAssembledReadBuilder createReadBuilder(
			NucleotideSequence reference, String readId,
			NucleotideSequence validBases, int offset, Direction dir,
			Range clearRange, int ungappedFullLength) {
		return DefaultAsmPlacedRead.createBuilder(
				reference, readId, validBases.toString(), 
				offset, dir, clearRange,
				ungappedFullLength,
				false);
	}

}
