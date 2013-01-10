package org.jcvi.common.core.assembly.ctg;

import org.jcvi.common.core.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.AssembledReadBuilder;
import org.jcvi.common.core.assembly.DefaultAssembledRead;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

public class TestDefaultAssembledReadBuilder extends AbstractTestAssembledReadBuilder<AssembledRead>{

	@Override
	protected AssembledReadBuilder<AssembledRead> createReadBuilder(
			NucleotideSequence reference, String readId,
			NucleotideSequence validBases, int offset, Direction dir,
			Range clearRange, int ungappedFullLength) {
		return DefaultAssembledRead.createBuilder(
				reference, readId, validBases, 
				offset, dir, clearRange, 
				ungappedFullLength);
	}

}
