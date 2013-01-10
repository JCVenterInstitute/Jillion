package org.jcvi.common.core.assembly.tasm;

import org.jcvi.common.core.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

public class TestDefaultTigrAssemblerAssembledReadBuilder extends AbstractTestAssembledReadBuilder<TasmAssembledRead>{
	@Override
	protected TasmAssembledReadBuilder createReadBuilder(
			NucleotideSequence reference, String readId,
			NucleotideSequence validBases, int offset, Direction dir,
			Range clearRange, int ungappedFullLength) {
		return DefaultTasmAssembledRead.createBuilder(
				reference, readId, validBases.toString(), 
				offset, dir, clearRange,
				ungappedFullLength);
	}

}
