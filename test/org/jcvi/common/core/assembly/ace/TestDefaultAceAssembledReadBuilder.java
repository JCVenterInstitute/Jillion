package org.jcvi.common.core.assembly.ace;

import java.util.Date;

import org.jcvi.common.core.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

public class TestDefaultAceAssembledReadBuilder extends AbstractTestAssembledReadBuilder<AceAssembledRead>{
	private final PhdInfo phdInfo = new PhdInfo("traceName", "phdName", new Date());
	@Override
	protected AceAssembledReadBuilder createReadBuilder(
			NucleotideSequence reference, String readId,
			NucleotideSequence validBases, int offset, Direction dir,
			Range clearRange, int ungappedFullLength) {
		return DefaultAceAssembledRead.createBuilder(
				reference, readId, validBases, 
				offset, dir, clearRange,
				phdInfo,
				ungappedFullLength);
	}

}
