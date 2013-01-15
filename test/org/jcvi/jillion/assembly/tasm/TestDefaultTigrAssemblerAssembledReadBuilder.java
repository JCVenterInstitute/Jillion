package org.jcvi.jillion.assembly.tasm;

import org.jcvi.jillion.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.jillion.assembly.tasm.DefaultTasmAssembledRead;
import org.jcvi.jillion.assembly.tasm.TasmAssembledRead;
import org.jcvi.jillion.assembly.tasm.TasmAssembledReadBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

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
