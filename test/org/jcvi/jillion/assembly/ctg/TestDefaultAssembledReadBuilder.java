package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.DefaultAssembledRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

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
